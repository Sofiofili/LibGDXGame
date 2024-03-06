package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class EnemyTank2 extends GameElement implements Movable, Destroyable{

	private Random random = new Random();
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
	
	public EnemyTank2(float x, float y, int height, int width, World world) {
        // Assuming textureIndex for Tank is 1, and elementType is "TANK"
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
	

	private void setDegrees(int i) {
		this.degrees = i;
	}
    
    public int getDegrees() {
    	return degrees;
    }

	public Orientation getOrientation() {
        return orientation;
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
        
        if (isAtIntersectionOrEdge()) {
            chooseRandomDirection(world);
        }
        moveInCurrentDirection(delta, world);
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

    private boolean isAtIntersectionOrEdge() {
        int openPaths = 0;
        float x = getX();
        float y = getY();

        // Adjust the check to specifically look for Ground elements
        if (world.isGroundPathOpen(x, y + 40)) openPaths++; // Up
        if (world.isGroundPathOpen(x, y - 40)) openPaths++; // Down
        if (world.isGroundPathOpen(x + 40, y)) openPaths++; // Right
        if (world.isGroundPathOpen(x - 40, y)) openPaths++; // Left

        // Check if exactly three sides are open, which means it's in the middle of three Ground elements
        return openPaths == 3;
    }

    private void chooseRandomDirection(World world) {
        List<Orientation> directions = new ArrayList<Orientation>();
        directions.add(Orientation.UP);
        directions.add(Orientation.DOWN);
        directions.add(Orientation.LEFT);
        directions.add(Orientation.RIGHT);

        Orientation newDirection;
        do {
            newDirection = directions.get(random.nextInt(directions.size()));
            // Ensure the new direction is valid (e.g., not blocked by walls)
        } while (!isValidMove(calculateNewPosition(newDirection), world));
        setOrientation(newDirection);
    }

    private void moveInCurrentDirection(float delta, World world) {
        // Move the tank in the current direction if possible
        Vector2 newPosition = calculateNewPosition(orientation);
        if (isValidMove(newPosition, world)) {
            updatePosition(newPosition);
            setIsMoving(true);
        } else {
            setIsMoving(false);
            // If movement is blocked, choose a new direction immediately
            chooseRandomDirection(world);
        }
    }

    private void updatePosition(Vector2 newPosition) {
        // Update the tank's position
        this.setX(newPosition.x);
        this.setY(newPosition.y);
        // Update the tank's bounding rectangle to the new position
        updateBounds();
    }

    private Vector2 calculateNewPosition(Orientation direction) {
        Vector2 newPosition = new Vector2(this.getX(), this.getY());
        switch (direction) {
            case UP:
                newPosition.y += speed;
                break;
            case DOWN:
                newPosition.y -= speed;
                break;
            case LEFT:
                newPosition.x -= speed;
                break;
            case RIGHT:
                newPosition.x += speed;
                break;
        }
        return newPosition;
    }


    private boolean isValidMove(Vector2 newPosition, World world) {
        Rectangle potentialBounds = new Rectangle(newPosition.x, newPosition.y, getWidth(), getHeight());
        return !world.checkCollisionWithWalls(potentialBounds, this);
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

        // Get the gun position based on the tank's current orientation and position
        Vector2 gunPosition = getGunPosition();

        // Assuming you have a class similar to EnemyBullet that accepts a Vector2 for direction
        // And that you have a method in your World class to add bullets to the game
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
