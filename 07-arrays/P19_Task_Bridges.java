import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

/// # Solved task · Bridges
/// Lesson: 7.5 A memory is not a second name — 05-copies.html
///
/// Original assignment (this version is complete):
///
/// The bridge puzzle has a working one-use undo. Turn it into a two-way time lens: compare the last two
/// versions repeatedly before committing to a route.
///
/// **Your mission:** After a real bridge edit, every press of U must alternate between its before and
/// after states, for any number of presses. Each snapshot stays independent of future edits. A new edit
/// establishes a new pair; R clears the pair and U then does nothing. Preserve the puzzle rules and
/// bridge counts.
///
/// Click one faint connection, then press U several times. The starter restores it once; the new time
/// lens alternates the two pictures on every press. R restarts the puzzle.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
final int WIDTH = 470, HEIGHT = 420;

Point[] islands = {new Point(0, 0), new Point(2, 0), new Point(4, 0), new Point(0, 2), new Point(4, 2), new Point(0, 4), new Point(2, 4), new Point(4, 4)};
int[][] edges = {{0, 1}, {1, 2}, {0, 3}, {2, 4}, {3, 5}, {4, 7}, {5, 6}, {6, 7}, {1, 6}, {3, 4}};
int[] counts = {2, 2, 2, 2, 2, 2, 2, 2};
int[] bridges;
void restart() {
    bridges = new int[edges.length];
    previousBridges = null;
}

boolean crossing(int a, int b) {
    var e = edges[a];
    var f = edges[b];
    for (int x : e) {
        for (int y : f) {
            if (x == y) {
                return false;
            }
        }
    }
    return Line2D.linesIntersect(islands[e[0]].x, islands[e[0]].y, islands[e[1]].x, islands[e[1]].y,
    islands[f[0]].x, islands[f[0]].y, islands[f[1]].x, islands[f[1]].y);
}

boolean won() {
    int[] actual = new int[8];
    boolean[] reached = new boolean[8];
    reached[0] = true;
    for (int i = 0; i < edges.length; i++) {
        if (bridges[i]>0) {
            if (bridges[i]>2) {
                return false;
            }
            actual[edges[i][0]] += bridges[i];
            actual[edges[i][1]] += bridges[i];
            for (int j = 0; j < i; j++) {
                if (bridges[j]>0 && crossing(i, j)) {
                    return false;
                }
            }
        }
    }
    if (!Arrays.equals(actual, counts)) {
        return false;
    }
    for (int pass = 0; pass < 8; pass++) {
        for (int i = 0; i < edges.length; i++) {
            if (bridges[i]>0 && (reached[edges[i][0]] || reached[edges[i][1]])) {
                reached[edges[i][0]] = reached[edges[i][1]] = true;
            }
        }
    }
    for (boolean r : reached) {
        if (!r) {
            return false;
        }
    }
    return true;
}

int[] snapshot() {
    return bridges.clone();
}

void choose(int x, int y) {
    double best = 14;
    int chosen = -1;
    for (int i = 0; i < edges.length; i++) {
        var a = islands[edges[i][0]];
        var b = islands[edges[i][1]];
        double distance = Line2D.ptSegDist(70 + a.x * 75, 45 + a.y * 75, 70 + b.x * 75, 45 + b.y * 75, x, y);
        if (distance < best) {
            best = distance;
            chosen = i;
        }
    }
    if (chosen >= 0) {
        previousBridges = snapshot();
        bridges[chosen] = (bridges[chosen] + 1) % 3;
    }
}

void draw(Graphics2D g) {
    for (int i = 0; i < edges.length; i++) {
        var a = islands[edges[i][0]];
        var b = islands[edges[i][1]];
        int ax = 70 + a.x * 75, ay = 45 + a.y * 75, bx = 70 + b.x * 75, by = 45 + b.y * 75;
        g.setColor(bridges[i] == 0 ? new Color(225, 225, 225) : new Color(50, 110, 155));
        g.setStroke(new BasicStroke(bridges[i] == 0 ? 1 : 3));
        if (bridges[i]<2) {
            g.drawLine(ax, ay, bx, by);
        }
        else {
            if (ax == bx) {
                g.drawLine(ax - 4, ay, bx - 4, by);
                g.drawLine(ax + 4, ay, bx + 4, by);
            }
            else {
                g.drawLine(ax, ay - 4, bx, by - 4);
                g.drawLine(ax, ay + 4, bx, by + 4);
            }
        }
    }
    for (int i = 0; i < 8; i++) {
        int x = 70 + islands[i].x * 75, y = 45 + islands[i].y * 75;
        g.setColor(Color.WHITE);
        g.fillOval(x - 17, y - 17, 34, 34);
        g.setColor(Color.BLACK);
        g.drawOval(x - 17, y - 17, 34, 34);
        g.drawString("" + counts[i], x - 4, y + 5);
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All islands joined!" : "Match numbers and join one network; no crossings", 20, 390);
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
    choose(x, y);
}

void showWindow() {
    var frame = new JFrame("Bridges");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a faint edge: 0 / 1 / 2 bridges<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
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

int[] previousBridges;

void undo() {
    if (previousBridges != null) {
        int[] current = snapshot();
        bridges = previousBridges;
        previousBridges = current;
    }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Use the time lens to compare two plausible links on a tiny island layout you design. Explain which
/// version strands an island and why.
// </editor-fold>
