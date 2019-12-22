
public class Move {
	public Position src, dest;
	public Type promotion;
	
	public Move(Position src, Position dest, Type promotion) {
		super();
		this.src = src;
		this.dest = dest;
		this.promotion = promotion;
	}

	@Override
	public String toString() {
		return src + ", " + dest;
	}
	
	
}
