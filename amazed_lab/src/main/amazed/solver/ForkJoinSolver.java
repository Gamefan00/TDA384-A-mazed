package amazed.solver;

import amazed.maze.Maze;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <code>ForkJoinSolver</code> implements a solver for
 * <code>Maze</code> objects using a fork/join multi-thread
 * depth-first search.
 * <p>
 * Instances of <code>ForkJoinSolver</code> should be run by a
 * <code>ForkJoinPool</code> object.
 */


public class ForkJoinSolver
    extends SequentialSolver
{
    // List that contains all the forkSolvers spawned
    private List<ForkJoinSolver> subSolutions = new ArrayList<ForkJoinSolver>();

    // Used when someone has reached the goal
    private static AtomicBoolean goalFound = new AtomicBoolean();

    // Override initStructures to create thread-safe structures
    @Override
    protected void initStructures() {
        visited = new ConcurrentSkipListSet<>();
        predecessor = new HashMap<>();
        frontier = new Stack<>();
    }

    /**
     * Creates a solver that searches in <code>maze</code> from the
     * start node to a goal.
     *
     * @param maze   the maze to be searched
     */
    public ForkJoinSolver(Maze maze)
    {
        super(maze);
    }

    /**
     * Creates a solver that searches in <code>maze</code> from the
     * start node to a goal, forking after a given number of visited
     * nodes.
     *
     * @param maze        the maze to be searched
     * @param forkAfter   the number of steps (visited nodes) after
     *                    which a parallel task is forked; if
     *                    <code>forkAfter &lt;= 0</code> the solver never
     *                    forks new tasks
     */
    public ForkJoinSolver(Maze maze, int forkAfter)
    {
        this(maze);
        this.forkAfter = forkAfter;
    }

    /**
     * Creates a solver that searches in <code>maze</code> from the 
     * start node to a goal. It forks after a given nr of visited nodes. 
     * Also makes sure that all the forked threads share the visited nodes
     * @param maze          the maze to be searched
     * @param forkAfter     the number of steps (visited nodes) after
        *                   which a parallel task is forked; if
        *                   <code>forkAfter &lt;= 0</code> the solver never
        *                   forks new tasks
     * @param startNode     start node for this forked thread
     * @param visited       set of nodes that are visited, shared among each other 
     */
    //todo not sure if it should be a set or the special concurrent set used above??
    public ForkJoinSolver (Maze maze, int forkAfter, int startNode, Set<Integer> visited) {
        this(maze, forkAfter);
        this.start = startNode;
        this.visited = visited;
    }

    /**
     * Searches for and returns the path, as a list of node
     * identifiers, that goes from the start node to a goal node in
     * the maze. If such a path cannot be found (because there are no
     * goals, or all goals are unreacheable), the method returns
     * <code>null</code>.
     *
     * @return   the list of node identifiers from the start node to a
     *           goal node in the maze; <code>null</code> if such a path cannot
     *           be found.
     */
    @Override
    public List<Integer> compute()
    {
        return parallelSearch();
    }

    private List<Integer> parallelSearch()
    {
        // one player per fork
        int player = maze.newPlayer(start);
        // start with start node
        frontier.push(start);
        // holds the number for how many moves the player has made since forking
        int nrMovesMade = 0;

        // as long as not all nodes have been processed and noone has found the goal
        while (!frontier.empty() && !goalFound.get()) {
            // get the new node to process
            int curr = frontier.pop();
            // Only handle the node if it hasn't been visited before
            if (visited.add(curr) || start == curr) {
                // check if current node has a goal
                if (maze.hasGoal(curr)) {
                    // set that goal is found
                    goalFound.set(true);
                    // move player to goal
                    maze.move(player, curr);
                    // one more move made
                    nrMovesMade++;
                    // search finished: reconstruct and return path
                    return pathFromTo(start, curr);
                }

                // move player to current node
                maze.move(player, curr);
                // one more move made
                nrMovesMade++;

                // TODO probably need to add a boolean here to make sure we aren't forkinng when only one way to take

                // for every node nb adjacent to current
                for (int nb: maze.neighbors(curr)) {
                    // nb can be reached from current, ie currennt is nb's predecessor
                    if (!visited.contains(nb)) {
                        predecessor.put(nb, curr);

                        // TODO check if first nb and then dont fork either??
                        // player should continue if it hasn't yet moved "forkAfter moves"
                        if (nrMovesMade < forkAfter) {
                            frontier.push(nb);
                        } else {
                            // if player has made at least forkafter moves, then a 
                            // new player should be created. 
                            
                            // this if checks again so no other thread reached to the node before
                            if (visited.add(nb)) {
                                // it is forked now so nr of moves made should be reset
                                nrMovesMade = 0; 
                                // create a new player
                                ForkJoinSolver forkedForkJoinSolver = new ForkJoinSolver(maze, forkAfter, nb, visited);
                                // add the new forked thread to the list with all the subsolutions
                                subSolutions.add(forkedForkJoinSolver);
                                // fork the new player
                                forkedForkJoinSolver.fork();
                            }
                        }
                    }
                }
            }
        }
        // ends up here if all nodes explored and didn't find a goal
        // TODO need to call some join method here for the subsolutions?!!
        return null;
    }
}
