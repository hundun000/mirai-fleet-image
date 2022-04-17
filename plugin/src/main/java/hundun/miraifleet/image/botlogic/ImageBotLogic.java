package hundun.miraifleet.image.botlogic;

import hundun.miraifleet.framework.core.botlogic.BaseJavaBotLogic;
import hundun.miraifleet.image.share.function.ImageExperimentalFunction;
import hundun.miraifleet.image.share.function.ImageStableFunction;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;

/**
 * @author hundun
 * Created on 2021/08/06
 */
public class ImageBotLogic extends BaseJavaBotLogic {

    ImageExperimentalFunction imageExperimentalFunction;
    ImageStableFunction imageStableFunction;
    
    public ImageBotLogic(JavaPlugin plugin) {
        super(plugin, "画图");

        imageExperimentalFunction = new ImageExperimentalFunction(this, plugin, characterName);
        imageExperimentalFunction.setSkipRegisterCommand(false);
        functions.add(imageExperimentalFunction);

        imageStableFunction = new ImageStableFunction(this, plugin, characterName);
        imageStableFunction.setSkipRegisterCommand(false);
        functions.add(imageStableFunction);
    }
    
}
