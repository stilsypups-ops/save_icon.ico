package com.pochti.diskette;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public class DisketteItem extends Item {

    public DisketteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // ===== СЕРВЕР: данные, достижение, звук, расход =====
            SavePointData data = player.getData(ModAttachments.SAVE_POINT.get());

            int perDiskette = ModConfig.RESPAWNS_PER_DISKETTE.get();
            int maxTotal = ModConfig.MAX_TOTAL_RESPAWNS.get();

            if (data.isActive()) {
                int newRespawns = Math.min(data.getRespawnsRemaining() + perDiskette, maxTotal);
                data.setRespawnsRemaining(newRespawns);
            } else {
                data.setRespawnsRemaining(Math.min(perDiskette, maxTotal));
            }

            data.captureFrom(player);

            if (player instanceof ServerPlayer serverPlayer) {
                awardAdvancement(serverPlayer, "determination", "set_save_point");
            }

            player.displayClientMessage(
                    Component.translatable("diskette.message.save_point_set", data.getRespawnsRemaining()),
                    true
            );

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 1.0f);

            ItemStack stack = player.getItemInHand(hand);
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        } else {
            // ===== КЛИЕНТ: либо Photon-эффект, либо ванильные партиклы =====
            if (ModList.get().isLoaded("photon")) {
                DisketteClientEffects.playSaveEffect(player);
            } else {
                // Фоллбэк: обычные искры вокруг игрока
                for (int i = 0; i < 30; i++) {
                    double dx = (level.random.nextDouble() - 0.5) * 1.0;
                    double dy = level.random.nextDouble() * 1.5;
                    double dz = (level.random.nextDouble() - 0.5) * 1.0;
                    level.addParticle(ParticleTypes.END_ROD,
                            player.getX() + dx,
                            player.getY() + 0.5 + dy,
                            player.getZ() + dz,
                            0.0, 0.02, 0.0);
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private void awardAdvancement(ServerPlayer player, String name, String criterion) {
        var adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(DisketteMod.MODID, name));
        if (adv != null) {
            player.getAdvancements().award(adv, criterion);
        }
    }
}