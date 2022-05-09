package Common.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundData {

    public final Map<SOUND, String> soundFileMap = new HashMap<>();
    public final Map<SOUND, Sound> soundMap = new HashMap<>();

    public enum SOUND{
        WALK,
        WALK_WATER,
        ATTACK,
        DAMAGE,
        DEATH,
        EXPLOSION,
        BUY,
        INTERACT,
        WAVE_CLEARED,
        ARENA_ENTERED,
        MINOTAUR_SOUND,
        MINOTAUR_DEATH,
        ARENA_EXIT
    }

    public SoundData(){

        //TODO record sounds
        soundFileMap.put(SOUND.WALK, "walk.mp3");
        soundFileMap.put(SOUND.WALK_WATER, "water_walk.mp3");
        soundFileMap.put(SOUND.ATTACK, "attack.mp3");
        soundFileMap.put(SOUND.DAMAGE, "damage.mp3");
        soundFileMap.put(SOUND.DEATH, "death.mp3");
        soundFileMap.put(SOUND.EXPLOSION, "explosion.mp3");
        soundFileMap.put(SOUND.BUY, "walk.mp3");
        soundFileMap.put(SOUND.INTERACT, "interact.mp3");
        soundFileMap.put(SOUND.WAVE_CLEARED, "wave_cleared.mp3");
        soundFileMap.put(SOUND.ARENA_ENTERED, "walk.mp3");
        soundFileMap.put(SOUND.ARENA_EXIT, "exit_arena.mp3");

        for(SOUND sound : soundFileMap.keySet()){
           soundFileMap.replace(sound,"Sounds/"+soundFileMap.get(sound));
        }


    }

    public void initSound(){
        for (SOUND sound : this.soundFileMap.keySet()){
            String path = this.soundFileMap.get(sound);
            this.soundMap.put(sound, Gdx.audio.newSound(Gdx.files.internal(path)));
        }
    }

    public Map<SOUND, String> getSoundFileMap() {
        return this.soundFileMap;
    }

    public Map<SOUND, Sound> getSoundMap(){ return soundMap; }

    public Sound getSound(SOUND sound){
        return this.soundMap.get(sound);
    }

    public void playSound(SOUND key){
        Sound sound = soundMap.get(key);
        sound.play(1f);
    }

}
