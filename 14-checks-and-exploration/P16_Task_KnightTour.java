import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Task · KnightTour
/// Lesson: 14.3 Ask which kind of data arrived — 03-patterns.html
///
/// Tour a small board with a chess knight, landing on each square at most once. A legal move should
/// arrive at the square you clicked, not its reflection across the diagonal.
///
/// **Your mission:** Build apply as the complete command dispatcher. A Move inside the 5-by-5 board
/// must call move with its carried column and row; an outside Move returns false. Restart must
/// restore the board and yield true. Let move own knight legality and visited cells.

boolean apply(Command command) {
    // Build the exhaustive command switch here.
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Design a different rider: change the legal-move predicate to a camel that moves three squares
/// along one axis and one along the other. Keep Move/Restart dispatch unchanged. Can you visit as
/// many distinct cells on the same board?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Component order carries meaning
///
/// The declaration `Move(int x, int y)` gives its components an order. Matching a Move created with
/// values 2 and 1 binds xx to 2 and yy to 1; the local names do not exchange those values. Before
/// running, predict the requested square and compare it with the square passed to move. On this
/// board, exchanging the coordinates can still be a legal knight jump, so merely accepting a move is
/// not enough evidence. Also trace an off-board Move: it should reach the rejecting alternative
/// without changing the board. Restart is a different alternative, not a pair of coordinates.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 520, HEIGHT = 420;

int[][] visited;
int x, y, count;
void restart() {
    visited = new int[5][5];
    x = y = 0;
    count = 1;
    visited[0][0] = 1;
}

boolean legal(int xx, int yy) {
    return xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && visited[yy][xx] == 0 && Math.abs(xx - x) * Math.abs(yy - y) == 2;
}

boolean move(int xx, int yy) {
    if (!legal(xx, yy)) {
        return false;
    }
    x = xx;
    y = yy;
    visited[y][x] = ++count;
    return true;
}

boolean won() {
    return count == 25;
}

void draw(Graphics2D g) {
    for (int yy = 0; yy < 5; yy++) {
        for (int xx = 0; xx < 5; xx++) {
            int px = 100 + xx * 60, py = 35 + yy * 60;
            g.setColor((xx + yy) % 2 == 0 ? new Color(235, 225, 205) : new Color(135, 155, 145));
            g.fillRect(px, py, 60, 60);
            if (legal(xx, yy)) {
                g.setColor(new Color(75, 190, 120));
                g.fillOval(px + 22, py + 22, 16, 16);
            }
            if (visited[yy][xx]>0) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("" + visited[yy][xx], px + 23, py + 35);
            }
        }
    }
    g.setColor(new Color(65, 75, 200));
    g.drawOval(105 + x * 60, 40 + y * 60, 50, 50);
    g.setColor(Color.BLACK);
    boolean canContinue = false;
    for (int row = 0; row < 5; row++) {
        for (int column = 0; column < 5; column++) canContinue |= legal(column, row);
    }
    g.drawString(won() ? "All 25 squares visited! R: replay" : !canContinue ? "No legal moves remain. R: try a new tour" : "Visited " + count + " / 25. Green dots are legal next moves.", 20, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        apply(new Restart());
    }
}

void press(int x, int y, int button) {
    if (x >= 100 && y >= 35 && x < 400 && y < 335) {
        apply(new Move((x - 100) / 60, (y - 35) / 60));
    }
}

void showWindow() {
    var frame = new JFrame("Knight Tour");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    var mouse = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            panel.requestFocusInWindow();
            press(e.getX(), e.getY(), e.getButton());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click a green legal move   Visit every square once   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

sealed interface Command permits Move, Restart {}
record Move(int x, int y) implements Command {}
record Restart() implements Command {}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
