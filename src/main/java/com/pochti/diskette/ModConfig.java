package com.pochti.diskette;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue RESPAWNS_PER_DISKETTE = BUILDER
            .comment("Number of respawns one diskette provides")
            .defineInRange("respawnsPerDiskette", 3, 1, 100);

    public static final ModConfigSpec.IntValue MAX_TOTAL_RESPAWNS = BUILDER
            .comment("Maximum total respawns that can be accumulated")
            .defineInRange("maxTotalRespawns", 99, 1, 1000);

    // Сейвскам
    public static final ModConfigSpec.BooleanValue ENABLE_SAVE_SCUM = BUILDER
            .comment("Allow saving diskettes inside save points (enables save scumming/doping)")
            .define("enableSaveScum", true);

    // === Источники добычи ===

    // Древний город
    public static final ModConfigSpec.BooleanValue ENABLE_ANCIENT_CITY = BUILDER
            .comment("Enable diskette loot in ancient city chests")
            .define("enableAncientCity", true);
    public static final ModConfigSpec.DoubleValue ANCIENT_CITY_CHANCE = BUILDER
            .comment("Chance to find diskette in ancient city chest (0.0 - 1.0)")
            .defineInRange("ancientCityChance", 0.15, 0.0, 1.0);

    // Крепость
    public static final ModConfigSpec.BooleanValue ENABLE_STRONGHOLD = BUILDER
            .comment("Enable diskette loot in stronghold chests")
            .define("enableStronghold", true);
    public static final ModConfigSpec.DoubleValue STRONGHOLD_CHANCE = BUILDER
            .comment("Chance to find diskette in stronghold chest (0.0 - 1.0)")
            .defineInRange("strongholdChance", 0.10, 0.0, 1.0);

    // Сниффер
    public static final ModConfigSpec.BooleanValue ENABLE_SNIFFER = BUILDER
            .comment("Enable diskette as sniffer digging result")
            .define("enableSniffer", true);
    public static final ModConfigSpec.DoubleValue SNIFFER_CHANCE = BUILDER
            .comment("Chance for sniffer to dig out a diskette (0.0 - 1.0)")
            .defineInRange("snifferChance", 0.05, 0.0, 1.0);

    // Археология
    public static final ModConfigSpec.BooleanValue ENABLE_ARCHAEOLOGY = BUILDER
            .comment("Enable diskette as archaeology loot")
            .define("enableArchaeology", true);
    public static final ModConfigSpec.DoubleValue ARCHAEOLOGY_CHANCE = BUILDER
            .comment("Chance to find diskette in archaeological sites (0.0 - 1.0)")
            .defineInRange("archaeologyChance", 0.07, 0.0, 1.0);

    // Trial Chambers: обычный Vault
    public static final ModConfigSpec.BooleanValue ENABLE_TRIAL_VAULT = BUILDER
            .comment("Enable diskette in regular Trial Chambers Vault")
            .define("enableTrialVault", true);
    public static final ModConfigSpec.DoubleValue TRIAL_VAULT_CHANCE = BUILDER
            .comment("Chance to find diskette in a regular Vault (0.0 - 1.0)")
            .defineInRange("trialVaultChance", 0.05, 0.0, 1.0);

    // Trial Chambers: Ominous Vault
    public static final ModConfigSpec.BooleanValue ENABLE_OMINOUS_VAULT = BUILDER
            .comment("Enable diskette in Ominous Trial Chambers Vault")
            .define("enableOminousVault", true);
    public static final ModConfigSpec.DoubleValue OMINOUS_VAULT_CHANCE = BUILDER
            .comment("Chance to find diskette in an Ominous Vault (0.0 - 1.0)")
            .defineInRange("ominousVaultChance", 0.15, 0.0, 1.0);

    // Закопанные сокровища
    public static final ModConfigSpec.BooleanValue ENABLE_BURIED_TREASURE = BUILDER
            .comment("Enable diskette in buried treasure chests")
            .define("enableBuriedTreasure", true);
    public static final ModConfigSpec.DoubleValue BURIED_TREASURE_CHANCE = BUILDER
            .comment("Chance to find diskette in buried treasure (0.0 - 1.0)")
            .defineInRange("buriedTreasureChance", 0.10, 0.0, 1.0);

    // Пустынная пирамида
    public static final ModConfigSpec.BooleanValue ENABLE_DESERT_PYRAMID = BUILDER
            .comment("Enable diskette in desert pyramid chests")
            .define("enableDesertPyramid", true);
    public static final ModConfigSpec.DoubleValue DESERT_PYRAMID_CHANCE = BUILDER
            .comment("Chance to find diskette in desert pyramid (0.0 - 1.0)")
            .defineInRange("desertPyramidChance", 0.08, 0.0, 1.0);

    // Храм в джунглях
    public static final ModConfigSpec.BooleanValue ENABLE_JUNGLE_TEMPLE = BUILDER
            .comment("Enable diskette in jungle temple chests")
            .define("enableJungleTemple", true);
    public static final ModConfigSpec.DoubleValue JUNGLE_TEMPLE_CHANCE = BUILDER
            .comment("Chance to find diskette in jungle temple (0.0 - 1.0)")
            .defineInRange("jungleTempleChance", 0.08, 0.0, 1.0);

    // Простое подземелье
    public static final ModConfigSpec.BooleanValue ENABLE_SIMPLE_DUNGEON = BUILDER
            .comment("Enable diskette in simple dungeon chests")
            .define("enableSimpleDungeon", true);
    public static final ModConfigSpec.DoubleValue SIMPLE_DUNGEON_CHANCE = BUILDER
            .comment("Chance to find diskette in simple dungeon (0.0 - 1.0)")
            .defineInRange("simpleDungeonChance", 0.03, 0.0, 1.0);

    // Заброшенная шахта
    public static final ModConfigSpec.BooleanValue ENABLE_ABANDONED_MINESHAFT = BUILDER
            .comment("Enable diskette in abandoned mineshaft chests")
            .define("enableAbandonedMineshaft", true);
    public static final ModConfigSpec.DoubleValue ABANDONED_MINESHAFT_CHANCE = BUILDER
            .comment("Chance to find diskette in abandoned mineshaft (0.0 - 1.0)")
            .defineInRange("abandonedMineshaftChance", 0.03, 0.0, 1.0);

    public static final ModConfigSpec SPEC = BUILDER.build();
}