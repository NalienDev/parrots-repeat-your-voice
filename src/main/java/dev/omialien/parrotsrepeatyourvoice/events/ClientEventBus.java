package dev.omialien.parrotsrepeatyourvoice.events;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ParrotsRepeatYourVoice.MOD_ID, dist = Dist.CLIENT)
public class ClientEventBus {
    public ClientEventBus(ModContainer container) {
        // TODO lang
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
