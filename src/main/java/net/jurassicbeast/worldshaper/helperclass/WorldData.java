package net.jurassicbeast.worldshaper.helperclass;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WorldData {
    public static void setNumericValue(int index, double value, MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        serverState.numericValues[index] = value;
        saveNumericValues(serverState.numericValues, server);
    }

    public static void setBooleanValue(int index, boolean value, MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        serverState.booleanValues[index] = value;
        saveBooleanValues(serverState.booleanValues, server);
    }

    public static double getNumericValue(int index, MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.numericValues[index];
    }

    public static boolean getBooleanValue(int index, MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.booleanValues[index];
    }

    public static void saveNumericValues(double[] newArray, MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        serverState.numericValues = newArray;
    }

    public static void saveBooleanValues(boolean[] newArray, MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        serverState.booleanValues = newArray;
    }

    public static double[] getNumericValues(MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.numericValues;
    }

    public static boolean[] getBooleanValues(MinecraftServer server) {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(server);
        return serverState.booleanValues;
    }

    public static void setNumericValue(int index, float value, ServerWorld serverWorld) {
        setNumericValue(index, value, serverWorld.getServer());
    }

    public static void setBooleanValue(int index, boolean value, ServerWorld serverWorld) {
        setBooleanValue(index, value, serverWorld.getServer());
    }

    public static double getNumericValue(int index, ServerWorld serverWorld) {
        return getNumericValue(index, serverWorld.getServer());
    }

    public static boolean getBooleanValue(int index, ServerWorld serverWorld) {
        return getBooleanValue(index, serverWorld.getServer());
    }

    public static void saveNumericValues(double[] newArray, ServerWorld serverWorld) {
        saveNumericValues(newArray, serverWorld.getServer());
    }

    public static void saveBooleanValues(boolean[] newArray, ServerWorld serverWorld) {
        saveBooleanValues(newArray, serverWorld.getServer());
    }

    public static double[] getNumericValues(ServerWorld serverWorld) {
        return getNumericValues(serverWorld.getServer());
    }

    public static boolean[] getBooleanValues(ServerWorld serverWorld) {
        return getBooleanValues(serverWorld.getServer());
    }
}
