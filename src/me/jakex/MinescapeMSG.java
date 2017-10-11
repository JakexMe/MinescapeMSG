//Name: MinescapeMSG
//Author: Jacob Boyer
//Purpose: Custom Messaging plugin to be used on all Minescape servers
//Date Started: 10-4-17
//Last Modified: 10-5-17

package me.jakex;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MinescapeMSG extends JavaPlugin implements Listener
{
  public MinescapeMSG() {}
  
  
  // Writes config file defaults
  public void onEnable()
  {
    Bukkit.getPluginManager().registerEvents(this, this);
    getConfig().addDefault("format.send", "§5§l(sender) >(target)§d§l(message)");
    getConfig().addDefault("format.recieve", "§5§l(sender) >(target)§d§l(message)");
    getConfig().addDefault("sender", "NOTE_PIANO");
    getConfig().addDefault("reciever", "NOTE_PIANO");
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
  
  public String buildConfigMessage(String s, Player target, Player sender, String message)
  {
    if (s.contains("(target")) {
      s = s.replace("(target)", target.getName());
    }
    if (s.contains("(sender")) {
      s = s.replace("(sender)", sender.getName());
    }
    if (s.contains("(message")) {
      s = s.replace("(message)", message);
    }
    return s;
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    getConfig().set(event.getPlayer().getUniqueId() + ".lastPlayer", "");
    saveConfig();
  }
  
  public String buildMessage(String[] args)
  {
    String message = "";
    for (int i = 1; i < args.length; i++) {
      message = message + " " + args[i];
    }
    return message;
  }
  
  public String buildReplyMessage(String[] args)
  {
    String message = "";
    for (int i = 0; i < args.length; i++) {
      message = message + " " + args[i];
    }
    return message;
  }
  
  // Sound for sending player
  public void sendSoundP(Player player, String s)
  {
    Sound[] arrayOfSound;
    int j = (arrayOfSound = Sound.values()).length;
    for (int i = 0; i < j; i++)
    {
      Sound sounValue = arrayOfSound[i];
      if (sounValue.toString().equals(s))
      {
        Sound sound = Sound.valueOf(s);
        player.getWorld().playSound(player.getLocation(), sound, 1.0F, 1.0F);
        break;
      }
    }
  }
  
  // Sound for target player
  public void sendSoundT(Player player, String s)
  {
    Sound[] arrayOfSound;
    int j = (arrayOfSound = Sound.values()).length;
    for (int i = 0; i < j; i++)
    {
      Sound sounValue = arrayOfSound[i];
      if (sounValue.toString().equals(s))
      {
        Sound sound = Sound.valueOf(s);
        player.getWorld().playSound(player.getLocation(), sound, 2.0F, 2.0F);
        break;
      }
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player))
    {
      Player player = (Player)sender;
      if (cmd.getName().equalsIgnoreCase("message"))
      {
        if (args.length > 1)
        {
          Player target = Bukkit.getServer().getPlayer(args[0]);
          if (target != null)
          {
            String message = buildMessage(args);
            String senderM = buildConfigMessage(getConfig().getString("format.send"), target, player, message);
            String targetM = buildConfigMessage(getConfig().getString("format.recieve"), target, player, message);
            player.sendMessage(senderM);
            target.sendMessage(targetM);
            getConfig().set(player.getUniqueId() + ".lastPlayer", target.getName());
            getConfig().set(target.getUniqueId() + ".lastPlayer", player.getName());
            sendSoundT(target, getConfig().getString("reciever"));
            sendSoundP(player, getConfig().getString("sender"));
            
            // This is for the famous people
            if ((target.getName().equals("354")) || (target.getName().equals("FearMe")) || (target.getName().equals("PPuddin")))
            {
              player.sendMessage(ChatColor.YELLOW + target.getName() + " is often AFK or minimized, due to plugin development.");
              player.sendMessage(ChatColor.YELLOW + "Please be patient if he does not reply instantly.");
            }
            return true;
          }
          player.sendMessage("ERROR: The player " + args[0] + " does not exist or is not online!");
          return true;
        }
        player.sendMessage(ChatColor.YELLOW + "USAGE: /msg <Player> <Message>");
        return true;
      }
      if (cmd.getName().equalsIgnoreCase("reply")) {
        if (args.length > 0)
        {
          Player target = Bukkit.getServer().getPlayer(getConfig().getString(player.getUniqueId() + ".lastPlayer"));
          if (target != null)
          {
            String message = buildReplyMessage(args);
            String senderM = buildConfigMessage(getConfig().getString("format.send"), target, player, message);
            String targetM = buildConfigMessage(getConfig().getString("format.recieve"), target, player, message);
            player.sendMessage(senderM);
            target.sendMessage(targetM);
            getConfig().set(player.getUniqueId() + ".lastPlayer", target.getName());
            getConfig().set(target.getUniqueId() + ".lastPlayer", player.getName());
            sendSoundT(target, getConfig().getString("reciever"));
            sendSoundP(player, getConfig().getString("sender"));
            return true;
          }
        }
        else
        {
          player.sendMessage(ChatColor.YELLOW + "USAGE: /r <Message>");
          return true;
        }
      }
    }
    return true;
  }
}
