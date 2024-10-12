package net.jurassicbeast.worldshaper.arguments.blockarguments;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BlockArguments {

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("block")
                .then(argument("blockPosition", BlockPosArgumentType.blockPos())
                        .then(literal("rotate")
                                .then(literal("horizontalDirection:north")
                                        .executes(context -> BlockActions.rotate(context, (byte) 0)))
                                .then(literal("horizontalDirection:east")
                                        .executes(context -> BlockActions.rotate(context, (byte) 1)))
                                .then(literal("horizontalDirection:west")
                                        .executes(context -> BlockActions.rotate(context, (byte) 2)))
                                .then(literal("horizontalDirection:south")
                                        .executes(context -> BlockActions.rotate(context, (byte) 3)))
                                .then(literal("verticalDirection:up")
                                        .executes(context -> BlockActions.rotate(context, (byte) 4)))
                                .then(literal("verticalDirection:down")
                                        .executes(context -> BlockActions.rotate(context, (byte) 5))))
                        .then(argument("action", StringArgumentType.word())
                                .suggests((BlockArguments::getCustomArgumentsForBlock))
                                .executes(BlockActions::executeAction)));
    }

    private static CompletableFuture<Suggestions> getCustomArgumentsForBlock(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        World world = context.getSource().getWorld();
        Block block = world.getBlockState(BlockPosArgumentType.getBlockPos(context, "blockPosition")).getBlock();

        ///

        return builder.buildFuture();
    }
}