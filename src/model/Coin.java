package model;

import java.util.Set;

public class Coin extends GameElement{

	public Coin(float x, float y, float f, float g) {
		super(x, y, f, g, "COIN", 200);
	}
	
	public void remove(World world) {
		Set<GameElement> coins = world.elementMap.get("Coin");
        if (coins != null) {
            // Remove the specific BrickWall instance from the set
            coins.remove(this);

            // Optionally, if the set becomes empty, you might decide to remove the key from the map
            if (coins.isEmpty()) {
                world.elementMap.remove("Coin");
            }
        }
	}
}
