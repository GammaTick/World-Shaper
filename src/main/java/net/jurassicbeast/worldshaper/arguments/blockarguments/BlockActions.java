package net.jurassicbeast.worldshaper.arguments.blockarguments;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockActions {

    public static int rotate(CommandContext<ServerCommandSource> context, byte direction) {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "blockPosition");
        World world = context.getSource().getWorld();
        BlockState oldState = world.getBlockState(pos);

        if (!oldState.contains(Properties.HORIZONTAL_FACING)) {
            context.getSource().sendError(Text.literal(oldState.getBlock().getName().getString() + " cannot be rotated"));
            return 0;
        }

        if (!oldState.contains(Properties.VERTICAL_DIRECTION)) {
            context.getSource().sendError(Text.literal(oldState.getBlock().getName().getString() + " cannot be rotated vertically"));
            return 0;
        }

        BlockState newState = switch (direction) {
            case 0 -> oldState.with(Properties.HORIZONTAL_FACING, Direction.NORTH);
            case 1 -> oldState.with(Properties.HORIZONTAL_FACING, Direction.EAST);
            case 2 -> oldState.with(Properties.HORIZONTAL_FACING, Direction.WEST);
            case 3 -> oldState.with(Properties.HORIZONTAL_FACING, Direction.SOUTH);
            case 4 -> oldState.with(Properties.HORIZONTAL_FACING, Direction.UP);
            case 5 -> oldState.with(Properties.HORIZONTAL_FACING, Direction.DOWN);
            default -> oldState;
        };

        world.setBlockState(pos, newState);

        return 1;
    }

    public static int executeAction(CommandContext<ServerCommandSource> context) {
        return 1;
    }
}
