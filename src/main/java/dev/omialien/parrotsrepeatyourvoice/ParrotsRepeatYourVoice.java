package dev.omialien.parrotsrepeatyourvoice;

import com.mojang.logging.LogUtils;
import dev.omialien.parrotsrepeatyourvoice.config.ParrotsRepeatYourVoiceServerConfigs;
import dev.omialien.parrotsrepeatyourvoice.registry.ParrotDataComponents;
import dev.omialien.parrotsrepeatyourvoice.voicechat.AudioStorage;
import dev.omialien.voicechatrecording.api.VoiceChatRecordingApi;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ParrotsRepeatYourVoice.MOD_ID)
public class ParrotsRepeatYourVoice {
    public static final String MOD_ID = "yappingparrots";
    public static VoiceChatRecordingApi RECORDING_API = null;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AudioStorage AUDIOS = new AudioStorage();
    public ParrotsRepeatYourVoice(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ParrotsRepeatYourVoiceServerConfigs.SPEC);

        ParrotDataComponents.register(modEventBus);
    }

}
