import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.Timer;

/// # Úkol · VirtualScribbler
/// Lekce: 6.2 Dej počítadlu vlastní místo — 02-for.html
///
/// Nech neposedné pero rozvíjet kresbu po plátně a potom řiď jeho tah a roztěkanost. Zastav
/// zajímavý vzor a prohlédni si ho před vyčištěním plátna pro další pokus.
///
/// **Tvoje mise:** Změň Signature na opakovatelný čtvercový podpis: začni uprostřed, projeď čtyři rovné
/// stejně dlouhé strany po třiceti krocích pera a skonči pozastavený v počátku. Počítadlo ukáže 120.
/// Dřívější drift (zatáčení), jitter (náhodné odchylky), poloha ani kresba podpis neovlivní. Kreslení i
/// krokovací tlačítka už fungují.
///
/// Klikni na Signature, potom prozkoumej pero pomocí Space a šipek. Klikni znovu na Signature: současný
/// recept dá novou přímku místo čtverce.

void signature() {
    restart();
    drift = 0;
    jitter = 0;
    oneShot(24);
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// Pero ujede 2.2 pixelu za krok. angle měří směr v radians (radiánech): 0 doprava, Math.PI / 2 dolů,
/// Math.PI doleva a 3 * Math.PI / 2 nahoru. Přičtení Math.PI / 2 znamená čtvrtotáčku. Nulové drift a
/// jitter zachovají zvolený směr. paused true brání kreslení; restart vyčistí plátno a vystředí pero.
/// Jde o existující hodnoty model (modelu), nikoli nové kreslicí API.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
void burst(int count) {
    if (count < 0 || count > 60) {
        return;
    }
    for (int i = 0; i < count; i++) {
        step();
    }
}

final int WIDTH = 640, HEIGHT = 450;

BufferedImage canvas;
Random random;
double x, y, angle;
int drift, jitter, steps;
boolean paused;
void clear() {
    var g = canvas.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.dispose();
}

void restart() {
    canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    clear();
    random = new Random(71);
    x = WIDTH / 2;
    y = HEIGHT / 2;
    angle = 0;
    drift = 3;
    jitter = 4;
    steps = 0;
    paused = true;
}

void adjust(int d, int j) {
    drift = Math.max(-10, Math.min(10, drift + d));
    jitter = Math.max(0, Math.min(10, jitter + j));
}

void step() {
    if (paused) {
        return;
    }
    angle += drift * .008 + random.nextGaussian() * jitter * .04;
    double nx = x + Math.cos(angle) * 2.2, ny = y + Math.sin(angle) * 2.2;
    if (nx < 2 || nx > WIDTH - 3) {
        angle = Math.PI - angle;
        nx = Math.max(2, Math.min(WIDTH - 3, nx));
    }
    if (ny < 2 || ny > HEIGHT - 3) {
        angle = -angle;
        ny = Math.max(2, Math.min(HEIGHT - 3, ny));
    }
    var g = canvas.createGraphics();
    g.setColor(Color.getHSBColor((steps++ % 1500) / 1500f, .75f, .7f));
    g.setStroke(new BasicStroke(1.5f));
    g.draw(new Line2D.Double(x, y, nx, ny));
    g.dispose();
    x = nx;
    y = ny;
}

void draw(Graphics2D g) {
    g.drawImage(canvas, 0, 0, null);
    g.setColor(Color.RED);
    g.drawOval((int)x - 4, (int)y - 4, 8, 8);
    g.setColor(new Color(245, 245, 245));
    g.fillRect(0, 0, WIDTH, 28);
    g.setColor(Color.BLACK);
    g.drawString("Steps: " + steps + "  Drift: " + drift + "  Jitter: " + jitter + "  " + (paused ? "Paused" : "Drawing"), 10, 20);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_SPACE -> paused = !paused;
            case KeyEvent.VK_LEFT -> adjust(-1, 0);
            case KeyEvent.VK_RIGHT -> adjust(1, 0);
            case KeyEvent.VK_UP -> adjust(0, 1);
            case KeyEvent.VK_DOWN -> adjust(0, -1);
            case KeyEvent.VK_C -> clear();
        }
    }
}

void oneShot(int count) {
    paused = false;
    burst(count);
    paused = true;
}

void showWindow() {
    var frame = new JFrame("Virtual Scribbler");
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
        burst(4);
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent e) {
            timer.stop();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Space: play / pause   Arrows: drift / jitter   C: clear   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    var controls = new JPanel(new BorderLayout());
    controls.add(help, BorderLayout.NORTH);
    var buttons = new JPanel();
    var stamp = new JButton("Signature");
    stamp.setFocusable(false);
    stamp.addActionListener(e -> {
        signature();
        panel.repaint();
        panel.requestFocusInWindow();
    });
    buttons.add(stamp);
    for (int count : new int[]{1, 12}) {
        var button = new JButton(count == 1 ? "1 step" : "12 steps");
        button.setFocusable(false);
        button.addActionListener(e -> {
            oneShot(count);
            panel.repaint();
            panel.requestFocusInWindow();
        });
        buttons.add(button);
    }
    controls.add(buttons, BorderLayout.SOUTH);
    frame.add(controls, BorderLayout.SOUTH);
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
