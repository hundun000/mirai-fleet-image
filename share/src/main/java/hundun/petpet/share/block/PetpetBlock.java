package hundun.petpet.share.block;

import hundun.petpet.share.block.dto.ImageProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import xmmt.dituon.share.Type;

import java.awt.*;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PetpetBlock {

    Type type;
    ContextInitTask initTask;
    List<FrameBlock> frameBlocks;
    PetpetBlockContext context;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ContextInitTask implements IDrawTask {
        int width;
        int height;
        String providerKey;
        ImageProviderType providerType;
        BackgroundConfig backgroundConfig;
    }

    @Setter
    @Getter
    public static class PetpetBlockContext {
        int width;
        int height;
        int imageType;

        boolean antialias;
        Color color;
        Font font;

        BackgroundConfig backgroundConfig;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BackgroundConfig {
        boolean transparent;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FrameBlock {
        List<IDrawTask> tasks;

    }

    public interface IDrawTask {

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ContextModifyTask implements IDrawTask {
        Boolean antialias;
        Color color;
        String fontName;
        Integer fontSize;
        BackgroundConfig backgroundConfig;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawImageTask implements IDrawTask {
        ImageMeta imageMeta;
        ImageModify imageModify;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ImageMeta {
        String providerKey;
        ImageProviderType providerType;
        int[] anchorPos;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ImageModify {
        Float angle;
        Boolean round;
        List<int[]> vertexPosList;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class DrawTextTask implements IDrawTask {
        String text;
        int[] anchorPos;
    }
}
