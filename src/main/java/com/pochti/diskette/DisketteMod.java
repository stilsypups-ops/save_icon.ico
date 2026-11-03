package com.pochti.diskette;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(DisketteMod.MODID)
public class DisketteMod {
    public static final String MODID = "diskette";

    public DisketteMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(
                net.neoforged.fml.config.ModConfig.Type.COMMON,
                ModConfig.SPEC
        );

        ModItems.register(modEventBus);
        ModAttachments.register(modEventBus);

        modEventBus.addListener(this::onCreativeModeTabBuild);

        NeoForge.EVENT_BUS.register(new EventHandler());
        NeoForge.EVENT_BUS.register(new LootTableHandler());

        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded("cloth_config")) {
            modContainer.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (container, parentScreen) -> ClothConfigScreen.create(parentScreen)
            );
        }
    }

    private void onCreativeModeTabBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.DISKETTE.get());
            event.accept(ModItems.COPPER_DISKETTE.get());
        }
    }
}