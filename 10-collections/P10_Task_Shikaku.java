import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/// # Úkol · Shikaku
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Kresli obdélníky kolem nápověd tak, aby plocha každého odpovídala jeho jedinému číslu. Špatný odhad
/// smaž nebo vrať a zkus jiné rozdělení.
///
/// **Tvoje mise:** Vrácení tahu sdílí živý seznam obdélníků, takže přidání či odebrání přepíše i uložený
/// stav. Oprav snapshot(), aby uchovalo členy seznamu.
///
/// Spusť program a tažením vyznač obdélník kolem jedné nápovědy, potom jej zkus vrátit klávesou
/// U nebo smazat pravým kliknutím.

ArrayList<Box> snapshot() {
    return boxes;
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// ### Použij model právě zde
///
/// Pro tento připravený List (seznam) vytváří `new ArrayList<>(existing)` samostatný mutable container
/// (měnitelný kontejner) se stejnými prvky. Box record (datový záznam) je immutable (neměnný), takže
/// jej obě List mohou sdílet. Po kopii dvou Box a vyprázdnění živé List zůstanou dva Box v uložené
/// List. Zachováváš členství obdélníků, nekreslíš novou geometrii.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 470, HEIGHT = 400;

record Box(int x, int y, int width, int height) {
    boolean contains(int xx, int yy) {
        return xx >= x && yy >= y && xx < x + width && yy < y + height;
    }
}

int[][] clues = {{0, 0, 0, 6, 0}, {6, 0, 0, 0, 0}, {0, 0, 0, 0, 4}, {0, 0, 3, 0, 0}, {0, 4, 0, 2, 0}};
ArrayList<Box> boxes = new ArrayList<>();
int startX = -1, startY, endX, endY;
String message;
void restart() {
    boxes.clear();
    previousBoxes = null;
    startX = -1;
    message = "Drag a rectangle around exactly one clue";
}

boolean add(int x1, int y1, int x2, int y2) {
    int x = Math.min(x1, x2), y = Math.min(y1, y2), w = Math.abs(x1 - x2) + 1, h = Math.abs(y1 - y2) + 1;
    if (x < 0 || y < 0 || x + w > 5 || y + h > 5) {
        return false;
    }
    int count = 0, area = 0;
    for (int yy = y; yy < y + h; yy++) {
        for (int xx = x; xx < x + w; xx++) {
            if (clues[yy][xx] != 0) {
                count++;
                area = clues[yy][xx];
            }
            for (var b : boxes) {
                if (b.contains(xx, yy)) {
                    return false;
                }
            }
        }
    }
    if (count != 1 || area != w * h) {
        return false;
    }
    previousBoxes = snapshot();
    boxes.add(new Box(x, y, w, h));
    return true;
}

void remove(int x, int y) {
    boolean found = false;
    for (var box : boxes) {
        if (box.contains(x, y)) {
            found = true;
        }
    }
    if (found) {
        previousBoxes = snapshot();
        boxes.removeIf(box -> box.contains(x, y));
        message = "Rectangle removed";
    }
}

boolean won() {
    int area = 0;
    for (var b : boxes) {
        area += b.width * b.height;
    }
    return area == 25;
}

int cellX(int x) {
    return (x - 80) / 55;
}

int cellY(int y) {
    return (y - 40) / 55;
}

boolean inside(int x, int y) {
    return x >= 80 && y >= 40 && x < 355 && y < 315;
}

void draw(Graphics2D g) {
    Color[] palette = {new Color(210, 230, 255), new Color(255, 225, 195), new Color(215, 245, 210), new Color(240, 215, 250), new Color(255, 245, 185), new Color(200, 245, 240)};
    for (int i = 0; i < boxes.size(); i++) {
        var b = boxes.get(i);
        g.setColor(palette[i % palette.length]);
        g.fillRect(80 + b.x * 55, 40 + b.y * 55, b.width * 55, b.height * 55);
    }
    g.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i <= 5; i++) {
        g.drawLine(80 + i * 55, 40, 80 + i * 55, 315);
        g.drawLine(80, 40 + i * 55, 355, 40 + i * 55);
    }
    g.setStroke(new BasicStroke(3));
    g.setColor(Color.DARK_GRAY);
    for (var b : boxes) {
        g.drawRect(80 + b.x * 55 + 2, 40 + b.y * 55 + 2, b.width * 55 - 4, b.height * 55 - 4);
    }
    if (startX >= 0) {
        g.setColor(Color.BLUE);
        g.drawRect(80 + Math.min(startX, endX) * 55, 40 + Math.min(startY, endY) * 55, (Math.abs(startX - endX) + 1) * 55, (Math.abs(startY - endY) + 1) * 55);
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    g.setColor(Color.BLACK);
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            if (clues[y][x] != 0) {
                g.drawString("" + clues[y][x], 102 + x * 55, 74 + y * 55);
            }
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString(won() ? "Every square covered!" : message, 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_U) {
        undo();
    }
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (inside(x, y)) {
        if (button == MouseEvent.BUTTON3) {
            remove(cellX(x), cellY(y));
        }
        else {
            startX = endX = cellX(x);
            startY = endY = cellY(y);
        }
    }
}

void drag(int x, int y) {
    if (startX >= 0 && inside(x, y)) {
        endX = cellX(x);
        endY = cellY(y);
    }
}

void release(int x, int y) {
    if (startX >= 0) {
        if (inside(x, y)) {
            endX = cellX(x);
            endY = cellY(y);
            message = add(startX, startY, endX, endY) ? "Rectangle added" : "Rejected: check clue, area and overlap";
        }
        startX = -1;
    }
}

void showWindow() {
    var frame = new JFrame("Shikaku");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Drag: add rectangle   Right click: remove rectangle<br>U: undo one change   R: reset</div></html>", SwingConstants.CENTER);
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

ArrayList<Box> previousBoxes;

void undo() {
    if (previousBoxes != null) {
        boxes = previousBoxes;
        previousBoxes = null;
        startX = -1;
        message = "Previous layout restored";
    }
}
// </editor-fold>
