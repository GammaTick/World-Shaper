package net.jurassicbeast.worldshaper.arguments.entityarguments.entity;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class AllayEntityArguments {
    static AllayEntity allayEntity1;
    static boolean dancing = false;

    public static CompletableFuture<Suggestions> getAvailableArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("triggerDance");

        return builder.buildFuture();
    }

    public static RequiredArgumentBuilder<ServerCommandSource, Integer> getArguments2() {
        return CommandManager.argument("duration", IntegerArgumentType.integer(0));
    }

    public static int runAction(CommandContext<ServerCommandSource> context, AllayEntity allayEntity) {
        String action = StringArgumentType.getString(context, "action");
        ServerWorld serverWorld = context.getSource().getWorld();

        switch (action) {
            case "triggerDance": return triggerDance(context, allayEntity);
        }

        return 0;
    }

    private static int triggerDance(CommandContext<ServerCommandSource> context, AllayEntity allayEntity) {
        allayEntity1 = allayEntity;
        allayEntity.setDancing(true);
        dancing = true;

        context.getSource().sendFeedback(() -> Text.literal("Successfully made " + allayEntity.getDisplayName().getString() + " to dance"), true);

        return 1;
    }

    public static void tickAllayActions() {
        if (allayEntity1 != null && dancing && !allayEntity1.isDancing()) {
            allayEntity1.setDancing(true);
        }
    }
}
