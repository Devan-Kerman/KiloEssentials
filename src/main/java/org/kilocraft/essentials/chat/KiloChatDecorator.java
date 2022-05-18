package org.kilocraft.essentials.chat;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.api.ModConstants;
import org.kilocraft.essentials.api.text.ComponentText;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.util.EssentialPermission;
import org.kilocraft.essentials.util.registry.RegistryUtils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KiloChatDecorator implements ChatDecorator {

    private static final int LINK_MAX_LENGTH = 20;
    private static final String ITEM_REGEX = "\\[item\\]";
    private static final String URL_REGEX = "(?:https?:\\/\\/)?(?:www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)[-a-zA-Z0-9()@:%_\\+.~#?&//=]*";
    private static final String EVERYONE_REGEX = "@everyone";
    private static final String HEART_REGEX = "(<3)|♥";

    // TODO: Why is it CompletableFuture<Component> now
    @Override
    public CompletableFuture<Component> decorate(@Nullable ServerPlayer serverPlayer, @NotNull Component component) {
        if (serverPlayer == null) return CompletableFuture.completedFuture(component);
        return CompletableFuture.completedFuture(ComponentText.toText(this.parse(serverPlayer, component.getString().trim())));
    }


    private net.kyori.adventure.text.Component parse(ServerPlayer player, final String input) {
        TextComponent.Builder builder = net.kyori.adventure.text.Component.text();
        if (input == null || input.equals("")) return builder.build();
        Matcher itemMatcher = Pattern.compile(ITEM_REGEX).matcher(input);
        Matcher urlMatcher = Pattern.compile(URL_REGEX).matcher(input);
        Matcher everyoneMatcher = Pattern.compile(EVERYONE_REGEX).matcher(input);
        Matcher hearthMatcher = Pattern.compile(HEART_REGEX).matcher(input);
        for (String censored : ModConstants.getCensored()) {
            Matcher censoredMatcher = Pattern.compile("(?i)" + censored).matcher(input);
            if (censoredMatcher.find()) {
                this.parseMatcher(player, builder, input, censoredMatcher, this::censoredComponent);
                return builder.build();
            }
        }
        if (itemMatcher.find() && this.hasPermission(player, EssentialPermission.CHAT_SHOW_ITEM)) {
            this.parseMatcher(player, builder, input, itemMatcher, s -> this.itemComponent(player));
        } else if (urlMatcher.find() && this.hasPermission(player, EssentialPermission.CHAT_URL)) {
            this.parseMatcher(player, builder, input, urlMatcher, s -> this.urlComponent(s, urlMatcher.group(1)));
        } else if (everyoneMatcher.find() && this.hasPermission(player, EssentialPermission.CHAT_PING_EVERYONE)) {
            this.parseMatcher(player, builder, input, everyoneMatcher, s -> this.everyoneComponent());
        } else if (hearthMatcher.find()) {
            this.parseMatcher(player, builder, input, hearthMatcher, s -> this.hearthComponent());
        } else {
            if (this.hasPermission(player, EssentialPermission.CHAT_PING_OTHER)) {
                for (final OnlineUser user : KiloEssentials.getUserManager().getOnlineUsersAsList()) {
                    if (!user.hasPermission(EssentialPermission.CHAT_GET_PINGED)) continue;
                    Matcher userNameMatcher = Pattern.compile(user.getUsername()).matcher(input);
                    if (userNameMatcher.find()) {
                        this.parseMatcher(player, builder, input, userNameMatcher, s -> this.pingUserComponent(user));
                        //this.pinged.add(user.getUuid());
                        return builder.build();
                    }
                }
            }
            builder.append(ComponentText.of(input));
        }
        return builder.build();
    }

    private boolean hasPermission(ServerPlayer player, EssentialPermission permission) {
        return KiloEssentials.hasPermissionNode(player.createCommandSourceStack(), permission);
    }

    private void parseMatcher(ServerPlayer player, TextComponent.Builder builder, String input, Matcher matcher, Function<String, net.kyori.adventure.text.Component> function) {
        String prefix = input.substring(0, Math.max(0, matcher.start()));
        String matched = matcher.group(0);
        String suffix = input.substring(Math.min(matcher.end(), input.length()));
        builder.append(this.parse(player, prefix));
        builder.append(function.apply(matched));
        builder.append(this.parse(player, suffix));
    }

    private net.kyori.adventure.text.Component itemComponent(@Nullable ServerPlayer serverPlayer) {
        TextComponent.Builder builder = net.kyori.adventure.text.Component.text();
        if (serverPlayer != null) {
            ItemStack itemStack = serverPlayer.getMainHandItem();
            CompoundTag tag = itemStack.getTag();
            builder.append(net.kyori.adventure.text.Component.text("["))
                    .append(ComponentText.toComponent(itemStack.getHoverName()))
                    .append(net.kyori.adventure.text.Component.text("]"));
            builder.style(
                    style -> style
                            .hoverEvent(
                                    HoverEvent.showItem(
                                            Key.key(RegistryUtils.toIdentifier(itemStack.getItem())), 1,
                                            BinaryTagHolder.of(tag == null ? new CompoundTag().toString() : tag.toString())
                                    )
                            )
            );
        }
        return builder.build();
    }

    private net.kyori.adventure.text.Component urlComponent(final String wholeUrl, final String mainUrl) {
        TextComponent.Builder builder = net.kyori.adventure.text.Component.text();
        String shortenedUrl = mainUrl.substring(0, Math.min(mainUrl.length(), LINK_MAX_LENGTH));
        if (mainUrl.length() > LINK_MAX_LENGTH) {
            builder.content(shortenedUrl + "..." + mainUrl.substring(mainUrl.length() - 5) + " ");
        } else {
            builder.append(ComponentText.of(mainUrl));
        }
        builder.color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC, TextDecoration.UNDERLINED);
        builder.style(
                style -> style
                        .clickEvent(ClickEvent.openUrl(wholeUrl))
                        .hoverEvent(HoverEvent.showText(
                                        net.kyori.adventure.text.Component.text(wholeUrl).color(NamedTextColor.WHITE)
                                                .append(net.kyori.adventure.text.Component.text("\nClick to open!").color(NamedTextColor.AQUA))
                                )
                        )
        );
        return builder.build();
    }

    private net.kyori.adventure.text.Component everyoneComponent() {
        //this.pinged.addAll(KiloEssentials.getUserManager().getOnlineUsersAsList().stream().map(User::getUuid).collect(Collectors.toList()));
        return net.kyori.adventure.text.Component.text("@everyone").color(NamedTextColor.AQUA);
    }

    private net.kyori.adventure.text.Component censoredComponent(String input) {
        String censored = "*".repeat(input.length());
        //this.flagged.add(input);
        return net.kyori.adventure.text.Component.text(censored).style(style ->
                style.hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text(input).color(NamedTextColor.GRAY)))
        );
    }

    private net.kyori.adventure.text.Component hearthComponent() {
        return net.kyori.adventure.text.Component.text("♥").color(NamedTextColor.RED);
    }

    private net.kyori.adventure.text.Component pingUserComponent(final OnlineUser user) {
        if (user.getNickname().isPresent()) {
            return ComponentText.of(user.getNickname().get());
        } else {
            return net.kyori.adventure.text.Component.text(user.getName()).color(NamedTextColor.GREEN).decorate(TextDecoration.ITALIC);
        }
    }

}
