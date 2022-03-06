package hundun.miraifleet.image.share.function;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.BaseFunction.AbstractCompositeCommandFunctionComponent;
import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.Command;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.permission.PermissionService;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;
import net.mamoe.mirai.utils.ExternalResource;


public class ImageFunction extends BaseFunction<Void>{

    @Getter
    private final CompositeCommandFunctionComponent commandComponent;
    
    ImageCoreKt imageCoreKt = new ImageCoreKt();
    
    public ImageFunction(
            BaseBotLogic baseBotLogic,
            JvmPlugin plugin,
            String characterName
            ) {
        super(
            baseBotLogic,
            plugin,
            characterName,
            "ImageFunction",
            false,
            null
            );
        this.commandComponent = new CompositeCommandFunctionComponent(plugin, characterName, functionName);
        
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
        public void help(CommandSender sender, String leftText, String rightText) {
            if (!checkCosPermission(sender)) {
                return;
            }
            
            ExternalResource image = imageCoreKt.ph(leftText, rightText);
            
        }
    }

}
