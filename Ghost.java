class Ghost extends Mover { //Ghost class controls the ghost.
	
	int lastPelletX, lastPelletY; //The pellet the ghost was last on top of
	
	public Ghost(int x, int y, Board board) { //Constructor places ghost and updates states
		super(x, y, board);
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
		if (isChoiceDest()) direction = newDirection(false); //If we can make a decision, pick a new direction randomly
		doMove(direction, false, false); //If that direction is valid, move that way
	}
}
