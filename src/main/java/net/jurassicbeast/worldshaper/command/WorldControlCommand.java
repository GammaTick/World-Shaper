package net.jurassicbeast.worldshaper.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.jurassicbeast.worldshaper.helperclasses.modcommandargumentsbuilders.EntityArguments;

import static net.minecraft.server.command.CommandManager.literal;

public class WorldControlCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("shapeWorld").requires(source -> source.hasPermissionLevel(2))
                .then(EntityArguments.register())));
    }
}