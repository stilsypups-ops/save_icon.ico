package com.pochti.diskette;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.BedBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;

public class EventHandler {

    private static final Map<ResourceLocation, Double> MODDED_VALUES = new HashMap<>();

    static {
        // Тут теперь double, если надо
        addModded("mythicmetals:adamantite_ingot", 8.0);
        addModded("mythicmetals:orichalcum_ingot", 8.0);
        addModded("mythicmetals:prometheum_ingot", 10.0);
        addModded("mythicmetals:mythril_ingot", 6.0);
        addModded("mythicmetals:starrite", 12.0);
        addModded("mythicmetals:metallurgium_ingot", 15.0);
        addModded("bosses_of_mass_destruction:ancient_ankh", 20.0);
        addModded("bosses_of_mass_destruction:charged_ender_pearl", 15.0);
        addModded("twilightforest:fiery_ingot", 10.0);
        addModded("twilightforest:knightmetal_ingot", 8.0);
        addModded("twilightforest:steeleaf_ingot", 5.0);
        addModded("aether:zanite_gemstone", 6.0);
        addModded("aether:ambrosium_shard", 3.0);
        addModded("aether:gravitite_ore", 4.0);
        addModded("botania:terrasteel_ingot", 8.0);
        addModded("botania:gaia_ingot", 10.0);
        addModded("create:refined_radiance", 10.0);
        addModded("create:shadow_steel", 10.0);
    }

