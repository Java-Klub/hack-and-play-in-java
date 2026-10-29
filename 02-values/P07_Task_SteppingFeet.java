import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Úkol · SteppingFeet
/// Lekce: 2.2 Změň uloženou hodnotu — 02-change.html
///
/// **Vrať obě nohy do obrázku.** Dva obdélníky mají společnou vodorovnou pozici `x`. Teď začínají
/// mimo levý okraj okna.
///
/// **Úkol:** V `startingPosition` níže změň druhý řádek tak, aby po provedení obou řádků bylo `x`
/// rovno `80`. První řádek `x = 20;` ponech. Číslo vpravo od `=` je hodnota, kterou řádek uloží
/// do `x`.
///
/// **Výsledek:** Při novém spuštění budou obě nohy vidět a horní popisek ukáže x = 80. Space
/// spustí jejich pohyb; potom zkus klávesou C skrýt pruhy a porovnej zdánlivé rychlosti.

void startingPosition() {
    x = 20;
    x = -80;
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Dvě řešení mohou dát stejné x = 80: pevné assignment (přiřazení) i výpočet ze starého x. Pro tuto
/// misi jsou obě v pořádku. Později je rozlišíš pokusem s jinou počáteční hodnotou. Pravá strana
/// assignment se vyhodnotí před uložením nové hodnoty; obě nohy pak čtou stejné x.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Porovnej dvě verze: nastav x přímo na 80, nebo k předchozímu x přičti 60. Nejprve obě spusť s
/// počátečním x = 20; potom první assignment (přiřazení) změň na 40. Před spuštěním předpověz obě
/// konečné pozice. Která verze zachovává cíl a která posun?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
