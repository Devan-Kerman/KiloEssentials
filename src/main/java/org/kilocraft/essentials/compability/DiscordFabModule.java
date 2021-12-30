package org.kilocraft.essentials.compability;

import com.github.hansi132.discordfab.discordbot.DiscordFab;
import com.github.hansi132.discordfab.discordbot.DiscordFabMod;
import com.github.hansi132.discordfab.discordbot.api.DiscordFabAPI;
import com.github.hansi132.discordfab.discordbot.api.Field;
import com.github.hansi132.discordfab.discordbot.api.events.AdvancedDiscordAlertEvent;
import com.github.hansi132.discordfab.discordbot.api.events.DiscordMessageEvent;
import com.github.hansi132.discordfab.discordbot.api.events.MinecraftMessageEvent;
import com.github.hansi132.discordfab.discordbot.util.MinecraftAvatar;
import com.mojang.authlib.GameProfile;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.api.ModConstants;
import org.kilocraft.essentials.api.text.ComponentText;
import org.kilocraft.essentials.api.user.CommandSourceUser;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.api.util.EntityIdentifiable;
import org.kilocraft.essentials.chat.ServerChat;
import org.kilocraft.essentials.events.ChatEvents;
import org.kilocraft.essentials.events.PunishEvents;
import org.kilocraft.essentials.util.TimeDifferenceUtil;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.ChatType;

public class DiscordFabModule implements DiscordFabAPI {

    private static final String STAFF_REPORTS_CHANNEL_ID = "staff_reports";
    private static final String STAFF_FLAGGED_MESSAGES = "staff_flagged";
    private static final String STAFF_CHANNEL_ID = "staff";

    private static boolean handleDiscordMessage(String minecraftChannelId, String name, UUID sender, String message) {
        if (minecraftChannelId.equals(STAFF_CHANNEL_ID)) {
            ServerChat.Channel.STAFF.send(ComponentText.toText(ModConstants.translation("compability.discordfab.chat.staff", name, message)), ChatType.CHAT, sender);
            return true;
        } else if (minecraftChannelId.equals(DiscordFabMod.PUBLIC_CHANNEL_ID)){
            ServerChat.Channel.PUBLIC.send(ComponentText.toText(ModConstants.translation("compability.discordfab.chat.public", name, message)), ChatType.CHAT, sender);
            return true;
        } else {
            // If we return true, discordfab will see this message as handled and won't send it to minecraft
            return KiloEssentials.getUserManager().getMutedPlayerList().isMuted(new GameProfile(sender, null));
        }
    }

    private static void sendStaffReport(String title, CommandSourceUser source, EntityIdentifiable victim, String reason, long expiry, Color color) {
        String thumbnailUrl = MinecraftAvatar.generateUrl(
                victim.getId(),
                MinecraftAvatar.RenderType.BODY,
                MinecraftAvatar.RenderType.Model.DEFAULT,
                256,
                6,
                true
        );
        Field reasonField = new Field("Reason:", reason);
        final Date expireDate = new Date(expiry + 1000);
        Field timeField = new Field("Time:", expiry > 0 ? TimeDifferenceUtil.formatDateDiff(new Date(), expireDate) : "Permanent");
        Field expiryField = new Field("Expiry:", expiry > 0 ? expireDate.toString() : "Never");
        Field nameField = new Field("Name:", victim.getName());
        Field uuidField = new Field("UUID:", victim.getId().toString());
        final UUID uuid = source.getUuid();
        AdvancedDiscordAlertEvent.EVENT.invoker().onAlert(STAFF_REPORTS_CHANNEL_ID, title, null, source.getName(), uuid != null ? DiscordFab.getInstance().getChatSynchronizer().getMCAvatarURL(uuid) : null, thumbnailUrl, color, reasonField, timeField, expiryField, nameField, uuidField);
    }

    private static void sendFlaggedMessageReport(OnlineUser sender, final String input, final List<String> flagged) {
        if (flagged.isEmpty()) return;

        Field messageField = new Field("Message:", input);
        Field flaggedField = new Field("Flagged:", getFlaggedMessage(flagged));
        AdvancedDiscordAlertEvent.EVENT.invoker().onAlert(STAFF_FLAGGED_MESSAGES, "Flagged Message", null, sender.getName(), DiscordFab.getInstance().getChatSynchronizer().getMCAvatarURL(sender.getUuid()), null, Color.ORANGE, messageField, flaggedField);
    }

    private static String getFlaggedMessage(final List<String> flagged) {
        if (flagged.size() > 1) {
            final String commaSeparated = String.join(", ", flagged.subList(0, flagged.size() - 1));
            return commaSeparated + " and " + flagged.get(flagged.size() - 1);
        } else {
            return flagged.get(0);
        }
    }

    @Override
    public void onInitialize(DiscordFab discordFab) {
        PunishEvents.BAN.register((source, victim, reason, ipBan, expiry, silent) -> {
            String title = (expiry > 0 ? "Temporary" : "Permanent") + " " + (ipBan ? "IpBan" : "Ban");
            sendStaffReport(title, source, victim, reason, expiry, Color.RED);
        });
        PunishEvents.MUTE.register((source, victim, reason, expiry, silent) -> {
            String title = (expiry > 0 ? "Temporary" : "Permanent") + " Mute";
            sendStaffReport(title, source, victim, reason, expiry, Color.YELLOW);
        });
        ChatEvents.FLAGGED_MESSAGE.register(DiscordFabModule::sendFlaggedMessageReport);
        ChatEvents.CHAT_MESSAGE.register((player, message, channel) -> {
            MinecraftMessageEvent.EVENT.invoker().onMessage(channel.getId(), player.getUUID(), message);
        });
        DiscordMessageEvent.EVENT.register(DiscordFabModule::handleDiscordMessage);
    }
}
