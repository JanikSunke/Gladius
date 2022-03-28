package Player;

import Common.data.Entity;
import Common.data.GameData;
import Common.data.GameKeys;
import Common.data.World;
import Common.data.entityparts.LifePart;
import Common.data.entityparts.MovingPart;
import Common.services.IEntityProcessingService;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class PlayerControlSystem implements IEntityProcessingService {

    //TODO implement attack feature

    @Override
    public void process(GameData gameData, World world) {

        for (Entity entity : world.getEntities(Player.class)){

            MovingPart movingPart = entity.getPart(MovingPart.class);
            //LifePart lifePart = entity.getPart(LifePart.class);

            movingPart.setLeft(gameData.getKeys().isDown(GameKeys.LEFT));
            movingPart.setRight(gameData.getKeys().isDown(GameKeys.RIGHT));
            movingPart.setUp(gameData.getKeys().isDown(GameKeys.UP));
            movingPart.setDown(gameData.getKeys().isDown(GameKeys.DOWN));
/*
            Animation animation;

            if(entity.getTexture() == null){
                Array<TextureRegion> textures = new Array<>();
                textures.add(new TextureRegion(playerTexture,0,0,32,32));
                textures.add(new TextureRegion(playerTexture,64,32,32,32));
                animation = new Animation(500, textures);

            } */

            movingPart.process(gameData, entity);

        }
    }


}
