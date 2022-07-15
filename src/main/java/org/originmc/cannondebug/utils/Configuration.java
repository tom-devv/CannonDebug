package org.originmc.cannondebug.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {

    private final JavaPlugin plugin;

    public Configuration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean alternativeTracking;

    public void loadConfiguration() {
        final FileConfiguration fileConfiguration = plugin.getConfig();
        fileConfiguration.options().copyDefaults();

        alternativeTracking = fileConfiguration.getBoolean("settings.alternative-tracking.enabled", false);
    }

    public void setBoolean(String path, boolean val) {
        final FileConfiguration fileConfiguration = plugin.getConfig();
        fileConfiguration.set(path, val);
        plugin.saveConfig();
    }

}

