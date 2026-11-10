package com.alienskin.mixin;

import com.alienskin.AlienSkin;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reshapes the player model: bulbous egg head, thin torso, long spindly arms. */
@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin {

    @Unique private boolean alienskin$applied;

    // Adds a big puffed-up "cranium" box on top of the head so it is not just a square.
    // It reuses the head's texture area, so the face is stretched over the big skull.
    @Inject(
            method = "getTexturedModelData(Lnet/minecraft/client/model/Dilation;Z)Lnet/minecraft/client/model/ModelData;",
            at = @At("RETURN")
    )
    private static void alienskin$addCranium(Dilation dilation, boolean slim, CallbackInfoReturnable<ModelData> cir) {
        ModelPartData head = cir.getReturnValue().getRoot().getChild("head");
        head.addChild(
                "alien_cranium",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -12.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(1.8f)),
                ModelTransform.NONE
        );
        // smaller cap on top so the skull tapers like an egg
        head.addChild(
                "alien_cap",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -14.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.7f)),
                ModelTransform.NONE
        );

        // claws on the hands (3 spikes each) - texture area (56,0)
        for (String arm : new String[]{"left_arm", "right_arm"}) {
            float cx = arm.equals("left_arm") ? 0.5f : -1.5f; // centre of the hand (slim/wide close enough)
            ModelPartData a = cir.getReturnValue().getRoot().getChild(arm);
            for (int i = 0; i < 3; i++) {
                a.addChild("alien_claw" + i,
                        ModelPartBuilder.create().uv(56, 0).cuboid(cx, 10.0f, -2.0f + i * 1.5f, 1.5f, 4.0f, 1.2f),
                        ModelTransform.NONE);
            }
        }
        // toes on the feet (3 long toes each) - texture area (44,8)
        for (String leg : new String[]{"left_leg", "right_leg"}) {
            ModelPartData l = cir.getReturnValue().getRoot().getChild(leg);
            for (int i = 0; i < 3; i++) {
                l.addChild("alien_claw" + i,
                        ModelPartBuilder.create().uv(44, 8).cuboid(-2.0f + i * 1.4f, 10.5f, -6.0f, 1.2f, 1.5f, 4.0f),
                        ModelTransform.NONE);
            }
        }
    }

    @Inject(
            method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V",
            at = @At("TAIL")
    )
    private void alienskin$reshape(PlayerEntityRenderState state, CallbackInfo ci) {
        PlayerEntityModel m = (PlayerEntityModel) (Object) this;
        BipedEntityModel<?> b = m;

        ModelPart cranium = b.head.hasChild("alien_cranium") ? b.head.getChild("alien_cranium") : null;
        ModelPart cap = b.head.hasChild("alien_cap") ? b.head.getChild("alien_cap") : null;
        alienskin$showClaws(b.leftArm, AlienSkin.enabled);
        alienskin$showClaws(b.rightArm, AlienSkin.enabled);
        alienskin$showClaws(b.leftLeg, AlienSkin.enabled);
        alienskin$showClaws(b.rightLeg, AlienSkin.enabled);

        if (AlienSkin.enabled) {
            alienskin$applied = true;
            if (cranium != null) cranium.visible = true;
            if (cap != null) cap.visible = true;
            b.hat.visible = false;
            //                         x     y     z
            scale(b.head,  1.10f, 1.10f, 1.10f);
            scale(b.body,  0.60f, 0.95f, 0.50f);
            scale(m.jacket, 0.60f, 0.95f, 0.50f);
            scale(b.leftArm,  0.50f, 1.45f, 0.50f);
            scale(b.rightArm, 0.50f, 1.45f, 0.50f);
            scale(m.leftSleeve,  0.50f, 1.45f, 0.50f);
            scale(m.rightSleeve, 0.50f, 1.45f, 0.50f);
            scale(b.leftLeg,  0.55f, 1.00f, 0.55f);
            scale(b.rightLeg, 0.55f, 1.00f, 0.55f);
            scale(m.leftPants,  0.55f, 1.00f, 0.55f);
            scale(m.rightPants, 0.55f, 1.00f, 0.55f);
        } else {
            if (cranium != null) cranium.visible = false;
            if (cap != null) cap.visible = false;
            if (alienskin$applied) {
                alienskin$applied = false;
                ModelPart[] all = {b.head, b.hat, b.body, m.jacket, b.leftArm, b.rightArm,
                        m.leftSleeve, m.rightSleeve, b.leftLeg, b.rightLeg, m.leftPants, m.rightPants};
                for (ModelPart p : all) scale(p, 1f, 1f, 1f);
            }
        }
    }

    @Unique
    private static void alienskin$showClaws(ModelPart limb, boolean show) {
        for (int i = 0; i < 3; i++) {
            String n = "alien_claw" + i;
            if (limb.hasChild(n)) limb.getChild(n).visible = show;
        }
    }

    @Unique
    private static void scale(ModelPart part, float x, float y, float z) {
        part.xScale = x;
        part.yScale = y;
        part.zScale = z;
    }
}
