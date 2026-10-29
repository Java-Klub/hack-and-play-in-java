import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Úkol · CannonPuzzle
/// Lekce: 5.3 Vrať výsledek volajícímu — 03-results.html
///
/// Zamiř dělem na oranžový cíl a sleduj zakřivený let střely v gravitaci. Každý pokus ti umožní viditelně
/// porovnat směr a sílu výstřelu.
///
/// **Tvoje mise · Probuď dělo:** Výchozí verze nechá střelu spadnout u hlavně, protože vypočtená rychlost
/// je nula.
/// Oprav launchSpeed tak, aby natažení 100 dalo rychlost 9 a natažení vzhůru zachovalo záporné znaménko.
///
/// Spusť program, táhni od děla směrem vpravo nahoru a pusť myš pro výstřel na oranžový cíl; R
/// připraví další pokus.

double launchSpeed(double drag) {
    return drag * (9 / 100);
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Po opravě převodu zkus dva směry zamíření se stejnou délkou tahu. Předpověz, který výstřel
/// vystoupá výš. Potom záměrně změň jen měřítko a ověř, zda původní předpověď o směru stále platí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
