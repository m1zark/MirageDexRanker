package com.snowblitzz.miragedexranker;

import com.google.inject.Inject;
import com.snowblitzz.miragedexranker.commands.DexChecker;
import com.snowblitzz.miragedexranker.commands.DexRankUp;
import com.snowblitzz.miragedexranker.commands.Reload;
import com.snowblitzz.miragedexranker.configuration.ConfigManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;
import org.slf4j.Logger;

@Plugin(
        id = "miragedexranker", name = "MirageDexRanker", description = "MirageCraft's Pokedex ranking plugin.",
        authors = {"SnowBlitzz", "m1zark"}
)
public class MirageDexRanker {
    @Inject Game game;
    @Inject private Logger logger;
    private static MirageDexRanker instance;

    @Inject @ConfigDir(sharedRoot = false) private Path configDir;
    private ConfigManager config;

    @Listener public void onInitialization(GameInitializationEvent e) {
        instance = this;

        this.config = new ConfigManager();

        CommandSpec reload = CommandSpec.builder()
                .description(Text.of("Reload DexRank config."))
                .permission("miragedexranker.command.reload")
                .executor(new Reload()).build();

        CommandSpec dexcheck = CommandSpec.builder()
                .description(Text.of("Check remaining Pokemon."))
                .permission("miragedexranker.command.dexcheck")
                .executor(new DexChecker()).build();

        CommandSpec dexrankup = CommandSpec.builder()
                .description(Text.of("Upgrades Pokedex rank."))
                .permission("miragedexranker.command.dexrank")
                .child(reload, "reload")
                .executor(new DexRankUp()).build();

        Sponge.getCommandManager().register(this, dexrankup, "dexrankup");
        Sponge.getCommandManager().register(this, dexcheck, "dexcheck");
    }

    @Listener public void onReload(GameReloadEvent e) {
        this.config = new ConfigManager();
        this.getLogger().info(Text.of(TextColors.YELLOW, "Market configurations have been reloaded").toPlain());
    }

    public static MirageDexRanker getInstance() {
        return instance;
    }

    public ConfigManager getConfig() {
        return this.config;
    }

    public Path getConfigDir() {
        return this.configDir;
    }

    public Logger getLogger(){
        return this.logger;
    }
}
