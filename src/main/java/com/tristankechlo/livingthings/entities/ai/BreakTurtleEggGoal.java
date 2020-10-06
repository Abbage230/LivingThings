package com.tristankechlo.livingthings.entities.ai;

import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.BreakBlockGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BreakTurtleEggGoal extends BreakBlockGoal {
	
	public BreakTurtleEggGoal(CreatureEntity creatureIn, double speed, int yMax) {
		super(Blocks.TURTLE_EGG, creatureIn, speed, yMax);
    }

    public void playBreakingSound(IWorld worldIn, BlockPos pos) {
    	worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + this.creature.getRNG().nextFloat() * 0.2F);
    }

    public void playBrokenSound(World worldIn, BlockPos pos) {
    	 worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + worldIn.rand.nextFloat() * 0.2F);
    }

    public double getTargetDistanceSq() {
    	return 1.25D;
    }
  }
