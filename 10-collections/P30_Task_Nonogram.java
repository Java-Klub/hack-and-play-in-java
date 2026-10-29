import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

/// # Úkol · Nonogram
/// Lekce: 10.5 Nech data projít filtrem — 05-streams.html
///
/// **Dopočítej poslední černou skupinu.** Čísla u řádku obrázku říkají délky souvislých skupin
/// černých políček. Například černá, černá, bílá, černá potřebuje nápovědu `[2, 1]`. Výchozí
/// výpočet poslední jedničku ztratí.
///
/// **Úkol:** Doplň `runs` níže, aby započítala také černou skupinu sahající až na konec vstupu.
/// Dřívější skupiny zachovej. Prázdný nebo celý bílý řádek nesmí vytvořit žádné číslo.
///
/// **Výsledek:** `[1, 1, 0, 1]` vrátí délky `[2, 1]`; `[0, 0]` vrátí prázdné pole. Potom můžeš
/// podle správných čísel odhalovat obrázek: levý klik barví černě, pravý bíle.

int[] runs(int[] values) {
    var result = new ArrayList<Integer>();
    int count = 0;
    for (int value : values) {
        if (value == 1) {
            count++;
        } else if (count > 0) {
            result.add(count);
            count = 0;
        }
    }
    return result.stream().mapToInt(Integer::intValue).toArray();
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Dokončení run (souvislé skupiny)
///
/// `result` je připravený rostoucí List (seznam). Jeho `add(count)` připojí délku jedné dokončené run
/// (souvislé skupiny); nenahrazuje dřívější délky. Závěrečný stream conversion (převod proudu) jen
/// vrátí zaznamenané délky jako int[]. Soustřeď se na okamžik dokončení skupiny. Porovnej řádek
/// končící bíle s řádkem končícím černě: co ukončí poslední skupinu? Prázdný či čistě bílý řádek
/// nesmí vymyslet černou skupinu nulové délky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 450, HEIGHT = 420;

int[][] picture = {{0, 0, 1, 0, 0}, {0, 1, 1, 1, 0}, {1, 1, 1, 1, 1}, {0, 1, 1, 1, 0}, {0, 0, 1, 0, 0}};
int[][] cells;
int[][] rowClues = new int[5][], colClues = new int[5][];
void restart() {
    cells = new int[5][5];
    for (int i = 0; i < 5; i++) {
        rowClues[i] = runs(picture[i]);
        int[] col = new int[5];
        for (int y = 0; y < 5; y++) {
            col[y] = picture[y][i];
        }
        colClues[i] = runs(col);
    }
}

void mark(int x, int y, boolean white) {
    if (x >= 0 && y >= 0 && x < 5 && y < 5) {
        cells[y][x] = white ? (cells[y][x] == 2 ? 0 : 2) : (cells[y][x] == 1 ? 0 : 1);
    }
}

boolean won() {
    for (int i = 0; i < 5; i++) {
        for (int c : cells[i]) {
            if (c == 0) {
                return false;
            }
        }
        int[] col = new int[5];
        for (int y = 0; y < 5; y++) {
            col[y] = cells[y][i];
        }
        if (!Arrays.equals(runs(cells[i]), rowClues[i]) || !Arrays.equals(runs(col), colClues[i])) {
            return false;
        }
    }
    return true;
}

void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    for (int i = 0; i < 5; i++) {
        g.setColor(Color.BLACK);
        g.drawString(Arrays.toString(rowClues[i]), 25, 110 + i * 50);
        g.drawString(colClues[i].length == 0 ? "0" : "" + colClues[i][0], 105 + i * 50, 65);
    }
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            int xx = 85 + x * 50, yy = 80 + y * 50;
            g.setColor(cells[y][x] == 1 ? new Color(35, 50, 75) : cells[y][x] == 2 ? Color.WHITE : new Color(215, 220, 230));
            g.fillRect(xx, yy, 48, 48);
            if (cells[y][x] == 2) {
                g.setColor(Color.GRAY);
                g.drawLine(xx + 18, yy + 18, xx + 30, yy + 30);
                g.drawLine(xx + 30, yy + 18, xx + 18, yy + 30);
            }
        }
    }
    g.setColor(Color.BLACK);
    g.drawString(won() ? "All clues satisfied!" : "Grey = undecided; mark every square", 25, 380);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 80 && x < 335 && y < 330) {
        mark((x - 85) / 50, (y - 80) / 50, button == MouseEvent.BUTTON3);
    }
}

void showWindow() {
    var frame = new JFrame("Nonogram");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left click: black / undecided   Right click: white / undecided<br>R: reset</div></html>", SwingConstants.CENTER);
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
