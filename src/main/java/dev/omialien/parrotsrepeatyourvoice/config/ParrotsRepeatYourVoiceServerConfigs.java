package dev.omialien.parrotsrepeatyourvoice.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ParrotsRepeatYourVoiceServerConfigs {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.ConfigValue<Integer> RECORDING_LIMIT = BUILDER.define("maxSavedRecordings", 5);
    public static final ModConfigSpec SPEC = BUILDER.build();
}
