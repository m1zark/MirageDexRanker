package com.snowblitzz.miragedexranker.Utils;

import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.SpawnSet;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon;
import com.pixelmonmod.pixelmon.api.spawning.util.SetLoader;
import com.pixelmonmod.pixelmon.api.world.WorldTime;

import java.lang.reflect.Field;
import java.util.*;

import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.text.WordUtils;

public class PokemonSpawnData {
    private EnumSpecies pokemon;
    private ArrayList<String> biomeNames = new ArrayList<>();
    private ArrayList<String> spawnTimes = new ArrayList<>();
    private int rarity = -1;

    public PokemonSpawnData(EnumSpecies pokemon) {
        this.pokemon = pokemon;

        setBiomeData();
        setTimeData();
        setRarityData();
    }

    private void setBiomeData() {
        ArrayList<Biome> allBiomes = new ArrayList<>();

        for (Object set : SetLoader.getAllSets()) {
            for (SpawnInfo info : ((SpawnSet)set).spawnInfos) {
                if (!(info instanceof SpawnInfoPokemon) || !((SpawnInfoPokemon)info).getPokemonSpec().name.equalsIgnoreCase(this.pokemon.name)) continue;

                allBiomes.addAll(info.condition.biomes);

                if (info.anticondition != null && info.anticondition.biomes != null && !info.anticondition.biomes.isEmpty()) {
                    allBiomes.removeIf(biome -> info.anticondition.biomes.contains(biome));
                }

                if (info.compositeCondition == null) continue;

                if (info.compositeCondition.conditions != null) {
                    info.compositeCondition.conditions.forEach(condition -> {
                        if (condition.biomes != null && !condition.biomes.isEmpty()) {
                            allBiomes.removeIf(biome -> !condition.biomes.contains(biome));
                        }
                    });
                }

                if (info.compositeCondition.anticonditions == null) continue;

                info.compositeCondition.anticonditions.forEach(anticondition -> {
                    if (anticondition.biomes != null && anticondition.biomes.isEmpty()) {
                        allBiomes.removeIf(biome -> anticondition.biomes.contains(biome));
                    }
                });
            }
        }

        for (Biome biome : allBiomes) {
            this.biomeNames.add(WordUtils.capitalizeFully(getBiomeName(biome)));

            this.biomeNames.replaceAll(b -> b.replace("Foresthills","Forest Hills"));
        }

        if (!biomeNames.isEmpty()) Collections.sort(this.biomeNames);
    }

    private void setTimeData() {
        for (Object sets : SetLoader.getAllSets()) {
            for (SpawnInfo info : ((SpawnSet) sets).spawnInfos) {
                if (!(info instanceof SpawnInfoPokemon) || !((SpawnInfoPokemon)info).getPokemonSpec().name.equalsIgnoreCase(this.pokemon.getPokemonName())) continue;
                ArrayList times = info.condition.times;

                try {
                    if (times != null && times.size() >= 1) {
                        for (Object time : times) {
                            if(time instanceof WorldTime) this.spawnTimes.add(((WorldTime) time).name());
                        }
                    }
                } catch(NullPointerException e) {
                    System.out.print("Error getting time data for: " + this.pokemon.getPokemonName());
                }
            }
        }
    }

    private void setRarityData() {
        for (Object set : SetLoader.getAllSets()) {
            for (SpawnInfo info : ((SpawnSet) set).spawnInfos) {
                if (!(info instanceof SpawnInfoPokemon) || !((SpawnInfoPokemon)info).getPokemonSpec().name.equalsIgnoreCase(this.pokemon.getPokemonName())) continue;
                this.rarity = (int) info.rarity;
            }
        }
    }

    public String getBiomes() {
        return asReadableList(this.biomeNames);
    }

    public String getspawnTimes() {
        return asReadableList(this.spawnTimes);
    }

    public int getRarity() {
        return this.rarity;
    }

    private static String asReadableList(ArrayList<String> data) {
        String separator = ", ";
        String list = "";
        for (Object s : data) {
            if (s != null) {
                if (s.equals(data.get(0))) {
                    list = s.toString();
                } else {
                    list = list.concat(separator + s);
                }
            }
        }
        return list.isEmpty() ? "None" : list;
    }

    private static String getBiomeName(Biome biome) {
        String name = "";
        try {
            Field f = ReflectionHelper.findField(Biome.class, "biomeName", "field_185412_a", "field_76791_y");
            name = (String) f.get(biome);
        } catch (Exception e) {
            return "Error getting biome name";
        }

        return name;
    }
}
