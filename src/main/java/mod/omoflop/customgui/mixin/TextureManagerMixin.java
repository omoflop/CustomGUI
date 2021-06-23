package mod.omoflop.customgui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.omoflop.customgui.data.OverrideManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
        Identifier newID = OverrideManager.getOverride(id);

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
}
