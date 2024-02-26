package amazed.maze;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.OverlayLayout;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;


class CellButton extends JPanel {

    private Cell cell;

    private Color visibleTextColor(Player player)
    {
        switch (player.getId()) {
        case 1:
            return Color.WHITE;
        case 2:
            return Color.RED;
        case 3:
        case 4:
            return Color.BLACK;
        default:
            return Color.RED;
        }
    }

    CellButton(Cell cell)
    {
        // super(new ImageIcon(cell.getImage()));
        this.cell = cell;
        setLayout(new OverlayLayout(this));
        setOpaque(false);

        JButton image = new JButton(new ImageIcon(cell.getImage()));
        image.setAlignmentX(JButton.CENTER_ALIGNMENT);
        image.setAlignmentY(JButton.CENTER_ALIGNMENT);

        Player[] players = cell.getPlayers();
        if (players.length > 1) {
            JButton text = new JButton();
            text.setText(Integer.toString(players.length));
            text.setMaximumSize(new Dimension(50,50));
            text.setAlignmentX(JButton.RIGHT_ALIGNMENT);
            text.setAlignmentY(JButton.BOTTOM_ALIGNMENT);
            text.setFont(new Font(null, Font.BOLD, 16));
            text.setForeground(visibleTextColor(players[0]));
            text.setBorder(null);
            text.setOpaque(false);
            text.setContentAreaFilled(false);
            text.setBorderPainted(false);
            add(text);
        }
        add(image);
    }

    @Override
    public boolean isOptimizedDrawingEnabled()
    {
        return false;
    }

    // preferred size of button, equal to image size
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(cell.getWidth(), cell.getHeight());
    }

}


class MazeFrame
    extends JFrame
    implements ActionListener
{

    private JPanel panel;
    private Board board;

    private Timer timer;
    private final int REFRESH_INTERVAL = 25;

    MazeFrame(Maze maze)
    {
        this.board = maze.getBoard();
        initBoard();
    }

    private void initBoard()
    {
        panel = new JPanel();
        panel.setLayout(new GridLayout(board.getRows(), board.getCols(), 0, 0));
        panel.setDoubleBuffered(true);

        // the following statements set up a <em>scollable</em> frame
        // to use a non-scrollable, replace all the following statements with:
        // add(panel, BorderLayout.CENTER);
        JScrollPane scrollFrame = new JScrollPane(panel);
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int scrWidth = Math.min(board.getWidth(), (int) screen.getWidth()) + 30;
        int scrHeight = Math.min(board.getHeight(), (int) screen.getHeight()) + 50;
        scrollFrame.setPreferredSize(new Dimension(scrWidth, scrHeight));
        add(scrollFrame);

        pack();
        setTitle("A-mazed");
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        timer = new Timer(REFRESH_INTERVAL, this);
        timer.start();
    }

    private void displayBoard()
    {
        panel.removeAll();
        Board b = board.consistentBoard();
        for (int row = 0; row < b.getRows(); row++) {
            for (int col = 0; col < b.getCols(); col++) {
                panel.add(new CellButton(b.getCell(row, col)));
            }
        }
        Toolkit.getDefaultToolkit().sync();
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        displayBoard();
    }
}
