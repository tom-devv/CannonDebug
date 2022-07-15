package org.originmc.cannondebug.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.originmc.cannondebug.CannonDebugPlugin;

import java.util.List;

public class FormatUtils {

    private static final CannonDebugPlugin plugin = CannonDebugPlugin.getInstance();


    public static String format(String s){
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }

    public static String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.prefix"));

    //Credits: SYS
    public static void sendMessage(CommandSender sender, List<String> messageList) {
        for (String message : messageList)
            if (!message.isEmpty())
                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
    }
    //Credits: SYS
    public static List<String> replaceList(List<String> ls, String initial, String change){
        for (int i = 0; i < ls.size(); i++)
            ls.set(i, ls.get(i).replace(initial, change));
        return ls;
    }

}
