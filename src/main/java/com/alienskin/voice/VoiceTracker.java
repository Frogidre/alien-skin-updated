package com.alienskin.voice;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VoiceTracker {
    private static final Map<UUID, Long> LAST_VOICE = new ConcurrentHashMap<>();
    private static final long HOLD_MS = 220;   // how long after the last audio chunk we still count as talking
    private static final long FLAP_MS = 110;   // mouth open/close speed

    private VoiceTracker() {}

    public static void mark(UUID id) {
        if (id != null) LAST_VOICE.put(id, System.currentTimeMillis());
    }

    public static boolean isTalking(UUID id) {
        Long t = LAST_VOICE.get(id);
        return t != null && System.currentTimeMillis() - t < HOLD_MS;
    }

    /** True while talking AND during the "open" half of the flap cycle. */
    public static boolean isMouthOpen(UUID id) {
        return isTalking(id) && ((System.currentTimeMillis() / FLAP_MS) & 1L) == 0L;
    }
}
