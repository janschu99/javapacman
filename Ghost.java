import java.util.HashSet;
import java.util.Set;

class Ghost extends Mover { //Ghost class controls the ghost.
	
	int lastPelletX, lastPelletY; //The pellet the ghost was last on top of
	
	public Ghost(int x, int y) { //Constructor places ghost and updates states
		direction = 'L';
		pelletX = x/gridSize-1;
		pelletY = x/gridSize-1;
		lastPelletX = pelletX;
		lastPelletY = pelletY;
		this.lastX = x;
		this.lastY = y;
		this.x = x;
		this.y = y;
	}
	
	public void updatePellet() { //update pellet status
		int tempX = x/gridSize-1;
		int tempY = y/gridSize-1;
		if (tempX!=pelletX || tempY!=pelletY) {
			lastPelletX = pelletX;
			lastPelletY = pelletY;
			pelletX = tempX;
			pelletY = tempY;
		}
	}
	
	public char newDirection() { //Chooses a new direction randomly for the ghost to move
		char backwards = 'U';
		int lookX = x, lookY = y;
		switch (direction) {
			case 'L':
				backwards = 'R';
				break;
			case 'R':
				backwards = 'L';
				break;
			case 'U':
				backwards = 'D';
				break;
			case 'D':
				backwards = 'U';
				break;
		}
		char newDirection = backwards;
		Set<Character> set = new HashSet<>();
		while (newDirection==backwards || !isValidDest(lookX, lookY)) { //While we still haven't found a valid direction
			if (set.size()==3) { //If we've tried every location, turn around and break the loop
				newDirection = backwards;
				break;
			}
			lookX = x;
			lookY = y;
			/* Randomly choose a direction */
			int random = (int) (Math.random()*4)+1;
			if (random==1) {
				newDirection = 'L';
				lookX -= increment;
			} else if (random==2) {
				newDirection = 'R';
				lookX += gridSize;
			} else if (random==3) {
				newDirection = 'U';
				lookY -= increment;
			} else if (random==4) {
				newDirection = 'D';
				lookY += gridSize;
			}
			if (newDirection!=backwards) set.add(newDirection);
		}
		return newDirection;
	}
	
	public void move() { //Random move function for ghost
		lastX = x;
		lastY = y;
		if (isChoiceDest()) direction = newDirection(); //If we can make a decision, pick a new direction randomly
		switch (direction) { //If that direction is valid, move that way
			case 'L':
				if (isValidDest(x-increment, y)) x -= increment;
				break;
			case 'R':
				if (isValidDest(x+gridSize, y)) x += increment;
				break;
			case 'U':
				if (isValidDest(x, y-increment)) y -= increment;
				break;
			case 'D':
				if (isValidDest(x, y+gridSize)) y += increment;
				break;
		}
	}
}
