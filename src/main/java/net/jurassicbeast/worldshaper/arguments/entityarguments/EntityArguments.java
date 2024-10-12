package net.jurassicbeast.worldshaper.arguments.entityarguments;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.jurassicbeast.worldshaper.arguments.entityarguments.entity.AllayEntityArguments;
import net.jurassicbeast.worldshaper.arguments.entityarguments.entity.RaiderEntityArguments;
import net.jurassicbeast.worldshaper.arguments.entityarguments.entity.VillagerEntityArguments;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.*;

public class EntityArguments {
    private static CommandContext<ServerCommandSource> globalContext;

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("entity")
                .then(argument("entity", EntityArgumentType.entity())
                        .then(literal("rotate")
                                .then(argument("rotation", RotationArgumentType.rotation())
                                        .executes(context -> EntityControls.rotate(context, EntityArgumentType.getEntity(context, "entity"), RotationArgumentType.getRotation(context, "rotation")))))
                        .then(literal("startPathfindingTo")
                                .then(argument("position", BlockPosArgumentType.blockPos())
                                        .executes(context -> EntityControls.startPathfindingTo(context, EntityArgumentType.getEntity(context, "entity"), BlockPosArgumentType.getBlockPos(context, "position"), 1.0f, 1.0f))
                                        .then(argument("proximityDistance", FloatArgumentType.floatArg())
                                                .executes(context -> EntityControls.startPathfindingTo(context, EntityArgumentType.getEntity(context, "entity"), BlockPosArgumentType.getBlockPos(context, "position"), FloatArgumentType.getFloat(context, "proximityDistance"), 1.0f))
                                                .then(argument("mobSpeed", FloatArgumentType.floatArg())
                                                        .executes(context -> EntityControls.startPathfindingTo(context, EntityArgumentType.getEntity(context, "entity"), BlockPosArgumentType.getBlockPos(context, "position"), FloatArgumentType.getFloat(context, "proximityDistance"), FloatArgumentType.getFloat(context, "mobSpeed")))))))
                        .then(literal("stopPathfinding")
                                .executes(context -> EntityControls.stopPathfinding(context, EntityArgumentType.getEntity(context, "entity"))))
                        .then(literal("startFollowingEntity")
                                .then(argument("targetEntity", EntityArgumentType.entity())
                                        .executes(context -> EntityControls.startFollowingEntity(context, EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "targetEntity"), 1.0f, 1.0f))
                                        .then(argument("proximityDistance", FloatArgumentType.floatArg())
                                                .executes(context -> EntityControls.startFollowingEntity(context, EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "targetEntity"), FloatArgumentType.getFloat(context, "proximityDistance"), 1.0f))
                                                .then(argument("mobSpeed", FloatArgumentType.floatArg())
                                                        .executes(context -> EntityControls.startFollowingEntity(context, EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "targetEntity"), FloatArgumentType.getFloat(context, "proximityDistance"), FloatArgumentType.getFloat(context, "mobSpeed")))))))
                        .then(literal("stopFollowingEntity")
                                .executes(context -> EntityControls.stopFollowingEntity(context, EntityArgumentType.getEntity(context, "entity"))))
                        .then(argument("action", StringArgumentType.word())
                                .suggests(EntityArguments::registerCustomEntityArguments)
                                .executes(EntityArguments::executeCustomEntityArgument)));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> registerEntityPoses() {
        LiteralArgumentBuilder<ServerCommandSource> entityPosesArgument = CommandManager.literal("setPose");
        byte poseCount = 0;

        for (EntityPose pose : EntityPose.values()) {
            final byte finalPoseCount = poseCount;

            entityPosesArgument = entityPosesArgument.then(literal("entityPose:" + formatPoseName(pose.name()))
                    .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), finalPoseCount)));

            poseCount++;
        }

        return entityPosesArgument;
    }

    private static String formatPoseName(String poseName) {
        StringBuilder formattedName = new StringBuilder();
        boolean toUpperCase = false;

        for (char ch : poseName.toCharArray()) {
            if (ch == '_') {
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    formattedName.append(Character.toUpperCase(ch));
                    toUpperCase = false;
                } else {
                    formattedName.append(Character.toLowerCase(ch));
                }
            }
        }

        return formattedName.toString();
    }

    private static CompletableFuture<Suggestions> registerCustomEntityArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Entity entity;

        try {
            entity = EntityArgumentType.getEntity(context, "entity");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        if (entity instanceof VillagerEntity) {
            return VillagerEntityArguments.getAvailableArguments(context, builder);
        }

        if (entity instanceof RaiderEntity) {
            return RaiderEntityArguments.getAvailableArguments(context, builder);
        }

        if (entity instanceof AllayEntity) {
            return AllayEntityArguments.getAvailableArguments(context, builder);
        }

        return builder.buildFuture();
    }

    private static int executeCustomEntityArgument(CommandContext<ServerCommandSource> context) {
        Entity entity;

        try {
            entity = EntityArgumentType.getEntity(context, "entity");
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        if (entity instanceof VillagerEntity villagerEntity) {
            return VillagerEntityArguments.runAction(context, villagerEntity);
        }

        if (entity instanceof RaiderEntity raiderEntity) {
            return RaiderEntityArguments.runAction(context, raiderEntity);
        }

        if (entity instanceof AllayEntity allayEntity) {
            return AllayEntityArguments.runAction(context, allayEntity);
        }

        return 0;
    }
}
