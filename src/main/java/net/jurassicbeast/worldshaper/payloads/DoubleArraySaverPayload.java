package net.jurassicbeast.worldshaper.payloads;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DoubleArraySaverPayload(double[] array) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, double[]> DOUBLE_ARRAY_CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, double[] array) {
            buf.writeVarInt(array.length);
            for (double f : array) {
                buf.writeDouble(f);
            }
        }

        @Override
        public double[] decode(PacketByteBuf buf) {
            double[] array = new double[buf.readVarInt()];
            for (int i = 0; i < array.length; i++) {
                array[i] = buf.readFloat();
            }
            return array;
        }
    };

    public static final Id<DoubleArraySaverPayload> ID = CustomPayload.id(WorldShaper.MOD_ID + "/double_array");
    public static final PacketCodec<PacketByteBuf, DoubleArraySaverPayload> CODEC = PacketCodec.tuple(DOUBLE_ARRAY_CODEC, DoubleArraySaverPayload::array, DoubleArraySaverPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
