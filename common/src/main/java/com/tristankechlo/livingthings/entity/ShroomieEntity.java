package com.tristankechlo.livingthings.entity;

import com.tristankechlo.livingthings.config.entity.ShroomieConfig;
import com.tristankechlo.livingthings.entity.ai.ShroomiePlantMushroomGoal;
import com.tristankechlo.livingthings.entity.misc.IMobVariants;
import com.tristankechlo.livingthings.init.ModEntityTypes;
import com.tristankechlo.livingthings.util.ILexiconEntry;
import com.tristankechlo.livingthings.util.LexiconEntries;
import com.tristankechlo.livingthings.util.Ingredients;
import com.tristankechlo.livingthings.util.LivingThingsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

public class ShroomieEntity extends Animal implements IMobVariants, ILexiconEntry {

    private static final EntityDataAccessor<Byte> VARIANT = SynchedEntityData.defineId(ShroomieEntity.class, EntityDataSerializers.BYTE);
    private static final UniformInt RANGED_INTEGER = TimeUtil.rangeOfSeconds(30, 60);
    private boolean canPlantMushroom;
    private int mushroomCooldown;

    public ShroomieEntity(EntityType<? extends ShroomieEntity> entityType, Level world) {
        super(entityType, world);
        canPlantMushroom = false;
    }

    public static boolean checkShroomieSpawnRules(EntityType<ShroomieEntity> animal, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(LivingThingsTags.SHROOMIE_SPAWNABLE_ON) && isBrightEnoughToSpawn(world, pos);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getByte("ShroomieType"));
        this.mushroomCooldown = tag.getInt("MushroomCooldown");
        this.canPlantMushroom = tag.getBoolean("CanPlantMushroom");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("ShroomieType", getVariant());
        tag.putInt("MushroomCooldown", this.mushroomCooldown);
        tag.putBoolean("CanPlantMushroom", this.canPlantMushroom);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredients.SHROOMIE_FOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new ShroomiePlantMushroomGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide && this.mushroomCooldown > 0) {
            this.mushroomCooldown--;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, (byte) 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData data, CompoundTag tag) {
        final int brownWeight = ShroomieConfig.get().colorBrownWeight.get();
        final int redWeight = ShroomieConfig.get().colorRedWeight.get();
        this.setVariant(this.getRandomVariant(random, new byte[]{0, 1}, new int[]{brownWeight, redWeight}));
        return super.finalizeSpawn(world, difficulty, reason, data, tag);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(LivingThingsTags.SHROOMIE_FOOD);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return ShroomieConfig.maxSpawnedInChunk();
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return size.height * 0.55F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if ((this.getVariant() == 1 && stack.is(Items.RED_MUSHROOM))
                || (this.getVariant() == 0 && stack.is(Items.BROWN_MUSHROOM))) {
            if (this.level.isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!this.canPlantMushroom) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.mushroomCooldown += 100;
                    this.canPlantMushroom = true;
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ShroomieConfig.health())
                .add(Attributes.MOVEMENT_SPEED, ShroomieConfig.movementSpeed());
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob parent) {
        ShroomieEntity shroomie = ModEntityTypes.SHROOMIE.get().create(world);
        shroomie.setVariant(this.getVariantFromParents(this, parent));
        shroomie.setAge(0); // no baby shroomies
        return shroomie;
    }

    public boolean canPlantMushroom() {
        return this.canPlantMushroom && this.mushroomCooldown <= 0;
    }

    public void plantedMushroom() {
        // 50% chance to plant another mushroom after the cooldown
        if (this.random.nextBoolean()) {
            this.canPlantMushroom = false;
        }
        this.mushroomCooldown = RANGED_INTEGER.sample(random);
    }

    @Override
    public byte getVariant() {
        return this.entityData.get(VARIANT);
    }

    @Override
    public void setVariant(byte type) {
        if (type > 1 || type < 0) {
            return;
        }
        this.entityData.set(VARIANT, type);
    }

    @Override
    public ResourceLocation getLexiconEntry() {
        return LexiconEntries.SHROOMIE;
    }

}
