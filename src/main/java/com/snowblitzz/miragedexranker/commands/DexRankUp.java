package com.snowblitzz.miragedexranker.commands;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.snowblitzz.miragedexranker.MirageDexRanker;
import com.snowblitzz.miragedexranker.Utils.ChatUtils;
import com.snowblitzz.miragedexranker.configuration.ConfigManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class DexRankUp implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ConfigManager config = MirageDexRanker.getInstance().getConfig();
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty((EntityPlayerMP) src);
        if (src instanceof Player) {
            if(src.hasPermission("miragedexranker.check.trainer")) {
                int dexCount = storage.pokedex.countCaught();
                int ranksSize = config.getRankSize();

                if(src.hasPermission("miragedexranker.check." + config.getRank(ranksSize,false))) {
                    ChatUtils.sendMessage(src, config.getMessages("Messages.DexRank.MaxRank").replace("{rank}", config.getRank(ranksSize,true)));
                } else {
                    for (int i = 1; i <= ranksSize; i++) {
                        if (!src.hasPermission("miragedexranker.check." + config.getRank(i,false))) {
                            int dexAmount = config.getDexAmount(i);

                            if (dexCount >= dexAmount) {
                                List<String> rewards = config.getRewards(i);
                                rewards.forEach(reward -> runConsoleCommand(reward.replace("{player}", src.getName())));

                                runConsoleCommand(config.getUpgradeCommand().replace("{player}", src.getName()));

                                ChatUtils.sendServerWideMessage(config.getMessages("Messages.DexRank.UpgradeRank")
                                        .replace("{player}", src.getName())
                                        .replace("{amount}", String.valueOf(dexAmount))
                                        .replace("{rank}", config.getRank(i,true)));
                            } else {
                                int remaining = dexAmount - dexCount;
                                ChatUtils.sendMessage(src, config.getMessages("Messages.DexRank.NotEnough")
                                        .replace("{remaining}", String.valueOf(remaining))
                                        .replace("{rank}", config.getRank(i,true)));
                                break;
                            }
                        }
                    }
                }
            } else {
                ChatUtils.sendMessage(src, config.getMessages("Messages.DexRank.NeedsTrainer"));
            }

            return CommandResult.success();
        }
        return CommandResult.empty();
    }

    private static void runConsoleCommand(String cmd) {
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
    }
}

