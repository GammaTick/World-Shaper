package net.jurassicbeast.worldshaper.helperclass;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class StateSaverAndLoader extends PersistentState {
    public double[] numericValues;
    public boolean[] booleanValues;

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtList numericList = tag.getList("numericValues", NbtElement.FLOAT_TYPE);
        state.numericValues = new double[numericList.size()];

        for (int i = 0; i < numericList.size(); i++) {
            state.numericValues[i] = numericList.getFloat(i);
        }

        NbtByteArray booleanArray = (NbtByteArray) tag.get("booleanValues");

        if (booleanArray != null) {
            byte[] byteArray = booleanArray.getByteArray();
            state.booleanValues = new boolean[byteArray.length];

            for (int i = 0; i < byteArray.length; i++) {
                state.booleanValues[i] = byteArray[i] != 0;
            }
        }

        return state;
    }

    private static final Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader ::new,
            (nbt, registry) -> StateSaverAndLoader.createFromNbt(nbt),
            DataFixTypes.LEVEL
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, WorldShaper.MOD_ID);
        state.markDirty();

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (numericValues != null) {
            NbtList numericList = new NbtList();

            for (double value : numericValues) {
                numericList.add(NbtDouble.of(value));
            }

            nbt.put("numericValues", numericList);
        }

        if (booleanValues != null) {
            byte[] booleanBytes = new byte[booleanValues.length];

            for (int i = 0; i < booleanValues.length; i++) {
                booleanBytes[i] = (byte) (booleanValues[i] ? 1 : 0);
            }

            nbt.put("booleanValues", new NbtByteArray(booleanBytes));
        }

        return nbt;
    }
}
