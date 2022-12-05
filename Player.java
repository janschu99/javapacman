class Player extends Mover { //This is the pacman object
	
	/* Direction (inherited from Mover) is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
	Direction currDirection = Direction.LEFT;
	Direction desiredDirection = Direction.LEFT;
	boolean teleport = false; //teleport is true when travelling through the teleport tunnels
	boolean stopped = false; //Stopped is set when the pacman is not moving or has been killed
	
	public Player(int x, int y, Board board) { //Constructor places pacman in initial location and orientation
		super(x, y, board);
	}
	
	public void demoMove() { //This function is used for demoMode.  It is copied from the Ghost class.  See that for comments
		lastX = x;
		lastY = y;
		if (isChoiceDest()) direction = newDirection(true);
		if (doMove(direction, true, true)) teleport = true;
		currDirection = direction;
		frameCount++;
	}
	
	public void move() { //The move function moves the pacman for one frame in non demo mode
		lastX = x;
		lastY = y;
		/* Try to turn in the direction input by the user */
		/*Can only turn if we're in center of a grid or if we're reversing*/
		if (isChoiceDest() || currDirection.getReverse()==desiredDirection) doMove(desiredDirection, false, true);
		if (lastX==x && lastY==y) { //If we haven't moved, then move in the direction the pacman was headed anyway
			if (doMove(currDirection, true, true)) teleport = true;
		} else currDirection = desiredDirection; //If we did change direction, update currDirection to reflect that
		if (lastX==x && lastY==y) stopped = true; //If we didn't move at all, set the stopped flag
		else { //Otherwise, clear the stopped flag and increment the frameCount for animation purposes
			stopped = false;
			frameCount++;
		}
	}
	
	public void updatePellet() { //Update what pellet the pacman is on top of
		if (isChoiceDest()) {
			pelletX = x/Pacman.GRID_SIZE-1;
			pelletY = y/Pacman.GRID_SIZE-1;
		}
	}
}
