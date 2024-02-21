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
import com.badlogic.gdx.math.Vector2;

public class World {
    public Tank tank; // the players tank
    public EnemyTank enemyTank1; 
    public EnemyTank2 enemyTank2;
    public Airplane airplane; // TODO add to elementMap
    public GameElement trophy;
    public HashMap<String, Set<GameElement>> elementMap;
    public final int maxScreenCol;
    public final int maxScreenRow;
    private final int TILESIZE = 40;
    public ArrayList<Explosion> explosions; // array of the explosions. Are drawn when projectile hits a wall
    public float airplaneTimer; // 
    public float airplaneInterval = 10.0f; // Interval in seconds for airplane appearance

    /**
     * Constructor for the World
     * 
     * this is the class where all the elements are added 
     * the elements are stored in a hashMap
     * 
     * the class contains collision detection and the
     * creation of the game elements
     * 
     * @param maxScreenCol 	this indicates how many tiles are on the x axis
     * 
     * @param maxScreenRow	this indicates how many tiles are on the y axis
     */
    public World(int maxScreenCol, int maxScreenRow) {
        explosions = new ArrayList<Explosion>();
        elementMap = new HashMap<String, Set<GameElement>>();
        this.maxScreenCol = maxScreenCol;
        this.maxScreenRow = maxScreenRow;
        loadGameElements("tileTypes.txt");
        tank = new Tank(46, 46, 24, 20); // initialize the player tank TODO add this to the txt file
        addToElementMap("Tank", tank);
        Vector2 start = new Vector2(-80, 300); // Starting position on the left
        Vector2 end = new Vector2(850, 100); // Ending position on the right
        airplane = new Airplane(start, end, 50, 50); // Initialize the airplane
        airplane.setSpeed(0.5f); // Set the speed of the airplane
        airplaneTimer = 0;
        addToElementMap("Airplane", airplane);
    }
    
    
    /**
     * Method to read and create the GameElements from the "tileTypes.txt" file
     * 
     * @param fileName
     */
    private void loadGameElements(String fileName) {
    	
        Scanner scanner = null;
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
            for (int z = 0; z < 2; z++) {
            	createTank(scanner, z);
            }
            for (int y = maxScreenRow - 1; y >= 0; y--) {
                for (int x = 0; x < maxScreenCol; x++) {
                    if (scanner.hasNextInt()) {
                        int textureIndex = scanner.nextInt();
                        float elementX = x * TILESIZE;
                        float elementY = y * TILESIZE;
                        
                        /*
                         * switch case to evaluate witch GameElement to create
                         */
                        
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
    
    
    /**
     * method for the creation of the enemy tanks
     * 
     * @param scanner
     * @param index
     */
    private void createTank(Scanner scanner, int index) {
		String line = scanner.nextLine();
		String[] words = line.split(" ");
		if (words[0].equals("tank")) {
			float xPosition = Float.parseFloat(words[1]);
			float yPosition = Float.parseFloat(words[2]);
			int width = Integer.parseInt(words[3]);
			int height = Integer.parseInt(words[4]);
			
			if (index == 0) {
				GameElement enemyTank = new EnemyTank(xPosition, yPosition, width, height, this);
				addToElementMap("Enemytank" ,enemyTank);
				enemyTank1 = (EnemyTank) enemyTank;
			} else {
				GameElement enemyTank = new EnemyTank2(xPosition, yPosition, width, height, this);
				addToElementMap("Enemytank2" ,enemyTank);
				enemyTank2 = (EnemyTank2) enemyTank;
			}
		}
	}

    /**
     * here the game elements are added to the hash map
     * 
     * @param type ,this will be the key of the hash map
     * @param element ,added as the value to the hash map 
     */
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
        if (elementMap.containsKey("Enemytank2")) {
        	for (GameElement enemy : elementMap.get("Enemytank2")) {
        		if (enemy.getBounds().overlaps(objectBounds) && object.getElementType() == "BULLET") {
        			EnemyTank2 enemyTank = (EnemyTank2) enemy;
        			enemyTank.takeDamage(1, this);
        			return true;
        		}
        	}
        }
        return false;
    }
    
    public boolean isPathOpen(float x, float y) {
        // Check if the position is outside the bounds of the world
        if (x < 0 || x >= maxScreenCol * TILESIZE || y < 0 || y >= maxScreenRow * TILESIZE) {
            return false;
        }

        // Check if the position hits a wall
        for (GameElement wall : elementMap.get("BrickWall")) {
            if (wall.getBounds().contains(x, y)) {
                return false;
            }
        }
        for (GameElement wall : elementMap.get("SteelWall")) {
            if (wall.getBounds().contains(x, y)) {
                return false;
            }
        }

        // If not outside bounds and not hitting a wall, path is open
        return true;
    }
    
    public boolean isGroundPathOpen(float x, float y) {
        // Assuming you have a method or way to get ground elements specifically
        for (GameElement ground : elementMap.get("Ground")) {
            if (ground.getBounds().contains(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    public void update(float delta) {
        airplaneTimer += delta;

        // Reactivate the airplane at regular intervals if it's not active
        if (!airplane.isActive() && airplaneTimer > airplaneInterval) {
            airplane.activate();
            airplaneTimer -= airplaneInterval;
        }

        airplane.update(delta);
        enemyTank1.update(delta, this, tank);
        enemyTank2.update(delta, this, tank);
        updateBullets(delta);
    }
    
    public void updateBullets(float delta) {
        // Check if the elementMap contains the "Bullet" key and get all bullets
        Set<GameElement> bulletsSet = elementMap.get("Bullet");
        if (bulletsSet != null) {
            // Since we cannot modify the set while iterating, we collect bullets to remove first
            List<Bullet> bulletsToRemove = new ArrayList<Bullet>();
            
            for (GameElement element : bulletsSet) {
                if (element instanceof Bullet) {
                    Bullet bullet = (Bullet) element;
                    bullet.update(delta, this);
                    
                    // Check if the bullet is not active anymore and mark it for removal
                    if (!bullet.isActive()) {
                        explosions.add(new Explosion(new Vector2(bullet.getX(), bullet.getY())));
                        bulletsToRemove.add(bullet);
                    }
                }
            }
            
            // Remove the inactive bullets from the set
            for (Bullet bullet : bulletsToRemove) {
                bulletsSet.remove(bullet);
            }
        }
    }

}
