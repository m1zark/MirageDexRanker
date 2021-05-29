package com.snowblitzz.miragedexranker.Utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.api.text.Text;

/**
 * Utility class to help with miscellaneous functions like sending messages and embedding colours in chat.
 * @author SnowBlitzz
 */
public class ChatUtils {

    /**
     * Sends a server-wide message if the parameter is not a blank String
     * @param message The message itself or blank if no message should be delivered
     */
    public static void sendServerWideMessage(String message) {
        if (message != null && !message.equals(""))
            Sponge.getServer().getBroadcastChannel().send(Text.of(embedColours(message)));
    }

    /**
     * Sends a message to a CommandSender if the parameter is not a blank String
     * @param src The console or the Player instance representing the player the message should be delivered to
     * @param message The message itself or blank if no message should be delivered
     */
    public static void sendMessage(CommandSource src, String message) {
        if (message != null && !message.equals(""))
            src.sendMessage(Text.of(embedColours(message)));
    }

    /**
     * Conversion method to replace the ampersand colours with the EnumTextFormatting format
     * @param str The String that needs to be operated on
     * @return the modified String
     */
    public static String embedColours(String str) {
        char curColour = '-';
        char curFormat = '-';
        for (int i = 0; i < str.length(); i++)
        {
            if (str.charAt(i) == '&')
            {
                if (str.charAt(i+1) != ' ')
                {
                    char code = str.charAt(i+1);
                    if (code == '0' || code == '1' || code == '2' || code == '3' || code == '4' || code == '5' || code == '6'
                            || code == '7' || code == '8' || code == '9' || code == 'a' || code == 'b' || code == 'c' || code == 'd'
                            || code == 'e' || code == 'f')
                    {
                        curColour = code;
                    }
                    else if (code == 'k' || code == 'l' || code == 'm' || code == 'n' || code == 'o')
                    {
                        curFormat = code;
                    }
                    else if (code == 'r')
                    {
                        curColour = '-';
                        curFormat = '-';
                    }
                }
            }
            else if (str.charAt(i) == ' ')
            {
                String charsToAdd = "";
                if (curColour != '-')
                    charsToAdd += "&" + curColour;
                if (curFormat != '-')
                    charsToAdd += "&" + curFormat;
                str = str.substring(0, i) + " " + charsToAdd + str.substring(i+1);
                i += charsToAdd.length();
            }
        }
        str = str.replaceAll("&0", TextFormatting.BLACK + "");
        str = str.replaceAll("&1", TextFormatting.DARK_BLUE + "");
        str = str.replaceAll("&2", TextFormatting.DARK_GREEN + "");
        str = str.replaceAll("&3", TextFormatting.DARK_AQUA + "");
        str = str.replaceAll("&4", TextFormatting.DARK_RED + "");
        str = str.replaceAll("&5", TextFormatting.DARK_PURPLE + "");
        str = str.replaceAll("&6", TextFormatting.GOLD + "");
        str = str.replaceAll("&7", TextFormatting.GRAY + "");
        str = str.replaceAll("&8", TextFormatting.DARK_GRAY + "");
        str = str.replaceAll("&9", TextFormatting.BLUE + "");
        str = str.replaceAll("&a", TextFormatting.GREEN + "");
        str = str.replaceAll("&b", TextFormatting.AQUA + "");
        str = str.replaceAll("&c", TextFormatting.RED + "");
        str = str.replaceAll("&d", TextFormatting.LIGHT_PURPLE + "");
        str = str.replaceAll("&e", TextFormatting.YELLOW + "");
        str = str.replaceAll("&f", TextFormatting.WHITE + "");

        str = str.replaceAll("&k", TextFormatting.OBFUSCATED + "");
        str = str.replaceAll("&l", TextFormatting.BOLD + "");
        str = str.replaceAll("&m", TextFormatting.STRIKETHROUGH + "");
        str = str.replaceAll("&n", TextFormatting.UNDERLINE + "");
        str = str.replaceAll("&o", TextFormatting.ITALIC + "");

        str = str.replaceAll("&r", TextFormatting.RESET + "");

        return str;
    }
}