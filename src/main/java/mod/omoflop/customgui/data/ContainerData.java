package mod.omoflop.customgui.data;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ContainerData(Identifier block_id, @Nullable String[] wanted_states) {
    public ContainerData(Identifier block_id, @Nullable String[] wanted_states) {
        this.block_id = block_id;
        this.wanted_states = wanted_states;
    }
}
