import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.awt.geom.Area;

/// # Vyřešený úkol · ShadowPlayground
/// Lekce: 13.3 Dej událostem jeden jasný úkol — 03-handlers.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Rozpohybuj lampu po mřížce.** Lampa zatím nereaguje na kliknutí ani tažení. Překážky i
/// jejich stíny se už kreslí.
///
/// **Úkol:** Doplň odloženou akci v `lightAction` níže. Souřadnice každého kliknutí či tažení
/// zarovnej k předchozím násobkům 20 a předej je připravené `moveLight`. Například `(43, 91)` se
/// změní na `(40, 80)`. Akce má lampou pohnout až při události, nikoli při svém vytvoření.
///
/// **Výsledek:** Lampa bude přeskakovat po dvacetipixelových krocích a stíny se budou měnit.
/// `moveLight` ji dál udrží uvnitř hranic plátna; R obnoví počáteční pozici.

java.util.function.BiConsumer<Integer, Integer> lightAction() {
    return (x, y) -> {
        int snappedX = x / 20 * 20;
        int snappedY = y / 20 * 20;
        moveLight(snappedX, snappedY);
    };
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vymysli strašidelnou lampu: kliknutí ovládá její zrcadlový obraz přes svislý střed, místo přímé
/// polohy. Předpověz výsledek kliknutí vlevo a sestav ze stínů loutkovou scénu, kterou má kamarád
/// poznat.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

Rectangle[] obstacles = {new Rectangle(190, 130, 70, 100), new Rectangle(380, 220, 100, 60)};
double lightX, lightY;
void restart() {
    lightX = 100;
    lightY = 80;
}

void moveLight(int x, int y) {
    lightX = Math.max(0, Math.min(WIDTH, x));
    lightY = Math.max(0, Math.min(HEIGHT, y));
}

Area shadow(Rectangle obstacle) {
    if (obstacle.contains(lightX, lightY)) {
        return new Area(new Rectangle(0, 0, WIDTH, HEIGHT));
    }
    Point[] corners = {new Point(obstacle.x, obstacle.y), new Point(obstacle.x + obstacle.width, obstacle.y), new Point(obstacle.x + obstacle.width, obstacle.y + obstacle.height), new Point(obstacle.x, obstacle.y + obstacle.height)};
    var area = new Area(obstacle);
    for (int i = 0; i < 4; i++) {
        var a = corners[i];
        var b = corners[(i + 1) % 4];
        double da = Math.max(.001, a.distance(lightX, lightY)), db = Math.max(.001, b.distance(lightX, lightY));
        var path = new Path2D.Double();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(b.x + (b.x - lightX) / db * 1500, b.y + (b.y - lightY) / db * 1500);
        path.lineTo(a.x + (a.x - lightX) / da * 1500, a.y + (a.y - lightY) / da * 1500);
        path.closePath();
        area.add(new Area(path));
    }
    return area;
}

void draw(Graphics2D g) {
    g.setColor(new Color(255, 246, 205));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    var combined = new Area();
    boolean inside = false;
    for (var obstacle : obstacles) {
        combined.add(shadow(obstacle));
        if (obstacle.contains(lightX, lightY)) {
            inside = true;
        }
    }
    g.setColor(new Color(65, 75, 100, 160));
    g.fill(combined);
    g.setColor(new Color(45, 50, 65));
    for (var obstacle : obstacles) {
        g.fill(obstacle);
    }
    g.setColor(Color.ORANGE);
    g.fillOval((int)lightX - 10, (int)lightY - 10, 20, 20);
    g.setColor(Color.BLACK);
    g.drawString(inside ? "Light inside an obstacle - drag it outside" : "Drag the light around two opaque rectangles", 12, 23);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    lightCallback.accept(x, y);
}

void drag(int x, int y) {
    lightCallback.accept(x, y);
}

void showWindow() {
    var frame = new JFrame("Shadow Playground");
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
    };
    panel.addMouseListener(mouse);
    panel.addMouseMotionListener(mouse);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click / drag: move the light   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

java.util.function.BiConsumer<Integer, Integer> lightCallback = lightAction();

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
