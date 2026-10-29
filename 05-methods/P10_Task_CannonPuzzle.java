import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Solved task · CannonPuzzle
/// Lesson: 5.3 Return a result to the caller — 03-results.html
///
/// Original assignment (this version is complete):
///
/// Aim a cannon at the orange target and watch a shot curve under gravity. Each attempt gives you a
/// visible way to compare direction and launch power.
///
/// **Your mission · Wake the cannon:** The starter drops the shot at the muzzle because its calculated
/// speed is zero.
/// Repair launchSpeed so a drag of 100 gives speed 9 and an upward drag keeps its negative sign.
///
/// Run the program, drag from the cannon towards the upper right and release to shoot at the
/// orange target; R prepares another attempt.

double launchSpeed(double drag) {
    return drag * 0.09;
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// After repairing the conversion, try two existing aim directions with the same pull length. Predict
/// which shot rises higher. Then deliberately change only the scale and see whether your first
/// prediction about direction still holds.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

double x, y, vx, vy, aimX, aimY;
boolean flying, hit, spent;
void restart() {
    x = 60;
    y = 350;
    vx = vy = 0;
    aimX = 150;
    aimY = 270;
    flying = hit = spent = false;
}

void aim(int xx, int yy) {
    if (flying || spent) {
        return;
    }
    double dx = xx - 60, dy = yy - 350, length = Math.hypot(dx, dy);
    if (length > 160) {
        dx *= 160 / length;
        dy *= 160 / length;
    }
    aimX = 60 + Math.max(0, dx);
    aimY = 350 + Math.min(0, dy);
}

void launch() {
    if (flying || spent) {
        return;
    }
    vx = launchSpeed(aimX - 60);
    vy = launchSpeed(aimY - 350);
    flying = true;
}

void step() {
    if (!flying) {
        return;
    }
    x += vx;
    y += vy;
    vy += .24;
    double xx = Math.max(490, Math.min(545, x)), yy = Math.max(275, Math.min(350, y));
    if (Math.hypot(x - xx, y - yy) <= 6) {
        hit = true;
        flying = false;
        spent = true;
    }
    else {
        if (y >= 350 || x > WIDTH + 6 || x<-6) {
            flying = false;
            spent = true;
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(230, 242, 255));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(70, 135, 80));
    g.fillRect(0, 350, WIDTH, 70);
    g.setColor(hit ? Color.GREEN : Color.ORANGE);
    g.fillRect(490, 275, 55, 75);
    g.setColor(Color.DARK_GRAY);
    g.setStroke(new BasicStroke(12));
    g.drawLine(60, 350, (int)(60 + (aimX - 60) * .3), (int)(350 + (aimY - 350) * .3));
    g.fillOval(44, 334, 32, 32);
    if (!flying && !spent) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.GRAY);
        g.drawLine(60, 350, (int)aimX, (int)aimY);
    }
    g.setColor(Color.RED);
    g.fillOval((int)x - 6, (int)y - 6, 12, 12);
    g.setColor(Color.BLACK);
    g.drawString(hit ? "Target hit! R: replay" : spent ? "Missed - R: retry" : "Drag upward/right to aim; release to launch", 15, 25);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    aim(x, y);
}

void drag(int x, int y) {
    aim(x, y);
}

void release(int x, int y) {
    aim(x, y);
    launch();
}

void showWindow() {
    var frame = new JFrame("Cannon Puzzle");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag from the cannon toward your aim; release: fire   R: reset</div></html>", SwingConstants.CENTER);
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
