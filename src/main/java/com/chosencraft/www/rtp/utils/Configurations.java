package com.chosencraft.www.rtp.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Configurations
{

    // limits
    public static int maximumXRadius;
    public static int maximumZRadius;

    // costs
    public static boolean chargeForTeleport;
    public static double costPerTeleport;

    // cooldown
    public static int amountOfTeleports;
    public static int cooldownTimeInSeconds;

    public static boolean askBeforeTeleporting;
    /**
     * Default constructor
     * @param plugin The Random teleport plugin
     */
    public Configurations(Plugin plugin)
    {
        // save config if it doesn't exist
        plugin.saveDefaultConfig();
        reload(plugin);
    }


    /**
     * Reloads the activate configurations for the plugin
     * @param plugin The Random Teleport plugin
     */
    public void reload(Plugin plugin)
    {
        FileConfiguration config = plugin.getConfig();
        this.maximumXRadius = config.getInt("MaximumXRadius");
        this.maximumZRadius = config.getInt("MaximumZRadius");
        this.chargeForTeleport = config.getBoolean("cost");
        this.costPerTeleport = config.getDouble("price");
        this.amountOfTeleports = config.getInt("Cooldown.amount");
        this.cooldownTimeInSeconds = config.getInt("Cooldown.time");
        this.askBeforeTeleporting = config.getBoolean("askBeforeTeleporting");
    }

    /**
     * Checks if the maximum radiuses are within the world border,
     * if not it overrides them to the world border limit.
     */
    private void checkWorldBorders()
    {
        //TODO: unhardcode from main world
        World world = Bukkit.getServer().getWorlds().get(0);
        WorldBorder border = world.getWorldBorder();
        //TODO: add support for radial borders

        // check x
        if (!border.isInside(new Location(world, maximumXRadius , 256 , 0 )))
        {
            this.maximumXRadius = (int) Math.floor(border.getSize());
        }

        // check z
        if (!border.isInside(new Location(world, 0 , 256 , maximumZRadius )))
        {
            this.maximumZRadius = (int) Math.floor(border.getSize());
        }
    }
}
