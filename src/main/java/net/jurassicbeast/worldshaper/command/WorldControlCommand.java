package net.jurassicbeast.worldshaper.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RotationArgumentType;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WorldControlCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("shapeWorld").requires(source -> source.hasPermissionLevel(2))
                .then(literal("entity")
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
                                .then(literal("setPose")
                                        .then(literal("Standing")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 0)))
                                        .then(literal("FallFlying")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 1)))
                                        .then(literal("Sleeping")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 2)))
                                        .then(literal("Swimming")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 3)))
                                        .then(literal("SpinAttack")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 4)))
                                        .then(literal("Crouching")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 5)))
                                        .then(literal("LongJumping")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 6)))
                                        .then(literal("Dying")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 7)))
                                        .then(literal("Croaking")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 8)))
                                        .then(literal("UsingTongue")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 9)))
                                        .then(literal("Sitting")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 10)))
                                        .then(literal("Roaring")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 11)))
                                        .then(literal("Sniffing")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 12)))
                                        .then(literal("Emerging")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 13)))
                                        .then(literal("Digging")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 14)))
                                        .then(literal("Sliding")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 15)))
                                        .then(literal("Shooting")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 16)))
                                        .then(literal("Inhaling")
                                                .executes(context -> EntityControls.setEntityPose(context, EntityArgumentType.getEntity(context, "entity"), (byte) 17))))))));
    }
}