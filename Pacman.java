import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.Timer;

/* This class contains the entire game... most of the game logic is in the Board class but this
   creates the gui and captures mouse and keyboard input, as well as controls the game states */
public class Pacman extends JApplet {
	
	public static final int BOARD_SIZE = 20; //BOARD_SIZE is the size of the game in squares
	public static final int GRID_SIZE = 20; //GRID_SIZE is the size of one square in the game.
	public static final int MAX = BOARD_SIZE*GRID_SIZE; //MAX is the height/width of the game.
	public static final int INCREMENT = 4; //INCREMENT is the speed at which the object moves, 1 INCREMENT per move() call
	
	private final Board b = new Board(); //Create a new board
	private Timer frameTimer; //This timer is used to do request new frames be drawn
	
	public Pacman() { //This constructor creates the entire game essentially
		b.requestFocus();
		JFrame f = new JFrame(); //Create and set up window frame
		f.setSize(420, 460);
		f.add(b, BorderLayout.CENTER); //Add the board to the frame
		b.addMouseListener(new MouseAdapter() { //Set listeners for mouse actions and button clicks
			@Override
			public void mousePressed(MouseEvent e) { //This function detects user clicks on the menu items at the bottom of the screen
				b.handleMouseClick(e.getX(), e.getY());
			}
		});
		b.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) { //Handles user key presses
				b.handleKeyboardKey(e.getKeyCode());
			}
		});
		f.setVisible(true); //Make frame visible
		f.setResizable(false); //Disable resizing
		frameTimer = new Timer(30, e -> b.stepFrame(false, frameTimer)); //Create a timer that calls stepFrame every 30 milliseconds
		b.stepFrame(true, frameTimer); //Manually call the first frameStep to initialize the game.
		frameTimer.start(); //Start the timer
		b.requestFocus();
	}
	
	public static void main(String[] args) { //Main function simply creates a new pacman instance
		new Pacman();
	}
}
