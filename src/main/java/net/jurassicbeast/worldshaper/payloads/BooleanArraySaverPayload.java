package net.jurassicbeast.worldshaper.payloads;

import net.jurassicbeast.worldshaper.WorldShaper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record BooleanArraySaverPayload(boolean[] array) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, boolean[]> BOOLEAN_ARRAY_CODEC = new PacketCodec<>() {
        @Override
        public void encode(PacketByteBuf buf, boolean[] array) {
            buf.writeVarInt(array.length);
            for (boolean f : array) {
                buf.writeBoolean(f);
            }
        }

        @Override
        public boolean[] decode(PacketByteBuf buf) {
            boolean[] array = new boolean[buf.readVarInt()];
            for (int i = 0; i < array.length; i++) {
                array[i] = buf.readBoolean();
            }
            return array;
        }
    };

    public static final Id<BooleanArraySaverPayload> ID = CustomPayload.id(WorldShaper.MOD_ID + "/boolean_array");
    public static final PacketCodec<PacketByteBuf, BooleanArraySaverPayload> CODEC = PacketCodec.tuple(BOOLEAN_ARRAY_CODEC, BooleanArraySaverPayload::array, BooleanArraySaverPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
