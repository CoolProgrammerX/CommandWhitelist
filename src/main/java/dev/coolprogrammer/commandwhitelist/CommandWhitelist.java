package dev.coolprogrammer.commandwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CommandWhitelist extends JavaPlugin implements CommandExecutor, Listener {

    public static List<String> allowedCommands = new ArrayList<>();
    public static String unknownCommandMessage;
    public static CommandWhitelist instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            loadConfig();
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginCommand("cmdwhitelist").setExecutor(this);
        Utility.logToConsole("&e[&bCommandWhitelist&e] &bPlugin enabled.");
    }

    @Override
    public void onDisable() {
        Utility.logToConsole("&e[&bCommandWhitelist&e] &bPlugin disabled.");
    }

    public void loadConfig() throws IOException, InvalidConfigurationException {
        reloadConfig();
        allowedCommands = this.getConfig().getStringList("whitelisted-commands");
        YamlConfiguration spigotYamlConfig = new YamlConfiguration();
        spigotYamlConfig.load(
                new File(getServer().getWorldContainer().getAbsolutePath() + File.separator + "spigot.yml")
        );
        unknownCommandMessage = spigotYamlConfig.getString("messages.unknown-command");
    }



    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player sender = event.getPlayer();
        String[] message = event.getMessage().split(" ");
        String command = message[0].substring(1);
        if (sender.hasPermission("server.cmdblocker.bypass")) return;
        if (allowedCommands.contains(command)) return;
        event.setCancelled(true);
        sender.sendMessage(unknownCommandMessage);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                if (args[0].equalsIgnoreCase("add")) {
                    allowedCommands.add(args[1]);
                    Utility.setInConfig("whitelisted-commands", allowedCommands);
                    Utility.sendMessage(player, "&aSuccessfully added &e\"" + args[1] + "\" &ato command whitelist.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    allowedCommands.remove(args[1]);
                    Utility.setInConfig("whitelisted-commands", allowedCommands);
                    Utility.sendMessage(player, "&aSuccessfully removed &e\"" + args[1] + "\" &afrom command whitelist.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    loadConfig();
                    Utility.sendMessage(player, "&aFile &econfig.yml &areloaded successfully");
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException | NullPointerException | IOException | InvalidConfigurationException exception) {
                Utility.sendMessage(player, "&cSomething went wrong when you executed &e" + label + "&c.");
                Utility.sendMessage(player, "&cPlease check your arguments.");
                Utility.sendMessage(player, "&e/" + label + " add (command without /)");
                Utility.sendMessage(player, "&e/" + label + " remove (command without /)");
                Utility.sendMessage(player, "&e/" + label + " reload");
                return true;
            }
            return true;
        }
        try {
            if (args[0].equalsIgnoreCase("add")) {
                allowedCommands.add(args[1]);
                Utility.setInConfig("whitelisted-commands", allowedCommands);
                Utility.logToConsole("&aSuccessfully added &e\"" + args[1] + "\" &ato command whitelist.");
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                allowedCommands.remove(args[1]);
                Utility.setInConfig("whitelisted-commands", allowedCommands);
                Utility.logToConsole("&aSuccessfully removed &e\"" + args[1] + "\" &afrom command whitelist.");
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                loadConfig();
                Utility.logToConsole("&aFile &econfig.yml &areloaded successfully");
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | InvalidConfigurationException | IOException exception) {
            Utility.logToConsole("&cSomething went wrong when you executed &e" + label + "&c.");
            Utility.logToConsole("&cPlease check your arguments.");
            Utility.logToConsole("&e/" + label + " add (command without /)");
            Utility.logToConsole("&e/" + label + " remove (command without /)");
            Utility.logToConsole("&e/" + label + " reload");
            return true;
        }
        return true;
    }

    public static CommandWhitelist getInstance() {
        return instance;
    }

}
