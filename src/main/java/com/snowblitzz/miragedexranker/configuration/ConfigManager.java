package com.snowblitzz.miragedexranker.configuration;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.snowblitzz.miragedexranker.MirageDexRanker;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static ConfigurationLoader<CommentedConfigurationNode> loader;
    public static CommentedConfigurationNode main;

    public ConfigManager() {
        this.loadConfig();
    }

    private void loadConfig() {
        Path configFile = Paths.get(MirageDexRanker.getInstance().getConfigDir() + "/DexRank.conf");
        loader = HoconConfigurationLoader.builder().setPath(configFile).build();

        try {
            if (!Files.exists(MirageDexRanker.getInstance().getConfigDir()))
                Files.createDirectory(MirageDexRanker.getInstance().getConfigDir());

            if (!Files.exists(configFile)) Files.createFile(configFile);

            if (main == null) {
                main = loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
            }

            CommentedConfigurationNode ranks = main.getNode("Ranks");
            CommentedConfigurationNode messages = main.getNode("Messages");

            ranks.getNode("UpgradeCommand").getString("lp user {player} promote dex");

            ranks.getNode("1","DexAmount").getInt(80);
            ranks.getNode("1","RankName").getString("&aCatcher");
            ranks.getNode("1","Rewards").getList(TypeToken.of(String.class), Lists.newArrayList("eco add {player} 3500","give {player} pixelmon:poke_ball 16","give {player} pixelmon:safari_ball 8","crate key silver {player} 5"));

            ranks.getNode("2","DexAmount").getInt(240);
            ranks.getNode("2","RankName").getString("&bCollector");
            ranks.getNode("2","Rewards").getList(TypeToken.of(String.class), Lists.newArrayList("eco add {player} 7000","give {player} pixelmon:great_ball 16","give {player} pixelmon:safari_ball 12","crate key silver {player} 3","crate key gold {player} 2"));

            ranks.getNode("3","DexAmount").getInt(400);
            ranks.getNode("3","RankName").getString("&cHoarder");
            ranks.getNode("3","Rewards").getList(TypeToken.of(String.class), Lists.newArrayList("eco add {player} 10500","give {player} pixelmon:ultra_ball 16","give {player} pixelmon:safari_ball 8","crate key silver {player} 2","crate key gold {player} 3"));

            ranks.getNode("4","DexAmount").getInt(560);
            ranks.getNode("4","RankName").getString("&6PokeManiac");
            ranks.getNode("4","Rewards").getList(TypeToken.of(String.class), Lists.newArrayList("eco add {player} 14000","give {player} pixelmon:master_ball 2","give {player} pixelmon:rare_candy 8","crate key gold {player} 5","crate key emerald {player} 1"));

            ranks.getNode("5","DexAmount").getInt(700);
            ranks.getNode("5","RankName").getString("&dPokeMaven");
            ranks.getNode("5","Rewards").getList(TypeToken.of(String.class), Lists.newArrayList("eco add {player} 20000","give {player} pixelmon:master_ball 2","give {player} pixelmon:rare_candy 24","crate key gold {player} 8","crate key emerald {player} 2","give {player} minecraft:elytra 1","give {player} minecraft:beacon 1"));

            messages.getNode("DexRank","NotEnough").getString("&cYou need &7{remaining} &cmore unique Pokemon to upgrade to {rank}. Use /dexcheck to see what Pokemon you're missing.");
            messages.getNode("DexRank","NeedsTrainer").getString("&4You need to be a Trainer first before you can upgrade your Pokedex rank. &c/info trainer");
            messages.getNode("DexRank","UpgradeRank").getString("&e{player} &3has caught over {amount} unique Pokemon. Rewards received & Pokedex rank upgraded to {rank}.");
            messages.getNode("DexRank","MaxRank").getString("&cYou have already attained the highest Pokedex rank of {rank}");

            messages.getNode("DexChecker","CompletePokeDex").getString("&4You have no more Pokemon to catch, well done!");

            loader.save(main);
        }catch(ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            loader.save(main);
        } catch (IOException var1) {
            var1.printStackTrace();
        }
    }

    public void reload() {
        try {
            main = loader.load();
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public int getRankSize() {
        List<String> ranks = new ArrayList<>();
        for (Object r : main.getNode("Ranks").getChildrenMap().keySet()) {
            ranks.add(r.toString());
        }

        // Have to add this as this node gets mixed in with the ranks.
        ranks.removeIf(name -> name.equalsIgnoreCase("UpgradeCommand"));

        return ranks.size();
    }

    public String getRank(int rankNumber, boolean formatted) {
        String rank = main.getNode("Ranks",String.valueOf(rankNumber),"RankName").getString();

        // Removes color codes from the string
        if (!formatted) { return rank.replaceAll("&[0-9a-f]","").toLowerCase(); }

        return rank;
    }

    public int getDexAmount(int rankNumber) {
        return main.getNode("Ranks",String.valueOf(rankNumber),"DexAmount").getInt();
    }

    public String getUpgradeCommand() { return main.getNode("Ranks","UpgradeCommand").getString(); }

    public List<String> getRewards(int rankNumber) {
        try {
            return new ArrayList<>(main.getNode("Ranks",String.valueOf(rankNumber),"Rewards").getList(TypeToken.of(String.class)));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    public String getMessages(String value) { return main.getNode((Object[])value.split("\\.")).getString(); }
}