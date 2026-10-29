import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

/// # Solved task · MagnetDominoes
/// Lesson: 7.5 A memory is not a second name — 05-copies.html
///
/// Original assignment (this version is complete):
///
/// Choose the polarity of magnetic dominoes to satisfy the row and column clues. A quick undo lets you
/// test a different arrangement safely.
///
/// **Your mission:** Undo keeps the last changed polarity instead of restoring the previous one. Repair
/// copyState() so the saved domino states are independent.
///
/// Run the program, click a domino to change its polarity, and press U to try restoring the
/// previous selection.

int[] copyState() {
    return state.clone();
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Copy values, not a later screen image
///
/// Each domino polarity is an int in one array. Its clone copies those integers into separate
/// storage. No row objects or nested lists need copying here. Predict the saved polarity after
/// changing the live domino twice; it should still describe the moment the snapshot was taken. The
/// supplied undo replaces the working state from that earlier data. Explain why repainting cannot
/// repair shared storage.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Choose a complete polarity arrangement, derive its row and column clues, then remove the answers for
/// a partner. Check that undo also restores a deliberately bad guess.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 520, HEIGHT = 400;

int[] state;
int[] rowPlus = {1, 2, 1, 2}, rowMinus = {1, 2, 1, 2}, colPlus = {1, 2, 1, 2}, colMinus = {2, 1, 2, 1};
void restart() {
    state = new int[8];
    previousState = null;
    Arrays.fill(state, -1);
}

int pole(int x, int y) {
    int s = state[y * 2 + x / 2];
    if (s <= 0) {
        return 0;
    }
    int first = s == 1 ? 1 : -1;
    return x % 2 == 0 ? first : -first;
}

void cycle(int domino) {
    if (domino >= 0 && domino < 8) {
        previousState = copyState();
        state[domino] = state[domino] == -1 ? 1 : state[domino] == 1 ? 2 : state[domino] == 2 ? 0 : -1;
    }
}

boolean won() {
    for (int s : state) {
        if (s < 0) {
            return false;
        }
    }
    int[] cp = new int[4], cm = new int[4];
    for (int y = 0; y < 4; y++) {
        int plus = 0, minus = 0;
        for (int x = 0; x < 4; x++) {
            int p = pole(x, y);
            if (p == 1) {
                plus++;
                cp[x]++;
            }
            if (p == -1) {
                minus++;
                cm[x]++;
            }
            if (p != 0 && ((x < 3 && p == pole(x + 1, y)) || (y < 3 && p == pole(x, y + 1)))) {
                return false;
            }
        }
        if (plus != rowPlus[y] || minus != rowMinus[y]) {
            return false;
        }
    }
    return Arrays.equals(cp, colPlus) && Arrays.equals(cm, colMinus);
}

void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    for (int i = 0; i < 4; i++) {
        g.setColor(Color.BLACK);
        g.drawString(colPlus[i] + "/" + colMinus[i], 109 + i * 60, 35);
        g.drawString(rowPlus[i] + "/" + rowMinus[i], 35, 85 + i * 60);
    }
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 95 + x * 60, yy = 50 + y * 60;
            int s = state[y * 2 + x / 2], p = pole(x, y);
            g.setColor(s < 0 ? new Color(225, 225, 230) : p == 1 ? new Color(255, 200, 190) : p == -1 ? new Color(190, 215, 255) : new Color(240, 240, 220));
            g.fillRect(xx, yy, 58, 58);
            g.setColor(Color.BLACK);
            g.drawString(s < 0 ? "?" : p == 1 ? "+" : p == -1 ? "-" : "·", xx + 22, yy + 35);
        }
    }
    g.setStroke(new BasicStroke(3));
    g.setColor(Color.DARK_GRAY);
    for (int y = 0; y < 4; y++) {
        for (int d = 0; d < 2; d++) {
            g.drawRect(95 + d * 120, 50 + y * 60, 118, 58);
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "All poles balanced!" : "Edge numbers are + / - counts. Equal poles cannot touch.", 20, 345);
    g.drawString("? is undecided; · is a deliberately neutral domino.", 20, 375);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_U) {
        undo();
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 95 && y >= 50 && x < 335 && y < 290) {
        cycle(((y - 50) / 60) * 2 + (x - 95) / 120);
    }
}

void showWindow() {
    var frame = new JFrame("Magnet Dominoes");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a domino: +\u2212 / \u2212+ / neutral / ?<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
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

int[] previousState;

void undo() {
    if (previousState != null) {
        state = previousState;
        previousState = null;
    }
}
// </editor-fold>
