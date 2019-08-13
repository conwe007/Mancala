import java.util.Scanner;

public class Player
{
	private boolean which_player; // false - player0, true - player1
	private int[] player_side;
	private int[] opponent_side;
	private int[] opposite_side;
	private int mancala;
	private int input_select; // selects user input or ai input
								// 0 - human player
								// 1 - random ai
								// 2 - greedy ai
								// 3 - double ai
								// 4 - capture ai
								// 5 - first ai
								// 6 - avoid ai
								// 7 - weighted ai
	private int[] order_ai;

	public Player()
	{
		which_player = false;
		player_side = new int[]{-1, -1, -1, -1, -1, -1};
		opponent_side = new int[]{-1, -1, -1, -1, -1, -1};
		opposite_side = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
		mancala = -1;
		input_select = -1;
		order_ai = null;
	}

	public Player(boolean w, int i, int[] o)
	{
		which_player = w;

		if(w)
		{
			player_side = new int[]{7, 8, 9, 10, 11, 12};
			opponent_side = new int[]{0, 1, 2, 3, 4, 5};
			mancala = 13;
		} else
		{
			player_side = new int[]{0, 1, 2, 3, 4, 5};
			opponent_side = new int[]{7, 8, 9, 10, 11, 12};
			mancala = 6;
		}

		opposite_side = new int[]{12, 11, 10, 9, 8, 7, -1, 5, 4, 3, 2, 1, 0, -1};
		input_select = i;
		order_ai = o;
	}

	public int choose(int[] b, double[][] w, int p)
	{
		switch(input_select)
		{
			case 0:
				return humanInput(b);

			case 1:
				return aiRandom(b);

			case 2:
				return aiGreedy(b);

			case 3:
				return aiDouble(b);

			case 4:
				return aiCapture(b);

			case 5:
				return aiSecond(b);

			case 6:
				return aiAvoid(b);

			case 7:
				return aiWeighted(b, w, p);

			case 99:
				return aiOrder(b, order_ai);

			default:
				return -1;
		}
	} // method returns an input option from instance variable

	public int select(int[] b, int i)
	{
		switch(i)
		{
			case 0:
				return humanInput(b);

			case 1:
				return aiRandom(b);

			case 2:
				return aiGreedy(b);

			case 3:
				return aiDouble(b);

			case 4:
				return aiCapture(b);

			case 5:
				return aiSecond(b);

			case 6:
				return aiAvoid(b);

			default:
				return -1;
		}
	} // method returns an input option from argument variable

	private int humanInput(int[] b)
	{
		Scanner s = new Scanner(System.in);
		System.out.println("Choose a spot (0-5): ");
		int input = Integer.valueOf(s.nextLine());
		s.close();

		if(input >= 0 && input <= 5 && b[player_side[input]] >= 0)
		{
			return input;
		}

		return -1;
	} // method prompts an input from the command line

	private int aiRandom(int[] b)
	{
		int spot_choice = (int)(Math.random() * 6.0);

		while(!(b[player_side[spot_choice]] != 0)) // loop until a non-empty space is chosen
		{
			spot_choice = (int)(Math.random() * 6.0);
		}

		return spot_choice;
	} // method randomly selects one of the available moves

	private int aiGreedy(int[] b)
	{
		int[] weight = new int[]{0, 0, 0, 0, 0, 0};
		int[] spot = new int[]{0, 1, 2, 3, 4, 5};
		int keyw = 0;
		int keys = 0;

		for(int i = 0; i < 6; i++)
		{
			if(player_side[i] + b[player_side[i]] >= mancala)
			{
				weight[i]++;
			} // if the spot will score a point, give it priority
		}

		for(int i = 1; i < 6; i++)
		{
			keyw = weight[i];
			keys = spot[i];
			int j = i - 1;

			while(j >= 0 && weight[j] < keyw)
			{
				weight[j + 1] = weight[j];
				spot[j + 1] = spot[j];
				j = j - 1;
			}

			weight[j + 1] = keyw;
			spot[j + 1] = keys;
		} // sorts spots by respective weight, maintaining order

		for(int i = 0; i < 6; i++)
		{
			if(b[player_side[spot[i]]] != 0)
			{
				return spot[i]; // returns the spot furthest to the left that will score, the most likely to give another turn
			}
		}

		return -1; // something went wrong
	} // method picks the first spot on the left that scores a point

