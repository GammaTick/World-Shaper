package net.jurassicbeast.worldshaper.customgamerulesystem;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.jurassicbeast.worldshaper.WorldShaper;
import net.jurassicbeast.worldshaper.helperclass.WorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ModGameRulesArgument {
    final static LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("gamerule");

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        ModGameRules.accept(
                new ModGameRules.Visitor() {
                    @Override
                    public <T extends ModGameRules.Rule<T>> void visit(ModGameRules.Key<T> key, ModGameRules.Type<T> type) {
                        addGameRuleToRespectiveCategory(key, type);
                    }
                }
        );

        literalArgumentBuilder.then(WorldShaper.mobGameRulesArgumentBuilder);

        return literalArgumentBuilder;
    }

    static <T extends ModGameRules.Rule<T>> int executeSet(CommandContext<ServerCommandSource> context, ModGameRules.Key<T> key) {
        ServerCommandSource serverCommandSource = context.getSource();
        int index = key.getIndexInArray();

        T rule = new ModGameRules().get(key);
        MinecraftServer server = serverCommandSource.getServer();

        if (rule instanceof ModGameRules.IntRule) {
            int value = IntegerArgumentType.getInteger(context, "value");

            WorldShaper.numericValues[index] = value;

            if (server == null) {
                return 0;
            }

            WorldData.setNumericValue(index, value, server);

            serverCommandSource.sendFeedback(() -> Text.literal(String.format("Gamerule %s is now set to: %s", key.getName(), value)), true);

            return rule.getCommandResult();
        }

        if (rule instanceof ModGameRules.BooleanRule) {
            boolean value = BoolArgumentType.getBool(context, "value");

            WorldShaper.booleanValues[index] = value;

            if (server == null) {
                return 0;
            }

            WorldData.setBooleanValue(index, value, server);

            serverCommandSource.sendFeedback(() -> Text.literal(String.format("Gamerule %s is now set to: %s", key.getName(), value)), true);

            return rule.getCommandResult();
        }

        return 0;
    }

    static <T extends ModGameRules.Rule<T>> int executeQuery(CommandContext<ServerCommandSource> context, ModGameRules.Key<T> key) {
        ServerCommandSource serverCommandSource = context.getSource();
        int index = key.getIndexInArray();

        T rule = new ModGameRules().get(key);

        if (rule instanceof ModGameRules.IntRule) {
            serverCommandSource.sendFeedback(() -> Text.literal(String.format("Gamerule %s is currently set to: %s", key.getName(), WorldShaper.numericValues[index])), false);
            return rule.getCommandResult();
        }

        if (rule instanceof ModGameRules.BooleanRule) {
            System.out.println(WorldShaper.booleanValues[index]);
            serverCommandSource.sendFeedback(() -> Text.literal(String.format("Gamerule %s is currently set to: %s", key.getName(), WorldShaper.booleanValues[index])), false);
            return rule.getCommandResult();
        }

        return 0;
    }

    static <T extends ModGameRules.Rule<T>> void addGameRuleToRespectiveCategory(ModGameRules.Key<T> key, ModGameRules.Type<T> type) {
        ModGameRules.Category category = key.getCategory();

//        switch (category) {
//            case MOBS -> mobGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//
//            case PLAYER -> playerGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//
//            case SPAWNING -> spawningGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//
//            case DROPS -> dropsGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//
//            case UPDATES -> updatesGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//
//            case CHAT -> chatGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//
//            case MISC -> miscGameRulesArgumentBuilder.then(
//                    CommandManager.literal(key.getName())
//                            .executes(context -> ModGameRulesArgument.executeQuery(context, key))
//                            .then(type.argument("value").executes(context -> ModGameRulesArgument.executeSet(context, key))));
//        }

    }

    public enum MinecraftMob {
        // Passive Mobs
        ALLAY,
        ARMADILLO,
        AXOLOTL,
        BAT,
        CAMEL,
        CAT,
        CHICKEN,
        COD,
        COW,
        DONKEY,
        FROG,
        GLOW_SQUID,
        HORSE,
        MOOSHROOM,
        MULE,
        OCELOT,
        PARROT,
        PIG,
        RABBIT,
        SALMON,
        SHEEP,
        SKELETON_HORSE,
        SNIFFER,
        SNOW_GOLEM,
        SQUID,
        STRIDER,
        TADPOLE,
        TROPICAL_FISH,
        TURTLE,
        VILLAGER,
        WANDERING_TRADER,

        // Neutral Mobs
        BEE,
        CAVE_SPIDER,
        DOLPHIN,
        DROWNED,
        ENDERMAN,
        FOX,
        GOAT,
        IRON_GOLEM,
        LLAMA,
        PANDA,
        PIGLIN,
        POLAR_BEAR,
        SPIDER,
        TRADER_LLAMA,
        WOLF,
        ZOMBIFIED_PIGLIN,

        // Hostile Mobs
        BLAZE,
        BOGGED,
        BREEZE,
        CREEPER,
        ELDER_GUARDIAN,
        ENDERMITE,
        EVOKER,
        GHAST,
        GUARDIAN,
        HOGLIN,
        HUSK,
        MAGMA_CUBE,
        PHANTOM,
        PIGLIN_BRUTE,
        PILLAGER,
        RAVAGER,
        SHULKER,
        SILVERFISH,
        SKELETON,
        SLIME,
        STRAY,
        VEX,
        VINDICATOR,
        WARDEN,
        WITCH,
        WITHER_SKELETON,
        ZOGLIN,
        ZOMBIE,
        ZOMBIE_VILLAGER,

        //Bosses
        ENDER_DRAGON,
        WITHER
    }
}
