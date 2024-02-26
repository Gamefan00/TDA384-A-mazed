package amazed.maze;

import java.awt.Image;


public class Player
    implements Comparable<Player>
{
    private final int id;
    private final String name;
    private final Image image;

    private Board board = null;
    private int row, col;

    private final String playerFileName = "player";

    Player(int id, String name, int imageId)
    {
        this.id = id;
        this.name = name;
        Image image = ImageFactory.getImage(playerFileName + Integer.toString(imageId));
        if (image == null)
            image = ImageFactory.getImage(playerFileName);
        this.image = image;
        this.row = -1;
        this.col = -1;
    }

    Player(int id, String name)
    {
        this(id, name, 1 + (id % 4));
    }

    // add player to board at initial position row, col
    void onBoard(Board board, int row, int col)
    {
        if (this.board == null && board.isOnBoard(row, col)) {
            this.board = board;
            this.row = row;
            this.col = col;
            board.register(this, row, col);
        }
    }

    // add player to board in node with given id
    void onBoard(Board board, int id)
    {
        Position position = board.getPosition(id);
        onBoard(board, position.getRow(), position.getCol());
    }

    int getId()
    {
        return id;
    }

    String getName()
    {
        return name;
    }

    Position getPosition()
    {
        return new Position(this.getRow(), this.getCol());
    }

    int getRow()
    {
        return row;
    }

    int getCol()
    {
        return col;
    }

    void setRow(int row)
    {
        this.row = row;
    }

    void setCol(int col)
    {
        this.col = col;
    }

    Character getText()
    {
        return 'P';
    }

    Image getImage()
    {
        return image;
    }

    // move player by one node in given direction
    void move(Direction direction)
    {
        Position newPosition = board.move(new Position(row, col), direction);
        if (board != null && newPosition != null)
            board.move(this, newPosition.getRow(), newPosition.getCol());
    }

    // move player to cell with given id newCellId
    void move(int newCellId)
    {
        Position newPosition = board.getPosition(newCellId);
        if (board != null
                && newPosition != null
                && board.isAccessible(newPosition.getRow(), newPosition.getCol()))
            board.move(this, newPosition.getRow(), newPosition.getCol());
    }

    public int compareTo(Player other)
    {
        return getId() - other.getId();
    }
}
