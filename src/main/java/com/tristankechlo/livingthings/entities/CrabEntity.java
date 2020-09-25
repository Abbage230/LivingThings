package com.tristankechlo.livingthings.entities;

import java.util.Random;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.tristankechlo.livingthings.config.LivingThingsConfig;
import com.tristankechlo.livingthings.entities.ai.BetterMeleeAttackGoal;
import com.tristankechlo.livingthings.init.ModEntityTypes;
import com.tristankechlo.livingthings.util.IMobVariants;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CrabEntity extends AnimalEntity implements IMobVariants, IAngerable {

	private static final DataParameter<Byte> CRAB_VARIANT = EntityDataManager.createKey(CrabEntity.class, DataSerializers.BYTE);
	private static final RangedInteger rangedInteger = TickRangeConverter.convertRange(20, 39);
	private static final Ingredient BREEDING_ITEMS = Ingredient.fromItems(Items.COD);
	private int angerTime;
	private UUID angerTarget;
	
	public CrabEntity(EntityType<? extends CrabEntity> type, World worldIn) {
		super(type, worldIn);
		this.stepHeight = 1.0F;
	    this.setPathPriority(PathNodeType.WATER, 1.0F);
	}

	@Override
	public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity entity) {	
		CrabEntity entityChild = ModEntityTypes.CRAB_ENTITY.create(this.world);
		entityChild.setVariant(this.getVariantFromParents(this, entity));
		return entityChild;
	}

	public static AttributeModifierMap.MutableAttribute getAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, LivingThingsConfig.CRAB.health.get())
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 25.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, LivingThingsConfig.CRAB.damage.get());
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BetterMeleeAttackGoal(this, 1.05D, false));
		this.goalSelector.addGoal(1, new BreedGoal(this, 1.1D));
		this.goalSelector.addGoal(1, new RandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
		
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new ResetAngerGoal<>(this, true));
	}

	public static boolean canCrabSpawn(EntityType<CrabEntity> animal, IWorld world, SpawnReason reason, BlockPos pos, Random random) {
		BlockState state = world.getBlockState(pos.down());
		return (world.hasWater(pos)) || (state.isIn(Blocks.GRASS_BLOCK) || state.isIn(Blocks.SAND) || state.isIn(Blocks.GRAVEL));
	}
	
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
		this.setVariant(CrabEntity.getWeightedRandomColorVariant(this.rand));
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	public static byte getWeightedRandomColorVariant(Random random) {
		int color1Weight = LivingThingsConfig.CRAB.color1Weight.get();
		int color2Weight = LivingThingsConfig.CRAB.color2Weight.get();
		int albinoWeight = LivingThingsConfig.CRAB.colorAlbinoWeight.get();
		if(color1Weight <= 0 && color2Weight <= 0 && albinoWeight <= 0) {
			return 0;
		}
		WeightedMobVariant variant = WeightedRandom.getRandomItem(random, ImmutableList.of(
				new WeightedMobVariant(Math.max(0, color1Weight), (byte) 0),
				new WeightedMobVariant(Math.max(0, color2Weight), (byte) 1),
				new WeightedMobVariant(Math.max(0, albinoWeight), (byte) 15)));
		return variant.variant;
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(CRAB_VARIANT, (byte)0);
	}
	
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("CrabVariant", this.getVariant());
	    this.writeAngerNBT(compound);
	}
	
	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("CrabVariant")) {
			this.setVariant(compound.getByte("CrabVariant"));
		} else {
			this.setVariant((byte) 0);
		}
		if(this.world instanceof ServerWorld) {
		    this.readAngerNBT((ServerWorld)this.world, compound);
		}
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_ITEMS.test(stack);
	}
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 8;
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return this.isChild() ? 0.175F : 0.375F;
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@Override
	public boolean canBeLeashedTo(PlayerEntity player) {
		return true;
	}
	
	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return false;
	}
	
	@Override
	protected float getWaterSlowDown() {
		return 0.98F;
	}

	@Override
	public byte getVariant() {
		return this.dataManager.get(CRAB_VARIANT);
	}

	@Override
	public void setVariant(byte variant) {
		this.dataManager.set(CRAB_VARIANT, variant);
	}

	@Override
	public int getAngerTime() {
		return this.angerTime;
	}

	@Override
	public void setAngerTime(int time) {
		this.angerTime = time;
		
	}

	@Override
	public UUID getAngerTarget() {
		return this.angerTarget;
	}

	@Override
	public void setAngerTarget(UUID target) {
		this.angerTarget = target;
		
	}

	@Override
	public void func_230258_H__() {
		this.setAngerTime(rangedInteger.getRandomWithinRange(this.rand));
	}
	
}
