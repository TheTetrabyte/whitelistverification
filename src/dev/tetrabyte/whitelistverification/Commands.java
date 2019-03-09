package dev.tetrabyte.whitelistverification;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {
    private final Plugin plugin;

    public Commands(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("whitelist")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    if (sender.hasPermission("whitelist.toggle")) {
                        if (plugin.getConfig().getBoolean("whitelist")) {
                            plugin.getConfig().set("whitelist", false);
                            plugin.saveConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lSuccessfully toggled the whitelist system &b&loff"));
                        } else {
                            plugin.getConfig().set("whitelist", true);
                            plugin.saveConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lSuccessfully toggled the whitelist system &b&lon"));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou don't have permission to use this command"));
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("whitelist.reload")) {
                        plugin.reloadConfig();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lSuccessfully reloaded Whitelist Verification config"));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou don't have permission to use this command"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lIncorrect command usage\n\n&c&lUsage:\n&a&l/whitelist toggle\n&a&l/whitelist reload"));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lIncorrect command usage\n\n&c&lUsage:\n&a&l/whitelist toggle\n&a&l/whitelist reload"));
            }
        }

        return true;
    }
}
