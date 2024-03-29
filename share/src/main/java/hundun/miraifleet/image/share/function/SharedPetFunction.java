package hundun.miraifleet.image.share.function;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import hundun.miraifleet.image.share.util.MD5HashUtil;
import kotlin.Pair;

import org.jetbrains.annotations.Nullable;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.helper.file.CacheableFileHelper;
import hundun.miraifleet.framework.helper.repository.SingletonDocumentRepository;
import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.event.events.NudgeEvent;
import xmmt.dituon.share.*;

import java.util.List;
import java.util.function.Function;
/**
 * @author hundun
 * Created on 2022/06/02
 */
public class SharedPetFunction extends BaseFunction {
    
    private final CacheableFileHelper petServiceCache;
    @Getter
    private BufferedImage defaultPetServiceFrom;
    private final SingletonDocumentRepository<BaseServiceConfig> petServiceConfigRepository;
    private BasePetService petService = new BasePetService();
    
    public SharedPetFunction(
            BaseBotLogic baseBotLogic,
            JvmPlugin plugin,
            String characterName
            ) {
        super(
                baseBotLogic,
                plugin,
                characterName,
                "SharedPetFunction"
                );
        this.petServiceCache = new CacheableFileHelper(resolveFunctionCacheFileFolder(), "petService", plugin.getLogger());
        this.petServiceConfigRepository = new SingletonDocumentRepository<BaseServiceConfig>(
                plugin,
                resolveConfigRepositoryFile("petServiceConfigRepository.json"),
                BaseServiceConfig.class,
                () -> new BaseServiceConfig()
                );
        try {
            defaultPetServiceFrom = ImageIO.read(resolveFunctionDataFile("petService/defaultAvatar.png"));
        } catch (IOException e) {
            plugin.getLogger().error("init error:", e);
        }
        petService.readBaseServiceConfig(petServiceConfigRepository.findSingleton());
        petService.readData(resolveFunctionDataFile("petService/templates"));
        
    }


    /**
     * 因为只需要比较md5，所以format固定即可，不需要和实际格式一致
     */
    private static final String IMAGE_FORMAT_FOR_GENERATE_CACHE_ID = "png";
    
    @Nullable
    public File petService(BufferedImage from, BufferedImage to, String key) {
        return petService(from, to, key, null);
    }
    
    
    @Nullable
    public File petService(BufferedImage from, BufferedImage to, String key, List<TextData> additionTextDatas) {
        String cacheId = key + "--" + MD5HashUtil.imageMD5(from, IMAGE_FORMAT_FOR_GENERATE_CACHE_ID)
                + "--" + MD5HashUtil.imageMD5(to, IMAGE_FORMAT_FOR_GENERATE_CACHE_ID)
                + (additionTextDatas != null ? ("--" + MD5HashUtil.stringMD5(additionTextDatas.toString())) : "");
        log.info("petService for cacheId = " + cacheId);
        Function<String, InputStream> uncachedFileProvider = uslessCacheId -> calculatePetServiceImage(from, to, key, additionTextDatas);
        File resultFile = petServiceCache.fromCacheOrProvider(cacheId, uncachedFileProvider);
        return resultFile;
    }
    
    private InputStream calculatePetServiceImage(BufferedImage from, BufferedImage to, String key, List<TextData> additionTextDatas) {
        AvatarExtraDataProvider avatarExtraData = new AvatarExtraDataProvider(
                () -> from,
                () -> to,
                null,
                null
                );
        
        Pair<InputStream, String> petServiceResult = petService.generateImage(key, avatarExtraData, null, additionTextDatas);
        return petServiceResult != null ? petServiceResult.getFirst() : null;
    }

    public BufferedImage userAvatarOrDefaultAvatar(CommandSender sender) {
        return sender.getUser() != null ? ImageSynthesisCore.getAvatarImage(sender.getUser().getAvatarUrl()) : this.getDefaultPetServiceFrom();
    }

    public BufferedImage userAvatarOrDefaultAvatar(NudgeEvent event) {
        return ImageSynthesisCore.getAvatarImage(event.getFrom().getAvatarUrl());
    }

    @Override
    public AbstractCommand provideCommand() {
        return null;
    }

    

}
