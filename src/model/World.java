package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import com.badlogic.gdx.utils.Array;

import view.TextureFactory;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class World {
	public boolean gameWon = false;
	private long startTime;
    public Tank tank;
    public EnemyTank enemyTank1;
    public EnemyTank2 enemyTank2;
    public Airplane airplane;
    public GameElement trophy;
    public HashMap<String, Set<GameElement>> elementMap;
    public final int maxScreenCol;
    public final int maxScreenRow;
    private final int TILESIZE = 40;
    public ArrayList<Explosion> explosions;
    public float airplaneTimer;
    public float airplaneInterval = 10.0f; // Interval in seconds for airplane appearance


    public World(int maxScreenCol, int maxScreenRow) {
    	
        explosions = new ArrayList<Explosion>();
        elementMap = new HashMap<String, Set<GameElement>>();
        this.maxScreenCol = maxScreenCol;
        this.maxScreenRow = maxScreenRow;
        loadGameElements("tileTypes.txt");
        loadCoinsToGame("Coins.txt");
        Vector2 start = new Vector2(-80, 300); // Starting position on the left
        Vector2 end = new Vector2(800, 100); // Ending position on the right
        airplane = new Airplane(start, end, 50, 50); // Initialize the airplane
        airplane.setSpeed(0.5f); // Set the speed of the airplane
        airplaneTimer = 0;
        addToElementMap("Airplane", airplane);
        startTime = System.currentTimeMillis();
    }

    private void loadCoinsToGame(String string) {
		Scanner scanner = null;
		try {
			File file = new File(string);
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] words = line.split(" ");
				
				Coin coin = new Coin(Integer.parseInt(words[1]), Integer.parseInt(words[2]), 30, 30);
				addToElementMap("Coin", coin);
			}
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
	}

	private void loadGameElements(String fileName) {
    	
        Scanner scanner = null;
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
            for (int z = 0; z < 4; z++) {
            	createTank(scanner, z);
            }
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
    
    private void createTank(Scanner scanner, int index) {
		String line = scanner.nextLine();
		String[] words = line.split(" ");
		float xPosition = Float.parseFloat(words[1]);
		float yPosition = Float.parseFloat(words[2]);
		int width = Integer.parseInt(words[3]);
		int height = Integer.parseInt(words[4]);
		if (words[0].equals("tank")) {
			if (index == 0) {
				GameElement enemyTank = new EnemyTank(xPosition, yPosition, width, height, this);
				addToElementMap("Enemytank" ,enemyTank);
				enemyTank1 = (EnemyTank) enemyTank;
			}
			else if (index == 2) {
				int lives = Integer.parseInt(words[5]);
				this.tank = new Tank(xPosition, yPosition, width, height, lives);
				addToElementMap("Tank" ,tank);
			}
			else {
				GameElement enemyTank = new EnemyTank2(xPosition, yPosition, width, height, this);
				addToElementMap("Enemytank2" ,enemyTank);
				enemyTank2 = (EnemyTank2) enemyTank;
			}
		} else {

			GameElement trophy = new Trophy(xPosition, yPosition, width, height);
			addToElementMap("Trophy", trophy);
			
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
                	if (object instanceof PlayerBullet) {
                		Destroyable destroyableElement = (Destroyable) wall;
                        destroyableElement.takeDamage(1, this); // Assuming each bullet does 1 damage
                	}
                	return true;
                }
            }
        }
        
        if (object instanceof Tank) {
        	
        	if (noCoins()) {
        		Set<GameElement> trophySet = elementMap.get("Trophy");
        		
        		for (GameElement trophy : trophySet) {
                    if (trophy.getBounds().overlaps(tank.getBounds())) {
                        gameWon = true;
                        break; // Exit the loop once the winning condition is met
                    }
                }
        	}
        }
       
        if (object instanceof Tank && elementMap.containsKey("Coin")) {
            Iterator<GameElement> iterator = elementMap.get("Coin").iterator();
            while (iterator.hasNext()) {
                GameElement coin = iterator.next();
                if (coin.getBounds().overlaps(object.getBounds())) {
                    iterator.remove(); // Safely remove the coin
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
        		if (enemy.getBounds().overlaps(objectBounds) && object instanceof Bullet) {
        			if (object instanceof PlayerBullet) {
        				EnemyTank enemyTank = (EnemyTank) enemy;
            			enemyTank.takeDamage(1, this);
            			return true;
        			}	
        		}
        	}
        }
        if (elementMap.containsKey("Enemytank2")) {
        	for (GameElement enemy : elementMap.get("Enemytank2")) {
        		if (enemy.getBounds().overlaps(objectBounds) && object instanceof Bullet) {
        			if (object instanceof PlayerBullet) { 

            			EnemyTank2 enemyTank = (EnemyTank2) enemy;
            			enemyTank.takeDamage(1, this);
            			return true;
        			}
        		}
        	}
        }
        if (this.tank != null && this.tank.getBounds() != null) {
            if (this.tank.getBounds().overlaps(objectBounds) && object instanceof EnemyBullet) {
                this.tank.takeDamage(1, this);
                return true;
            }
        }
        
        return false;
    }
    
    private boolean noCoins() {
        Set<GameElement> coins = elementMap.get("Coin");
        // Return true if coins is null or empty, indicating no coins left
        return (coins == null || coins.isEmpty());
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
            airplaneTimer -= airplaneInterval; // Instead of resetting to 0, subtract the interval
        }

        airplane.update(delta);

    }

    public float getTime() {
        long currentTime = System.currentTimeMillis(); // Get the current time
        return (currentTime - startTime) / 1000.0f; // Return the difference in seconds
    }

    public void respawnTank(int remainingLives) {
    	tank.setPosition(46, 46);
    	tank.setHealth(2);
    }
    
    public void removeTank(Tank tank) {
        // Remove the tank from the elementMap or other collections
        Set<GameElement> tanks = elementMap.get("Tank");
        if (tanks != null) {
            tanks.remove(tank);
            if (tanks.isEmpty()) {
                elementMap.remove("Tank");
            }
        }
        
        // Nullify the reference if it's directly accessible
        this.tank = null;
    }
    
    public boolean isGameOver() {
    	if (tank == null) {
    		return true;
    	}
        return false;
    }


}
