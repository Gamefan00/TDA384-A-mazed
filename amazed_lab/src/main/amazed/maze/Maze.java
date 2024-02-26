package amazed.maze;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * <code>Maze</code> is the main public class through which methods
 * clients can explore a maze.
 * <p>
 * A <code>Maze</code> object represents a connected graphs that can
 * be explored incrementally.  Every node in the graph represents a
 * <em>cell</em>, which can be thought as a room in the maze.  Every
 * node has an identifier &mdash; an integer whose value is unique
 * within the maze.  Node identifiers are generated randomly at every
 * object creation, and thus they are not persistent or deterministic.
 * <p>
 * Exploration of a maze begins at the start node, whose identifier
 * is returned by method <code>start</code>.  Given the identifier
 * of a node, method <code>neighbors</code> returns the set of
 * identifiers of all nodes adjacent to it.  Method
 * <code>hasGoal</code> determines if a given node contains a goal.
 * <p>
 * Finally, methods <code>spawn</code> and <code>move</code> animate
 * icons of players that move around the maze in its graphical
 * representation.
 *
 * @author  Carlo A. Furia
 */

public class Maze
{
    private Board board;

    Board getBoard()
    {
        return board;
    }

    private int delay = 500;
    private boolean animate = true;


    /**
     * Creates a maze by reading a map from file.
     *
     * @param filename   the name of the text file containing the map
     */
    Maze(String filename)
    {
        board = new Board(filename);
    }

    // initialize Maze wrapping given board
    Maze(Board board)
    {
        this.board = board;
    }

    void setDelay(int delay)
    {
        this.delay = delay;
        if (delay <= 0)
            setAnimate(false);
    }

    void setAnimate(boolean animate)
    {
        this.animate = animate;
    }

    /**
     * Returns the unique identifier of the start node, corresponding
     * to the top-left cell in the maze.
     *
     * @return   the identifier of the unique start node
     */
    public int start()
    {
        return board.getCell(0, 0).getId();
    }

    /**
     * Returns the set of the identifiers of all nodes directly
     * adjacent to a given node, and accessible from it. The set does
     * not include the given node itself, and thus has from zero to
     * four elements.
     *
     * @param id  the identifier of a node in the maze
     * @return    the set of identifier of all nodes that are in
     *            <code>id</code>'s neighborhood
     */
    public Set<Integer> neighbors(int id)
    {
        Set<Integer> neighbors = new HashSet<>(4);
        Position position = board.getPosition(id);
        for (Direction direction: Direction.values()) {
            Position newPosition = board.move(position, direction);
            if (newPosition != null)
                neighbors.add(board.getCell(newPosition).getId());
        }
        return neighbors;
    }

    /**
     * Tests whether a given node contains a goal.
     *
     * @param id   the identifier of a node in the maze
     * @return     <code>true</code> if the node with identifier <code>id</code> is a goal;
     *             <code>false</code> otherwise
     */
    public boolean hasGoal(int id)
    {
        return board.getCell(id).isHeart();
    }

    /**
     * Tests whether a sequence of node identifiers corresponds to a
     * connected path from the start node to a goal.
     *
     * @param path   a list of identifiers nodes in the maze
     * @return       <code>true</code> if <code>path</code> begins with the
     *               start node, follows a connected chain of adjacent
     *               nodes, and ends with a goal node;
     *               <code>false</code> otherwise
     */
    boolean isValidPath(List<Integer> path)
    {
        if (path.isEmpty())
            return false;
        ListIterator<Integer> iter = path.listIterator();
        int prev = 0, curr = iter.next();
        if (curr != start())
            return false;
        while (iter.hasNext()) {
            prev = curr;
            curr = iter.next();
            if (!neighbors(prev).contains(curr))
                return false;
        }
        return hasGoal(curr);
    }

    /**
     * Creates a new animated player, and place it on a given node.
     *
     * @param id   the identifier of a node in the maze where the new player is placed
     * @return     a unique identifier of the newly created player
     */
    public int newPlayer(int id)
    {
        if (!animate)
            return 0;
        return board.newPlayer(id);
    }

    /**
     * Removes all players from the maze. If multiple threads are
     * active on the maze, it is advisable to stop all threads before
     * calling this method; failure to do so may result in a
     * <code>ConcurrentModificationExeception</code>.
     */
    void removePlayers()
    {
        if (!animate)
            return;
        board.deregisterAll();
    }

    /**
     * Moves an existing animated player to a given node. This method
     * doesn't do anything if the player doesn't exist or the given
     * node is not accessible. The given node need not be adjacent to
     * the player's current node.
     *
     * @param playerId   the identifier of an existing player
     * @param id         a node in the maze where the player is moved
     */
    public void move(int playerId, int id)
    {
        if (!animate)
            return;
        Player player = board.getPlayer(playerId);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
        }
        if (player != null)
            player.move(id);
    }

    /**
     * Highlights on the maze all nodes that can be highlighted in path.
     *
     * @param path   a list of identifiers nodes in the maze
     */
    void markPath(List<Integer> path)
    {
        board.markPath(path);
    }
}
