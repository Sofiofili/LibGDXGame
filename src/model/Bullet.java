package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Bullet extends GameElement {
	public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    }
	private Orientation orientation = Orientation.UP;
    private Vector2 position;
    private Vector2 direction; // Direction vector
    private float speed = 350;
    private boolean active;

    public Bullet(float x, float y, Vector2 direction) {
        super(x, y, 10, 10, "BULLET", 50);
        this.position = new Vector2(x, y);
        this.direction = direction.nor(); // Normalize the direction vector
        this.active = true;

        // Determine orientation based on the direction vector
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            // Horizontal movement
            if (direction.x > 0) {
                orientation = Orientation.RIGHT;
            } else {
                orientation = Orientation.LEFT;
            }
        } else {
            // Vertical movement
            if (direction.y > 0) {
                orientation = Orientation.UP;
            } else {
                orientation = Orientation.DOWN;
            }
        }
    }


    public void update(float delta, World world) {
    	
        // Move the bullet based on its orientation
        switch (orientation) {
            case UP:
                position.add(0, speed * delta);
                break;
            case DOWN:
                position.add(0, -speed * delta);
                break;
            case LEFT:
                position.add(-speed * delta, 0);
                break;
            case RIGHT:
                position.add(speed * delta, 0);
                break;
        }
        
        // Update the bullet's position in the superclass
        super.setX(position.x);
        super.setY(position.y);

        // Check if the bullet goes off the screen and deactivate it
        if (position.y > Gdx.graphics.getHeight() || position.y < 0 || 
            position.x > Gdx.graphics.getWidth() || position.x < 0) {
            active = false;
        }

        // Check for collision with walls
        Rectangle bulletBounds = new Rectangle(position.x, position.y, getWidth(), getHeight());
        if (world.checkCollisionWithWalls(bulletBounds, this)) {
            active = false; // Deactivate the bullet on collision
        }
    }

    // Removed setOrientation method since we're not using Orientation anymore

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }
}
