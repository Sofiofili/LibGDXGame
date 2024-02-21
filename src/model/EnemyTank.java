package model;

import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class EnemyTank extends GameElement implements Movable, Destroyable{
	
	private Vector2 targetPosition;
	private World world;
	private int health = 2;
	private boolean isMoving = false;
	private Vector2 position;
	public int currentFrame = 0;
	public enum Orientation {
        UP, DOWN, LEFT, RIGHT
    }
	private Orientation orientation = Orientation.UP;
    private float speed = 1;
    private int degrees = 0;
	
	public EnemyTank(float x, float y, int height, int width, World world) {
        super(x, y, height, width, "ENEMYTANK", 9);
        this.targetPosition = new Vector2();
        this.world = world;
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
	
	public void update(float delta, World world, Tank playerTank) {
        // Implement movement logic here based on the player's position
        chooseNextMove(playerTank);
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

	private void chooseNextMove(Tank playerTank) {
	    Vector2 playerPosition = new Vector2(playerTank.getX(), playerTank.getY());

	    // Determine the direction to minimize the X distance to the player
	    if (Math.abs(this.getX() - playerPosition.x) > Math.abs(this.getY() - playerPosition.y)) {
	        // Move in X direction
	        if (this.getX() < playerPosition.x) {
	            // Player is to the right
	            moveRight();
	        } else {
	            // Player is to the left
	            moveLeft();
	        }
	    } else {
	        // Move in Y direction
	        if (this.getY() < playerPosition.y) {
	            // Player is above
	            moveUp();
	        } else {
	            // Player is below
	            moveDown();
	        }
	    }
	}

	@Override
	public void moveUp() {
	    float newY = this.getY() + speed;
	    Rectangle potentialBounds = new Rectangle(getX(), newY, getWidth(), getHeight());
	    if (!world.checkCollisionWithWalls(potentialBounds, this)) {
	        setIsMoving(true);
	        super.setY(newY);
	        updateBounds();
	        setOrientation(Orientation.UP);
	    } else {
	        setIsMoving(false);
	    }
	}

	@Override
	public void moveDown() {
	    float newY = this.getY() - speed;
	    Rectangle potentialBounds = new Rectangle(getX(), newY, getWidth(), getHeight());
	    if (!world.checkCollisionWithWalls(potentialBounds, this)) {
	        setIsMoving(true);
	        super.setY(newY);
	        setOrientation(Orientation.DOWN);
	        updateBounds();
	    } else {
	        setIsMoving(false);
	    }
	}

	@Override
	public void moveLeft() {
	    float newX = this.getX() - speed;
	    Rectangle potentialBounds = new Rectangle(newX, getY(), getWidth(), getHeight());
	    if (!world.checkCollisionWithWalls(potentialBounds, this)) {
	        setIsMoving(true);
	        super.setX(newX);
	        setOrientation(Orientation.LEFT);
	        updateBounds();
	    } else {
	        setIsMoving(false);
	    }
	}

	@Override
	public void moveRight() {
	    float newX = this.getX() + speed;
	    Rectangle potentialBounds = new Rectangle(newX, getY(), getWidth(), getHeight());
	    if (!world.checkCollisionWithWalls(potentialBounds, this)) {
	        setIsMoving(true);
	        super.setX(newX);
	        setOrientation(Orientation.RIGHT);
	        updateBounds();
	    } else {
	        setIsMoving(false);
	    }
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
