package hundun.petpet.share.block

import hundun.petpet.share.block.DrawTaskType.*
import hundun.petpet.share.block.ImageProviderType.FILE_BASE_IMAGE_PROVIDER
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import xmmt.dituon.share.Type
import xmmt.dituon.share.Type.IMG

fun decodePetpetBlockDTO(text: String): PetpetBlockDTO {
    return Json.decodeFromString(text);
}

@Serializable
data class PetpetBlockDTO constructor(
    val type: Type,
    val initTask: ContextInitTaskDTO,
    val frameTasks: List<FrameBlockDTO>,
)

@Serializable
data class FrameBlockDTO constructor(
    val tasks: List<DrawTaskDTO>
)

enum class ImageProviderType {
    FILE_BASE_IMAGE_PROVIDER,
    SIMPLE_GEOMETRIC_IMAGE_PROVIDER
}

enum class DrawTaskType {
    Empty,
    CONTEXT_INIT,
    CONTEXT_MODIFY,
    DRAW_IMAGE,
    DRAW_TEXT
}

@Serializable(with = DrawTaskDTOSerializer::class)
abstract class DrawTaskDTO {
    abstract val type: DrawTaskType
}

@Serializable
data class EmptyTaskDTO(override val type: DrawTaskType): DrawTaskDTO()

@Serializable
data class ContextInitTaskDTO constructor(
    override val type: DrawTaskType,
    val providerType: ImageProviderType,
    val providerKey: String,
    val width: Int,
    val height: Int,
    val backgroundConfig: BackgroundConfigDTO,
): DrawTaskDTO()

@Serializable
data class ContextModifyTaskDTO constructor(
    override val type: DrawTaskType,
    val antialias: Boolean? = null,
    val colorRgb: List<Int>? = null,
    val colorHex: String? = null,
    val fontName: String? = null,
    val fontSize: Int? = null,
): DrawTaskDTO()

@Serializable
data class DrawImageTaskDTO constructor(
    override val type: DrawTaskType,
    val imageMetaDTO: ImageMetaDTO,
    val imageModify: ImageModifyDTO? = null,
): DrawTaskDTO()

@Serializable
data class ImageModifyDTO constructor(
    val angle: Float? = null,
    val round: Boolean? = null,
    val vertexPosList: List<Pos>? = null
)

@Serializable
data class Pos constructor(
    val x: Int,
    val y: Int
)

@Serializable
data class DrawTextTaskDTO constructor(
    override val type: DrawTaskType,
    val text: String,
    val pos: Pos
): DrawTaskDTO()

@Serializable
data class ImageMetaDTO constructor(
    val providerType: ImageProviderType,
    val providerKey: String,
    val pos: Pos,
)

@Serializable
data class BackgroundConfigDTO constructor(
    val transparent: Boolean
)

object DrawTaskDTOSerializer : JsonContentPolymorphicSerializer<DrawTaskDTO>(DrawTaskDTO::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out DrawTaskDTO> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        return when {
            type.equals(CONTEXT_MODIFY.name) -> ContextModifyTaskDTO.serializer()
            type.equals(DRAW_IMAGE.name) -> DrawImageTaskDTO.serializer()
            type.equals(DRAW_TEXT.name) -> DrawTextTaskDTO.serializer()
            else -> EmptyTaskDTO.serializer()
        }
    }
}

fun main() {
    val data = PetpetBlockDTO(
        type = IMG,
        initTask = ContextInitTaskDTO(
            CONTEXT_INIT,
            providerType = FILE_BASE_IMAGE_PROVIDER,
            providerKey = "0.png",
            width = 100,
            height = 100,
            backgroundConfig = BackgroundConfigDTO(true)
        ),
        frameTasks = listOf(
            FrameBlockDTO(
                listOf(
                    ContextModifyTaskDTO(
                        CONTEXT_MODIFY,
                        fontSize = 12,
                        fontName = "黑体",
                        antialias = true,
                        colorHex = "#FFFFFF"
                    ),
                    DrawImageTaskDTO(
                        DRAW_IMAGE,
                        imageMetaDTO = ImageMetaDTO(
                            providerType = FILE_BASE_IMAGE_PROVIDER,
                            providerKey = "0.png",
                            pos = Pos(50, 50)
                        ),
                        imageModify = ImageModifyDTO(
                            round = true
                        )
                    ),
                    DrawTextTaskDTO(
                        DRAW_TEXT,
                        text = "你好世界",
                        pos = Pos(50, 50)
                    )
                )
            )
        )
    )
    val json = Json
    val string = json.encodeToString(data)
    println(string)
    val data2 : PetpetBlockDTO = json.decodeFromString(string)
    println(data2)
}