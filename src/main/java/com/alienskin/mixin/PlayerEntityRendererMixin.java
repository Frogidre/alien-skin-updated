package com.alienskin.mixin;

import com.alienskin.AlienSkin;
import com.alienskin.AlienTalker;
import com.alienskin.voice.VoiceTracker;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    // Copy "is this player talking right now" from the voice tracker into the render state.
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void alienskin$talking(PlayerLikeEntity entity, PlayerEntityRenderState state, float tickProgress, CallbackInfo ci) {
        ((AlienTalker) (Object) state).alienskin$setTalking(VoiceTracker.isMouthOpen(entity.getUuid()));
    }

    // Swap the skin: closed mouth / open mouth.
    @Inject(
            method = "getTexture(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void alienskin$texture(PlayerEntityRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (!AlienSkin.enabled) return;
        boolean open = ((AlienTalker) (Object) state).alienskin$isTalking();
        cir.setReturnValue(open ? AlienSkin.TALK : AlienSkin.IDLE);
    }
}
