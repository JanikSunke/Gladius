package Player;

import Common.data.Entity;
import Common.data.GameData;
import Common.data.World;
import Common.data.entityparts.AnimationPart;
import Common.data.entityparts.LifePart;
import Common.data.entityparts.MovingPart;
import Common.data.entityparts.StatsPart;
import Common.services.IGamePluginService;
import Common.tools.FileLoader;
import CommonPlayer.Player;
import com.badlogic.gdx.graphics.Color;

public class PlayerPlugin implements IGamePluginService {
    private Entity player;

    @Override
    public void start(GameData gameData, World world) {
        player = createPlayer(gameData);
        world.addEntity(player);
    }

    private Entity createPlayer(GameData gamedata) {
        String file = "GladiatorSpriteSheet.png";

        // radius should be texture width / 16
        Entity player = new Player(file, 2);
        player.add(new MovingPart(100));
        player.add(new AnimationPart());
        player.add(new LifePart(300, Color.GREEN));
        player.add(new StatsPart(20, 5));
        FileLoader.loadFile(file, getClass());

        player.setX(800);
        player.setY(140);
        return player;
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.removeEntity(player);
    }
}
