	enum Type
	{
		ROOK,
		BISHOP,
		KNIGHT,
		KING,
		QUEEN,
		PAWN
	} 
	enum Colour
	{
		BLACK,
		WHITE
	}
public class Piece {

	
	private Type type;
	private Colour colour;
	
	private int row, col;

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Piece(Type type, Colour colour, int row, int col) {
		super();
		this.setType(type);
		this.colour = colour;
		this.setRow(row < 8 ? row:7);
		this.setCol(col < 8 ? col:7);
	}

	@Override
	public String toString() {
		switch(type)
		{
			case BISHOP:
				if(colour == Colour.WHITE)
					return "B";
				return "b";
			case KING:
				if(colour == Colour.WHITE)
					return "K";
				return "k";
			case KNIGHT:
				if(colour == Colour.WHITE)
					return "N";
				return "n";
			case PAWN:
				if(colour == Colour.WHITE)
					return "P";
				return "p";
			case QUEEN:
				if(colour == Colour.WHITE)
					return "Q";
				return "q";
			case ROOK:
				if(colour == Colour.WHITE)
					return "R";
				return "r";
			default:
				return "?";
		}
	}

	public Colour getColour() {
		return colour;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	 
	
}
