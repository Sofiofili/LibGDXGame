package model;

import java.util.Set;

import com.badlogic.gdx.math.Vector2;

public class Tank extends GameElement implements Movable, Destroyable {
	private int lives;
	private int health = 2;
	private boolean isMoving = false;
	private Vector2 position;
	public int currentFrame = 0;
	public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    }

    private Orientation orientation = Orientation.UP;
    private int degrees = 0;
    private float speed = 2;

    public Tank(float x, float y, int height, int width, int lives) {
        super(x, y, height, width, "TANK", 1);
        this.position = new Vector2(x, y);
        this.lives = lives;
    }
    
    public Vector2 computeDirection() {
        Vector2 direction = new Vector2();
        switch (this.orientation) {
            case UP:
                direction.set(0, 1); // Up
                break;
            case DOWN:
                direction.set(0, -1); // Down
                break;
            case LEFT:
                direction.set(-1, 0); // Left
                break;
            case RIGHT:
                direction.set(1, 0); // Right
                break;
        }
        return direction;
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
    
    public Vector2 getPosition() {
    	return position;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void moveUp() {
        float newY = this.getY() + speed;
        super.setY(newY); // Update the Y position using the superclass method
        this.position.y = newY; // Synchronize the position vector
        super.bounds.y	 = newY;
    }

    @Override
    public void moveDown() {
        float newY = this.getY() - speed;
        super.setY(newY);
        this.position.y = newY;
        super.bounds.y = newY;
    }

    @Override
    public void moveLeft() {
        float newX = this.getX() - speed;
        super.setX(newX); // Update the X position using the superclass method
        this.position.x = newX; // Synchronize the position vector
        super.bounds.x = newX;
    }

    @Override
    public void moveRight() {
        float newX = this.getX() + speed;
        super.setX(newX);
        this.position.x = newX;
        super.bounds.x = newX;
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
		health -= damageAmount;
        if (health <= 0) {
            onDestroy(world);
        }
	}

	@Override
	public boolean isDestroyed() {
		// TODO Auto-generated method stub
		return health <= 0;
	}

	@Override
	public void onDestroy(World world) {
	    // Decrement lives since the tank is destroyed
	    lives--;

	    if (lives <= 0) {
	        // No lives left, remove the tank from the game
	        world.removeTank(this);
	    } else {
	        // Respawn the tank with one less life
	        world.respawnTank(lives);
	    }
	}

	
	public void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
		super.setX(x);
		super.setY(y);
		super.bounds.x = x;
		super.bounds.y = y;
	}
	
	public void setHealth(int health) {this.health = health;}

	public int getLives() {
		// TODO Auto-generated method stub
		return this.lives;
	}
}
