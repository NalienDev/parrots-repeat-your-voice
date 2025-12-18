package dev.omialien.parrotsrepeatyourvoice.mixinutil;

import com.mojang.datafixers.util.Pair;
import dev.omialien.parrotsrepeatyourvoice.registry.ParrotDataComponents;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParrotAudioStorage {
    void yappingparrots$addSavedAudio(Pair<UUID, UUID> uuid);
    void yappingparrots$removeSavedAudio(Pair<UUID, UUID> uuid);
    List<Pair<UUID, UUID>> yappingparrots$getSavedAudios();
    int yappingparrots$audioCount();
    void yappingparrots$removeRandomAudio();
    Pair<UUID, UUID> yappingparrots$getRandomAudio();
    boolean yappingparrots$executeSeedAction(ParrotDataComponents.SeedActions action);
    boolean yappingparrots$rememberNewAudios();
    Optional<ParrotDataComponents.ParrotAudio> yappingparrots$getLastAudio();
}
