import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Task · MissingTriangle
/// Lesson: 1.2 Statement order — 02-order.html
///
/// **Bring back three cut-out circles.** The picture starts empty because `paintDisc` does not
/// draw anything yet.
///
/// **Your task:** In `paintDisc` below, replace the line starting with `//` with two separate
/// lines. Use both supplied calls, each once, and choose their order yourself:
///
/// - `whiteBite(g, i);` draws a white bite.
/// - `darkDisc(g, i);` draws a solid black circle.
///
/// Copy each call completely, including `(g, i);`. Do not write it after `//`: that makes the
/// whole line a comment, which Java skips.
///
/// **Result:** Three black circles with white bites will appear. Their bites suggest a white
/// triangle between the circles. If the circles stay solid black, change the order of your two
/// lines.

void paintDisc(Graphics2D g, int i) {
    // Rebuild this disc with the two supplied drawing actions.
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Use clicks to rotate the discs until the triangle disappears. Set up one convincing triangle and
/// one broken illusion for a friend. Which disc makes the biggest difference? Change the existing
/// colours only after you have compared both arrangements.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void darkDisc(Graphics2D g, int i) {
    g.setColor(Color.BLACK);
    g.fillOval(cx[i] - 65, cy[i] - 65, 130, 130);
}

void whiteBite(Graphics2D g, int i) {
    g.setColor(Color.WHITE);
    g.fillArc(cx[i] - 66, cy[i] - 66, 132, 132, base[i] + rotation[i], 60);
}

final int WIDTH = 600, HEIGHT = 450;

int[] rotation = new int[3];
int[] cx = {300, 160, 440}, cy = {80, 320, 320}, base = {-120, 0, 120};
boolean outlines;
void restart() {
    rotation = new int[3];
    outlines = false;
}

void rotate(int index) {
    if (index >= 0 && index < 3) {
        rotation[index] = (rotation[index] + 30) % 360;
    }
}

void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int i = 0; i < 3; i++) {
        paintDisc(g, i);
        if (outlines) {
            g.setColor(Color.RED);
            g.drawOval(cx[i] - 65, cy[i] - 65, 130, 130);
            for (int side = 0; side < 2; side++) {
                double a = Math.toRadians(base[i] + rotation[i] + side * 60);
                g.drawLine(cx[i], cy[i], cx[i] + (int)(65 * Math.cos(a)), cy[i] - (int)(65 * Math.sin(a)));
            }
        }
    }
    g.setColor(Color.DARK_GRAY);
    g.drawString("Is there a white triangle? No triangle is filled by the program.", 15, 420);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_3) {
        rotate(code - KeyEvent.VK_1);
    }
    if (pressed && code == KeyEvent.VK_O) {
        outlines = !outlines;
    }
}

void press(int x, int y, int button) {
    for (int i = 0; i < 3; i++) {
        if (Math.hypot(x - cx[i], y - cy[i])<65) {
            rotate(i);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Missing Triangle");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Click a disc or press 1-3: rotate   O: actual outlines   R: align</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
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
