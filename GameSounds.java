import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GameSounds { //This class controls all sound effects
	
	Clip nomNom;
	Clip newGame;
	Clip death;
	boolean stopped = true; //Keeps track of whether the eating sound is playing
	
	public GameSounds() { //Initialize audio files
		try {
			URL url = this.getClass().getClassLoader().getResource("sounds/nomnom.wav"); //Pacman eating sound
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			nomNom = AudioSystem.getClip();
			nomNom.open(audioIn);
			url = this.getClass().getClassLoader().getResource("sounds/newGame.wav"); //newGame
			audioIn = AudioSystem.getAudioInputStream(url);
			newGame = AudioSystem.getClip();
			newGame.open(audioIn);
			url = this.getClass().getClassLoader().getResource("sounds/death.wav"); //death
			audioIn = AudioSystem.getAudioInputStream(url);
			death = AudioSystem.getClip();
			death.open(audioIn);
		} catch (Exception ignored) {}
	}
	
	public void nomNom() { //Play pacman eating sound
		if (!stopped) return; //If it's already playing, don't start it playing again!
		stopped = false;
		nomNom.stop();
		nomNom.setFramePosition(0);
		nomNom.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void nomNomStop() { //Stop pacman eating sound
		stopped = true;
		nomNom.stop();
		nomNom.setFramePosition(0);
	}
	
	public void newGame() { //Play new game sound
		newGame.stop();
		newGame.setFramePosition(0);
		newGame.start();
	}
	
	public void death() { //Play pacman death sound
		death.stop();
		death.setFramePosition(0);
		death.start();
	}
}
