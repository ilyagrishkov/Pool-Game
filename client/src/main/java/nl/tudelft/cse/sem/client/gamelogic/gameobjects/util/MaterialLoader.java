package nl.tudelft.cse.sem.client.gamelogic.gameobjects.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class MaterialLoader {

    private static MaterialLoader instance = null;

    /**
     * Create instance of MaterialLoader class.
     *
     * @return - instance of the MaterialLoader class
     */
    public static MaterialLoader getInstance() {
        if (instance == null) {
            instance = new MaterialLoader();
        }
        return instance;
    }

    /**
     * Loads a texture.
     *
     * @param filename - name of texture file
     * @return - new Texture
     */
    public Texture load(String filename) {
        return new Texture(filename);
    }

    /**
     * Creates a material.
     *
     * @param noTexture  - true if material has solid color
     * @param noSpecular - true if material has only diffuse component
     * @param texture    - texture for the material
     * @return - new Material
     */
    public Material createMaterial(boolean noTexture, boolean noSpecular, Texture texture) {

        if (noTexture) {
            return new Material(ColorAttribute.createDiffuse(Color.BLACK));

        } else {
            if (noSpecular) {
                return new Material(TextureAttribute.createDiffuse(texture));

            } else {
                return new Material(TextureAttribute.createDiffuse(texture),
                        ColorAttribute.createSpecular(1, 1, 1, 1),
                        FloatAttribute.createShininess(64f));
            }
        }
    }
}
