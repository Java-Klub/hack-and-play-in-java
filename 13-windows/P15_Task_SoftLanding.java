import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Task · SoftLanding
/// Lesson: 13.3 Give events one clear job — 03-handlers.html
///
/// The limited-tank lander already works. Turn it into a flight trainer so you can practise the landing
/// rather than ration a shrinking tank.
///
/// **Your mission:** Make thrust work even with a zero fuel counter and never spend that counter.
/// Preserve the original engine strengths, independent gravity, combined controls, touchdown rules and
/// terminal crashes. Find which conditions actually restrict powered motion; simply drawing a larger
/// flame is not enough.
///
/// Up brakes the fall; Left/Right steer; R restarts. In the normal game, watch fuel fall while
/// thrusting. After the hack it stays fixed, but releasing all keys still lets gravity accelerate the
/// fall.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
void step() {
    if (!state.equals("FLYING")) {
        return;
    }
    velocityY += 0.04;
    applyThrust();
    x += velocityX;
    y += velocityY;
    if (x < HALF || x > WIDTH - HALF || y < HALF) {
        state = "CRASH";
        return;
    }
    if (y + HALF >= GROUND) {
        y = GROUND - HALF;
        boolean onPad = x - HALF >= PAD_X && x + HALF <= PAD_X + PAD_WIDTH;
        boolean gentle = Math.abs(velocityX) <= 1.2 && velocityY >= 0 && velocityY <= 1.5;
        state = onPad && gentle ? "LANDED!" : "CRASH";
    }
}

void applyThrust() {
    if (fuel <= 0 || !(left || right || up)) return;
    if (left) velocityX -= 0.06;
    if (right) velocityX += 0.06;
    if (up) velocityY -= 0.09;
    fuel--;
}

final int WIDTH = 600, HEIGHT = 420, GROUND = 385, PAD_X = 380, PAD_WIDTH = 140, HALF = 12;
double x, y, velocityX, velocityY;
int fuel;
boolean left, right, up;
String state;

void clearKeys() {
    left = right = up = false;
}

void restart() {
    x = 150;
    y = 60;
    velocityX = 1.2;
    velocityY = 0;
    fuel = 240;
    state = "READY";
    clearKeys();
}

void begin() {
    if (state.equals("READY")) {
        state = "FLYING";
    }
}

void draw(Graphics g) {
    g.setColor(new Color(14, 20, 39));
    g.fillRect(0, 0, WIDTH, HEIGHT + 60);
    g.setColor(new Color(74, 79, 101));
    g.fillRect(0, GROUND, WIDTH, HEIGHT - GROUND);
    g.setColor(new Color(87, 226, 166));
    g.fillRect(PAD_X, GROUND, PAD_WIDTH, 8);
    g.setColor(Color.WHITE);
    g.drawString("LAND HERE", PAD_X + 34, GROUND + 25);
    if (state.equals("FLYING") && fuel > 0) {
        g.setColor(new Color(255, 164, 70));
        if (up) {
            g.fillPolygon(new int[]{(int) x - 7, (int) x, (int) x + 7},
            new int[]{(int) y + HALF, (int) y + 30, (int) y + HALF}, 3);
        }
        if (left) {
            g.fillRect((int) x + HALF, (int) y - 3, 12, 6);
        }
        if (right) {
            g.fillRect((int) x - HALF - 12, (int) y - 3, 12, 6);
        }
    }
    g.setColor(state.equals("CRASH") ? new Color(255, 115, 120) : new Color(126, 209, 255));
    g.fillRoundRect((int) x - HALF, (int) y - HALF, HALF * 2, HALF * 2, 6, 6);
    g.setColor(Color.WHITE);
    g.fillOval((int) x - 5, (int) y - 6, 10, 9);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
    g.drawString(state + "    Fuel: " + fuel, 14, HEIGHT + 22);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Speed X: %.2f   Down: %.2f   Safe: |X| <= 1.2, Down <= 1.5".formatted(velocityX, velocityY), 14, HEIGHT + 41);
    g.drawString("Arrows: thrust and start     Up: brake fall     R: restart", 14, HEIGHT + 57);
}

void showWindow() {
    var frame = new JFrame("Soft Landing");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 60));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    left = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    right = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_UP -> {
                    up = pressed;
                    if (pressed) {
                        begin();
                    }
                }
                case KeyEvent.VK_R -> {
                    if (pressed) {
                        restart();
                    }
                }
            }
        }
        @Override public void keyPressed(KeyEvent event) {
            change(event, true);
        }
        @Override public void keyReleased(KeyEvent event) {
            change(event, false);
        }
    });
    var timer = new Timer(20, event -> {
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) {
            clearKeys();
        }
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
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

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Create a low-gravity training planet, then try to land using only three planned bursts. Keep the
/// training tank unlimited so only your flight plan changes.
// </editor-fold>
