public class AI
{
  private static final int NUM_TRIALS = 100000;
  private double[][] seed;
  private double[][] weight;

  public AI(double[][] w)
  {
    seed = new double[][]{{1, 1, 1, 1, 1, 1}, 
                          {1, 1, 1, 1, 1, 1},
                          {1, 1, 1, 1, 1, 1},
                          {1, 1, 1, 1, 1, 1},
                          {1, 1, 1, 1, 1, 1},
                          {1, 1, 1, 1, 1, 1}};

    if(w == null)
    {
      weight = seed;
    } else
    {
      weight = w;
    }
    
    refineWeightTable();
  }

  public void incrementWeightTable()
  {
    IntNode move_list_head = generateMoveList();
    IntNode move_list_pointer = move_list_head.getNext();

    int[] data;

    IntNode p0_weight_list_head = new IntNode();
    IntNode p1_weight_list_head = new IntNode();

    p0_weight_list_head.setNext(new IntNode());
    p1_weight_list_head.setNext(new IntNode());

    IntNode p0_weight_list_pointer = p0_weight_list_head.getNext();
    IntNode p1_weight_list_pointer = p1_weight_list_head.getNext();

    boolean turn_group = (move_list_pointer.getNext().getData()[0] == 1)?true:false;

    while(move_list_pointer.getNext() != null) // loop while there is another move in the move list
    {
      turn_group = (move_list_pointer.getNext().getData()[0] == 1)?true:false; // turn group is true if player 1 is reacting, false if player0 is reacting

      if(move_list_pointer.getData()[0] != move_list_pointer.getNext().getData()[0])
      {
        data = new int[]{move_list_pointer.getData()[1], move_list_pointer.getNext().getData()[1]}; // 2 element array with other player's move in [0] and current player's reaction move in [1]

        if(turn_group) // player0's reaction to player1
        {
          p0_weight_list_pointer.setData(data[0], 0);
          p0_weight_list_pointer.setData(data[1], 1);

          p0_weight_list_pointer.setNext(new IntNode());
          p0_weight_list_pointer = p0_weight_list_pointer.getNext();
        } else // player1's reaction to player0
        {
          p1_weight_list_pointer.setData(data[0], 0);
          p1_weight_list_pointer.setData(data[1], 1);

          p1_weight_list_pointer.setNext(new IntNode());
          p1_weight_list_pointer = p1_weight_list_pointer.getNext();
        }
      }

      move_list_pointer = move_list_pointer.getNext(); // increment move list pointer
    }

    /*
    p0_weight_list_pointer = p0_weight_list_head.getNext();
    p1_weight_list_pointer = p1_weight_list_head.getNext();

    while(p0_weight_list_pointer != null)
    {
      System.out.println(p0_weight_list_pointer.toString());
      p0_weight_list_pointer = p0_weight_list_pointer.getNext();
    }
    System.out.println();
    while(p1_weight_list_pointer != null)
    {
      System.out.println(p1_weight_list_pointer.toString());
      p1_weight_list_pointer = p1_weight_list_pointer.getNext();
    }
    */

    p0_weight_list_pointer = p0_weight_list_head.getNext();
    p1_weight_list_pointer = p1_weight_list_head.getNext();

    if(move_list_head.getData()[1] == 0) // player 0 won
    {
      while(p0_weight_list_pointer != null) // loop through player0's moves and add to weight list
      {
        weight[p0_weight_list_pointer.getData()[0]][p0_weight_list_pointer.getData()[1]]++;
        p0_weight_list_pointer = p0_weight_list_pointer.getNext();
      }
/*
      while(p1_weight_list_pointer != null) // loop through player1's moves and subtract from weight list
      {
        weight[p1_weight_list_pointer.getData()[0]][p1_weight_list_pointer.getData()[1]]--;
        p1_weight_list_pointer = p1_weight_list_pointer.getNext();
      }*/
    } else // player 1 won
    {/*
      while(p0_weight_list_pointer != null) // loop through player0's moves and subtract from weight list
      {
        weight[p0_weight_list_pointer.getData()[0]][p0_weight_list_pointer.getData()[1]]--;
        p0_weight_list_pointer = p0_weight_list_pointer.getNext();
      }

      while(p1_weight_list_pointer != null) // loop through player1's moves and add to weight list
      {
        weight[p1_weight_list_pointer.getData()[0]][p1_weight_list_pointer.getData()[1]]++;
        p1_weight_list_pointer = p1_weight_list_pointer.getNext();
      }*/
    }
  }

  public void generateWeightTable()
  {
    for(int i = 0; i < NUM_TRIALS; i++)
    {
      incrementWeightTable();
    }

    for(int i = 0; i < 6; i++)
    {
      for(int j = 0; j < 6; j++)
      {
        weight[i][j] = weight[i][j] / NUM_TRIALS;
      }
    }
  }

  public void refineWeightTable()
  {
    for(int i = 0; i < 6; i++)
    {
      for(int j = 0; j < 6; j++)
      {
        if(j != 0)
        {
          weight[i][j] += weight[i][j - 1];
        }
      }
      
      for(int j = 0; j < 6; j++)
      {
        weight[i][j] /= weight[i][5];
      }
    }
  }

  private IntNode generateMoveList()
  {
    Board b = new Board(6, 99, new int[]{5,3,4,2}, weight);
    b.playGame();

    return b.getMoveList();
  }

  public double[][] getWeightTable()
  {
    return weight;
  }

  public String toStringWeight()
  {
    String output = "";// = "                                 reaction\n";

    for(int i = 0; i < 6; i++)
    {
      if(i == 3)
      {
        output += "         ";
      } else
      {
        output += "         ";
      }

      for(int j = 0; j < 6; j++)
      {
        output += String.valueOf(weight[i][j]) + " " + ((weight[i][j] < 0)?"":" ") + ((Math.abs(weight[i][j]) > 10)?"":" ") + ((Math.abs(weight[i][j]) > 100)?"":" ") + ((Math.abs(weight[i][j]) > 1000)?"":" ");
      }

      output += "\n";
    }

    return output;
  }
}