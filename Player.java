import java.util.HashSet;
import java.util.Set;

class Player extends Mover { //This is the pacman object
	/* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
	char direction;
	char currDirection;
	char desiredDirection;
	int pelletsEaten; //Keeps track of pellets eaten to determine end of game
	/* Last location */
	int lastX;
	int lastY;
	/* Current location */
	int x;
	int y;
	/* Which pellet the pacman is on top of */
	int pelletX;
	int pelletY;
	boolean teleport; //teleport is true when travelling through the teleport tunnels
	boolean stopped = false; //Stopped is set when the pacman is not moving or has been killed
	
	public Player(int x, int y) { //Constructor places pacman in initial location and orientation
		teleport = false;
		pelletsEaten = 0;
		pelletX = x/gridSize-1;
		pelletY = y/gridSize-1;
		this.lastX = x;
		this.lastY = y;
		this.x = x;
		this.y = y;
		currDirection = 'L';
		desiredDirection = 'L';
	}
	
	public char newDirection() { //This function is used for demoMode.  It is copied from the Ghost class.  See that for comments
		int random;
		char backwards = 'U';
		int newX = x, newY = y;
		int lookX = x, lookY = y;
		Set<Character> set = new HashSet<Character>();
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
		while (newDirection==backwards || !isValidDest(lookX, lookY)) {
			if (set.size()==3) {
				newDirection = backwards;
				break;
			}
			newX = x;
			newY = y;
			lookX = x;
			lookY = y;
			random = (int) (Math.random()*4)+1;
			if (random==1) {
				newDirection = 'L';
				newX -= increment;
				lookX -= increment;
			} else if (random==2) {
				newDirection = 'R';
				newX += increment;
				lookX += gridSize;
			} else if (random==3) {
				newDirection = 'U';
				newY -= increment;
				lookY -= increment;
			} else if (random==4) {
				newDirection = 'D';
				newY += increment;
				lookY += gridSize;
			}
			if (newDirection!=backwards) set.add(new Character(newDirection));
		}
		return newDirection;
	}
	
	public boolean isChoiceDest() { //This function is used for demoMode.  It is copied from the Ghost class.  See that for comments
		if (x%gridSize==0 && y%gridSize==0) {
			return true;
		}
		return false;
	}
	
	public void demoMove() { //This function is used for demoMode.  It is copied from the Ghost class.  See that for comments
		lastX = x;
		lastY = y;
		if (isChoiceDest()) direction = newDirection();
		switch (direction) {
			case 'L':
				if (isValidDest(x-increment, y)) {
					x -= increment;
				} else if (y==9*gridSize && x<2*gridSize) {
					x = max-gridSize*1;
					teleport = true;
				}
				break;
			case 'R':
				if (isValidDest(x+gridSize, y)) {
					x += increment;
				} else if (y==9*gridSize && x>max-gridSize*2) {
					x = 1*gridSize;
					teleport = true;
				}
				break;
			case 'U':
				if (isValidDest(x, y-increment))
					y -= increment;
				break;
			case 'D':
				if (isValidDest(x, y+gridSize))
					y += increment;
				break;
		}
		currDirection = direction;
		frameCount++;
	}
	
	public void move() { //The move function moves the pacman for one frame in non demo mode
		int gridSize = 20;
		lastX = x;
		lastY = y;
		/* Try to turn in the direction input by the user */
		/*Can only turn if we're in center of a grid*/
		if (x%20==0 && y%20==0 ||
				/* Or if we're reversing*/
				(desiredDirection=='L' && currDirection=='R') ||
				(desiredDirection=='R' && currDirection=='L') ||
				(desiredDirection=='U' && currDirection=='D') ||
				(desiredDirection=='D' && currDirection=='U')
		) {
			switch (desiredDirection) {
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
		if (lastX==x && lastY==y) { //If we haven't moved, then move in the direction the pacman was headed anyway
			switch (currDirection) {
				case 'L':
					if (isValidDest(x-increment, y)) x -= increment;
					else if (y==9*gridSize && x<2*gridSize) {
						x = max-gridSize*1;
						teleport = true;
					}
					break;
				case 'R':
					if (isValidDest(x+gridSize, y)) x += increment;
					else if (y==9*gridSize && x>max-gridSize*2) {
						x = 1*gridSize;
						teleport = true;
					}
					break;
				case 'U':
					if (isValidDest(x, y-increment)) y -= increment;
					break;
				case 'D':
					if (isValidDest(x, y+gridSize)) y += increment;
					break;
			}
		} else currDirection = desiredDirection; //If we did change direction, update currDirection to reflect that
		if (lastX==x && lastY==y) stopped = true; //If we didn't move at all, set the stopped flag
		else { //Otherwise, clear the stopped flag and increment the frameCount for animation purposes
			stopped = false;
			frameCount++;
		}
	}
	
	public void updatePellet() { //Update what pellet the pacman is on top of
		if (x%gridSize==0 && y%gridSize==0) {
			pelletX = x/gridSize-1;
			pelletY = y/gridSize-1;
		}
	}
}
