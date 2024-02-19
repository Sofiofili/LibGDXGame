package model;

public interface Destroyable {
	void takeDamage(int damageAmount, World world);
    boolean isDestroyed();
    void onDestroy(World world);
}
