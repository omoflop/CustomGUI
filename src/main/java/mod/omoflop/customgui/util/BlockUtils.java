package mod.omoflop.customgui.util;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

public class BlockUtils {
    public static String stateToString(BlockState blockState) {
        String str = blockState.toString();
        if (str.contains("[")) return str.substring(str.indexOf("[")+1, str.indexOf("]"));
        return "";
    }

    public static Identifier getBlockIdentifier(Block block) {
        String blockName = block.toString();
        return new Identifier(blockName.substring(blockName.indexOf("{")+1, blockName.indexOf("}")));
    }
}
