package com.chosencraft.www.rtp;

import com.chosencraft.www.rtp.Commands.RandomTeleport;

import com.chosencraft.www.rtp.utils.Configurations;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomTeleportMain extends JavaPlugin{

    public Configurations configurations;

    public void onEnable() {

        registerCommands();
        this.configurations = new Configurations(this);

    }

    /**
     * Registers commands for the plugin
     */
    private void registerCommands()
    {
        getCommand("randomteleport").setExecutor(new RandomTeleport(this));
    }
}
