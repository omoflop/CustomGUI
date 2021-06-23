package mod.omoflop.customgui;

import mod.omoflop.customgui.data.ResourceLoader;
import mod.omoflop.customgui.event.UseBlockEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class CustomGUIClient implements ClientModInitializer {

	public static void warn(Object s, Object... args) {
		print("[WARN] " + s, args);
	}
	public static void print(Object s, Object... args) {
		System.out.printf("[Custom GUI] %s\n", String.format(s.toString(),args));
	}

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourceLoader());
		UseBlockCallback.EVENT.register(new UseBlockEvent());
	}
}
