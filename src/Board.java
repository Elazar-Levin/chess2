import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Board {
	private int startX;
	private int startY;
	private ArrayList<Piece> pieces;
	private Chess chess;
	//private boolean[][] inAir;
	private boolean inAir;
	private int airX, airY;
	private ArrayList<Move> possibleMoves;
	
	private boolean promoting;
	private int promoteX, promoteY;
	
	private boolean stop;
	
	private int mouseX; 
	private int mouseY; 
	private boolean doDrag;
	
	private Canvas canvas;
	private BufferedImage bf;
	
	private BufferedImage pawn;
	private BufferedImage chessPieces;
	
	private Map<Type, Rect> images;
	
	private int offset = 27; // the y offset to get fro white to black
	
	public Board(Chess chess)
	{
		bf = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		possibleMoves = new ArrayList<>();
		this.chess = chess;
		this.pieces = chess.getPieces();
		
		stop = false;
		inAir = false;
		airX = airY = -1;
		startX = 50;
		startY = 50;
		JFrame frame = new JFrame("Chess");
		
		try {
			pawn = ImageIO.read(new File("assets/pawn.png"));
			chessPieces = ImageIO.read(new File("assets/chesspieces.png"));
		 
		} catch (IOException e) {
		}
		images = new HashMap<>();	
		images.put(Type.PAWN, new Rect(6, 3, 18, 27));
		images.put(Type.KNIGHT, new Rect(25, 3, 36, 27));
		images.put(Type.BISHOP, new Rect(44, 3, 54, 27));
		images.put(Type.ROOK, new Rect(60, 3, 70, 27));
		images.put(Type.KING, new Rect(77, 3, 87, 27));
		images.put(Type.QUEEN, new Rect(92, 3, 104, 27));
		canvas = new Canvas(){
			
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g)
			{
				paintComponent(bf.getGraphics());
				
				g.drawImage(bf, 0, 0, null);
			}
			@Override 
			public void update(Graphics g)
			{
				paint(g);
			}
			
			
		};
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(promoting)
				{
					if(e.getX() > 60 && e.getX() < 140 && e.getY() > 210 && e.getY() < 290)//castle square
					{
						promoting = false;
						chess.move(airY, airX, promoteY, promoteX, Type.ROOK, true);
						canvas.repaint();
					}
					if(e.getX() > 160 && e.getX() < 240 && e.getY() > 210 && e.getY() < 290)//knight square
					{
						promoting = false;
						chess.move(airY, airX, promoteY, promoteX, Type.KNIGHT, true);
						canvas.repaint();
					}
					if(e.getX() > 260 && e.getX() < 340 && e.getY() > 210 && e.getY() < 290)//bishop square
					{
						promoting = false;
						chess.move(airY, airX, promoteY, promoteX, Type.BISHOP, true);
						canvas.repaint();
					}
					if(e.getX() > 360 && e.getX() < 440 && e.getY() > 210 && e.getY() < 290)//queen square
					{
						promoting = false;
						chess.move(airY, airX, promoteY, promoteX, Type.QUEEN, true);
						canvas.repaint();
					}
				}
				else
				{
					if(e.getX() > 210 && e.getX() < 280 && e.getY() > 470 && e.getY() < 490)//we are in the undo button
					{
						chess.undo();
						canvas.repaint();
					}
					else if(e.getX() > 310 && e.getX() < 405 && e.getY() > 470 && e.getY() < 490)//we are in the undox2 button
					{
						chess.undo();
						chess.undo();
						canvas.repaint();
					}
					else
					{
						mouseDown(e);
					}
				}
				//Graphics g = canvas.getGraphics();
				//paintComponent(g);
				//g.dispose();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if(doDrag)
					canvas.repaint();
				mouseUp(e);
				
			//	Graphics g = canvas.getGraphics();
			//	paintComponent(g);
			//g.fillOval(mouseX - 20 , mouseY - 20, 40, 40);
			//	g.dispose();
				
			}
			
		});
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e)
			{
				mouseDrag(e);
				if(doDrag)
					canvas.repaint();
			}
			
		});
		canvas.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
	
	}
	
	private void mouseDown(MouseEvent e)
	{
		int boardX = (e.getX() - startX)/50; // find the x coordinate of the block we clicked in
		int boardY = (e.getY() - startY)/50; // find the y coordinate of the block we clicked in
		mouseX = e.getX();
		mouseY = e.getY();
	
		if(chess.getPiece(boardY, boardX) != null)
		{
			doDrag = true;
			inAir = true;
			airX = boardX;
			airY = boardY;
			//handle later
			possibleMoves = chess.getAvailableMoves(chess.getPiece(boardY, boardX));
		}
			
	}
	private void mouseUp(MouseEvent e)
	{
		int boardX = (e.getX() - startX)/50; // find the x coordinate of the block we released in
		int boardY = (e.getY() - startY)/50; // find the y coordinate of the block we released in
		doDrag = false;
	
		if(inAir)
		{
			Piece srcPiece = chess.getPiece(airY, airX);
				
			if((srcPiece.getColour() == chess.getCurrPlayer() && srcPiece.getType() == Type.PAWN && ((srcPiece.getColour() == Colour.BLACK && airY == 1 && boardY == 0) || (srcPiece.getColour() == Colour.WHITE && airY == 6 && boardY == 7))) && (chess.canTake(srcPiece, chess.getPiece(boardY, boardX)) || chess.canMove(srcPiece, new Position(boardY, boardX))))
			{
				promoting = true;
				promoteX = boardX;
			 	promoteY = boardY;
				inAir = false;
			}
			else
			{
				chess.move(airY, airX, boardY, boardX, true);
				inAir = false;
			}
	
			
		}
	}
	private void mouseDrag(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}
	public void refresh()
	{
		canvas.repaint();
	}
	public void stop()
	{
		stop = true;
	}
	public void start()
	{
		stop = false;
	}
	private void paintComponent(Graphics g)
	{
		if(!stop)
		{
		
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, 500, 500);
			//g.clearRect(0, 0, 500, 500);
			g.setColor(Color.WHITE);
		
			for(int i = 0; i < 8; i++)
			{
				for(int j = 0; j < 8; j ++)
				{
					if((i + j) % 2 == 0)
						g.setColor(Color.WHITE);
					else
						g.setColor(Color.BLACK);
					g.fillRect(startX + i*50, startY + j*50, 50, 50);
				}
			}
			Piece myPiece = null;
			for(int i = 0; i < pieces.size(); i++)
			{
			
				Piece p = pieces.get(i);
				if(!inAir || (inAir && airX != p.getCol() || airY != p.getRow()))//draw everything that's not being dragged
				{
					Rect myRect = images.get(p.getType());
					if(p.getColour() == Colour.WHITE)
					{
						g.drawImage(chessPieces, startX + p.getCol()*50 + 5 , startY + p.getRow()*50 + 5, startX + p.getCol()*50 + 45, startY + p.getRow()*50 + 45, myRect.x1, myRect.y1, myRect.x2,  myRect.y2, null );
					}
					else
					{
						g.drawImage(chessPieces, startX + p.getCol()*50 + 5 , startY + p.getRow()*50 + 5, startX + p.getCol()*50 + 45, startY + p.getRow()*50 + 45, myRect.x1, myRect.y1 + offset, myRect.x2,  myRect.y2 + offset, null );
					}
					
				}
				else
				{
					for(int j = 0; j < possibleMoves.size(); j++)
					{
						g.setColor(Color.GREEN);
						Move myMove = possibleMoves.get(j);
						g.fillRect(startX + myMove.dest.getCol()*50, startY + myMove.dest.getRow()*50, 50, 50);
						
					}
					
					Rect myRect = images.get(p.getType());
					if(p.getColour() == Colour.WHITE)
					{
						g.drawImage(chessPieces, mouseX - 20 , mouseY - 20, mouseX + 20, mouseY + 20, myRect.x1, myRect.y1, myRect.x2,  myRect.y2, null );
					}
					else
					{
						g.drawImage(chessPieces, mouseX - 20, mouseY - 20, mouseX + 20, mouseY + 20, myRect.x1, myRect.y1 + offset, myRect.x2,  myRect.y2 + offset, null );
					}
					
				}
				
		
				
			}
			
			//promoting
			if(promoting)
			{
				g.setColor(Color.DARK_GRAY);
				g.fillRect(startX, startY + 150, 400, 100);
				g.setColor(Color.WHITE);
				for(int i = 0; i < 4; i++)
					g.fillRect(startX+100*i + 10, startY + 160, 80, 80);
				if(chess.getCurrPlayer() == Colour.WHITE)
				{
					Rect myRect = images.get(Type.ROOK);
					g.drawImage(chessPieces, startX + 100 * 0 + 20 , startY + 170, startX + 100 * 0 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1, myRect.x2,  myRect.y2, null );
					myRect = images.get(Type.KNIGHT);
					g.drawImage(chessPieces, startX + 100 * 1 + 20 , startY + 170, startX + 100 * 1 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1, myRect.x2,  myRect.y2, null );
					myRect = images.get(Type.BISHOP);
					g.drawImage(chessPieces, startX + 100 * 2 + 20 , startY + 170, startX + 100 * 2 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1, myRect.x2,  myRect.y2, null );
					myRect = images.get(Type.QUEEN);
					g.drawImage(chessPieces, startX + 100 * 3 + 20 , startY + 170, startX + 100 * 3 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1, myRect.x2,  myRect.y2, null );
				}
				else
				{
					Rect myRect = images.get(Type.ROOK);
					g.drawImage(chessPieces, startX + 100 * 0 + 20 , startY + 170, startX + 100 * 0 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1 + offset, myRect.x2,  myRect.y2 + offset, null );
					myRect = images.get(Type.KNIGHT);
					g.drawImage(chessPieces, startX + 100 * 1 + 20 , startY + 170, startX + 100 * 1 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1 + offset, myRect.x2,  myRect.y2 + offset, null );
					myRect = images.get(Type.BISHOP);
					g.drawImage(chessPieces, startX + 100 * 2 + 20 , startY + 170, startX + 100 * 2 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1 + offset, myRect.x2,  myRect.y2 + offset, null );
					myRect = images.get(Type.QUEEN);
					g.drawImage(chessPieces, startX + 100 * 3 + 20 , startY + 170, startX + 100 * 3 + 20 + 60 , startY + 170 + 60, myRect.x1, myRect.y1 + offset, myRect.x2,  myRect.y2 + offset, null );
				}
			}
			
			
			//undo button
			g.setColor(Color.BLUE);
			g.drawRect(210, 470, 70, 20);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Dialog", Font.PLAIN, 15));
			g.drawString("UNDO", 226, 486);
			
			//undox2 button
			g.setColor(Color.BLUE);
			g.drawRect(310, 470, 95, 20);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Dialog", Font.PLAIN, 15));
			g.drawString("UNDO X2", 326, 486);
			
			// line numbers
			//top row
			g.setColor(Color.DARK_GRAY);
			for(Integer j = 0; j < 8; j++)
				g.drawString(j.toString(), 72 + 50 * j, 40);
			//left row
			for(Integer j = 0; j < 8; j++)
				g.drawString(j.toString(), 33, 80 + 50 * j);
		
		}
	}
	
	
	
}
