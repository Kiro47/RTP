package com.chosencraft.www.rtp;

import java.io.File;
        import java.io.IOException;

import com.chosencraft.www.rtp.Commands.RandomTeleport;
import org.bukkit.Bukkit;
        import org.bukkit.plugin.Plugin;
        import org.bukkit.plugin.java.JavaPlugin;

public class RandomTeleportMain extends JavaPlugin{

    // Add in config option to send to only pre generated chunks.
    // learn to hook into world border.

    public void onEnable() {
        File data = new File(getThisPlugin().getDataFolder(), "data.yml");
        saveDefaultConfig();
        if (!data.exists()){
            try {
                data.createNewFile();
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        getCommand("randomteleport").setExecutor(new RandomTeleport(this));

    }
    public static Plugin getThisPlugin(){
        return Bukkit.getPluginManager().getPlugin("RandomTeleporter");
    }

    public static File getData(){
        return new File(getThisPlugin().getDataFolder(), "data.yml");
    }
}
