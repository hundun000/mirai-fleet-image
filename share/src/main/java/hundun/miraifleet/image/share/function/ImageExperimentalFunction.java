package hundun.miraifleet.image.share.function;

import java.io.*;
import java.util.TimerTask;
import java.util.function.Function;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.AsListenerHost;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;
import hundun.miraifleet.image.share.function.hundun.miraifleet.image.share.function.ImageCoreKt;
import lombok.Data;
import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;

@AsListenerHost
public class ImageExperimentalFunction extends BaseFunction<ImageExperimentalFunction.SessionData>{

    @Getter
    private final CompositeCommandFunctionComponent commandComponent;
    private final int BW_TIMEOUT_SECOND = 30;
    private ImageCoreKt imageCoreKt = ImageCoreKt.INSTANCE;

    private enum ImageFunctionState {
        INIT,
        WAIT_BW_IMAGE,
        WAIT_XXX_IMAGE,
    }
    @Data
    public static class SessionData {
        ImageFunctionState state = ImageFunctionState.INIT;
        String drawText;
    }


    public ImageExperimentalFunction(
            BaseBotLogic baseBotLogic,
            JvmPlugin plugin,
            String characterName
            ) {
        super(
                baseBotLogic,
                plugin,
                characterName,
                "ImageExperimentalFunction",
                () -> new SessionData()
            );
        this.commandComponent = new CompositeCommandFunctionComponent(plugin, characterName, functionName);
    }

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) throws Exception {
        if (!checkCosPermission(event)) {
            return;
        }
        var sessionData = getOrCreateSessionData(event);
        if (sessionData.getState() == ImageFunctionState.INIT) {
            return;
        }
        FunctionReplyReceiver receiver = new FunctionReplyReceiver(event.getSubject(), plugin.getLogger());
        var currentState = sessionData.getState();

        Image image = (Image) event.getMessage().stream().filter(Image.class::isInstance).findFirst().orElse(null);
        if (image != null) {
            log.info("currentState = " + currentState + " and has image");
            // 在执行耗时操作前改变状态，防止被判为timeout
            sessionData.setState(ImageFunctionState.INIT);
            if (currentState == ImageFunctionState.WAIT_BW_IMAGE) {
                var externalResource = imageCoreKt.bw(sessionData.getDrawText(), image);
                Message outputMessage = receiver.uploadImageOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(outputMessage);
            }
        } else {
            log.info("wait image but messageChain no image");
        }



    }



    @Override
    public AbstractCommand provideCommand() {
        return commandComponent;
    }

    public class CompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public CompositeCommandFunctionComponent(JvmPlugin plugin, String characterName, String functionName) {
            super(plugin, characterName, functionName, functionName);
        }

        @SubCommand("bw")
        public void bw(CommandSender sender, String drawText) {
            if (!checkCosPermission(sender)) {
                return;
            }
            var sessionData = getOrCreateSessionData(sender);
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            if (sessionData.getState() == ImageFunctionState.INIT) {
                sessionData.setState(ImageFunctionState.WAIT_BW_IMAGE);
                sessionData.setDrawText(drawText);
                receiver.sendMessage("进入等待bw图片状态，请在" + BW_TIMEOUT_SECOND + "秒内发送图片");
                botLogic.getPluginScheduler().delayed(BW_TIMEOUT_SECOND * 1000, new TimeoutTask(receiver, sessionData));
            } else {
                receiver.sendMessage("当前已在等待bw图片状态，请先完成当前任务");
            }
        }

        private class TimeoutTask extends TimerTask {
            FunctionReplyReceiver receiver;
            SessionData sessionData;
            TimeoutTask(FunctionReplyReceiver receiver, SessionData sessionData) {
                this.receiver = receiver;
                this.sessionData = sessionData;
            }
            @Override
            public void run() {
                if (sessionData.state == ImageFunctionState.WAIT_BW_IMAGE) {
                    receiver.sendMessage("已超时，离开等待图片状态。");
                    sessionData.setState(ImageFunctionState.INIT);
                }
            }
        }

        @SubCommand("假装自己是图片")
        public void fakeImage(CommandSender sender) {
            var sessionData = getOrCreateSessionData(sender);
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            if (sessionData.getState() == ImageFunctionState.WAIT_BW_IMAGE) {
                receiver.sendMessage("已处理假装收到图片，离开等待图片状态");
                sessionData.setState(ImageFunctionState.INIT);
                return;
            }
        }


    }

}
