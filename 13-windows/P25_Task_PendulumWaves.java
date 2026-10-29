import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Úkol · PendulumWaves
/// Lekce: 13.4 Posouvej simulaci po malých krocích — 04-timers.html
///
/// Spusť řadu kyvadel společně a sleduj, jak se rozcházejí do pohyblivých vln. Různé periody tvoří vzory,
/// které se opakovaně rozdělí a zase setkají.
///
/// **Tvoje mise:** Čas postupuje jen při pause (pozastavení), takže ovládání přehrávání funguje obráceně.
/// Oprav, kdy se smí měnit společný čas simulace.
///
/// Po spuštění programu klávesou Space rozběhni či zastav kyvadla, pomocí Up/Down měň rychlost
/// a klávesou R je znovu srovnej.

void step() {
    if (paused) {
        time += .02 * speed;
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Staň se choreografem: změň řadu period tak, aby sousední kyvadla tvořila dvě rodiny místo jedné
/// plynulé řady. Odhadni, kdy by se mohl vzor zopakovat, a porovnej pohyb s předpovědí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 660, HEIGHT = 400;

double time, speed;
boolean paused;
void restart() {
    time = 0;
    speed = 1;
    paused = true;
}

double angle(int index) {
    return .25 * Math.cos(2 * Math.PI * (20 + index) * time / 30);
}

double length(int index) {
    return 240 * Math.pow(20.0 / (20 + index), 2);
}

void adjust(double change) {
    speed = Math.max(.25, Math.min(2, speed + change));
}

void draw(Graphics2D g) {
    for (int i = 0; i < 12; i++) {
        double a = angle(i), len = length(i);
        int xx = 35 + i * 51, yy = 60;
        int bx = xx + (int)(Math.sin(a) * len), by = yy + (int)(Math.cos(a) * len);
        g.setColor(Color.GRAY);
        g.drawLine(xx, yy, bx, by);
        g.setColor(Color.getHSBColor(i / 12f, .7f, .8f));
        g.fillOval(bx - 9, by - 9, 18, 18);
    }
    g.setColor(Color.BLACK);
    g.drawString(String.format("Time %.1f s   Speed %.2fx   Realign at 30, 60, 90... seconds", time, speed), 15, 28);
    g.drawString(paused ? "Space starts the wave" : "Twelve different periods; all share the same phase every 30 seconds", 15, 365);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_UP -> adjust(.25);
            case KeyEvent.VK_DOWN -> adjust(-.25);
        }
    }
}

void showWindow() {
    var frame = new JFrame("Pendulum Waves");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   Up / Down: speed   R: return to aligned start</div></html>", SwingConstants.CENTER);
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
