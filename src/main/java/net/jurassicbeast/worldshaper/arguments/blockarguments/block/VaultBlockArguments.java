package net.jurassicbeast.worldshaper.arguments.blockarguments.block;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.enums.VaultState;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.CompletableFuture;

public class VaultBlockArguments {
    public static CompletableFuture<Suggestions> getAvailableFirstArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        World world = context.getSource().getWorld();

        if (isVaultBlock(world, pos)) {
            builder.suggest("setVaultState");
            builder.suggest("unlock");
        }

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> getAvailableSecondArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        World world = context.getSource().getWorld();
        String action = StringArgumentType.getString(context, "action");

        if (isVaultBlock(world, pos) && "setVaultState".equals(action)) {
            builder.suggest("default");
            builder.suggest("ominous");
            builder.suggest("ejecting");
            builder.suggest("unlocking");
            builder.suggest("active");
            builder.suggest("inactive");
        }

        return builder.buildFuture();
    }

    public static int setVaultState(CommandContext<ServerCommandSource> context) {
        BlockPos blockPos = BlockPosArgumentType.getBlockPos(context, "pos");
        World world = context.getSource().getWorld();

        String action = StringArgumentType.getString(context, "action");
        String state = StringArgumentType.getString(context, "state");
        VaultBlockEntity vaultBlockEntity = (VaultBlockEntity) world.getBlockEntity(blockPos);

        if (vaultBlockEntity == null) {
            return 0;
        }

        BlockState oldState = world.getBlockState(blockPos);
        BlockState newState = oldState;

        switch (action) {
            case "setVaultState": {
                newState = switch (state) {
                    case "default" -> oldState.with(VaultBlock.OMINOUS, false);
                    case "ominous" -> oldState.with(VaultBlock.OMINOUS, true);
                    case "ejecting" -> oldState.with(VaultBlock.VAULT_STATE, VaultState.EJECTING);
                    case "unlocking" -> oldState.with(VaultBlock.VAULT_STATE, VaultState.UNLOCKING);
                    case "active" -> oldState.with(VaultBlock.VAULT_STATE, VaultState.ACTIVE);
                    case "inactive" -> oldState.with(VaultBlock.VAULT_STATE, VaultState.INACTIVE);
                    default -> oldState;
                };
            } break;
        }

        VaultState vaultState = oldState.get(VaultBlock.VAULT_STATE);
        VaultState vaultState2 = newState.get(VaultBlock.VAULT_STATE);
        world.setBlockState(blockPos, newState, Block.NOTIFY_ALL);
        vaultState.onStateChange(context.getSource().getWorld(), blockPos, vaultState2, vaultBlockEntity.getConfig(), vaultBlockEntity.getSharedData(), (Boolean)newState.get(VaultBlock.OMINOUS));
        vaultBlockEntity.toUpdatePacket();
        vaultBlockEntity.markDirty();
        return 1;
    }

    private static boolean isVaultBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().equals(Blocks.VAULT);
    }
}
