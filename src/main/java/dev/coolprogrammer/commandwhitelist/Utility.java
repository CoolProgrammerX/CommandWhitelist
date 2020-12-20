package dev.coolprogrammer.commandwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utility {

    public static String formatString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(formatString(message));
    }

    public static void logToConsole(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(formatString(message));
    }

    public static void setInConfig(String path, Object value) {
        CommandWhitelist.getInstance().getConfig().set(path, value);
        CommandWhitelist.getInstance().saveConfig();
    }

}
