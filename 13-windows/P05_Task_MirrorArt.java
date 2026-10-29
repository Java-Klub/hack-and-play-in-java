import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/// # Solved task · MirrorArt
/// Lesson: 13.1 Draw in a Java window — 01-drawing.html
///
/// Original assignment (this version is complete):
///
/// Draw one stroke and let a ring of reflected copies turn it into a rosette. Changing the number of
/// mirror axes can make the same gesture look completely different.
///
/// **Your mission:** Implement the whole coordinate adapter in transform. Subtract the canvas centre,
/// optionally reflect the local y, rotate by 2π × copy / axes, then add the centre back. Return a new
/// point; never mutate the original stroke.

Point2D.Double transform(int x, int y, int copy, boolean reflect) {
    double dx = x - WIDTH / 2;
    double dy = y - HEIGHT / 2;
    if (reflect) dy = -dy;
    double angle = 2 * Math.PI * copy / axes;
    double rotatedX = dx * Math.cos(angle) - dy * Math.sin(angle);
    double rotatedY = dx * Math.sin(angle) + dy * Math.cos(angle);
    return new Point2D.Double(WIDTH / 2 + rotatedX, HEIGHT / 2 + rotatedY);
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Design a two-colour emblem: give reflected strokes a different colour from rotated strokes in the
/// prepared drawing loop. Draw just one deliberate gesture and choose the axis count that turns it
/// into your emblem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 440;

BufferedImage canvas;
int axes, lastX, lastY;
boolean drawing;
String message;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    axes = 6;
    drawing = false;
    message = "Draw with the mouse";
}

void adjust(int amount) {
    axes = Math.max(2, Math.min(12, axes + amount));
}

void stroke(int x1, int y1, int x2, int y2) {
    var g = canvas.createGraphics();
    g.setColor(new Color(40, 80, 170));
    g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    for (int i = 0; i < axes; i++) {
        for (boolean reflected : new boolean[]{false, true}) {
            g.draw(new Line2D.Double(transform(x1, y1, i, reflected), transform(x2, y2, i, reflected)));
        }
    }
    g.dispose();
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.GRAY);
    g.drawLine(WIDTH / 2 - 4, HEIGHT / 2, WIDTH / 2 + 4, HEIGHT / 2);
    g.drawLine(WIDTH / 2, HEIGHT / 2 - 4, WIDTH / 2, HEIGHT / 2 + 4);
    g.setColor(Color.BLACK);
    g.drawString("Axes: " + axes + "   " + message, 10, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_UP -> adjust(1);
            case KeyEvent.VK_DOWN -> adjust(-1);
        }
    }
}

void press(int x, int y, int button) {
    drawing = true;
    lastX = x;
    lastY = y;
}

void drag(int x, int y) {
    if (drawing) {
        stroke(lastX, lastY, x, y);
        lastX = x;
        lastY = y;
    }
}

void release(int x, int y) {
    drawing = false;
}

void showWindow() {
    var frame = new JFrame("Mirror Art");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: draw   Up/Down: axes   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
