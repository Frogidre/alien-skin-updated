package com.alienskin;

import net.minecraft.util.Identifier;

public final class AlienSkin {
    public static final String MOD_ID = "alienskin";

    public static final Identifier IDLE = Identifier.of(MOD_ID, "textures/entity/alien.png");
    public static final Identifier TALK = Identifier.of(MOD_ID, "textures/entity/alien_talk.png");

    /** Toggled with the keybind. */
    public static volatile boolean enabled = true;

    private AlienSkin() {}
}
