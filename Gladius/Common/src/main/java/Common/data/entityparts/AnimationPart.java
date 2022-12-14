package Common.data.entityparts;

import Common.data.Entity;
import Common.data.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class AnimationPart implements EntityPart{

    public enum ANIMATION_STATES{
        IDLE_RIGHT,
        IDLE_LEFT,
        RUNNING_RIGHT,
        RUNNING_LEFT,
        ATTACK_RIGHT,
        ATTACK_LEFT,
        DEATH_RIGHT,
        DEATH_LEFT,
        TAKE_DAMAGE_LEFT,
        TAKE_DAMAGE_RIGHT
    }

    private ANIMATION_STATES currentState = ANIMATION_STATES.IDLE_RIGHT;
    private HashMap<ANIMATION_STATES, Animation> animations = new HashMap<>();
    private float animationTime = 0;
    private Boolean isLeft = true;
    private Boolean takeDamage = false;
    private Boolean attack = false;
    private boolean isAttacking = false;

    @Override
    public void process(GameData gameData, Entity entity) {
        LifePart lifePart = entity.getPart(LifePart.class);
        MovingPart movingPart = entity.getPart(MovingPart.class);
        boolean hasLifePart = lifePart != null;
        boolean hasMovingPart = movingPart != null;
        boolean isInDeathAnimationState = this.getCurrentState() == ANIMATION_STATES.DEATH_LEFT || this.getCurrentState() == ANIMATION_STATES.DEATH_RIGHT;
        boolean isInTakeDamageAnimationState = this.getCurrentState() == ANIMATION_STATES.TAKE_DAMAGE_LEFT || this.getCurrentState() == ANIMATION_STATES.TAKE_DAMAGE_RIGHT;
        boolean isInAttackAnimationState = this.getCurrentState() == ANIMATION_STATES.ATTACK_LEFT || this.getCurrentState() == ANIMATION_STATES.ATTACK_RIGHT;

        // DEATH ANIMATION
        if (hasLifePart && lifePart.isDead() && !isInDeathAnimationState) {
            // If the entity has just died
            // Reset animationtime so that it uses the first keyframe
            // Set animation state to a death animation state
            this.animationTime = 0;
            if (this.isLeft) {
                this.setCurrentState(ANIMATION_STATES.DEATH_LEFT);
            } else {
                this.setCurrentState(ANIMATION_STATES.DEATH_RIGHT);
            }
        }

        if (hasLifePart && !lifePart.isDead() && isInAttackAnimationState && !this.isAttacking) {
            this.animationTime = 0;
            this.isAttacking = true;
        }

        // IDLE ANIMATION
        if(isInTakeDamageAnimationState && this.getCurrentAnimation().isAnimationFinished(animationTime) || isInAttackAnimationState && this.getCurrentAnimation().isAnimationFinished(animationTime)) {
            if(hasLifePart && !lifePart.isDead()) {
                this.animationTime = 0;
                if (this.isLeft) {
                    this.setCurrentState(ANIMATION_STATES.IDLE_LEFT);
                } else {
                    this.setCurrentState(ANIMATION_STATES.IDLE_RIGHT);
                }
            }
        }

        if (hasMovingPart && hasLifePart && !lifePart.isDead() && !isInAttackAnimationState && !isInTakeDamageAnimationState) {
            this.isAttacking = false;
            processMovementAnimation(entity);
        }

        // TAKE DAMAGE ANIMATION
        if(takeDamage && hasLifePart && !lifePart.isDead() && !isInAttackAnimationState) {
            // If the entity isn't dead but takes damage
            // reset animationTime so that it uses the first keyframe
            this.animationTime = 0;
            if (this.isLeft) {
                this.setCurrentState(ANIMATION_STATES.TAKE_DAMAGE_LEFT);
            } else {
                this.setCurrentState(ANIMATION_STATES.TAKE_DAMAGE_RIGHT);
            }
            takeDamage = false;
        }

        // ATTACK ANIMATION
        if(attack && hasLifePart && !lifePart.isDead()) {
            // If the entity isn't dead but takes damage
            // reset animationTime so that it uses the first keyframe
            this.animationTime = 0;
            if (this.isLeft) {
                this.setCurrentState(ANIMATION_STATES.ATTACK_LEFT);
            } else {
                this.setCurrentState(ANIMATION_STATES.ATTACK_RIGHT);
            }
            attack = false;
        }

        if(this.getCurrentAnimation() != null) {
            if(!this.getCurrentAnimation().isAnimationFinished(animationTime)){
                // Advance animation
                animationTime += gameData.getDelta();
            } else if (hasLifePart && !lifePart.isDead()) {
                // Loop animation if the entity has a lifepart, and it is alive
                animationTime = 0;
            } else if (lifePart == null) {
                // Loop animation if entity is not a living entity
                animationTime = 0;
            }
            entity.setRegion(getCurrentKeyFrame());
        }
    }

    /**
     * @param entity
     *
     * This method uses logical statements based on keyboard input to call setAnimationDirection.
     * See JavaDoc at setAnimationDirection for more context.
     */
    private void processMovementAnimation(Entity entity){
        MovingPart movingPart = entity.getPart(MovingPart.class);

        boolean left = movingPart.isLeft();
        boolean right = movingPart.isRight();
        boolean up = movingPart.isUp();
        boolean down = movingPart.isDown();

        // If either left or right keys are pressed, use the running animation
        if (left ^ right){
            setAnimationDirection(left, right, ANIMATION_STATES.RUNNING_LEFT, ANIMATION_STATES.RUNNING_RIGHT);
        }

        // If either up or down keys are pressed, use the running animation
        if (up ^ down) {
            setAnimationDirection(isLeft(), !isLeft(), ANIMATION_STATES.RUNNING_LEFT, ANIMATION_STATES.RUNNING_RIGHT);
        }

        // If only left and right keys are pressed, use the idle animation
        if ((left && right) && (!up && !down)) {
            setAnimationDirection(isLeft(), !isLeft(), ANIMATION_STATES.IDLE_LEFT, ANIMATION_STATES.IDLE_RIGHT);
        }

        // If only up and down keys are pressed, use the idle animation
        if ((up && down) && (!left && !right)) {
            setAnimationDirection(isLeft(), !isLeft(), ANIMATION_STATES.IDLE_LEFT, ANIMATION_STATES.IDLE_RIGHT);
        }

        // If no keys are pressed, use the idle animation
        if (!left && !right && !up && !down){
            setAnimationDirection(isLeft(), !isLeft(), ANIMATION_STATES.IDLE_LEFT, ANIMATION_STATES.IDLE_RIGHT);
        }
    }

    /**
     * @param leftCondition
     * @param rightCondition
     * @param leftAnimationState
     * @param rightAnimationState
     *
     * This method sets the animation to either idle or running with a left or right orientation.
     * Left and right animation state is needed because both the running and idle animation has an orientation.
     */
    private void setAnimationDirection(boolean leftCondition, boolean rightCondition, AnimationPart.ANIMATION_STATES leftAnimationState, AnimationPart.ANIMATION_STATES rightAnimationState){
        if (leftCondition) {
            this.setCurrentState(leftAnimationState);
            this.setLeft(true);
        }
        if (rightCondition) {
            this.setCurrentState(rightAnimationState);
            this.setLeft(false);
        }
    }

    public ANIMATION_STATES getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ANIMATION_STATES currentState) {
        this.currentState = currentState;
    }

    public void addAnimation(ANIMATION_STATES state, Animation animation){
        this.animations.put(state,animation);
    }

    public Animation getCurrentAnimation(){
        return animations.get(currentState);
    }

    public TextureRegion getCurrentKeyFrame(){
        return getCurrentAnimation().getKeyFrame(animationTime,false);
    }

    public boolean isDoneAnimating() {
        return this.getCurrentAnimation().isAnimationFinished(animationTime);
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public void setTakeDamage() {
        takeDamage = true;
    }

    public void setAttack() {
        attack = true;
    }

    public float getAnimationTime() {
        return this.animationTime;
    }
}
