package CommonMonster;

import Common.data.Entity;
import Common.data.GameData;
import Common.data.entityparts.AnimationPart;
import CommonWeapon.IWeaponUserService;
import CommonWeapon.Weapon;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

public class Monster extends Entity implements IWeaponUserService {
    private Weapon equippedWeapon;
    private Polygon attackRange;
    private float movementScale = 1f;

    public Monster(String texturePath, int radius, float movementScale) {
        super(texturePath, radius, 64, 64, 0, 0.5f, 0.5f);
        // The magic number 7 for x adjustment is the difference between texturewidth for monster and texturewidth for weapon divided by 2
        this.equippedWeapon = new Weapon("jump", 65, 110, 8, "", 50, 50, 1.0f, 1.0f, 0, 0.0f, 0.0f, 7, 7, -10.0f, this);
        this.equippedWeapon.setRotationDuration(23);
        // this.getX(), this.getY(), this.equippedWeapon.getRange()
        this.attackRange = new Polygon(new float[]{super.getX(), super.getY(), super.getX(), super.getY() + super.getTextureHeight(), super.getX() + super.getTextureWidth(), super.getY() + super.getTextureHeight(), super.getX() + super.getTextureWidth(), super.getY()});
        this.attackRange.setOrigin(super.getTextureWidth() / 2f, -43);
        this.attackRange.setScale(0.9f, 0.7f);

        this.movementScale = movementScale;
    }

    @Override
    public Weapon getWeapon() {
        return this.equippedWeapon;
    }

    @Override
    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    @Override
    public void initTexture(){
        super.initTexture();
        this.setRegion(0,0,96, 96);
        initAnimation();
    }

    @Override
    public void initTextureFormAssetManager(GameData gameData) {
        super.initTextureFormAssetManager(gameData);
        initAnimation();
    }

    public Polygon getAttackRange() {
        return this.attackRange;
    }

    private void initAnimation() {
        if (this.getPart(AnimationPart.class) != null) {
            AnimationPart animationPart = this.getPart(AnimationPart.class);

            // IDLE animation
            Array<TextureRegion> idleRightTextures = new Array<>();
            Array<TextureRegion> idleLeftTextures = new Array<>();

            for (int i = 0; i < 4; i++) {
                // IDLE right
                idleRightTextures.add(new TextureRegion(this.getTexture(), super.getTextureWidth() * i, 0, super.getTextureWidth(), super.getTextureHeight()));

                // IDLE left
                TextureRegion idleLeftTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * i, 0, super.getTextureWidth(), super.getTextureHeight());
                idleLeftTexture.flip(true, false);
                idleLeftTextures.add(idleLeftTexture);
            }
            // IDLE right animation
            Animation idleRightAnimation = new Animation(0.16f, idleRightTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.IDLE_RIGHT, idleRightAnimation);

            // IDLE left animation
            Animation leftIdleAnimation = new Animation(0.16f, idleLeftTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.IDLE_LEFT, leftIdleAnimation);

            // Running animation
            Array<TextureRegion> rightMoveTextures = new Array<>();
            Array<TextureRegion> leftMoveTextures = new Array<>();

            for (int i = 0; i < 6; i++) {
                // Running right
                rightMoveTextures.add(new TextureRegion(this.getTexture(), super.getTextureWidth() * i, super.getTextureHeight(), super.getTextureWidth(), super.getTextureHeight()));

                // Running left
                TextureRegion leftTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * i, super.getTextureHeight(), super.getTextureWidth(), super.getTextureHeight());
                leftTexture.flip(true, false);
                leftMoveTextures.add(leftTexture);
            }
            // RUNNING right animation
            Animation rightMoveAnimation = new Animation(0.175f*movementScale, rightMoveTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.RUNNING_RIGHT, rightMoveAnimation);

            // RUNNING left animation
            Animation leftMoveAnimation = new Animation(0.175f*movementScale, leftMoveTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.RUNNING_LEFT, leftMoveAnimation);

            // DEATH animation
            Array<TextureRegion> rightDeathTextures = new Array<>();
            Array<TextureRegion> leftDeathTextures = new Array<>();

            for (int i = 0; i < 11; i++) {
                int death_row = 7;
                TextureRegion rightDeathTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * i, super.getTextureHeight() * death_row, super.getTextureWidth(), super.getTextureHeight());
                rightDeathTextures.add(rightDeathTexture);
                TextureRegion leftDeathTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * i, super.getTextureHeight() * death_row, super.getTextureWidth(), super.getTextureHeight());
                leftDeathTexture.flip(true, false);
                leftDeathTextures.add(leftDeathTexture);
            }
            // RIGHT DEATH animation
            Animation rightDeathAnimation = new Animation(0.175f, rightDeathTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.DEATH_RIGHT, rightDeathAnimation);

            Animation leftDeathAnimation = new Animation(0.175f, leftDeathTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.DEATH_LEFT, leftDeathAnimation);

            // ATTACK animation
            Array<TextureRegion> rightAttackTextures = new Array<>();
            Array<TextureRegion> leftAttackTextures = new Array<>();
            for (int i = 0; i < 10; i++) {
                // The attack animation expands on two rows on the spritesheet
                int attack_row = i < 6 ? 9 : 10;
                int adjustment = i >= 6 ? i - 6 : i;
                TextureRegion rightAttackTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * adjustment, super.getTextureHeight() * attack_row, super.getTextureWidth(), super.getTextureHeight());
                rightAttackTextures.add(rightAttackTexture);

                TextureRegion leftAttackTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * adjustment, super.getTextureHeight() * attack_row, super.getTextureWidth(), super.getTextureHeight());
                leftAttackTexture.flip(true, false);
                leftAttackTextures.add(leftAttackTexture);
            }
            // ATTACK ANIMATION right
            Animation attackAnimation = new Animation(0.12f, rightAttackTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.ATTACK_RIGHT, attackAnimation);

            // ATTACK ANIMATION left
            Animation leftAttackAnimation = new Animation(0.12f, leftAttackTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.ATTACK_LEFT, leftAttackAnimation);

            // TAKE DAMAGE animation
            Array<TextureRegion> rightTakeDamageTextures = new Array<>();
            Array<TextureRegion> leftTakeDamageTextures = new Array<>();
            for (int i = 0; i < 3; i++) {
                int death_row = 6;
                TextureRegion rightTakeDamageTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * i, super.getTextureHeight() * death_row, super.getTextureWidth(), super.getTextureHeight());
                rightTakeDamageTextures.add(rightTakeDamageTexture);
                TextureRegion leftTakeDamageTexture = new TextureRegion(this.getTexture(), super.getTextureWidth() * i, super.getTextureHeight() * death_row, super.getTextureWidth(), super.getTextureHeight());
                leftTakeDamageTexture.flip(true, false);
                leftTakeDamageTextures.add(leftTakeDamageTexture);
            }
            // TAKE DAMAGE right animation
            Animation rightTakeDamageAnimation = new Animation(0.08f,rightTakeDamageTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.TAKE_DAMAGE_RIGHT, rightTakeDamageAnimation);
            // TAKE DAMAGE left animation
            Animation leftTakeDamageAnimation = new Animation(0.08f,leftTakeDamageTextures);
            animationPart.addAnimation(AnimationPart.ANIMATION_STATES.TAKE_DAMAGE_LEFT, leftTakeDamageAnimation);
        }
    }
}
