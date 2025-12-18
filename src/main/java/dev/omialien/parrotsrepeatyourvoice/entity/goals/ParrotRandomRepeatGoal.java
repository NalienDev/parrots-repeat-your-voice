package dev.omialien.parrotsrepeatyourvoice.entity.goals;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.config.ParrotsRepeatYourVoiceServerConfigs;
import dev.omialien.parrotsrepeatyourvoice.mixinutil.ParrotAudioStorage;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRandomRepeatGoal extends Goal {

    private final Parrot parrot;
    private ParrotAudioStorage parrotAudioStorage;
    private long cooldownEndsAt = -1;
    public ParrotRandomRepeatGoal(Parrot parrot){
        this.parrot = parrot;
        this.cooldownEndsAt = 0;
    }
    @Override
    public boolean canUse() {
        ParrotsRepeatYourVoice.LOGGER.debug("checking if canuse: {} vs {}", this.cooldownEndsAt, this.parrot.level().getGameTime());
        parrotAudioStorage = (ParrotAudioStorage) parrot;
        return parrotAudioStorage.yappingparrots$audioCount() > 0 && !isInCooldown();
    }

    private boolean isInCooldown(){
        return this.parrot.level().getGameTime() < this.cooldownEndsAt;
    }

    @Override
    public void start() {
        ParrotsRepeatYourVoice.LOGGER.info("Started Goal!");
        IRecordedAudio audio = ParrotsRepeatYourVoice.AUDIOS.getAudio(parrotAudioStorage.yappingparrots$getRandomAudio());
        if (audio != null) {
            ParrotsRepeatYourVoice.LOGGER.info("Started Goal! {}", audio.getDuration());
            AudioPlayingUtil.playFromEntity(audio, parrot, ParrotsRepeatYourVoice.MOD_ID);
            // TODO random time
            this.cooldownEndsAt = this.parrot.level().getGameTime() + ParrotsRepeatYourVoiceServerConfigs.AUDIO_COOLDOWN.get();
        }
        super.start();
    }
}
