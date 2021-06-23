package mod.omoflop.customgui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class CustomGUIClient implements ClientModInitializer {

	public static Identifier textureOverride;

	public static void warn(String s, Object... args) {
		print("[WARN]" + s, args);
	}

	public static void print(String s, Object... args) {
		System.out.printf("[Custom GUI] %s\n", String.format(s,args));
	}

	@Override
	public void onInitializeClient() {
		CustomGUILoader.initialize();

		//TODO: separate logic from the client class
		UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (player.equals(client.player)) {
				textureOverride = null;
				BlockState  blockState  = world.getBlockState(hitResult.getBlockPos());
				BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
				String stateString = blockState.toString();
				if (stateString.contains("["))
					stateString = stateString.substring(stateString.indexOf("[")+1, stateString.indexOf("]"));
				else
					stateString = "";

				String clickedBlockName = blockState.getBlock().toString();
				Identifier clickedBlockID = new Identifier(clickedBlockName.substring(clickedBlockName.indexOf("{")+1, clickedBlockName.indexOf("}")));
				for (var i : CustomGUILoader.containerOverrides.keySet()) {
					if (clickedBlockID.equals(i.block_id())) {
						boolean has = true;
						if (i.wanted_states() != null) {
							for (var j : i.wanted_states()) {
								if (!stateString.contains(j)) {
									System.out.println("nope");
									has = false;
									break;
								}
							}
						}
						if (has) {
							textureOverride = CustomGUILoader.containerOverrides.get(i);
							break;
						}
					}
				}

			}

			/**
			 *      HitResult look = client.player.raycast(10, client.getTickDelta(), false);
			 *         BlockState lookingAt = client.world.getBlockState(new BlockPos(look.getPos()));
			 *         System.out.println(lookingAt.getBlock().getTranslationKey());
			 *
			 *         ScreenHandler screenHandler = client.player.currentScreenHandler;
			 *         ScreenHandlerType screenType = screenHandler.getType();
			 *
			 *         Screen screen = client.currentScreen;
			 *
			 *         Text t = screen.getTitle();
			 *         String screenTitle = (t instanceof TranslatableText ? ((TranslatableText) t).getKey() : t.getString());
			 */

			return ActionResult.PASS;
		}));

	}
}
