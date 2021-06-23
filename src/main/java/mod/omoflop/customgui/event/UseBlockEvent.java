package mod.omoflop.customgui.event;

import mod.omoflop.customgui.data.OverrideManager;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockEvent implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Make sure the client is the one interacting with a block
        if (player.equals(client.player)) {
            // Reset the texture override
            OverrideManager.textureOverride = null;

            // Get the interacted block
            BlockState blockState  = world.getBlockState(hitResult.getBlockPos());
            Identifier override = OverrideManager.getContainer(blockState);

            if (override != null) OverrideManager.textureOverride = override;
        }
        return ActionResult.PASS;
    }
}
