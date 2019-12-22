import java.util.ArrayList;
import java.util.Stack;

public class Chess {
	ArrayList<Piece> pieces;
	private Colour currPlayer;
	private boolean inCheck;
	
	private boolean whiteCanCastleK; // white can castle kings side
	private boolean blackCanCastleQ; // black can castle queens side
	private boolean whiteCanCastleQ; // white can castle queens side
	private boolean blackCanCastleK; // black can castle kings side
	private Stack<State> moves;

	private Piece kingB;
	private Piece kingW;
	
	public Colour getCurrPlayer() {
		synchronized(this)
		{	
			return currPlayer;
		}
	}

	public Stack<State> getMoves() {
		return moves;
	}
	
	public ArrayList<Piece> getPieces()
	{
		return pieces;
	}
	public Chess(ArrayList<Piece> pieces)
	{
		currPlayer = Colour.WHITE;
		this.pieces = pieces;
		moves = new Stack<>();
	}
	
	
	public Chess()
	{
		currPlayer = Colour.WHITE;
		pieces = new ArrayList<>();
		moves = new Stack<>();
		this.whiteCanCastleK = true;
		this.whiteCanCastleQ = true;
		this.blackCanCastleK = true;
		this.blackCanCastleQ = true;
		//create a board with the standard setup
		//White Castles
		pieces.add(new Piece(Type.ROOK, Colour.WHITE, 0, 0));
		pieces.add(new Piece(Type.ROOK, Colour.WHITE, 0, 7));
		//Black Castles
		pieces.add(new Piece(Type.ROOK, Colour.BLACK, 7, 0));
		pieces.add(new Piece(Type.ROOK, Colour.BLACK, 7, 7));
		//White Knights
		pieces.add(new Piece(Type.KNIGHT, Colour.WHITE, 0, 1));
		pieces.add(new Piece(Type.KNIGHT, Colour.WHITE, 0, 6));
		//Black Knights
		pieces.add(new Piece(Type.KNIGHT, Colour.BLACK, 7, 1));
		pieces.add(new Piece(Type.KNIGHT, Colour.BLACK, 7, 6));
		//White Bishops
		pieces.add(new Piece(Type.BISHOP, Colour.WHITE, 0, 2));
		pieces.add(new Piece(Type.BISHOP, Colour.WHITE, 0, 5));
		//Black Bishops
		pieces.add(new Piece(Type.BISHOP, Colour.BLACK, 7, 2));
		pieces.add(new Piece(Type.BISHOP, Colour.BLACK, 7, 5));
		//White Queen
		pieces.add(new Piece(Type.QUEEN, Colour.WHITE, 0, 4));
		//Black Queen
		pieces.add(new Piece(Type.QUEEN, Colour.BLACK, 7, 4));
		//White King
		pieces.add(new Piece(Type.KING, Colour.WHITE, 0, 3));
		kingW = pieces.get(pieces.size() -1);
		//Black King
		pieces.add(new Piece(Type.KING, Colour.BLACK, 7, 3));
		kingB = pieces.get(pieces.size() -1);
		//White Pawns
		for(int i = 0; i < 8; i++)
			pieces.add(new Piece(Type.PAWN, Colour.WHITE, 1, i));
		//Black Pawns
		for(int i = 0; i < 8; i++)
			pieces.add(new Piece(Type.PAWN, Colour.BLACK, 6, i));
 
	}
	
	public void undo()
	{	 
		undo(true);
	}
	private void undo(boolean changeColour)
	{
		if(moves.size()>0)
		 {
		 	State currMove = moves.peek();
		 	if(currMove.taken != null)
		 		pieces.add(currMove.taken);
		 	if(currMove.castled)
			{
				if(currMove.prevPiece.getCol() == 1)
					getPiece(currMove.prevPos.getRow(), 2).setCol(0);
				else
					getPiece(currMove.prevPos.getRow(), 4).setCol(7);
				
			}
		 	currMove.prevPiece.setRow(currMove.prevPos.getRow());
			currMove.prevPiece.setCol(currMove.prevPos.getCol());
			if(currMove.promotion)
			{
				currMove.prevPiece.setType(Type.PAWN) ;
			}
			//handle castling
			
			if(currMove.madeUnCastleableK)
			{
				if(currPlayer == Colour.WHITE)
					blackCanCastleK = true;
				else
					whiteCanCastleK = true;
			}
			if(currMove.madeUnCastleableQ)
			{
				if(currPlayer == Colour.WHITE)
					blackCanCastleQ = true;
				else
					whiteCanCastleQ = true;
			}
			if(changeColour)
				currPlayer = currPlayer == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
			moves.pop(); 
		 }
	}
	
