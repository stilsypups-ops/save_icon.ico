package com.pochti.diskette;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DisketteMod.MODID);

    public static final DeferredItem<Item> DISKETTE = ITEMS.registerItem(
            "diskette",
            DisketteItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> COPPER_DISKETTE = ITEMS.registerItem(
            "copper_diskette",
            Item::new, // Пока обычный предмет, позже заменим на CopperDisketteItem
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON) // Чуть хуже эпической
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}