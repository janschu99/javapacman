import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.Timer;

/* This class contains the entire game... most of the game logic is in the Board class but this
   creates the gui and captures mouse and keyboard input, as well as controls the game states */
public class Pacman extends JApplet implements MouseListener, KeyListener {
	
	public static final int GRID_SIZE = 20; //GRID_SIZE is the size of one square in the game.
	public static final int MAX = 400; //MAX is the height/width of the game.
	public static final int INCREMENT = 4; //INCREMENT is the speed at which the object moves, 1 INCREMENT per move() call
	public static final int BOARD_SIZE = 20;
	
	Board b = new Board(); //Create a new board
	Timer frameTimer; //This timer is used to do request new frames be drawn
	
	public Pacman() { //This constructor creates the entire game essentially
		b.requestFocus();
		JFrame f = new JFrame(); //Create and set up window frame
		f.setSize(420, 460);
		f.add(b, BorderLayout.CENTER); //Add the board to the frame
		b.addMouseListener(this); //Set listeners for mouse actions and button clicks
		b.addKeyListener(this);
		f.setVisible(true); //Make frame visible
		f.setResizable(false); //Disable resizing
		frameTimer = new Timer(30, e -> b.stepFrame(false, frameTimer)); //Create a timer that calls stepFrame every 30 milliseconds
		b.stepFrame(true, frameTimer); //Manually call the first frameStep to initialize the game.
		frameTimer.start(); //Start the timer
		b.requestFocus();
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
