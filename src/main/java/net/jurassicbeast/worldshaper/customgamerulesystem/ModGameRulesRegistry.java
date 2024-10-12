package net.jurassicbeast.worldshaper.customgamerulesystem;

import static net.jurassicbeast.worldshaper.customgamerulesystem.ModGameRules.register;

public class ModGameRulesRegistry {
public static ModGameRules.Key<ModGameRules.BooleanRule> CAN_WATER_SPREAD_INFINITELY;
    public static ModGameRules.Key<ModGameRules.BooleanRule> SHOW_COUNT_OF_ALL_REMAINING_RAIDERS;
    public static ModGameRules.Key<ModGameRules.BooleanRule> SHOW_COUNT_OF_WAVES_IN_RAID;
    public static ModGameRules.Key<ModGameRules.BooleanRule> CAN_BOATS_BE_INFLUENCED_BY_BUBBLE_COLUMNS;
    public static ModGameRules.Key<ModGameRules.IntRule> RAIDERS_GLOW_RANGE_AROUND_BELL;

    public static void registerRules() {
        SHOW_COUNT_OF_ALL_REMAINING_RAIDERS = register("showCountOfAllRemainingRaiders", ModGameRules.Category.MISC, ModGameRulesFactory.createBooleanRule(false));
        SHOW_COUNT_OF_WAVES_IN_RAID = register("showCountOfWavesInRaid", ModGameRules.Category.MISC, ModGameRulesFactory.createBooleanRule(false));
        RAIDERS_GLOW_RANGE_AROUND_BELL = register("raidersGlowRangeAroundBell", ModGameRules.Category.MISC, ModGameRulesFactory.createIntRule(32, 0, 1024));
        CAN_BOATS_BE_INFLUENCED_BY_BUBBLE_COLUMNS = register("canBoatsBeInfluencedByBubbleColumns", ModGameRules.Category.MOBS, ModGameRulesFactory.createBooleanRule(true));
        CAN_WATER_SPREAD_INFINITELY = register("canWaterSpreadInfinitely", ModGameRules.Category.UPDATES, ModGameRulesFactory.createBooleanRule(false));
    }
}
