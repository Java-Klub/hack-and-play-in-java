import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Solved task · MiniGolf
/// Lesson: 5.5 Combine small referees — 05-composition.html
///
/// Original assignment (this version is complete):
///
/// Putt a ball across a small course, use the walls for rebounds and try to enter the hole slowly. Longer
/// pulls should give stronger shots, while friction gradually stops the ball.
///
/// **Your mission · Build and connect a shot:** The ball has no launch power. Implement dragVelocity (100
/// → 8, -25 → -2, 0 → 0),
/// then connect both inputs of applyPull to that calculation: horizontal must supply vx and vertical
/// must supply vy. The prepared shot limiter stays unchanged.
///
/// Run the program, pull backwards from the resting ball and release for a putt; let it stop
/// before the next shot, or press R to restart.

double dragVelocity(double distance) {
    return distance / 12.5;
}

void applyPull(double horizontal, double vertical) {
    vx = dragVelocity(horizontal);
    vy = dragVelocity(vertical);
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// distance is already a double. Compare distance / 125 and distance / 125.0: both preserve fractions
/// and both use the same scale. Getting eight speed units from a hundred pixels requires a different
/// scale. applyPull must then actually store the results in vx and vy. The prepared shoot already
/// limits excessive power; do not add a second limit here.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Make a gentle and an exaggerated course by changing only the conversion scale. Try the same pull
/// in both. Find a setting that lets a friend reach the hole without bouncing off a wall, then
/// restore the original scale if you want the original check to pass.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

double x, y, vx, vy, aimX, aimY;
boolean aiming, won;
int strokes;
void restart() {
    x = 90;
    y = 110;
    vx = vy = 0;
    strokes = 0;
    aiming = won = false;
}

boolean resting() {
    return Math.hypot(vx, vy)<.05;
}

void begin(int xx, int yy) {
    if (!won && resting() && Math.hypot(xx - x, yy - y)<22) {
        aiming = true;
        aimX = xx;
        aimY = yy;
    }
}

void shoot(int xx, int yy) {
    if (!aiming) {
        return;
    }
    aiming = false;
    applyPull(x - xx, y - yy);
    double speed = Math.hypot(vx, vy);
    if (speed > 10) {
        vx *= 10 / speed;
        vy *= 10 / speed;
    }
    if (speed>.1) {
        strokes++;
    }
}

void step() {
    if (won || resting()) {
        return;
    }
    x += vx;
    y += vy;
    vx *= .985;
    vy *= .985;
    if (x < 28) {
        x = 28;
        vx = Math.abs(vx) * .8;
    }
    if (x > 572) {
        x = 572;
        vx = -Math.abs(vx) * .8;
    }
    if (y < 28) {
        y = 28;
        vy = Math.abs(vy) * .8;
    }
    if (y > 372) {
        y = 372;
        vy = -Math.abs(vy) * .8;
    }
    if (Math.hypot(x - 500, y - 310)<12 && Math.hypot(vx, vy)<1.5) {
        x = 500;
        y = 310;
        vx = vy = 0;
        won = true;
    }
    if (resting()) {
        vx = vy = 0;
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(40, 120, 70));
    g.fillRect(20, 20, 560, 360);
    g.setStroke(new BasicStroke(5));
    g.setColor(new Color(90, 60, 40));
    g.drawRect(20, 20, 560, 360);
    g.setColor(Color.BLACK);
    g.fillOval(486, 296, 28, 28);
    g.setColor(Color.YELLOW);
    g.drawLine(500, 310, 500, 270);
    g.fillPolygon(new int[]{500, 520, 500}, new int[]{270, 280, 290}, 3);
    g.setColor(Color.WHITE);
    g.fillOval((int)x - 8, (int)y - 8, 16, 16);
    if (aiming) {
        g.setStroke(new BasicStroke(2));
        g.drawLine((int)x, (int)y, (int)aimX, (int)aimY);
    }
    g.setColor(Color.BLACK);
    g.drawString(won ? "Holed in " + strokes + " strokes!" : "Strokes: " + strokes + " - enter the hole slowly", 20, 408);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    begin(x, y);
}

void drag(int x, int y) {
    if (aiming) {
        aimX = x;
        aimY = y;
    }
}

void release(int x, int y) {
    shoot(x, y);
}

void showWindow() {
    var frame = new JFrame("Mini Golf");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag backward from the ball; release: putt   R: restart</div></html>", SwingConstants.CENTER);
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
