package net.jurassicbeast.worldshaper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.jurassicbeast.worldshaper.command.EntityControls;
import net.jurassicbeast.worldshaper.command.WorldControlCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldShaper implements ModInitializer {
	public static final String MOD_ID = "world-shaper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		WorldControlCommand.register();

		ServerTickEvents.START_SERVER_TICK.register(server -> EntityControls.tickPathfindingEntities());
		ServerTickEvents.START_SERVER_TICK.register(server -> EntityControls.tickFollowingEntitiesPathfinding());

		LOGGER.info("Hello Fabric world!");
	}
}