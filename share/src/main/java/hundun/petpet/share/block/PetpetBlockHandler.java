package hundun.petpet.share.block;


import hundun.petpet.share.block.dto.ImageProviderType;
import kotlin.Pair;
import xmmt.dituon.share.GifBuilder;
import xmmt.dituon.share.ImageDeformer;
import xmmt.dituon.share.ImageSynthesis;
import xmmt.dituon.share.Type;
import hundun.petpet.share.block.PetpetBlock.*;
import hundun.petpet.share.block.provider.IImageProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PetpetBlockHandler {


    private interface IDrawTaskHandler<T extends IDrawTask> {
        void apply(HandlerContext handlerContext, Graphics2D g2d, PetpetBlockContext blockContext, T task) throws PetpetBlockException;
    }

    private static class DrawImageHandler implements IDrawTaskHandler<DrawImageTask> {
        static DrawImageHandler INSTANCE = new DrawImageHandler();
        @Override
        public void apply(HandlerContext handlerContext, Graphics2D g2d, PetpetBlockContext blockContext, DrawImageTask task) throws PetpetBlockException {
            BufferedImage image = getImage(handlerContext, task.getImageMeta().getProviderType(), task.getImageMeta().getProviderKey());
            float angle = 0.0f;
            var imageModify = task.getImageModify();
            if (imageModify != null) {
                if (imageModify.getRound() != null && imageModify.getRound()) {
                    try {
                        image = ImageSynthesis.convertCircular(image, blockContext.isAntialias());
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw PetpetBlockException.fromIOException(e);
                    }
                }

                if (imageModify.getVertexPosList() != null) {
                    List<Point2D> vertexPoints = imageModify.getVertexPosList().stream()
                            .map(pos -> new Point2D.Double(pos[0], pos[1]))
                            .collect(Collectors.toList());
                    image = ImageDeformer.computeImage(image, vertexPoints.toArray(Point2D[]::new));
                }

                if (imageModify.getAngle() != null) {
                    angle = task.getImageModify().getAngle();
                }
            }

            int[] anchorAndWH = new int[4];
            anchorAndWH[0] = task.getImageMeta().getAnchorPos()[0];
            anchorAndWH[1] = task.getImageMeta().getAnchorPos()[1];
            anchorAndWH[2] = image.getWidth();
            anchorAndWH[3] = image.getHeight();
            ImageSynthesis.g2dDrawZoomAvatar(g2d, image, anchorAndWH, angle, blockContext.isAntialias());
        }

    }


    private static BufferedImage getImage(HandlerContext handlerContext, ImageProviderType providerType, String key) throws PetpetBlockException {
        IImageProvider provider = handlerContext.getImageProviderMap().get(providerType);
        if (provider != null) {
            return provider.apply(key);
        } else {
            throw PetpetBlockException.fromImageSupplierNotFound(providerType, key);
        }
    }


    private static class DrawTextHandler implements IDrawTaskHandler<DrawTextTask> {
        static DrawTextHandler INSTANCE = new DrawTextHandler();

        @Override
        public void apply(HandlerContext handlerContext, Graphics2D g2d, PetpetBlockContext blockContext, DrawTextTask task) {
            ImageSynthesis.g2dDrawText(g2d, task.getText(), task.getAnchorPos(), blockContext.getColor(), blockContext.getFont());
        }
    }

    private static class ContextModifyHandler implements IDrawTaskHandler<ContextModifyTask> {
        static ContextModifyHandler INSTANCE = new ContextModifyHandler();

        @Override
        public void apply(HandlerContext handlerContext, Graphics2D g2d, PetpetBlockContext blockContext, ContextModifyTask task) {
            if (task.getAntialias() != null) {
                blockContext.setAntialias(task.getAntialias());
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            if (task.getColor() != null) {
                blockContext.setColor(task.getColor());
            }
            if (task.getFontName() != null || task.getFontSize() != null ) {
                String newName = Objects.requireNonNullElseGet(task.getFontName(), () -> blockContext.getFont().getName());
                Integer newSize = Objects.requireNonNullElseGet(task.getFontSize(), () -> blockContext.getFont().getSize());
                blockContext.setFont(new Font(newName, Font.PLAIN, newSize));
            }
        }
    }

    private static class ContextInitHandler implements IDrawTaskHandler<ContextInitTask> {
        static ContextInitHandler INSTANCE = new ContextInitHandler();

        @Override
        public void apply(HandlerContext handlerContext, Graphics2D g2d, PetpetBlockContext blockContext, ContextInitTask task) throws PetpetBlockException {
            BufferedImage image = getImage(handlerContext, task.getProviderType(), task.getProviderKey());
            blockContext.setHeight(task.getHeight());
            blockContext.setWidth(task.getWidth());
            blockContext.setImageType(image.getType());
            blockContext.setBackgroundConfig(task.getBackgroundConfig());
        }
    }

    public Pair<InputStream, String> handle(PetpetBlock petpetBlock, HandlerContext handlerContext) throws PetpetBlockException {

        ContextInitHandler.INSTANCE.apply(handlerContext, null, petpetBlock.getContext(), petpetBlock.getInitTask());

        if (petpetBlock.getType() == Type.GIF) {
            return new Pair<>(makeGIF(handlerContext, petpetBlock), "gif");
        } else {
            BufferedImage frameImage = drawOneFrame(handlerContext, petpetBlock.getContext(), petpetBlock.frameBlocks.get(0));
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(frameImage, "png", os);
                return new Pair<>(new ByteArrayInputStream(os.toByteArray()), "png");
            } catch (IOException e) {
                e.printStackTrace();
                throw PetpetBlockException.fromIOException(e);
            }
        }
    }

    private InputStream makeGIF(HandlerContext handlerContext, PetpetBlock petpetBlock) throws PetpetBlockException {
        try {
            GifBuilder gifBuilder = new GifBuilder(petpetBlock.getContext().getImageType(), 65, true);

            for (FrameBlock frameBlock : petpetBlock.getFrameBlocks()) {
                BufferedImage frameImage = drawOneFrame(handlerContext, petpetBlock.getContext(), frameBlock);
                gifBuilder.writeToSequence(frameImage);
            }

            gifBuilder.close();
            return gifBuilder.getOutput();
        } catch (IOException e) {
            e.printStackTrace();
            throw PetpetBlockException.fromIOException(e);
        }
    }


    private BufferedImage drawOneFrame(HandlerContext handlerContext, PetpetBlockContext blockContext, FrameBlock frameBlock) throws PetpetBlockException {
        BufferedImage output = new BufferedImage(blockContext.getWidth(), blockContext.getHeight(), blockContext.getImageType());
        Graphics2D g2d = output.createGraphics();

        // TODO 研究output和g2d创建后立刻指向新的实例，是否合理
        // 背景
        if (blockContext.getBackgroundConfig().isTransparent()) {
            output = g2d.getDeviceConfiguration().createCompatibleImage(
                    blockContext.getWidth(), blockContext.getHeight(), Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = output.createGraphics();
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, blockContext.getWidth(), blockContext.getHeight());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0F));
        }

        for (IDrawTask imageTask : frameBlock.getTasks()) {
            if (imageTask instanceof DrawImageTask) {
                var implTask = (DrawImageTask)imageTask;
                DrawImageHandler.INSTANCE.apply(handlerContext, g2d, blockContext, implTask);
            } else if (imageTask instanceof DrawTextTask) {
                var implTask = (DrawTextTask)imageTask;
                DrawTextHandler.INSTANCE.apply(handlerContext, g2d, blockContext, implTask);
            } else if (imageTask instanceof ContextModifyTask) {
                var implTask = (ContextModifyTask)imageTask;
                ContextModifyHandler.INSTANCE.apply(handlerContext, g2d, blockContext, implTask);
            }
        }

        g2d.dispose();
        return output;
    }

}