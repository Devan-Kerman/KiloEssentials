package org.kilocraft.essentials.api.world.location;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.api.util.EntityRotation;
import org.kilocraft.essentials.util.LocationUtil;
import org.kilocraft.essentials.util.player.PlayerRotation;
import org.kilocraft.essentials.util.registry.RegistryUtils;

import java.text.DecimalFormat;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Vec3dLocation implements Location {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DecimalFormat decimalFormat = new DecimalFormat("##.##");
    private double x, y, z;
    private EntityRotation rotation;
    @Nullable
    private ResourceKey<Level> dimension;
    private boolean useShortDecimals = false;

    private Vec3dLocation(double x, double y, double z, float yaw, float pitch, @Nullable ResourceKey<Level> dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = new PlayerRotation(yaw, pitch);
        this.dimension = dimension;
    }

    public static Vec3dLocation of(double x, double y, double z, float yaw, float pitch, @Nullable ResourceKey<Level> dimension) {
        return new Vec3dLocation(x, y, z, yaw, pitch, dimension);
    }

    public static Vec3dLocation of(double x, double y, double z, float yaw, float pitch) {
        return new Vec3dLocation(x, y, z, yaw, pitch, null);
    }

    public static Vec3dLocation of(double x, double y, double z) {
        return new Vec3dLocation(x, y, z, 0.0F, 0.0F, null);
    }

    public static Vec3dLocation of(Vec3 vec3d) {
        return of(vec3d.x(), vec3d.y(), vec3d.z());
    }

    public static Vec3dLocation of(Entity entity) {
        return new Vec3dLocation(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot(), entity.getLevel().dimension());
    }

    public static Vec3dLocation of(OnlineUser user) {
        return of(user.asPlayer());
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Nullable
    @Override
    public ResourceKey<Level> getDimension() {
        return this.dimension;
    }

    @Nullable
    @Override
    public DimensionType getDimensionType() {
        if (this.getWorld() == null) {
            return null;
        } else {
            return this.getWorld().dimensionType();
        }
    }

    @Override
    public EntityRotation getRotation() {
        return this.rotation;
    }

    @Nullable
    @Override
    public ServerLevel getWorld() {
        if (this.dimension == null) {
            return null;
        } else {
            return KiloEssentials.getMinecraftServer().getLevel(this.dimension);
        }
    }

    @Override
    public boolean isSafe() {
        return false;
    }

    @Override
    public boolean isSafeFor(OnlineUser user) {
        return LocationUtil.isBlockSafeFor(user, this);
    }

    @Override
    public boolean isSafeFor(ServerPlayer player) {
        return this.isSafeFor(KiloEssentials.getUserManager().getOnline(player));
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag pos = new CompoundTag();

        if (this.useShortDecimals)
            this.shortDecimals();

        pos.putDouble("x", this.x);
        pos.putDouble("y", this.y);
        pos.putDouble("z", this.z);

        tag.put("pos", pos);

        if (this.dimension != null) {
            ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.dimension.location()).resultOrPartial(LOGGER::error).ifPresent(compoundTag -> tag.put("dim", compoundTag));
        }

        if (!(this.rotation.getYaw() == 0 && this.rotation.getPitch() == 0)) {
            CompoundTag view = new CompoundTag();
            view.putFloat("yaw", this.rotation.getYaw());
            view.putFloat("pitch", this.rotation.getPitch());
            tag.put("view", view);
        }

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        CompoundTag pos = tag.getCompound("pos");

        this.x = pos.getDouble("x");
        this.y = pos.getDouble("y");
        this.z = pos.getDouble("z");

        if (tag.contains("dim")) {
            this.dimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("dim")).resultOrPartial(LOGGER::error).orElse(Level.OVERWORLD);
        }

        if (tag.contains("view")) {
            CompoundTag view = tag.getCompound("view");
            this.rotation = new PlayerRotation(view.getFloat("yaw"), view.getFloat("pitch"));
        }
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        this.rotation = new PlayerRotation(yaw, pitch);
    }

    @Override
    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    @Override
    public ChunkPos toChunkPos() {
        return new ChunkPos(this.toPos());
    }

    @Override
    public BlockPos toPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override
    public Vec3 toVec3d() {
        return new Vec3(this.x, this.y, this.z);
    }

    @Override
    public Vec3i toVec3i() {
        return new Vec3i(this.z, this.y, this.z);
    }

    public static Vec3dLocation dummy() {
        return of(0, 100, 0);
    }

    public void setVector(Vec3 vector) {
        this.x = vector.x();
        this.y = vector.y();
        this.z = vector.z();
    }

    public Vec3dLocation shortDecimals() {
        this.useShortDecimals = true;

        this.x = Double.parseDouble(decimalFormat.format(this.x));
        this.y = Double.parseDouble(decimalFormat.format(this.y));
        this.z = Double.parseDouble(decimalFormat.format(this.z));

        this.setRotation(Float.parseFloat(decimalFormat.format(this.rotation.getYaw())),
                Float.parseFloat(decimalFormat.format(this.rotation.getPitch())));

        return this;
    }

    public Vec3dLocation center() {
        return of(this.x + 0.5D, this.y, this.z + 0.5D, this.rotation.getYaw(), this.rotation.getPitch(), this.dimension);
    }

    @Override
    public String toString() {
        return "x: " + this.x + " y: " + this.y + " z: " + this.z;
    }

    public boolean isUsingShortDecimals() {
        return this.useShortDecimals;
    }
}
