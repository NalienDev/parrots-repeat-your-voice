package dev.omialien.parrotsrepeatyourvoice.events;

import dev.omialien.parrotsrepeatyourvoice.ParrotsRepeatYourVoice;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = ParrotsRepeatYourVoice.MOD_ID)
public class ServerLifecycleEvents {
    private static MinecraftServer serverInstance;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        serverInstance = event.getServer();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        serverInstance = null;
    }

    public static MinecraftServer getServer() {
        return serverInstance;
    }
}
