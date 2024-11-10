package com.tristankechlo.livingthings.entity;

import com.tristankechlo.livingthings.config.entity.FlamingoConfig;
import com.tristankechlo.livingthings.init.ModEntityTypes;
import com.tristankechlo.livingthings.util.ILexiconEntry;
import com.tristankechlo.livingthings.util.Ingredients;
import com.tristankechlo.livingthings.util.LexiconEntries;
import com.tristankechlo.livingthings.util.LivingThingsTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlamingoEntity extends Animal implements ILexiconEntry {

    private static final EntityDataAccessor<Boolean> LEFT_LEG_UP = SynchedEntityData.defineId(FlamingoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT_LEG_UP = SynchedEntityData.defineId(FlamingoEntity.class, EntityDataSerializers.BOOLEAN);
    private DeepWaterAvoidingRandomWalkingGoal randomWalkingGoal;

    public FlamingoEntity(EntityType<? extends FlamingoEntity> type, Level worldIn) {
        super(type, worldIn);
        //this.setMaxUpStep(1.0F);
        this.setPathfindingMalus(PathType.WATER, 1.0F);
    }

    public static boolean checkFlamingoSpawnRules(EntityType<FlamingoEntity> animal, LevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.below()).is(LivingThingsTags.FLAMINGO_SPAWNABLE_ON) && isBrightEnoughToSpawn(world, pos);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return ModEntityTypes.FLAMINGO.get().create(world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, FlamingoConfig.health())
                .add(Attributes.MOVEMENT_SPEED, FlamingoConfig.movementSpeed());
    }

    @Override
    protected void registerGoals() {
        this.randomWalkingGoal = new FlamingoEntity.DeepWaterAvoidingRandomWalkingGoal(this, 1.0D, 50);

        this.goalSelector.addGoal(0, new FlamingoEntity.SwimInDeepWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredients.FLAMINGO_FOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.2D));
        this.goalSelector.addGoal(5, new FlamingoEntity.LiftLegsGoal(this, 15));
        this.goalSelector.addGoal(5, this.randomWalkingGoal);
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, FlamingoEntity.class, 8.0F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LEFT_LEG_UP, false);
        builder.define(RIGHT_LEG_UP, false);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(LivingThingsTags.FLAMINGO_FOOD);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return FlamingoConfig.maxSpawnedInChunk();
    }

    public boolean isLeftLegUp() {
        return this.entityData.get(LEFT_LEG_UP);
    }

    public boolean isRightLegUp() {
        return this.entityData.get(RIGHT_LEG_UP);
    }

    public void setLeftLegUp(boolean up) {
        this.entityData.set(LEFT_LEG_UP, up);
    }

    public void setRightLegUp(boolean up) {
        this.entityData.set(RIGHT_LEG_UP, up);
    }

    @Override
    public ResourceLocation getLexiconEntry() {
        return LexiconEntries.FLAMINGO;
    }

    static class LiftLegsGoal extends Goal {

        private final FlamingoEntity flamingo;
        private final int chance;
        private int rightLegCounter;
        private int leftLegCounter;

        public LiftLegsGoal(FlamingoEntity flamingo, int chance) {
            this.flamingo = flamingo;
            this.chance = chance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.flamingo.getRandom().nextInt(this.chance) != 0) {
                return false;
            }
            return !this.flamingo.isRightLegUp() && !this.flamingo.isLeftLegUp() && this.flamingo.getNavigation().isDone();
        }

        @Override
        public boolean canContinueToUse() {
            return this.flamingo.getNavigation().isDone() && (this.flamingo.isRightLegUp() || this.flamingo.isLeftLegUp());
        }

        @Override
        public void stop() {
            this.leftLegCounter = 0;
            this.rightLegCounter = 0;
            this.flamingo.setLeftLegUp(false);
            this.flamingo.setRightLegUp(false);
        }

        @Override
        public void tick() {
            if (this.leftLegCounter > 0) {
                this.leftLegCounter--;
            } else {
                this.leftLegCounter = 0;
                this.flamingo.setLeftLegUp(false);
            }

            if (this.rightLegCounter > 0) {
                this.rightLegCounter--;
            } else {
                this.rightLegCounter = 0;
                this.flamingo.setRightLegUp(false);
            }

            if (this.rightLegCounter == 0 && this.leftLegCounter == 0 && Math.random() < 0.1F) {
                if (Math.random() < 0.5F) {
                    this.leftLegCounter = 500;
                    this.flamingo.setLeftLegUp(true);
                } else {
                    this.rightLegCounter = 500;
                    this.flamingo.setRightLegUp(true);
                }
            }

        }

    }

    static class SwimInDeepWaterGoal extends FloatGoal {

        private final FlamingoEntity flamingo;

        public SwimInDeepWaterGoal(FlamingoEntity entityIn) {
            super(entityIn);
            this.flamingo = entityIn;
        }

        @Override
        public boolean canUse() {
            return this.flamingo.wasEyeInWater && super.canUse();
        }

        @Override
        public void tick() {
            // find a new position on land
            this.flamingo.randomWalkingGoal.trigger();
            // start swimming
            super.tick();
        }

    }

    private static class DeepWaterAvoidingRandomWalkingGoal extends RandomStrollGoal {

        private final FlamingoEntity flamingo;

        public DeepWaterAvoidingRandomWalkingGoal(FlamingoEntity creatureIn, double speedIn, int chance) {
            super(creatureIn, speedIn, chance);
            this.flamingo = creatureIn;
        }

        @Override
        protected Vec3 getPosition() {
            if (this.flamingo.wasEyeInWater) {
                Vec3 vector3d = LandRandomPos.getPos(this.mob, 15, 7);
                return vector3d == null ? super.getPosition() : vector3d;
            } else {
                return LandRandomPos.getPos(this.mob, 10, 7);
            }
        }

    }

}
