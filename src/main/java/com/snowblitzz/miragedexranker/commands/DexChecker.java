package com.snowblitzz.miragedexranker.commands;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.snowblitzz.miragedexranker.MirageDexRanker;
import com.snowblitzz.miragedexranker.Utils.ChatUtils;
import com.snowblitzz.miragedexranker.ui.DexUI;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class DexChecker implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            double percent = calcPercent((EntityPlayerMP) src);
            if (percent < 100) {
                ((Player)src).openInventory((new DexUI((Player)src, 1)).getInventory());
            } else {
                ChatUtils.sendMessage(src, MirageDexRanker.getInstance().getConfig().getMessages("Messages.DexChecker.CompletePokeDex"));
            }
        } else {
            ChatUtils.sendMessage(src, "&4You need to be a player to run this command!");
        }
        return CommandResult.success();
    }

    private static double calcPercent(EntityPlayerMP entity) {
        PlayerPartyStorage party = Pixelmon.storageManager.getParty(entity);
        int caught = party.pokedex.countCaught();
        return (double) caught / (double) EnumSpecies.values().length * 100.00;
    }
}