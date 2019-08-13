public class Main
{
	private static int MODE_SELECT = 2;
                    // 0 - human vs human
                    // 1 - human vs random ai
                    // 2 - human vs ordered ai
                    // 3 - random ai vs ordered ai macro
                    // 4 - random ai vs ordered ai deterministic
                    // 5 - ai generation
                    // 6 - human vs generated ai
	private static final int NUM_TRIALS = 100000;
	private static final int AI_FIRST = 2;
	private static final int AI_LAST = 6;

	public static void main(String[] args)
	{
    Board b;
    int[] order_ai = new int[]{5, 3, 6, 4, 2};
                    // 0 - human player
                    // 1 - random ai
                    // 2 - greedy ai
                    // 3 - double ai
                    // 4 - capture ai
                    // 5 - first ai
                    // 6 - weighted ai

    if(MODE_SELECT == 0)
    {
      b = new Board(0, 0, null, null);
      b.playGame();
    }
    
    else if(MODE_SELECT == 1)
    {
      b = new Board(0, 1, null, null);
      b.playGame();
    }
    
    else if(MODE_SELECT == 2)
    {
      b = new Board(0, 99, order_ai, null);
      b.playGame();
    }
    
    else if(MODE_SELECT == 3)
    {
      int[] results = {0, 0, 0};
      double[][] weight = new double[][]{{0.250162778,0.509626945,0.638724951,0.68760926,0.760924857,1},
                                          {0.054506528,0.180505839,0.288597045,0.364107983,0.37016307,1},
                                          {0.037556392,0.142358548,0.195756363,0.211271604,0.24185778,1},
                                          {0.072508948,0.142033595,0.204241831,0.269501204,0.355532215,1},
                                          {0.116928075,0.215955821,0.284900268,0.437822243,0.494701275,1},
                                          {0.131822922,0.154536888,0.220029223,0.486916956,0.539748668,1}};

      for(int i = 0; i < NUM_TRIALS; i++)
      {
        b = new Board(99, 1, order_ai, null);
        results[b.playGame()]++;
      }

      System.out.println(results[0]);
      System.out.println(results[1]);
      System.out.println(results[2]);
    }
    
    else if(MODE_SELECT == 4)
    {
      int[] results;
      double win_percent;

      for(int w = AI_FIRST + 1; w <= AI_LAST; w++)
      {
        for(int x = AI_FIRST + 1; x <= AI_LAST; x++)
        {
          for(int y = AI_FIRST + 1; y <= AI_LAST; y++)
          {
            for(int z = AI_FIRST + 1; z <= AI_LAST; z++)
            {
				for(int v = AI_FIRST; v <= AI_LAST; v++)
				{
					if(w != x && w != y && w != z && w != v && x != y && x != z && x != v && y != z && y != v && z != v)
					{
						order_ai = new int[]{w, x, y, z, v};

						results = new int[]{0, 0, 0};

						for(int i = 0; i < NUM_TRIALS; i++)
						{
							b = new Board(1, 99, order_ai, null);
							results[b.playGame()]++;
						}

						win_percent = (double)results[1] / (double)NUM_TRIALS;

						System.out.print("Order: ");
						for(int i = 0; i < order_ai.length; i++)
						{
							System.out.print(order_ai[i]);
						}
						System.out.print(" Win Percent:");
						System.out.println(win_percent);
					}
				}
            }
          }
        }
      }

      
    }

    else if(MODE_SELECT == 5)
    {
      AI[] ai = new AI[100];

      ai[0] = new AI(null);
      ai[0].generateWeightTable();
      ai[0].refineWeightTable();
      System.out.println(ai[0].toStringWeight());

      for(int i = 1; i < 100; i++)
      {
        ai[i] = new AI(ai[i - 1].getWeightTable());
        ai[i].generateWeightTable();
        ai[i].refineWeightTable();
        if(i % 10 == 0)
        {
          System.out.println(ai[i].toStringWeight());
        }
      }

      System.out.println(ai[99].toStringWeight());
    }

    else if(MODE_SELECT == 6)
    {
      double[][] weight = new double[][]{{0.250162778,0.509626945,0.638724951,0.68760926,0.760924857,1},
                                          {0.054506528,0.180505839,0.288597045,0.364107983,0.37016307,1},
                                          {0.037556392,0.142358548,0.195756363,0.211271604,0.24185778,1},
                                          {0.072508948,0.142033595,0.204241831,0.269501204,0.355532215,1},
                                          {0.116928075,0.215955821,0.284900268,0.437822243,0.494701275,1},
                                          {0.131822922,0.154536888,0.220029223,0.486916956,0.539748668,1}};
      b = new Board(0, 6, new int[]{5,3,4,2}, weight);
      b.playGame();
    }
	}
}