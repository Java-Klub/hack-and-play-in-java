import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;

/// # Solved task · SpringToy
/// Lesson: 13.5 Let painting observe the model — 05-model-view.html
///
/// Original assignment (this version is complete):
///
/// Drag a weight, release it and watch a spring trace its motion. Tune the spring and damping to find a
/// slow wobble or a lively bounce.
///
/// **Your mission:** Painting the window advances the physics, so even repainting can move a paused
/// weight. Restore a drawing method that only observes the current model.

void draw(Graphics2D g) {
    g.setColor(new Color(225, 230, 240));
    for (int i = 1; i < trail.size(); i++) {
        g.draw(new Line2D.Double(trail.get(i - 1), trail.get(i)));
    }
    g.setColor(Color.GRAY);
    g.setStroke(new BasicStroke(2));
    var spring = new Path2D.Double();
    spring.moveTo(WIDTH / 2, 65);
    double dx = x - WIDTH / 2, dy = y - 65, len = Math.max(1, Math.hypot(dx, dy));
    for (int i = 1; i < 20; i++) {
        double a = i / 20.0, zig = (i % 2 == 0 ? -7 : 7);
        spring.lineTo(WIDTH / 2 + dx * a - dy / len * zig, 65 + dy * a + dx / len * zig);
    }
    spring.lineTo(x, y);
    g.draw(spring);
    g.setColor(Color.BLACK);
    g.fillRect(WIDTH / 2 - 20, 58, 40, 7);
    g.setColor(new Color(65, 110, 210));
    g.fillOval((int)x - 18, (int)y - 18, 36, 36);
    g.setColor(Color.BLACK);
    g.drawString(String.format("Stiffness %.3f   Damping %.3f   %s", stiffness, damping, paused ? "Paused" : "Moving"), 15, 25);
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Build a sleepy spring and a nervous spring by choosing two stiffness/damping pairs. Add a visual
/// mark that depends on the current speed, such as a larger halo at high speed. Painting must still
/// leave every model value unchanged.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

double x, y, vx, vy, stiffness, damping;
boolean paused, grabbed;
ArrayList<Point2D.Double> trail = new ArrayList<>();
void restart() {
    x = WIDTH / 2;
    y = 260;
    vx = vy = 0;
    stiffness = .012;
    damping = .985;
    paused = true;
    grabbed = false;
    trail.clear();
}

void tune(double k, double d) {
    stiffness = Math.max(.002, Math.min(.04, stiffness + k));
    damping = Math.max(.9, Math.min(1, damping + d));
}

void pull(int xx, int yy) {
    x = Math.max(12, Math.min(WIDTH - 12, xx));
    y = Math.max(95, Math.min(HEIGHT - 12, yy));
    vx = vy = 0;
}

void step() {
    if (paused || grabbed) {
        return;
    }
    double dx = x - WIDTH / 2, dy = y - 65, length = Math.max(1, Math.hypot(dx, dy));
    double force = -stiffness * (length - 115);
    vx = (vx + force * dx / length) * damping;
    vy = (vy + force * dy / length + .06) * damping;
    double speed = Math.hypot(vx, vy);
    if (speed > 8) {
        vx *= 8 / speed;
        vy *= 8 / speed;
    }
    x += vx;
    y += vy;
    if (x < 12 || x > WIDTH - 12) {
        x = Math.max(12, Math.min(WIDTH - 12, x));
        vx *= -.5;
    }
    if (y < 80 || y > HEIGHT - 12) {
        y = Math.max(80, Math.min(HEIGHT - 12, y));
        vy *= -.5;
    }
    trail.add(new Point2D.Double(x, y));
    if (trail.size()>800) {
        trail.remove(0);
    }
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_UP -> tune(.002, 0);
            case KeyEvent.VK_DOWN -> tune(-.002, 0);
            case KeyEvent.VK_LEFT -> tune(0, -.005);
            case KeyEvent.VK_RIGHT -> tune(0, .005);
        }
    }
}

void press(int x, int y, int button) {
    if (Math.hypot(x - this.x, y - this.y)<30) {
        grabbed = true;
        pull(x, y);
    }
}

void drag(int x, int y) {
    if (grabbed) {
        pull(x, y);
    }
}

void release(int x, int y) {
    if (grabbed) {
        pull(x, y);
        grabbed = false;
        paused = false;
    }
}

void showWindow() {
    var frame = new JFrame("Spring Toy");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag mass: pull / release   Space: pause   Up/Down: stiffness   Left/Right: damping   R:<br>reset</div></html>", SwingConstants.CENTER);
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
