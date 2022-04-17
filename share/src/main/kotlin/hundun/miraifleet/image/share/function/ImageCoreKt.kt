package hundun.miraifleet.image.share.function.hundun.miraifleet.image.share.function

import hundun.miraifleet.image.share.function.externalResourceToSkioImage
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import java.io.InputStream
import net.mamoe.mirai.message.data.Image as MiraiImage
import net.mamoe.mirai.utils.ExternalResource

object ImageCoreKt {

    fun ph(leftText:String, rightText:String): ExternalResource {

        val words = arrayOf(leftText, rightText);
    
        val phHeight = 170
        val widthPlus = 12
    
        val leftText = TextLine.make(words[0], MiSansBold88)
        val leftPorn = Surface.makeRasterN32Premul(leftText.width.toInt() + (widthPlus shl 1), phHeight)
        val paint = Paint().apply {
            isAntiAlias = true
        }
    
        leftPorn.canvas.apply {
            clear(Color.makeARGB(255, 0, 0, 0))
            drawTextLine(
                leftText,
                (leftPorn.width - leftText.width) / 2 + 5,
                ((leftPorn.height shr 1) + (leftText.height / 4)),
                paint.apply { color = Color.makeARGB(255, 255, 255, 255) }
            )
        }
    
        val rightText = TextLine.make(words[1], MiSansBold88)
        val rightPorn = Surface.makeRasterN32Premul(
            rightText.width.toInt() + (widthPlus shl 1) + 20,
            rightText.height.toInt()
        )
    
        rightPorn.canvas.apply {
            val rRect = RRect.makeComplexXYWH(
                ((rightPorn.width - rightText.width) / 2) - widthPlus,
                0F,
                rightText.width + widthPlus,
                rightText.height - 1,
                floatArrayOf(19.5F)
            )
            drawRRect(
                rRect, paint.apply { color = Color.makeARGB(255, 255, 145, 0) }
            )
            // clear(Color.makeARGB(255, 255,144,0))
            // drawCircle(100F, 100F, 50F, Paint().apply { color = Color.BLUE })
            drawTextLine(
                rightText,
                ((rightPorn.width - rightText.width - widthPlus.toFloat()) / 2),
                ((rightPorn.height shr 1) + (rightText.height / 4) + 2),
                paint.apply { color = Color.makeARGB(255, 0, 0, 0) }
            )
        }
    
        Surface.makeRasterN32Premul(leftPorn.width + rightPorn.width, phHeight).apply {
            canvas.apply {
                clear(Color.makeARGB(255, 0, 0, 0))
                drawImage(leftPorn.makeImageSnapshot(), 0F, 0F)
                drawImage(
                    rightPorn.makeImageSnapshot(),
                    leftPorn.width.toFloat() - (widthPlus shr 1),
                    (((phHeight - rightPorn.height) shr 1) - 2).toFloat()
                )
            }
            return makeImageSnapshot().toExternalResource();
        }
    }

    @JvmName("bw")
    fun jbw(content:String, image:MiraiImage): ExternalResource = runBlocking { bw(content, image) }

    suspend fun bw(content:String, image:MiraiImage): ExternalResource {

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val skikoImage = HttpClient(OkHttp).use { client ->
            client.get<InputStream>(image.queryUrl()).use { input ->
                Image.makeFromEncoded(input.readBytes())
            }
        }


        val h = skikoImage.height
        val w = skikoImage.width
        val foo = h / 6
        val bar = foo / 1.4f
        val fontSize = if (bar.toInt() * content.length > w) ((w * 0.8f) / content.length) else bar
        val text = TextLine.make(content, Fonts["MiSans-Bold", fontSize])

        Surface.makeRasterN32Premul(skikoImage.width, h + (foo * 1.4f).toInt()).apply {
            canvas.apply {
                clear(Color.BLACK)
                drawImage(skikoImage, 0F, 0F, paint.apply {
                    colorFilter = ColorFilter.makeMatrix(
                        ColorMatrix(
                            0.33F, 0.38F, 0.29F, 0F, 0F,
                            0.33F, 0.38F, 0.29F, 0F, 0F,
                            0.33F, 0.38F, 0.29F, 0F, 0F,
                            0.33F, 0.38F, 0.29F, 1F, 0F,
                        )
                    )
                })

                drawTextLine(text,
                    ((width - text.width) / 2),
                    h + ((foo + text.height) / 2),
                    paint.apply { color = Color.WHITE })

                return makeImageSnapshot().toExternalResource();
            }
        }

    }

    fun anyImageAddBottomText(content:String, resource:ExternalResource):ExternalResource{

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val skikoImage = externalResourceToSkioImage(resource)

        val h = skikoImage.height
        val w = skikoImage.width
        val foo = h / 6
        val bar = foo / 1.4f
        val fontSize = if (bar.toInt() * content.length > w) ((w * 0.8f) / content.length) else bar
        val text = TextLine.make(content, Fonts["MiSans-Bold", fontSize])

        Surface.makeRasterN32Premul(skikoImage.width, h + (foo * 1.4f).toInt()).apply {
            canvas.apply {
                clear(Color.BLACK)
                drawImage(skikoImage, 0F, 0F)

                drawTextLine(text,
                    ((width - text.width) / 2),
                    h + ((foo + text.height) / 2),
                    paint.apply { color = Color.WHITE })

                return makeImageSnapshot().toExternalResource();
            }
        }

    }

}