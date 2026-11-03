package com.pochti.diskette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetCustomDataFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.lang.reflect.Field;
import java.util.List;

public class LootTableHandler {

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        String table = event.getName().toString();

        // Древний город
        if (isEnabled(ModConfig.ENABLE_ANCIENT_CITY, true) && table.equals("minecraft:chests/ancient_city")) {
            addDiskettePool(event, getChance(ModConfig.ANCIENT_CITY_CHANCE, 0.15), null);
        }

        // Крепость
        if (isEnabled(ModConfig.ENABLE_STRONGHOLD, true) &&
                (table.equals("minecraft:chests/stronghold_library") ||
                 table.equals("minecraft:chests/stronghold_corridor") ||
                 table.equals("minecraft:chests/stronghold_crossing"))) {
            addDiskettePool(event, getChance(ModConfig.STRONGHOLD_CHANCE, 0.10), null);
        }

        // Сниффер
        if (isEnabled(ModConfig.ENABLE_SNIFFER, true) && table.equals("minecraft:gameplay/sniffer_digging")) {
            addDiskettePool(event, getChance(ModConfig.SNIFFER_CHANCE, 0.05), "sniffer");
        }

        // Археология (пул в начало)
        if (isEnabled(ModConfig.ENABLE_ARCHAEOLOGY, true) &&
                (table.equals("minecraft:archaeology/desert_pyramid") ||
                 table.equals("minecraft:archaeology/desert_well") ||
                 table.equals("minecraft:archaeology/trail_ruins_common") ||
                 table.equals("minecraft:archaeology/trail_ruins_rare"))) {
            addDiskettePoolAtStart(event, getChance(ModConfig.ARCHAEOLOGY_CHANCE, 0.07), "archaeology");
        }

        // === Trial Chambers: уникальные пулы ===

        // Обычный уникальный пул (Bolt Armor Trim, Guster Banner Pattern и т.д.)
        if (isEnabled(ModConfig.ENABLE_TRIAL_VAULT, true) && table.equals("minecraft:chests/trial_chambers/reward_unique")) {
            addDiskettePoolAtStart(event, getChance(ModConfig.TRIAL_VAULT_CHANCE, 0.05), null);
        }

        // Зловещий уникальный пул (Heavy Core, Flow Armor Trim и т.д.)
        if (isEnabled(ModConfig.ENABLE_OMINOUS_VAULT, true) && table.equals("minecraft:chests/trial_chambers/reward_ominous_unique")) {
            addDiskettePoolAtStart(event, getChance(ModConfig.OMINOUS_VAULT_CHANCE, 0.10), null);
        }

        // Закопанные сокровища
        if (isEnabled(ModConfig.ENABLE_BURIED_TREASURE, true) && table.equals("minecraft:chests/buried_treasure")) {
            addDiskettePool(event, getChance(ModConfig.BURIED_TREASURE_CHANCE, 0.10), null);
        }

        // Пустынная пирамида
        if (isEnabled(ModConfig.ENABLE_DESERT_PYRAMID, true) && table.equals("minecraft:chests/desert_pyramid")) {
            addDiskettePool(event, getChance(ModConfig.DESERT_PYRAMID_CHANCE, 0.08), null);
        }

        // Храм в джунглях
        if (isEnabled(ModConfig.ENABLE_JUNGLE_TEMPLE, true) && table.equals("minecraft:chests/jungle_temple")) {
            addDiskettePool(event, getChance(ModConfig.JUNGLE_TEMPLE_CHANCE, 0.08), null);
        }

        // Простое подземелье
        if (isEnabled(ModConfig.ENABLE_SIMPLE_DUNGEON, true) && table.equals("minecraft:chests/simple_dungeon")) {
            addDiskettePool(event, getChance(ModConfig.SIMPLE_DUNGEON_CHANCE, 0.03), null);
        }

        // Заброшенная шахта
        if (isEnabled(ModConfig.ENABLE_ABANDONED_MINESHAFT, true) && table.equals("minecraft:chests/abandoned_mineshaft")) {
            addDiskettePool(event, getChance(ModConfig.ABANDONED_MINESHAFT_CHANCE, 0.03), null);
        }
    }

    private boolean isEnabled(net.neoforged.neoforge.common.ModConfigSpec.BooleanValue config, boolean defaultValue) {
        return ModConfig.SPEC.isLoaded() ? config.get() : defaultValue;
    }

    private double getChance(net.neoforged.neoforge.common.ModConfigSpec.DoubleValue config, double defaultValue) {
        return ModConfig.SPEC.isLoaded() ? config.get() : defaultValue;
    }

    private LootPool buildPool(double chance, String source) {
        var entry = LootItem.lootTableItem(ModItems.DISKETTE.get())
                .setWeight(1)
                .when(LootItemRandomChanceCondition.randomChance((float) chance));

        var builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(entry);

        if (source != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("diskette_source", source);
            builder.apply(SetCustomDataFunction.setCustomData(tag));
        }

        return builder.build();
    }

    private void addDiskettePool(LootTableLoadEvent event, double chance, String source) {
        event.getTable().addPool(buildPool(chance, source));
    }

    private void addDiskettePoolAtStart(LootTableLoadEvent event, double chance, String source) {
        LootPool pool = buildPool(chance, source);
        try {
            Field poolsField = LootTable.class.getDeclaredField("pools");
            poolsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<LootPool> pools = (List<LootPool>) poolsField.get(event.getTable());
            pools.add(0, pool);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            event.getTable().addPool(pool);
        }
    }
}