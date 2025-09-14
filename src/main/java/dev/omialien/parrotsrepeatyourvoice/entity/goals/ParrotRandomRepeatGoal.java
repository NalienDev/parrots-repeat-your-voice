package dev.omialien.parrotsrepeatyourvoice.entity.goals;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.entity.ParrotAudioStorage;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRandomRepeatGoal extends Goal {

    private final Parrot parrot;
    public ParrotRandomRepeatGoal(Parrot parrot){
        this.parrot = parrot;
    }
    @Override
    public boolean canUse() {
        ParrotAudioStorage parrotAudioStorage = (ParrotAudioStorage) parrot;

        return !parrotAudioStorage.parrotsrepeatyourvoice$getSavedAudios().isEmpty();
    }

    @Override
    public void start() {
        ParrotsRepeatYourVoice.LOGGER.info("Started Goal!");
        super.start();
    }
}
