package com.alienskin.voice;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.minecraft.client.MinecraftClient;

/** Loaded by Simple Voice Chat (entrypoint "voicechat"). Only runs if voice chat is installed. */
public class AlienVoicePlugin implements VoicechatPlugin {
    private static final double MIC_THRESHOLD = 400.0; // RMS of 16-bit samples

    @Override
    public String getPluginId() {
        return "alienskin";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        // Your own microphone
        registration.registerEvent(ClientSoundEvent.class, event -> {
            short[] audio = event.getRawAudio();
            if (audio == null || audio.length == 0) return;
            double sum = 0;
            for (short s : audio) sum += (double) s * s;
            if (Math.sqrt(sum / audio.length) < MIC_THRESHOLD) return;
            var player = MinecraftClient.getInstance().player;
            if (player != null) VoiceTracker.mark(player.getUuid());
        });

        // Other players (proximity, group, and static audio)
        registration.registerEvent(ClientReceiveSoundEvent.EntitySound.class, e -> VoiceTracker.mark(e.getId()));
        registration.registerEvent(ClientReceiveSoundEvent.LocationalSound.class, e -> VoiceTracker.mark(e.getId()));
        registration.registerEvent(ClientReceiveSoundEvent.StaticSound.class, e -> VoiceTracker.mark(e.getId()));
    }
}
