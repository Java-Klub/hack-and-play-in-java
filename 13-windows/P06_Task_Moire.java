import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Point2D;

/// # Vyřešený úkol · Moire
/// Lekce: 13.1 Kresli v okně Javy — 01-drawing.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Polož jemné mřížky přes sebe a sleduj vznik velkých pohyblivých pásů. Moaré vzniká jejich překrytím,
/// nikoli přímým kreslením těchto pásů.
///
/// **Tvoje mise:** Otočená mřížka je vodorovně vystředěná, ale svisle přilepená k hornímu okraji. Oprav
/// screen coordinate (souřadnice obrazovky), aby se otáčela kolem zamýšleného středu.
///
/// Po spuštění programu pomocí Left/Right otáčej jednu grid (mřížku) a Up/Down měň rozestupy
/// pro zkoumání překrytí; R obnoví počáteční vzor.

Point2D.Double gridPoint(int column, int row, double a) {
    double x = column * spacing, y = row * spacing;
    return new Point2D.Double(WIDTH / 2 + x * Math.cos(a) - y * Math.sin(a), HEIGHT / 2 + x * Math.sin(a) + y * Math.cos(a));
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř detektor pohybu bez pohyblivých součástí: zvol dvě kontrastní barvy mřížek a najdi úhel,
/// kde drobný stisk klávesy vyvolá zdánlivě obrovské vlny. Zapiš úhel a rozteč, aby je někdo jiný
/// mohl zopakovat.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

double angle;
int spacing;
void restart() {
    angle = .07;
    spacing = 22;
}

void adjust(int amount) {
    spacing = Math.max(12, Math.min(40, spacing + amount));
}

int extent() {
    return (int)Math.ceil(Math.hypot(WIDTH, HEIGHT) / 2 / spacing) + 1;
}

void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    int limit = extent();
    for (int layer = 0; layer < 2; layer++) {
        double a = layer == 0 ? 0 : angle;
        g.setColor(layer == 0 ? new Color(20, 20, 20, 200) : new Color(40, 80, 170, 170));
        for (int y = -limit; y <= limit; y++) {
            for (int x = -limit; x <= limit; x++) {
                var p = gridPoint(x, y, a);
                g.fillOval((int)p.x - 2, (int)p.y - 2, 4, 4);
            }
        }
    }
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString(String.format("Angle %.2f degrees   Spacing %d", Math.toDegrees(angle), spacing), 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> angle -= .01;
            case KeyEvent.VK_RIGHT -> angle += .01;
            case KeyEvent.VK_UP -> adjust(1);
            case KeyEvent.VK_DOWN -> adjust(-1);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Moir\u00e9");
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
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left/Right: rotate   Up/Down: spacing   R: reset</div></html>", SwingConstants.CENTER);
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
