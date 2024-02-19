package model;

import com.badlogic.gdx.math.Vector2;

public class Tank extends GameElement implements Movable, Destroyable {
	private boolean isMoving = false;
	private Vector2 position;
	public int currentFrame = 0;
	public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    }

    private Orientation orientation = Orientation.UP;
    private int degrees = 0;
    private float speed = 2;

    public Tank(float x, float y, int height, int width) {
        super(x, y, height, width, "TANK", 1);
        this.position = new Vector2(x, y);
    }
    
    public void setOrientation(Orientation newOrientation) {
        this.orientation = newOrientation;
        switch (orientation) {
        case UP:
            setDegrees(0);
            break;
        case DOWN:
            setDegrees(180);
            break;
        case LEFT:
            setDegrees(90);
            break;
        case RIGHT:
            setDegrees(270);
            break;
        default:
            setDegrees(0);
            break;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void moveUp() {
        float newY = this.getY() + speed;
        super.setY(newY); // Update the Y position using the superclass method
        this.position.y = newY; // Synchronize the position vector
    }

    @Override
    public void moveDown() {
        float newY = this.getY() - speed;
        super.setY(newY);
        this.position.y = newY;
    }

    @Override
    public void moveLeft() {
        float newX = this.getX() - speed;
        super.setX(newX); // Update the X position using the superclass method
        this.position.x = newX; // Synchronize the position vector
    }

    @Override
    public void moveRight() {
        float newX = this.getX() + speed;
        super.setX(newX);
        this.position.x = newX;
    }

    
    public float getSpeed() {
    	return speed;
    }
    
    public void setSpeed(float speed) {
    	this.speed = speed;
    }
    
    public void setIsMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public boolean isMoving() {
        return isMoving;
    }

	@Override
	public void shoot() {
		// TODO Auto-generated method stub
		
	}
	
	public Vector2 getGunPosition() {
	    Vector2 gunPosition = new Vector2();
	    switch (this.orientation) {
	        case UP:
	            gunPosition.set(this.getX() + this.getWidth() / 2 - 5, this.getY() + this.getHeight() - 5); // Front middle when facing up
	            break;
	        case DOWN:
	            gunPosition.set(this.getX() + this.getWidth() / 2 - 5, this.getY() - 5); // Front middle when facing down
	            break;
	        case LEFT:
	            gunPosition.set(this.getX() - 5, this.getY() + this.getHeight() / 2 - 5); // Front middle when facing left
	            break;
	        case RIGHT:
	            gunPosition.set(this.getX() + this.getWidth() - 5, this.getY() + this.getHeight() / 2 - 5); // Front middle when facing right
	            break;
	    }
	    return gunPosition;
	}


	public int getDegrees() {
		return degrees;
	}

	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}

	@Override
	public void takeDamage(int damageAmount, World world) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroy(World world) {
		// TODO Auto-generated method stub
		
	}
}
