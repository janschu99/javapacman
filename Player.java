import java.util.HashSet;
import java.util.Set;

class Player extends Mover { //This is the pacman object
	
	/* Direction (inherited from Mover) is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
	char currDirection;
	char desiredDirection;
	int pelletsEaten; //Keeps track of pellets eaten to determine end of game
	boolean teleport; //teleport is true when travelling through the teleport tunnels
	boolean stopped = false; //Stopped is set when the pacman is not moving or has been killed
	
	public Player(int x, int y) { //Constructor places pacman in initial location and orientation
		super(x, y);
		teleport = false;
		pelletsEaten = 0;
		currDirection = 'L';
		desiredDirection = 'L';
	}
	
	public char newDirection() { //This function is used for demoMode.  It is copied from the Ghost class.  See that for comments
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
		while (newDirection==backwards || !isValidDest(lookX, lookY)) {
			if (set.size()==3) {
				newDirection = backwards;
				break;
			}
			lookX = x;
			lookY = y;
			int random = (int) (Math.random()*4)+1;
			if (random==1) {
				newDirection = 'L';
				lookX -= Pacman.INCREMENT;
			} else if (random==2) {
				newDirection = 'R';
				lookX += Pacman.GRID_SIZE;
			} else if (random==3) {
				newDirection = 'U';
				lookY -= Pacman.INCREMENT;
			} else if (random==4) {
				newDirection = 'D';
				lookY += Pacman.GRID_SIZE;
			}
			if (newDirection!=backwards) set.add(newDirection);
		}
		return newDirection;
	}
	
	public void demoMove() { //This function is used for demoMode.  It is copied from the Ghost class.  See that for comments
		lastX = x;
		lastY = y;
		if (isChoiceDest()) direction = newDirection();
		switch (direction) {
			case 'L':
				if (isValidDest(x-Pacman.INCREMENT, y)) x -= Pacman.INCREMENT;
				else if (y==9*Pacman.GRID_SIZE && x<2*Pacman.GRID_SIZE) {
					x = Pacman.MAX-Pacman.GRID_SIZE;
					teleport = true;
				}
				break;
			case 'R':
				if (isValidDest(x+Pacman.GRID_SIZE, y)) x += Pacman.INCREMENT;
				else if (y==9*Pacman.GRID_SIZE && x>Pacman.MAX-Pacman.GRID_SIZE*2) {
					x = Pacman.GRID_SIZE;
					teleport = true;
				}
				break;
			case 'U':
				if (isValidDest(x, y-Pacman.INCREMENT)) y -= Pacman.INCREMENT;
				break;
			case 'D':
				if (isValidDest(x, y+Pacman.GRID_SIZE)) y += Pacman.INCREMENT;
				break;
		}
		currDirection = direction;
		frameCount++;
	}
	
	public void move() { //The move function moves the pacman for one frame in non demo mode
		lastX = x;
		lastY = y;
		/* Try to turn in the direction input by the user */
		/*Can only turn if we're in center of a grid*/
		if (x%Pacman.GRID_SIZE==0 && y%Pacman.GRID_SIZE==0 ||
				/* Or if we're reversing*/
				(desiredDirection=='L' && currDirection=='R') ||
				(desiredDirection=='R' && currDirection=='L') ||
				(desiredDirection=='U' && currDirection=='D') ||
				(desiredDirection=='D' && currDirection=='U')) {
			switch (desiredDirection) {
				case 'L':
					if (isValidDest(x-Pacman.INCREMENT, y)) x -= Pacman.INCREMENT;
					break;
				case 'R':
					if (isValidDest(x+Pacman.GRID_SIZE, y)) x += Pacman.INCREMENT;
					break;
				case 'U':
					if (isValidDest(x, y-Pacman.INCREMENT)) y -= Pacman.INCREMENT;
					break;
				case 'D':
					if (isValidDest(x, y+Pacman.GRID_SIZE)) y += Pacman.INCREMENT;
					break;
			}
		}
		if (lastX==x && lastY==y) { //If we haven't moved, then move in the direction the pacman was headed anyway
			switch (currDirection) {
				case 'L':
					if (isValidDest(x-Pacman.INCREMENT, y)) x -= Pacman.INCREMENT;
					else if (y==9*Pacman.GRID_SIZE && x<2*Pacman.GRID_SIZE) {
						x = Pacman.MAX-Pacman.GRID_SIZE;
						teleport = true;
					}
					break;
				case 'R':
					if (isValidDest(x+Pacman.GRID_SIZE, y)) x += Pacman.INCREMENT;
					else if (y==9*Pacman.GRID_SIZE && x>Pacman.MAX-Pacman.GRID_SIZE*2) {
						x = Pacman.GRID_SIZE;
						teleport = true;
					}
					break;
				case 'U':
					if (isValidDest(x, y-Pacman.INCREMENT)) y -= Pacman.INCREMENT;
					break;
				case 'D':
					if (isValidDest(x, y+Pacman.GRID_SIZE)) y += Pacman.INCREMENT;
					break;
			}
		} else currDirection = desiredDirection; //If we did change direction, update currDirection to reflect that
		if (lastX==x && lastY==y) stopped = true; //If we didn't move at all, set the stopped flag
		else { //Otherwise, clear the stopped flag and INCREMENT the frameCount for animation purposes
			stopped = false;
			frameCount++;
		}
	}
	
	public void updatePellet() { //Update what pellet the pacman is on top of
		if (x%Pacman.GRID_SIZE==0 && y%Pacman.GRID_SIZE==0) {
			pelletX = x/Pacman.GRID_SIZE-1;
			pelletY = y/Pacman.GRID_SIZE-1;
		}
	}
}
