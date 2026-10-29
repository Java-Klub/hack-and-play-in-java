import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.Timer;

/// # Solved task · PlatformDash
/// Lesson: 13.4 Advance a simulation in small steps — 04-timers.html
///
/// Original assignment (this version is complete):
///
/// Run and jump across a few platforms to reach the flag. Jumping upward through a platform and landing
/// on it later should feel natural.
///
/// **Your mission:** Platforms snap the player onto their top even during the wrong part of a jump.
/// Repair the landing branch of step() to accept genuine top crossings only.
///
/// Run the program, move with Left and Right, and press Space to jump toward the platforms and
/// flag.

void step() {
    if (!started || won) {
        return;
    }
    x += (held[KeyEvent.VK_RIGHT] ? 3.4 : 0) - (held[KeyEvent.VK_LEFT] ? 3.4 : 0);
    x = Math.max(0, Math.min(WIDTH - 20, x));
    double oldBottom = y + 28;
    vy += .25;
    y += vy;
    grounded = false;
    if (vy >= 0) {
        for (var p : platforms) {
            if (x + 20 > p.x && x < p.x + p.width && oldBottom <= p.y && y + 28 >= p.y) {
                y = p.y - 28;
                vy = 0;
                grounded = true;
                break;
            }
        }
    }
    if (grounded && x > 495 && y < 200) {
        won = true;
    }
    if (y > HEIGHT) {
        falls++;
        attempt();
        clearKeys();
    }
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Landing is a crossing, not just an overlap
///
/// Screen y grows downward, so positive vertical speed means falling. Compare the previous bottom
/// edge with the new bottom edge and a platform's top. In a separate example, a bottom moving from 30
/// to 38 crosses a surface at 35, while one already below 35 does not cross it this step. Predict a
/// rising jump passing upward through a platform. Horizontal overlap is necessary but does not
/// establish which side the player came from.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Move the platforms and flag to create two routes: a short risky jump and a longer safe route. Test
/// both upward passage and downward landing.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

Rectangle[] platforms = {new Rectangle(30, 360, 150, 14), new Rectangle(220, 280, 130, 14), new Rectangle(400, 200, 150, 14)};
double x, y, vy;
boolean started, grounded, won;
int falls;
void attempt() {
    x = 60;
    y = 332;
    vy = 0;
    grounded = true;
    started = false;
}

void restart() {
    falls = 0;
    won = false;
    attempt();
}

void jump() {
    if (!won && grounded) {
        started = true;
        grounded = false;
        vy = -8.8;
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(225, 240, 255));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(55, 100, 85));
    for (var p : platforms) {
        g.fill(p);
    }
    g.setColor(Color.ORANGE);
    g.fillRect(520, 160, 8, 40);
    g.fillPolygon(new int[]{528, 555, 528}, new int[]{160, 170, 180}, 3);
    g.setColor(new Color(60, 80, 200));
    g.fillRect((int)x, (int)y, 20, 28);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Goal reached! R: replay" : "Falls: " + falls + (!started ? " - press an arrow or Space" : ""), 15, 25);
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
    if (pressed && (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT)) {
        started = true;
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        jump();
    }
}

void showWindow() {
    var frame = new JFrame("Platform Dash");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: run   Space: jump   Reach the flag   R: restart</div></html>", SwingConstants.CENTER);
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