	private void makeMove(Position src, Position dest)
	{
		//promotion
		if(getPiece(src).getType() == Type.PAWN && ((getPiece(src).getColour() == Colour.BLACK && src.getRow() == 1 && dest.getRow() == 0) || (getPiece(src).getColour() == Colour.WHITE && src.getRow() == 6 && dest.getRow() == 7)))
		{
			makeMove(src, dest, Type.QUEEN);
			return;
		}
		Piece destPiece = getPiece(dest);
		Piece srcPiece = getPiece(src);
		State myState = new State();
		myState.prevPiece = srcPiece;
		myState.prevPos = src;
		//en passant
		if(srcPiece.getType() == Type.PAWN && src.getCol() != dest.getCol() && destPiece == null)
		{
			srcPiece.setRow(dest.getRow());
			srcPiece.setCol(dest.getCol());
			
			Piece takenPiece = currPlayer == Colour.WHITE ? getPiece(4, dest.getCol()) : getPiece(3, dest.getCol()) ;
					
			pieces.remove(takenPiece);
			myState.taken = takenPiece;
			moves.add(myState);
			return;
		}
			
		if(destPiece != null)//take Piece
		{
			srcPiece.setRow(dest.getRow());
			srcPiece.setCol(dest.getCol());
			pieces.remove(destPiece);
			myState.taken = destPiece;
						
		}
		else
		{
			//check for castling
			if(srcPiece.getType() == Type.KING && Math.abs(src.getCol() - dest.getCol()) == 2)
			{
				myState.castled = true;
				//move the castle
				if(dest.getCol() == 1)//king side
				{
					if(srcPiece.getColour() == Colour.WHITE)
				 	{
						Piece castle = getPiece(0,0);
						castle.setCol(2);
					}
					else
					{
						Piece castle = getPiece(7,0);
						castle.setCol(2);
					}
				}
				else//queens side
				{
					if(srcPiece.getColour() == Colour.WHITE)
					{
						Piece castle = getPiece(0,7);
						castle.setCol(4);
					}
					else
					{
						Piece castle = getPiece(7,7);
						castle.setCol(4);
					}
				}
				if(srcPiece.getColour() == Colour.WHITE)
				{
					whiteCanCastleK = false;
					whiteCanCastleQ = false;
					myState.madeUnCastleableK = true;
					myState.madeUnCastleableQ = true;
				}
				else
				{
					blackCanCastleK = false;
					blackCanCastleQ = false;
					myState.madeUnCastleableK = true;
					myState.madeUnCastleableQ = true;
				}
			}
			
			srcPiece.setRow(dest.getRow());
			srcPiece.setCol(dest.getCol());
		}
			
		
		
		
		

		//handle castling
		if(srcPiece.getType() == Type.KING || srcPiece.getType() == Type.ROOK)
		{
			if(srcPiece.getColour() == Colour.WHITE)
			{
				if(srcPiece.getType() == Type.KING)
				{
					if(whiteCanCastleK)
					{
						myState.madeUnCastleableK = true;
						whiteCanCastleK = false;
					}
					if(whiteCanCastleQ)
					{
						myState.madeUnCastleableQ = true;
						whiteCanCastleQ = false;
					}
				}
				else // must be a castle
				{
					if(whiteCanCastleK)
					{
						if(srcPiece.getCol() == 0)
						{
							myState.madeUnCastleableK = true;
							whiteCanCastleK = false;
						}
					}
					if(whiteCanCastleQ)
					{
						if(srcPiece.getCol() == 7)
						{
							myState.madeUnCastleableQ = true;
							whiteCanCastleQ = false;
						}
					}
				}
			}
			else//black
			{
				if(srcPiece.getType() == Type.KING)
				{
					if(blackCanCastleK)
					{
						myState.madeUnCastleableK = true;
						blackCanCastleK = false;
					}
					if(blackCanCastleQ)
					{
						myState.madeUnCastleableQ = true;
						blackCanCastleQ = false;
					}
				}
				else // must be a castle
				{
					if(blackCanCastleK)
					{
						if(srcPiece.getCol() == 0)
						{
							myState.madeUnCastleableK = true;
							blackCanCastleK = false;
						}
					}
					if(blackCanCastleQ)
					{
						if(srcPiece.getCol() == 7)
						{
							myState.madeUnCastleableQ = true;
							blackCanCastleQ = false;
						}
					}
				}
			}
		}
		
		moves.add(myState);
	}
	private void makeMove(Position src, Position dest, Type promotion)
	{
		Piece myPiece = getPiece(src);
		State myState = new State();
		if(src.getCol() != dest.getCol())//need to take a piece
		{
			Piece destPiece = getPiece(dest);
			pieces.remove(destPiece);
			myState.taken = destPiece;
			
		}
		myPiece.setRow(dest.getRow());
		myPiece.setCol(dest.getCol());
		myPiece.setType(promotion);
		
		myState.prevPiece = myPiece;
		myState.prevPos = src;
		myState.promotion = true;
		myState.madeUnCastleableK = false;
		myState.madeUnCastleableQ = false;
		
		moves.add(myState);
			
	}
		
		
	public boolean move(int r1, int c1, int r2, int c2, boolean changeColour)
	{
		return move(new Move(new Position(r1, c1), new Position(r2, c2), null), changeColour);
	}
	
