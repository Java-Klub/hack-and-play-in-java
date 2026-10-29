import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

/// # Solved task · BinaryBalance
/// Lesson: 8.5 Read the past; write the future — 05-generations.html
///
/// Original assignment (this version is complete):
///
/// Fill a small board with balanced black and white cells without making a run of three. The orange clues
/// should stay fixed while your answers stay editable.
///
/// **Your mission:** A free cell locks after you edit it because answers also change the clue template.
/// Repair copyBoard() to separate both levels of the grid.
///
/// Run the program and click a non-orange cell repeatedly to switch between unknown, black, and
/// white while following the balance rules.

int[][] copyBoard(int[][] original) {
    int[][] copy = new int[original.length][];
    for (int row = 0; row < original.length; row++) {
        copy[row] = original[row].clone();
    }
    return copy;
}

// <editor-fold defaultstate="expanded" desc="For this game">
/// ### Copying rows as well as the outside
///
/// A grid's outer array stores row references. An outer clone therefore copies references to the same
/// rows. Here is a separate two-row example with independent rows:
///
/// ```java
/// int[][] original = {{1}, {2}};
/// int[][] copied = {original[0].clone(), original[1].clone()};
/// copied[0][0] = 9;
/// IO.println(original[0][0]);
/// ```
///
/// The output stays one because the changed row belongs to copied. Predict the result if its first
/// row were merely `original[0]`. Your board has more rows: use the actual row count to repeat the
/// copying operation. Each row's clone preserves its current elements. Explain which layer would
/// remain shared if only the outside were copied.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Choose a balanced finished grid and decide which cells become fixed orange clues. Try to leave a
/// choice that needs more than avoiding three equal cells.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 480, HEIGHT = 400;

int[][] givens = {{1, 0, 0, 2}, {0, 2, 0, 0}, {0, 0, 2, 0}, {2, 0, 0, 1}};
int[][] cells;
void restart() {
    cells = copyBoard(givens);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 3;
    }
}

boolean lineValid(int[] line) {
    int ones = 0;
    for (int i = 0; i < 4; i++) {
        if (line[i]<1 || line[i]>2) {
            return false;
        }
        if (line[i] == 1) {
            ones++;
        }
        if (i >= 2 && line[i] == line[i - 1] && line[i] == line[i - 2]) {
            return false;
        }
    }
    return ones == 2;
}

boolean won() {
    for (int y = 0; y < 4; y++) {
        if (!lineValid(cells[y])) {
            return false;
        }
        int[] column = new int[4];
        for (int x = 0; x < 4; x++) {
            column[x] = cells[x][y];
            if (givens[y][x] != 0 && cells[y][x] != givens[y][x]) {
                return false;
            }
        }
        if (!lineValid(column)) {
            return false;
        }
    }
    return true;
}

void draw(Graphics2D g) {
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 90 + x * 65, yy = 45 + y * 65;
            g.setColor(new Color(205, 210, 220));
            g.fillRect(xx, yy, 62, 62);
            if (cells[y][x] != 0) {
                g.setColor(cells[y][x] == 1 ? Color.BLACK : Color.WHITE);
                g.fillOval(xx + 11, yy + 11, 40, 40);
            }
            if (givens[y][x] != 0) {
                g.setColor(new Color(225, 100, 45));
                g.drawRect(xx + 2, yy + 2, 58, 58);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Balanced!" : "Two black and two white in every row and column", 20, 345);
    g.drawString("No three alike in a row. Orange borders are fixed.", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 90 && y >= 45 && x < 350 && y < 305) {
        cycle((x - 90) / 65, (y - 45) / 65);
    }
}

void showWindow() {
    var frame = new JFrame("Binary Balance");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: unknown / black / white   Orange cells are fixed   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
