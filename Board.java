import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JPanel;

public class Board extends JPanel { //This board class contains the player, ghosts, pellets, and most of the game logic.
	/* Initialize the images*/
	Image pacmanImage = Toolkit.getDefaultToolkit().getImage("img/pacman.jpg");
	Image pacmanUpImage = Toolkit.getDefaultToolkit().getImage("img/pacmanup.jpg");
	Image pacmanDownImage = Toolkit.getDefaultToolkit().getImage("img/pacmandown.jpg");
	Image pacmanLeftImage = Toolkit.getDefaultToolkit().getImage("img/pacmanleft.jpg");
	Image pacmanRightImage = Toolkit.getDefaultToolkit().getImage("img/pacmanright.jpg");
	Image ghost10 = Toolkit.getDefaultToolkit().getImage("img/ghost10.jpg");
	Image ghost20 = Toolkit.getDefaultToolkit().getImage("img/ghost20.jpg");
	Image ghost30 = Toolkit.getDefaultToolkit().getImage("img/ghost30.jpg");
	Image ghost40 = Toolkit.getDefaultToolkit().getImage("img/ghost40.jpg");
	Image ghost11 = Toolkit.getDefaultToolkit().getImage("img/ghost11.jpg");
	Image ghost21 = Toolkit.getDefaultToolkit().getImage("img/ghost21.jpg");
	Image ghost31 = Toolkit.getDefaultToolkit().getImage("img/ghost31.jpg");
	Image ghost41 = Toolkit.getDefaultToolkit().getImage("img/ghost41.jpg");
	Image titleScreenImage = Toolkit.getDefaultToolkit().getImage("img/titleScreen.jpg");
	Image gameOverImage = Toolkit.getDefaultToolkit().getImage("img/gameOver.jpg");
	Image winScreenImage = Toolkit.getDefaultToolkit().getImage("img/winScreen.jpg");
	/* Initialize the player and ghosts */
	Player player = new Player(200, 300);
	Ghost ghost1 = new Ghost(180, 180);
	Ghost ghost2 = new Ghost(200, 180);
	Ghost ghost3 = new Ghost(220, 180);
	Ghost ghost4 = new Ghost(220, 180);
	long timer = System.currentTimeMillis(); //Timer is used for playing sound effects and animations
	/* Dying is used to count frames in the dying animation.  If it's non-zero,
	   pacman is in the process of dying */
	int dying = 0;
	/* Score information */
	int currScore;
	int highScore;
	boolean clearHighScores = false; //if the high scores have been cleared, we have to update the top of the screen to reflect that
	int numLives = 2;
	boolean[][] state; //Contains the game map, passed to player and ghosts
	boolean[][] pellets; //Contains the state of all pellets
	/* State flags*/
	boolean stopped;
	boolean titleScreen;
	boolean winScreen = false;
	boolean overScreen = false;
	boolean demo = false;
	int New;
	GameSounds sounds; //Used to call sound effects
	int lastPelletEatenX = 0;
	int lastPelletEatenY = 0;
	Font font = new Font("Monospaced", Font.BOLD, 12); //This is the font used for the menus
	
	public Board() { //Constructor initializes state flags etc.
		initHighScores();
		sounds = new GameSounds();
		currScore = 0;
		stopped = false;
		New = 0;
		titleScreen = true;
	}
	
	public void initHighScores() { //Reads the high scores file and saves it
		try {
			File file = new File("highScores.txt");
			Scanner sc = new Scanner(file);
			highScore = sc.nextInt();
			sc.close();
		} catch (Exception ignored) {}
	}
	
	public void updateScore(int score) { //Writes the new high score to a file and sets flag to update it on screen
		try {
			PrintWriter out = new PrintWriter("highScores.txt");
			out.println(score);
			out.close();
		} catch (Exception ignored) {}
		highScore = score;
		clearHighScores = true;
	}
	
	public void clearHighScores() { //Wipes the high scores file and sets flag to update it on screen
		try {
			PrintWriter out = new PrintWriter("highScores.txt");
			out.println("0");
			out.close();
		} catch (Exception ignored) {}
		highScore = 0;
		clearHighScores = true;
	}
	
