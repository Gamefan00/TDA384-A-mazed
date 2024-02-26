package amazed.maze;

import java.awt.EventQueue;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import amazed.solver.SequentialSolver;
import amazed.solver.ForkJoinSolver;

/**
 * <code>Amazed</code> is a simple application class that applies a
 * solver to a maze.
 * <p>
 * This class supports sequential solvers of class
 * <code>SequentialSolver</code> and fork/join solvers of class
 * <code>ForkJoinSolver</code>. It runs both using the common pool of
 * <code>java.util.concurrent.ForkJoinPool</code>; thus, the solvers
 * must be a subtype of
 * <code>RecursiveTask&lt;List&lt;Integer&gt;&gt;</code>. After creating an
 * instance from a map file, the solving process is started by calling
 * method <code>solve</code>. After <code>solve</code> terminates, the
 * solution can be displayed by calling method
 * <code>showSolution</code>.
 *
 * @author  Carlo A. Furia
 */

public class Amazed
{
    private Maze maze;
    private RecursiveTask<List<Integer>> solver;
    private List<Integer> path;

    /**
     * Creates a maze reading from map file <code>map</code>.
     *
     * @param map              the name of the map file describing the maze to be searched
     * @param sequentialSolver if <code>true</code>, it uses
     *                         <code>SequentialSolver</code> to search the maze; otherwise it
     *                         uses <code>ForkJoinSolver</code>
     * @param forkAfter        the number of steps (visited nodes) after
     *                         which a parallel task is forked; this value
     *                         is passed to the instance of the solver as
     *                         described in
     *                         {@link amazed.solver.ForkJoinSolver#ForkJoinSolver(Maze, int)}
     * @param animationDelay   milliseconds of pause between a step and
     *                         the next one in the animation of the
     *                         solution search; if
     *                         <code>animationDelay &lt;= 0</code>
     *                         then there is no graphical animation
     *                         and no spurious delays; if
     *                         <code>animationDelay &lt; 0</code> then
     *                         there is no graphical display at all
     */
    public Amazed(String map, boolean sequentialSolver, int forkAfter, int animationDelay)
    {
        maze = new Maze(map);
        if (animationDelay >= 0) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MazeFrame ex = new MazeFrame(maze);
                    ex.setVisible(true);
                }
            });
        }
        maze.setDelay(animationDelay);
        if (sequentialSolver)
            solver = new SequentialSolver(maze);
        else
            solver = new ForkJoinSolver(maze, forkAfter);
    }

    /**
     * Runs the solver on the maze, waits for termination, and prints
     * to screen the outcome of the search.
     */
    public void solve()
    {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        path = pool.invoke(solver);
        if (path != null && maze.isValidPath(path))
            System.out.println("Goal found :-D");
        else
            System.out.println("Search completed: no goal found :-(");
        pool.shutdown();
    }

    /**
     * Displays the solution by removing all players and marking a
     * path from the start node to a goal on the maze graphical
     * representation. The method only removes the players if no
     * solution has been found.
     */
    public void showSolution()
    {
        maze.removePlayers();
        if (path != null) {
            maze.markPath(path);
        }
    }
}

