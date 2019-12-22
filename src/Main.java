import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		Chess chess = new Chess();
		Board myBoard = new Board(chess);
		Agent myAgent = new Agent(chess, "random");
		while(true)
		{
//			//TODO: fix it so that when double clicking it doesn't automatically move (getAvailable moves seems to cause this) getAvailable moves changes the currPlayer, need to fix this
//			//TODO: also throwing a lot of errors, fix that 
			if(chess.getCurrPlayer() == Colour.BLACK)
			{
				myBoard.stop();
			
	//			System.out.println(myAgent.move());
		 		myAgent.move();
				myBoard.start();
				myBoard.refresh();
				if(chess.gameOver())
					break;
	  		}
		}
		

	}

}
