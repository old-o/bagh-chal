public class Move
{

    public Move(int X1, int Y1, int X2, int Y2, int b[][])
    {
        board = new int[5][5];
        x1 = X1;
        y1 = Y1;
        x2 = X2;
        y2 = Y2;
        board = b;
    }

    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public int board[][];
}
