package org.kilocraft.essentials.config.main.sections.chat;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Objects;

@ConfigSerializable
public class ChatPingSoundConfigSection {

    @Setting(value = "enabled")
    @Comment("Enable or disable the Chat Ping Sound")
    public boolean enabled = true;

    @Setting(value = "id")
    @Comment("Sound identifier, you can search for them through the '/playsound' command in the game!")
    public String id;

    @Setting(value = "volume")
    @Comment("The volume of the sound, Can be between 0 and 3")
    public double volume;

    @Setting(value = "pitch")
    @Comment("Pitch of the sound, can be between 0 and 3, Default: 1.0")
    public double pitch;

    public ChatPingSoundConfigSection() {
        this.id = Objects.requireNonNull(Registry.SOUND_EVENT.getId(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP)).toString();
        this.volume = 3.0;
        this.pitch = 1.0;
    }

    public ChatPingSoundConfigSection(SoundEvent event, double volume, double pitch) {
        Identifier identifier = Registry.SOUND_EVENT.getId(event);
        this.id = identifier != null ? identifier.toString() : "entity.experience_orb.pickup";
        this.volume = volume;
        this.pitch = pitch;
    }
}
