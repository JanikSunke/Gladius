package Enemy;

import Common.data.Entity;
import Common.data.GameData;
import Common.data.World;
import Common.data.entityparts.*;
import Common.services.IGamePluginService;
import Common.tools.FileLoader;
import CommonEnemy.Enemy;

import java.util.Random;
import com.badlogic.gdx.graphics.Color;

public class EnemyPlugin implements IGamePluginService {
    private Entity enemy;

    @Override
    public void start(GameData gameData, World world) {
        enemy = createEnemy(gameData);
        world.addEntity(enemy);
    }

    private Entity createEnemy(GameData gamedata) {
        String file = "Minotaur.png";

        // radius should be texture width / 16
        Entity enemy = new Enemy(file, 6);
        enemy.add(new MovingPart(30));
        enemy.add(new LifePart(100, Color.RED));
        enemy.add(new AnimationPart());
        enemy.add(new StatsPart(20, 5));
        enemy.add(new SoundPart(gamedata));

        FileLoader.loadFile(file, getClass());

        //400 is max, 280 is min
        enemy.setX(new Random().nextInt((580 - 385) + 1) + 385);
        enemy.setY(new Random().nextInt((1000 - 350) + 1) + 350);

        return enemy;
    }

    @Override
    public void stop(GameData gameData, World world) {
        SoundPart soundPart = enemy.getPart(SoundPart.class);
        if (soundPart != null){
            soundPart.disposeSounds();
        }

        world.removeEntity(this.enemy);
    }
}
