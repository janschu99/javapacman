class Ghost extends Mover { //Ghost class controls the ghost.
	
	int lastPelletX, lastPelletY; //The pellet the ghost was last on top of
	
	public Ghost(int x, int y) { //Constructor places ghost and updates states
		super(x, y);
		lastPelletX = pelletX;
		lastPelletY = pelletY;
	}
	
	public void updatePellet() { //update pellet status
		int tempX = x/Pacman.GRID_SIZE-1;
		int tempY = y/Pacman.GRID_SIZE-1;
		if (tempX!=pelletX || tempY!=pelletY) {
			lastPelletX = pelletX;
			lastPelletY = pelletY;
			pelletX = tempX;
			pelletY = tempY;
		}
	}
	
	public void move() { //Random move function for ghost
		lastX = x;
		lastY = y;
		if (isChoiceDest()) direction = newDirection(); //If we can make a decision, pick a new direction randomly
		switch (direction) { //If that direction is valid, move that way
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
}
