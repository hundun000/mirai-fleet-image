package hundun.miraifleet.image;


import net.mamoe.mirai.console.plugin.PluginManager;
import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal;
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader;


/**
 * @author hundun
 * Created on 2021/06/03
 */
public class ImagePluginTest {
    public static void main(String[] args) throws InterruptedException {
        MiraiConsoleTerminalLoader.INSTANCE.startAsDaemon(new MiraiConsoleImplementationTerminal());

        PluginManager.INSTANCE.loadPlugin(ImagePlugin.INSTANCE);
        PluginManager.INSTANCE.enablePlugin(ImagePlugin.INSTANCE);
        
        
    }
}
