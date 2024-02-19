package controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import model.Bullet;
import model.Tank;
import model.World;

public class InputHandler extends InputAdapter {
    private Tank tank;
    private World world;

    public InputHandler(Tank tank, World world) {
        this.tank = tank;
        this.world = world;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.SPACE) {
            Vector2 gunPosition = tank.getGunPosition();
            Bullet bullet = new Bullet(gunPosition.x, gunPosition.y, tank.getOrientation());
            world.addToElementMap("Bullet", bullet);
            return true;
        }
        return false;
    }

    public void update(float delta) {
        // Determine potential new position without actually moving the tank
        float potentialX = tank.getX();
        float potentialY = tank.getY();
        float speed = delta * tank.getSpeed(); // Assuming getSpeed() method exists
        tank.setIsMoving(false);

        // Check which direction is pressed and calculate potential new position
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            potentialY += speed;
            tank.setOrientation(Tank.Orientation.UP);
        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            potentialY -= speed;
            tank.setOrientation(Tank.Orientation.DOWN);
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            potentialX -= speed;
            tank.setOrientation(Tank.Orientation.LEFT);
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            potentialX += speed;
            tank.setOrientation(Tank.Orientation.RIGHT);
        }

        // Create a temporary rectangle for the potential new position to check collision
        Rectangle tempBounds = new Rectangle(potentialX, potentialY, tank.getWidth(), tank.getHeight());

        // Check for collision with the potential new position
        if (!world.checkCollisionWithWalls(tempBounds, world.tank)) {
        	tank.setIsMoving(true);
            // If no collision, move the tank using its move methods
            if (Gdx.input.isKeyPressed(Keys.UP)) {
            	if (potentialY + tank.getHeight() > Gdx.graphics.getHeight()) {
            		potentialY = Gdx.graphics.getHeight() - tank.getHeight();
            		return;
            	}
                tank.moveUp();
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            	if (potentialY < 0) {
            		 potentialY = 0;
            		 return;
            	}
                tank.moveDown();
            } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            	if (potentialX < 0) {
            		potentialX = 0;
            		return;
            	}
                tank.moveLeft();
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            	if (potentialX + tank.getWidth() > Gdx.graphics.getWidth()) {
            		potentialX = Gdx.graphics.getWidth() - tank.getWidth();
            		return;
            	}
                tank.moveRight();
            } else {
                tank.setIsMoving(false);
            }
        }
    }
}
