package net.jurassicbeast.worldshaper.customgamerulesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import oshi.util.tuples.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModGameRules {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ModGameRules.Key<?>, ModGameRules.Type<?>> RULE_TYPES = new LinkedHashMap<>();
    private final Map<ModGameRules.Key<?>, ModGameRules.Rule<?>> rules;
    public static int countOfNumericRules = 0;
    public static int countOfBooleanRules = 0;

    public static <T extends ModGameRules.Rule<T>> ModGameRules.Key<T> register(String name, ModGameRules.Category category, ModGameRules.Type<T> type) {
        int index = 0;

        if (isRuleNumeric(type)) {
            index = countOfNumericRules++;
        } else if (type.getRuleClass().equals(ModGameRules.BooleanRule.class)) {
            index = countOfBooleanRules++;
        }

        ModGameRules.Key<T> key = new ModGameRules.Key<>(name, category, index);
        ModGameRules.Type<?> type2 = RULE_TYPES.put(key, type);
        if (type2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        } else {
            return key;
        }
    }

    public static boolean isRuleNumeric(ModGameRules.Type<?> type) {
        return type.getRuleClass().equals(ModGameRules.IntRule.class) || type.getRuleClass().equals(ModGameRules.DoubleRule.class);
    }

    public ModGameRules() {
        this.rules = RULE_TYPES.entrySet()
                .stream()
                .collect(ImmutableMap.toImmutableMap(
                        Map.Entry::getKey,
                        e -> e.getValue().createRule()
                ));
    }

    public Map<ModGameRules.Key<?>, ModGameRules.Rule<?>> getRules() {
        return this.rules;
    }

    @SuppressWarnings("unchecked")
    public <T extends ModGameRules.Rule<T>> T get(ModGameRules.Key<T> key) {
        return (T)this.rules.get(key);
    }

    /**
     * Make the visitor visit all registered game rules.
     *
     * <p>The visitation involves calling both {@link Visitor#visit(ModGameRules.Key, ModGameRules.Type)} and {@code visitX} for every game rule, where X is the current rule's concrete type such as a boolean.
     */
    public static void accept(ModGameRules.Visitor visitor) {
        RULE_TYPES.forEach((key, type) -> accept(visitor, key, type));
    }

    @SuppressWarnings("all")
    private static <T extends ModGameRules.Rule<T>> void accept(ModGameRules.Visitor consumer, ModGameRules.Key<?> key, ModGameRules.Type<?> type) {
        consumer.visit((Key<T>) key, (Type<T>) type);
        type.accept(consumer, (Key) key);
    }

    interface Acceptor<T extends ModGameRules.Rule<T>> {
        void call(ModGameRules.Visitor consumer, ModGameRules.Key<T> key, ModGameRules.Type<T> type);
    }

    public abstract static class Rule<T extends ModGameRules.Rule<T>> {
        protected final ModGameRules.Type<T> type;

        public Rule(ModGameRules.Type<T> type) {
            this.type = type;
        }

        protected abstract void deserialize(String value);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getThis();

        protected abstract T copy();
    }

    public static class Type<T extends ModGameRules.Rule<T>> {
        final Supplier<ArgumentType<?>> argumentType;
        private final Function<ModGameRules.Type<T>, T> ruleFactory;
        final BiConsumer<MinecraftServer, T> changeCallback;
        private final ModGameRules.Acceptor<T> ruleAcceptor;

        Type(
                Supplier<ArgumentType<?>> argumentType,
                Function<ModGameRules.Type<T>, T> ruleFactory,
                BiConsumer<MinecraftServer, T> changeCallback,
                ModGameRules.Acceptor<T> ruleAcceptor
        ) {
            this.argumentType = argumentType;
            this.ruleFactory = ruleFactory;
            this.changeCallback = changeCallback;
            this.ruleAcceptor = ruleAcceptor;
        }

        @SuppressWarnings("unchecked")
        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, (ArgumentType<T>)this.argumentType.get());
        }

        public T createRule() {
            return this.ruleFactory.apply(this);
        }

        public void accept(ModGameRules.Visitor consumer, ModGameRules.Key<T> key) {
            this.ruleAcceptor.call(consumer, key, this);
        }

        public Class<?> getRuleClass() {
            return this.ruleFactory.apply(this).getClass();
        }
    }

    public static final class Key<T extends ModGameRules.Rule<T>> {
        final String name;
        private final ModGameRules.Category category;
        private final int index;

        public Key(String name, ModGameRules.Category category, int index) {
            this.name = name;
            this.category = category;
            this.index = index;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            return o instanceof ModGameRules.Key && ((Key<?>) o).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String getName() {
            return this.name;
        }

        public int getIndexInArray() {
            return this.index;
        }

        public ModGameRules.Category getCategory() {
            return this.category;
        }
    }

    public enum Category {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String category;

        Category(final String category) {
            this.category = category;
        }

        public String getCategory() {
            return this.category;
        }
    }

    public static class BooleanRule extends ModGameRules.Rule<ModGameRules.BooleanRule> {
        private boolean defaultValue;

        static ModGameRules.Type<ModGameRules.BooleanRule> create(boolean defaultValue) {
            return create(defaultValue, (server, rule) -> {
            });
        }

        static ModGameRules.Type<ModGameRules.BooleanRule> create(boolean defaultValue, BiConsumer<MinecraftServer, ModGameRules.BooleanRule> changeCallback) {
            return new ModGameRules.Type<>(BoolArgumentType::bool, type -> new ModGameRules.BooleanRule(type, defaultValue), changeCallback, ModGameRules.Visitor::visitBoolean);
        }

        public BooleanRule(ModGameRules.Type<ModGameRules.BooleanRule> type, boolean defaultValue) {
            super(type);
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.defaultValue);
        }

        @Override
        protected void deserialize(String value) {
            this.defaultValue = Boolean.parseBoolean(value);
        }

        @Override
        public int getCommandResult() {
            return this.defaultValue ? 1 : 0;
        }

        protected ModGameRules.BooleanRule getThis() {
            return this;
        }

        protected ModGameRules.BooleanRule copy() {
            return new ModGameRules.BooleanRule(this.type, this.defaultValue);
        }
    }

    public static class IntRule extends ModGameRules.Rule<ModGameRules.IntRule> {
        private int defaultValue;

        private static ModGameRules.Type<ModGameRules.IntRule> create(int defaultValue, BiConsumer<MinecraftServer, ModGameRules.IntRule> changeCallback) {
            return new ModGameRules.Type<>(IntegerArgumentType::integer, type -> new ModGameRules.IntRule(type, defaultValue), changeCallback, ModGameRules.Visitor::visitInt);
        }

        static ModGameRules.Type<ModGameRules.IntRule> create(int defaultValue, int min, int max, BiConsumer<MinecraftServer, ModGameRules.IntRule> changeCallback) {
            return new ModGameRules.Type<>(
                    () -> IntegerArgumentType.integer(min, max), type -> new ModGameRules.IntRule(type, defaultValue), changeCallback, ModGameRules.Visitor::visitInt
            );
        }

        static ModGameRules.Type<ModGameRules.IntRule> create(int defaultValue) {
            return create(defaultValue, (server, rule) -> {
            });
        }

        public IntRule(ModGameRules.Type<ModGameRules.IntRule> rule, int defaultValue) {
            super(rule);
            this.defaultValue = defaultValue;
        }

        public int getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String serialize() {
            return Integer.toString(this.defaultValue);
        }

        @Override
        protected void deserialize(String value) {
            this.defaultValue = parseInt(value);
        }

        private static int parseInt(String input) {
            if (!input.isEmpty()) {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException var2) {
                    ModGameRules.LOGGER.warn("Failed to parse integer {}", input);
                }
            }

            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.defaultValue;
        }

        protected ModGameRules.IntRule getThis() {
            return this;
        }

        protected ModGameRules.IntRule copy() {
            return new ModGameRules.IntRule(this.type, this.defaultValue);
        }
    }

    public static class DoubleRule extends ModGameRules.Rule<ModGameRules.DoubleRule> {
        private double defaultValue;

        private static ModGameRules.Type<ModGameRules.DoubleRule> create(double defaultValue, BiConsumer<MinecraftServer, ModGameRules.DoubleRule> changeCallback) {
            return new ModGameRules.Type<>(DoubleArgumentType::doubleArg, type -> new ModGameRules.DoubleRule(type, defaultValue), changeCallback, ModGameRules.Visitor::visitDouble);
        }

        static ModGameRules.Type<ModGameRules.DoubleRule> create(double defaultValue, double min, double max, BiConsumer<MinecraftServer, ModGameRules.DoubleRule> changeCallback) {
            return new ModGameRules.Type<>(
                    () -> DoubleArgumentType.doubleArg(min, max), type -> new ModGameRules.DoubleRule(type, defaultValue), changeCallback, ModGameRules.Visitor::visitDouble
            );
        }

        static ModGameRules.Type<ModGameRules.DoubleRule> create(double defaultValue) {
            return create(defaultValue, (server, rule) -> {
            });
        }

        public DoubleRule(ModGameRules.Type<ModGameRules.DoubleRule> rule, double defaultValue) {
            super(rule);
            this.defaultValue = defaultValue;
        }

        public double getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String serialize() {
            return Double.toString(this.defaultValue);
        }

        @Override
        protected void deserialize(String value) {
            this.defaultValue = parseDouble(value);
        }

        private static double parseDouble(String input) {
            if (!input.isEmpty()) {
                try {
                    return Double.parseDouble(input);
                } catch (NumberFormatException var2) {
                    ModGameRules.LOGGER.warn("Failed to parse double {}", input);
                }
            }

            return 0;
        }

        @Override
        public int getCommandResult() {
            return (int)this.defaultValue;
        }

        protected ModGameRules.DoubleRule getThis() {
            return this;
        }

        protected ModGameRules.DoubleRule copy() {
            return new ModGameRules.DoubleRule(this.type, this.defaultValue);
        }
    }

    /**
     * A visitor used to visit all game rules.
     */
    public interface Visitor {
        /**
         * Visit a game rule.
         *
         * <p>It is expected all game rules regardless of type will be visited using this method.
         */
        default <T extends ModGameRules.Rule<T>> void visit(ModGameRules.Key<T> key, ModGameRules.Type<T> type) {
        }

        /**
         * Visit a boolean rule.
         *
         * <p>Note {@link #visit(ModGameRules.Key, ModGameRules.Type)} will be called before this method.
         */
        default void visitBoolean(ModGameRules.Key<ModGameRules.BooleanRule> key, ModGameRules.Type<ModGameRules.BooleanRule> type) {
        }

        /**
         * Visit an integer rule.
         *
         * <p>Note {@link #visit(ModGameRules.Key, ModGameRules.Type)} will be called before this method.
         */
        default void visitInt(ModGameRules.Key<ModGameRules.IntRule> key, ModGameRules.Type<ModGameRules.IntRule> type) {
        }

        /**
         * Visit a double rule.
         *
         * <p>Note {@link #visit(ModGameRules.Key, ModGameRules.Type)} will be called before this method.
         */
        default void visitDouble(ModGameRules.Key<ModGameRules.DoubleRule> key, ModGameRules.Type<ModGameRules.DoubleRule> type) {
        }
    }
}