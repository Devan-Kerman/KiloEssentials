package org.kilocraft.essentials.craft.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.MinecraftVersion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.kilocraft.essentials.api.Mod;
import org.kilocraft.essentials.api.chat.LangText;

import java.util.ArrayList;
import java.util.List;

public class VersionCommand {
	
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        List<String> list = new ArrayList<String>(){{
            add("version");
            add("info");
            add("kiloessentials");
        }};

        list.forEach((name) -> dispatcher.register(
                CommandManager.literal(name).executes(context -> {
                    context.getSource().sendFeedback(
                            LangText.getFormatter(
                                    true,
                                    "commands.version.info",
                                    Mod.getMinecraftVersion(),
                                    Mod.getLoaderVersion(),
                                    Mod.getMappingsVersion(),
                                    Mod.getVersion()
                            ), false);
                    return 1;
                })
        ));

    }
    
}