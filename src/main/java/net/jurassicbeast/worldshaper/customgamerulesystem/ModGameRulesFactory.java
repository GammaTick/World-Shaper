package net.jurassicbeast.worldshaper.customgamerulesystem;

public final class ModGameRulesFactory {
    public static ModGameRules.Type<ModGameRules.IntRule> createIntRule(int defaultValue) {
        return ModGameRules.IntRule.create(defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, (server, rule) -> {
        });
    }

    public static ModGameRules.Type<ModGameRules.IntRule> createIntRule(int defaultValue, int minimumValue) {
        return ModGameRules.IntRule.create(defaultValue, minimumValue, Integer.MIN_VALUE, (server, rule) -> {
        });
    }

    public static ModGameRules.Type<ModGameRules.IntRule> createIntRule(int defaultValue, int minimumValue, int maximumValue) {
        return ModGameRules.IntRule.create(defaultValue, minimumValue, maximumValue, (server, rule) -> {
        });
    }

    public static ModGameRules.Type<ModGameRules.DoubleRule> createDoubleRule(double defaultValue) {
        return ModGameRules.DoubleRule.create(defaultValue, Double.MAX_VALUE, Double.MIN_VALUE, (server, rule) -> {
        });
    }

    public static ModGameRules.Type<ModGameRules.DoubleRule> createDoubleRule(double defaultValue, double minimumValue) {
        return ModGameRules.DoubleRule.create(defaultValue, minimumValue, Double.MAX_VALUE, (server, rule) -> {
        });
    }

    public static ModGameRules.Type<ModGameRules.DoubleRule> createDoubleRule(double defaultValue, double minimumValue, double maximumValue) {
        return ModGameRules.DoubleRule.create(defaultValue, minimumValue, maximumValue, (server, rule) -> {
        });
    }

    public static ModGameRules.Type<ModGameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
        return ModGameRules.BooleanRule.create(defaultValue);
    }
}

