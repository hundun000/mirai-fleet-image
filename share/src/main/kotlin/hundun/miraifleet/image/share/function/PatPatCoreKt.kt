package hundun.miraifleet.image.share.function

import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.ContactOrBot
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.jetbrains.skia.*
import org.laolittle.plugin.getBytes
import org.laolittle.plugin.gif.GifImage
import org.laolittle.plugin.gif.GifSetting
import org.laolittle.plugin.gif.buildGifImage
import java.io.File

object PatPatCoreKt {

    private const val width = 320
    private const val height = 320




    @JvmName("patpat")
    fun jpatpat(image: Image, handFiles: Array<File>, delay: Double = .05) = runBlocking { patpat(image, handFiles, delay) }

    suspend fun patpat(image: Image, handFiles: Array<File>, delay: Double = .05): GifImage {
        val hands = Array(5) { Image.makeFromEncoded(handFiles[it].readBytes())}
        val gifImage = buildGifImage(GifSetting(width, height, 100, true, GifSetting.Repeat.Infinite)) {
            addFrame(patFrame(Rect(40f, 40f, 300f, 300f), Point(0f, 0f), image, 0, hands).getBytes(), delay)
            addFrame(patFrame(Rect(40f, 70f, 300f, 300f), Point(0f, 0f), image, 1, hands).getBytes(), delay)
            addFrame(patFrame(Rect(33f, 105f, 300f, 300f), Point(0f, 0f), image, 2, hands).getBytes(), delay)
            addFrame(patFrame(Rect(37f, 90f, 300f, 300f), Point(0f, 0f), image, 3, hands).getBytes(), delay)
            addFrame(patFrame(Rect(40f, 65f, 300f, 300f), Point(0f, 0f), image, 4, hands).getBytes(), delay)
        }
        return gifImage
    }

    private val whitePaint = Paint().apply { color = Color.WHITE }
    private val srcInPaint = Paint().apply { blendMode = BlendMode.SRC_IN }


    private const val imgW = width.toFloat()
    private const val imgH = height.toFloat()
/*    fun initHands(files: Array<File>) {
        hands = Array(5) { Image.makeFromEncoded(files[it].readBytes())}
    }

    private lateinit var hands: Array<Image>*/

    private fun patFrame(imgDst: Rect, handPoint: Point, image: Image, no: Int, hands: Array<Image>): Image {
        val hand = hands[no]

        return Surface.makeRasterN32Premul(width, height).apply {

            canvas.apply {
                bar {
                    val radius = (width shr 1).toFloat()
                    translate(imgDst.left, imgDst.top)
                    scale(imgDst.width / width, imgDst.height / height)
                    drawCircle(imgW * .5f, imgH * .5f, radius, whitePaint)
                    drawImageRect(
                        image,
                        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
                        Rect.makeWH(imgW, imgH),
                        FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST),
                        srcInPaint,
                        true
                    )
                }

                drawImageRect(
                    hand,
                    Rect.makeWH(hand.width.toFloat(), hand.height.toFloat()),
                    Rect(handPoint.x, handPoint.y, handPoint.x + width, handPoint.y + height),
                    SamplingMode.CATMULL_ROM,
                    null,
                    true
                )
                //drawImageRect(hand, Rect(handPoint.x, handPoint.y, handPoint.x + width, handPoint.y + height))
                //makeImageSnapshot().getBytes().also { File("out$no.png").writeBytes(it) }
            }
        }.makeImageSnapshot()
    }
}