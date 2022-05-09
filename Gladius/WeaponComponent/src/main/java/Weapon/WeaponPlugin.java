package Weapon;

import Common.data.GameData;
import Common.data.World;
import Common.services.IGamePluginService;
import CommonWeapon.IWeaponUserService;
import CommonWeapon.Weapon;
import Common.data.Entity;
import CommonWeapon.WeaponImages;
import CommonPlayer.Player;

public class WeaponPlugin implements IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {

    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity weapon : world.getEntities(Weapon.class)) {
            if (weapon instanceof  Weapon) {
                weapon.getTexture().dispose();
                world.removeEntity(weapon);
            }
        }
    }
}
