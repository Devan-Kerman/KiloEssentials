package org.kilocraft.essentials.util;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.kilocraft.essentials.mixin.accessor.StoredUserEntryAccessor;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;

import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;

public class MutedPlayerList extends StoredUserList<GameProfile, MutedPlayerEntry> {
    public MutedPlayerList(File file) {
        super(file);
    }

    @Override
    protected StoredUserEntry<GameProfile> createEntry(@NotNull JsonObject jsonObject) {
        return new MutedPlayerEntry(jsonObject);
    }

    public boolean isMuted(GameProfile gameProfile) {
        return this.contains(gameProfile);
    }

    @SuppressWarnings("unchecked")
    public String[] getUserList() {
        return this.getEntries().stream().map(entry -> ((StoredUserEntryAccessor<GameProfile>) entry).getUser()).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
    }

    @Override
    protected String getKeyForUser(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    public String toString(final GameProfile profile) {
        return profile.getId().toString();
    }

}
