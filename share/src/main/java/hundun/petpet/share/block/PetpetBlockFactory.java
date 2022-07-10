package hundun.petpet.share.block;

import hundun.petpet.share.block.PetpetBlock.*;
import hundun.petpet.share.block.dto.JPetpetData.*;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PetpetBlockFactory {
    public static PetpetBlock buildPetpetBlockFromDTO(PetpetBlockDTO dto) {
        return PetpetBlock.builder()
                .type(dto.getType())
                .initTask(buildContextInitTaskFromDTO(dto.getInitTask()))
                .context(new PetpetBlockContext())
                .frameBlocks(dto.getFrameTasks().stream()
                        .map(it -> buildFrameBlockFromDTO(it))
                        .collect(Collectors.toList())
                )
                .build();
    }

    private static ContextInitTask buildContextInitTaskFromDTO(ContextInitTaskDTO dto) {
        return ContextInitTask.builder()
                .width(dto.getWidth())
                .height(dto.getHeight())
                .providerType(dto.getProviderType())
                .providerKey(dto.getProviderKey())
                .backgroundConfig(buildBackgroundConfigFromDTO(dto.getBackgroundConfig()))
                .build();
    }

    private static BackgroundConfig buildBackgroundConfigFromDTO(BackgroundConfigDTO dto) {
        return BackgroundConfig.builder()
                .transparent(dto.getTransparent())
                .build();
    }

    private static FrameBlock buildFrameBlockFromDTO(FrameBlockDTO dto) {
        return FrameBlock.builder()
                .tasks(dto.getTasks().stream()
                        .map(it -> buildDrawTaskFromDTO(it))
                        .collect(Collectors.toList())
                )
                .build();
    }

    private static IDrawTask buildDrawTaskFromDTO(DrawTaskDTO dto) {
        if (dto == null) {
            return  null;
        }
        if (dto instanceof ContextModifyTaskDTO) {
            var implDto = (ContextModifyTaskDTO)dto;
            return buildContextModifyTaskFromDTO(implDto);
        } else if (dto instanceof DrawImageTaskDTO) {
            var implDto = (DrawImageTaskDTO)dto;
            return buildDrawImageTaskFromDTO(implDto);
        } else if (dto instanceof DrawTextTaskDTO) {
            var implDto = (DrawTextTaskDTO)dto;
            return buildDrawTextTaskFromDTO(implDto);
        }
        throw new IllegalArgumentException("无法buildDrawTaskFromDTO for " + dto);
    }

    private static DrawTextTask buildDrawTextTaskFromDTO(DrawTextTaskDTO dto) {
        return DrawTextTask.builder()
                .text(dto.getText())
                .anchorPos(new int[]{dto.getPos().getX(), dto.getPos().getY()})
                .build();
    }


    private static DrawImageTask buildDrawImageTaskFromDTO(DrawImageTaskDTO dto) {
        if (dto == null) {
            return null;
        }
        return DrawImageTask.builder()
                .imageMeta(buildImageMetaFromDTO(dto.getImageMetaDTO()))
                .imageModify(buildImageModifyFromDTO(dto.getImageModify()))
                .build();
    }

    private static ImageMeta buildImageMetaFromDTO(ImageMetaDTO dto) {
        if (dto == null) {
            return null;
        }
        return ImageMeta.builder()
                .providerType(dto.getProviderType())
                .providerKey(dto.getProviderKey())
                .anchorPos(new int[]{dto.getPos().getX(), dto.getPos().getY()})
                .build();
    }

    private static ImageModify buildImageModifyFromDTO(ImageModifyDTO dto) {
        if (dto == null) {
            return null;
        }
        return ImageModify.builder()
                .angle(dto.getAngle())
                .round(dto.getRound())
                .vertexPosList(dto.getVertexPosList() == null ? null : dto.getVertexPosList().stream()
                        .map(pair -> new int[]{pair.getX(), pair.getY()})
                        .collect(Collectors.toList())
                )
                .build();
    }

    private static ContextModifyTask buildContextModifyTaskFromDTO(ContextModifyTaskDTO dto) {
        if (dto == null) {
            return  null;
        }
        return ContextModifyTask.builder()
                .antialias(dto.getAntialias())
                .color(parseColorOrNull(dto.getColorRgb(), dto.getColorHex()))
                .fontName(dto.getFontName())
                .fontSize(dto.getFontSize())
                .build();
    }



    private static Color parseColorOrNull(List<Integer> colorRgb, String colorHex) {

        try {
            if (colorRgb != null) {
                int[] rgba = new int[4];
                rgba[0] = colorRgb.get(0).shortValue();
                rgba[1] = colorRgb.get(1).shortValue();
                rgba[2] = colorRgb.get(2).shortValue();
                rgba[3] = colorRgb.size() == 4 ? colorRgb.get(3).shortValue() : 255;
                return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
            } else if (colorHex != null) {
                int[] rgba = new int[4];
                String hex = colorHex.replace("#", "");
                rgba[0] = Short.parseShort(hex.substring(0, 2), 16);
                rgba[1] = Short.parseShort(hex.substring(2, 4), 16);
                rgba[2] = Short.parseShort(hex.substring(4, 6), 16);
                rgba[3] = hex.length() == 8 ? Short.parseShort(hex.substring(6, 8), 16) : 255;
                return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
