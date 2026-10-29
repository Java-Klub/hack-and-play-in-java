import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/// # Vyřešený úkol · StarTunnel
/// Lekce: 13.1 Kresli v okně Javy — 01-drawing.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Prolétej tunelem hvězd a řiď jeho úběžník. Blízké hvězdy mají uhánět ven, zatímco vzdálené se shlukují
/// u středu.
///
/// **Tvoje mise:** Dodatečný posun hloubky kreslí hvězdy v nesprávném měřítku. Obnov použití požadované
/// hloubky a zachovej ochranu před nebezpečným dělením u blízké roviny.
///
/// Po spuštění programu kliknutím nebo tažením spusť a řiď hvězdný tunel; Space pozastaví pohyb
/// a R obnoví výchozí pohled.

Point2D.Double project(Star star, double depth) {
    double z = Math.max(5, depth);
    return new Point2D.Double(centreX + star.x * 260 / z, centreY + star.y * 260 / z);
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Proměň let v déšť nebo sněžení změnou kreslení promítnutých hvězd. Contract (dohodu vstupů a
/// výsledku) metody project zachovej. Porovnej bod a krátkou stopu při stejné rychlosti: co působí
/// rychleji?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

record Star(double x, double y, double z) {
}

ArrayList<Star> stars = new ArrayList<>();
Random random;
double centreX, centreY, speed;
boolean paused;
Star fresh() {
    return new Star(random.nextDouble() * 1000 - 500, random.nextDouble() * 800 - 400, 800);
}

void restart() {
    random = new Random(38);
    stars.clear();
    for (int i = 0; i < 220; i++) {
        var s = fresh();
        stars.add(new Star(s.x, s.y, 20 + random.nextDouble() * 780));
    }
    centreX = WIDTH / 2;
    centreY = HEIGHT / 2;
    speed = 3;
    paused = true;
}

void aim(int x, int y) {
    centreX = Math.max(0, Math.min(WIDTH, x));
    centreY = Math.max(0, Math.min(HEIGHT, y));
}

void step() {
    if (paused) {
        return;
    }
    for (int i = 0; i < stars.size(); i++) {
        var s = stars.get(i);
        double z = s.z - speed;
        stars.set(i, z < 5 ? fresh() : new Star(s.x, s.y, z));
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(7, 10, 24));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (var s : stars) {
        var head = project(s, s.z);
        var tail = project(s, s.z + speed * 4);
        int light = (int)Math.min(255, 100 + 155 * (1 - s.z / 800));
        g.setColor(new Color(light, light, 255));
        g.draw(new Line2D.Double(tail, head));
    }
    g.setColor(Color.WHITE);
    g.drawString("Speed: " + (int)speed + "   " + (paused ? "Click or Space to fly" : "Drag to steer the vanishing point"), 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
        }
    }
}

void press(int x, int y, int button) {
    aim(x, y);
    paused = false;
}

void drag(int x, int y) {
    aim(x, y);
}

void showWindow() {
    var frame = new JFrame("Star Tunnel");
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
    var timer = new Timer(20, e -> {
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click/drag: steer + start   Space: pause   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
