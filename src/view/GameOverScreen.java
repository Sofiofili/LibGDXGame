package view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class GameOverScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private BitmapFont font;

    public GameOverScreen() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Game Over! Tap anywhere to restart.", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        batch.end();

        if (Gdx.input.justTouched()) {
        	((Game)Gdx.app.getApplicationListener()).setScreen(new TankScreen());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
