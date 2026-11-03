package com.pochti.diskette;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SavePointData {

    private ResourceKey<Level> dimension = Level.OVERWORLD;
    private double posX = 0.0D;
    private double posY = 0.0D;
    private double posZ = 0.0D;
    private float yaw = 0f;
    private float pitch = 0f;
    private List<ItemStack> inventory = new ArrayList<>();
    private List<ItemStack> armor = new ArrayList<>();
    private List<ItemStack> offhand = new ArrayList<>();
    private int respawnsRemaining = 0;
    private double valuableLoss = 0.0; // Теперь double
    private int deathsWithoutLoss = 0;
    private HealthData healthData = new HealthData(20.0f, 20, 5.0f, 0.0f, 0, 0);
    private List<MobEffectInstance> effects = new ArrayList<>();
    private String saveUuid = "";

    public record HealthData(
            float health,
            int foodLevel,
            float saturation,
            float exhaustion,
            int totalExperience,
            int selectedSlot
    ) {
        public static final Codec<HealthData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.fieldOf("health").forGetter(HealthData::health),
                Codec.INT.fieldOf("foodLevel").forGetter(HealthData::foodLevel),
                Codec.FLOAT.fieldOf("saturation").forGetter(HealthData::saturation),
                Codec.FLOAT.fieldOf("exhaustion").forGetter(HealthData::exhaustion),
                Codec.INT.fieldOf("totalExperience").forGetter(HealthData::totalExperience),
                Codec.INT.fieldOf("selectedSlot").forGetter(HealthData::selectedSlot)
        ).apply(i, HealthData::new));
    }

    private static final Codec<List<MobEffectInstance>> EFFECTS_CODEC = CompoundTag.CODEC.listOf().xmap(
            list -> {
                List<MobEffectInstance> out = new ArrayList<>();
                for (CompoundTag tag : list) {
                    MobEffectInstance inst = MobEffectInstance.load(tag);
                    if (inst != null) out.add(inst);
                }
                return out;
            },
            effects -> {
                List<CompoundTag> out = new ArrayList<>();
                for (MobEffectInstance inst : effects) {
                    Tag tag = inst.save();
                    if (tag instanceof CompoundTag compound) {
                        out.add(compound);
                    }
                }
                return out;
            }
    );

    public static final Codec<SavePointData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(SavePointData::getDimension),
                    Codec.DOUBLE.fieldOf("posX").forGetter(SavePointData::getPosX),
                    Codec.DOUBLE.fieldOf("posY").forGetter(SavePointData::getPosY),
                    Codec.DOUBLE.fieldOf("posZ").forGetter(SavePointData::getPosZ),
                    Codec.FLOAT.fieldOf("yaw").forGetter(SavePointData::getYaw),
                    Codec.FLOAT.fieldOf("pitch").forGetter(SavePointData::getPitch),
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("inventory").forGetter(SavePointData::getInventory),
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("armor").forGetter(SavePointData::getArmor),
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("offhand").forGetter(SavePointData::getOffhand),
                    Codec.INT.optionalFieldOf("respawnsRemaining", 0).forGetter(SavePointData::getRespawnsRemaining),
                    Codec.DOUBLE.optionalFieldOf("valuableLoss", 0.0).forGetter(SavePointData::getValuableLoss),
                    Codec.INT.optionalFieldOf("deathsWithoutLoss", 0).forGetter(SavePointData::getDeathsWithoutLoss),
                    HealthData.CODEC.optionalFieldOf("healthData", new HealthData(20.0f, 20, 5.0f, 0.0f, 0, 0))
                            .forGetter(SavePointData::getHealthData),
                    EFFECTS_CODEC.optionalFieldOf("effects", List.of()).forGetter(SavePointData::getEffects),
                    Codec.STRING.optionalFieldOf("saveUuid", "").forGetter(SavePointData::getSaveUuid)
            ).apply(instance, SavePointData::new)
    );

    public SavePointData() {}

    private SavePointData(
            ResourceKey<Level> dimension,
            double posX, double posY, double posZ,
            float yaw, float pitch,
            List<ItemStack> inventory,
            List<ItemStack> armor,
            List<ItemStack> offhand,
            int respawnsRemaining,
            double valuableLoss,
            int deathsWithoutLoss,
            HealthData healthData,
            List<MobEffectInstance> effects,
            String saveUuid
    ) {
        this.dimension = dimension;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.inventory = inventory;
        this.armor = armor;
        this.offhand = offhand;
        this.respawnsRemaining = respawnsRemaining;
        this.valuableLoss = valuableLoss;
        this.deathsWithoutLoss = deathsWithoutLoss;
        this.healthData = healthData;
        this.effects = effects;
        this.saveUuid = saveUuid;
    }

    public void captureFrom(Player player) {
        this.dimension = player.level().dimension();
        this.posX = player.getX();
        this.posY = player.getY();
        this.posZ = player.getZ();
        this.yaw = player.getYRot();
        this.pitch = player.getXRot();

        this.saveUuid = UUID.randomUUID().toString();

        this.inventory = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.DISKETTE.get())) {
                this.inventory.add(ItemStack.EMPTY);
            } else {
                applySaveTag(stack, this.saveUuid);
                this.inventory.add(stack.copy());
            }
        }
        this.armor = new ArrayList<>();
        for (ItemStack stack : player.getInventory().armor) {
            applySaveTag(stack, this.saveUuid);
            this.armor.add(stack.copy());
        }
        this.offhand = new ArrayList<>();
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.DISKETTE.get())) {
                this.offhand.add(ItemStack.EMPTY);
            } else {
                applySaveTag(stack, this.saveUuid);
                this.offhand.add(stack.copy());
            }
        }

        FoodData food = player.getFoodData();
        this.healthData = new HealthData(
                player.getHealth(),
                food.getFoodLevel(),
                food.getSaturationLevel(),
                food.getExhaustionLevel(),
                player.totalExperience,
                player.getInventory().selected
        );

        this.effects = new ArrayList<>();
        for (MobEffectInstance effect : player.getActiveEffects()) {
            this.effects.add(new MobEffectInstance(effect));
        }

        this.valuableLoss = 0.0;
        this.deathsWithoutLoss = 0;
    }

    private void applySaveTag(ItemStack stack, String uuid) {
        if (stack.isEmpty()) return;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putString("diskette_save_uuid", uuid);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public void restoreTo(Player player) {
        player.getInventory().clearContent();
        for (int i = 0; i < this.inventory.size() && i < player.getInventory().items.size(); i++) {
            player.getInventory().items.set(i, this.inventory.get(i).copy());
        }
        for (int i = 0; i < this.armor.size() && i < player.getInventory().armor.size(); i++) {
            player.getInventory().armor.set(i, this.armor.get(i).copy());
        }
        for (int i = 0; i < this.offhand.size() && i < player.getInventory().offhand.size(); i++) {
            player.getInventory().offhand.set(i, this.offhand.get(i).copy());
        }

        player.setHealth(this.healthData.health());

        FoodData food = player.getFoodData();
        food.setFoodLevel(this.healthData.foodLevel());
        food.setSaturation(this.healthData.saturation());
        food.setExhaustion(this.healthData.exhaustion());

        player.experienceLevel = 0;
        player.experienceProgress = 0f;
        player.totalExperience = 0;
        player.giveExperiencePoints(this.healthData.totalExperience());

        player.getInventory().selected = this.healthData.selectedSlot();

        player.removeAllEffects();
        for (MobEffectInstance effect : this.effects) {
            player.addEffect(new MobEffectInstance(effect));
        }
    }

    public ResourceKey<Level> getDimension() { return dimension; }
    public double getPosX() { return posX; }
    public double getPosY() { return posY; }
    public double getPosZ() { return posZ; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public List<ItemStack> getInventory() { return inventory; }
    public List<ItemStack> getArmor() { return armor; }
    public List<ItemStack> getOffhand() { return offhand; }
    public int getRespawnsRemaining() { return respawnsRemaining; }
    public double getValuableLoss() { return valuableLoss; }
    public int getDeathsWithoutLoss() { return deathsWithoutLoss; }
    public HealthData getHealthData() { return healthData; }
    public List<MobEffectInstance> getEffects() { return effects; }
    public String getSaveUuid() { return saveUuid; }

    public void setRespawnsRemaining(int value) { this.respawnsRemaining = value; }
    public void setValuableLoss(double value) { this.valuableLoss = value; }
    public void setDeathsWithoutLoss(int value) { this.deathsWithoutLoss = value; }

    public boolean isActive() { return respawnsRemaining > 0; }
}