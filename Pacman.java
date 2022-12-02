import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;
import javax.swing.JFrame;

/* This class contains the entire game... most of the game logic is in the Board class but this
   creates the gui and captures mouse and keyboard input, as well as controls the game states */
public class Pacman extends JApplet implements MouseListener, KeyListener {
	
	public static final int GRID_SIZE = 20; //GRID_SIZE is the size of one square in the game.
	public static final int MAX = 400; //MAX is the height/width of the game.
	public static final int INCREMENT = 4; //INCREMENT is the speed at which the object moves, 1 INCREMENT per move() call
	public static final int BOARD_SIZE = 20;
	
	/* These timers are used to kill title, game over, and victory screens after a set idle period (5 seconds)*/
	long titleTimer = -1;
	long timer = -1;
	Board b = new Board(); //Create a new board
	javax.swing.Timer frameTimer; //This timer is used to do request new frames be drawn
	
	public Pacman() { //This constructor creates the entire game essentially
		b.requestFocus();
		JFrame f = new JFrame(); //Create and set up window frame
		f.setSize(420, 460);
		f.add(b, BorderLayout.CENTER); //Add the board to the frame
		b.addMouseListener(this); //Set listeners for mouse actions and button clicks
		b.addKeyListener(this);
		f.setVisible(true); //Make frame visible
		f.setResizable(false); //Disable resizing
		b.New = 1; //Set the New flag to 1 because this is a new game
		stepFrame(true); //Manually call the first frameStep to initialize the game.
		frameTimer = new javax.swing.Timer(30, e -> stepFrame(false)); //Create a timer that calls stepFrame every 30 milliseconds
		frameTimer.start(); //Start the timer
		b.requestFocus();
	}
	
	/* This repaint function repaints only the parts of the screen that may have changed,
	   namely the area around every player ghost and the menu bars
	*/
	@Override
	public void repaint() {
		if (b.player.teleport) {
			b.repaint(b.player.lastX-20, b.player.lastY-20, 80, 80);
			b.player.teleport = false;
		}
		b.repaint(0, 0, 600, 20);
		b.repaint(0, 420, 600, 40);
		b.repaint(b.player.x-20, b.player.y-20, 80, 80);
		b.repaint(b.ghost1.x-20, b.ghost1.y-20, 80, 80);
		b.repaint(b.ghost2.x-20, b.ghost2.y-20, 80, 80);
		b.repaint(b.ghost3.x-20, b.ghost3.y-20, 80, 80);
		b.repaint(b.ghost4.x-20, b.ghost4.y-20, 80, 80);
	}
	
	public void stepFrame(boolean New) { //Steps the screen forward one frame
		if (!b.titleScreen && !b.winScreen && !b.overScreen) { //If we aren't on a special screen than the timers can be set to -1 to disable them
			timer = -1;
			titleTimer = -1;
		}
		if (b.dying>0) { //If we are playing the dying animation, keep advancing frames until the animation is complete
			b.repaint();
			return;
		}
		/* New can either be specified by the New parameter in stepFrame function call or by the state
		   of b.New.  Update New accordingly */
		New = New || b.New!=0;
		/* If this is the title screen, make sure to only stay on the title screen for 5 seconds.
		   If after 5 seconds the user hasn't started a game, start up demo mode */
		if (b.titleScreen) {
			if (titleTimer==-1) titleTimer = System.currentTimeMillis();
			long currTime = System.currentTimeMillis();
			if (currTime-titleTimer>=5000) {
				b.titleScreen = false;
				b.demo = true;
				titleTimer = -1;
			}
			b.repaint();
			return;
		}
		/* If this is the win screen or game over screen, make sure to only stay on the screen for 5 seconds.
		   If after 5 seconds the user hasn't pressed a key, go to title screen */
		if (b.winScreen || b.overScreen) {
			if (timer==-1) timer = System.currentTimeMillis();
			long currTime = System.currentTimeMillis();
			if (currTime-timer>=5000) {
				b.winScreen = false;
				b.overScreen = false;
				b.titleScreen = true;
				timer = -1;
			}
			b.repaint();
			return;
		}
		if (!New) { //If we have a normal game state, move all pieces and update pellet status
			/* The pacman player has two functions, demoMove if we're in demo mode and move if we're in
			   user playable mode.  Call the appropriate one here */
			if (b.demo) b.player.demoMove();
			else b.player.move();
			/* Also move the ghosts, and update the pellet states */
			b.ghost1.move();
			b.ghost2.move();
			b.ghost3.move();
			b.ghost4.move();
			b.player.updatePellet();
			b.ghost1.updatePellet();
			b.ghost2.updatePellet();
			b.ghost3.updatePellet();
			b.ghost4.updatePellet();
		}
		if (b.stopped || New) { //We either have a new game or the user has died, either way we have to reset the board
			frameTimer.stop(); //Temporarily stop advancing frames
			while (b.dying>0) //If user is dying ...
				stepFrame(false); //Play dying animation.
			/* Move all game elements back to starting positions and orientations */
			b.player.currDirection = 'L';
			b.player.direction = 'L';
			b.player.desiredDirection = 'L';
			b.player.x = 200;
			b.player.y = 300;
			b.ghost1.x = 180;
			b.ghost1.y = 180;
			b.ghost2.x = 200;
			b.ghost2.y = 180;
			b.ghost3.x = 220;
			b.ghost3.y = 180;
			b.ghost4.x = 220;
			b.ghost4.y = 180;
			b.repaint(0, 0, 600, 600); //Advance a frame to display main state
			/*Start advancing frames once again*/
			b.stopped = false;
			frameTimer.start();
		} else repaint(); //Otherwise we're in a normal state, advance one frame
	}
	
	@Override
	public void keyPressed(KeyEvent e) { //Handles user key presses
		if (b.titleScreen) { //Pressing a key in the title screen starts a game
			b.titleScreen = false;
			return;
		}
		if (b.winScreen || b.overScreen) { //Pressing a key in the win screen or game over screen goes to the title screen
			b.titleScreen = true;
			b.winScreen = false;
			b.overScreen = false;
			return;
		}
		if (b.demo) { //Pressing a key during a demo kills the demo mode and starts a new game
			b.demo = false;
			b.sounds.nomNomStop(); //Stop any pacman eating sounds
			b.New = 1;
			return;
		}
		switch (e.getKeyCode()) { //Otherwise, key presses control the player!
			case KeyEvent.VK_LEFT:
				b.player.desiredDirection = 'L';
				break;
			case KeyEvent.VK_RIGHT:
				b.player.desiredDirection = 'R';
				break;
			case KeyEvent.VK_UP:
				b.player.desiredDirection = 'U';
				break;
			case KeyEvent.VK_DOWN:
				b.player.desiredDirection = 'D';
				break;
		}
		repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) { //This function detects user clicks on the menu items at the bottom of the screen
		if (b.titleScreen || b.winScreen || b.overScreen) //If we aren't in the game where a menu is showing, ignore clicks
			return;
		/* Get coordinates of click */
		int x = e.getX();
		int y = e.getY();
		if (400<=y && y<=460) {
			if (100<=x && x<=150) b.New = 1; //New game has been clicked
			else if (180<=x && x<=300) { //Clear high scores has been clicked
				b.scoreManager.clearHighScores();
				b.clearHighScores = true;
			}
			else if (350<=x && x<=420) System.exit(0); //Exit has been clicked
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) { //Main function simply creates a new pacman instance
		new Pacman();
	}
}
