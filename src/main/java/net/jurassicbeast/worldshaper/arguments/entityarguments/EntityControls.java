package net.jurassicbeast.worldshaper.arguments.entityarguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityControls {
    private static final Map<MobEntity, PathfindingData> followingEntities = new HashMap<>();
    private static final Map<MobEntity, PathfindingData> pathfindingEntities = new HashMap<>();
    private static final List<MobEntity> pathfindingEntitiesToRemove = new ArrayList<>();
    private static final List<MobEntity> followingEntitiesToRemove = new ArrayList<>();

    public static int rotate(CommandContext<ServerCommandSource> context, Entity entity, PosArgument rotation) {
        if (entity instanceof PlayerEntity) {
            context.getSource().sendError(Text.literal("Cannot rotate a player"));
            return 0;
        }

        Vec2f rotationVec = rotation.toAbsoluteRotation(context.getSource());
        float yaw = rotationVec.y % 360;
        float pitch = rotationVec.x % 360;

        entity.setYaw(yaw);
        entity.setPitch(pitch);
        entity.prevYaw = yaw;
        entity.prevPitch = pitch;
        entity.setHeadYaw(yaw);

        if (entity instanceof MobEntity mobEntity) {
            mobEntity.headYaw = yaw;
            mobEntity.bodyYaw = yaw;
        }

        context.getSource().sendFeedback(() -> Text.literal("Entity rotated to [" + yaw + ", " + pitch + "]"), true);
        return 1;
    }

    public static int startPathfindingTo(CommandContext<ServerCommandSource> context, Entity entity, BlockPos blockPos, float proximityDistance, float mobSpeed) {
        if (entity instanceof MobEntity mobEntity) {
            pathfindingEntities.put(mobEntity, new PathfindingData(blockPos, proximityDistance, mobSpeed, null));
            context.getSource().sendFeedback(() -> Text.literal("Started pathfinding of " + entity.getDisplayName().getString()), true);
            return 1;
        }

        context.getSource().sendError(Text.literal("Unable to start the pathfinding of " + entity.getDisplayName().getString()));
        return 0;
    }

    public static int stopPathfinding(CommandContext<ServerCommandSource> context, Entity entity) {
        if (entity instanceof MobEntity mobEntity) {
            pathfindingEntitiesToRemove.add(mobEntity);
            context.getSource().sendFeedback(() -> Text.literal("Stopped the pathfinding of " + entity.getDisplayName().getString()), true);
            return 1;
        }

        context.getSource().sendError(Text.literal("Unable to stop the pathfinding of " + entity.getDisplayName().getString()));
        return 0;
    }

    public static int startFollowingEntity(CommandContext<ServerCommandSource> context, Entity followingEntity, Entity targetEntity, float proximityDistance, float mobSpeed) {
        if (followingEntity instanceof MobEntity mobEntity) {
            followingEntities.put(mobEntity, new PathfindingData(null, proximityDistance, mobSpeed, targetEntity));
            context.getSource().sendFeedback(() -> Text.literal(followingEntity.getDisplayName().getString() + " is now following " + targetEntity.getDisplayName().getString()), true);
            return 1;
        }

        context.getSource().sendError(Text.literal("Unable to make " + followingEntity.getDisplayName().getString() + " to follow " + targetEntity.getDisplayName().getString()));
        return 0;
    }

    public static int stopFollowingEntity(CommandContext<ServerCommandSource> context, Entity followingEntity) {
        if (followingEntity instanceof MobEntity mobEntity) {
            followingEntitiesToRemove.add(mobEntity);
            context.getSource().sendFeedback(() -> Text.literal(followingEntity.getDisplayName().getString() + " is not following anything anymore"), true);
            return 1;
        }

        context.getSource().sendError(Text.literal("Unable to stop " + followingEntity.getDisplayName().getString() + " of following an entity"));
        return 0;
    }

    public static int setEntityPose(CommandContext<ServerCommandSource> context, Entity entity, byte poseType) {
        if (!(entity instanceof PlayerEntity) && !(entity instanceof MobEntity)) {
            context.getSource().sendError(Text.literal("Unable to set the pose of " + entity.getDisplayName().getString()));
            return 0;
        }

        EntityPose pose = EntityPose.INDEX_TO_VALUE.apply(poseType);

        entity.setPose(pose);

        context.getSource().sendFeedback(() -> Text.literal("Set the pose of " + entity.getDisplayName().getString() + " to " + formatPoseName(pose.name())), true);

        return 1;
    }

    private static String formatPoseName(String poseName) {
        StringBuilder formattedName = new StringBuilder();
        boolean toUpperCase = true;

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

    public static void tickPathfindingEntities() {
        if (!pathfindingEntities.isEmpty()) {
            for (Map.Entry<MobEntity, PathfindingData> entry : pathfindingEntities.entrySet()) {
                MobEntity entity = entry.getKey();

                if (!entity.isAlive()) {
                    pathfindingEntitiesToRemove.add(entity);
                    continue;
                }

                PathfindingData data = entry.getValue();
                BlockPos targetPos = data.targetPos;
                float proximityDistance = data.proximityDistance;
                float mobSpeed = data.mobSpeed;

                if (entity.getBlockPos().isWithinDistance(targetPos, proximityDistance)) {
                    entity.getNavigation().stop();
                    pathfindingEntitiesToRemove.add(entity);
                } else {
                    entity.getNavigation().startMovingTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), mobSpeed);
                }
            }

            for (MobEntity entity : pathfindingEntitiesToRemove) {
                entity.getNavigation().stop();
                pathfindingEntities.remove(entity);
            }
        }
    }

    public static void tickFollowingEntitiesPathfinding() {
        if (!followingEntities.isEmpty()) {
            for (Map.Entry<MobEntity, PathfindingData> entry : followingEntities.entrySet()) {
                MobEntity followingEntity = entry.getKey();

                if (!followingEntity.isAlive()) {
                    pathfindingEntitiesToRemove.add(followingEntity);
                    continue;
                }

                PathfindingData data = entry.getValue();
                Entity targetEntity = data.targetEntity;

                if (!targetEntity.isAlive()) {
                    pathfindingEntitiesToRemove.add(followingEntity);
                    continue;
                }

                if (!followingEntity.isAlive()) {
                    followingEntitiesToRemove.add(followingEntity);
                    continue;
                }

                float proximityDistance = data.proximityDistance;
                float mobSpeed = data.mobSpeed;

                followingEntity.getLookControl().lookAt(targetEntity);

                if (followingEntity.getBlockPos().isWithinDistance(targetEntity.getBlockPos(), proximityDistance)) {
                    followingEntity.getNavigation().stop();
                } else {
                    followingEntity.getNavigation().startMovingTo(targetEntity, mobSpeed);
                }
            }

            for (MobEntity entity : followingEntitiesToRemove) {
                entity.getNavigation().stop();
                followingEntities.remove(entity);
            }
        }
    }
}