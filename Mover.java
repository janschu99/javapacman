class Mover { //Both Player and Ghost inherit Mover.  Has generic functions relevant to both
	
	char direction; //Direction mover is heading
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
	boolean[][] state; //State contains the game map
	
	public Mover(int x, int y) { //Generic constructor
		direction = 'L';
		this.lastX = x;
		this.lastY = y;
		this.x = x;
		this.y = y;
		pelletX = x/Pacman.GRID_SIZE-1;
		pelletY = y/Pacman.GRID_SIZE-1;
		state = new boolean[Pacman.BOARD_SIZE][Pacman.BOARD_SIZE];
		for (int i = 0; i<Pacman.BOARD_SIZE; i++) {
			for (int j = 0; j<Pacman.BOARD_SIZE; j++) {
				state[i][j] = false;
			}
		}
	}
	
	public void updateState(boolean[][] state) { //Updates the state information
		for (int i = 0; i<Pacman.BOARD_SIZE; i++) {
			for (int j = 0; j<Pacman.BOARD_SIZE; j++) {
				this.state[i][j] = state[i][j];
			}
		}
	}
	
	public boolean isValidDest(int x, int y) { //Determines if a set of coordinates is a valid destination.
	/* The first statements check that the x and y are inbounds.  The last statement checks the map to
	   see if it's a valid location */
		return (x%Pacman.GRID_SIZE==0 || y%Pacman.GRID_SIZE==0) && Pacman.GRID_SIZE<=x && x<Pacman.MAX && Pacman.GRID_SIZE<=y && y<Pacman.MAX && state[x/Pacman.GRID_SIZE-1][y/Pacman.GRID_SIZE-1];
	}
	
	public boolean isChoiceDest() { //Determines if the location is one where the mover can make a decision
		return x%Pacman.GRID_SIZE==0 && y%Pacman.GRID_SIZE==0;
	}
}
