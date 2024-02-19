package model;

import java.util.Set;

public class BrickWall extends GameElement implements Destroyable{
	private int health = 3;
	
	public BrickWall(float x, float y, int height, int width) {
        // Assuming textureIndex for Wall is 2, and elementType is "WALL"
        super(x, y, height, width, "WALL", 27);
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
		// TODO Auto-generated method stub
		Set<GameElement> brickWalls = world.elementMap.get("BrickWall");
        if (brickWalls != null) {
            // Remove the specific BrickWall instance from the set
            brickWalls.remove(this);

            // Optionally, if the set becomes empty, you might decide to remove the key from the map
            if (brickWalls.isEmpty()) {
                world.elementMap.remove("BrickWall");
            }
        }
        // Create a new Ground instance at the same location
        Ground ground = new Ground(this.getX(), this.getY(), this.getHeight(), this.getWidth());
        
        // Add the new Ground instance to the game world
        world.addToElementMap("Ground", ground);
	}
}
