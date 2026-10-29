import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/// # Vyřešený úkol · BouncingBalls
/// Lekce: 5.3 Vrať výsledek volajícímu — 03-results.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Tvoje mise · Hackni fyziku stěn:** Vytvoř dokonale pružné boční stěny a strop: při odrazu se
/// zachová celá velikost příchozí rychlosti v daném směru a směr se obrátí. Najdi jejich společný
/// výpočet ztráty rychlosti. Podlahu s vlastním tlumením, gravitaci i házení myší zachovej.
///
/// Výchozí program funguje podle původních pravidel. Kontrola už očekává tvůj nový cíl.
///
/// Hoď míč stranou ke stěně; Space pozastaví a R vyprázdní místnost. Porovnej pohyb před úpravou a po
/// ní. Obrázek ukazuje pohyb, nikoli číselné rychlosti; Check porovná přesné hodnoty při srážkách
/// odděleně od gravitace mezi nimi.

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Vytvoř záměrně měkkou místnost a potom vrať pružné pravidlo. Pro stejnou příchozí hodnotu
/// vysvětli, která číselná změna předpovídá jiný odraz.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · prozkoumej podle potřeby">
double bounceSpeed(double speed) {
    return Math.abs(speed);
}

final int WIDTH = 640, HEIGHT = 440;

record Ball(double x, double y, double vx, double vy, Color colour) {
}

ArrayList<Ball> balls = new ArrayList<>();
Random random = new Random(9);
int startX, startY, aimX, aimY;
boolean aiming, paused;
void restart() {
    balls.clear();
    aiming = false;
    paused = true;
}

void add(double x, double y, double vx, double vy) {
    if (balls.size() >= 60) {
        return;
    }
    double speed = Math.hypot(vx, vy);
    if (speed > 12) {
        vx *= 12 / speed;
        vy *= 12 / speed;
    }
    balls.add(new Ball(Math.max(10, Math.min(WIDTH - 10, x)), Math.max(10, Math.min(HEIGHT - 10, y)), vx, vy, Color.getHSBColor(random.nextFloat(), .7f, .85f)));
    paused = false;
}

void step() {
    if (paused) {
        return;
    }
    for (int i = 0; i < balls.size(); i++) {
        var b = balls.get(i);
        double vx = b.vx * .998, vy = b.vy + .15, x = b.x + vx, y = b.y + vy;
        if (x < 10) {
            x = 10;
            vx = bounceSpeed(vx);
        }
        if (x > WIDTH - 10) {
            x = WIDTH - 10;
            vx = -bounceSpeed(vx);
        }
        if (y < 10) {
            y = 10;
            vy = bounceSpeed(vy);
        }
        if (y > HEIGHT - 10) {
            y = HEIGHT - 10;
            vy = -Math.abs(vy) * .72;
            vx *= .97;
            if (Math.abs(vy)<.6) {
                vy = 0;
            }
        }
        balls.set(i, new Ball(x, y, vx, vy, b.colour));
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(240, 243, 250));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (var b : balls) {
        g.setColor(b.colour);
        g.fillOval((int)b.x - 10, (int)b.y - 10, 20, 20);
    }
    if (aiming) {
        g.setColor(Color.GRAY);
        g.drawLine(startX, startY, aimX, aimY);
    }
    g.setColor(Color.BLACK);
    g.drawString("Balls: " + balls.size() + " / 60   " + (paused ? "Paused" : "Gravity on") + "   Drag to throw a ball; Space pauses; R clears", 12, 23);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        paused = !paused;
    }
}

void press(int x, int y, int button) {
    startX = aimX = x;
    startY = aimY = y;
    aiming = true;
}

void drag(int x, int y) {
    aimX = x;
    aimY = y;
}

void release(int x, int y) {
    if (aiming) {
        add(startX, startY, (x - startX) * .08, (y - startY) * .08);
        aiming = false;
    }
}

void showWindow() {
    var frame = new JFrame("Bouncing Balls");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: add and throw a ball   Space: pause   R: clear room</div></html>", SwingConstants.CENTER);
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
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
