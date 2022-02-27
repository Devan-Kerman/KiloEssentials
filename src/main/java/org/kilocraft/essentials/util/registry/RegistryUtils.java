package org.kilocraft.essentials.util.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.api.util.StringUtils;

import java.util.Objects;
import java.util.Set;

public class RegistryUtils {

    private static final MinecraftServer server = KiloEssentials.getMinecraftServer();

    public static String dimensionToName(final ResourceKey<Level> resourceKey) {
        if (resourceKey == null) {
            return "Unknown";
        } else {
            return resourceKey == Level.OVERWORLD ? "Overworld"
                    : resourceKey == Level.NETHER ? "The Nether"
                    : resourceKey == Level.END ? "The End"
                    : StringUtils.normalizeCapitalization(resourceKey.location().getPath());
        }
    }

    public static Set<ResourceKey<Level>> getWorldsKeySet() {
        return server.levelKeys();
    }

    public static String toIdentifier(@NotNull Item item) {
        return Registry.ITEM.getKey(item).toString();
    }

    @Nullable
    public static Item toItem(@NotNull String item) {
        return Registry.ITEM.get(new ResourceLocation(item));
    }

}