	public void reset() { //Reset occurs on a new game
		numLives = 2;
		state = new boolean[Pacman.BOARD_SIZE][Pacman.BOARD_SIZE];
		pellets = new boolean[Pacman.BOARD_SIZE][Pacman.BOARD_SIZE];
		for (int i = 0; i<Pacman.BOARD_SIZE; i++) { //Clear state and pellets arrays
			for (int j = 0; j<Pacman.BOARD_SIZE; j++) {
				state[i][j] = true;
				pellets[i][j] = true;
			}
		}
		/* Handle the weird spots with no pellets*/
		for (int i = 5; i<14; i++) {
			for (int j = 5; j<12; j++) {
				pellets[i][j] = false;
			}
		}
		pellets[9][7] = false;
		pellets[8][8] = false;
		pellets[9][8] = false;
		pellets[10][8] = false;
	}
	
	/* Function is called during drawing of the map.
	   Whenever a portion of the map is covered up with a barrier,
	   the map and pellets arrays are updated accordingly to note
	   that those are invalid locations to travel or put pellets
	*/
	public void updateMap(int x, int y, int width, int height) {
		for (int i = x/Pacman.GRID_SIZE; i<x/Pacman.GRID_SIZE+width/Pacman.GRID_SIZE; i++) {
			for (int j = y/Pacman.GRID_SIZE; j<y/Pacman.GRID_SIZE+height/Pacman.GRID_SIZE; j++) {
				state[i-1][j-1] = false;
				pellets[i-1][j-1] = false;
			}
		}
	}
	
