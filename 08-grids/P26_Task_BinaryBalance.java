import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;

/// # Vyřešený úkol · BinaryBalance
/// Lekce: 8.5 Minulost čti, budoucnost zapisuj — 05-generations.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Vyplň malou desku vyváženými černými a bílými políčky bez trojice za sebou. Oranžové nápovědy mají
/// zůstat pevné, tvoje odpovědi měnitelné.
///
/// **Tvoje mise:** Volné políčko se po úpravě uzamkne, protože odpovědi mění i předlohu nápověd. Oprav
/// copyBoard(), aby se oddělily obě úrovně mřížky.
///
/// Spusť program a opakovaným klikáním na neoranžové políčko přepínej neznámou, černou a bílou
/// podle pravidel vyvážení.

int[][] copyBoard(int[][] original) {
    int[][] copy = new int[original.length][];
    for (int row = 0; row < original.length; row++) {
        copy[row] = original[row].clone();
    }
    return copy;
}

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// ### Kopie řádků i vnější vrstvy
///
/// Vnější array (pole) mřížky ukládá row reference (odkazy na řádky). Vnější `clone()` proto kopíruje
/// odkazy na stejné řádky. Zde je jiný dvouřádkový příklad s nezávislými řádky:
///
/// ```java
/// int[][] original = {{1}, {2}};
/// int[][] copied = {original[0].clone(), original[1].clone()};
/// copied[0][0] = 9;
/// IO.println(original[0][0]);
/// ```
///
/// Výstup zůstane jedna, protože změněný řádek patří copied. Předpověz výsledek, kdyby jeho první
/// řádek byl jen `original[0]`. Tvoje deska má více řádků: podle skutečného počtu opakuj operaci
/// kopírování. Každý row clone (klon řádku) zachová jeho aktuální element (prvky). Vysvětli, která
/// vrstva by zůstala sdílená při kopii pouze vnějšího array.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 480, HEIGHT = 400;

int[][] givens = {{1, 0, 0, 2}, {0, 2, 0, 0}, {0, 0, 2, 0}, {2, 0, 0, 1}};
int[][] cells;
void restart() {
    cells = copyBoard(givens);
}

void cycle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4 && givens[y][x] == 0) {
        cells[y][x] = (cells[y][x] + 1) % 3;
    }
}

boolean lineValid(int[] line) {
    int ones = 0;
    for (int i = 0; i < 4; i++) {
        if (line[i]<1 || line[i]>2) {
            return false;
        }
        if (line[i] == 1) {
            ones++;
        }
        if (i >= 2 && line[i] == line[i - 1] && line[i] == line[i - 2]) {
            return false;
        }
    }
    return ones == 2;
}

boolean won() {
    for (int y = 0; y < 4; y++) {
        if (!lineValid(cells[y])) {
            return false;
        }
        int[] column = new int[4];
        for (int x = 0; x < 4; x++) {
            column[x] = cells[x][y];
            if (givens[y][x] != 0 && cells[y][x] != givens[y][x]) {
                return false;
            }
        }
        if (!lineValid(column)) {
            return false;
        }
    }
    return true;
}

void draw(Graphics2D g) {
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 90 + x * 65, yy = 45 + y * 65;
            g.setColor(new Color(205, 210, 220));
            g.fillRect(xx, yy, 62, 62);
            if (cells[y][x] != 0) {
                g.setColor(cells[y][x] == 1 ? Color.BLACK : Color.WHITE);
                g.fillOval(xx + 11, yy + 11, 40, 40);
            }
            if (givens[y][x] != 0) {
                g.setColor(new Color(225, 100, 45));
                g.drawRect(xx + 2, yy + 2, 58, 58);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Balanced!" : "Two black and two white in every row and column", 20, 345);
    g.drawString("No three alike in a row. Orange borders are fixed.", 20, 370);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 90 && y >= 45 && x < 350 && y < 305) {
        cycle((x - 90) / 65, (y - 45) / 65);
    }
}

void showWindow() {
    var frame = new JFrame("Binary Balance");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: unknown / black / white   Orange cells are fixed   R: reset</div></html>", SwingConstants.CENTER);
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
