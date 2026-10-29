import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;

/// # Task · AsteroidDrift
/// Lesson: 13.3 Give events one clear job — 03-handlers.html
///
/// Fly a tiny ship through five drifting rocks. The standard flight already works; this time you are
/// its control hacker rather than its repair mechanic.
///
/// **Your mission:** Rewire this working flight: F must fire, Space must brake by multiplying both
/// velocity components by 0.92 each active tick, and arrows must keep steering. Shooting still obeys
/// cooldown. First sketch the route from a key press to a new shot; find the actual route in the
/// source before editing.
///
/// Start with an arrow. Test Space and F, then compare them again after the hack. R resets; the
/// window shows the controls implemented by your current version.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Make a rescue flight: allow shots only while the ship is not thrusting. Change the firing
/// callback, then try surviving by coasting and choosing when to shoot. Explain which action still
/// owns cooldown; this new rule need not pass the original shooting checks.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
Runnable firingAction() {
    return this::fire;
}

final int WIDTH = 600, HEIGHT = 420;

record Rock(double x, double y, double vx, double vy, double radius) {
}

record Shot(double x, double y, double vx, double vy, int life) {
}

ArrayList<Rock> rocks = new ArrayList<>();
ArrayList<Shot> shots = new ArrayList<>();
double x, y, vx, vy, angle;
int cooldown;
boolean started, lost;
void restart() {
    x = WIDTH / 2;
    y = HEIGHT / 2;
    vx = vy = 0;
    angle = -Math.PI / 2;
    cooldown = 0;
    started = lost = false;
    rocks.clear();
    shots.clear();
    for (int i = 0; i < 5; i++) {
        rocks.add(new Rock(40 + i * 115, 40, .6 + i * .1, .45, 19));
    }
}

double wrap(double value, double size) {
    return (value % size + size) % size;
}

void fire() {
    if (cooldown != 0 || lost || rocks.isEmpty()) {
        return;
    }
    started = true;
    shots.add(new Shot(x + Math.cos(angle) * 15, y + Math.sin(angle) * 15,
    vx + Math.cos(angle) * 6, vy + Math.sin(angle) * 6, 70));
    cooldown = 10;
}

void step() {
    if (!started || lost || rocks.isEmpty()) {
        return;
    }
    if (held[KeyEvent.VK_LEFT]) {
        angle -= .07;
    }
    if (held[KeyEvent.VK_RIGHT]) {
        angle += .07;
    }
    if (held[KeyEvent.VK_UP]) {
        vx += Math.cos(angle) * .09;
        vy += Math.sin(angle) * .09;
    }
    double speed = Math.hypot(vx, vy);
    if (speed > 5) {
        vx *= 5 / speed;
        vy *= 5 / speed;
    }
    x = wrap(x + vx, WIDTH);
    y = wrap(y + vy, HEIGHT);
    if (cooldown > 0) {
        cooldown--;
    }
    if (held[KeyEvent.VK_SPACE]) {
        fireCallback.run();
    }
    for (int i = 0; i < rocks.size(); i++) {
        var r = rocks.get(i);
        rocks.set(i, new Rock(wrap(r.x + r.vx, WIDTH), wrap(r.y + r.vy, HEIGHT), r.vx, r.vy, r.radius));
    }
    for (int i = shots.size() - 1; i >= 0; i--) {
        var s = shots.get(i);
        s = new Shot(wrap(s.x + s.vx, WIDTH), wrap(s.y + s.vy, HEIGHT), s.vx, s.vy, s.life - 1);
        boolean hit = false;
        for (int j = rocks.size() - 1; j >= 0; j--) {
            var r = rocks.get(j);
            if (Math.hypot(s.x - r.x, s.y - r.y) < r.radius + 2) {
                rocks.remove(j);
                hit = true;
                break;
            }
        }
        if (hit || s.life <= 0) {
            shots.remove(i);
        }
        else {
            shots.set(i, s);
        }
    }
    for (var r : rocks) {
        if (Math.hypot(x - r.x, y - r.y) < r.radius + 8) {
            lost = true;
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(16, 22, 38));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.LIGHT_GRAY);
    for (var r : rocks) {
        g.drawOval((int)(r.x - r.radius), (int)(r.y - r.radius), (int)(2 * r.radius), (int)(2 * r.radius));
    }
    g.setColor(Color.ORANGE);
    for (var s : shots) {
        g.fillOval((int)s.x - 2, (int)s.y - 2, 4, 4);
    }
    var ship = new Path2D.Double();
    for (int i = 0; i < 3; i++) {
        double a = angle + (i == 0 ? 0 : i == 1 ? 2.5 : -2.5);
        double px = x + Math.cos(a) * 13, py = y + Math.sin(a) * 13;
        if (i == 0) {
            ship.moveTo(px, py);
        }
        else {
            ship.lineTo(px, py);
        }
    }
    ship.closePath();
    g.setColor(lost ? Color.RED : Color.CYAN);
    g.draw(ship);
    g.setColor(Color.WHITE);
    g.drawString(!started ? "Press an arrow or Space to launch" : lost ? "Ship lost! R: retry" : rocks.isEmpty() ? "All five cleared! R: replay" : "Rocks: " + rocks.size(), 15, 22);
}

boolean[] held = new boolean[512];
void clearKeys() {
    Arrays.fill(held, false);
}

void key(int code, boolean pressed) {
    if (code >= 0 && code < held.length) {
        held[code] = pressed;
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
        clearKeys();
    }
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE)) {
        started = true;
    }
}

void showWindow() {
    var frame = new JFrame("Asteroid Drift");
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
        @Override public void keyReleased(KeyEvent e) {
            key(e.getKeyCode(), false);
        }
    });
    var timer = new Timer(20, e -> {
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent e) {
            clearKeys();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: turn / thrust   Space: fire   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

Runnable fireCallback = firingAction();

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
