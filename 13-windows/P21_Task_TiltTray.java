import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.Timer;

/// # Solved task · TiltTray
/// Lesson: 13.4 Advance a simulation in small steps — 04-timers.html
///
/// Original assignment (this version is complete):
///
/// Tilt a tray and guide a marble around wooden blocks into a green target. Arriving slowly is harder
/// than merely touching the goal.
///
/// **Your mission:** A side collision reverses speed but leaves the marble inside the obstacle, causing
/// rattling. Repair horizontal separation in step().
///
/// Run the program and use the arrow keys to tilt the marble around blocks, braking before it
/// reaches the green target.

void step() {
    if (!started || won) {
        return;
    }
    vx += (held[KeyEvent.VK_RIGHT] ? .13 : 0) - (held[KeyEvent.VK_LEFT] ? .13 : 0);
    vy += (held[KeyEvent.VK_DOWN] ? .13 : 0) - (held[KeyEvent.VK_UP] ? .13 : 0);
    vx = Math.max(-3.5, Math.min(3.5, vx * .985));
    vy = Math.max(-3.5, Math.min(3.5, vy * .985));
    x += vx;
    for (var p : obstacles) {
        if (hit(p)) {
            x = vx > 0 ? p.x - 10 : p.x + p.width + 10;
            vx *= -.3;
        }
    }
    y += vy;
    for (var p : obstacles) {
        if (hit(p)) {
            y = vy > 0 ? p.y - 10 : p.y + p.height + 10;
            vy *= -.3;
        }
    }
    if (x < 20) {
        x = 20;
        vx = Math.abs(vx) * .3;
    }
    if (x > 580) {
        x = 580;
        vx = -Math.abs(vx) * .3;
    }
    if (y < 20) {
        y = 20;
        vy = Math.abs(vy) * .3;
    }
    if (y > 400) {
        y = 400;
        vy = -Math.abs(vy) * .3;
    }
    if (Math.hypot(x - 530, y - 345)<19 && Math.hypot(vx, vy)<1.5) {
        won = true;
    }
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Rearrange two obstacle rectangles into a new route. Place the start outside every obstacle and test
/// both approaches to one block.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

Rectangle[] obstacles = {new Rectangle(180, 70, 40, 230), new Rectangle(350, 200, 130, 35)};
double x, y, vx, vy;
boolean started, won;
void restart() {
    x = 65;
    y = 65;
    vx = vy = 0;
    started = won = false;
}

boolean hit(Rectangle p) {
    return Math.hypot(x - Math.max(p.x, Math.min(p.x + p.width, x)), y - Math.max(p.y, Math.min(p.y + p.height, y)))<10;
}

void draw(Graphics2D g) {
    g.setColor(new Color(235, 222, 190));
    g.fillRect(10, 10, 580, 400);
    g.setColor(new Color(110, 85, 60));
    for (var p : obstacles) {
        g.fill(p);
    }
    g.setColor(new Color(80, 180, 120));
    g.fillOval(502, 317, 56, 56);
    g.setColor(new Color(65, 100, 180));
    g.fillOval((int)x - 10, (int)y - 10, 20, 20);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Balanced into the target! R: replay" : !started ? "Arrow keys tilt the tray" : "Brake with the opposite arrow; enter green slowly", 20, 35);
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
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN)) {
        started = true;
    }
}

void showWindow() {
    var frame = new JFrame("Tilt Tray");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: tilt / brake   Guide the ball slowly into green   R: restart</div></html>", SwingConstants.CENTER);
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
