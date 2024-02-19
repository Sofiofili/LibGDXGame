package controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import model.Bullet;
import model.EnemyTank;
import model.Explosion;
import model.GameElement;
import model.Tank;
import model.World;
import view.TextureFactory;

public class WorldRenderer {

    private World world;
    private float stateTime = 0f; // Track the elapsed time for animations
    private float stateTime2 = 0f;
    // Animation objects for the tank
    private Animation<TextureRegion> tankAnimation;
    private Animation<TextureRegion> enemyTankAnimation;
    private Animation<TextureRegion> explosionAnimation;

    public WorldRenderer(World world) {
        this.world = world;
        // Initialize the tank animation
        tankAnimation = TextureFactory.createTankAnimation(0.1f, 1, 8); // Adjust the frame duration as needed
        enemyTankAnimation = TextureFactory.createTankAnimation(0.1f, 9, 16);
        explosionAnimation = TextureFactory.createTankAnimation(0.1f, 17, 19);
    }
    
    public void render(SpriteBatch batch, float delta) {
        // First, draw static objects and tanks
        for (String key : world.elementMap.keySet()) {
            for (GameElement element : world.elementMap.get(key)) {
                if (!(element instanceof Bullet)) { // Exclude bullets in this pass
                    if (element instanceof Tank || element instanceof EnemyTank) {
                        // Handle tank and enemy tank animations
                        drawTank(batch, element, delta);
                    } else {
                        // General case for static elements
                        Texture texture = TextureFactory.getTexture(element.getTextureIndex());
                        batch.draw(texture, element.getX(), element.getY(), element.getWidth(), element.getHeight());
                    }
                }
            }
        }

        // Then, draw bullets so they are on top
        Set<GameElement> bulletsSet = world.elementMap.get("Bullet");
        if (bulletsSet != null) {
            for (GameElement bullet : bulletsSet) {
                drawBullet(batch, bullet);
            }
        }
        
        // Handle explosions separately if they're not part of elementMap
        drawExplosions(batch, delta);
    }

    
    private void drawBullet(SpriteBatch batch, GameElement bullet) {
        Texture texture = TextureFactory.getTexture(bullet.getTextureIndex());
        batch.draw(texture, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
    }

    private void drawTank(SpriteBatch batch, GameElement tank, float delta) {
    	
    	if (tank.getElementType() == "TANK") {
    		Tank tank1 = (Tank) tank;
        	
        	if (tank1.isMoving()) {
                stateTime += delta;
            }

            TextureRegion currentFrame = tankAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, tank1.getX(), tank1.getY(),
                       12.5f, 12.5f, // This should be half of the width and height if you want to rotate around the center
                       25, 25, // Assuming these are the correct dimensions for your tank
                       1, 1, // No scaling
                       tank1.getDegrees()); // Apply the tank's current rotation

    	} else {
    		
    		EnemyTank tank1 = (EnemyTank) tank;
        	if (tank1.isMoving()) {
                stateTime += delta;
            }

            TextureRegion currentFrame = enemyTankAnimation.getKeyFrame(stateTime2, true);

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


}
