package com.pochti.diskette;

import com.lowdragmc.photon.client.fx.BlockEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class DisketteClientEffects {

    public static void playSaveEffect(Player player) {
        ResourceLocation fxId = ResourceLocation.fromNamespaceAndPath("diskette", "save_effect");

        FX fx = FXHelper.getFX(fxId);
        if (fx == null) { return; }

        // Проигрываем эффект в блоке, на котором стоит игрок.
        // Если хочешь, чтобы эффект летел за игроком — замени на EntityEffectExecutor(fx, player.level(), player, EntityEffectExecutor.AutoRotate.NONE)
        new BlockEffectExecutor(fx, player.level(), player.blockPosition()).start();
    }
}