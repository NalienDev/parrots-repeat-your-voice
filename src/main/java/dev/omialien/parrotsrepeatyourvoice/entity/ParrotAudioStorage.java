package dev.omialien.parrotsrepeatyourvoice.entity;

import dev.omialien.voicechat_recording.voicechat.RecordedAudio;

import java.util.Set;
import java.util.UUID;

public interface ParrotAudioStorage {
    void parrotsrepeatyourvoice$addSavedAudio(UUID uuid);
    void parrotsrepeatyourvoice$removeSavedAudio(UUID uuid);
    Set<UUID> parrotsrepeatyourvoice$getSavedAudios();
    void parrotsrepeatyourvoice$removeRandomAudio();
    RecordedAudio parrotsrepeatyourvoice$getRandomAudio();
}
