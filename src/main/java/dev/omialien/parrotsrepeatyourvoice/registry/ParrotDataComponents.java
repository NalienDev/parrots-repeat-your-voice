package dev.omialien.parrotsrepeatyourvoice.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
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
        public static ParrotAudio fromAudio(IRecordedAudio audio) {
            return new ParrotAudio(audio.getPlayerUUID(), audio.getId());
        }
    }

    private static final Codec<ParrotAudio> PARROT_AUDIO_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("playerId").forGetter(ParrotAudio::playerId),
                    UUIDUtil.CODEC.fieldOf("audioId").forGetter(ParrotAudio::audioId)
            ).apply(instance, ParrotAudio::new)
    );

    public static final Supplier<AttachmentType<List<ParrotAudio>>> PARROT_AUDIO_ATTACHMENT = REGISTRY.register(
            "parrot_audios", () -> AttachmentType.builder(() -> (List<ParrotAudio>)(new ArrayList<ParrotAudio>()))
                    .serialize(PARROT_AUDIO_CODEC.listOf().xmap(ArrayList::new, Function.identity()))
                    .build()
    );

    public static final Supplier<AttachmentType<Boolean>> REMEMBER_NEW_AUDIOS_ATTACHMENT = REGISTRY.register(
            "remember_audios", () -> AttachmentType.builder(() -> true)
                    .serialize(PrimitiveCodec.BOOL)
                    .build()
    );

    public static final Supplier<AttachmentType<ParrotAudio>> LAST_AUDIO_PLAYED = REGISTRY.register(
            // TODO find better way instead of using null, maybe Optional<ParrotAudio>
            "last_audio", () -> AttachmentType.builder(() -> (ParrotAudio)null)
                    .serialize(PARROT_AUDIO_CODEC)
                    .build()
    );

    public enum SeedActions implements StringRepresentable {
        REMEMBER, // Stops saving new audios and removing old ones
        UNREMEMBER, // Goes back to saving new audios and removing old ones
        FORGETAUDIO,
        FORCEAUDIO,
        ;

        @Override
        public @NotNull String getSerializedName() {
            return this.toString();
        }
    }

    private static final Codec<SeedActions> SEED_ACTIONS_CODEC = StringRepresentable.fromEnum(SeedActions::values);

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ParrotsRepeatYourVoice.MOD_ID);

    public static final Supplier<DataComponentType<SeedActions>> SEED_ACTIONS = DATA_COMPONENTS.registerComponentType(
            "seed_actions",
            builder -> builder
                    .persistent(SEED_ACTIONS_CODEC)
    );

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
        REGISTRY.register(bus);

    }
}
