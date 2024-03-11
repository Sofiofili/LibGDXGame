package view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import controller.InputHandler;
import controller.WorldRenderer;
import model.World;

public class TankScreen implements Screen{
	private SpriteBatch batch;
    private World world;
    private WorldRenderer worldRenderer;
    private InputHandler inputHandler;
    private OrthographicCamera camera;

    @Override
    public void show() {
    	camera = new OrthographicCamera();
        batch = new SpriteBatch();
        world = new World(20, 20);
        worldRenderer = new WorldRenderer(world);
        inputHandler = new InputHandler(world.tank, world);
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void render(float delta) {
    	inputHandler.update(delta);
    	worldRenderer.updateBullets(delta);
    	world.enemyTank1.update(delta, world, world.tank);
    	world.enemyTank2.update(delta, world, world.tank);
    	world.update(delta);
    	
    	if(world.gameWon) {
    		((Game)Gdx.app.getApplicationListener()).setScreen(new VictoryScreen(world.getTime()));
    	}
    	
    	if(world.isGameOver()) {
            ((Game)Gdx.app.getApplicationListener()).setScreen(new GameOverScreen());
            return; // Prevents further rendering and updating after the game over screen is set
        }
    	
        batch.begin();
        worldRenderer.render(batch, delta);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    	camera.setToOrtho(false, width, height);
        camera.position.set (width / 2, height / 2, 0);
        camera.update();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        TextureFactory.disposeAllTextures();
    }

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
