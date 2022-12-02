import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class ScoreManager {
	
	private int currentScore = 0;
	private int highScore;
	
	public ScoreManager() { //Reads the high scores file and saves it
		try {
			File file = new File("highScores.txt");
			Scanner sc = new Scanner(file);
			highScore = sc.nextInt();
			sc.close();
		} catch (Exception ignored) {}
	}
	
	public boolean checkHighScore() { //Writes the current score to a file if it's a new high score.
	                                  //Returns whether a new score has been written
		if (currentScore>highScore) {
			try {
				PrintWriter out = new PrintWriter("highScores.txt");
				out.println(currentScore);
				out.close();
			} catch (Exception ignored) {}
			highScore = currentScore;
			return true;
		}
		return false;
	}
	
	public void clearHighScores() { //Wipes the high scores file and sets flag to update it on screen
		try {
			PrintWriter out = new PrintWriter("highScores.txt");
			out.println("0");
			out.close();
		} catch (Exception ignored) {}
		highScore = 0;
	}
	
	public int getCurrentScore() {
		return currentScore;
	}
	
	public int getHighScore() {
		return highScore;
	}
	
	public void incrementCurrentScore(int increment) {
		currentScore += increment;
	}
	
	public void resetCurrentScore() {
		currentScore = 0;
	}
}
