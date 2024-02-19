package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import com.badlogic.gdx.utils.Array;

import view.TextureFactory;

import com.badlogic.gdx.math.Rectangle;

public class World {
    public Tank tank;
    public EnemyTank enemyTank1;
    public EnemyTank enemyTank2;
    public GameElement trophy;
    public HashMap<String, Set<GameElement>> elementMap;
    public final int maxScreenCol;
    public final int maxScreenRow;
    private final int TILESIZE = 40;
    public ArrayList<Explosion> explosions;

    public World(int maxScreenCol, int maxScreenRow) {
        explosions = new ArrayList<Explosion>();
        elementMap = new HashMap<String, Set<GameElement>>();
        this.maxScreenCol = maxScreenCol;
        this.maxScreenRow = maxScreenRow;
        loadGameElements("tileTypes.txt");
        tank = new Tank(46, 46, 26, 26);
        enemyTank1 = new EnemyTank(200, 200, 26, 26);
        addToElementMap("Tank", tank);
        addToElementMap("Enemytank", enemyTank1);
    }

    private void loadGameElements(String fileName) {
    	
        Scanner scanner = null;
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);

            for (int y = maxScreenRow - 1; y >= 0; y--) {
                for (int x = 0; x < maxScreenCol; x++) {
                    if (scanner.hasNextInt()) {
                        int textureIndex = scanner.nextInt();
                        float elementX = x * TILESIZE;
                        float elementY = y * TILESIZE;
                        switch (textureIndex) {
                        case 0:
                            GameElement ground = new Ground(elementX, elementY, TILESIZE, TILESIZE);
                            addToElementMap("Ground", ground);
                            break;
                        case 2:
                            GameElement brickWall = new BrickWall(elementX, elementY, TILESIZE, TILESIZE);
                            addToElementMap("BrickWall", brickWall);
                            break;
                        case 4:
                        	GameElement steelWall = new SteelWall(elementX, elementY, TILESIZE, TILESIZE);
                            addToElementMap("SteelWall", steelWall);
                            break;
                        case 7:
                        	GameElement vegitation = new Vegetation(elementX, elementY, TILESIZE, TILESIZE);
                            addToElementMap("Vegitation", vegitation);
                            break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    public void addToElementMap(String type, GameElement element) {
        if (!elementMap.containsKey(type)) {
            elementMap.put(type, new HashSet<GameElement>());
        }
        elementMap.get(type).add(element);
    }
    
    public boolean checkCollisionWithWalls(Rectangle objectBounds, GameElement object) {
        if (elementMap.containsKey("BrickWall")) {
            for (GameElement wall : elementMap.get("BrickWall")) {
                if (wall.getBounds().overlaps(objectBounds)) {
                	if (object.getElementType() == "BULLET") {
                		Destroyable destroyableElement = (Destroyable) wall;
                        destroyableElement.takeDamage(1, this); // Assuming each bullet does 1 damage
                	}
                	return true;
                }
            }
        }
        if (elementMap.containsKey("SteelWall")) {
            for (GameElement wall : elementMap.get("SteelWall")) {
                if (wall.getBounds().overlaps(objectBounds)) return true;
            }
        }
        if (elementMap.containsKey("Enemytank")) {
        	for (GameElement enemy : elementMap.get("Enemytank")) {
        		if (enemy.getBounds().overlaps(objectBounds) && object.getElementType() == "BULLET") {
        			EnemyTank enemyTank = (EnemyTank) enemy;
        			enemyTank.takeDamage(1, this);
        			return true;
        		}
        	}
        }
        return false;
    }
}
