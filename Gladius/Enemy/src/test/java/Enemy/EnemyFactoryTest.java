package Enemy;

import Common.data.GameData;
import Common.data.World;
import Common.tools.FileLoader;
import CommonEnemy.Enemy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class EnemyFactoryTest {
    World world;
    GameData gameData;
    Enemy enemy;

    @BeforeEach
    public void setUp() throws Exception {
        world = new World();
        FileLoader.loadFile("Map.tmx", this.getClass());
        world.setCsvMap(FileLoader.fetchData("Map.tmx"));
        gameData = mock(GameData.class);
        enemy = new Enemy("",0,0);
        world.addEntity(enemy);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void startTest() throws Exception {
        EnemyFactory enemyFactory = new EnemyFactory();

        //When wavenumber is 10, there will spawn 14 enemies
        enemyFactory.spawn(gameData, world, 10);

        //Pause this thread in some time to make sure spawn thread has completed
        Thread.sleep(5000);
        assertEquals(world.getEntities().size(), 14);
    }

    @Test
    public void stopTest() throws Exception {
        EnemyFactory enemyFactory = new EnemyFactory();

        int entitiesBeforeStop = world.getEntities().size();
        assertNotEquals(0, entitiesBeforeStop);

        enemyFactory.stop(world);

        int entitiesAfterStop = world.getEntities().size();

        assertEquals(0, entitiesAfterStop);
    }


}