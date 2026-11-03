package com.pochti.diskette;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreen {
    public static Screen create(Screen parent) {
        try {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("diskette.config.title"));

            ConfigEntryBuilder eb = builder.entryBuilder();

            // === Основные ===
            ConfigCategory general = builder.getOrCreateCategory(Component.translatable("diskette.config.category.general"));
            
            general.addEntry(eb.startIntField(Component.translatable("diskette.config.respawns_per_diskette"), ModConfig.RESPAWNS_PER_DISKETTE.get())
                    .setDefaultValue(3).setMin(1).setMax(100)
                    .setTooltip(Component.translatable("diskette.config.respawns_per_diskette.tooltip"))
                    .setSaveConsumer(v -> ModConfig.RESPAWNS_PER_DISKETTE.set(v)).build());
            
            general.addEntry(eb.startIntField(Component.translatable("diskette.config.max_total_respawns"), ModConfig.MAX_TOTAL_RESPAWNS.get())
                    .setDefaultValue(99).setMin(1).setMax(1000)
                    .setTooltip(Component.translatable("diskette.config.max_total_respawns.tooltip"))
                    .setSaveConsumer(v -> ModConfig.MAX_TOTAL_RESPAWNS.set(v)).build());

            // Новая настройка
            general.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_save_scum"), ModConfig.ENABLE_SAVE_SCUM.get())
                    .setDefaultValue(false)
                    .setTooltip(Component.translatable("diskette.config.enable_save_scum.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_SAVE_SCUM.set(v)).build());

            // === Добыча ===
            ConfigCategory loot = builder.getOrCreateCategory(Component.translatable("diskette.config.category.loot"));

            // Древний город
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_ancient_city"), ModConfig.ENABLE_ANCIENT_CITY.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_ancient_city.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_ANCIENT_CITY.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.ancient_city_chance"), ModConfig.ANCIENT_CITY_CHANCE.get())
                    .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.ancient_city_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ANCIENT_CITY_CHANCE.set(v)).build());

            // Крепость
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_stronghold"), ModConfig.ENABLE_STRONGHOLD.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_stronghold.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_STRONGHOLD.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.stronghold_chance"), ModConfig.STRONGHOLD_CHANCE.get())
                    .setDefaultValue(0.10).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.stronghold_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.STRONGHOLD_CHANCE.set(v)).build());

            // Сниффер
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_sniffer"), ModConfig.ENABLE_SNIFFER.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_sniffer.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_SNIFFER.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.sniffer_chance"), ModConfig.SNIFFER_CHANCE.get())
                    .setDefaultValue(0.05).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.sniffer_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.SNIFFER_CHANCE.set(v)).build());

            // Археология
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_archaeology"), ModConfig.ENABLE_ARCHAEOLOGY.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_archaeology.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_ARCHAEOLOGY.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.archaeology_chance"), ModConfig.ARCHAEOLOGY_CHANCE.get())
                    .setDefaultValue(0.07).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.archaeology_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ARCHAEOLOGY_CHANCE.set(v)).build());

            // Trial Vault
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_trial_vault"), ModConfig.ENABLE_TRIAL_VAULT.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_trial_vault.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_TRIAL_VAULT.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.trial_vault_chance"), ModConfig.TRIAL_VAULT_CHANCE.get())
                    .setDefaultValue(0.05).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.trial_vault_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.TRIAL_VAULT_CHANCE.set(v)).build());

            // Ominous Vault
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_ominous_vault"), ModConfig.ENABLE_OMINOUS_VAULT.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_ominous_vault.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_OMINOUS_VAULT.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.ominous_vault_chance"), ModConfig.OMINOUS_VAULT_CHANCE.get())
                    .setDefaultValue(0.15).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.ominous_vault_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.OMINOUS_VAULT_CHANCE.set(v)).build());

            // Закопанные сокровища
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_buried_treasure"), ModConfig.ENABLE_BURIED_TREASURE.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_buried_treasure.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_BURIED_TREASURE.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.buried_treasure_chance"), ModConfig.BURIED_TREASURE_CHANCE.get())
                    .setDefaultValue(0.10).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.buried_treasure_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.BURIED_TREASURE_CHANCE.set(v)).build());

            // Пустынная пирамида
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_desert_pyramid"), ModConfig.ENABLE_DESERT_PYRAMID.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_desert_pyramid.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_DESERT_PYRAMID.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.desert_pyramid_chance"), ModConfig.DESERT_PYRAMID_CHANCE.get())
                    .setDefaultValue(0.08).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.desert_pyramid_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.DESERT_PYRAMID_CHANCE.set(v)).build());

            // Храм в джунглях
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_jungle_temple"), ModConfig.ENABLE_JUNGLE_TEMPLE.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_jungle_temple.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_JUNGLE_TEMPLE.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.jungle_temple_chance"), ModConfig.JUNGLE_TEMPLE_CHANCE.get())
                    .setDefaultValue(0.08).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.jungle_temple_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.JUNGLE_TEMPLE_CHANCE.set(v)).build());

            // Простое подземелье
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_simple_dungeon"), ModConfig.ENABLE_SIMPLE_DUNGEON.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_simple_dungeon.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_SIMPLE_DUNGEON.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.simple_dungeon_chance"), ModConfig.SIMPLE_DUNGEON_CHANCE.get())
                    .setDefaultValue(0.03).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.simple_dungeon_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.SIMPLE_DUNGEON_CHANCE.set(v)).build());

            // Заброшенная шахта
            loot.addEntry(eb.startBooleanToggle(Component.translatable("diskette.config.enable_abandoned_mineshaft"), ModConfig.ENABLE_ABANDONED_MINESHAFT.get())
                    .setDefaultValue(true)
                    .setTooltip(Component.translatable("diskette.config.enable_abandoned_mineshaft.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ENABLE_ABANDONED_MINESHAFT.set(v)).build());
            loot.addEntry(eb.startDoubleField(Component.translatable("diskette.config.abandoned_mineshaft_chance"), ModConfig.ABANDONED_MINESHAFT_CHANCE.get())
                    .setDefaultValue(0.03).setMin(0.0).setMax(1.0)
                    .setTooltip(Component.translatable("diskette.config.abandoned_mineshaft_chance.tooltip"))
                    .setSaveConsumer(v -> ModConfig.ABANDONED_MINESHAFT_CHANCE.set(v)).build());

            builder.setSavingRunnable(() -> ModConfig.SPEC.save());
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}