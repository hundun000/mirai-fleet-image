package hundun.miraifleet.image;

import org.jetbrains.annotations.NotNull;

import hundun.miraifleet.image.botlogic.ImageBotLogic;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author hundun
 * Created on 2021/08/09
 */
public class ImagePlugin extends JavaPlugin {
    public static final ImagePlugin INSTANCE = new ImagePlugin(); 
    
    ImageBotLogic botLogic;
    
    public ImagePlugin() {
        super(new JvmPluginDescriptionBuilder(
                "hundun.fleet.image",
                "0.1.0"
            )
            .build());
    }
    
    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        
    }
    
    @Override
    public void onEnable() {
        botLogic = new ImageBotLogic(this);
        botLogic.onBotLogicEnable();
    }
    
    @Override
    public void onDisable() {
        botLogic.onDisable();
        // 由GC回收即可
        botLogic = null;
    }
}
