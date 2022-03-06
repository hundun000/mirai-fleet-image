package hundun.miraifleet.image.share.function

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.console.plugin.description.PluginDependency
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.firstIsInstanceOrNull
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import java.io.InputStream
import kotlin.math.min
import net.mamoe.mirai.message.data.Image as MiraiImage
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.utils.ExternalResource

class ImageCoreKt {
    val MiSansBold88 by lazy {
        Fonts["MiSans-Bold", 88F]
    }
    
    public fun ph(leftText:String, rightText:String):ExternalResource { 

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
}