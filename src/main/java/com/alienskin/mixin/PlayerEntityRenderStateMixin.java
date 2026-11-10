package com.alienskin.mixin;

import com.alienskin.AlienTalker;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements AlienTalker {
    private boolean alienskin$talking;

    @Override
    public boolean alienskin$isTalking() {
        return alienskin$talking;
    }

    @Override
    public void alienskin$setTalking(boolean talking) {
        this.alienskin$talking = talking;
    }
}