	/* Draws the appropriate number of lives on the bottom left of the screen.
	   Also draws the menu */
	public void drawLives(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, Pacman.MAX+5, 600, Pacman.GRID_SIZE); //Clear the bottom bar
		g.setColor(Color.YELLOW);
		for (int i = 0; i<numLives; i++) { //Draw each life
			g.fillOval(Pacman.GRID_SIZE*(i+1), Pacman.MAX+5, Pacman.GRID_SIZE, Pacman.GRID_SIZE);
		}
		/* Draw the menu items */
		g.setColor(Color.YELLOW);
		g.setFont(font);
		g.drawString("Reset", 100, Pacman.MAX+5+Pacman.GRID_SIZE);
		g.drawString("Clear High Scores", 180, Pacman.MAX+5+Pacman.GRID_SIZE);
		g.drawString("Exit", 350, Pacman.MAX+5+Pacman.GRID_SIZE);
	}
	
	/*  This function draws the board.  The pacman board is really complicated and can only feasibly be done
		manually.  Whenever I draw a wall, I call updateMap to invalidate those coordinates.  This way the pacman
		and ghosts know that they can't traverse this area */
	public void drawBoard(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 600, 600);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 420, 420);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 20, 600);
		g.fillRect(0, 0, 600, 20);
		g.setColor(Color.WHITE);
		g.drawRect(19, 19, 382, 382);
		g.setColor(Color.BLUE);
		
		g.fillRect(40, 40, 60, 20);
		updateMap(40, 40, 60, 20);
		g.fillRect(120, 40, 60, 20);
		updateMap(120, 40, 60, 20);
		g.fillRect(200, 20, 20, 40);
		updateMap(200, 20, 20, 40);
		g.fillRect(240, 40, 60, 20);
		updateMap(240, 40, 60, 20);
		g.fillRect(320, 40, 60, 20);
		updateMap(320, 40, 60, 20);
		g.fillRect(40, 80, 60, 20);
		updateMap(40, 80, 60, 20);
		g.fillRect(160, 80, 100, 20);
		updateMap(160, 80, 100, 20);
		g.fillRect(200, 80, 20, 60);
		updateMap(200, 80, 20, 60);
		g.fillRect(320, 80, 60, 20);
		updateMap(320, 80, 60, 20);
		
		g.fillRect(20, 120, 80, 60);
		updateMap(20, 120, 80, 60);
		g.fillRect(320, 120, 80, 60);
		updateMap(320, 120, 80, 60);
		g.fillRect(20, 200, 80, 60);
		updateMap(20, 200, 80, 60);
		g.fillRect(320, 200, 80, 60);
		updateMap(320, 200, 80, 60);
		
		g.fillRect(160, 160, 40, 20);
		updateMap(160, 160, 40, 20);
		g.fillRect(220, 160, 40, 20);
		updateMap(220, 160, 40, 20);
		g.fillRect(160, 180, 20, 20);
		updateMap(160, 180, 20, 20);
		g.fillRect(160, 200, 100, 20);
		updateMap(160, 200, 100, 20);
		g.fillRect(240, 180, 20, 20);
		updateMap(240, 180, 20, 20);
		g.setColor(Color.BLUE);
		
		
		g.fillRect(120, 120, 60, 20);
		updateMap(120, 120, 60, 20);
		g.fillRect(120, 80, 20, 100);
		updateMap(120, 80, 20, 100);
		g.fillRect(280, 80, 20, 100);
		updateMap(280, 80, 20, 100);
		g.fillRect(240, 120, 60, 20);
		updateMap(240, 120, 60, 20);
		
		g.fillRect(280, 200, 20, 60);
		updateMap(280, 200, 20, 60);
		g.fillRect(120, 200, 20, 60);
		updateMap(120, 200, 20, 60);
		g.fillRect(160, 240, 100, 20);
		updateMap(160, 240, 100, 20);
		g.fillRect(200, 260, 20, 40);
		updateMap(200, 260, 20, 40);
		
		g.fillRect(120, 280, 60, 20);
		updateMap(120, 280, 60, 20);
		g.fillRect(240, 280, 60, 20);
		updateMap(240, 280, 60, 20);
		
		g.fillRect(40, 280, 60, 20);
		updateMap(40, 280, 60, 20);
		g.fillRect(80, 280, 20, 60);
		updateMap(80, 280, 20, 60);
		g.fillRect(320, 280, 60, 20);
		updateMap(320, 280, 60, 20);
		g.fillRect(320, 280, 20, 60);
		updateMap(320, 280, 20, 60);
		
		g.fillRect(20, 320, 40, 20);
		updateMap(20, 320, 40, 20);
		g.fillRect(360, 320, 40, 20);
		updateMap(360, 320, 40, 20);
		g.fillRect(160, 320, 100, 20);
		updateMap(160, 320, 100, 20);
		g.fillRect(200, 320, 20, 60);
		updateMap(200, 320, 20, 60);
		
		g.fillRect(40, 360, 140, 20);
		updateMap(40, 360, 140, 20);
		g.fillRect(240, 360, 140, 20);
		updateMap(240, 360, 140, 20);
		g.fillRect(280, 320, 20, 40);
		updateMap(280, 320, 20, 60);
		g.fillRect(120, 320, 20, 60);
		updateMap(120, 320, 20, 60);
		drawLives(g);
	}
	
	public void drawPellets(Graphics g) { //Draws the pellets on the screen
		g.setColor(Color.YELLOW);
		for (int i = 1; i<Pacman.BOARD_SIZE; i++) {
			for (int j = 1; j<Pacman.BOARD_SIZE; j++) {
				if (pellets[i-1][j-1]) g.fillOval(i*20+8, j*20+8, 4, 4);
			}
		}
	}
	
	public static void fillPellet(int x, int y, Graphics g) { //Draws one individual pellet.  Used to redraw pellets that ghosts have run over
		g.setColor(Color.YELLOW);
		g.fillOval(x*20+28, y*20+28, 4, 4);
	}
	
	@Override
	public void paint(Graphics g) { //This is the main function that draws one entire frame of the game
		if (dying>0) { //If we're playing the dying animation, don't update the entire screen. Just kill the pacman
			sounds.nomNomStop(); //Stop any pacman eating sounds
			g.drawImage(pacmanImage, player.x, player.y, Color.BLACK, null); //Draw the pacman
			g.setColor(Color.BLACK);
			/* Kill the pacman */
			if (dying==4) g.fillRect(player.x, player.y, 20, 7);
			else if (dying==3) g.fillRect(player.x, player.y, 20, 14);
			else if (dying==2) g.fillRect(player.x, player.y, 20, 20);
			else if (dying==1) g.fillRect(player.x, player.y, 20, 20);
			/* Take .1 seconds on each frame of death, and then take 2 seconds
			   for the final frame to allow for the sound effect to end */
			long currTime = System.currentTimeMillis();
			long temp;
			if (dying!=1) temp = 100;
			else temp = 2000;
			/*  */
			if (currTime-timer>=temp) { //If it's time to draw a new death frame...
				dying--;
				timer = currTime;
				if (dying==0) { //If this was the last death frame...
					if (numLives==-1) {
						if (demo) numLives = 2; //Demo mode has infinite lives, just give it more lives
						else { //Game over for player.  If relevant, update high score.  Set gameOver flag
							if (currScore>highScore) updateScore(currScore);
							overScreen = true;
						}
					}
				}
			}
			return;
		}
		if (titleScreen) { //If this is the title screen, draw the title screen and return
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 600, 600);
			g.drawImage(titleScreenImage, 0, 0, Color.BLACK, null);
			sounds.nomNomStop(); //Stop any pacman eating sounds
			New = 1;
			return;
		}
		if (winScreen) { //If this is the win screen, draw the win screen and return
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 600, 600);
			g.drawImage(winScreenImage, 0, 0, Color.BLACK, null);
			New = 1;
			sounds.nomNomStop(); //Stop any pacman eating sounds
			return;
		}
		if (overScreen) { //If this is the game over screen, draw the game over screen and return
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 600, 600);
			g.drawImage(gameOverImage, 0, 0, Color.BLACK, null);
			New = 1;
			sounds.nomNomStop(); //Stop any pacman eating sounds
			return;
		}
		if (clearHighScores) { //If we need to update the high scores, redraw the top menu bar
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 600, 18);
			g.setColor(Color.YELLOW);
			g.setFont(font);
			clearHighScores = false;
			if (demo) g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore, 20, 10);
			else g.drawString("Score: "+currScore+"\t High Score: "+highScore, 20, 10);
		}
		if (New==1) { //Game initialization
			reset();
			player = new Player(200, 300);
			ghost1 = new Ghost(180, 180);
			ghost2 = new Ghost(200, 180);
			ghost3 = new Ghost(220, 180);
			ghost4 = new Ghost(220, 180);
			currScore = 0;
			drawBoard(g);
			drawPellets(g);
			drawLives(g);
			player.updateState(state); //Send the game map to player and all ghosts
			player.state[9][7] = false; //Don't let the player go in the ghost box
			ghost1.updateState(state);
			ghost2.updateState(state);
			ghost3.updateState(state);
			ghost4.updateState(state);
			/* Draw the top menu bar*/
			g.setColor(Color.YELLOW);
			g.setFont(font);
			if (demo) g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore, 20, 10);
			else g.drawString("Score: "+currScore+"\t High Score: "+highScore, 20, 10);
			New++;
		} else if (New==2) New++; //Second frame of new game
		else if (New==3) { //Third frame of new game
			New++;
			sounds.newGame(); //Play the newGame sound effect
			timer = System.currentTimeMillis();
			return;
		} else if (New==4) { //Fourth frame of new game
			/* Stay in this state until the sound effect is over */
			long currTime = System.currentTimeMillis();
			if (currTime-timer>=5000) New = 0;
			else return;
		}
		/* Drawing optimization */
		g.copyArea(player.x-20, player.y-20, 80, 80, 0, 0);
		g.copyArea(ghost1.x-20, ghost1.y-20, 80, 80, 0, 0);
		g.copyArea(ghost2.x-20, ghost2.y-20, 80, 80, 0, 0);
		g.copyArea(ghost3.x-20, ghost3.y-20, 80, 80, 0, 0);
		g.copyArea(ghost4.x-20, ghost4.y-20, 80, 80, 0, 0);
		/* Detect collisions */
		boolean oops = false; //oops is set to true when pacman has lost a life
		if (player.x==ghost1.x && Math.abs(player.y-ghost1.y)<10) oops = true;
		else if (player.x==ghost2.x && Math.abs(player.y-ghost2.y)<10) oops = true;
		else if (player.x==ghost3.x && Math.abs(player.y-ghost3.y)<10) oops = true;
		else if (player.x==ghost4.x && Math.abs(player.y-ghost4.y)<10) oops = true;
		else if (player.y==ghost1.y && Math.abs(player.x-ghost1.x)<10) oops = true;
		else if (player.y==ghost2.y && Math.abs(player.x-ghost2.x)<10) oops = true;
		else if (player.y==ghost3.y && Math.abs(player.x-ghost3.x)<10) oops = true;
		else if (player.y==ghost4.y && Math.abs(player.x-ghost4.x)<10) oops = true;
		if (oops && !stopped) { //Kill the pacman
			dying = 4; //4 frames of death
			sounds.death(); //Play death sound effect
			sounds.nomNomStop(); //Stop any pacman eating sounds
			/*Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
			numLives--;
			stopped = true;
			drawLives(g);
			timer = System.currentTimeMillis();
		}
		/* Delete the players and ghosts */
		g.setColor(Color.BLACK);
		g.fillRect(player.lastX, player.lastY, 20, 20);
		g.fillRect(ghost1.lastX, ghost1.lastY, 20, 20);
		g.fillRect(ghost2.lastX, ghost2.lastY, 20, 20);
		g.fillRect(ghost3.lastX, ghost3.lastY, 20, 20);
		g.fillRect(ghost4.lastX, ghost4.lastY, 20, 20);
		/* Eat pellets */
		if (pellets[player.pelletX][player.pelletY] && New!=2 && New!=3) {
			lastPelletEatenX = player.pelletX;
			lastPelletEatenY = player.pelletY;
			sounds.nomNom(); //Play eating sound
			player.pelletsEaten++; //Increment pellets eaten value to track for end game
			pellets[player.pelletX][player.pelletY] = false; //Delete the pellet
			currScore += 50; //Increment the score
			/* Update the screen to reflect the new score */
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 600, 20);
			g.setColor(Color.YELLOW);
			g.setFont(font);
			if (demo) g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore, 20, 10);
			else g.drawString("Score: "+currScore+"\t High Score: "+highScore, 20, 10);
			if (player.pelletsEaten==173) { //If this was the last pellet
				if (!demo) { //Demo mode can't get a high score
					if (currScore>highScore) updateScore(currScore);
					winScreen = true;
				} else titleScreen = true;
				return;
			}
		}
		/* If we moved to a location without pellets, stop the sounds */
		else if (player.pelletX!=lastPelletEatenX || player.pelletY!=lastPelletEatenY || player.stopped)
			sounds.nomNomStop(); //Stop any pacman eating sounds
		/* Replace pellets that have been run over by ghosts */
		if (pellets[ghost1.lastPelletX][ghost1.lastPelletY]) fillPellet(ghost1.lastPelletX, ghost1.lastPelletY, g);
		if (pellets[ghost2.lastPelletX][ghost2.lastPelletY]) fillPellet(ghost2.lastPelletX, ghost2.lastPelletY, g);
		if (pellets[ghost3.lastPelletX][ghost3.lastPelletY]) fillPellet(ghost3.lastPelletX, ghost3.lastPelletY, g);
		if (pellets[ghost4.lastPelletX][ghost4.lastPelletY]) fillPellet(ghost4.lastPelletX, ghost4.lastPelletY, g);
		/*Draw the ghosts */
		if (ghost1.frameCount<5) { //Draw first frame of ghosts
			g.drawImage(ghost10, ghost1.x, ghost1.y, Color.BLACK, null);
			g.drawImage(ghost20, ghost2.x, ghost2.y, Color.BLACK, null);
			g.drawImage(ghost30, ghost3.x, ghost3.y, Color.BLACK, null);
			g.drawImage(ghost40, ghost4.x, ghost4.y, Color.BLACK, null);
			ghost1.frameCount++;
		} else { //Draw second frame of ghosts
			g.drawImage(ghost11, ghost1.x, ghost1.y, Color.BLACK, null);
			g.drawImage(ghost21, ghost2.x, ghost2.y, Color.BLACK, null);
			g.drawImage(ghost31, ghost3.x, ghost3.y, Color.BLACK, null);
			g.drawImage(ghost41, ghost4.x, ghost4.y, Color.BLACK, null);
			if (ghost1.frameCount>=10) ghost1.frameCount = 0;
			else ghost1.frameCount++;
		}
		/* Draw the pacman */
		if (player.frameCount<5) { //Draw mouth closed
			g.drawImage(pacmanImage, player.x, player.y, Color.BLACK, null);
		} else { //Draw mouth open in appropriate direction
			if (player.frameCount>=10) player.frameCount = 0;
			switch (player.currDirection) {
				case 'L':
					g.drawImage(pacmanLeftImage, player.x, player.y, Color.BLACK, null);
					break;
				case 'R':
					g.drawImage(pacmanRightImage, player.x, player.y, Color.BLACK, null);
					break;
				case 'U':
					g.drawImage(pacmanUpImage, player.x, player.y, Color.BLACK, null);
					break;
				case 'D':
					g.drawImage(pacmanDownImage, player.x, player.y, Color.BLACK, null);
					break;
			}
		}
		/* Draw the border around the game in case it was overwritten by ghost movement or something */
		g.setColor(Color.WHITE);
		g.drawRect(19, 19, 382, 382);
	}
}
