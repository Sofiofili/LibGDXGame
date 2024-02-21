package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class EnemyTank2 extends GameElement implements Movable, Destroyable{

	private Random random = new Random();

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
	
	public EnemyTank2(float x, float y, int height, int width, World world) {
        // Assuming textureIndex for Tank is 1, and elementType is "TANK"
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
        if (isAtIntersectionOrEdge()) {
            chooseRandomDirection(world);
        }
        moveInCurrentDirection(delta, world);
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
		Set<GameElement> enemyTanks = world.elementMap.get("Enemytank2");
        if (enemyTanks != null) {
            // Remove the specific BrickWall instance from the set
            enemyTanks.remove(this);

            // Optionally, if the set becomes empty, you might decide to remove the key from the map
            if (enemyTanks.isEmpty()) {
                world.elementMap.remove("Enemytank2");
            }
        }
	}
}
