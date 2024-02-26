package amazed.maze;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;


public class Board
{

    // after creation, read-only access (except for operation markPath)
    private Cell[][] board;
    private int nRows;
    private int nCols;

    // players currently on the board
    // player identifier --> player object
    private final Map<Integer, Player> players;
    // count of number of registered players, to ensure unique player ids
    private final AtomicInteger nPlayers = new AtomicInteger();

    // unique node id --> coordinate position of node on board
    // after creation, read-only access
    private Map<Integer, Position> idToCell;

    // empty board
    Board(int nRows, int nCols)
    {
        board = new Cell[nRows][nCols];
        this.nRows = nRows;
        this.nCols = nCols;
        players = new ConcurrentHashMap<>();
        idToCell = new HashMap<>();
    }

    // board from map `filename'
    Board(String filename)
    {
        try {
            readMap(filename);
        } catch (IOException e) {
            System.err.println("Error: cannot open map file " + filename);
            System.exit(1);
        }
        players = new ConcurrentHashMap<>();
    }

    Cell getCell(int row, int col)
    {
        return board[row][col];
    }

    Cell getCell(Position position)
    {
        return board[position.getRow()][position.getCol()];
    }

    Cell getCell(int id)
    {
        return getCell(idToCell.get(id));
    }

    Position getPosition(int id)
    {
        return idToCell.get(id);
    }

    int getWidth()
    {
        return nCols * board[0][0].getWidth();
    }

    int getHeight()
    {
        return nRows * board[0][0].getHeight();
    }

    int getRows()
    {
        return nRows;
    }

    int getCols()
    {
        return nCols;
    }

    List<Position> pathToPositions(List<Integer> path)
    {
        List<Position> positionPath = new ArrayList<>(path.size());
        for (int id: path)
            positionPath.add(getPosition(id));
        return positionPath;
    }

    // thread unsafe
    void markPath(List<Integer> path)
    {
        List<Position> positionPath = pathToPositions(path);
        for (Position position: positionPath) {
            int row = position.getRow(), col = position.getCol();
            board[row][col] = getCell(position).marked();
        }
    }

    private void readMap(String mapFile)
    throws FileNotFoundException, IOException
    {
        Cell cell;
        int row = 0, col = 0, nId = 0;
        List<Integer> ids = null;
        try (BufferedReader br = new BufferedReader(new FileReader(mapFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // remove whitespaces
                line = line.replaceAll("\\s", "");
                StringReader lineReader = new StringReader(line);
                int ch, id = 0;
                line_loop:
                while ((ch = lineReader.read()) != -1) {
                    switch (ch) {
                    case '@':
                        break line_loop;
                    case '$':
                        Pattern p = Pattern.compile("^\\$(\\d+),(\\d+)$");
                        Matcher m = p.matcher(line);
                        if (m.find()) {
                            nRows = Integer.parseInt(m.group(1));
                            nCols = Integer.parseInt(m.group(2));
                            board = new Cell[nRows][nCols];
                            int numCells = nRows*nCols;
                            ids = new ArrayList<>(2*numCells);
                            for (int i = -numCells; i < numCells; i++)
                                ids.add(i);
                            Collections.shuffle(ids);
                            idToCell = new HashMap<>(numCells);
                        }
                        break line_loop;
                    default:
                        if (row < nRows && col < nCols)
                            id = ids.get(nId++);
                        if (ch == Tile.EMPTY.getChar())
                            cell = new Cell(Tile.EMPTY, id);
                        else if (ch == Tile.SOLID.getChar())
                            cell = new Cell(Tile.SOLID, id);
                        else if (ch == Tile.BRICK.getChar())
                            cell = new Cell(Tile.BRICK, id);
                        else if (ch == Tile.HEART.getChar())
                            cell = new Cell(Tile.HEART, id);
                        else {
                            System.out.println("Unrecognized symbol " +
                                               Character.toString((char) ch) + " on " +
                                               "row " + row + " column " + col);
                            System.out.println("... using empty cell instead.");
                            cell = new Cell(Tile.EMPTY, id);
                        }
                    }
                    // Ignore rows and columns beyond the declared ones
                    if (row < nRows && col < nCols) {
                        board[row][col] = cell;
                        idToCell.put(id, new Position(row, col));
                        col += 1;
                    }
                }
                if (nCols > 0 && col == nCols) {
                    row += 1;
                    col = 0;
                }
            }
        }
    }

    String asText()
    {
        StringWriter result = new StringWriter(nRows*(2 + nCols*2));
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                result.append(' ');
                result.append(board[row][col].getText());
            }
            result.append('\n');
        }
        return result.toString();
    }

    // printable deep copy of the board with all players in consistent positions
    Board consistentBoard()
    {
        Board result = new Board(nRows, nCols);
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                Cell cell = board[row][col];
                result.board[row][col] = new Cell(cell.getTile(), cell.getId());
            }
        }
        for (Player player: players.values()) {
            Position pos = player.getPosition();
            Player newPlayer = new Player(player.getId(), player.getName());
            newPlayer.onBoard(result, pos.getRow(), pos.getCol());
        }
        return result;
    }

    // is the position row, col a valid position on the board?
    boolean isOnBoard(int row, int col)
    {
        return 0 <= row && row < nRows && 0 <= col && col < nCols;
    }

    boolean isAccessible(int row, int col)
    {
        return isOnBoard(row, col) && board[row][col].isAccessible();
    }

    Position move(Position position, Direction direction)
    {
        int newRow = position.getRow(), newCol = position.getCol();
        switch (direction) {
        case NORTH:
            newRow -= 1;
            break;
        case SOUTH:
            newRow += 1;
            break;
        case WEST:
            newCol -= 1;
            break;
        case EAST:
            newCol += 1;
            break;
        }
        if (isAccessible(newRow, newCol))
            return new Position(newRow, newCol);
        return null;
    }

    void register(Player player, int row, int col)
    {
        if (isOnBoard(row, col)) {
            board[row][col].add(player);
            players.put(player.getId(), player);
        }
    }

    int newPlayer(int id)
    {
        int playerId = nPlayers.getAndIncrement();
        Player player = new Player(playerId, "thread_" + playerId);
        player.onBoard(this, id);
        return playerId;
    }

    void deregister(Player player, int row, int col)
    {
        if (isOnBoard(row, col)) {
            board[row][col].remove(player);
            players.remove(player.getId());
        }
    }

    void deregisterAll()
    {
        for (Map.Entry<Integer, Player> entry: players.entrySet()) {
            Player player = entry.getValue();
            Position position = player.getPosition();
            deregister(player, position.getRow(), position.getCol());
        }
    }

    Player getPlayer(int playerId)
    {
        return players.get(playerId);
    }

    // move registered player from its current position to newRow, newCol
    void move(Player player, int newRow, int newCol)
    {
        int row = player.getRow();
        int col = player.getCol();
        if (isOnBoard(newRow, newCol) && players.containsKey(player.getId())) {
            board[row][col].remove(player);
            board[newRow][newCol].add(player);
            player.setRow(newRow);
            player.setCol(newCol);
        }
    }
}
