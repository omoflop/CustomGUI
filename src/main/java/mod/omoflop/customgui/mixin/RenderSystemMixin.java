package mod.omoflop.customgui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.omoflop.customgui.data.OverrideManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderSystem.class})
@Environment(EnvType.CLIENT)
public abstract class RenderSystemMixin {
  @Inject(method = {"setShaderTexture(ILnet/minecraft/util/Identifier;)V"}, at = {@At("HEAD")}, cancellable = true, remap = false)
  @Group(name = "setShaderMixin")
  private static void setShaderTextureDev(int i, Identifier id, CallbackInfo ci) {
    setShaderTextureMixin(i, id, ci);
  }
  
  @Inject(method = {"setShaderTexture(ILnet/minecraft/class_2960;)V"}, at = {@At("HEAD")}, cancellable = true, remap = false)
  @Group(name = "setShaderMixin")
  private static void setShaderTextureProd(int i, Identifier id, CallbackInfo ci) {
    setShaderTextureMixin(i, id, ci);
  }
  
  private static void setShaderTextureMixin(int i, Identifier id, CallbackInfo ci) {
    if (!id.getPath().contains("textures/gui"))
      return; 
    Identifier newID = OverrideManager.getOverride(id);
    if (newID != null) {
      Identifier finalID = newID;
      if (!RenderSystem.isOnRenderThread()) {
        RenderSystem.recordRenderCall(() -> RenderSystem._setShaderTexture(i, finalID));
      } else {
        RenderSystem._setShaderTexture(i, finalID);
      } 
      ci.cancel();
    } 
  }
}
