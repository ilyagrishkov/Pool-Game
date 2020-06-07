package nl.tudelft.cse.sem.client.gamelogic.gameobjects.helpline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.badlogic.gdx.graphics.g3d.Model;
import org.junit.jupiter.api.Test;


class HelpLineFactoryTest {

    @Test
    void dispose() {
        HelpLineFactory helpLineFactory = new HelpLineFactory();
        Model model = mock(Model.class);
        helpLineFactory.setModel(model);

        helpLineFactory.dispose();

        verify(model, times(1)).dispose();
    }
}