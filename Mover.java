import java.util.HashSet;
import java.util.Set;

class Mover { //Both Player and Ghost inherit Mover.  Has generic functions relevant to both
	
	char direction = 'L'; //Direction mover is heading
	/* Last location */
	int lastX;
	int lastY;
	/* Current location */
	int x;
	int y;
	/* Which pellet the mover is on top of */
	int pelletX;
	int pelletY;
	int frameCount = 0; //frameCount is used to count animation frames
	final Board board;
	
	public Mover(int x, int y, Board board) { //Generic constructor
		this.lastX = x;
		this.lastY = y;
		this.x = x;
		this.y = y;
		pelletX = x/Pacman.GRID_SIZE-1;
		pelletY = y/Pacman.GRID_SIZE-1;
		this.board = board;
	}
	
	public boolean doMove(char dir, boolean allowTeleport, boolean isPlayer) { //Returns whether teleportation happened
	                                                                     //Player is not allowed to enter the ghost box
		switch (dir) {
			case 'L':
				if (board.isValidDest(x-Pacman.INCREMENT, y, isPlayer)) x -= Pacman.INCREMENT;
				else if (allowTeleport && y==9*Pacman.GRID_SIZE && x<2*Pacman.GRID_SIZE) {
					x = Pacman.MAX-Pacman.GRID_SIZE;
					return true;
				}
				break;
			case 'R':
				if (board.isValidDest(x+Pacman.GRID_SIZE, y, isPlayer)) x += Pacman.INCREMENT;
				else if (allowTeleport && y==9*Pacman.GRID_SIZE && x>Pacman.MAX-Pacman.GRID_SIZE*2) {
					x = Pacman.GRID_SIZE;
					return true;
				}
				break;
			case 'U':
				if (board.isValidDest(x, y-Pacman.INCREMENT, isPlayer)) y -= Pacman.INCREMENT;
				break;
			case 'D':
				if (board.isValidDest(x, y+Pacman.GRID_SIZE, isPlayer)) y += Pacman.INCREMENT;
				break;
		}
		return false;
	}
	
	public char newDirection(boolean isPlayer) { //Chooses a new direction randomly for the mover to move
	                                             //Player is not allowed to enter the ghost box
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
		while (newDirection==backwards || !board.isValidDest(lookX, lookY, isPlayer)) { //While we still haven't found a valid direction
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
	
	public boolean isChoiceDest() { //Determines if the location is one where the mover can make a decision
		return x%Pacman.GRID_SIZE==0 && y%Pacman.GRID_SIZE==0;
	}
}
