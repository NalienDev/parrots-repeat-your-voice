package dev.omialien.parrotsrepeatyourvoice.mixinutil;

import com.mojang.datafixers.util.Pair;

import java.util.List;
import java.util.UUID;

public interface ParrotAudioStorage {
    void yappingparrots$addSavedAudio(Pair<UUID, UUID> uuid);
    void yappingparrots$removeSavedAudio(Pair<UUID, UUID> uuid);
    List<Pair<UUID, UUID>> yappingparrots$getSavedAudios();
    int yappingparrots$audioCount();
    void yappingparrots$removeRandomAudio();
    Pair<UUID, UUID> yappingparrots$getRandomAudio();
}
