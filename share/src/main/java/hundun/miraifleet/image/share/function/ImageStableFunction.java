package hundun.miraifleet.image.share.function;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;

import hundun.miraifleet.framework.core.helper.file.CacheableFileHelper;
import hundun.miraifleet.image.share.function.hundun.miraifleet.image.share.function.ImageCoreKt;
import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.function.Function;

public class ImageStableFunction extends BaseFunction<Void>{

    @Getter
    private final CompositeCommandFunctionComponent commandComponent;

    private final ImageCoreKt imageCoreKt = ImageCoreKt.INSTANCE;
    private final PatPatCoreKt patPatCoreKt = PatPatCoreKt.INSTANCE;
    private static final int PATPAT_HANDS_SIZE = 5;
    private final File[] patpatHandFiles = new File[PATPAT_HANDS_SIZE];
    private ExternalResource defaultTarget;
    private final CacheableFileHelper cacheableFileHelper;

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
        this.cacheableFileHelper = new CacheableFileHelper(resolveFunctionCacheFileFolder());
        init();
    }

    private void init() {
        for (int i = 0; i < PATPAT_HANDS_SIZE; i++) {
            patpatHandFiles[i] = (resolveFunctionDataFile("patpat/img" + i + ".png"));
        }
        defaultTarget = ExternalResource.create(resolveFunctionDataFile("patpat/defaultTarget.png"));
    }

    @Override
    public AbstractCommand provideCommand() {
        return commandComponent;
    }

    public class CompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public CompositeCommandFunctionComponent(JvmPlugin plugin, String characterName, String functionName) {
            super(plugin, characterName, functionName, functionName);
        }
        
        @SubCommand("ph")
        public void ph(CommandSender sender, String leftText, String rightText) {
            if (!checkCosPermission(sender)) {
                return;
            }
            sender.getCoroutineContext();
            ExternalResource externalResource = imageCoreKt.ph(leftText, rightText);
            FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, log);
            Message image = receiver.uploadImageOrNotSupportPlaceholder(externalResource);
            if (image instanceof Image) {
                log.info("has real image: " + Arrays.toString(((Image) image).getMd5()));
            }
            receiver.sendMessage(image);
        }

        @SubCommand("patpatDefault")
        public void patpatDefault(CommandSender sender) {
            var targetAvatarImage = UtilsKt.externalResourceToSkioImage(defaultTarget);
            patpat(sender, targetAvatarImage, "default");
        }

        @SubCommand("patpat")
        public void patpat(CommandSender sender, User target) {
            var targetAvatarImage = UtilsKt.getContactOrBotAvatarImage(target);
            patpat(sender, targetAvatarImage, String.valueOf(target.getId()));
        }


        private InputStream calculatePatPatImage(org.jetbrains.skia.Image targetAvatarImage) {
            var patpatResult = patPatCoreKt.patpat(targetAvatarImage, patpatHandFiles, 0.05);
            return new ByteArrayInputStream(patpatResult.getBytes());
        }




        private void patpat(CommandSender sender, org.jetbrains.skia.Image targetAvatarImage, String cacheId) {

            Function<String, InputStream> uncachedPatPatFileProvider = it -> calculatePatPatImage(targetAvatarImage);
            File patpatResultFile = cacheableFileHelper.fromCacheOrProvider(cacheId, uncachedPatPatFileProvider);
            if (patpatResultFile != null) {
                ExternalResource externalResource = ExternalResource.create(patpatResultFile).toAutoCloseable();
                FunctionReplyReceiver receiver = new FunctionReplyReceiver(sender, plugin.getLogger());
                Message message = receiver.uploadImageOrNotSupportPlaceholder(externalResource);
                receiver.sendMessage(message);
            }

        }

    }

}
