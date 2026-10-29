import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;

/// # Úkol · Untangle
/// Lekce: 13.1 Kresli v okně Javy — 01-drawing.html
///
/// Rozmotej síť tažením očíslovaných bodů, až se její hrany přestanou křížit. Kolečka i spojnice se musí
/// shodnout, kde každý bod skutečně leží.
///
/// **Tvoje mise:** Vytvoř chybějící node renderer (kreslení uzlu). Nakresli tmavý plný kruh o
/// poloměru 13 se středem v point a potom bílé číslo v (point.x - 4, point.y + 4). Použij připravené
/// Graphics2D; neměň point ani síť.
///
/// Po spuštění programu tahej očíslované body a rozmotej křížení sítě; klávesou R ji můžeš
/// znovu promíchat.

void drawNode(Graphics2D g, Point point, int label) {
    // Zde nakresli uzel i číslo. Point čti, neměň jej.
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Dej uzlům osobnost: kresli čtverec, prstenec nebo malý obličej, ale zachovej střed a čitelné
/// číslo. S kamarádem zkuste stejnou zamotanou síť. Pomůže nový vzhled sledovat tažený uzel bez změny
/// pravidel?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 420;

int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 0}, {0, 2}, {2, 4}, {4, 6}, {6, 0}};
Point[] points = new Point[8];
int selected = -1;
void layout(boolean scrambled) {
    int[] order = scrambled ? new int[]{0, 4, 1, 5, 2, 6, 3, 7} : new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    for (int i = 0; i < 8; i++) {
        double a = order[i] * Math.PI / 4;
        points[i] = new Point(300 + (int)(160 * Math.cos(a)), 210 + (int)(160 * Math.sin(a)));
    }
}

void restart() {
    selected = -1;
    layout(true);
}

boolean crosses(int[] a, int[] b) {
    for (int x : a) {
        for (int y : b) {
            if (x == y) {
                return false;
            }
        }
    }
    return Line2D.linesIntersect(points[a[0]].x, points[a[0]].y, points[a[1]].x, points[a[1]].y,
    points[b[0]].x, points[b[0]].y, points[b[1]].x, points[b[1]].y);
}

int crossings() {
    int count = 0;
    for (int i = 0; i < edges.length; i++) {
        for (int j = i + 1; j < edges.length; j++) {
            if (crosses(edges[i], edges[j])) {
                count++;
            }
        }
    }
    return count;
}

void pick(int x, int y) {
    selected = -1;
    for (int i = 0; i < 8; i++) {
        if (points[i].distance(x, y)<15) {
            selected = i;
        }
    }
}

void moveSelected(int x, int y) {
    if (selected < 0) {
        return;
    }
    x = Math.max(18, Math.min(WIDTH - 18, x));
    y = Math.max(38, Math.min(HEIGHT - 18, y));
    for (int i = 0; i < 8; i++) {
        if (i != selected && points[i].distance(x, y)<25) {
            return;
        }
    }
    points[selected].setLocation(x, y);
}

void draw(Graphics2D g) {
    g.setStroke(new BasicStroke(2));
    for (int i = 0; i < edges.length; i++) {
        boolean crossed = false;
        for (int j = 0; j < edges.length; j++) {
            if (i != j && crosses(edges[i], edges[j])) {
                crossed = true;
            }
        }
        g.setColor(crossed ? new Color(220, 70, 70) : new Color(65, 155, 130));
        var a = points[edges[i][0]];
        var b = points[edges[i][1]];
        g.drawLine(a.x, a.y, b.x, b.y);
    }
    for (int i = 0; i < 8; i++) {
        drawNode(g, points[i], i);
    }
    g.setColor(Color.BLACK);
    int n = crossings();
    g.drawString(n == 0 ? "Untangled! R: scramble again" : "Crossing edge pairs: " + n, 15, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    pick(x, y);
}

void drag(int x, int y) {
    moveSelected(x, y);
}

void release(int x, int y) {
    selected = -1;
}

void showWindow() {
    var frame = new JFrame("Untangle");
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
        @Override public void mouseDragged(MouseEvent e) {
            drag(e.getX(), e.getY());
            panel.repaint();
        }
        @Override public void mouseReleased(MouseEvent e) {
            release(e.getX(), e.getY());
            panel.repaint();
        }
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag numbered nodes until every edge is green   R: restart</div></html>", SwingConstants.CENTER);
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
