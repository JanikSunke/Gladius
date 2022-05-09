package Enemy;

import Common.ai.AStarPathFinding;
import Common.ai.Node;
import Common.data.Entity;
import Common.data.GameData;
import Common.data.World;
import Common.data.entityparts.AnimationPart;
import Common.data.entityparts.LifePart;
import Common.data.entityparts.MovingPart;
import Common.services.IEntityProcessingService;
import CommonPlayer.Player;
import CommonEnemy.Enemy;
import CommonWeapon.IWeaponService;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnemyControlSystem implements IEntityProcessingService {

    //TODO enemy attack implementation

    private AStarPathFinding aStarPathFinding = new AStarPathFinding();
    private IWeaponService weaponService;

    @Override
    public void process(GameData gameData, World world) {
        ShapeRenderer sr = null;
        if (gameData.isDebugMode()) {
            //used to show path
            sr = new ShapeRenderer();
            //set projection to not follow camera
            sr.setProjectionMatrix(gameData.getCam().combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
        }
        for (Entity enemy : world.getEntities(Enemy.class)) {
            MovingPart movingPart = enemy.getPart(MovingPart.class);

            // get the enemy position on the tile map
            int enemyY = (int) ((enemy.getY() / gameData.getMapHeight()) * 40);
            int enemyX = (int) ((((enemy.getRegionWidth() / 2) + enemy.getX()) / gameData.getMapWidth()) * 50);

            AnimationPart animationPart = enemy.getPart(AnimationPart.class);
            LifePart lifePart = enemy.getPart(LifePart.class);

            for (Entity player : world.getEntities(Player.class)) {
                // getting player position on the tile map
                int playerY = (int) ((player.getY() / gameData.getMapHeight()) * 40);
                int playerX = (int) (((player.getX() + player.getTextureWidth() / 2) / gameData.getMapWidth()) * 50);

                if (!lifePart.isDead()) {
                    // if player is in hub (<300) && player is inside the wall (39 is gridMapHeight)
                        if (player.getY() > 300 && world.getCsvMap().get(39 - playerY).get(playerX) != 1) {

                            // listing the positions of enemy and the target (player) in Lists
                            List<Integer> enemyPos = new ArrayList<>(Arrays.asList(enemyX, enemyY));
                            List<Integer> targetPos = new ArrayList<>(Arrays.asList(playerX, playerY));

                            // Initialization of a new search for the given positions
                            List<Node> path = aStarPathFinding.treeSearch(enemyPos, targetPos, world.getCsvMap());

                            //Removes the goal node so it does not stand on the goal, but next to it
                            if (path.size() > 1) {
                                path.remove(0);
                            }

                            // choose the point to go to
                            Node nextPoint;
                            // if there are over 2 points in the path
                            if (path.size() > 2) {
                                //get the 3rd node from start (including start node).
                                //Makes it possible to walk diagonal
                                nextPoint = path.get(path.size() - 3);
                            } else {
                                // get goal node
                                nextPoint = path.get(0);
                            }
                            // the y of the target tile is flipped to fit the position representation
                            // the output from the search is given in tiles (0-40), so it's scaled to fit the position representation (0-1280)
                            float targetY = (nextPoint.getY() * 32) + 16;
                            float targetX = (nextPoint.getX() * 32) + 16;
                            float currentX = (int) enemy.getX() + (enemy.getRadius() * 16) / 2;
                            float currentY = (int) enemy.getY();

                            Polygon attackRange = ((Enemy) enemy).getAttackRange();
                            attackRange.setPosition(enemy.getX(), enemy.getY());
                            attackRange.getBoundingRectangle();

                            if (gameData.isDebugMode()) {
                                sr.setColor(Color.GREEN);

                                // for every node, draw its outline

                                for (Node node : path) {
                                    int nodeX = node.getX() * 32;
                                    int nodeY = 32 + (node.getY()) * 32;

                                    sr.line(nodeX, nodeY, nodeX + 32, nodeY);
                                    sr.line(nodeX, nodeY, nodeX, nodeY - 32);

                                    sr.line(nodeX + 32, nodeY - 32, nodeX + 32, nodeY);
                                    sr.line(nodeX + 32, nodeY - 32, nodeX, nodeY - 32);
                                }

                                Gdx.gl.glEnable(GL20.GL_BLEND);
                                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                                sr.setColor(Color.RED);
                                sr.polygon(attackRange.getTransformedVertices());
                            }

                            // Checking if player is inside of enemy's attack range
                            if (Intersector.overlapConvexPolygons(attackRange, player.getPolygonBoundaries())) {
                                LifePart playerLifePart = player.getPart(LifePart.class);
                                if (animationPart.getCurrentAnimation().isAnimationFinished(animationPart.getAnimationTime()) && !playerLifePart.isDead()) {
                                    if (enemy.getX() > player.getX()) {
                                        animationPart.setCurrentState(AnimationPart.ANIMATION_STATES.ATTACK_LEFT);
                                    } else {
                                        animationPart.setCurrentState(AnimationPart.ANIMATION_STATES.ATTACK_RIGHT);
                                    }
                                    weaponService.attack(enemy, gameData, world);
                                }
                            } else {
                                if (animationPart.getCurrentAnimation().isAnimationFinished(animationPart.getAnimationTime())) {
                                    if (animationPart.isLeft()) {
                                        animationPart.setCurrentState(AnimationPart.ANIMATION_STATES.IDLE_LEFT);
                                    } else {
                                        animationPart.setCurrentState(AnimationPart.ANIMATION_STATES.IDLE_RIGHT);
                                    }
                                }
                            }

                            //if not at/near end goal
                            if (!(path.size() <= 1)) {

                                // if the targetX and EnemyX is not the same
                                if (!((int) targetX == (int) currentX)) {


                                    if (targetX < currentX) {
                                        movingPart.setLeft(true);
                                        movingPart.setRight(false);
                                    } else {
                                        movingPart.setRight(true);
                                        movingPart.setLeft(false);
                                    }

                                    // needed if walking diagonal
                                    if (targetY < currentY) {
                                        movingPart.setUp(false);
                                        movingPart.setDown(true);
                                    } else {
                                        movingPart.setDown(false);
                                        movingPart.setUp(true);
                                    }

                                } else {

                                    movingPart.setLeft(false);
                                    movingPart.setRight(false);
                                    if (targetY < currentY) {
                                        movingPart.setUp(false);
                                        movingPart.setDown(true);
                                    } else {
                                        movingPart.setDown(false);
                                        movingPart.setUp(true);

                                    }
                                }
                            } else {
                                //if at goal node
                                stopMovement(movingPart);
                            }
                        } else {
                            //if player is not inside arena
                            stopMovement(movingPart);
                        }
                    }
            }

            movingPart.process(gameData, enemy);
            animationPart.process(gameData, enemy);
            lifePart.process(gameData, enemy);
        }
        if (gameData.isDebugMode()) {
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private void stopMovement(MovingPart movingPart) {
        movingPart.setDown(false);
        movingPart.setUp(false);
        movingPart.setLeft(false);
        movingPart.setRight(false);
    }

    public void setWeaponService(IWeaponService weaponService) {
        this.weaponService = weaponService;
    }

    public void removeWeaponService(IWeaponService weaponService) {
        this.weaponService = null;
    }
}
