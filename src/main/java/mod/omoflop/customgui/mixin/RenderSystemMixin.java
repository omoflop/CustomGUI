package mod.omoflop.customgui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.omoflop.customgui.data.OverrideManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mojang.blaze3d.systems.RenderSystem._setShaderTexture;
import static com.mojang.blaze3d.systems.RenderSystem.recordRenderCall;

@Mixin(value = RenderSystem.class, remap = false)
@Environment(EnvType.CLIENT)
public abstract class RenderSystemMixin {

    @Inject(method = "setShaderTexture(ILnet/minecraft/util/Identifier;)V", at = @At("HEAD"), cancellable = true)
    private static void setShaderTextureMixin(int i, Identifier id, CallbackInfo ci) {
        
        if (!id.getPath().contains("textures/gui")) return;
        Identifier newID = OverrideManager.getOverride(id);

        if (newID != null) {
            final Identifier finalID = newID;
            if (!RenderSystem.isOnRenderThread()) {
                recordRenderCall(() -> _setShaderTexture(i, finalID));
            } else {
                _setShaderTexture(i, finalID);
            }
            ci.cancel();
        }
    }
}