	public boolean move(int r1, int c1, int r2, int c2, Type type, boolean changeColour)
	{
		return move(new Move(new Position(r1, c1), new Position(r2, c2), type), changeColour);
	}
	
	public boolean move(Move move, boolean changeColour)
	{
		if(move != null)
			return move(move.src, move.dest, move.promotion, changeColour);
		return false;
	}
	public boolean move(Position src, Position dest, Type promotion, boolean changeColour)
	{
		
		if((getPiece(src) == null || ((getPiece(src) != null) && getPiece(src).getColour() != currPlayer)))//we are moving the correnct colour piece
			return false;
		if(getPiece(dest) != null && getPiece(dest).getColour() == currPlayer)//the piece we want to take is of the opposite colour
			return false;
		if(src.getRow() < 0 || src.getRow() > 7 || src.getCol() < 0 || src.getCol() > 7)
			return false;
		if(dest.getRow() < 0 || dest.getRow() > 7 || dest.getCol() < 0 || dest.getCol() > 7)
			return false;
		
		Piece myPiece = getPiece(src);
		if(promotion != null)//promote a piece
		{
			
			if(myPiece.getType() != Type.PAWN)// we can only promote a pawn
				return false;
			if(promotion != Type.QUEEN && promotion != Type.ROOK && promotion != Type.BISHOP && promotion != Type.KNIGHT)
				return false;
			
			//make sure that dest is 1 square in front of src and that it's the final row
			if(myPiece.getColour() == Colour.WHITE)
			{
				if(dest.getRow() != 7 || src.getRow() != 6)
					return false;
			}
			else
			{
				if(dest.getRow() != 0 || src.getRow() != 1)
					return false;
			}
			//straight ahead
			if(src.getCol() == dest.getCol() && getPiece(dest) == null)
			{
				
				makeMove(src, dest, promotion);
				if(isCheck())
				{
					undo();
					return false;
				}
				if(changeColour)
					currPlayer = currPlayer == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
				return true;  	
			}
			//take a piece
			if(Math.abs(dest.getCol() - src.getCol()) == 1 && getPiece(dest) != null && getPiece(dest).getColour() != currPlayer)
			{
				makeMove(src, dest, promotion);
				if(isCheck())
				{
					undo(false);
					return false;
				}
				if(changeColour)
					currPlayer = currPlayer == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
				return true;
			}
		}
		else
		{
			if(getPiece(dest) != null)
			{
				//take piece
				if(canTake(myPiece, getPiece(dest)))
				{
					makeMove(src, dest);
					if(isCheck())
					{
						undo(false);
						return false;
					}
					if(changeColour)
						currPlayer = currPlayer == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
					return true;
				}
			}
			else
			{
				//move piece normally
				if(canMove(myPiece, dest))
				{
					makeMove(src, dest);
					if(isCheck())
					{
						undo(false);
						return false;
					}
					if(changeColour)	
						currPlayer = currPlayer == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
					return true;
				}
			}
			
		}
 
		return false;
		
	}
	public boolean canMove(Piece src, Position dest)
	{
		if(src.getType() == Type.PAWN)
		{
			//en passant
			if(moves.size() > 0)
			{
				if(moves.peek().prevPiece.getType() == Type.PAWN && Math.abs(moves.peek().prevPiece.getRow() - moves.peek().prevPos.getRow()) == 2)//check if last move moved a pawn and it moved 2 squares
				{
					if(Math.abs(src.getCol() - moves.peek().prevPiece.getCol()) == 1)//the piece is currently next to our piece
					{	
						
						if(dest.getCol() == moves.peek().prevPiece.getCol())//our destination is 1 behind the previous piece
						{
							//System.out.println("En Passant");
							if(src.getColour() == Colour.WHITE)
							{
								if(dest.getRow() == 5)
								{
									//System.out.println("En Passant");
									return true;
								}
							}
							else
							{
								if(dest.getRow() == 2)
								{
					//				System.out.println("En Passant");
									return true;
								}	
							}
						}
						
					}
				}
			}
			
			if(src.getColour() == Colour.WHITE)
			{
				
				
				if(dest.getRow() - src.getRow() == 1 && dest.getCol() == src.getCol())
					return true;
				if(src.getRow() == 1 && dest.getRow() == 3 && src.getCol() == dest.getCol() && getPiece(2, dest.getCol()) == null)
					return true;
			}
			else
			{
				if(dest.getRow() - src.getRow() == -1 && dest.getCol() == src.getCol())
					return true;
				if(src.getRow() == 6 && dest.getRow() == 4 && src.getCol() == dest.getCol() && getPiece(5, dest.getCol()) == null)
					return true;
			}
			return false;
		}
		else if(src.getType() == Type.KING && Math.abs(dest.getCol() - src.getCol()) == 2)
		{
			if(isCheck())
				return false;
			if((src.getCol() != 3) || (src.getRow() != 0 && src.getRow() != 7))//king is not where he's supposed to be
			{
//				if(src.getColour() == Colour.WHITE)
//				{
//					whiteCanCastleQ = false;
//					whiteCanCastleK = false;
//				}
//				else
//				{
//					blackCanCastleQ = false;
//					blackCanCastleK = false;
//				}
				return false;
				
			}
			if(dest.getCol() - src.getCol() == 2)//queen side
			{
				
				if(src.getColour() == Colour.WHITE)
				{
					if(whiteCanCastleQ)
					{
						if(src.getRow() == 0 && getPiece(0, 7) != null && getPiece(0, 7).getType() == Type.ROOK)
						{
							if(getPiece(0,4) == null && getPiece(0,5) == null && getPiece(0,6) == null)
							{	
								makeMove(new Position(0, 3),new Position(0, 4));
								if(isCheck())
								{
									undo(false);
									return false;
								}
								makeMove(new Position(0, 4), new Position(0, 5));
								if(isCheck())
								{
									undo(false);
									undo(false);
									return false;
								}
								undo(false);
								undo(false);
								return true;
								
							}
						}
					}
					return false;
				}
				else
				{
					if(blackCanCastleQ)
					{
						if(src.getRow() == 7 && getPiece(7, 7) != null && getPiece(7, 7).getType() == Type.ROOK)
						{
							if(getPiece(7,4) == null && getPiece(7,5) == null && getPiece(7,6) == null)
							{	
								makeMove(new Position(7, 3),new Position(7, 4));
								if(isCheck())
								{
									undo(false);
									return false;
								}
								makeMove(new Position(7, 4), new Position(7, 5));
								if(isCheck())
								{
									undo(false);
									undo(false);
									return false;
								}
								undo(false);
								undo(false);
								return true;
							}
						} 
					}
					return false;
				}
			}
			else//king side
			{
				if(src.getColour() == Colour.WHITE)
				{
					if(whiteCanCastleK)
					{
						if(src.getRow() == 0 && getPiece(0, 0) != null && getPiece(0, 0).getType() == Type.ROOK)
						{
							if(getPiece(0,1) == null && getPiece(0,2) == null )
							{	
								makeMove(new Position(0, 3),new Position(0, 2));
								if(isCheck())
								{
									undo(false);
									return false;
								}
								makeMove(new Position(0, 2), new Position(0, 1));
								if(isCheck())
								{
									undo(false);
									undo(false);
									return false;
								}
								undo(false);
								undo(false);
								return true;
							}
						}
					}
					return false;
				}
				else
				{
					if(blackCanCastleK)
					{
						if(src.getRow() == 7 && getPiece(7, 0) != null && getPiece(7, 0).getType() == Type.ROOK)
						{
							if(getPiece(7,1) == null && getPiece(7,2) == null)
							{
								makeMove(new Position(7, 3),new Position(7, 2));
								if(isCheck())
								{
									undo(false);
									return false;
								}
								makeMove(new Position(7, 2), new Position(7, 1));
								if(isCheck())
								{
									undo(false);
									undo(false);
									return false;
								}
								undo(false);
								undo(false);
								return true;
							}
						}
					}
					return false;
				}
			}
		}
		else
		{//cheat way to not have to rewrite the whole method
			return canTake(src, new Piece(src.getType(), src.getColour(), dest.getRow(), dest.getCol()));
		}
		
		
	}
	
