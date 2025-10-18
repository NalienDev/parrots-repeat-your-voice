package dev.omialien.parrotsrepeatyourvoice.voicechat;

import com.mojang.datafixers.util.Pair;
import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.voicechatrecording.api.IRecordedAudio;

import java.util.*;

public class AudioStorage {
    public static final int SAMPLE_RATE = 48000;
    private final Map<Pair<UUID, UUID>, IRecordedAudio> storedAudios;

    public AudioStorage(){
        storedAudios = new HashMap<>();
    }

    public int getTotalAudioCount(){
        return storedAudios.size();
    }

    public void addAudio(IRecordedAudio audio){
        ParrotsRepeatYourVoice.LOGGER.debug("Adding audio: {}", new Pair<>(audio.getPlayerUUID(), audio.getId()));
        storedAudios.put(new Pair<>(audio.getPlayerUUID(), audio.getId()), audio);
    }

    public void removeAudio(Pair<UUID, UUID> audioId){
        IRecordedAudio audio = storedAudios.remove(audioId);
        if (audio == null) {
            ParrotsRepeatYourVoice.LOGGER.warn("removeAudio on non-existing audio: {} {}", audioId.getFirst(), audioId.getSecond());
        } else {
            audio.unsaveAudio(ParrotsRepeatYourVoice.MOD_ID);
        }
    }

    public IRecordedAudio getAudio(Pair<UUID, UUID> id){
        // TODO don't block when getting an audio that isn't in storage
        ParrotsRepeatYourVoice.LOGGER.debug("Getting audio {}", id);
        if(storedAudios.containsKey(id)) {
            return storedAudios.get(id);
        } else {
            try {
                return ParrotsRepeatYourVoice.RECORDING_API.loadAudio(id.getFirst(), id.getSecond(), audio -> {
                    if(audio != null) {
                        audio.saveAudio(ParrotsRepeatYourVoice.MOD_ID);
                        storedAudios.put(id, audio);
                    }
                }).get();
            } catch(Exception e) {
                ParrotsRepeatYourVoice.LOGGER.error("Error getting audio {} {}", id.getFirst(), id.getSecond());
                ParrotsRepeatYourVoice.LOGGER.error("{}", e.getMessage());
            }
        }
        return null;
    }

    public void saveAllAudios() {
        storedAudios.values().forEach((audio -> audio.saveAudio(ParrotsRepeatYourVoice.MOD_ID)));
    }
    public void loadAllAudios() {
        ParrotsRepeatYourVoice.RECORDING_API.loadNamespaceAudios(ParrotsRepeatYourVoice.MOD_ID);
    }
}
