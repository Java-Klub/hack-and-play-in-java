import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.Timer;

/// # Úkol · PegDrop
/// Lekce: 7.1 Dej hodnotám očíslovaná místa — 01-arrays.html
///
/// Pusť kotouček mezi kolíky a miř na cenné přihrádky. Číslo pod přihrádkou má odpovídat získaným bodům.
///
/// **Tvoje mise:** Každá přihrádka vyplácí hodnotu první. Oprav scoreAt(int index), aby dotaz na platnou
/// přihrádku vrátil její vlastní cenu a sám body nepřiděloval.
///
/// Kliknutím nad přihrádkou pusť kotouček, potom porovnej číslo přihrádky
/// dopadu se získanými body.

int scoreAt(int index) {
    // Dodaná ochrana hlásí neplatný index; exception handling přijde v kapitole 12.
    if (index < 0 || index >= values.length) {
        throw new IllegalArgumentException("Unknown bin");
    }
    return values[0];
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 600, HEIGHT = 440;

ArrayList<Point> pegs = new ArrayList<>();
double x, y, vx, vy;
boolean active, done;
int score, bin;
int[] values = {10, 30, 50, 100, 100, 50, 30, 10};
void restart() {
    pegs.clear();
    for (int row = 0; row < 7; row++) {
        for (int column = 0; column < 8; column++) {
            int xx = 35 + column * 75 + (row % 2) * 37;
            if (xx < WIDTH - 15) {
                pegs.add(new Point(xx, 75 + row * 40));
            }
        }
    }
    x = WIDTH / 2;
    y = 25;
    vx = vy = 0;
    active = done = false;
    score = 0;
    bin = -1;
}

void drop(int xx) {
    if (active || done) {
        return;
    }
    x = Math.max(20, Math.min(WIDTH - 20, xx));
    y = 25;
    vx = vy = 0;
    active = true;
}

void step() {
    if (!active) {
        return;
    }
    for (int sub = 0; sub < 4 && active; sub++) {
        vy = Math.min(7, vy + .04);
        x += vx / 4;
        y += vy / 4;
        for (var p : pegs) {
            double dx = x - p.x, dy = y - p.y, distance = Math.hypot(dx, dy);
            if (distance < 14) {
                double nx = distance<.001 ? 1 : dx / distance, ny = distance<.001 ? 0 : dy / distance;
                x = p.x + nx * 14.01;
                y = p.y + ny * 14.01;
                double normal = vx * nx + vy * ny;
                if (normal < 0) {
                    vx -= 1.65 * normal * nx;
                    vy -= 1.65 * normal * ny;
                }
                if (Math.abs(nx)<.1 && Math.abs(vx)<.1) {
                    vx += p.x % 2 == 0 ? .12 : -.12;
                }
            }
        }
        if (x < 9) {
            x = 9;
            vx = Math.abs(vx) * .7;
        }
        if (x > WIDTH - 9) {
            x = WIDTH - 9;
            vx = -Math.abs(vx) * .7;
        }
        vx = Math.max(-6, Math.min(6, vx));
        if (y >= HEIGHT - 12) {
            y = HEIGHT - 12;
            active = false;
            done = true;
            bin = Math.min(7, (int)x / 75);
            score = scoreAt(bin);
        }
    }
}

void draw(Graphics2D g) {
    g.setColor(new Color(235, 242, 250));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(80, 100, 130));
    for (var p : pegs) {
        g.fillOval(p.x - 5, p.y - 5, 10, 10);
    }
    g.setColor(Color.GRAY);
    for (int i = 0; i <= 8; i++) {
        g.drawLine(i * 75, 370, i * 75, HEIGHT);
    }
    for (int i = 0; i < 8; i++) {
        g.drawString("" + values[i], i * 75 + 25, HEIGHT - 20);
    }
    g.setColor(Color.ORANGE);
    g.fillOval((int)x - 9, (int)y - 9, 18, 18);
    g.setColor(Color.BLACK);
    g.drawString(done ? "Bin " + (bin + 1) + " - " + score + " points! R: another disc" : active ? "Bouncing..." : "Click across the top to choose and drop a disc", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        drop(WIDTH / 2);
    }
}

void press(int x, int y, int button) {
    drop(x);
}

void showWindow() {
    var frame = new JFrame("Peg Drop");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: choose a drop position   Space: centre drop   R: next disc</div></html>", SwingConstants.CENTER);
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
