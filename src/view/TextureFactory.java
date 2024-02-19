package view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class TextureFactory {

    private static final HashMap<Integer, Texture> textureCache = new HashMap<Integer, Texture>();

    private TextureFactory() {}

    public static Texture getTexture(int textureIndex) {
        if (textureCache.containsKey(textureIndex)) {
            return textureCache.get(textureIndex);
        }

        Texture texture = createTexture(textureIndex);
        textureCache.put(textureIndex, texture);
        return texture;
    }

    private static Texture createTexture(int textureIndex) {
        String fileName = textureIndex + ".png"; // Check this path is correct
        try {
            Texture texture = new Texture(fileName);
            return texture;
        } catch (Exception e) {
        }
        return null; // Consider what to return here to avoid NullPointerException
    }


    public static Animation<TextureRegion> createTankAnimation(float frameDuration, int firstFrameNumber, int lastFrameNumber) {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = firstFrameNumber; i <= lastFrameNumber; i++) { // Load frames for the tank animation
            frames.add(new TextureRegion(getTexture(i)));
        }
        return new Animation<TextureRegion>(frameDuration, frames);
    }
    
    public static Animation<TextureRegion> createExplosionAnimation(float frameDuration) {
    	Array<TextureRegion> frames = new Array<TextureRegion>();
    	for	(int i = 17; i <= 19; i++) {
    		frames.add(new TextureRegion(getTexture(i)));
    	}
    	return new Animation<TextureRegion>(frameDuration, frames);
    } 

    public static void disposeTexture(int textureIndex) {
        Texture texture = textureCache.remove(textureIndex);
        if (texture != null) {
            texture.dispose();
        }
    }

    public static void disposeAllTextures() {
        for (Texture texture : textureCache.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textureCache.clear();
    }
}
