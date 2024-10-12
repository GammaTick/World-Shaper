package net.jurassicbeast.worldshaper.arguments.entityarguments.entity;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.CompletableFuture;

public class IronGolemEntityArguments {
    public static CompletableFuture<Suggestions> getAvailableArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {


        return builder.buildFuture();
    }

    public static int runAction(CommandContext<ServerCommandSource> context, IronGolemEntity ironGolemEntity) {
        String action = StringArgumentType.getString(context, "action");
        ServerWorld serverWorld = context.getSource().getWorld();

        switch (action) {

        }

        return 0;
    }
}
