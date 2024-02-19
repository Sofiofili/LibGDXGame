package model;

import com.badlogic.gdx.math.Vector2;

public class Explosion {
    public Vector2 position;
    public float stateTime;

    public Explosion(Vector2 position) {
        this.position = position;
        this.stateTime = 0;
    }
}
