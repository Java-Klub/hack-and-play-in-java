import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Timer;

/// # Solved task · CrossingRush
/// Lesson: 13.2 Turn a click into a choice — 02-input-events.html
///
/// Original assignment (this version is complete):
///
/// Hop between traffic lanes and reach the green bank. Escaping sideways out of the window should not be
/// a secret safe route.
///
/// **Your mission:** Repeated sideways hops can carry the player beyond the visible playfield. Repair
/// hop() so the whole player remains within the side boundaries.
///
/// Run the program and hop with the arrow keys between passing cars until you reach the green
/// bank at the top.

void hop(int dx, int dy) {
    if (lost || won) {
        return;
    }
    started = true;
    x = Math.max(12, Math.min(WIDTH - 12, x + dx));
    y = Math.min(370, y + dy);
    collide();
    if (!lost && y < 45) {
        won = true;
    }
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Change the car lanes and starting gaps to make a crossing that needs a pause in the middle. Keep
/// both sides of the player inside the field.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 420;

record Car(double x, int lane, double speed) {
}

ArrayList<Car> cars = new ArrayList<>();
int x, y;
boolean started, lost, won;
void restart() {
    x = 300;
    y = 370;
    started = lost = won = false;
    cars.clear();
    for (int lane = 0; lane < 5; lane++) {
        for (int n = 0; n < 3; n++) {
            cars.add(new Car(n * 230 + lane * 47, lane, (lane % 2 == 0 ? 1 : -1) * (1.3 + lane * .28)));
        }
    }
}

void collide() {
    for (var car : cars) {
        if (new Rectangle2D.Double(car.x, 65 + car.lane * 50, 65, 32).intersects(x - 11, y - 11, 22, 22)) {
            lost = true;
        }
    }
}

void step() {
    if (!started || lost || won) {
        return;
    }
    for (int i = 0; i < cars.size(); i++) {
        var c = cars.get(i);
        double xx = c.x + c.speed;
        if (xx > WIDTH + 10) {
            xx = -75;
        }
        if (xx<-75) {
            xx = WIDTH + 10;
        }
        cars.set(i, new Car(xx, c.lane, c.speed));
    }
    collide();
}

void draw(Graphics2D g) {
    g.setColor(new Color(100, 180, 120));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int lane = 0; lane < 5; lane++) {
        g.setColor(new Color(65, 70, 80));
        g.fillRect(0, 60 + lane * 50, WIDTH, 44);
    }
    for (var c : cars) {
        g.setColor(c.lane % 2 == 0 ? Color.ORANGE : Color.CYAN);
        g.fillRect((int)c.x, 65 + c.lane * 50, 65, 32);
    }
    g.setColor(lost ? Color.RED : Color.YELLOW);
    g.fillOval(x - 11, y - 11, 22, 22);
    g.setColor(Color.BLACK);
    g.drawString(won ? "Across safely! R: another run" : lost ? "Traffic got you! R: retry" : !started ? "Arrow keys: hop to the green goal" : "Five lanes; choose your moment", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> hop(-30, 0);
            case KeyEvent.VK_RIGHT -> hop(30, 0);
            case KeyEvent.VK_UP -> hop(0, -50);
            case KeyEvent.VK_DOWN -> hop(0, 50);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Crossing Rush");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Arrows: hop   Reach the top green bank   R: restart</div></html>", SwingConstants.CENTER);
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
