package net.jurassicbeast.worldshaper;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.jurassicbeast.worldshaper.arguments.blockarguments.BlockArguments;
import net.jurassicbeast.worldshaper.arguments.entityarguments.EntityArguments;
import net.jurassicbeast.worldshaper.customgamerulesystem.ModGameRulesArgument;

import static net.minecraft.server.command.CommandManager.literal;

public class WorldShaperCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("shapeWorld").requires(source -> source.hasPermissionLevel(2))
                .then(BlockArguments.register())
                .then(EntityArguments.register())
                .then(ModGameRulesArgument.register())));
    }
}