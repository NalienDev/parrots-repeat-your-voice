package dev.omialien.parrotsrepeatyourvoice.events;

import com.mojang.datafixers.util.Pair;
import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.config.ParrotsRepeatYourVoiceServerConfigs;
import dev.omialien.parrotsrepeatyourvoice.mixinutil.ParrotAudioStorage;
import dev.omialien.voicechatrecording.api.IRecordedAudio;
import dev.omialien.voicechatrecording.api.events.AudioLoadedEvent;
import dev.omialien.voicechatrecording.api.events.AudioRecordedEvent;
import dev.omialien.voicechatrecording.api.events.RecordingSetupEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Parrot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = ParrotsRepeatYourVoice.MOD_ID)
public class CommonEventBus {
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event){
        ParrotsRepeatYourVoice.AUDIOS.saveAllAudios();
    }

    @SubscribeEvent
    public static void onRecordingSetup(RecordingSetupEvent event) {
        ParrotsRepeatYourVoice.RECORDING_API = event.getApi();
        ParrotsRepeatYourVoice.LOGGER.debug("recording setup event");
        event.addCategory(
                ParrotsRepeatYourVoice.MOD_ID,
                "Yapping Parrots",
                "The volume of all yapping parrots", null
        );
        ParrotsRepeatYourVoice.LOGGER.debug("API: {}", ParrotsRepeatYourVoice.RECORDING_API.toString());
        ParrotsRepeatYourVoice.AUDIOS.loadAllAudios();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event){
    }

    @SubscribeEvent
    private static void onAudioRecordedEvent(AudioRecordedEvent event){
        IRecordedAudio audio = event.getAudio();
        if(audio.getFilterResult() == IRecordedAudio.FilterResult.PASSED){
            List<Parrot> parrots = withinAParrotsRange(audio.getPlayerUUID());
            if (parrots == null || parrots.isEmpty()) {
                return;
            }
            ParrotsRepeatYourVoice.AUDIOS.addAudio(audio);
            parrots.forEach(parrot -> {
                ParrotAudioStorage parrotAudioStorage = (ParrotAudioStorage) parrot;
                if (parrotAudioStorage.yappingparrots$audioCount() >= ParrotsRepeatYourVoiceServerConfigs.RECORDING_LIMIT.get()){
                    parrotAudioStorage.yappingparrots$removeRandomAudio();
                }
                parrotAudioStorage.yappingparrots$addSavedAudio(Pair.of(audio.getPlayerUUID(), audio.getId()));
                ParrotsRepeatYourVoice.LOGGER.debug("Audio recorded and stored!");
            });

        }
    }

    @SubscribeEvent
    private static void onAudioLoadedEvent(AudioLoadedEvent event){
        // TODO do we really need this? why not load the audios as the parrots that need those audios speak?
        //  with the audio loading cache, that shouldn't be a problem; just don't block the thread by waiting
        //  for the audios
        if(event.getLoadReason() == AudioLoadedEvent.LoadType.NAMESPACE && event.getNamespace().equals(ParrotsRepeatYourVoice.MOD_ID)) {
            ParrotsRepeatYourVoice.AUDIOS.addAudio(event.getAudio());
        }
        // load the audios to the entities that learned them
    }

    private static List<Parrot> withinAParrotsRange(UUID playerUUID) {
        MinecraftServer server = ServerLifecycleEvents.getServer();
        if (server == null) {
            return null;
        }

        ServerPlayer player = server.getPlayerList().getPlayer(playerUUID);
        if (player == null) {
            return null;
        }
        return player.level().getNearbyEntities(Parrot.class, TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(20, 20, 20));
    }

}
