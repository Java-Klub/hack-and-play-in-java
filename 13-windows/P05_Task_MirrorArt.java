import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/// # Úkol · MirrorArt
/// Lekce: 13.1 Kresli v okně Javy — 01-drawing.html
///
/// Nakresli jeden tah a nech kruh zrcadlených kopií proměnit jej v růžici. Změna počtu os může ze
/// stejného gesta udělat úplně jiný obrazec.
///
/// **Tvoje mise:** Implementuj celý coordinate adapter (převod souřadnic) v transform. Odečti střed
/// plátna, případně odraz místní y, otoč o 2π × copy / axes a střed zase přičti. Vrať nový bod;
/// původní tah neměň.
///
/// Po spuštění programu táhni čáru a prohlédni její odrazy, pomocí Up/Down měň osy zrcadlení a
/// R kresbu vymaž a obnov.

Point2D.Double transform(int x, int y, int copy, boolean reflect) {
    // Sestav místní souřadnice, odraz, otočení a návrat na plátno.
    return new Point2D.Double(0, 0);
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni dvoubarevný znak: v připraveném kreslicím cyklu dej odraženým tahům jinou barvu než
/// otočeným. Nakresli jediný promyšlený tah a vyber počet os, který jej promění ve tvůj znak.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
