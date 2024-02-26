package amazed.maze;

import java.util.Hashtable;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.File;


class ImageFactory
{
    private static String imgDir = "images";

    private static Hashtable<String, Image> images = initializeImages();
    private static Hashtable<String, Character> characters = initializeCharacters();

    static Image getImage(String name)
    {
        return images.get(name);
    }

    static Character getText(String name)
    {
        return characters.get(name);
    }

    private static Hashtable<String, Image> initializeImages()
    {
        Hashtable<String, Image> images = new Hashtable<>();
        File dir = new File(imgDir);
        File[] dirFiles = dir.listFiles();
        if (dirFiles != null) {
            for (File f : dirFiles) {
                String name = graphicName(f);
                if (name != null) {
                    images.put(name, new ImageIcon(f.getPath()).getImage());
                }
            }
        }
        return images;
    }

    private static String graphicName(File f)
    {
        String fname = f.getName();
        int lastDot = fname.lastIndexOf(".");
        String ext = fname.substring(lastDot + 1);
        String name = fname.substring(0, lastDot);
        if (f.isFile() && ext.equals("png")) {
            return name;
        } else {
            return null;
        }
    }

    private static Hashtable<String, Character> initializeCharacters()
    {
        Hashtable<String, Character> characters = new Hashtable<>();
        for (String name : images.keySet()) {
            Character ch;
            switch (name) {
            case "empty":
                ch = '.';
                break;
            case "brick":
                ch = '*';
                break;
            case "solid":
                ch = '#';
                break;
            case "marked":
                ch = '+';
                break;
            case "heart":
                ch = 'v';
                break;
            default:
                ch = '?';
            }
            characters.put(name, ch);
        }
        return characters;
    }
}
