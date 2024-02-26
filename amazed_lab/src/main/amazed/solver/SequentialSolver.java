package amazed.solver;

import amazed.maze.Maze;

import java.util.concurrent.RecursiveTask;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Collections;

/**
 * <code>SequentialSolver</code> implements a solver for
 * <code>Maze</code> objects using a single-thread depth-first search.
 * <p>
 * Even though <code>SequentialSolver</code> is implemented as a
 * <code>RecursiveTask</code>, it is purely sequential. Method
 * <code>compute</code> returns a solution consisting of a list of
 * node identifiers in the maze that lead from the start node to a
 * goal.
 * <p>
 * Depth-first search is implemented using a stack of
 * <code>frontier</code> nodes &mdash; giving the nodes to be explored
 * next in depth-first order. Visited nodes are added to a set
 * <code>visited</code>. For each visited node,
 * <code>predecessor</code> keeps track of the other node adjacent to
 * the visited node that has been visited just before it. Method
 * <code>pathFromTo</code> reconstructs a path by following the
 * <code>precedessor</code> relation backwards.
 *
 * @author  Carlo A. Furia
 */

public class SequentialSolver
    extends RecursiveTask<List<Integer>>
{
    /**
     * Creates a solver that searches in <code>maze</code> from the
     * start node to a goal.
     *
     * @param maze   the maze to be searched
     */
    public SequentialSolver(Maze maze)
    {
        this.maze = maze;
        this.start = maze.start();
        initStructures();
    }

    /**
     * Initializes <code>visited</code>, <code>predecessor</code>, and
     * <code>frontier</code> with empty data structures for sequential
     * access.
     */
    protected void initStructures()
    {
        visited = new HashSet<>();
        predecessor = new HashMap<>();
        frontier = new Stack<>();
    }

    /**
     * The maze being searched.
     */
    protected Maze maze;

    /**
     * Number of steps (nodes to be visited) before forking. This is
     * set to <code>0</code> in <code>SequentialSolver</code>, which
     * means no forking.
     */
    protected int forkAfter = 0;

    /**
     * Set of identifiers of all nodes visited so far during the
     * search.
     */
    protected Set<Integer> visited;
    /**
     * If <code>(m -&gt; n)</code> is in <code>precedessor</code>, then
     * the node with identifier <code>n</code> has been first visited
     * from its neighbor node with identifier <code>m</code> during
     * the search.
     */
    protected Map<Integer, Integer> predecessor;
    /**
     * The nodes in the maze to be visited next. Using a stack
     * implements a search that goes depth first..
     */
    protected Stack<Integer> frontier;
    /**
     * The identifier of the node in the maze from where the search
     * starts.
     */
    protected int start;

    /**
     * Searches for and returns the path, as a list of node
     * identifiers, that goes from the start node to a goal node in
     * the maze. If such a path cannot be found (because there are no
     * goals, or all goals are unreacheable), the method returns
     * <code>null</code>.
     *
     * @return   the list of node identifiers from the start node to a
     *           goal node in the maze; <code>null</code> if such a path cannot
     *           be found
     */
    @Override
    public List<Integer> compute()
    {
        return depthFirstSearch();
    }

    private List<Integer> depthFirstSearch()
    {
        // one player active on the maze at start
        int player = maze.newPlayer(start);
        // start with start node
        frontier.push(start);
        // as long as not all nodes have been processed
        while (!frontier.empty()) {
            // get the new node to process
            int current = frontier.pop();
            // if current node has a goal
            if (maze.hasGoal(current)) {
                // move player to goal
                maze.move(player, current);
                // search finished: reconstruct and return path
                return pathFromTo(start, current);
            }
            // if current node has not been visited yet
            if (!visited.contains(current)) {
                // move player to current node
                maze.move(player, current);
                // mark node as visited
                visited.add(current);
                // for every node nb adjacent to current
                for (int nb: maze.neighbors(current)) {
                    // add nb to the nodes to be processed
                    frontier.push(nb);
                    // if nb has not been already visited,
                    // nb can be reached from current (i.e., current is nb's predecessor)
                    if (!visited.contains(nb))
                        predecessor.put(nb, current);
                }
            }
        }
        // all nodes explored, no goal found
        return null;
    }

    /**
     * Returns the connected path, as a list of node identifiers, that
     * goes from node <code>from</code> to node <code>to</code>
     * following the inverse of relation <code>predecessor</code>. If
     * such a path cannot be reconstructed from
     * <code>predecessor</code>, the method returns <code>null</code>.
     *
     * @param from   the identifier of the initial node on the path
     * @param to     the identifier of the final node on the path
     * @return       the list of node identifiers from <code>from</code> to
     *               <code>to</code> if such a path can be reconstructed from
     *               <code>predecessor</code>; <code>null</code> otherwise
     */
    protected List<Integer> pathFromTo(int from, int to) {
        List<Integer> path = new LinkedList<>();
        Integer current = to;
        while (current != from) {
            path.add(current);
            current = predecessor.get(current);
            if (current == null)
                return null;
        }
        path.add(from);
        Collections.reverse(path);
        return path;
    }
}
