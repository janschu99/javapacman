import java.awt.Image;
import java.awt.Toolkit;

public class GameImages {
	
	/* Initialize the images*/
	private final Image pacmanImage = Toolkit.getDefaultToolkit().getImage("img/pacman.jpg");
	private final Image pacmanUpImage = Toolkit.getDefaultToolkit().getImage("img/pacmanup.jpg");
	private final Image pacmanDownImage = Toolkit.getDefaultToolkit().getImage("img/pacmandown.jpg");
	private final Image pacmanLeftImage = Toolkit.getDefaultToolkit().getImage("img/pacmanleft.jpg");
	private final Image pacmanRightImage = Toolkit.getDefaultToolkit().getImage("img/pacmanright.jpg");
	private final Image ghost10 = Toolkit.getDefaultToolkit().getImage("img/ghost10.jpg");
	private final Image ghost20 = Toolkit.getDefaultToolkit().getImage("img/ghost20.jpg");
	private final Image ghost30 = Toolkit.getDefaultToolkit().getImage("img/ghost30.jpg");
	private final Image ghost40 = Toolkit.getDefaultToolkit().getImage("img/ghost40.jpg");
	private final Image ghost11 = Toolkit.getDefaultToolkit().getImage("img/ghost11.jpg");
	private final Image ghost21 = Toolkit.getDefaultToolkit().getImage("img/ghost21.jpg");
	private final Image ghost31 = Toolkit.getDefaultToolkit().getImage("img/ghost31.jpg");
	private final Image ghost41 = Toolkit.getDefaultToolkit().getImage("img/ghost41.jpg");
	private final Image titleScreenImage = Toolkit.getDefaultToolkit().getImage("img/titleScreen.jpg");
	private final Image gameOverImage = Toolkit.getDefaultToolkit().getImage("img/gameOver.jpg");
	private final Image winScreenImage = Toolkit.getDefaultToolkit().getImage("img/winScreen.jpg");
	
	public Image getTitleScreenImage() {
		return titleScreenImage;
	}
	
	public Image getGameOverImage() {
		return gameOverImage;
	}
	
	public Image getWinScreenImage() {
		return winScreenImage;
	}
	
	public Image getPacmanImage() {
		return pacmanImage;
	}
	
	public Image getPacmanDirectionImage(Direction direction) throws IllegalArgumentException {
		switch (direction) {
			case LEFT:
				return pacmanLeftImage;
			case RIGHT:
				return pacmanRightImage;
			case UP:
				return pacmanUpImage;
			case DOWN:
				return pacmanDownImage;
		}
		throw new IllegalArgumentException("No pacman image for direction '"+direction+"'");
	}
	
	public Image getGhostImage(int ghostNum, boolean secondFrame) throws IllegalArgumentException {
		if (ghostNum==1) return secondFrame ? ghost11 : ghost10;
		if (ghostNum==2) return secondFrame ? ghost21 : ghost20;
		if (ghostNum==3) return secondFrame ? ghost31 : ghost30;
		if (ghostNum==4) return secondFrame ? ghost41 : ghost40;
		throw new IllegalArgumentException("No ghost image for ghost number "+ghostNum);
	}
}
