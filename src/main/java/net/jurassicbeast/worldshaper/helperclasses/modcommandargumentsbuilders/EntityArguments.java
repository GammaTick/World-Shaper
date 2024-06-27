package net.jurassicbeast.worldshaper.helperclasses.modcommandargumentsbuilders;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.jurassicbeast.worldshaper.command.EntityControls;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.entity.EntityPose;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EntityArguments {
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
                        .then(registerEntityPoses()));
    }

    private static  LiteralArgumentBuilder<ServerCommandSource> registerEntityPoses() {
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
}