	public int aiDouble(int[] b)
	{
		for(int i = 5; i >= 0; i--)
		{
			if(player_side[i] + b[player_side[i]] == mancala)
			{
				return i;
			}
		} // starting from the right, loop until a spot will end in the player's mancala

		return -1;
	} // method picks the first spot on the right that gives another turn

	public int aiCapture(int[] b)
	{
		int spot;

		for(int i = 5; i >= 0; i--)
		{
			spot = (player_side[i] + b[player_side[i]]) % b.length;

			if(b[player_side[i]] != 0 && spot >= player_side[0] && spot <= player_side[5] && b[spot] == 0 && b[opposite_side[player_side[i]]] != 0)
			{
				return i;
			}
		} // starting from the rightmost spot, loop until a spot will result in a capture

		return -1;
	} // method picks the first spot on the right that captures opponent's marbles

	public int aiSecond(int[] b)
	{
		if(b[player_side[0]] == 4 && b[player_side[1]] == 4 && b[player_side[2]] == 0 && b[player_side[3]] == 5 && b[player_side[4]] == 5 && b[player_side[5]] == 5)
		{
			return 5;
		} else if(b[player_side[0]] == 5 && b[player_side[1]] == 0 && b[player_side[2]] >= 4 && b[player_side[3]] >= 4 && b[player_side[4]] >= 4 && b[player_side[5]] >= 4)
		{
			return 5;
		} // ensures that the rightmost spot is chosen on each player's second turn, contrary to aiGreedy

		return -1;
	} // method defines the second move of each player's turn

	public int aiWeighted(int[] b, double[][] w, int p) // board, weight chart, previous move
	{
		int spot_choice;
		int count = 0;
		double rand_double = Math.random();

		// using weighted odds, randomly choose a spot based on previous move made
		if(rand_double < w[p][0])
		{
			spot_choice = 0;
		} else if(rand_double < w[p][1])
		{
			spot_choice = 1;
		} else if(rand_double < w[p][2])
		{
			spot_choice = 2;
		} else if(rand_double < w[p][3])
		{
			spot_choice = 3;
		} else if(rand_double < w[p][4])
		{
			spot_choice = 4;
		} else
		{
			spot_choice = 5;
		}

		while(!(b[player_side[spot_choice]] != 0)) // loop until a non-empty space is chosen
		{
			if(rand_double < w[p][0])
			{
				spot_choice = 0;
			} else if(rand_double < w[p][1])
			{
				spot_choice = 1;
			} else if(rand_double < w[p][2])
			{
				spot_choice = 2;
			} else if(rand_double < w[p][3])
			{
				spot_choice = 3;
			} else if(rand_double < w[p][4])
			{
				spot_choice = 4;
			} else
			{
				spot_choice = 5;
			}

			count++;
			if(count > 1000)
			{
				return aiRandom(b);
			}
		}

		return spot_choice;
	} // method picks a random spot, weighted by a predetermined input

	public int aiAvoid(int[] b)
	{
		int spot;

		for(int i = 0; i < 6; i++)
		{
			spot = (opponent_side[i] + b[opponent_side[i]]) % b.length;

			if(b[opponent_side[i]] != 0 && spot >= opponent_side[0] && spot <= opponent_side[5] && b[spot] == 0 && b[opposite_side[opponent_side[i]]] != 0)
			{
				for(int j = 0; j < 6; j++)
				{
					if(opposite_side[opponent_side[i]] == player_side[j])
					{
						return j;
					}
				} // starting from player's rightmost spot, loop until captured location is found
			}
		} // starting from opponent's rightmost spot, loop until a spot will result in a capture

		return -1;
	} // method picks the first spot that will avoid capture by the opponent

	public int aiOrder(int[] b, int[] order)
	{
		for(int i = 0; i < order.length; i++)
		{
			if(select(b, order[i]) != -1)
			{
				return select(b, order[i]);
			} // loops through ais in order determined by order[]
		}

		return aiRandom(b); // if no other ais are valid, random spot will be selected
	} // method excecutes multiple ai selections in a predefined order

	public boolean whichPlayer()
	{
		return which_player;
	} // method returns boolean player tag

	public int boardIndexAt(int i)
	{
		return player_side[i];
	} // method translates player spot [0,5] to board spot [0,13]

	public int getMancala()
	{
		return mancala;
	} // method returns board index of player's mancala
}