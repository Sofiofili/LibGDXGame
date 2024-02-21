package model;

import com.badlogic.gdx.math.Vector2;

public class Airplane extends GameElement{
    private Vector2 startPosition;
    private Vector2 endPosition;
    private Vector2 currentPosition;
    private float t; // Parameter for the parametric equation
    private float speed; // Controls the speed of the movement
    private boolean isActive; // Indicates if the airplane is active and should be moved and drawn

    public Airplane(Vector2 start, Vector2 end, int height, int width) {
        super(start.x, start.y, height, width, "AIRPLANE", 100);
        this.startPosition = start;
        this.endPosition = end;
        this.t = 0.1f;
        this.isActive = true;
        this.currentPosition = new Vector2(start);
    }

    public void update(float delta) {
        if (isActive) {
            t += speed * delta;
            if (t >= 1.0f) {
                isActive = false; // Deactivate the airplane after reaching the end
                t = 0.1f; // Reset t for the next activation
            }
            currentPosition.x = startPosition.x + t * (endPosition.x - startPosition.x);
            currentPosition.y = startPosition.y + t * (endPosition.y - startPosition.y);
        }
    }



    public void activate() {
        isActive = true;
        t = 0.0f; // Reset t to start the movement again
    }

    public boolean isActive() {
        return isActive;
    }

    public Vector2 getCurrentPosition() {
        return currentPosition;
    }

	public void setSpeed(float f) {
		// TODO Auto-generated method stub
		speed = f;
	}

    // Additional methods like render can be added based on how you handle rendering in your game
}
