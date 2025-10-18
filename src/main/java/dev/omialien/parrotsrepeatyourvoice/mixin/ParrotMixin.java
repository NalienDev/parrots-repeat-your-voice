package dev.omialien.parrotsrepeatyourvoice.mixin;

import com.mojang.datafixers.util.Pair;
import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.entity.goals.ParrotRandomRepeatGoal;
import dev.omialien.parrotsrepeatyourvoice.mixinutil.ParrotAudioStorage;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
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
    private final List<Pair<UUID, UUID>> yappingparrots$savedAudios = new ArrayList<>();

    protected ParrotMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals()V", at = @At("HEAD"))
    protected void parrotGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(2, new ParrotRandomRepeatGoal((Parrot)(Object)this));
    }

    private static final String AUDIOS_DATA_NAME = ResourceLocation.fromNamespaceAndPath
            (
                    ParrotsRepeatYourVoice.MOD_ID,
                    "parrot_audios"
            ).toString();

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    protected void parrotAudiosAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        IntArrayTag uuidlist = new IntArrayTag(List.of());
        yappingparrots$savedAudios.forEach((uuid) -> {
            ParrotsRepeatYourVoice.LOGGER.debug("saving audio: {}", uuid);
            uuidlist.addAll(NbtUtils.createUUID(uuid.getFirst()));
            uuidlist.addAll(NbtUtils.createUUID(uuid.getSecond()));
        });
        compound.put(AUDIOS_DATA_NAME, uuidlist);
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    protected void parrotAudiosReadSaveData(CompoundTag compound, CallbackInfo ci) {
        ParrotsRepeatYourVoice.LOGGER.debug("reading save data...");
        IntArrayTag uuids = (IntArrayTag)compound.get(AUDIOS_DATA_NAME);
        if(uuids != null ){
            int[] total = uuids.getAsIntArray();
            for(int i = 0; i < total.length; i += 8) {
                UUID player = UUIDUtil.uuidFromIntArray(
                        Arrays.copyOfRange(total, i, i+4)
                );
                UUID audio = UUIDUtil.uuidFromIntArray(
                        Arrays.copyOfRange(total, i+4, i+8)
                );
                this.yappingparrots$addSavedAudio(Pair.of(player, audio));
            }
        }
    }

    @Override
    public void yappingparrots$addSavedAudio(Pair<UUID, UUID> uuid) {
        yappingparrots$savedAudios.add(uuid);
    }

    @Override
    public void yappingparrots$removeSavedAudio(Pair<UUID, UUID> uuid) {
        yappingparrots$savedAudios.remove(uuid);
    }

    @Override
    public List<Pair<UUID, UUID>> yappingparrots$getSavedAudios() {
        return yappingparrots$savedAudios;
    }

    @Override
    public void yappingparrots$removeRandomAudio() {
        int toRemove = random.nextInt(yappingparrots$savedAudios.size());
        // Remove the last audio added; if the target wasn't that,
        // then replace the target with the removed audio
        // This avoids having to move all audios to the right of the removed audio back one element,
        // at the cost of losing the chronological order of the audios, but we don't care about that i think
        Pair<UUID, UUID> removed = yappingparrots$savedAudios.removeLast();
        if(toRemove < yappingparrots$savedAudios.size()) {
            yappingparrots$savedAudios.set(toRemove, removed);
        }
    }

    @Override
    public Pair<UUID, UUID> yappingparrots$getRandomAudio() {
        return yappingparrots$savedAudios.get(random.nextInt(yappingparrots$savedAudios.size()));
    }
}
