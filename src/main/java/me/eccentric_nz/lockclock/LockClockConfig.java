package me.eccentric_nz.lockclock;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LockClockConfig {

    private final LockClock plugin;
    private FileConfiguration config = null;
    private File configFile = null;
//    HashMap<String, String> strOptions = new HashMap<String, String>();
//    HashMap<String, Integer> intOptions = new HashMap<String, Integer>();
    HashMap<String, Boolean> boolOptions = new HashMap<String, Boolean>();
    private List<String> lockables = Arrays.asList("ACACIA_DOOR", "ACACIA_FENCE_GATE", "BIRCH_DOOR", "BIRCH_FENCE_GATE", "DARK_OAK_DOOR", "DARK_OAK_FENCE_GATE", "FENCE_GATE", "GOLD_PLATE", "IRON_PLATE", "IRON_TRAPDOOR", "JUNGLE_DOOR", "JUNGLE_FENCE_GATE", "SPRUCE_DOOR", "SPRUCE_FENCE_GATE");

    public LockClockConfig(LockClock plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        boolOptions.put("uuid_conversion_done", false);
//        intOptions.put("withdraw", 5);
//        strOptions.put("firstline", "XPKeeper");
    }

    public void checkConfig() {
        int i = 0;
        // boolean values
        for (Map.Entry<String, Boolean> entry : boolOptions.entrySet()) {
            if (!config.contains(entry.getKey())) {
                plugin.getConfig().set(entry.getKey(), entry.getValue());
                i++;
            }
        }
        int a = 0;
        List<String> tmp = config.getStringList("lockables");
        for (String l : lockables) {
            if (!config.getStringList("lockables").contains(l)) {
                tmp.add(l);
                a++;
            }
        }
        if (a > 0) {
            plugin.getConfig().set("lockables", tmp);
            System.out.println("[LockClock] Added " + a + " new lockable blocks to config");
        }
//        // int values
//        for (Map.Entry<String, Integer> entry : intOptions.entrySet()) {
//            if (!config.contains(entry.getKey())) {
//                plugin.getConfig().set(entry.getKey(), entry.getValue());
//                i++;
//            }
//        }
//        // string values
//        for (Map.Entry<String, String> entry : strOptions.entrySet()) {
//            if (!config.contains(entry.getKey())) {
//                plugin.getConfig().set(entry.getKey(), entry.getValue());
//                i++;
//            }
//        }
        plugin.saveConfig();
        if (i > 0) {
            System.out.println("[LockClock] Added " + i + " new items to config");
        }
    }
}
