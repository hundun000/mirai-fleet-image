package hundun.petpet.share.block.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hundun.petpet.share.block.PetpetBlockException;
import hundun.petpet.share.block.dto.DrawTaskType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import xmmt.dituon.share.Type;

import java.util.Arrays;
import java.util.List;

public class JPetpetData {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static PetpetBlockDTO decodePetpetBlockDTO(String text) throws PetpetBlockException {
        try {
            return objectMapper.readValue(text, PetpetBlockDTO.class);
        } catch (JsonProcessingException e) {
            throw PetpetBlockException.fromJsonException(e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PetpetBlockDTO {
        Type type;
        ContextInitTaskDTO initTask;
        List<FrameBlockDTO> frameTasks;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FrameBlockDTO {
        List<DrawTaskDTO> tasks;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = As.EXISTING_PROPERTY,
            property = "type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = EmptyTaskDTO.class, name = "EMPTY"),
            @JsonSubTypes.Type(value = DrawTextTaskDTO.class, name = "DRAW_TEXT"),
            @JsonSubTypes.Type(value = DrawImageTaskDTO.class, name = "DRAW_IMAGE"),
            @JsonSubTypes.Type(value = ContextInitTaskDTO.class, name = "CONTEXT_INIT"),
            @JsonSubTypes.Type(value = ContextModifyTaskDTO.class, name = "CONTEXT_MODIFY")
    })
    @Data
    @NoArgsConstructor
    @SuperBuilder
    public static abstract class DrawTaskDTO {
        DrawTaskType type;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class EmptyTaskDTO extends DrawTaskDTO {

    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class ContextInitTaskDTO extends DrawTaskDTO {
        ImageProviderType providerType;
        String providerKey;
        Integer width;
        Integer height;
        BackgroundConfigDTO backgroundConfig;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class ContextModifyTaskDTO extends DrawTaskDTO {
        Boolean antialias;
        List<Integer> colorRgb;
        String colorHex;
        String fontName;
        Integer fontSize;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DrawImageTaskDTO extends DrawTaskDTO {
        ImageMetaDTO imageMetaDTO;
        ImageModifyDTO imageModify;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @ToString(callSuper = true)
    public static class ImageModifyDTO {
        Float angle;
        Boolean round;
        List<Pos> vertexPosList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pos {
        Integer x;
        Integer y;
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DrawTextTaskDTO extends DrawTaskDTO {
        String text;
        Pos pos;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageMetaDTO {
        ImageProviderType providerType;
        String providerKey;
        Pos pos;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BackgroundConfigDTO {
        Boolean transparent;
    }


    public static void main(String[] args) {
        DrawTextTaskDTO drawTextTaskDTO = DrawTextTaskDTO.builder()
                .type(DrawTaskType.DRAW_TEXT)
                .text("foo")
                .pos(new Pos(1, 1))
                .build();
        EmptyTaskDTO emptyTaskDTO = EmptyTaskDTO.builder()
                .type(DrawTaskType.EMPTY)
                .build();
        FrameBlockDTO frameBlockDTO = new FrameBlockDTO(Arrays.asList(drawTextTaskDTO, emptyTaskDTO));
        String frameBlockDTOJson;
        try {
            frameBlockDTOJson = objectMapper.writeValueAsString(frameBlockDTO);
            System.out.println("string object: " + frameBlockDTOJson);

            frameBlockDTO = objectMapper.readValue(frameBlockDTOJson, FrameBlockDTO.class);
            System.out.println("Deserialized object: " + frameBlockDTO);

            frameBlockDTOJson = objectMapper.writeValueAsString(frameBlockDTO);
            System.out.println("string object: " + frameBlockDTOJson);

            frameBlockDTO = objectMapper.readValue(frameBlockDTOJson, FrameBlockDTO.class);
            System.out.println("Deserialized object: " + frameBlockDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
