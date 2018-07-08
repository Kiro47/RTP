package com.chosencraft.www.rtp;

import com.chosencraft.www.rtp.Commands.RandomTeleport;

import com.chosencraft.www.rtp.utils.Configurations;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomTeleportMain extends JavaPlugin{

    public Configurations configurations;
    public static Economy economy;

    public void onEnable() {

        registerCommands();
        registerEconomy();
        this.configurations = new Configurations(this);

    }

    /**
     * Registers commands for the plugin
     */
    private void registerCommands()
    {
        getCommand("randomteleport").setExecutor(new RandomTeleport(this));
    }

    /**
     * Registers vault economy hook
     */
    private void registerEconomy()
    {
        RegisteredServiceProvider<Economy> serviceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        this.economy = serviceProvider.getProvider();
    }

}
