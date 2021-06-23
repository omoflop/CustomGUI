package mod.omoflop.customgui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.omoflop.customgui.CustomGUIClient;
import mod.omoflop.customgui.data.OverrideManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mojang.blaze3d.systems.RenderSystem._setShaderTexture;
import static com.mojang.blaze3d.systems.RenderSystem.recordRenderCall;

@Mixin(RenderSystem.class)
@Environment(EnvType.CLIENT)
public abstract class RenderSystemMixin {

    @Inject(method = "setShaderTexture(ILnet/minecraft/util/Identifier;)V", at = @At("HEAD"), cancellable = true)
    private static void setShaderTextureMixin(int i, Identifier id, CallbackInfo ci) {

        if (!id.getPath().contains("textures/gui")) return;
        Identifier newID = OverrideManager.getOverride(id);

        if (newID != null) {
            final Identifier finalID = newID;
            if (!RenderSystem.isOnRenderThread()) {
                recordRenderCall(() -> {
                    _setShaderTexture(i, finalID);
                });
            } else {
                _setShaderTexture(i, finalID);
            }
            ci.cancel();
        }
    }
}
