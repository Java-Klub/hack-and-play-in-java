import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;

/// # Vyřešený úkol · MirrorLaser
/// Lekce: 9.4 Data mohou znát své operace — 04-data-methods.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Otáčej zrcadla a doveď laser k přijímači. Jediný špatný krok způsobí, že paprsek zdánlivě kráčí bokem.
///
/// **Tvoje mise:** Paprsek zaměňuje vodorovný a svislý pohyb. Oprav RayState.advance(), aby krok
/// odpovídal směru uloženému v paprsku.
///
/// Kliknutím otoč zrcadlo a mezerníkem nech paprsek projít tvým uspořádáním.

record RayState(int x, int y, int dx, int dy) {
    RayState advance() {
        return new RayState(x + dx, y + dy, dx, dy);
    }
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Předpověz hodnotu, ne pohyb objektu
///
/// Před úpravou napiš čtyři component (složky) paprsku v `(7,3)` se směrem `(-1,0)`. Předpověz je po
/// jediném advance: které popisují novou pozici a které stále směr? Pořadí constructor argument
/// (argumentů konstruktoru) sleduje declaration (deklaraci) record. Potom vysvětli, proč jiná
/// variable (proměnná) odkazující na předchozí RayState musí zachovat původní složky. Popis další
/// hodnoty nepotřebuje field mutation (změnu položky objektu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 480, HEIGHT = 420;

int[][] mirrors;
ArrayList<Point> beam = new ArrayList<>();
String result;
void restart() {
    mirrors = new int[6][6];
    mirrors[2][2] = 2;
    mirrors[0][2] = 2;
    mirrors[0][5] = 1;
    beam.clear();
    result = "Rotate the three mirrors, then trace";
}

String traceFrom(int x, int y, int dx, int dy) {
    var seen = new HashSet<RayState>();
    beam.clear();
    var ray = new RayState(x, y, dx, dy);
    while (ray.x() >= 0 && ray.y() >= 0 && ray.x() < 6 && ray.y() < 6) {
        if (!seen.add(ray)) {
            return "Loop detected";
        }
        beam.add(new Point(ray.x(), ray.y()));
        if (ray.x() == 5 && ray.y() == 5) {
            return "Target reached!";
        }
        int mirror = mirrors[ray.y()][ray.x()];
        int nextDx = ray.dx(), nextDy = ray.dy();
        if (mirror == 1) {
            nextDx = -ray.dy();
            nextDy = -ray.dx();
        }
        if (mirror == 2) {
            nextDx = ray.dy();
            nextDy = ray.dx();
        }
        ray = new RayState(ray.x(), ray.y(), nextDx, nextDy).advance();
    }
    beam.add(new Point(ray.x(), ray.y()));
    return "Beam escaped - try another rotation";
}

void trace() {
    result = traceFrom(0, 2, 1, 0);
}

void rotate(int x, int y) {
    if (x >= 0 && x < 6 && y >= 0 && y < 6 && mirrors[y][x] != 0) {
        mirrors[y][x] = 3 - mirrors[y][x];
        beam.clear();
        result = "Mirror rotated";
    }
}

void draw(Graphics2D g) {
    g.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i <= 6; i++) {
        g.drawLine(70 + i * 52, 35, 70 + i * 52, 347);
        g.drawLine(70, 35 + i * 52, 382, 35 + i * 52);
    }
    g.setStroke(new BasicStroke(4));
    for (int y = 0; y < 6; y++) {
        for (int x = 0; x < 6; x++) {
            if (mirrors[y][x] != 0) {
                g.setColor(Color.BLUE);
                int xx = 70 + x * 52, yy = 35 + y * 52;
                if (mirrors[y][x] == 1) {
                    g.drawLine(xx + 8, yy + 44, xx + 44, yy + 8);
                }
                else {
                    g.drawLine(xx + 8, yy + 8, xx + 44, yy + 44);
                }
            }
        }
    }
    g.setColor(Color.RED);
    g.drawString("SOURCE >", 4, 171);
    g.setColor(new Color(40, 170, 80));
    g.fillOval(70 + 5 * 52 + 15, 35 + 5 * 52 + 15, 22, 22);
    g.setColor(Color.ORANGE);
    g.setStroke(new BasicStroke(2));
    int xx = 44, yy = 165;
    for (var p : beam) {
        int nx = 96 + p.x * 52, ny = 61 + p.y * 52;
        g.drawLine(xx, yy, nx, ny);
        xx = nx;
        yy = ny;
    }
    g.setColor(Color.BLACK);
    g.drawString(result, 40, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        trace();
    }
}

void press(int x, int y, int button) {
    if (x >= 70 && y >= 35 && x < 382 && y < 347) {
        rotate((x - 70) / 52, (y - 35) / 52);
    }
}

void showWindow() {
    var frame = new JFrame("Mirror Laser");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click mirrors: rotate   Space: trace beam   R: restart</div></html>", SwingConstants.CENTER);
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
