package hundun.miraifleet.image.botlogic;

import hundun.miraifleet.framework.core.botlogic.BaseJavaBotLogic;
import hundun.miraifleet.image.share.function.ImageExperimentalFunction;
import hundun.miraifleet.image.share.function.ImageStableFunction;
import hundun.miraifleet.image.share.function.SharedPetFunction;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;

/**
 * @author hundun
 * Created on 2021/08/06
 */
public class ImageBotLogic extends BaseJavaBotLogic {


    public ImageBotLogic(JavaPlugin plugin) {
        super(plugin, "画图");

        var sharedPetFunction = new SharedPetFunction(this, plugin, characterName);

        var imageExperimentalFunction = new ImageExperimentalFunction(this, plugin, characterName);
        imageExperimentalFunction.setSkipRegisterCommand(false);
        imageExperimentalFunction.lazyInitSharedFunction(sharedPetFunction);
        registerFunction(imageExperimentalFunction);

        var imageStableFunction = new ImageStableFunction(this, plugin, characterName);
        imageStableFunction.setSkipRegisterCommand(false);
        imageStableFunction.lazyInitSharedFunction(sharedPetFunction);
        registerFunction(imageStableFunction);
    }
    
}