	public boolean canTake(Piece src, Piece dest)
	{
		switch(src.getType())
		{
			case BISHOP:
				if(src.getRow() - dest.getRow() == src.getCol() - dest.getCol() || src.getRow() - dest.getRow() == dest.getCol() - src.getCol())
				{
					for(int i = 0; i < Math.abs(src.getRow() - dest.getRow()) -1; i++ )
					{
						int row = Math.min(dest.getRow(), src.getRow()) + i + 1;
						//set col according to which diagonal we're looking in
						int col = src.getRow() - dest.getRow() == src.getCol() - dest.getCol() ? Math.min(dest.getCol(), src.getCol()) + i + 1 : Math.max(dest.getCol(), src.getCol()) - i - 1; 
						if(getPiece(row, col) != null)
						{
							return false;
						}
					}
					return true;
					
				}
				return false;
			case QUEEN:
				return canTake(new Piece(Type.ROOK, src.getColour(), src.getRow(), src.getCol()), dest) || canTake(new Piece(Type.BISHOP, src.getColour(), src.getRow(), src.getCol()), dest);
			case KING:
				if(Math.abs(src.getRow() - dest.getRow()) +  Math.abs(src.getCol() - dest.getCol()) == 1 || Math.abs(src.getRow() - dest.getRow()) -  Math.abs(src.getCol() - dest.getCol()) == 0)
					return true;
				return false;
			case KNIGHT:
				boolean a = Math.abs(src.getRow() - dest.getRow()) == 2 && Math.abs(src.getCol() - dest.getCol()) == 1;
				boolean b = Math.abs(src.getCol() - dest.getCol()) == 2 && Math.abs(src.getRow() - dest.getRow()) == 1;
				if(a || b)
					return true;
				return false;
			case PAWN:
				if(src.getColour() == Colour.WHITE)
				{
					if(dest.getRow() - src.getRow() == 1 && Math.abs(dest.getCol() - src.getCol())== 1)
						return true;
				}
				else
				{
					if(dest.getRow() - src.getRow() == -1 && Math.abs(dest.getCol() - src.getCol())== 1)
						return true;
				}
				return false;
			case ROOK:
				if(src.getRow() == dest.getRow() || src.getCol() == dest.getCol())
				{
					for(int i = 0; i < Math.max(Math.abs(src.getRow() - dest.getRow()),Math.abs(src.getCol() - dest.getCol() )) - 1; i++ )
					{
						int row = src.getRow() == dest.getRow() ? src.getRow() : Math.min(dest.getRow(), src.getRow()) + i + 1;
						int col = src.getCol() == dest.getCol() ? src.getCol() : Math.min(dest.getCol(), src.getCol()) + i + 1;
						if(getPiece(row, col) != null)
						{
							return false;
						}
					}
					return true;
				}
				return false;
			default:
				break;
			
		}
		return false;
	}
	
	
	private boolean isCheck()
	{
		Piece king = currPlayer == Colour.WHITE ? kingW : kingB;
//		for(int i = 0; i < pieces.size(); i++)
//		{
//			if(pieces.get(i).getColour() == currPlayer && pieces.get(i).getType() == Type.KING)
//			{
//				king = pieces.get(i);
//				break;
//			}
//		}
		
		for(int i = 0; i < pieces.size(); i++)
		{
			if(pieces.get(i).getColour() != currPlayer)
			{
				if(canTake(pieces.get(i), king))
					return true;
			}
			
		}
		return false;
	}
	
