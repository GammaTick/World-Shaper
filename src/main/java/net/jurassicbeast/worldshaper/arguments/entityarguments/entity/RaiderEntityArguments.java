package net.jurassicbeast.worldshaper.arguments.entityarguments.entity;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class RaiderEntityArguments {
    public static int countOfArguments = 2;

    static RaiderEntity raiderEntity1;
    static boolean celeb = false;

    public static CompletableFuture<Suggestions> getAvailableArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {

        builder.suggest("runCelebrateGoal");

        return builder.buildFuture();
    }



    public static int runAction(CommandContext<ServerCommandSource> context, RaiderEntity raiderEntity) {
        String action = StringArgumentType.getString(context, "action");

        switch (action) {
            case "runCelebrateGoal": return runCelebrateGoal(context, raiderEntity);
        }

        return 0;
    }

    private static int runCelebrateGoal(CommandContext<ServerCommandSource> context, RaiderEntity raiderEntity) {
        raiderEntity1 = raiderEntity;
        celeb = true;

        context.getSource().sendFeedback(() -> Text.literal("Successfully triggered celebration goal of " + raiderEntity.getDisplayName().getString()), false);
        return 1;
    }

    public static void tickAllayActions() {
        if (raiderEntity1 != null && celeb) {
            if (!raiderEntity1.isSilent() && raiderEntity1.getRandom().nextInt(50) == 0) {
                raiderEntity1.playSound(raiderEntity1.getCelebratingSound());
            }

            if (!raiderEntity1.hasVehicle() && raiderEntity1.getRandom().nextInt(25) == 0) {
                raiderEntity1.getJumpControl().setActive();
            }

            raiderEntity1.setCelebrating(true);
        }
    }
}
