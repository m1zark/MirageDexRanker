package com.snowblitzz.miragedexranker.ui;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.snowblitzz.miragedexranker.Utils.ChatUtils;
import com.snowblitzz.miragedexranker.Utils.PokemonSpawnData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.ArrayList;

public class SharedIcons {
    public static InventoryIcon BorderIcon(int slot, DyeColor color) {
        return new InventoryIcon(slot, ItemStack.builder()
                .itemType(ItemTypes.STAINED_GLASS_PANE).quantity(1)
                .keyValue(Keys.DISPLAY_NAME, Text.of(TextColors.BLACK, ""))
                .keyValue(Keys.DYE_COLOR, color).build());
    }

    static InventoryIcon pageIcon(int slot, boolean nextOrLast, int curr, int next) {
        return new InventoryIcon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, nextOrLast ? "pixelmon:trade_holder_right" : "pixelmon:trade_holder_left").get())
                .quantity(1)
                .keyValue(Keys.DISPLAY_NAME, nextOrLast ? Text.of(TextColors.GREEN, "\u2192 ", "Next Page", TextColors.GREEN, " \u2192") : Text.of(TextColors.RED, "\u2190 ", "Previous Page", TextColors.RED, " \u2190"))
                .build());
    }

    static InventoryIcon infoIcon(int slot, PlayerPartyStorage storage) {
        String percentCaught = String.valueOf((double) storage.pokedex.countCaught() / EnumSpecies.values().length * 100.00);
        if (percentCaught.substring(percentCaught.indexOf(".") + 1).length() == 1) {
            percentCaught = percentCaught.substring(0, percentCaught.length() - 2);
        } else {
            percentCaught = percentCaught.substring(0, percentCaught.indexOf(".") + 3);
        }

        String percentSeen = String.valueOf((double) storage.pokedex.countSeen() / EnumSpecies.values().length * 100.00);
        if (percentSeen.substring(percentSeen.indexOf(".") + 1).length() == 1) {
            percentSeen = percentSeen.substring(0, percentSeen.length() - 2);
        } else {
            percentSeen = percentSeen.substring(0, percentSeen.indexOf(".") + 3);
        }

        ArrayList<Text> itemLore = new ArrayList<>();
        itemLore.add(Text.of(ChatUtils.embedColours("&bCaught: &e" + storage.pokedex.countCaught() + "&7/&e" + EnumSpecies.values().length + " &7(&d" + percentCaught + "%&7)")));
        itemLore.add(Text.of(ChatUtils.embedColours("&bSeen: &e" + storage.pokedex.countSeen() + "&7/&e" + EnumSpecies.values().length + " &7(&d" + percentSeen + "%&7)")));

        return new InventoryIcon(slot, ItemStack.builder()
                .itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_panel").get()).quantity(1)
                .keyValue(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "PokeDex Information"))
                .keyValue(Keys.ITEM_LORE, itemLore)
                .build());
    }

    static ItemStack pokemonIcon(String pokemon) {
        EnumSpecies poke = EnumSpecies.getFromNameAnyCase(pokemon);
        PokemonSpawnData data = new PokemonSpawnData(poke);
        String idValue = String.format("%03d", poke.getNationalPokedexInteger());

        ItemStack Item = ItemStackUtil.fromNative(ItemPixelmonSprite.getPhoto(Pixelmon.pokemonFactory.create(poke)));

        String rarity = "";
        if(EnumSpecies.legendaries.contains(pokemon)) { rarity = "Legendary";
        } else if(EnumSpecies.ultrabeasts.contains(pokemon)) { rarity = "Ultrabeast";
        } else if (data.getRarity() > 0) { rarity = String.valueOf(data.getRarity());
        } else if (data.getRarity() <= 0) { rarity = "Does not spawn"; }

        String time = data.getspawnTimes();
        if(data.getspawnTimes().equals("None")) time = "Any";

        ArrayList<Text> itemLore = new ArrayList<>();
        String[] newInfo = insertLinebreaks(data.getBiomes(),40).split("\n");
        itemLore.add(Text.of(ChatUtils.embedColours("&bBiomes")));
        for(String s:newInfo) itemLore.add(Text.of(ChatUtils.embedColours("&a" + s)));
        itemLore.add(Text.of(ChatUtils.embedColours("")));
        itemLore.add(Text.of(ChatUtils.embedColours("&bTimes: &a" + time)));
        itemLore.add(Text.of(ChatUtils.embedColours("&bRarity: &a" + rarity)));
        itemLore.add(Text.of(ChatUtils.embedColours("&bCatch Rate: &a" + (int)((double)poke.getBaseStats().catchRate / 255.0D * 100.0D))));

        Item.offer(Keys.DISPLAY_NAME, Text.of(ChatUtils.embedColours("&f" + idValue + " - &6" + pokemon)));
        Item.offer(Keys.ITEM_LORE, itemLore);

        return Item;
    }

    private static String insertLinebreaks(String s, int charsPerLine) {
        char[] chars = s.toCharArray();
        int lastLinebreak = 0;
        boolean wantLinebreak = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            if (wantLinebreak && chars[i] == ' ') {
                sb.append('\n');
                lastLinebreak = i;
                wantLinebreak = false;
            } else {
                sb.append(chars[i]);
            }
            if (i - lastLinebreak + 1 == charsPerLine)
                wantLinebreak = true;
        }
        return sb.toString();
    }
}
