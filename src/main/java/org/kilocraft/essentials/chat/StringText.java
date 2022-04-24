package org.kilocraft.essentials.chat;

import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kilocraft.essentials.api.ModConstants;
import org.kilocraft.essentials.api.text.ComponentText;

public class StringText {

    public static MutableComponent of(@NotNull final String key, @Nullable final Object... objects) {
        final String translated = ModConstants.translation(key, objects);
        return ComponentText.toText(translated);
    }

}
