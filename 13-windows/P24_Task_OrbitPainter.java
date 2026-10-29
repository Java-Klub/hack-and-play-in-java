import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

/// # Solved task · OrbitPainter
/// Lesson: 13.4 Advance a simulation in small steps — 04-timers.html
///
/// Original assignment (this version is complete):
///
/// Launch a particle around a centre of attraction and let its orbit paint a coloured trail. Try to find
/// a long looping flight that stays on the canvas.
///
/// **Your mission:** The pause condition is reversed, so the orbit refuses to advance while running.
/// Repair the tick guard and keep paused positions and trail unchanged.

void step() {
    if (paused) {
        return;
    }
    double dx = WIDTH / 2 - x, dy = HEIGHT / 2 - y, r2 = dx * dx + dy * dy + 25 * 25;
    vx += 600 * dx / Math.pow(r2, 1.5);
    vy += 600 * dy / Math.pow(r2, 1.5);
    double nx = x + vx, ny = y + vy;
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((ticks++ % 1000) / 1000f, .8f, 1));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        paused = true;
        message = "Escaped - drag a new launch";
    }
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Create a two-colour orbit signature: colour outward travel differently from inward travel using
/// the old and new distance from the centre. Search for one launch that crosses its earlier path many
/// times without leaving the canvas.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage canvas;
double x, y, vx, vy, aimX, aimY;
boolean paused, aiming;
int ticks;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(new Color(12, 17, 30));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    x = 440;
    y = 220;
    vx = 0;
    vy = 2.1;
    ticks = 0;
    paused = true;
    aiming = false;
    message = "Space: default orbit, or drag a new launch";
}

void launch(double xx, double yy, double endX, double endY) {
    x = xx;
    y = yy;
    vx = (endX - xx) * .025;
    vy = (endY - yy) * .025;
    double speed = Math.hypot(vx, vy);
    if (speed > 6) {
        vx *= 6 / speed;
        vy *= 6 / speed;
    }
    paused = false;
    aiming = false;
    message = "Painting orbit";
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.WHITE);
    g.fillOval(WIDTH / 2 - 7, HEIGHT / 2 - 7, 14, 14);
    g.setColor(Color.YELLOW);
    g.fillOval((int)x - 3, (int)y - 3, 6, 6);
    if (aiming) {
        g.setColor(Color.CYAN);
        g.draw(new Line2D.Double(x, y, aimX, aimY));
    }
    g.setColor(Color.WHITE);
    g.drawString(message, 12, 22);
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
    x = Math.max(0, Math.min(WIDTH - 1, x));
    y = Math.max(0, Math.min(HEIGHT - 1, y));
    this.x = aimX = x;
    this.y = aimY = y;
    paused = true;
    aiming = true;
}

void drag(int x, int y) {
    if (aiming) {
        aimX = x;
        aimY = y;
    }
}

void release(int x, int y) {
    if (aiming) {
        launch(this.x, this.y, x, y);
    }
}

void showWindow() {
    var frame = new JFrame("Orbit Painter");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: position + launch velocity   Space: pause   R: reset</div></html>", SwingConstants.CENTER);
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
