package model;

import java.util.Set;

import com.badlogic.gdx.math.Vector2;


public class EnemyTank extends GameElement implements Movable, Destroyable{
	
	private int health = 2;
	private boolean isMoving = false;
	private Vector2 position;
	public int currentFrame = 0;
	public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    }
	private Orientation orientation = Orientation.UP;
    private float speed = 2;
    private int degrees = 0;
	
	public EnemyTank(float x, float y, int height, int width) {
        // Assuming textureIndex for Tank is 1, and elementType is "TANK"
        super(x, y, height, width, "ENEMYTANK", 9);
    }
	
	public void setOrientation(Orientation newOrientation) {
        this.orientation = newOrientation;
        switch (orientation) {
        case UP:
            setDegrees(0);
        case DOWN:
            setDegrees(180);
        case LEFT:
            setDegrees(90);
        case RIGHT:
            setDegrees(270);
        default:
            setDegrees(0);
        }
    }

    private void setDegrees(int i) {
		this.degrees = i;
	}
    
    public int getDegrees() {
    	return degrees;
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
	    float halfWidth = this.getWidth() / 2;
	    float halfHeight = this.getHeight() / 2;

	    switch (orientation) {
	        case UP:
	            // Position the gun at the middle front of the tank (top center)
	            gunPosition.set(this.getX() + halfWidth, this.getY() + this.getHeight());
	            break;
	        case DOWN:
	            // Position the gun at the middle front of the tank (bottom center)
	            gunPosition.set(this.getX() + halfWidth, this.getY());
	            break;
	        case LEFT:
	            // Position the gun at the middle front of the tank (left center)
	            gunPosition.set(this.getX(), this.getY() + halfHeight);
	            break;
	        case RIGHT:
	            // Position the gun at the middle front of the tank (right center)
	            gunPosition.set(this.getX() + this.getWidth(), this.getY() + halfHeight);
	            break;
	    }

	    return gunPosition;
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
		Set<GameElement> enemyTanks = world.elementMap.get("Enemytank");
        if (enemyTanks != null) {
            // Remove the specific BrickWall instance from the set
            enemyTanks.remove(this);

            // Optionally, if the set becomes empty, you might decide to remove the key from the map
            if (enemyTanks.isEmpty()) {
                world.elementMap.remove("Enemytank");
            }
        }
	}
}
