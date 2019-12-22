import java.util.ArrayList;
import java.util.Random;

public class Agent {
	private Chess chess;
	public String method;
	private Random myRandom;
	
	public Agent(Chess chess, String method) {
		super();
		this.chess = chess;
		this.method = method;
		myRandom = new Random();
	}
	
	public void move()
	{
		switch(method)
		{
			case "random":
				if(!chess.gameOver())
					chess.move(random(), true);
				break;
		//	case "minimax":
		//		return miniMaxRoot();
			default:
				if(!chess.gameOver())
					chess.move(random(), true);
				break;
		}
	}
	private Move random()
	{
		ArrayList<Move> moves = chess.getAvailableMoves();
		if(moves.size() > 0)
		{
			int i = myRandom.nextInt(moves.size());
			return moves.get(i);
		}
	
		return null;
	}
}