	public Piece getPiece(int row, int col)
	{
		for(int i = 0; i < pieces.size(); i++)
			if(pieces.get(i).getRow() == row && pieces.get(i).getCol() == col)
				return pieces.get(i);
		return null;
	}
	
	public Piece getPiece(Position pos)
	{
		return getPiece(pos.getRow(), pos.getCol());
	}
	
	public ArrayList<Move> getAvailableMoves(Piece myPiece )
	{
		ArrayList<Move> possibleMoves = new ArrayList<>();

		//add all the moves that this piece can make
		switch(myPiece.getType())
		{
			case BISHOP:
				//top left diagonal
				for(int i = 1; i <= Math.min(myPiece.getRow(), myPiece.getCol()); i++)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() - i, myPiece.getCol() - i, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() - i, myPiece.getCol() - i), null));
						
					}
					if(getPiece(myPiece.getRow() - i, myPiece.getCol() - i) != null)
						break;
					
				}
				//top right diagonal
				for(int i = 1; i <= Math.min(myPiece.getRow(),7 - myPiece.getCol()); i++)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() - i, myPiece.getCol() + i, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() - i, myPiece.getCol() + i), null));
						
					}
					if(getPiece(myPiece.getRow() - i, myPiece.getCol() + i) != null)
						break;
				}
				//bottom left diagonal
				for(int i = 1; i <= Math.min(7 - myPiece.getRow(), myPiece.getCol()); i++)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + i, myPiece.getCol() - i, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + i, myPiece.getCol() - i), null));
						
					}
					if(getPiece(myPiece.getRow() + i, myPiece.getCol() - i) != null)
						break;
				}
				//bottom right diagonal
				for(int i = 1; i <= Math.min(7 - myPiece.getRow(), 7 - myPiece.getCol()); i++)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + i, myPiece.getCol() + i, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + i, myPiece.getCol() + i), null));
						
					}
					if(getPiece(myPiece.getRow() + i, myPiece.getCol() + i) != null)
						break;
				}
				break;
			case KING:
				if((myPiece.getColour() == Colour.WHITE && whiteCanCastleK && myPiece.getRow() == 0) || (myPiece.getColour() == Colour.BLACK && blackCanCastleK && myPiece.getRow() == 7))
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow(), myPiece.getCol() - 2, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow(), myPiece.getCol() - 2), null));
						
					}
				}
				
				if((myPiece.getColour() == Colour.WHITE && whiteCanCastleQ) || (myPiece.getColour() == Colour.BLACK && blackCanCastleQ))
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow(), myPiece.getCol() + 2, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow(), myPiece.getCol() + 2), null));
						
					}
				}
				for(int i = -1; i <= 1; i++)
				{
					for(int j = -1; j <= 1; j++)
					{
						if(i != 0 || j != 0)
						{
							if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + i, myPiece.getCol() + j, false))
							{
								undo(false);
								possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + i, myPiece.getCol() + j), null));
								
							}
						}	
 					}
				}
				break;
			case KNIGHT:
				for(int i = -2; i <= 2; i++)
				{
					for(int j = -2; j <= 2; j++)
					{
						if(i != 0 && j != 0 && i != j && i != -j) 
						{
							if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + i, myPiece.getCol() + j, false))
							{
								undo(false);
								possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + i, myPiece.getCol() + j), null));
								
							}
						}
					}
				}
				
				
				
				break;
			case PAWN:
				//check if its one line away from promotion
				if((myPiece.getColour() == Colour.WHITE && myPiece.getRow() == 6) || (myPiece.getColour() == Colour.BLACK && myPiece.getRow() == 1))
				{
					//promotions
					Type[] options = {Type.BISHOP, Type.KNIGHT, Type.QUEEN, Type.ROOK};
					if(myPiece.getColour() == Colour.WHITE)
					{
						for(int i = -1; i <= 1; i++)
						{
							for(int  j = 0; j < 4; j++)
							{
								if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + 1, myPiece.getCol() + i, options[j], false))
								{
									undo(false);
									possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + 1, myPiece.getCol() + i), options[j]));
									
								}
							}
						}
					}
					else
					{
						for(int i = -1; i <= 1; i++)
						{
							for(int  j = 0; j < 4; j++)
							{
								if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() - 1, myPiece.getCol() + i, options[j], false))
								{
									undo(false);
									possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() - 1, myPiece.getCol() + i), options[j]));
									
								}
							}
						}
					}
				}
				else
				{
					if(myPiece.getColour() == Colour.WHITE)
					{
						for(int i = -1; i <= 1; i++)
						{
							if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + 1, myPiece.getCol() + i, false))
							{
								undo(false);
								possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + 1, myPiece.getCol() + i), null));
								
							}
						}
						//double move
						if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() + 2, myPiece.getCol(), false))
						{
							undo(false);
							possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() + 2, myPiece.getCol()), null));
							
						}
					}
					else
					{
						for(int i = -1; i <= 1; i++)
						{
							if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() - 1, myPiece.getCol() + i, false))
							{
								undo(false);
								possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() - 1, myPiece.getCol() + i), null));
								
							}
							
						}
						//double move
						if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow() - 2, myPiece.getCol(), false))
						{
							undo(false);
							possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow() - 2, myPiece.getCol() ), null));
							
						}
					}
				}
				break;
			case QUEEN:
				ArrayList<Move> bishopMoves = getAvailableMoves(new Piece(Type.BISHOP, myPiece.getColour(), myPiece.getRow(), myPiece.getCol()));
				ArrayList<Move> rookMoves = getAvailableMoves(new Piece(Type.ROOK, myPiece.getColour(), myPiece.getRow(), myPiece.getCol()));
				for(int i = 0; i < bishopMoves.size(); i++)
					rookMoves.add(bishopMoves.get(i));
				return rookMoves;
			case ROOK:
				//up
				for(int i = myPiece.getRow() - 1; i >= 0; i--)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), i, myPiece.getCol(), false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(i, myPiece.getCol()), null));
						
					}
					if(getPiece(i, myPiece.getCol()) != null)
						break;
				}
				//down
				for(int i = myPiece.getRow() + 1; i <= 7; i++)
				{
					
					if(move(myPiece.getRow(), myPiece.getCol(), i, myPiece.getCol(), false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(i, myPiece.getCol()), null));
						
					}
					if(getPiece(i, myPiece.getCol()) != null)
						break;
				}
				//left
				for(int i = myPiece.getCol() - 1; i >= 0; i--)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow(), i, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow(), i), null));
						
					}
					if(getPiece(myPiece.getRow(), i) != null)
						break;
				}
				//right
				for(int i = myPiece.getCol() + 1; i <= 7; i++)
				{
					if(move(myPiece.getRow(), myPiece.getCol(), myPiece.getRow(),i, false))
					{
						undo(false);
						possibleMoves.add(new Move(new Position(myPiece.getRow(), myPiece.getCol()), new Position(myPiece.getRow(), i), null));
						
					}
					if(getPiece(myPiece.getRow(), i) != null)
						break;
				}
				break;
			default:
				break;
		
		}
		
		return possibleMoves;
	}
	
	public ArrayList<Move> getAvailableMoves()
	{
		ArrayList<Move> possibleMoves = new ArrayList<>();
		for(int i = 0; i < pieces.size(); i++)
		{
			ArrayList<Move> movesForPiece = new ArrayList<>();
			Piece myPiece = pieces.get(i);
			if(myPiece.getColour() == currPlayer)
			{
				//if(canMove(myPiece))//TODO:maybe add this later
				//{
					movesForPiece = getAvailableMoves(myPiece);
				//}
			}
			for(int j = 0; j < movesForPiece.size(); j++)
			{
				possibleMoves.add(movesForPiece.get(j));//add the pieces found for this piece to the main array 
			}
		}
		return possibleMoves;
	}	
	
	
	
	
	
	public boolean isCheckMate()
	{
		return getAvailableMoves().size() == 0 && isCheck();
	}
	
	public boolean isStaleMate()
	{
		return getAvailableMoves().size() == 0 && !isCheck();
	}
	public boolean gameOver()
	{
		return getAvailableMoves().size() == 0;
	}
	
	
	public void print()
	{
		char[][] board = new char[8][8];
		for(int i = 0; i < 8; i++)
			for(int j = 0; j< 8; j++)
				board[i][j] = (i+j) % 2 == 0 ? ' ' : '#';
		for(int i = 0; i < pieces.size(); i++)
			board[pieces.get(i).getRow()][pieces.get(i).getCol()] = pieces.get(i).toString().charAt(0);
		for(int i = 0; i < 8; i++)
			System.out.print(" -");
		System.out.println();
		for(int i = 0; i < 8; i++)
		{
			System.out.print("|");
			for(int j = 0; j < 8; j++)
				System.out.print(board[i][j] + "|");			
			System.out.println();
			for(int k = 0; k < 8; k++)
				System.out.print(" -");
			System.out.println();
		}
		
	}

}
