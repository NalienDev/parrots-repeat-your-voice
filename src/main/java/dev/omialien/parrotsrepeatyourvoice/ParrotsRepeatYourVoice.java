package dev.omialien.parrotsrepeatyourvoice;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ParrotsRepeatYourVoice.MODID)
public class ParrotsRepeatYourVoice {

    public static final String MODID = "parrotsrepeatyourvoice";
    public static final Logger LOGGER = LogUtils.getLogger();
    public ParrotsRepeatYourVoice(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
