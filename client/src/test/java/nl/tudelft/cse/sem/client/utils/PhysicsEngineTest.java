package nl.tudelft.cse.sem.client.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import nl.tudelft.cse.sem.client.gamelogic.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PhysicsEngineTest {

    transient PhysicsEngine physicsEngine;

    @BeforeEach
    void before() {
        physicsEngine = mock(PhysicsEngine.class);
    }

    @Test
    void tableGetters() {
        doCallRealMethod().when(physicsEngine).setTable(any());
        when(physicsEngine.getHelpLine()).thenCallRealMethod();
        when(physicsEngine.getCue()).thenCallRealMethod();
        when(physicsEngine.getCueBall()).thenCallRealMethod();
        when(physicsEngine.getRenderables()).thenCallRealMethod();

        Table table = mock(Table.class);
        physicsEngine.setTable(table);

        physicsEngine.getRenderables();
        physicsEngine.getCueBall();
        physicsEngine.getCue();
        physicsEngine.getHelpLine();

        verify(table).getRenderables();
        verify(table).getCue();
        verify(table).getHelpLine();
        verify(table).getCueBall();
    }

    @Test
    void dispose() {
        doCallRealMethod().when(physicsEngine).dispose();
        doCallRealMethod().when(physicsEngine).setDynamicsWorld(any());
        doCallRealMethod().when(physicsEngine).setConstraintSolver(any());
        doCallRealMethod().when(physicsEngine).setBroadPhase(any());
        doCallRealMethod().when(physicsEngine).setDispatcher(any());
        doCallRealMethod().when(physicsEngine).setCollisionConfig(any());
        doCallRealMethod().when(physicsEngine).setTable(any());

        btDynamicsWorld dynamicsWorld = mock(btDynamicsWorld.class);
        physicsEngine.setDynamicsWorld(dynamicsWorld);

        btConstraintSolver constraintSolver = mock(btConstraintSolver.class);
        physicsEngine.setConstraintSolver(constraintSolver);

        btBroadphaseInterface broadPhase = mock(btBroadphaseInterface.class);
        physicsEngine.setBroadPhase(broadPhase);

        btDispatcher dispatcher = mock(btDispatcher.class);
        physicsEngine.setDispatcher(dispatcher);

        btCollisionConfiguration collisionConfig = mock(btCollisionConfiguration.class);
        physicsEngine.setCollisionConfig(collisionConfig);


        Table table = mock(Table.class);
        physicsEngine.setTable(table);

        physicsEngine.dispose();

        verify(dynamicsWorld).dispose();
        verify(constraintSolver).dispose();
        verify(broadPhase).dispose();
        verify(dispatcher).dispose();
        verify(collisionConfig).dispose();
        verify(table).dispose();

    }
}
