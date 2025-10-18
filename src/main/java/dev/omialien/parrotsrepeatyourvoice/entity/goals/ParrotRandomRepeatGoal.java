package dev.omialien.parrotsrepeatyourvoice.entity.goals;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.mixinutil.ParrotAudioStorage;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
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
        return !parrotAudioStorage.yappingparrots$getSavedAudios().isEmpty() && !isInCooldown();
    }

    private boolean isInCooldown(){
        return inCooldown;
    }

    @Override
    public void start() {
        ParrotsRepeatYourVoice.LOGGER.info("Started Goal!");
        IRecordedAudio audio = ParrotsRepeatYourVoice.AUDIOS.getAudio(parrotAudioStorage.yappingparrots$getRandomAudio());
        if (audio != null) {
            ParrotsRepeatYourVoice.LOGGER.info("Started Goal! {}", audio.getDuration());
            AudioPlayingUtil.playFromEntity(audio, parrot, ParrotsRepeatYourVoice.MOD_ID);
            inCooldown = true;
        }

        super.start();
    }
}
