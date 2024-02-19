package view;

import com.badlogic.gdx.Game;

public class TankGame extends Game{
	private TankScreen myScreen;

    @Override
    public void create() {
        
        myScreen = new TankScreen();
        setScreen(myScreen);
    }

    @Override
    public void dispose() {
        // Dispose of resources when the game is closed
    	super.dispose();
    }
}
