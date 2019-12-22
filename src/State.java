public class State {
	public Piece taken; //the piece that was taken off last move
	public Piece prevPiece;// the last piece that moved
	public Position prevPos; // the original position of the last piece that moved 
	public boolean promotion; //whether or not the last move promoted a pawn
	public boolean madeUnCastleableK;
	public boolean madeUnCastleableQ;
	public boolean castled;
	
	public State()
	{
		taken = null;
		promotion = false;
	}
}


