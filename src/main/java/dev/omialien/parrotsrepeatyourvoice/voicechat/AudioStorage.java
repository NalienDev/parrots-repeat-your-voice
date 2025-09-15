package dev.omialien.parrotsrepeatyourvoice.voicechat;

import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import dev.omialien.voicechat_recording.voicechat.VoiceChatRecordingPlugin;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class AudioStorage {
    public static final int SAMPLE_RATE = 48000;
    private final Random rnd;
    private final Set<RecordedAudio> storedAudios;

    public AudioStorage(){
        rnd = new Random();
        storedAudios = new HashSet<>();
    }

    public int getTotalAudioCount(){
        return storedAudios.size();
    }

    public void addAudio(RecordedAudio audio){
        storedAudios.add(audio);
    }

    public void removeAudio(UUID audioId){
        storedAudios.stream().filter(a -> a.getId().equals(audioId)).findFirst().ifPresent(storedAudios::remove);
    }

    public RecordedAudio getAudio(UUID audioId){
        return storedAudios.stream().filter(a -> a.getId().equals(audioId)).findFirst().orElse(null);
    }

    public void saveAllAudios() {
        storedAudios.forEach((RecordedAudio::saveAudio));
    }
    public void loadAllAudios() {
        VoiceChatRecordingPlugin.loadAllAudios();
    }
}
