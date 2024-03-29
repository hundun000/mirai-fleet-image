package hundun.miraifleet.image.share.function;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;


import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.ConsoleCommandSender;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.ExternalResource;
import xmmt.dituon.share.ImageSynthesisCore;
import xmmt.dituon.share.TextData;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;


public class ImageStableFunction extends BaseFunction {

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
                "ImageStableFunction"
        );
        this.commandComponent = new CompositeCommandFunctionComponent();
    }

    public void lazyInitSharedFunction(SharedPetFunction petFunction) {
        this.petFunction = petFunction;
    }

    @Override
    public AbstractCommand provideCommand() {
        return commandComponent;
    }
    
    @Override
    public AbstractCommand provideDebugCommand() {
        return new DebugCompositeCommandFunctionComponent();
    }
    
    public static List<TextData> textDataForLianyebing(String text) {
        int fullWidth = 392;
        int board = 20;
        int textWidth = fullWidth - board * 2;
        int fontSizeByHeight = 40;
        int fontSizeByWidth = (int) (textWidth * 1.0 / text.length());
        int fontSize = Math.min(fontSizeByWidth, fontSizeByHeight);
        int x = (fullWidth / 2) - (text.length() * fontSize / 2);
        return Arrays.asList(new TextData(
                text,
                Arrays.asList(x, 300),
                null,
                "Misans",
                fontSize
        ));
    }
    
    public static List<TextData> textDataForJiusi(String text) {
        int fullWidth = 1080;
        int board = 50;
        int textWidth = fullWidth - board * 2;
        int fontSizeByHeight = 150;
        int fontSizeByWidth = (int) (textWidth * 1.0 / text.length());
        int fontSize = Math.min(fontSizeByWidth, fontSizeByHeight);
        int x = (fullWidth / 2) - (text.length() * fontSize / 2);
        int y = board + fontSize;
        return Arrays.asList(new TextData(
                text,
                Arrays.asList(x, y),
                null,
                "Misans",
                fontSize
        ));
    }
    
    public class DebugCompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public DebugCompositeCommandFunctionComponent() {
            super(plugin, botLogic, new DebugLevelFunctionComponentConstructPack(characterName, functionName));
        }
        
        @SubCommand("画图Console")
        public void simpleTemplateTest(ConsoleCommandSender sender, String key) {
            if (!checkCosPermission(sender)) {
                return;
            }
            BufferedImage fromAvatarImage = null;
            BufferedImage toAvatarImage = petFunction.getDefaultPetServiceFrom();
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            var resultFile = petFunction.petService(fromAvatarImage, toAvatarImage, key);
            if (resultFile != null) {
                ExternalResource externalResource = ExternalResource.create(resultFile).toAutoCloseable();
                Message message = receiver.uploadImageAndCloseOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(message);
            }
        }
        
        @SubCommand("petpetConsole")
        public void petpetConsole(ConsoleCommandSender sender) {
            getCommandComponent().imageGeneral(sender, null, "petpet", null);
        }
        
        @SubCommand("阿米娅玩手机Console")
        public void plauPhoneConsole(ConsoleCommandSender sender) {
            getCommandComponent().imageGeneral(sender, null, "阿米娅玩手机", null);
        }

        @SubCommand("北方指人Console")
        public void lianyebingConsole(ConsoleCommandSender sender, String text) {
            getCommandComponent().imageGeneral(sender, null, "北方指人", textDataForLianyebing(text));
        }
        
        @SubCommand("94Console")
        public void jiusiConsole(ConsoleCommandSender sender, String text) {
            getCommandComponent().jiusi(sender, null, text);
        }
    }

    public class CompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public CompositeCommandFunctionComponent() {
            super(plugin, botLogic, new UserLevelFunctionComponentConstructPack(characterName, functionName));
        }

        @SubCommand("画图")
        public void simpleTemplate(CommandSender sender, User target, String key) {
            if (!checkCosPermission(sender)) {
                return;
            }
            BufferedImage fromAvatarImage = ImageSynthesisCore.getAvatarImage(sender.getUser().getAvatarUrl());
            BufferedImage toAvatarImage = ImageSynthesisCore.getAvatarImage(target.getAvatarUrl());
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            var resultFile = petFunction.petService(fromAvatarImage, toAvatarImage, key);
            if (resultFile != null) {
                ExternalResource externalResource = ExternalResource.create(resultFile).toAutoCloseable();
                Message message = receiver.uploadImageAndCloseOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(message);
            }
        }

        
        @SubCommand("94")
        public void jiusi(CommandSender sender, User target, String text) {
            imageGeneral(sender, target, "94", textDataForJiusi("请问你们看到" + text + "了吗？"));
        }


        private void imageGeneral(CommandSender sender, User target, String key, List<TextData> additionTextDatas) {
            if (!checkCosPermission(sender)) {
                return;
            }
            BufferedImage fromAvatarImage = (sender != null && sender.getUser() != null) ? ImageSynthesisCore.getAvatarImage(sender.getUser().getAvatarUrl()) : petFunction.getDefaultPetServiceFrom();
            BufferedImage toAvatarImage = target != null ? ImageSynthesisCore.getAvatarImage(target.getAvatarUrl()) : petFunction.getDefaultPetServiceFrom();
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
            var resultFile = petFunction.petService(fromAvatarImage, toAvatarImage, key, additionTextDatas);
            if (resultFile != null) {
                ExternalResource externalResource = ExternalResource.create(resultFile).toAutoCloseable();
                Message message = receiver.uploadImageAndCloseOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(message);
            }
        }

    }

}
