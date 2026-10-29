import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

/// # Solved task · ToadsAndFrogs
/// Lesson: 5.5 Combine small referees — 05-composition.html
///
/// Original assignment (this version is complete):
///
/// Swap two groups of creatures across a single gap. Toads travel right, frogs travel left, and a jump
/// may cross one opponent, so one careless step can block the whole puzzle.
///
/// **Your mission · Compose a movement rule:** Write two connected rules. direction returns +1 for toad 1
/// and -1 for frog 2.
/// destination must call that rule and calculate origin plus the signed distance. The prepared move
/// asks it for both a one-cell step and a two-cell jump; do not move any creature inside these
/// calculations.
///
/// Run the program and press 5 to ask the frog beside the initial gap to move left; R restores
/// the starting arrangement.

int direction(int piece) {
    if (piece == 1) {
        return 1;
    } else {
        return -1;
    }
}

int destination(int origin, int distance, int piece) {
    int step = direction(piece);
    return origin + distance * step;
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// After solving the standard swap, change only the initial creature arrangement in restart to make a
/// shorter puzzle using the same seven cells. Give it to a friend with a claimed move limit. Check
/// that your own puzzle really has a solution before setting the challenge.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 330;

int[] cells;
int moves;
void restart() {
    cells = new int[]{1, 1, 1, 0, 2, 2, 2};
    moves = 0;
}

boolean move(int index) {
    if (index < 0 || index >= 7 || cells[index] == 0 || won()) {
        return false;
    }
    int piece = cells[index];
    int step = destination(index, 1, piece);
    int jump = destination(index, 2, piece);
    int destination = -1;
    if (step >= 0 && step < 7 && cells[step] == 0) {
        destination = step;
    }
    else {
        if (jump >= 0 && jump < 7 && cells[step] == 3 - piece && cells[jump] == 0) {
            destination = jump;
        }
    }
    if (destination < 0) {
        return false;
    }
    cells[destination] = piece;
    cells[index] = 0;
    moves++;
    return true;
}

boolean won() {
    return Arrays.equals(cells, new int[]{2, 2, 2, 0, 1, 1, 1});
}

void draw(Graphics2D g) {
    for (int i = 0; i < 7; i++) {
        int x = 35 + i * 75;
        g.setColor(new Color(220, 235, 205));
        g.fillOval(x, 170, 65, 35);
        if (cells[i] != 0) {
            g.setColor(cells[i] == 1 ? new Color(60, 150, 100) : new Color(80, 130, 210));
            g.fillOval(x + 9, 130, 46, 46);
            g.setColor(Color.WHITE);
            g.drawString(cells[i] == 1 ? ">" : "<", x + 27, 158);
        }
        g.setColor(Color.BLACK);
        g.drawString("" + (i + 1), x + 29, 235);
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Groups swapped in " + moves + " moves!" : "Swap both groups. Step into the gap, or jump one opponent.", 20, 60);
    g.drawString("Forward only. Stuck? R starts again.", 20, 285);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_7) {
        move(code - KeyEvent.VK_1);
    }
}

void press(int x, int y, int button) {
    if (x >= 35 && x < 560 && y >= 125 && y <= 210) {
        move((x - 35) / 75);
    }
}

void showWindow() {
    var frame = new JFrame("Toads and Frogs");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a piece or press 1-7   Toads &gt; right, frogs &lt; left   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

// </editor-fold>
