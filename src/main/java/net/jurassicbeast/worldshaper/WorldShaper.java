package net.jurassicbeast.worldshaper;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.jurassicbeast.worldshaper.customgamerulesystem.ModGameRulesArgument;
import net.jurassicbeast.worldshaper.customgamerulesystem.ModGameRulesRegistry;
import net.jurassicbeast.worldshaper.customgamerulesystem.ModGameRules;
import net.jurassicbeast.worldshaper.helperclass.WorldData;
import net.jurassicbeast.worldshaper.payloads.BooleanArraySaverPayload;
import net.jurassicbeast.worldshaper.payloads.DoubleArraySaverPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WorldShaper implements ModInitializer {
	public static final String MOD_ID = "world-shaper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static double[] numericValues;
	public static boolean[] booleanValues;

	public Map<ModGameRules.Key<?>, ModGameRules.Rule<?>> rules;

	public final static LiteralArgumentBuilder<ServerCommandSource> mobGameRulesArgumentBuilder = CommandManager.literal("mob");

	private boolean loadedGameRules;

	@Override
	public void onInitialize() {
		this.loadedGameRules = false;

		LOGGER.info("Building mobs sub argument of gamerule argument.");
		for (ModGameRulesArgument.MinecraftMob mob : ModGameRulesArgument.MinecraftMob.values()) {
			mobGameRulesArgumentBuilder.then(CommandManager.literal(mob.name().toLowerCase()));
		}

		LOGGER.info("Loading payloads.");
		PayloadTypeRegistry.playS2C().register(DoubleArraySaverPayload.ID, DoubleArraySaverPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(BooleanArraySaverPayload.ID, BooleanArraySaverPayload.CODEC);

		LOGGER.info("Registering gamerules.");
		ModGameRulesRegistry.registerRules();

		ServerWorldEvents.LOAD.register(((server, world) -> {
			LOGGER.info("Loading gamerules' values from world.");
			this.rules = new ModGameRules().getRules();
			loadData(server);
			this.loadedGameRules = true;
		}));

		LOGGER.info("Registering the \"World Shaper\" command.");
		WorldShaperCommand.register();

		LOGGER.info("Successfully loaded the mod!");
	}

	private void loadData(MinecraftServer server) {
		numericValues = WorldData.getNumericValues(server);
		booleanValues = WorldData.getBooleanValues(server);

		if (numericValues == null || numericValues.length == 0) {
			initNumericValues(server);
		}

		if (booleanValues == null || booleanValues.length == 0) {
			initBooleanValues(server);
		}

		if (numericValues.length < ModGameRules.countOfNumericRules) {
			addMissingNumericValues(server);
		}

		if (booleanValues.length < ModGameRules.countOfBooleanRules) {
			addMissingBooleanValues(server);
		}
	}

	private void initNumericValues(MinecraftServer server) {
		numericValues = new double[ModGameRules.countOfNumericRules];
		int index = 0;

		for (Map.Entry<ModGameRules.Key<?>, ModGameRules.Rule<?>> entry : rules.entrySet()) {
			ModGameRules.Rule<?> rule = entry.getValue();

			if (rule instanceof ModGameRules.IntRule intRule) {
				numericValues[index] = intRule.getDefaultValue();
				index++;
			}
		}

		WorldData.saveNumericValues(numericValues, server);
	}

	private void initBooleanValues(MinecraftServer server) {
		booleanValues = new boolean[ModGameRules.countOfBooleanRules];
		int index = 0;

		for (Map.Entry<ModGameRules.Key<?>, ModGameRules.Rule<?>> entry : rules.entrySet()) {
			ModGameRules.Rule<?> rule = entry.getValue();

			if (rule instanceof ModGameRules.BooleanRule booleanRule) {
				booleanValues[index] = booleanRule.getDefaultValue();
				index++;
			}
		}

		WorldData.saveBooleanValues(booleanValues, server);
	}

	private void addMissingNumericValues(MinecraftServer server) {
		double[] newNumericValuesArray = new double[ModGameRules.countOfNumericRules];
		System.arraycopy(numericValues, 0, newNumericValuesArray, 0, numericValues.length);

		int iterationsLeft = numericValues.length;
		int index = 0;

		for (Map.Entry<ModGameRules.Key<?>, ModGameRules.Rule<?>> entry : rules.entrySet()) {
			if (iterationsLeft > 0) {
				iterationsLeft--;
				continue;
			}

			ModGameRules.Rule<?> rule = entry.getValue();

			if (rule instanceof ModGameRules.IntRule intRule) {
				newNumericValuesArray[index] = intRule.getDefaultValue();
				index++;
				continue;
			}

			if (rule instanceof ModGameRules.DoubleRule doubleRule) {
				newNumericValuesArray[index] = doubleRule.getDefaultValue();
				index++;
			}
		}

		numericValues = newNumericValuesArray;
		WorldData.saveNumericValues(newNumericValuesArray, server);
	}

	private void addMissingBooleanValues(MinecraftServer server) {
		boolean[] newBooleanValuesArray = new boolean[ModGameRules.countOfBooleanRules];
		System.arraycopy(booleanValues, 0, newBooleanValuesArray, 0, booleanValues.length);

		int iterationsLeft = booleanValues.length;
		int index = 0;

		for (Map.Entry<ModGameRules.Key<?>, ModGameRules.Rule<?>> entry : rules.entrySet()) {
			if (iterationsLeft > 0) {
				iterationsLeft--;
				continue;
			}

			ModGameRules.Rule<?> rule = entry.getValue();

			if (rule instanceof ModGameRules.BooleanRule booleanRule) {
				newBooleanValuesArray[index] = booleanRule.getDefaultValue();
				index++;
			}
		}

		booleanValues = newBooleanValuesArray;
		WorldData.saveBooleanValues(newBooleanValuesArray, server);
	}
}