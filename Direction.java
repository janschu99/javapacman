public enum Direction {
	
	LEFT,
	RIGHT,
	UP,
	DOWN;
	
	static {
		LEFT.reverse = RIGHT;
		RIGHT.reverse = LEFT;
		UP.reverse = DOWN;
		DOWN.reverse = UP;
	}
	
	private Direction reverse;
	
	public Direction getReverse() {
		return reverse;
	}
}
