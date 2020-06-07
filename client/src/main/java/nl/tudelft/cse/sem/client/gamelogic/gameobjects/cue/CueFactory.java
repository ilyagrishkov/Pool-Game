package nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue;

import static nl.tudelft.cse.sem.client.utils.Constants.CUE_LENGTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;

@Getter
@Setter
public class CueFactory implements Disposable {

    private transient Model model;
    private transient Texture texture;
    private transient MaterialLoader materialLoader;

    public CueFactory(MaterialLoader materialLoader) {
        this.materialLoader = materialLoader;
    }

    /**
     * Constructs a cue.
     *
     * @return - a cue instance
     */
    public Cue construct() {
        ModelBuilder modelBuilder = new ModelBuilder();
        this.texture = materialLoader.load("cue.jpg");

        final Material material = materialLoader.createMaterial(false, true, texture);

        this.model = modelBuilder.createCylinder(2, CUE_LENGTH, 2,
                20, material, VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);

        return new Cue(this.model);
    }

    @Override
    public void dispose() {
        model.dispose();
        texture.dispose();
    }
}
