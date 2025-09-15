package dev.omialien.parrotsrepeatyourvoice.mixin;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.entity.ParrotAudioStorage;
import dev.omialien.parrotsrepeatyourvoice.entity.goals.ParrotRandomRepeatGoal;
import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Parrot.class)
public class ParrotMixin extends Mob implements ParrotAudioStorage {

    @Unique
    private final Set<UUID> parrotsrepeatyourvoice$savedAudios = new HashSet<>();

    protected ParrotMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals()V", at = @At("HEAD"))
    protected void parrotGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(2, new ParrotRandomRepeatGoal((Parrot)(Object)this));
    }

    @Override
    public void parrotsrepeatyourvoice$addSavedAudio(UUID uuid) {
        ParrotsRepeatYourVoice.LOGGER.debug("adding saved audio!");
        parrotsrepeatyourvoice$savedAudios.add(uuid);
    }

    @Override
    public void parrotsrepeatyourvoice$removeSavedAudio(UUID uuid) {
        ParrotsRepeatYourVoice.LOGGER.debug("removing saved audios!");
        parrotsrepeatyourvoice$savedAudios.remove(uuid);
    }

    @Override
    public Set<UUID> parrotsrepeatyourvoice$getSavedAudios() {
        return parrotsrepeatyourvoice$savedAudios;
    }

    @Override
    public void parrotsrepeatyourvoice$removeRandomAudio() {
        ParrotsRepeatYourVoice.LOGGER.debug("Removing random audio!");
        parrotsrepeatyourvoice$savedAudios.remove(
                parrotsrepeatyourvoice$savedAudios.stream().toList().get(
                        new Random().nextInt(parrotsrepeatyourvoice$savedAudios.size())
                )
        );
    }

    @Override
    public RecordedAudio parrotsrepeatyourvoice$getRandomAudio() {
        UUID randomUuid = parrotsrepeatyourvoice$savedAudios.stream().toList().get(new Random().nextInt(parrotsrepeatyourvoice$savedAudios.size()));
        return ParrotsRepeatYourVoice.AUDIOS.getAudio(randomUuid);
    }
}
