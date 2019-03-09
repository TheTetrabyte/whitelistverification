package dev.tetrabyte.whitelistverification;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Plugin plugin;
    FileConfiguration config;

    @Override
    public void onEnable() {
        // Log that the plugin was enabled
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2+------&r &aWhitelist Verification " + getDescription().getVersion() + "&r &2------+&r"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2|&r       &6Plugin Successfully Enabled&r        &2|&r"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2|&r          &bCreated by: Tetrabyte&r           &2|&r"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2+------------------------------------------+&r"));

        // Save default config.yml
        saveDefaultConfig();

        // Assign this to plugin value
        plugin = this;

        // Load the config into memory
        config = getConfig();

        // Save the config
        saveConfig();

        // Register the commands
        this.getCommand("wl").setExecutor(new Commands(this));

        // Register the events
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        // Log that the plugin was disabled
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2+------&r &aWhitelist Verification " + getDescription().getVersion() + "&r &2------+&r"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2|&r       &cPlugin Successfully Disabled&r       &2|&r"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2|&r          &bCreated by: Tetrabyte&r           &2|&r"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&2+------------------------------------------+&r"));

        plugin = null;
    }
}
