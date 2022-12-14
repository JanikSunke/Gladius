package Monster;

import Common.data.GameData;
import Common.data.SoundData;
import Common.data.World;
import Common.data.Entity;
import Common.data.entityparts.*;
import Common.services.IEntityFactoryService;
import Common.tools.FileLoader;
import com.badlogic.gdx.graphics.Color;
import CommonMonster.Monster;


import java.util.Random;

public class MonsterFactory implements IEntityFactoryService {
    private Entity monster;

    @Override
    public void spawn(GameData gameData, World world, Integer waveNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int spawnAmount = (waveNumber/4)+1;
                for(int i = 0; i < spawnAmount; i++ ) {
                    Entity monster = createMonster(world, waveNumber);
                    world.addEntity(monster);
                    monster.initTextureFormAssetManager(gameData);
                }
            }
        }).start();
    }

    private Entity createMonster(World world, int waveNumber) {
        String file = "Goblin_king.png";
        String goblin_death = "Sounds/goblin_death.mp3";
        String goblin_attack = "Sounds/goblin_attack.mp3";
        String[] files = {goblin_attack,goblin_death};

        // in the modifiers, 4 and 5 are magic numbers. Every 2nd wave, the attack modifier will be upped by 1
        int attackModifier = (waveNumber/4) * (waveNumber/6);
        int defenceModifier = (waveNumber/5) * (waveNumber/10);
        int hpScaling = (20*((waveNumber/4)+1))*waveNumber;
        int speed = new Random().nextInt((75-30)+1) + 30;

        Entity monster = new Monster(file, 5,30f/speed);
        monster.add(new MovingPart(speed));
        monster.add(new LifePart(100 + hpScaling, Color.TEAL));
        monster.add(new AnimationPart());
        monster.add(new StatsPart(20+attackModifier, 5+defenceModifier, 0));

        SoundPart soundPart = new SoundPart();

        soundPart.putAudio(SoundData.SOUND.ATTACK, goblin_attack);
        soundPart.putAudio(SoundData.SOUND.DEATH, goblin_death);

        monster.add(soundPart);
        FileLoader.loadFiles(files, getClass());

        do{
            //1350 is max x value of arena, 220 is min
            monster.setX(new Random().nextInt((1100  - 220) + 1) + 220);
            //1000 is max y value of arena, 350 is min
            monster.setY(new Random().nextInt((1000 - 350) + 1) + 350);
        } while(world.getCsvMap().get((int) monster.getY()/32).get((int) monster.getX()/32) == 1);

        return monster;
    }

    @Override
    public void stop(World world) {
        for (Entity monster: world.getEntities(Monster.class)){
            world.removeEntity(monster);
        }
    }
}
