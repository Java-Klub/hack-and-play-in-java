import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Vyřešený úkol · MissingTriangle
/// Lekce: 1.2 Pořadí příkazů — 02-order.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Vrať tři vykrojené kruhy.** Po spuštění je obrázek prázdný, protože `paintDisc` zatím nic
/// nekreslí.
///
/// **Úkol:** V `paintDisc` níže nahraď řádek začínající `//` dvěma samostatnými řádky. Použij obě
/// připravená volání, každé jednou; jejich pořadí vyber sám:
///
/// - `whiteBite(g, i);` nakreslí bílý výřez.
/// - `darkDisc(g, i);` nakreslí plný černý kruh.
///
/// Zápisy včetně `(g, i);` opiš celé. Nepiš je za `//`: takový řádek je jen komentář a Java ho
/// neprovede.
///
/// **Výsledek:** Objeví se tři černé kruhy s bílými výřezy. Výřezy vytvoří dojem bílého
/// trojúhelníku mezi kruhy; kdyby zůstaly kruhy celé černé, změň pořadí svých dvou řádků.

void paintDisc(Graphics2D g, int i) {
    darkDisc(g, i);
    whiteBite(g, i);
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Klikáním otoč kotouče tak, aby trojúhelník zmizel. Připrav kamarádovi jednu přesvědčivou a jednu
/// rozbitou iluzi. Který kotouč má největší účinek? Existující barvy změň až po porovnání obou
/// sestav.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
