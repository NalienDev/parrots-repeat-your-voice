package dev.omialien.parrotsrepeatyourvoice.mixin;

import com.mojang.datafixers.util.Pair;
import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.entity.goals.ParrotRandomRepeatGoal;
import dev.omialien.parrotsrepeatyourvoice.mixinutil.ParrotAudioStorage;
import dev.omialien.parrotsrepeatyourvoice.registry.ParrotDataComponents;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.util.AudioPlayingUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Parrot.class)
public class ParrotMixin extends Mob implements ParrotAudioStorage {
    protected ParrotMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals()V", at = @At("HEAD"))
    protected void parrotGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(2, new ParrotRandomRepeatGoal((Parrot)(Object)this));
    }

    @Override
    public boolean yappingparrots$executeSeedAction(ParrotDataComponents.SeedActions action) {
        // If return true, consumes the seed
        switch ( action ) {
            case REMEMBER -> {
                this.setData(ParrotDataComponents.REMEMBER_NEW_AUDIOS_ATTACHMENT.get(), true);
                return true;
            }
            case FORCEAUDIO -> {
                Optional<ParrotDataComponents.ParrotAudio> audioid = this.yappingparrots$getLastAudio();
                if ( audioid.isEmpty() ) {
                    return false;
                }
                IRecordedAudio audio = ParrotsRepeatYourVoice.AUDIOS.getAudio(audioid.get().asPair());
                AudioPlayingUtil.playFromEntity(audio, this, ParrotsRepeatYourVoice.MOD_ID);
                return true;
            }
            case FORGETAUDIO -> {
                Optional<ParrotDataComponents.ParrotAudio> audioid = this.yappingparrots$getLastAudio();
                if ( audioid.isEmpty() ) {
                    return false;
                }
                this.yappingparrots$removeSavedAudio(audioid.get().asPair());
                this.hurt(this.level().damageSources().generic(), 0.5f);
                return true;
            }
            case UNREMEMBER -> {
                this.setData(ParrotDataComponents.REMEMBER_NEW_AUDIOS_ATTACHMENT.get(), false);
                return true;
            }
            default -> {
                ParrotsRepeatYourVoice.LOGGER.error("Invalid SeedAction used: {}", action);
                return false;
            }
        }
    }

    @Override
    public Optional<ParrotDataComponents.ParrotAudio> yappingparrots$getLastAudio() {
        ParrotDataComponents.ParrotAudio audio = this.getData(ParrotDataComponents.LAST_AUDIO_PLAYED.get());
        if ( audio == null ) {
            return Optional.empty();
        }
        return Optional.of(audio);
    }

    public boolean yappingparrots$rememberNewAudios() {
        return this.getData(ParrotDataComponents.REMEMBER_NEW_AUDIOS_ATTACHMENT.get());
    }

    @Override
    public void yappingparrots$addSavedAudio(Pair<UUID, UUID> uuid) {
        if ( yappingparrots$rememberNewAudios() ) {
            List<ParrotDataComponents.ParrotAudio> lst = this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get());
            ParrotsRepeatYourVoice.LOGGER.debug("adding audio: {}", lst.size());
            lst.add(ParrotDataComponents.ParrotAudio.fromPair(uuid));
            ParrotsRepeatYourVoice.LOGGER.debug("added: {}", this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get()).size());
        }
    }

    @Override
    public void yappingparrots$removeSavedAudio(Pair<UUID, UUID> uuid) {
        if ( this.yappingparrots$rememberNewAudios() ) {
            List<ParrotDataComponents.ParrotAudio> lst = this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get());
            ParrotsRepeatYourVoice.LOGGER.debug("removing audio: {}", lst.size());
            lst.remove(ParrotDataComponents.ParrotAudio.fromPair(uuid));
            ParrotsRepeatYourVoice.LOGGER.debug("rmvd: {}", this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get()).size());
        }
    }

    @Override
    public int yappingparrots$audioCount() {
        return this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get()).size();
    }

    @Override
    public List<Pair<UUID, UUID>> yappingparrots$getSavedAudios() {
        return this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get()).stream().map(ParrotDataComponents.ParrotAudio::asPair).toList();
    }

    @Override
    public void yappingparrots$removeRandomAudio() {
        if ( this.yappingparrots$rememberNewAudios() ) {
            List<ParrotDataComponents.ParrotAudio> lst = this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get());
            int toRemove = random.nextInt(lst.size());
            // Remove the last audio added; if the target wasn't that,
            // then replace the target with the removed audio
            // This avoids having to move all audios to the right of the removed audio back one element,
            // at the cost of losing the chronological order of the audios, but we don't care about that i think
            ParrotDataComponents.ParrotAudio removed = lst.removeLast();
            if(toRemove < lst.size()) {
                lst.set(toRemove, removed);
            }
        }
    }

    @Override
    public Pair<UUID, UUID> yappingparrots$getRandomAudio() {
        List<ParrotDataComponents.ParrotAudio> lst = this.getData(ParrotDataComponents.PARROT_AUDIO_ATTACHMENT.get());
        return lst.get(random.nextInt(lst.size())).asPair();
    }
}
