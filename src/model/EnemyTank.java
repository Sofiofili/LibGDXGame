package model;

import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class EnemyTank extends GameElement implements Movable, Destroyable{
	private Vector2 startPosition;
	private float lastShotTime; // Time since the last shot was fired
	private float defaultShootInterval = 2.0f; // Shoot every 2 seconds by default
	private float alignedShootInterval = 1.0f; // Shoot every 1 second when aligned
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
    private float speed = 0.7f;
    private int degrees = 0;
	
	public EnemyTank(float x, float y, int height, int width, World world) {
        super(x, y, height, width, "ENEMYTANK", 9);
        this.targetPosition = new Vector2();
        this.world = world;
        this.startPosition = new Vector2(x, y);
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
	    if (playerTank == null) return;
	    Vector2 playerPosition = new Vector2(playerTank.getX(), playerTank.getY());

	    // Check alignment with the player
	    boolean alignedWithPlayer = isAlignedWithPlayer(playerPosition);

	    // Calculate the time elapsed since the last shot
	    float timeSinceLastShot = world.getTime() - lastShotTime;

	    // Determine the appropriate shooting interval based on alignment
	    float currentShootInterval = alignedWithPlayer ? alignedShootInterval : defaultShootInterval;

	    // Shooting logic based on alignment and timing
	    if (timeSinceLastShot >= currentShootInterval) {
	        shoot();
	        lastShotTime = world.getTime(); // Update the last shot time to the current time
	    }
	    
	    chooseNextMove(playerTank);
	}


	private boolean isAlignedWithPlayer(Vector2 playerPosition) {
	    // Check if the tank is facing towards the player position
	    switch (orientation) {
	        case UP:
	            return this.getY() < playerPosition.y && Math.abs(this.getX() - playerPosition.x) < this.getWidth() / 2;
	        case DOWN:
	            return this.getY() > playerPosition.y && Math.abs(this.getX() - playerPosition.x) < this.getWidth() / 2;
	        case LEFT:
	            return this.getX() > playerPosition.x && Math.abs(this.getY() - playerPosition.y) < this.getHeight() / 2;
	        case RIGHT:
	            return this.getX() < playerPosition.x && Math.abs(this.getY() - playerPosition.y) < this.getHeight() / 2;
	        default:
	            return false;
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

	private void chooseNextMove(Tank playerTank) {
	    Vector2 playerPosition = new Vector2(playerTank.getX(), playerTank.getY());

	    boolean canMoveUp = !world.checkCollisionWithWalls(new Rectangle(getX(), getY() + speed, getWidth(), getHeight()), this);
	    boolean canMoveDown = !world.checkCollisionWithWalls(new Rectangle(getX(), getY() - speed, getWidth(), getHeight()), this);
	    boolean canMoveLeft = !world.checkCollisionWithWalls(new Rectangle(getX() - speed, getY(), getWidth(), getHeight()), this);
	    boolean canMoveRight = !world.checkCollisionWithWalls(new Rectangle(getX() + speed, getY(), getWidth(), getHeight()), this);

	    // Check if the tank is stuck in a corner (cannot move in two perpendicular directions)
	    boolean isStuck = !canMoveUp && !canMoveRight || !canMoveUp && !canMoveLeft || !canMoveDown && !canMoveRight || !canMoveDown && !canMoveLeft;

	    // Try moving in an available direction if stuck
	    if (isStuck) {
	        if (canMoveUp) {
	            moveUp();
	            return;
	        } else if (canMoveDown) {
	            moveDown();
	            return;
	        } else if (canMoveLeft) {
	            moveLeft();
	            return;
	        } else if (canMoveRight) {
	            moveRight();
	            return;
	        }
	    }

	    // Normal movement logic when not stuck
	    if (Math.abs(this.getX() - playerPosition.x) > Math.abs(this.getY() - playerPosition.y)) {
	        if (this.getX() < playerPosition.x && canMoveRight) {
	            moveRight();
	        } else if (this.getX() > playerPosition.x && canMoveLeft) {
	            moveLeft();
	        } else if (this.getY() < playerPosition.y && canMoveUp) {
	            moveUp();
	        } else if (canMoveDown) {
	            moveDown();
	        }
	    } else {
	        if (this.getY() < playerPosition.y && canMoveUp) {
	            moveUp();
	        } else if (this.getY() > playerPosition.y && canMoveDown) {
	            moveDown();
	        } else if (this.getX() < playerPosition.x && canMoveRight) {
	            moveRight();
	        } else if (canMoveLeft) {
	            moveLeft();
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
        Vector2 direction = new Vector2();
        
        // Calculate the bullet's direction based on the tank's orientation
        switch (this.orientation) {
            case UP:
                direction.set(0, 1); // Move up
                break;
            case DOWN:
                direction.set(0, -1); // Move down
                break;
            case LEFT:
                direction.set(-1, 0); // Move left
                break;
            case RIGHT:
                direction.set(1, 0); // Move right
                break;
        }

        // Assuming EnemyBullet constructor now takes a direction vector
        Vector2 gunPosition = getGunPosition();
        EnemyBullet bullet = new EnemyBullet(gunPosition.x, gunPosition.y, direction);
        world.addToElementMap("Bullet", bullet);
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
		health -= damageAmount;
        if (health <= 0) {
            onDestroy(world);
        }
	}

	@Override
	public boolean isDestroyed() {
		return health <= 0;
	}

	@Override
	public void onDestroy(World world) {
	    // Reset the tank's position to its starting location
	    this.setX(startPosition.x);
	    this.setY(startPosition.y);
	    
	    // Reset the tank's health
	    this.health = 2; // Assuming 2 is the starting health
	    
	    // Reset any other relevant properties to their starting values
	    this.lastShotTime = 0; // Reset the shooting timer
	    this.isMoving = false; // Reset movement status

	}
	
	 public Vector2 getPosition() {
	    	return position;
	    }

}
