package mod.omoflop.customgui.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.omoflop.customgui.CustomGUIClient;
import mod.omoflop.customgui.data.OverrideManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.JsonHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier RESOURCE_LOADER_ID = new Identifier("cgui", "gui_overrides");

    public void reload(ResourceManager manager) {
        // Clear previously loaded data (if any)
        OverrideManager.clear();

        // Find our custom assets located in textures/gui
        for (Identifier id : manager.findResources("textures/gui", path -> path.endsWith(".json"))) {

            // Gets the relative texture identifier for our asset
            Identifier texture_id = new Identifier(id.toString().replace(".json", ".png"));

            try {
                // Get the resource
                Resource r = manager.getResource(id);

                // Read our resource as a JSON
                String data = inputStreamToString(r.getInputStream());
                JsonObject jsonObject = JsonHelper.deserialize(data);

                // To make sure it's from this mod, test if it has a member named "cgui"
                if (jsonObject.has("cgui")) {
                    JsonObject assetData = jsonObject.getAsJsonObject("cgui");

                    // All valid files have a type member
                    if (assetData.has("type")) {
                        String type = assetData.get("type").getAsString();

                        // Load the correct format based on the type
                        switch (type) {
                            case "animation": parseAnimationData(assetData, id, texture_id); break;
                            case "container": parseBlockData(assetData, id, texture_id); break;
                            default: CustomGUIClient.warn("Unknown type: \"%s\"", type); break;
                        }
                    } else {
                        // Warn the client that they formatted their file incorrectly
                        CustomGUIClient.warn("No type entry found for file: \"%s\"", id);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                CustomGUIClient.warn("Caught exception: %s", ex.toString());
            }
        } 
        try {
          InputStream s = manager.getResource(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).getInputStream();
          byte[] buffer = new byte[s.available()];
          s.read(buffer);
          File targetFile = new File("src/main/resources/" + SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE + ".png");
          OutputStream outStream = new FileOutputStream(targetFile);
          outStream.write(buffer);
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    private static void parseAnimationData(JsonObject cguiData, Identifier dataID, Identifier textureID) {
        int animationRate = 1;
        // Get the rate of animation if specified
        if (cguiData.has("rate")) {
            animationRate = cguiData.get("rate").getAsInt();
        }

        JsonArray frameArray = cguiData.getAsJsonArray("frames");
        Identifier[] frames = new Identifier[frameArray.size()];

        // Get each frame and find it's texture identifier
        for (int i = 0; i < frameArray.size(); i++) {
            frames[i] = new Identifier(dataID.getNamespace(), "textures/gui/" + frameArray.get(i).getAsString());
        }

        OverrideManager.addAnimation(textureID, new OverrideManager.GUIAnimation(frames, animationRate));
    }

    private static void parseBlockData(JsonObject cguiData, Identifier dataID, Identifier textureID) {
        if (!cguiData.has("block")) {
            CustomGUIClient.warn("\"block\" member is undefined in %s", dataID);
            return;
        }
        Identifier blockID = new Identifier(cguiData.get("block").getAsString());

        String[] wantedStates = null;
        // If the block has state requirements
        if (cguiData.has("state")) {
            // Get the blockstate object
            JsonObject state_holder = cguiData.getAsJsonObject("state");

            // Populate an array with each state formatted into a string
            wantedStates = new String[state_holder.size()];
            int i = 0;
            for (var s : state_holder.entrySet()) {
                wantedStates[i] = (s.getKey() + "=" + s.getValue()).replace("\"","");
                i++;
            }
        }
        OverrideManager.addContainer(new OverrideManager.SimpleBlockstate(blockID, wantedStates), textureID);
    }



    /**
     * Converts an input stream into a string, for reading JSONs and other files
     * @param stream
     * @return
     * @throws IOException
     */
    private static String inputStreamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = stream.read(buffer)) != -1;) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    /**
     * @return The unique identifier of this listener.
     */
    @Override
    public Identifier getFabricId() {
        return RESOURCE_LOADER_ID;
    }
}
