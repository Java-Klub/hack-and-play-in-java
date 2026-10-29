import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Solved task · SteppingFeet
/// Lesson: 2.2 Change a stored value — 02-change.html
///
/// Original assignment (this version is complete):
///
/// **Bring both feet into view.** Two rectangles share one horizontal position, `x`. They
/// currently start beyond the left edge of the window.
///
/// **Your task:** In `startingPosition` below, change the second line so that `x` is `80` after
/// both lines have run. Keep the first line, `x = 20;`. The number to the right of `=` is the
/// value that line stores in `x`.
///
/// **Result:** A fresh run will show both feet and x = 80 in the top label. Space starts their
/// motion; then use C to hide the stripes and compare their apparent speeds.

void startingPosition() {
    x = 20;
    x = x + 60;
}

// <editor-fold defaultstate="expanded" desc="For this game">
/// A fixed assignment and a calculation using the old x can both leave x at 80. Both satisfy this
/// mission. A later experiment with another starting value distinguishes them. Java evaluates the
/// right-hand side before storing the new value; both feet then read the same x.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Compare two versions: set x directly to 80, or add 60 to its previous value. First run each with
/// the initial x = 20; then change that first assignment to 40. Predict both final positions before
/// running. Which version keeps a fixed destination and which keeps a fixed shift?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 640, HEIGHT = 360;

double x;
boolean paused, stripes, marker;
void restart() {
    startingPosition();
    paused = true;
    stripes = true;
    marker = false;
}

void step() {
    if (!paused) {
        x += 1.6;
        if (x > WIDTH + 70) {
            x = -70;
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(145, 145, 145));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    if (stripes) {
        for (int i = 0; i < WIDTH; i += 40) {
            g.setColor((i / 40) % 2 == 0 ? Color.BLACK : Color.WHITE);
            g.fillRect(i, 0, 40, HEIGHT);
        }
    }
    g.setColor(Color.WHITE);
    g.fillRect((int)x, 110, 65, 35);
    g.setColor(Color.BLACK);
    g.fillRect((int)x, 230, 65, 35);
    if (marker) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawLine((int)x, 65, (int)x, 300);
    }
    g.setColor(new Color(255, 220, 90));
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString((paused ? "Paused" : "Moving") + " - both feet have x = " + (int)x + " and the same speed", 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_C -> stripes = !stripes;
            case KeyEvent.VK_M -> marker = !marker;
        }
    }
}

void showWindow() {
    var frame = new JFrame("Stepping Feet");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   C: striped / plain   M: shared marker   R: reset</div></html>", SwingConstants.CENTER);
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
