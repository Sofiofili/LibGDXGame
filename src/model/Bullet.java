package model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;



public class Bullet extends GameElement{
	
	public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    }
	
    private Vector2 position;
    private float speed = 350;
    private boolean active;
    private Tank.Orientation orientation;
    

    public Bullet(float x, float y, Tank.Orientation orientation) {
    	super(x, y, 10, 10, "BULLET", 50);
    	this.orientation = orientation;
        this.position = new Vector2(x, y);
        this.active = true;
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

    
    public void setOrientation(Tank.Orientation newOrientation) {
        this.orientation = newOrientation;
    }

    public float getRotation() {
        switch (orientation) {
            case UP:
                return 0;
            case DOWN:
                return 180;
            case LEFT:
                return 90;
            case RIGHT:
                return 270;
            default:
                return 0; // Default case, should not happen
        }
    }

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }
}
