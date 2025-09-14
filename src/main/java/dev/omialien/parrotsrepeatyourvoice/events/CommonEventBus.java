package dev.omialien.parrotsrepeatyourvoice.events;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import dev.omialien.parrotsrepeatyourvoice.config.ParrotsRepeatYourVoiceServerConfigs;
import dev.omialien.parrotsrepeatyourvoice.entity.ParrotAudioStorage;
import dev.omialien.voicechat_recording.voicechat.RecordedAudio;
import dev.omialien.voicechat_recording.voicechat.events.AudioLoadedEvent;
import dev.omialien.voicechat_recording.voicechat.events.AudioRecordedEvent;
import dev.omialien.voicechat_recording.voicechat.events.MicPacketReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Parrot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = ParrotsRepeatYourVoice.MOD_ID)
public class CommonEventBus {
    @SubscribeEvent
    public static void onMicrophonePacket(MicPacketReceivedEvent event) {
        // do something
    }
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event){
        // save all the audios
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event){
        // load all the audios
    }

    @SubscribeEvent
    private static void onAudioRecordedEvent(AudioRecordedEvent event){
        if(filterAudio(event.getAudio())){
            List<Parrot> parrots = withinAParrotsRange(event.getAudio().getPlayerUUID());
            if (parrots == null || parrots.isEmpty()) {
                return;
            }

            parrots.forEach(parrot -> {
                ParrotAudioStorage parrotAudioStorage = (ParrotAudioStorage) parrot;
                if (parrotAudioStorage.parrotsrepeatyourvoice$getSavedAudios().size() >= ParrotsRepeatYourVoiceServerConfigs.RECORDING_LIMIT.get()){
                    parrotAudioStorage.parrotsrepeatyourvoice$removeRandomAudio();
                }
                parrotAudioStorage.parrotsrepeatyourvoice$addSavedAudio(event.getAudio().getId());
                ParrotsRepeatYourVoice.LOGGER.debug("Audio recorded and stored!");
            });
        }
    }

    @SubscribeEvent
    private static void onAudioLoadedEvent(AudioLoadedEvent event){
        // load the audios to the entities that learned them
    }

    private static boolean filterAudio(RecordedAudio audio) {
        double duration = audio.getDuration();
        if (duration <= 0.5) {
            return false;
        } else if (duration > 4.0) {
            return false;
        } else if (audio.getActiveSamples() <= 0) {
            return false;
        } else {
            return audio.getRms() >= 500.0;
        }
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
