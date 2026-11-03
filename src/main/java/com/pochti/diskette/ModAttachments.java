package com.pochti.diskette;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DisketteMod.MODID);

    public static final Supplier<AttachmentType<SavePointData>> SAVE_POINT =
            ATTACHMENT_TYPES.register(
                    "save_point",
                    () -> AttachmentType.builder(SavePointData::new)
                            .serialize(SavePointData.CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}