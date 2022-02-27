package org.kilocraft.essentials.extensions.homes.api;

import net.minecraft.nbt.CompoundTag;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.api.world.location.Location;
import org.kilocraft.essentials.api.world.location.Vec3dLocation;
import org.kilocraft.essentials.util.LocationUtil;

import java.util.UUID;

public class Home {
    private UUID owner_uuid;
    private String name;
    private Location location;

    public Home(UUID uuid, String name, Location location) {
        this.owner_uuid = uuid;
        this.name = name;
        this.location = location;
    }

    public Home() {
    }

    public Home(CompoundTag tag) {
        this.fromTag(tag);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("loc", this.location.toTag());
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (this.location == null) this.location = Vec3dLocation.dummy();
        this.location.fromTag(tag.getCompound("loc"));
    }

    public UUID getOwner() {
        return this.owner_uuid;
    }

    public void setOwner(UUID uuid) {
        this.owner_uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return this.location;
    }

    public void teleportTo(OnlineUser user) {
        user.saveLocation();
        user.teleport(this.location, true);
    }

    public boolean shouldTeleport() {
        return LocationUtil.shouldBlockAccessTo(this.location.getDimension());
    }

}
