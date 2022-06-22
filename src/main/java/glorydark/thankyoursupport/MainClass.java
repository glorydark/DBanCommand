package glorydark.thankyoursupport;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainClass extends PluginBase implements Listener {

    public static List<String> bancommands = new ArrayList<>();
    public static HashMap<String, Object> worldBannedCommands = new HashMap<>();


    @Override
    public void onEnable() {
        this.saveResource("bancommands.yml",false);
        this.getServer().getPluginManager().registerEvents(this, this);
        Config config = new Config(this.getDataFolder()+"/bancommands.yml",Config.YAML);
        updateConfig(config);
        bancommands = config.getStringList("全局禁用指令");
        worldBannedCommands = config.get("单世界禁用指令", new HashMap<>());
    }

    @EventHandler
    public void CommandProcess(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if(player == null){return;}
        if(player.isOp()){ return; }
        String command = event.getMessage().toLowerCase();
        if(isWorldBannedCommand(player, command)){
            event.setCancelled(true);
        }
        if(isGlobalBannedCommand(player, command)){
            event.setCancelled(true);
        }
    }

    public void updateConfig(Config config){
        if(config.exists("bancommands")){
            config.set("全局禁用指令", config.getStringList("bancommands"));
            config.set("单世界禁用指令", new ArrayList<>());
            config.remove("bancommands");
            config.save();
            this.getLogger().info("您的配置文件已更新！");
        }else{
            this.getLogger().info("您的配置文件已为最新！");
        }
    }

    public Boolean isWorldBannedCommand(Player player, String command){
        Object object =  worldBannedCommands.get(player.getLevel().getName());
        if(object != null) {
            List<String> strings = (List<String>) object;
            if(strings.size() < 1){return false;}
            String[] commandSplits = command.split(" ");
            for(String verifyString: strings){
                verifyString = "/"+verifyString;
                String[] verifyStrings = verifyString.split(" ");
                if(verifyStrings.length > 0){
                    if(verifyStrings.length > commandSplits.length){ continue; }
                    boolean state = true;
                    for(int i=0; i<verifyStrings.length; i++){
                        if (!verifyStrings[i].equals(commandSplits[i])) {
                            state = false;
                            break;
                        }
                    }
                    if(state){
                        player.sendMessage(TextFormat.RED+ "[DBanCommands] 该指令被禁用");
                        return true;
                    }
                }else{
                    if(command.equals(verifyString)){
                        player.sendMessage(TextFormat.RED+ "[DBanCommands] 该指令被禁用");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean isGlobalBannedCommand(Player player, String command){
        if(bancommands.size()<1){return false;}
        String[] commandSplits = command.split(" ");
        for(String verifyString: bancommands){
            verifyString = "/"+verifyString;
            String[] verifyStrings = verifyString.split(" ");
            if(verifyStrings.length > 0){
                if(verifyStrings.length > commandSplits.length){ continue; }
                boolean state = true;
                for(int i=0; i<verifyStrings.length; i++){
                    if (!verifyStrings[i].equals(commandSplits[i])) {
                        state = false;
                        break;
                    }
                }
                if(state){
                    player.sendMessage(TextFormat.RED+ "[DBanCommands] 该指令被禁用");
                    return true;
                }
            }else{
                if(command.equals(verifyString)){
                    player.sendMessage(TextFormat.RED+ "[DBanCommands] 该指令被禁用");
                    return true;
                }
            }
        }
        return false;
    }
}
