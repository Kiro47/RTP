package com.chosencraft.www.rtp.utils;

import java.io.File;
import java.util.Set;

import com.chosencraft.www.rtp.RandomTeleportMain;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {



    private static final SettingsManager
            data = new SettingsManager("data");

    public static SettingsManager getData() {
        return data;
    }


    private File file;
    private FileConfiguration config;

    private SettingsManager(String fileName) {
        if (! RandomTeleportMain.getThisPlugin().getDataFolder().exists()) {
            RandomTeleportMain.getThisPlugin().getDataFolder().mkdir();
        }

        file = new File(RandomTeleportMain.getThisPlugin().getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String path) {
        return (T) config.get(path);
    }

    public Set<String> getKeys() {
        return config.getKeys(false);
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public ConfigurationSection createSection(String path) {
        ConfigurationSection section = config.createSection(path);
        save();
        return section;
    }

    public void save() {
        try {
            config.save(file);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
