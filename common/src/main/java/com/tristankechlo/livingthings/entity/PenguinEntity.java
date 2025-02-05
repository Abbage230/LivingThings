package com.tristankechlo.livingthings.entity;

import com.tristankechlo.livingthings.config.entity.PenguinConfig;
import com.tristankechlo.livingthings.init.ModEntityTypes;
import com.tristankechlo.livingthings.init.ModSounds;
import com.tristankechlo.livingthings.util.ILexiconEntry;
import com.tristankechlo.livingthings.util.Ingredients;
import com.tristankechlo.livingthings.util.LexiconEntries;
import com.tristankechlo.livingthings.util.LivingThingsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class PenguinEntity extends Animal implements ILexiconEntry {

    public PenguinEntity(EntityType<PenguinEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public static boolean checkPenguinSpawnRules(EntityType<PenguinEntity> animal, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(LivingThingsTags.PENGUIN_SPAWNABLE_ON) && isBrightEnoughToSpawn(world, pos);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel worldIn, AgeableMob parent) {
        return ModEntityTypes.PENGUIN.get().create(worldIn);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, PenguinConfig.health())
                .add(Attributes.MOVEMENT_SPEED, PenguinConfig.movementSpeed());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PolarBear.class, 8.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredients.PENGUIN_FOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, PenguinEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(LivingThingsTags.PENGUIN_FOOD);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return PenguinConfig.talkInterval();
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return PenguinConfig.maxSpawnedInChunk();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.PENGUIN_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.PENGUIN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PENGUIN_DEATH.get();
    }

    @Override
    public ResourceLocation getLexiconEntry() {
        return LexiconEntries.PENGUIN;
    }

}
