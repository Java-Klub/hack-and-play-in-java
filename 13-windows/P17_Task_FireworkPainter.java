import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/// # Solved task · FireworkPainter
/// Lesson: 13.3 Give events one clear job — 03-handlers.html
///
/// Original assignment (this version is complete):
///
/// **Light the sky with paired fireworks.** Clicking creates no explosion yet: the supplied
/// handler is waiting for your code.
///
/// **Your task:** Fill the delayed action body in `burstAction` below. Each click creates two
/// bursts: at `(x, y)` and its reflected position `(WIDTH - x, y)`. Use the supplied `burst` to
/// create particles. Merely constructing the action without clicking must launch nothing.
///
/// **Result:** The sky starts empty. Clicking on the left also lights the matching position on
/// the right, creating 120 particles. Space pauses motion and R clears the sky.

java.util.function.BiConsumer<Integer, Integer> burstAction() {
    return (x, y) -> {
        burst(x, y);
        burst(WIDTH - x, y);
    };
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Turn the paired fireworks into a signature: make three bursts forming a triangle around each
/// click. Draw their planned centres on paper first. Leave burst in charge of particles; let your
/// callback arrange the three calls.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

record Particle(double x, double y, double vx, double vy, int age, Color colour) {
}

BufferedImage canvas;
ArrayList<Particle> particles = new ArrayList<>();
Random random = new Random(41);
boolean paused;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(new Color(8, 12, 28));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
    particles.clear();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    paused = true;
    message = "Click to burst";
}

void burst(int x, int y) {
    if (particles.size()>1140) {
        return;
    }
    Color colour = Color.getHSBColor(random.nextFloat(), .75f, 1);
    for (int i = 0; i < 60; i++) {
        double a = 2 * Math.PI * i / 60, velocity = 1 + random.nextDouble() * 3;
        particles.add(new Particle(x, y, Math.cos(a) * velocity, Math.sin(a) * velocity, 0, colour));
    }
    paused = false;
}

void step() {
    if (paused) {
        return;
    }
    var g = canvas.createGraphics();
    for (int i = particles.size() - 1; i >= 0; i--) {
        var p = particles.get(i);
        double x = p.x + p.vx, y = p.y + p.vy;
        g.setColor(new Color(p.colour.getRed(), p.colour.getGreen(), p.colour.getBlue(), Math.max(0, 150 - p.age * 2)));
        g.draw(new Line2D.Double(p.x, p.y, x, y));
        if (p.age >= 65) {
            particles.remove(i);
        }
        else {
            particles.set(i, new Particle(x, y, p.vx * .992, p.vy + .035, p.age + 1, p.colour));
        }
    }
    g.dispose();
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.WHITE);
    g.drawString(message + "   Particles: " + particles.size() + "   " + (paused ? "Paused" : ""), 12, 22);
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
    burstCallback.accept(x, y);
}

void showWindow() {
    var frame = new JFrame("Firework Painter");
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
    };
    panel.addMouseListener(mouse);
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: burst   Space: pause   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
    timer.start();
}

java.util.function.BiConsumer<Integer, Integer> burstCallback = burstAction();

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
