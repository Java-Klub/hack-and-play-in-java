import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · RippleTank
/// Lekce: 13.5 Nech kreslení pozorovat model — 05-model-view.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Klepni do klidné nádrže a vyšlou se po ní kruhy. Dvě vlny se mohou potkat, zesílit či vyrušit a změnit
/// pár kliknutí v pohyblivý obrazec.
///
/// **Tvoje mise:** Platné klepnutí přidá poruchu, ale nádrž okamžitě zastaví. Obnov přechod, který dovolí
/// nové vlně skutečně se šířit.
///
/// Po spuštění programu klikni dovnitř vody pro vytvoření vln, pomocí Space zastav pohyb a
/// klávesou R obnov klidnou nádrž.

void tap(int x, int y) {
    if (x > 0 && y > 0 && x < COLUMNS - 1 && y < ROWS - 1) {
        height[y][x] = Math.min(120, height[y][x] + 60);
        paused = false;
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř nástroj dvojité kapky: kliknutí přidá kladné vychýlení a stejně vzdálené záporné vychýlení.
/// Porovnej setkání vln se dvěma kladnými kapkami. Zachovej chráněný okraj; nezapisuj mimo vnitřní
/// buňky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 640, HEIGHT = 440;

final int COLUMNS = 64, ROWS = 44;
double[][] height, previous;
boolean paused;
void restart() {
    height = new double[ROWS][COLUMNS];
    previous = new double[ROWS][COLUMNS];
    paused = true;
}

void step() {
    if (paused) {
        return;
    }
    double[][] next = new double[ROWS][COLUMNS];
    for (int y = 1; y < ROWS - 1; y++) {
        for (int x = 1; x < COLUMNS - 1; x++) {
            next[y][x] = ((height[y][x - 1] + height[y][x + 1] + height[y - 1][x] + height[y + 1][x]) / 2 - previous[y][x]) * .992;
        }
    }
    previous = height;
    height = next;
}

double energy() {
    double sum = 0;
    for (var row : height) {
        for (double h : row) {
            sum += h * h;
        }
    }
    return sum;
}

void draw(Graphics2D g) {
    for (int y = 0; y < ROWS; y++) {
        for (int x = 0; x < COLUMNS; x++) {
            int value = (int)Math.max(-100, Math.min(100, height[y][x] * 3));
            g.setColor(new Color(70 + Math.max(0, value), 140 + value, 210 - Math.max(0, -value)));
            g.fillRect(x * 10, y * 10, 10, 10);
        }
    }
    g.setColor(Color.WHITE);
    g.drawString(paused ? "Paused - click to disturb water" : "Click more ripples; watch interference and edge reflections", 12, 22);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code == KeyEvent.VK_SPACE) {
        paused = !paused;
    }
}

void press(int x, int y, int button) {
    tap(x / 10, y / 10);
}

void showWindow() {
    var frame = new JFrame("Ripple Tank");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: ripple   Space: pause   R: still water</div></html>", SwingConstants.CENTER);
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
