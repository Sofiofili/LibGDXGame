package model;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameElement {
    private float x;
    private float y;
    private String elementType;
    private int textureIndex;
    protected Rectangle bounds;

    public GameElement(float x, float y, float f, float g, String elementType, int textureIndex) {
        // Calculate the center of the element
        float centerX = x + (g) / 2;
        float centerY = y + (f) / 2;
        // Adjust the coordinates of the bounding rectangle so that it's centered
        float rectX = centerX - g / 2;
        float rectY = centerY - f / 2;
        // Initialize the bounds rectangle with the adjusted coordinates
        this.bounds = new Rectangle(rectX, rectY, g, f);
        this.x = x;
        this.y = y;
        this.elementType = elementType;
        this.textureIndex = textureIndex;
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getElementType() {
        return elementType;
    }

    public int getTextureIndex() {
        return textureIndex;
    }
    
    public void updateBounds() {
        // Assuming your GameElement class has a Rectangle field named 'bounds'
        // which represents the bounding box for collision detection
        this.bounds.setPosition(this.getX(), this.getY());
    }
    
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
    
    public Rectangle getBounds() {
    	return bounds;
    }
    
    public float getWidth() {
    	return bounds.getWidth();
    }
    
    public float getHeight() {
    	return bounds.getHeight();
    }
}
