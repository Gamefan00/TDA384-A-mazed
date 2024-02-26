package amazed.maze;

import java.awt.Image;


public enum Tile
{
    EMPTY("empty"),
    BRICK("brick"),
    SOLID("solid"),
    MARKED("marked"),
    FOUND("found"),
    HEART("heart");

    private final String name;
    private final Image image;
    private final Character text;

    Tile(String name)
    {
        this.name = name;
        this.image = ImageFactory.getImage(this.name);
        this.text = ImageFactory.getText(this.name);
    }

    Image getImage()
    {
        return image;
    }

    Character getText()
    {
        return text;
    }

    char getChar()
    {
        return text.charValue();
    }
}
