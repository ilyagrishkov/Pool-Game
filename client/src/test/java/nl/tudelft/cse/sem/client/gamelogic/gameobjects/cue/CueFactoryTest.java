package nl.tudelft.cse.sem.client.gamelogic.gameobjects.cue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import nl.tudelft.cse.sem.client.gamelogic.gameobjects.util.MaterialLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CueFactoryTest {

    @Test
    void constructor() {
        MaterialLoader materialLoader = mock(MaterialLoader.class);
        CueFactory factory = new CueFactory(materialLoader);
        assertEquals(factory.getMaterialLoader(), materialLoader);
    }

    @Test
    void dispose() {
        MaterialLoader materialLoader = mock(MaterialLoader.class);
        CueFactory factory = new CueFactory(materialLoader);
        Model model = mock(Model.class);
        Texture texture = mock(Texture.class);
        factory.setModel(model);
        factory.setTexture(texture);

        factory.dispose();

        verify(model, times(1)).dispose();
        verify(texture, times(1)).dispose();
    }
}
