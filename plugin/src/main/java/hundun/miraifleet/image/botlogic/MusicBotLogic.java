package hundun.miraifleet.image.botlogic;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.image.share.function.ImageFunction;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;

/**
 * @author hundun
 * Created on 2021/08/06
 */
public class MusicBotLogic extends BaseBotLogic {

    ImageFunction imageFunction;

    
    public MusicBotLogic(JvmPlugin plugin) {
        super(plugin, "画图");
        
        imageFunction = new ImageFunction(this, plugin, characterName);
        functions.add(imageFunction);

    }
    
}
