package hundun.miraifleet.image.botlogic;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.image.share.function.ImageDemoFunction;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;

/**
 * @author hundun
 * Created on 2021/08/06
 */
public class ImageBotLogic extends BaseBotLogic {

    ImageDemoFunction imageFunction;

    
    public ImageBotLogic(JavaPlugin plugin) {
        super(plugin, "画图");
        
        imageFunction = new ImageDemoFunction(this, plugin, characterName);
        imageFunction.setSkipRegisterCommand(false);
        functions.add(imageFunction);

    }
    
}
