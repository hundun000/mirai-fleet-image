package hundun.miraifleet.image.share.function

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.ContactOrBot
import net.mamoe.mirai.utils.ExternalResource
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect

internal val httpClient = HttpClient(OkHttp)

fun Canvas.bar(block: Canvas.() -> Unit) {
    save()
    block()
    restore()
}

fun Rect.copy(
    left: Float = this.left,
    top: Float = this.top,
    right: Float = this.right,
    bottom: Float = this.bottom
) = Rect(left, top, right, bottom)

fun getContactOrBotAvatarImage (target: ContactOrBot): Image? {
    var image: Image? = null
    runBlocking {
        httpClient.get<ByteArray>(target.avatarUrl).apply {
            image = Image.makeFromEncoded(this)
        }
    }
    return image;
}

fun externalResourceToSkioImage (externalResource: ExternalResource): Image {
    val skikoImage = externalResource.inputStream().use { it ->
        Image.makeFromEncoded(it.readBytes())
    }
    return skikoImage;
}