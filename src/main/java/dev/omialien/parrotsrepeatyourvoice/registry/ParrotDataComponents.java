package dev.omialien.parrotsrepeatyourvoice.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import net.minecraft.core.UUIDUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ParrotDataComponents {
    private static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ParrotsRepeatYourVoice.MOD_ID);

    public record ParrotAudio(UUID playerId, UUID audioId) {
        public Pair<UUID, UUID> asPair() {
            return Pair.of(playerId(), audioId());
        }
        public static ParrotAudio fromPair(Pair<UUID, UUID> id) {
            return new ParrotAudio(id.getFirst(), id.getSecond());
        }
    }

    private static final Codec<ParrotAudio> PARROT_AUDIO_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("playerId").forGetter(ParrotAudio::playerId),
                    UUIDUtil.CODEC.fieldOf("audioId").forGetter(ParrotAudio::audioId)
            ).apply(instance, ParrotAudio::new)
    );

    public static final Supplier<AttachmentType<List<ParrotAudio>>> PARROT_AUDIO_ATTACHMENT = REGISTRY.register(
            "parrot_audios", () -> AttachmentType.builder(() -> Collections.synchronizedList(new ArrayList<ParrotAudio>()))
                    .serialize(PARROT_AUDIO_CODEC.listOf())
                    .build()
    );

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
