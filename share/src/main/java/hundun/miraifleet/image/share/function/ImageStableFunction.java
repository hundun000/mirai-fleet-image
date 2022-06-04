package hundun.miraifleet.image.share.function;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;


import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.ExternalResource;
import xmmt.dituon.share.ImageSynthesis;

import java.awt.image.BufferedImage;


public class ImageStableFunction extends BaseFunction<Void>{

    @Getter
    private final CompositeCommandFunctionComponent commandComponent;

    private SharedPetFunction petFunction;

    public ImageStableFunction(
            BaseBotLogic baseBotLogic,
            JvmPlugin plugin,
            String characterName
    ) {
        super(
                baseBotLogic,
                plugin,
                characterName,
                "ImageStableFunction",
                null
        );
        this.commandComponent = new CompositeCommandFunctionComponent(plugin, characterName, functionName);
    }

    public void lazyInitSharedFunction(SharedPetFunction petFunction) {
        this.petFunction = petFunction;
    }

    @Override
    public AbstractCommand provideCommand() {
        return commandComponent;
    }

    public class CompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public CompositeCommandFunctionComponent(JvmPlugin plugin, String characterName, String functionName) {
            super(plugin, characterName, functionName, functionName);
        }

        @SubCommand("ConsoleTest画图")
        public void playPhoneTest(CommandSender sender, String key) {
            if (!checkCosPermission(sender)) {
                return;
            }
            BufferedImage fromAvatarImage = null;
            BufferedImage toAvatarImage = petFunction.getDefaultPetServiceFrom();
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            var resultFile = petFunction.petService(fromAvatarImage, toAvatarImage, key);
            if (resultFile != null) {
                ExternalResource externalResource = ExternalResource.create(resultFile).toAutoCloseable();
                Message message = receiver.uploadImageOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(message);
            }
        }


        @SubCommand("画图")
        public void playPhone(CommandSender sender, User target, String key) {
            if (!checkCosPermission(sender)) {
                return;
            }
            BufferedImage fromAvatarImage = ImageSynthesis.getAvatarImage(sender.getUser().getAvatarUrl());
            BufferedImage toAvatarImage = ImageSynthesis.getAvatarImage(target.getAvatarUrl());
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            var resultFile = petFunction.petService(fromAvatarImage, toAvatarImage, key);
            if (resultFile != null) {
                ExternalResource externalResource = ExternalResource.create(resultFile).toAutoCloseable();
                Message message = receiver.uploadImageOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(message);
            }
        }





    }

}
