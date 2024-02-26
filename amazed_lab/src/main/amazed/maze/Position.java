package amazed.maze;


class Position
{
    private final int row;
    private final int col;

    Position(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    int getRow()
    {
        return row;
    }

    int getCol()
    {
        return col;
    }

    @Override
    public String toString()
    {
        return "(" + getRow() + ", " + getCol() + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
            return true;
        if (other == null || !(other instanceof Position))
            return false;
        Position otherPosition = (Position) other;
        return otherPosition.getRow() == getRow() && otherPosition.getCol() == getCol();
    }
}