    private static void addModded(String id, double value) {
        ResourceLocation loc = ResourceLocation.parse(id);
        if (BuiltInRegistries.ITEM.containsKey(loc)) {
            MODDED_VALUES.put(loc, value);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());
            if (data.isActive()) {
                double currentValue = 0;
                for (ItemStack stack : player.getInventory().items) currentValue += getValue(stack) * stack.getCount();
                for (ItemStack stack : player.getInventory().armor) currentValue += getValue(stack) * stack.getCount();
                for (ItemStack stack : player.getInventory().offhand) currentValue += getValue(stack) * stack.getCount();

                double savedValue = 0;
                for (ItemStack stack : data.getInventory()) savedValue += getValue(stack) * stack.getCount();
                for (ItemStack stack : data.getArmor()) savedValue += getValue(stack) * stack.getCount();
                for (ItemStack stack : data.getOffhand()) savedValue += getValue(stack) * stack.getCount();

                double loss = Math.max(0, currentValue - savedValue);
                data.setValuableLoss(loss);

                if (loss < 0.01) { // Считаем нулем, если потеря меньше 0.01
                    data.setDeathsWithoutLoss(data.getDeathsWithoutLoss() + 1);
                } else {
                    data.setDeathsWithoutLoss(0);
                }

                player.getInventory().clearContent();
                player.experienceLevel = 0;
                player.experienceProgress = 0;
                player.totalExperience = 0;
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());
            if (data.isActive()) {
                event.setCanceled(true);
                event.getDrops().clear();
            }
        }
    }

    @SubscribeEvent
    public void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());
            if (data.isActive()) {
                event.setDroppedExperience(0);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity().level().isClientSide) return;

        ServerPlayer player = (ServerPlayer) event.getEntity();
        SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());

        if (data.isActive()) {
            ServerLevel targetLevel = player.server.getLevel(data.getDimension());
            if (targetLevel == null) targetLevel = player.server.overworld();

            player.connection.teleport(data.getPosX(), data.getPosY(), data.getPosZ(), data.getYaw(), data.getPitch());

            player.server.execute(() -> data.restoreTo(player));

            data.setRespawnsRemaining(data.getRespawnsRemaining() - 1);
            if (data.getRespawnsRemaining() > 0) {
                player.displayClientMessage(Component.translatable("diskette.message.remaining_respawns", data.getRespawnsRemaining()), true);
            } else {
                data.setRespawnsRemaining(0);
                player.displayClientMessage(Component.translatable("diskette.message.save_point_exhausted"), true);
            }

            if (data.getValuableLoss() >= 20) {
                awardAdvancement(player, "offense", "lose_valuables");
            }
            if (data.getValuableLoss() >= 50) {
                awardAdvancement(player, "oh_oh_oh", "lose_valuables");
            }
            if (data.getDeathsWithoutLoss() >= 3) {
                awardAdvancement(player, "close_call", "deaths_without_loss");
            }

            if (!ModConfig.ENABLE_SAVE_SCUM.get() && !data.getSaveUuid().isEmpty()) {
                String uuid = data.getSaveUuid();
                player.level().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(128)).forEach(itemEntity -> {
                    if (matchesUuid(itemEntity.getItem(), uuid)) {
                        itemEntity.discard();
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());
            if (data.isActive() && !ModConfig.ENABLE_SAVE_SCUM.get() && !data.getSaveUuid().isEmpty()) {
                String uuid = data.getSaveUuid();
                AbstractContainerMenu menu = event.getContainer();
                
                for (Slot slot : menu.slots) {
                    if (slot.container != player.getInventory() && matchesUuid(slot.getItem(), uuid)) {
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onContainerClose(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            checkSaveScum(player);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            if (!event.isCanceled() && player.level().getBlockState(event.getPos()).getBlock() instanceof BedBlock) {
                SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());
                if (data.isActive()) {
                    data.setRespawnsRemaining(0);
                    player.displayClientMessage(Component.translatable("diskette.message.save_point_removed_bed"), true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemPickup(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            ItemEntity itemEntity = event.getItemEntity();
            ItemStack stack = itemEntity.getItem();
            if (stack.is(ModItems.DISKETTE.get())) {
                var customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null) {
                    CompoundTag tag = customData.copyTag();
                    if (tag.contains("diskette_source")) {
                        String source = tag.getString("diskette_source");
                        if ("sniffer".equals(source)) {
                            awardAdvancement(serverPlayer, "antiquarian", "sniffer_diskette");
                        } else if ("archaeology".equals(source)) {
                            awardAdvancement(serverPlayer, "how_did_this_get_here", "archaeology_diskette");
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemPickupPost(ItemEntityPickupEvent.Post event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            checkSaveScum(player);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
                checkSaveScum(player);
            }
        }
    }

    private void checkSaveScum(ServerPlayer player) {
        if (!ModConfig.ENABLE_SAVE_SCUM.get()) return;
        SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());
        if (!data.isActive() || data.getSaveUuid().isEmpty()) return;

        String uuid = data.getSaveUuid();
        
        double savedValue = 0;
        for (ItemStack stack : data.getInventory()) if (matchesUuid(stack, uuid)) savedValue += getValue(stack) * stack.getCount();
        for (ItemStack stack : data.getArmor()) if (matchesUuid(stack, uuid)) savedValue += getValue(stack) * stack.getCount();
        for (ItemStack stack : data.getOffhand()) if (matchesUuid(stack, uuid)) savedValue += getValue(stack) * stack.getCount();

        double currentValue = 0;
        for (ItemStack stack : player.getInventory().items) if (matchesUuid(stack, uuid)) currentValue += getValue(stack) * stack.getCount();
        for (ItemStack stack : player.getInventory().armor) if (matchesUuid(stack, uuid)) currentValue += getValue(stack) * stack.getCount();
        for (ItemStack stack : player.getInventory().offhand) if (matchesUuid(stack, uuid)) currentValue += getValue(stack) * stack.getCount();

        System.out.println("[Diskette] SaveScum Check: Current=" + currentValue + " Saved=" + savedValue + " Diff=" + (currentValue - savedValue));

        if (currentValue - savedValue >= 10.0) {
            awardAdvancement(player, "save_scum", "dupe_valuables");
        }
    }

    private boolean matchesUuid(ItemStack stack, String uuid) {
        if (stack.isEmpty()) return false;
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return uuid.equals(customData.copyTag().getString("diskette_save_uuid"));
        }
        return false;
    }

    private void awardAdvancement(ServerPlayer player, String advancementId, String criterion) {
        var advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(DisketteMod.MODID, advancementId));
        if (advancement != null) {
            player.getAdvancements().award(advancement, criterion);
            System.out.println("[Diskette] Awarded advancement: " + advancementId);
        } else {
            System.out.println("[Diskette] Advancement not found: " + advancementId);
        }
    }

    private double getValue(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Item item = stack.getItem();

        if (item == Items.DIAMOND) return 1.0;
        if (item == Items.DIAMOND_BLOCK) return 9.0;
        if (item == Items.NETHERITE_INGOT) return 5.0;
        if (item == Items.NETHERITE_BLOCK) return 45.0;
        if (item == Items.NETHERITE_SCRAP) return 0.9;

        if (item == Items.GOLD_INGOT || item == Items.RAW_GOLD) return 0.35;
        if (item == Items.GOLD_BLOCK || item == Items.RAW_GOLD_BLOCK) return 3.15;
        
        if (item == Items.IRON_INGOT || item == Items.RAW_IRON) return 0.5;
        if (item == Items.IRON_BLOCK || item == Items.RAW_IRON_BLOCK) return 4.5;
        
        if (item == Items.EMERALD) return 0.2;
        if (item == Items.EMERALD_BLOCK) return 1.8;
        
        if (item == Items.COPPER_INGOT || item == Items.RAW_COPPER) return 0.07;
        if (item == Items.COPPER_BLOCK || item == Items.RAW_COPPER_BLOCK) return 0.63;

        if (item == Items.ENCHANTED_GOLDEN_APPLE) return 15.0;
        if (item == Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE) return 8.0;
        if (item.toString().contains("_armor_trim_smithing_template")) return 7.0;
        
        if (item == Items.NETHER_STAR) return 15.0;
        if (item == Items.WITHER_SKELETON_SKULL) return 10.0;
        if (item == Items.TRIDENT) return 12.0;
        if (item == Items.ELYTRA) return 20.0;
        if (item == Items.TOTEM_OF_UNDYING) return 15.0;
        if (item == Items.DRAGON_EGG) return 50.0;
        if (item == Items.BEACON) return 20.0;

        if (item == Items.DIAMOND_HELMET) return 5.0;
        if (item == Items.DIAMOND_CHESTPLATE) return 8.0;
        if (item == Items.DIAMOND_LEGGINGS) return 7.0;
        if (item == Items.DIAMOND_BOOTS) return 4.0;
        if (item == Items.DIAMOND_SWORD) return 2.0;
        if (item == Items.DIAMOND_PICKAXE) return 3.0;
        if (item == Items.DIAMOND_AXE) return 3.0;
        if (item == Items.DIAMOND_SHOVEL) return 1.0;
        if (item == Items.DIAMOND_HOE) return 2.0;

        if (item == Items.NETHERITE_HELMET) return 18.0;
        if (item == Items.NETHERITE_CHESTPLATE) return 21.0;
        if (item == Items.NETHERITE_LEGGINGS) return 20.0;
        if (item == Items.NETHERITE_BOOTS) return 17.0;
        if (item == Items.NETHERITE_SWORD) return 15.0;
        if (item == Items.NETHERITE_PICKAXE) return 16.0;
        if (item == Items.NETHERITE_AXE) return 16.0;
        if (item == Items.NETHERITE_SHOVEL) return 14.0;
        if (item == Items.NETHERITE_HOE) return 15.0;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Double moddedValue = MODDED_VALUES.get(id);
        return moddedValue != null ? moddedValue : 0.0;
    }
}