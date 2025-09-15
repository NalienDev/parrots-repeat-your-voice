package dev.omialien.parrotsrepeatyourvoice.entity.goals;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.entity.ParrotAudioStorage;
import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import dev.omialien.voicechat_recording.voicechat.util.AudioPlayingUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRandomRepeatGoal extends Goal {

    private final Parrot parrot;
    private ParrotAudioStorage parrotAudioStorage;
    private boolean inCooldown = false;
    public ParrotRandomRepeatGoal(Parrot parrot){
        this.parrot = parrot;
    }
    @Override
    public boolean canUse() {
        parrotAudioStorage = (ParrotAudioStorage) parrot;

        return !parrotAudioStorage.parrotsrepeatyourvoice$getSavedAudios().isEmpty() && !isInCooldown();
    }

    private boolean isInCooldown(){
        return inCooldown;
    }

    @Override
    public void start() {
        ParrotsRepeatYourVoice.LOGGER.info("Started Goal!");
        RecordedAudio audio = parrotAudioStorage.parrotsrepeatyourvoice$getRandomAudio();
        if (audio != null) {
            AudioPlayingUtil.playFromEntity(audio, parrot, ParrotsRepeatYourVoice.MOD_ID);
            inCooldown = true;
        }

        super.start();
    }
}
