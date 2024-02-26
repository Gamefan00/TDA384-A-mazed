JAVAC = javac
JAVA = java
MAIN_CP = src/main
MAIN_SOURCEPATH = $(MAIN_CP)/amazed
MAZE_SOURCEPATH = $(MAIN_SOURCEPATH)/maze
SOLVER_SOURCEPATH = $(MAIN_SOURCEPATH)/solver

MAIN_CLASS = amazed.Main

MAZE_SOURCES = MazeFrame.java Board.java Cell.java Player.java Position.java Direction.java Tile.java ImageFactory.java Maze.java Amazed.java
SOLVER_SOURCES = SequentialSolver.java ForkJoinSolver.java
MAIN_SOURCES = Main.java 

SOURCE_FILES = $(MAZE_SOURCES:%=$(MAZE_SOURCEPATH)/%) \
					$(SOLVER_SOURCES:%=$(SOLVER_SOURCEPATH)/%) \
					$(MAIN_SOURCES:%=$(MAIN_SOURCEPATH)/%)

MAPS_DIR = maps

compile: $(SOURCE_FILES)
	$(JAVAC) $^

sequential_small: compile
	$(JAVA) -cp $(MAIN_CP) $(MAIN_CLASS) $(MAPS_DIR)/small.map sequential

sequential_medium: compile
	$(JAVA) -cp $(MAIN_CP) $(MAIN_CLASS) $(MAPS_DIR)/medium.map sequential

parallel_small_step3: compile
	$(JAVA) -cp $(MAIN_CP) $(MAIN_CLASS) $(MAPS_DIR)/small.map parallel-3

parallel_small_step9: compile
	$(JAVA) -cp $(MAIN_CP) $(MAIN_CLASS) $(MAPS_DIR)/small.map parallel-9

parallel_medium_step3: compile
	$(JAVA) -cp $(MAIN_CP) $(MAIN_CLASS) $(MAPS_DIR)/medium.map parallel-3

parallel_medium_step9: compile
	$(JAVA) -cp $(MAIN_CP) $(MAIN_CLASS) $(MAPS_DIR)/medium.map parallel-9

.PHONY: compile

