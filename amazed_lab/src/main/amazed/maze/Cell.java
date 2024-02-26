package amazed.maze;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Toolkit;


class Cell
{
    private final Tile tile;
    private final Queue<Player> players;

    private final Image image;
    private final Character text;
    private final int id;

    Cell(Tile tile, int id)
    {
        this.tile = tile;
        this.image = tile.getImage();
        this.text = tile.getText();
        this.id = id;

        this.players = new ConcurrentLinkedQueue<>();
    }

    Tile getTile()
    {
        return tile;
    }

    int getId()
    {
        return id;
    }

    Image getImage()
    {
        if (players.isEmpty())
            return image;
        else
            return players.element().getImage();
    }

    Character getText()
    {
        if (players.isEmpty())
            return text;
        else
            return players.element().getText();
    }

    int getWidth()
    {
        return getImage().getWidth(null);
    }

    int getHeight()
    {
        return getImage().getHeight(null);
    }

    boolean isAccessible()
    {
        return tile == Tile.EMPTY || tile == Tile.HEART;
    }

    boolean isMarkable()
    {
        return tile == Tile.EMPTY || tile == Tile.HEART;
    }

    Cell marked()
    {
        if (!isMarkable())
            return this;
        if (isHeart())
            return new Cell(Tile.FOUND, id);
        else
            return new Cell(Tile.MARKED, id);
    }

    public boolean isHeart()
    {
        return tile == Tile.HEART;
    }

    void add(Player player)
    {
        if (isAccessible())
            players.add(player);
    }

    void remove(Player player)
    {
        if (isAccessible())
            players.remove(player);
    }

    // return a copy of the players list
    Player[] getPlayers()
    {
        return players.toArray(new Player[0]);
    }
}
