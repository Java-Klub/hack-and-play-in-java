import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

/// # Solved task · InequalityLatin
/// Lesson: 9.4 Data can know their operations — 04-data-methods.html
///
/// Original assignment (this version is complete):
///
/// Fill a Latin square while obeying the little inequality signs between its cells. A correct row can
/// still lose to one tiny comparison.
///
/// **Your mission:** A displayed less-than clue accepts the opposite relationship. Repair
/// Rule.satisfiedBy() using each Rule’s own position.
///
/// Repeatedly click a free cell to cycle its number while checking the
/// inequality signs beside it.

record Rule(int x, int y) {
    boolean satisfiedBy(int[][] values) {
        return values[y][x] < values[y][x + 1];
    }
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 500, HEIGHT = 400;

Rule[] rules = {new Rule(0, 0), new Rule(2, 0), new Rule(1, 1), new Rule(0, 2), new Rule(1, 3), new Rule(2, 3)};
int[][] givens = {{1, 0, 0, 0}, {0, 0, 0, 1}, {0, 4, 0, 0}, {0, 0, 2, 0}};
int[][] cells;
void restart() {
    cells = Arrays.stream(givens).map(int[]::clone).toArray(int[][]::new);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 5;
    }
}

boolean won() {
    for (int i = 0; i < 4; i++) {
        boolean[] row = new boolean[5], col = new boolean[5];
        for (int j = 0; j < 4; j++) {
            int a = cells[i][j], b = cells[j][i];
            if (a < 1 || a > 4 || b < 1 || b > 4 || row[a] || col[b]) {
                return false;
            }
            if (givens[i][j] != 0 && a != givens[i][j]) {
                return false;
            }
            row[a] = true;
            col[b] = true;
        }
    }
    for (var r : rules) {
        if (!r.satisfiedBy(cells)) {
            return false;
        }
    }
    return true;
}

void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 85 + x * 72, yy = 45 + y * 72;
            g.setColor(givens[y][x] != 0 ? new Color(215, 225, 245) : new Color(240, 240, 240));
            g.fillRect(xx, yy, 52, 52);
            g.setColor(Color.BLACK);
            if (cells[y][x] != 0) {
                g.drawString("" + cells[y][x], xx + 19, yy + 34);
            }
        }
    }
    for (var r : rules) {
        g.drawString("<", 140 + r.x * 72, 79 + r.y * 72);
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "Latin square complete!" : "Use 1-4 once in each row and column; obey every <", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 45 && x < 373 && y < 333 && (x - 85) % 72 < 52 && (y - 45) % 72 < 52) {
        cycle((x - 85) / 72, (y - 45) / 72);
    }
}

void showWindow() {
    var frame = new JFrame("Inequality Latin");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a free cell: 1 / 2 / 3 / 4 / blank   R: reset</div></html>", SwingConstants.CENTER);
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
