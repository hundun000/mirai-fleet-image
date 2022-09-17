package hundun.miraifleet.image.share.function;

import java.awt.image.BufferedImage;
import java.util.TimerTask;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.AsListenerHost;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;
import hundun.miraifleet.framework.core.function.SessionDataMap;
import hundun.miraifleet.framework.core.function.SessionDataMap.GroupMessageToSessionIdType;
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
import xmmt.dituon.share.ImageSynthesisCore;

@AsListenerHost
public class ImageExperimentalFunction extends BaseFunction {

    @Getter
    private final CompositeCommandFunctionComponent commandComponent;
    private final int BW_TIMEOUT_SECOND = 30;
    private SharedPetFunction petFunction;
    private final SessionDataMap<ImageExperimentalFunction.SessionData> sessionDataMap;
    
    private enum ImageFunctionState {
        INIT,
        WAIT_BW_IMAGE,
        WAIT_XXX_IMAGE,
    }
    @Data
    public static class SessionData {
        ImageFunctionState state = ImageFunctionState.INIT;
        String drawText;
        String petpetKey;
        BufferedImage toAvatarImage;
        BufferedImage fromAvatarImage;
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
                "ImageExperimentalFunction"
            );
        this.sessionDataMap = new SessionDataMap<>(
                GroupMessageToSessionIdType.USE_GROUP_ID, 
                () -> new SessionData());
        this.commandComponent = new CompositeCommandFunctionComponent();
    }

    public void lazyInitSharedFunction(SharedPetFunction petFunction) {
        this.petFunction = petFunction;
    }

    @EventHandler
    public void onMessage(@NotNull MessageEvent event) throws Exception {
        if (!checkCosPermission(event)) {
            return;
        }
        var sessionData = sessionDataMap.getOrCreateSessionData(event);
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
                var resultFile = petFunction.petService(sessionData.getFromAvatarImage(), sessionData.toAvatarImage, sessionData.getPetpetKey());
                if (resultFile != null) {
                    ExternalResource externalResource = ExternalResource.create(resultFile).toAutoCloseable();
                    Message message = receiver.uploadImageAndCloseOrNotSupportPlaceholder(externalResource);
                    receiver.sendMessage(message);
                } else {
                    log.info("petService resultFile null");
                }
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
        public CompositeCommandFunctionComponent() {
            super(plugin, botLogic, new UserLevelFunctionComponentConstructPack(characterName, functionName));
        }

        @SubCommand("画图")
        public void bw(CommandSender sender, User target, String petpetKey) {
            if (!checkCosPermission(sender)) {
                return;
            }

            var sessionId = sessionDataMap.getSessionId(sender);
            var sessionData = sessionDataMap.getOrCreateSessionData(sessionId);
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            if (sessionData.getState() == ImageFunctionState.INIT) {
                sessionData.setState(ImageFunctionState.WAIT_BW_IMAGE);
                sessionData.setPetpetKey(petpetKey);
                BufferedImage fromAvatarImage = ImageSynthesisCore.getAvatarImage(sender.getUser().getAvatarUrl());
                BufferedImage toAvatarImage = ImageSynthesisCore.getAvatarImage(target.getAvatarUrl());
                sessionData.setFromAvatarImage(fromAvatarImage);
                sessionData.setToAvatarImage(toAvatarImage);
                receiver.sendMessage("进入等待petpet图片状态，请在" + BW_TIMEOUT_SECOND + "秒内发送图片");
                botLogic.getPluginScheduler().delayed(BW_TIMEOUT_SECOND * 1000, new TimeoutTask(receiver, sessionId));
            } else {
                receiver.sendMessage("当前已在等待petpet图片状态，请先完成当前任务");
            }
        }

        private class TimeoutTask extends TimerTask {
            FunctionReplyReceiver receiver;
            String sessionId;
            TimeoutTask(FunctionReplyReceiver receiver, String sessionId) {
                this.receiver = receiver;
                this.sessionId = sessionId;
            }
            @Override
            public void run() {
                SessionData sessionData = sessionDataMap.getOrCreateSessionData(sessionId);
                if (sessionData.state == ImageFunctionState.WAIT_BW_IMAGE) {
                    receiver.sendMessage("已超时，离开等待图片状态。");
                    // FIXME wait removeSessionData change to public
                    // sessionDataMap.removeSessionData(sessionId);
                } else {
                    plugin.getLogger().warning("id = " + sessionId + " state = " + sessionData.state + " but TimeoutTask arrival");
                }
            }
        }

        @SubCommand("假装自己是图片")
        public void fakeImage(CommandSender sender) {
            var sessionData = sessionDataMap.getOrCreateSessionData(sender);
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            if (sessionData.getState() == ImageFunctionState.WAIT_BW_IMAGE) {
                receiver.sendMessage("已处理假装收到图片，离开等待图片状态");
                sessionData.setState(ImageFunctionState.INIT);
                return;
            }
        }


    }

}
