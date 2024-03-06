package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import model.Airplane;
import model.Bullet;
import model.EnemyTank;
import model.EnemyTank2;
import model.Explosion;
import model.GameElement;
import model.Tank;
import model.Trophy;
import model.World;
import view.TextureFactory;

public class WorldRenderer {
	
	private BitmapFont font;
    private World world;
    private float stateTime = 0f; // Track the elapsed time for animations
    private float stateTime2 = 0f;
    private float stateTime3 = 0f;
    // Animation objects for the tank
    private Animation<TextureRegion> tankAnimation;
    private Animation<TextureRegion> enemyTankAnimation;
    private Animation<TextureRegion> explosionAnimation;

    public WorldRenderer(World world) {
        this.world = world;
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        // Initialize the tank animation
        tankAnimation = TextureFactory.createTankAnimation(0.1f, 1, 8); // Adjust the frame duration as needed
        enemyTankAnimation = TextureFactory.createTankAnimation(0.1f, 9, 16);
        explosionAnimation = TextureFactory.createTankAnimation(0.1f, 17, 19);
    }
    
    public void render(SpriteBatch batch, float delta) {
        // Draw static objects and tanks
        for (String key : world.elementMap.keySet()) {
            if (!key.equals("Trophy")) { // Skip the trophy for now
                for (GameElement element : world.elementMap.get(key)) {
                    // Exclude bullets and the airplane in this pass
                    if (!(element instanceof Bullet) && !(element instanceof Airplane)) {
                        drawElement(batch, element, delta);
                    }
                }
            }
        }

        // Then, draw bullets so they are on top of static objects but below the airplane
        drawBullets(batch);

        // Draw the airplane
        drawAirplane(batch);

        // Draw the trophy last so it appears on top of everything else
        drawTrophy(batch);

        // Display lives in the top right corner
        displayLives(batch);
        
        // Handle explosions
        drawExplosions(batch, delta);
    }
    
    private void drawTrophy(SpriteBatch batch) {
        Set<GameElement> trophySet = world.elementMap.get("Trophy");
        if (trophySet != null) {
            for (GameElement trophy : trophySet) {
                Texture texture = TextureFactory.getTexture(trophy.getTextureIndex());
                batch.draw(texture, trophy.getX(), trophy.getY(), trophy.getWidth(), trophy.getHeight());
            }
        }
    }
    
    private void displayLives(SpriteBatch batch) {
        if (world.tank != null) {
            String livesText = "Lives: " + world.tank.getLives();
            float x = 700; // Adjust based on your screen size
            float y = 790; // Adjust based on your screen size
            font.draw(batch, livesText, x, y);
        }
    }

    private void drawAirplane(SpriteBatch batch) {
        if (world.airplane.isActive()) {
            Texture airplaneTexture = TextureFactory.getTexture(world.airplane.getTextureIndex());
            Vector2 pos = world.airplane.getCurrentPosition();
            batch.draw(airplaneTexture, pos.x, pos.y, world.airplane.getWidth(), world.airplane.getHeight());
        }
    }
    
    private void drawBullets(SpriteBatch batch) {
        Set<GameElement> bulletsSet = world.elementMap.get("Bullet");
        if (bulletsSet != null) {
            for (GameElement bullet : bulletsSet) {
                Texture texture = TextureFactory.getTexture(bullet.getTextureIndex());
                batch.draw(texture, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            }
        }
    }
    
    private void drawElement(SpriteBatch batch, GameElement element, float delta) {
        if (element instanceof Tank || element instanceof EnemyTank || element instanceof EnemyTank2) {
            drawTank(batch, element, delta);
        } else {
            Texture texture = TextureFactory.getTexture(element.getTextureIndex());
            batch.draw(texture, element.getX(), element.getY(), element.getWidth(), element.getHeight());
        }
    }

    private void drawTank(SpriteBatch batch, GameElement tank, float delta) {
        if (tank instanceof Tank) {
            Tank tank1 = (Tank) tank;

            if (tank1.isMoving()) {
                stateTime += delta; // Only increment stateTime when the tank is moving
            }

            TextureRegion currentFrame = tankAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, tank1.getX(), tank1.getY(),
                       12.5f, 12.5f, // This should be half of the width and height if you want to rotate around the center
                       25, 25, // Assuming these are the correct dimensions for your tank
                       1, 1, // No scaling
                       tank1.getDegrees()); // Apply the tank's current rotation
        } else if (tank instanceof EnemyTank) {
            EnemyTank tank1 = (EnemyTank) tank;
            if (tank1.isMoving()) {
                stateTime2 += delta; // Use a separate stateTime for enemy tanks
            }

            TextureRegion currentFrame = enemyTankAnimation.getKeyFrame(stateTime2, true);

            batch.draw(currentFrame, tank1.getX(), tank1.getY(),
                       12.5f, 12.5f, // This should be half of the width and height if you want to rotate around the center
                       25, 25, // Assuming these are the correct dimensions for your tank
                       1, 1, // No scaling
                       tank1.getDegrees()); // Apply the tank's current rotation
        }
        else if (tank instanceof EnemyTank2) {
            EnemyTank2 tank1 = (EnemyTank2) tank;
            if (tank1.isMoving()) {
                stateTime3 += delta; // Use a separate stateTime for enemy tanks
            }

            TextureRegion currentFrame = enemyTankAnimation.getKeyFrame(stateTime3, true);

            batch.draw(currentFrame, tank1.getX(), tank1.getY(),
                       12.5f, 12.5f, // This should be half of the width and height if you want to rotate around the center
                       25, 25, // Assuming these are the correct dimensions for your tank
                       1, 1, // No scaling
                       tank1.getDegrees()); // Apply the tank's current rotation
        }
    }


    private void drawExplosions(SpriteBatch batch, float delta) {
        Iterator<Explosion> explosionIterator = world.explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.stateTime += delta;
            if (!explosionAnimation.isAnimationFinished(explosion.stateTime)) {
                TextureRegion currentFrame = explosionAnimation.getKeyFrame(explosion.stateTime, false);
                batch.draw(currentFrame, explosion.position.x - 8f, explosion.position.y - 8f, 0, 0, 30, 30, 1, 1, 0);
            } else {
                explosionIterator.remove();
            }
        }
    }
    
    public void updateBullets(float delta) {
        // Check if the elementMap contains the "Bullet" key and get all bullets
        Set<GameElement> bulletsSet = world.elementMap.get("Bullet");
        if (bulletsSet != null) {
            // Since we cannot modify the set while iterating, we collect bullets to remove first
            List<Bullet> bulletsToRemove = new ArrayList<Bullet>();
            
            for (GameElement element : bulletsSet) {
                if (element instanceof Bullet) {
                    Bullet bullet = (Bullet) element;
                    bullet.update(delta, world);
                    
                    // Check if the bullet is not active anymore and mark it for removal
                    if (!bullet.isActive()) {
                        world.explosions.add(new Explosion(new Vector2(bullet.getX(), bullet.getY())));
                        bulletsToRemove.add(bullet);
                    }
                }
            }
            
            // Remove the inactive bullets from the set
            for (Bullet bullet : bulletsToRemove) {
                bulletsSet.remove(bullet);
            }
        }
    }
    
    public void update(float delta) {
        // Update the airplane
        if (world.airplane.isActive()) {
            world.airplane.update(delta);
        } else {
            world.airplaneTimer += delta;
            if (world.airplaneTimer >= world.airplaneInterval) {
                world.airplaneTimer = 0;
                world.airplane.activate(); // Activate the airplane every 10 seconds
            }
        }

    }

}
