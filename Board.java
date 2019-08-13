public class Board
{
  private static final boolean display = true;
  private static final boolean record_moves = false;

	private int[] board;

	private Player player0;
	private Player player1;

	private int[] opposite_side;

  private int previous_move;
  private double[][] weight;

  private IntNode move_list_head;
  private IntNode move_list_pointer;

	public Board()
	{
		board = new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0};

		player0 = new Player(false, 0, null);
		player1 = new Player(true, 1, null);

		opposite_side = new int[]{12, 11, 10, 9, 8, 7, -1, 5, 4, 3, 2, 1, 0, -1};

    previous_move = -1;
    weight = null;

    move_list_head = null;
    move_list_pointer = null;
	}

	public Board(int p0, int p1, int[] o, double[][] w)
	{
		board = new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0};

		player0 = new Player(false, p0, o);
		player1 = new Player(true, p1, o);

		opposite_side = new int[]{12, 11, 10, 9, 8, 7, -1, 5, 4, 3, 2, 1, 0, -1};

    previous_move = 2;
    weight = w;

    move_list_head = new IntNode();
    move_list_head.setNext(new IntNode());
    move_list_pointer = move_list_head.getNext();
	}

	public boolean move(Player p)
	{
		int spot = p.choose(board, weight, previous_move);
		int start_index;
		int player_mancala;
		int opponent_mancala;

		previous_move = spot;

		if(record_moves)
		{
		  move_list_pointer.setData(spot, 1);
		}

		if(display)
		{
		  System.out.println(String.valueOf(p.whichPlayer()?"computer ":"player ") + "moves at spot: " + String.valueOf(spot));
		}

		if(p.whichPlayer()) // choose starting index based on which player is moving
		{
			start_index = player1.boardIndexAt(spot);
			player_mancala = player1.getMancala();
			opponent_mancala = player0.getMancala();
		} else
		{
			start_index = player0.boardIndexAt(spot);
			player_mancala = player0.getMancala();
			opponent_mancala = player1.getMancala();
		}

		int count = board[start_index];
		int i = start_index + 1;

		board[start_index] = 0; // take marbles out of starting spot

		while(count > 1)
		{
			if(i == opponent_mancala)
			{
				i++;
			} // skip opponent's mancala

			i %= board.length; // loop if pointer > board length
			board[i]++;
			i++;
			count--;
		} // place all but the last marble

		if(i == opponent_mancala)
		{
			i++;
		} // skip opponent's mancala

		i %= board.length; // loop if pointer > board length
		board[i]++;

		if(i >= p.boardIndexAt(0) && i <= p.boardIndexAt(5) && board[i] == 1)
		{
			board[player_mancala] += board[opposite_side[i]];
			board[opposite_side[i]] = 0;
			return false; // player passes turn to opponent
		}
		// if the last marble placed is on an empty spot on the player's side
		// that is not the starting spot, put marbles in opposite spot into
		// player's mancala

		if(i == player_mancala)
		{
			return true; // player takes another turn
		}

		return false; // player passes turn to opponent
	}

	public boolean gameOver()
	{
	    return !((board[player0.boardIndexAt(0)] != 0) || (board[player0.boardIndexAt(1)] != 0) || (board[player0.boardIndexAt(2)] != 0) || (board[player0.boardIndexAt(3)] != 0) || (board[player0.boardIndexAt(4)] != 0) || (board[player0.boardIndexAt(5)] != 0))
	    		|| !((board[player1.boardIndexAt(0)] != 0) || (board[player1.boardIndexAt(1)] != 0) || (board[player1.boardIndexAt(2)] != 0) || (board[player1.boardIndexAt(3)] != 0) || (board[player1.boardIndexAt(4)] != 0) || (board[player1.boardIndexAt(5)] != 0)); // if either side is empty, return true
	}

	public boolean finishGame()
	{
		if(!gameOver())
		{
			return false; // both sides still have marbles left
		}

		for(int i = 0; i < 6; i++)
		{
			board[player0.getMancala()] += board[player0.boardIndexAt(i)];
			board[player1.getMancala()] += board[player1.boardIndexAt(i)];
			board[player0.boardIndexAt(i)] = 0;
			board[player1.boardIndexAt(i)] = 0;
		} // add all marbles on each player's side to their mancala

		return true;
	}

	public int decideWinner()
	{
		if(board[player0.getMancala()] > board[player1.getMancala()])
		{
			return 0;
		} else if(board[player0.getMancala()] < board[player1.getMancala()])
		{
			return 1;
		}

		return 2;
	}

	public int playGame()
	{
		boolean player_turn = (Math.random() < 0.5); // true - player1, false - player0

		if(record_moves)
		{
			move_list_head.setData(player_turn?1:0, 0); // records which player moved first in the first index of the head node
		}

		if(display)
		{
			printBoard();
			System.out.println();
		}

		while(!gameOver())
		{
			if(player_turn) // player1's turn
			{
			if(record_moves)
			{
			move_list_pointer.setData(1, 0); // records player1's move
			//move_list_pointer.setData(player1.choose(board), 1);
			}

			player_turn = move(player1); // if player1 gets another turn, true, else false

			if(display)
			{
				printBoard();
				System.out.println();
			}
					
			if(sumMarbles() != 48)
			{
				return -1; // sum should never change
			}
			} else // player0's turn
			{
        if(record_moves)
        {
          move_list_pointer.setData(0, 0); // records player1's move
          //move_list_pointer.setData(player0.choose(board), 1);
        }

        player_turn = !move(player0); // if player0 gets another turn, false, else true

				if(display)
				{
					printBoard();
					System.out.println();
				}
				
				if(sumMarbles() != 48)
				{
				  return -1; // sum should never change
				}
			}
      
			if(record_moves)
			{
				move_list_pointer.setNext(new IntNode());
				move_list_pointer = move_list_pointer.getNext(); // increments the move list pointer
			}
		}

    if(record_moves)
    {
      move_list_head.setData(decideWinner(), 1); // records which player won in the second index of the head node
    }

		finishGame();

		return decideWinner();
	}

	private void resetBoard()
	{
		board = new int[]{4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0};
	}

	private int sumMarbles()
	{
		int sum = 0;

		for(int i = 0; i < 14; i++)
		{
			sum += board[i];
		}

		return sum;
	}

  public IntNode getMoveList()
  {
    return move_list_head;
  }

	private void printBoard()
	{
		System.out.println(toString());
	}

  public void printMoveList()
  {
    move_list_pointer = move_list_head;

    while(move_list_pointer != null)
    {
      System.out.println(move_list_pointer.toString());
      move_list_pointer = move_list_pointer.getNext();
    }
  }

	public String toString()
	{
		String output = "__________________________\n|  | ";

		for(int i = 12; i > 7; i--)
		{
			output += (String.valueOf(board[i])) + ((board[i] / 10 == 0)?"  ":" ");
		}

		output += (String.valueOf(board[7])) + ((board[7] / 10 == 0)?" ":"");

		output += ("|  |\n|" + String.valueOf(board[player1.getMancala()]) + ((board[player1.getMancala()] / 10 == 0)?" ":"")) + "|------------------|" + String.valueOf(board[player0.getMancala()]) + " |\n|  | ";

		for(int i = 0; i < 5; i++)
		{
			output += (String.valueOf(board[i])) + ((board[i] / 10 == 0)?"  ":" ");
		}

		output += (String.valueOf(board[5])) + ((board[5] / 10 == 0)?" ":"");

		output += "|  |\n‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\npos: 0  1  2  3  4  5";

		return output;
	}
}