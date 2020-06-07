package nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline;

import static nl.tudelft.cse.sem.client.utils.Constants.INITIAL_HELP_LINE_LENGTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelpLineFactory implements Disposable {

    private transient Model model;

    /**
     * Constructs a help line instance.
     *
     * @return - help line instance
     */
    public HelpLine construct() {
        ModelBuilder modelBuilder = new ModelBuilder();

        final Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));

        this.model = modelBuilder.createCylinder(0.3f, INITIAL_HELP_LINE_LENGTH, 0.3f,
                20, material, VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal);

        return new HelpLine(this.model);
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}
