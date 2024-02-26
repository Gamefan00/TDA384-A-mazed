package amazed;

import java.lang.invoke.MethodHandles;

import amazed.maze.Amazed;


public class Main
{
    private static void printUsageAndExit()
    {
        String className = MethodHandles.lookup().lookupClass().getName();
        System.out.println("A-mazed: finds and displays paths to goal in a maze.\n"
                           + "\n"
                           + "usage: java " + className + " MAP [SOLVER] [PERIOD]\n"
                           + "\n"
                           + " MAP    filename with map file\n"
                           + " SOLVER 'sequential' or 'parallel-N' solver, forking after N steps\n"
                           + " PERIOD time in millisecond between steps (0: don't animate)");
        System.exit(0);
    }

    private final static String SEQUENTIAL = "sequential";
    private final static String PARALLEL = "parallel";

    private static String map;
    private static boolean sequential = true;
    private static int forkAfter = 0;
    private static int period = 500;

    private static void parseArguments(String[] args)
    {
        if (args.length >= 1) {
            map = args[0];
            if (args.length >= 2) {
                String solver = args[1];
                if (solver.equals(SEQUENTIAL))
                    sequential = true;
                else {
                    sequential = false;
                    String[] splitSolver = solver.split("-");
                    if (splitSolver.length == 2) {
                        if (splitSolver[0].equals(PARALLEL)) {
                            try {
                                forkAfter = Integer.parseInt(splitSolver[1]);
                            } catch (NumberFormatException e) {
                                printUsageAndExit();
                            }
                        } else
                            printUsageAndExit();
                    } else
                        printUsageAndExit();
                }
                if (args.length >= 3) {
                    try {
                        period = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        printUsageAndExit();
                    }
                }
            }
        } else
            printUsageAndExit();
    }

    public static void main(String[] args)
    throws InterruptedException
    {
        parseArguments(args);
        Amazed amazed = new Amazed(map, sequential, forkAfter, period);
        long start = System.currentTimeMillis();
        amazed.solve();
        long stop = System.currentTimeMillis();
        long elapsed = stop - start;
        System.out.println("Solving time: " + elapsed + " ms");
        Thread.sleep(1000);
        amazed.showSolution();
    }
}
