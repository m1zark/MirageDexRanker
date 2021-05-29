package com.snowblitzz.miragedexranker.ui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.pokedex.Pokedex;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.snowblitzz.miragedexranker.Utils.ChatUtils;
import com.snowblitzz.miragedexranker.MirageDexRanker;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DexUI extends InventoryManager {
    private PlayerPartyStorage storage;
    private int page = 1;
    private int maxPage;

    public DexUI(Player p, int page) {
        super(6, Text.of(ChatUtils.embedColours("&4&lUnregistered Pok\u00E9mon")));
        this.storage = this.getPlayerStorage(p);
        this.page = page;

        int size = EnumSpecies.values().length - this.storage.pokedex.countCaught();
        this.maxPage = size % 36 == 0 && size / 36 != 0 ? size / 36 : size / 36 + 1;

        this.updatePokemon();
        this.setupInventory();
    }

    private void setupInventory() {
        int x = 0;
        for(int y = 4; x < 9; x++) {
            this.addIcon(SharedIcons.BorderIcon(x + 9 * y, DyeColors.GRAY));
        }

        InventoryIcon previousPage = SharedIcons.pageIcon(48, false, this.page, this.page > 1 ? this.page - 1 : this.maxPage);
        previousPage.addListener(ClickInventoryEvent.class, (e) -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(false);
            }).delayTicks(1L).submit(MirageDexRanker.getInstance());
        });
        this.addIcon(previousPage);

        this.addIcon(SharedIcons.infoIcon(49, this.storage));

        InventoryIcon nextPage = SharedIcons.pageIcon(50, true, this.page, this.page < this.maxPage ? this.page + 1 : 1);
        nextPage.addListener(ClickInventoryEvent.class, (e) -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(true);
            }).delayTicks(1L).submit(MirageDexRanker.getInstance());
        });
        this.addIcon(nextPage);
    }

    private List<String> setupDex() {
        List<String> contents = new ArrayList<>();

        for (EnumSpecies e : EnumSpecies.values()) {
            if(!this.storage.pokedex.hasCaught(e.getNationalPokedexInteger())) {
                contents.add(e.getPokemonName());
            }
        }

        return contents;
    }

    private void updatePokemon() {
        int index = (this.page - 1) * 36;
        int x = 0;
        for(int y = 0; y < 4 && index <= setupDex().size(); ++index) {
            if (x == 9 && y != 3) {
                x = 0;
                ++y;
            } else if (x == 9) {
                break;
            }

            if (index >= setupDex().size()) {
                this.getAllIcons().remove(x + 9 * y);
                ++x;
            } else {
                this.addIcon(new InventoryIcon(x + 9 * y, SharedIcons.pokemonIcon(setupDex().get(index))));
                ++x;
            }
        }
    }

    private void updatePage(boolean upOrDown) {
        if (upOrDown) {
            if (this.page < this.maxPage) {
                ++this.page;
            } else {
                this.page = 1;
            }
        } else if (this.page > 1) {
            --this.page;
        } else {
            this.page = this.maxPage;
        }

        // Previous Page
        InventoryIcon previousPage = SharedIcons.pageIcon(48, false, this.page, this.page > 1 ? this.page - 1 : this.maxPage);
        previousPage.addListener(ClickInventoryEvent.class, (e) -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(false);
            }).delayTicks(1L).submit(MirageDexRanker.getInstance());
        });
        this.addIcon(previousPage);

        // Next Page
        InventoryIcon nextPage = SharedIcons.pageIcon(50, true, this.page, this.page < this.maxPage ? this.page + 1 : 1);
        nextPage.addListener(ClickInventoryEvent.class, (e) -> {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                this.updatePage(true);
            }).delayTicks(1L).submit(MirageDexRanker.getInstance());
        });
        this.addIcon(nextPage);

        this.clearIcons(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35);
        this.updatePokemon();
        this.updateContents();
    }

    @Nullable private PlayerPartyStorage getPlayerStorage(Player player) {
        return Pixelmon.storageManager.getParty((EntityPlayerMP) player);
    }
}
