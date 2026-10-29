import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/// # Úkol · Spirograph
/// Lekce: 2.3 Nech program počítat — 03-calculations.html
///
/// Kresli uzavřené spletité křivky inspirované perem pohybujícím se uvnitř větší kružnice. Změnou
/// vnitřního poloměru a vzdálenosti pera získáš výrazně odlišné vzory.
///
/// **Tvoje mise · Zachraň ztracenou přesnost:** Výpočet křivky zahazuje část poměru poloměrů. Oprav
/// radiusRatio tak, aby celé poloměry
/// mohly dát desetinný poměr, včetně výsledku menšího než jedna.
///
/// Spusť program, šipkami vlevo/vpravo měň vnitřní poloměr a nahoru/dolů vzdálenost pera a
/// porovnávej překreslené křivky.

double radiusRatio(int outer, int innerRadius) {
    return (outer - innerRadius) / innerRadius;
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Poloměry přicházejí jako int parameter (celočíselné parametry), takže výraz tvořený jen těmito
/// hodnotami může používat integer division (celočíselné dělení) i uvnitř method (metody) vracející
/// double. Cast (převod) se vztahuje na hodnotu bezprostředně za ním. Neutrální příklad: se sedmi
/// seats a čtyřmi rows dá `(double) seats / rows` hodnotu 1.75, zatímco `(double) (seats / rows)`
/// pouze 1.0, protože desetinná část už zmizela. Cast nemění declaration (deklaraci) parametru. Nyní
/// předpověz oba výrazy pro pět seats a dvě rows a potom rozhodni, kam převod patří ve tvém výpočtu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Existujícími šipkami najdi křivku připomínající květinu a jinou připomínající uzel. Kamarádovi
/// zapiš jen dvě zobrazená nastavení jako recepty. Vysvětli, proč celočíselný poměr dělal některá
/// nastavení nečekaně podobná.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

int inner, pen;
BufferedImage canvas;
int gcd(int a, int b) {
    while (b != 0) {
        int next = a % b;
        a = b;
        b = next;
    }
    return a;
}

double period() {
    return 2 * Math.PI * inner / gcd(120, inner);
}

Point2D.Double point(double t) {
    double a = 120 - inner;
    return new Point2D.Double(a * Math.cos(t) + pen * Math.cos(radiusRatio(120, inner) * t), a * Math.sin(t) - pen * Math.sin(radiusRatio(120, inner) * t));
}

int samples() {
    return Math.min(14000, (int)(period() * 280) + 1);
}

void render() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.getHSBColor(0, .75f, .75f));
    double scale = (HEIGHT - 65) / 2.0 / Math.max(1, 120 - inner + pen), period = period();
    var previous = point(0);
    int n = samples();
    for (int i = 1; i <= n; i++) {
        var p = point(period * i / n);
        g.draw(new Line2D.Double(WIDTH / 2 + previous.x * scale, HEIGHT / 2 + previous.y * scale, WIDTH / 2 + p.x * scale, HEIGHT / 2 + p.y * scale));
        previous = p;
    }
    g.dispose();
}

void restart() {
    inner = 45;
    pen = 65;
    render();
}

void adjust(int r, int d) {
    inner = Math.max(15, Math.min(110, inner + r));
    pen = Math.max(0, Math.min(140, pen + d));
    render();
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.BLACK);
    g.drawString("Inner radius: " + inner + "  Pen distance: " + pen, 10, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> adjust(-5, 0);
            case KeyEvent.VK_RIGHT -> adjust(5, 0);
            case KeyEvent.VK_UP -> adjust(0, 5);
            case KeyEvent.VK_DOWN -> adjust(0, -5);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Spirograph");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left/Right: inner radius   Up/Down: pen distance   R: reset</div></html>", SwingConstants.CENTER);
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
