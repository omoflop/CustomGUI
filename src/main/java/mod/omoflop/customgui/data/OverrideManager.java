package mod.omoflop.customgui.data;

import mod.omoflop.customgui.CustomGUIClient;
import mod.omoflop.customgui.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;

public class OverrideManager {

    // The current custom gui texture
    public static Identifier textureOverride;

    private static HashMap<SimpleBlockstate, Identifier> containerOverrides = new HashMap<>();
    private static HashMap<Identifier, GUIAnimation> animationOverrides = new HashMap<>();

    public static Identifier getOverride(Identifier id) {
        if (textureOverride != null && !(id.equals(new Identifier("minecraft:textures/gui/container/gamemode_switcher.png")) || !id.getPath().contains("container") || id.getPath().contains("inventory"))) {
            return textureOverride;
        }
        return OverrideManager.getAnimation(id);
    }

    public record SimpleBlockstate(Identifier block_id, String[] states) {
        public SimpleBlockstate(Identifier block_id, String[] states) {
            this.block_id = block_id;
            this.states = states;
        }
    }
    public record GUIAnimation(Identifier[] frames, int frameRate) {
        public GUIAnimation(Identifier[] frames, int frameRate) {
            this.frames = frames;
            this.frameRate = frameRate;
        }
    }

    public static void clear() {
        containerOverrides.clear();
        animationOverrides.clear();
    }

    public static void addContainer(SimpleBlockstate state, Identifier id) {
        CustomGUIClient.print(id);
        CustomGUIClient.print("%s: %s", state.block_id, Arrays.toString(state.states));
        containerOverrides.put(state, id);     }
    public static void addAnimation(Identifier id, GUIAnimation animation) {
        CustomGUIClient.print(id);
        animationOverrides.put(id, animation); }

    public static Identifier getContainer(BlockState state) {
        Identifier blockID = BlockUtils.getBlockIdentifier(state.getBlock());
        String stateString = BlockUtils.stateToString(state);

        for (var i : containerOverrides.keySet()) {
            if (blockID.equals(i.block_id())) {
                boolean has = true;
                if (i.states() != null) {
                    for (var j : i.states()) {
                        if (!stateString.contains(j)) {
                            has = false;
                            break;
                        }
                    }
                }
                if (has) {
                    return containerOverrides.get(i);
                }
            }
        }

        return null;
    }

    public static Identifier getAnimation(Identifier id) {
        for (Identifier key: animationOverrides.keySet()) {
            if (id.equals(key)) {
                GUIAnimation animation = animationOverrides.get(key);
                Identifier[] frames = animation.frames;
                int time = (int) (System.currentTimeMillis()/17);
                int frame = (time/animation.frameRate) % frames.length;
                return frames[frame];
            }
        }
        return null;
    }
}