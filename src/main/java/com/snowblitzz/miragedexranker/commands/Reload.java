package com.snowblitzz.miragedexranker.commands;

import com.snowblitzz.miragedexranker.Utils.ChatUtils;
import com.snowblitzz.miragedexranker.MirageDexRanker;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class Reload implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        MirageDexRanker.getInstance().getConfig().reload();

        ChatUtils.sendMessage(src, "&7MirageDexRanks config successfully reloaded.");

        return CommandResult.success();
    }
}
