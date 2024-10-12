package net.jurassicbeast.worldshaper.arguments.entityarguments.entity;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.ai.brain.task.SeekSkyTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VillagerEntityArguments {
    public static CompletableFuture<Suggestions> getAvailableArguments(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {

        builder.suggest("runCelebrateRaidWin");

        return builder.buildFuture();
    }

    public static int runAction(CommandContext<ServerCommandSource> context, VillagerEntity villagerEntity) {
        String action = StringArgumentType.getString(context, "action");
        ServerWorld serverWorld = context.getSource().getWorld();

        return runCelebrateRaidWinTask(serverWorld, villagerEntity, context);

    }

    public static int runCelebrateRaidWinTask(ServerWorld serverWorld, VillagerEntity villagerEntity, CommandContext<ServerCommandSource> context) {
        Random random = villagerEntity.getRandom();

        villagerEntity.playCelebrateSound();

        if (SeekSkyTask.isSkyVisible(serverWorld, villagerEntity, villagerEntity.getBlockPos())) {
            DyeColor dyeColor = Util.getRandom(DyeColor.values(), random);
            int i = random.nextInt(3);
            ItemStack itemStack = createFirework(dyeColor, i);
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
                    villagerEntity.getWorld(), villagerEntity, villagerEntity.getX(), villagerEntity.getEyeY() + 2, villagerEntity.getZ(), itemStack
            );
            villagerEntity.getWorld().spawnEntity(fireworkRocketEntity);

            context.getSource().sendFeedback(() -> Text.literal("Successfully triggered celebration task of " + villagerEntity.getDisplayName().getString()), true);
            return 1;
        }

        context.getSource().sendError(Text.literal("Unable to trigger celebration task of " + villagerEntity.getDisplayName().getString()));
        return 0;
    }

    private static ItemStack createFirework(DyeColor color, int flight) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
        itemStack.set(
                DataComponentTypes.FIREWORKS,
                new FireworksComponent(
                        (byte)flight,
                        List.of(new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(color.getFireworkColor()), IntList.of(), false, false))
                )
        );
        return itemStack;
    }

    public static int makeVillagerPanic(ServerWorld serverWorld, VillagerEntity villagerEntity, CommandContext<ServerCommandSource> context) {
        return 1;
    }
}
