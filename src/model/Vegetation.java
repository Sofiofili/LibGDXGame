package model;

public class Vegetation extends GameElement{
	public Vegetation(float x, float y, int height, int width) {
        // Assuming textureIndex for Vegetation is 3, and elementType is "VEGETATION"
        super(x, y, height, width, "VEGETATION", 40);
    }
}
