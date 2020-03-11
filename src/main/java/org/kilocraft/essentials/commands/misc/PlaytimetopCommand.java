package org.kilocraft.essentials.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import org.kilocraft.essentials.CommandPermission;
import org.kilocraft.essentials.api.ModConstants;
import org.kilocraft.essentials.api.command.EssentialCommand;
import org.kilocraft.essentials.api.text.TextInput;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.api.user.User;
import org.kilocraft.essentials.api.util.Cached;
import org.kilocraft.essentials.util.CacheManager;
import org.kilocraft.essentials.util.Pager;
import org.kilocraft.essentials.util.TimeDifferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class PlaytimetopCommand extends EssentialCommand {
    private static final String CACHE_ID = "command.playtimetop";
    private static final String TICKS_CACHE__ID = "command.playtimetop.ticks";

    public PlaytimetopCommand() {
        super("playtimetop", CommandPermission.PLAYTIMETOP, new String[]{"pttop", "topplaytimes"});
    }

    @Override
    public void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        final RequiredArgumentBuilder<ServerCommandSource, Integer> page = argument("page", IntegerArgumentType.integer(0))
                .executes(ctx -> this.send(ctx, IntegerArgumentType.getInteger(ctx, "page"), false));

        final LiteralArgumentBuilder<ServerCommandSource> force = literal("force")
                .executes(ctx -> this.send(ctx, IntegerArgumentType.getInteger(ctx, "page"), true));

        page.then(force);
        this.argumentBuilder.executes(this::execute);
        this.commandNode.addChild(page.build());
    }

    private int execute(final CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return this.send(ctx, 1, false);
    }

    private int send(final CommandContext<ServerCommandSource> ctx, int page, boolean force) throws CommandSyntaxException {
        final OnlineUser src = this.getOnlineUser(ctx);

        if (!force && CacheManager.shouldUse(CACHE_ID)) {
            AtomicReference<List<Map.Entry<String, Integer>>> sortedList = new AtomicReference<>();
            AtomicLong totalTicks = new AtomicLong();

            CacheManager.getAndRun(CACHE_ID, (cached) -> sortedList.set((List<Map.Entry<String, Integer>>) cached.get()));
            CacheManager.getAndRun(TICKS_CACHE__ID, (cached) -> totalTicks.set((Long) cached.get()));

            if (sortedList.get() != null) {
                return send(src, page, sortedList.get(), totalTicks.get());
            }
        }

        this.essentials.getAllUsersThenAcceptAsync(src, "general.wait_users", list -> {
            final HashMap<String, Integer> map = new HashMap<>();
            long totalTicks = 0L;

            for (User user : list) {
                map.put(user.getFormattedDisplayName(), user.getTicksPlayed());
                totalTicks += user.getTicksPlayed();
            }

            final List<Map.Entry<String, Integer>> sorted = new ArrayList<>(map.entrySet());
            sorted.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

            Cached listCached = new Cached(CACHE_ID, sorted);
            Cached ticksCached = new Cached(TICKS_CACHE__ID, totalTicks);
            CacheManager.cache(listCached, ticksCached);

            send(src, page, sorted, totalTicks);
        });

        return SINGLE_SUCCESS;
    }

    private static int send(OnlineUser src, int page, List<Map.Entry<String, Integer>> sortedList, long totalTicks) {
        final String LINE_FORMAT = ModConstants.translation("command.playtimetop.format");
        int rank = 0;

        TextInput input = new TextInput(
                ModConstants.translation("command.playtimetop.total", TimeDifferenceUtil.convertSecondsToString((int) (totalTicks / 20L), 'e', '6'))
        );

        for (int i = 0; i < sortedList.size(); i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);

            if (entry.getKey().equalsIgnoreCase(src.getFormattedDisplayName())) {
                rank = i;
            }

            String pt = TimeDifferenceUtil.convertSecondsToString(entry.getValue() / 20, 'e', '6');
            input.append(String.format(LINE_FORMAT, i + 1, entry.getKey(), pt));
        }

        Pager.Page paged = Pager.getPageFromStrings(Pager.Options.builder().setPageIndex(page - 1).build(), input.getLines());

        String pt = TimeDifferenceUtil.convertSecondsToString(src.getTicksPlayed() / 20, 'b', '3');
        paged.setStickyFooter(String.format(ModConstants.translation("command.playtimetop.format.self"), rank + 1, src.getFormattedDisplayName(), pt));
        paged.send(src.getCommandSource(), "Top Play Times", "/playtimetop %page%");
        return SINGLE_SUCCESS;
    }
}