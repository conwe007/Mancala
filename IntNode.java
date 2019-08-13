public class IntNode
{
  private int[] data;
  private IntNode next;

  public IntNode()
  {
    data = new int[2];
    next = null;
  }

  public IntNode(int[] d)
  {
    data = d;
    next = null;
  }

  public int[] getData()
  {
    return data;
  }

  public void setData(int d, int i)
  {
    data[i] = d;
  }

  public IntNode getNext()
  {
    return next;
  }

  public void setNext(IntNode n)
  {
    next = n;
  }

  public String toString()
  {
    return String.valueOf(data[0]) + " " + String.valueOf(data[1]);
  }
}