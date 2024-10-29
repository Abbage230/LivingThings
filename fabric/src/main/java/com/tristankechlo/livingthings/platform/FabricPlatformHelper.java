package com.tristankechlo.livingthings.platform;

import com.google.auto.service.AutoService;
import com.tristankechlo.livingthings.entity.SeahorseEntity;
import com.tristankechlo.livingthings.init.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluid;

import java.nio.file.Path;
import java.util.function.Supplier;

@AutoService(IPlatformHelper.class)
public final class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public CreativeModeTab getCreativeModeTab() {
        return FabricItemGroup.GENERAL;
    }

    @Override
    public Component getPatchouliSubtitle(ResourceLocation bookId) {
        return vazkii.patchouli.api.PatchouliAPI.get().getSubtitle(bookId);
    }

    @Override
    public void openBookEntry(ResourceLocation bookId, ResourceLocation entryId, int page) {
        vazkii.patchouli.api.PatchouliAPI.get().openBookEntry(bookId, entryId, page);
    }

    @Override
    public void openBookGui(ServerPlayer player, ResourceLocation bookId) {
        vazkii.patchouli.api.PatchouliAPI.get().openBookGUI(player, bookId);
    }

    @Override
    public MobBucketItem createMobBucketItem(RegistryObject<EntityType<SeahorseEntity>> type, Fluid fluid, SoundEvent sound, Item.Properties props) {
        return new MobBucketItem(type.get(), fluid, sound, props);
    }

    @Override
    public SpawnEggItem createSpawnEgg(Supplier<EntityType<?>> type, int primaryColor, int secondaryColor, Item.Properties props) {
        return new SpawnEggItem((EntityType<? extends Mob>) type.get(), primaryColor, secondaryColor, props);
    }

}
