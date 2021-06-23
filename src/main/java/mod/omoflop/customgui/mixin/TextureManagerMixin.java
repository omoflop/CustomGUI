package mod.omoflop.customgui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.omoflop.customgui.CustomGUIClient;
import mod.omoflop.customgui.CustomGUILoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
@Environment(EnvType.CLIENT)
public abstract class TextureManagerMixin {


    @Shadow protected abstract void bindTextureInner(Identifier id);

    @Inject(method = "bindTexture(Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"), cancellable = true)
    public void bindTextureMixin(Identifier id, CallbackInfo ci) {
        if (!id.getPath().contains("textures/gui")) return;
        Identifier newID = null;
        if (newID == null) newID = getContainerOverride(id);
        if (newID == null) newID = getAnimationOverride(id);
        if (newID != null) {
            final Identifier finalID = newID;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> {
                    this.bindTextureInner(finalID);
                });
            } else {
                this.bindTextureInner(finalID);
            }
            ci.cancel();
        }
    }

    //TODO: for everything below, make it good

    @Unique
    protected Identifier getContainerOverride(Identifier id) {
        if (id.equals(new Identifier("minecraft:textures/gui/container/gamemode_switcher.png")) || !id.getPath().contains("container") || id.getPath().contains("inventory")) return null;
        return CustomGUIClient.textureOverride;
    }

    @Unique
    private Identifier getAnimationOverride(Identifier id) {
        for (Identifier key: CustomGUILoader.animationOverrides.keySet()) {
            if (id.equals(key)) {
                final Identifier frame = getFrame(key);
                return frame;
            }
        }
        return null;
    }

    @Unique
    protected Identifier getFrame(Identifier key) {
        Identifier[] frames = CustomGUILoader.animationOverrides.get(key);
        int time = (int) (System.currentTimeMillis()/17);
        int frame = (time/ CustomGUILoader.animationSpeeds.get(key)) % frames.length;
        return frames[frame];
    }

}
