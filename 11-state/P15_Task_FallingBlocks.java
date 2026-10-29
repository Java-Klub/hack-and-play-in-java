import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · FallingBlocks
/// Lekce: 11.3 Odmítnutí nesmí poškodit stav — 03-invariants.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Posouvej a otáčej padající dílky do skládačky. Zkušební otočení u stěny se má buď vejít, nebo čistě
/// odmítnout.
///
/// **Tvoje mise:** Kontrola navrhované polohy mění skutečnou polohu dílku a způsobuje skoky či dvojí
/// pohyb. Oprav fits(), aby zůstalo jen dotazem.
///
/// Šipkami vlevo a vpravo posouvej padající dílek a šipkou nahoru vyzkoušej
/// otočení u skládačky nebo stěny.

boolean fits(int[][] cells, int x, int y) {
    for (var c : cells) {
        int xx = x + c[0], yy = y + c[1];
        if (xx < 0 || xx >= 10 || yy < 0 || yy >= 16 || board[yy][xx] != 0) {
            return false;
        }
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 540, HEIGHT = 430;

int[][] board = new int[16][10];
int[][] piece;
int px, py, kind, lines, ticks, nextKind;
boolean started, over;
int[][] shape(int type) {
    return switch (type) {
        case 0 -> new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}};
        case 1 -> new int[][]{{-1, 0}, {0, 0}, {1, 0}};
        default -> new int[][]{{0, -1}, {0, 0}, {1, 0}};
    };
}

void restart() {
    board = new int[16][10];
    lines = ticks = nextKind = 0;
    started = over = false;
    spawn();
}

void spawn() {
    kind = nextKind++ % 3;
    piece = shape(kind);
    px = 4;
    py = 1;
    if (!fits(piece, px, py)) {
        over = true;
    }
}

void move(int dx) {
    if (!over && fits(piece, px + dx, py)) {
        px += dx;
    }
}

void rotate() {
    if (over || kind == 0) {
        return;
    }
    int[][] rotated = new int[piece.length][2];
    for (int i = 0; i < piece.length; i++) {
        rotated[i][0] = -piece[i][1];
        rotated[i][1] = piece[i][0];
    }
    if (fits(rotated, px, py)) {
        piece = rotated;
    }
}

int clearRows() {
    int destination = 15, removed = 0;
    for (int y = 15; y >= 0; y--) {
        boolean full = true;
        for (int cell : board[y]) {
            if (cell == 0) {
                full = false;
            }
        }
        if (full) {
            removed++;
        }
        else {
            board[destination--] = board[y].clone();
        }
    }
    while (destination >= 0) {
        board[destination--] = new int[10];
    }
    lines += removed;
    return removed;
}

void down() {
    if (over) {
        return;
    }
    if (fits(piece, px, py + 1)) {
        py++;
    }
    else {
        for (var c : piece) {
            board[py + c[1]][px + c[0]] = kind + 1;
        }
        clearRows();
        spawn();
    }
}

void drop() {
    if (over) {
        return;
    }
    while (fits(piece, px, py + 1)) {
        py++;
    }
    down();
}

void step() {
    if (started && !over && ++ticks % 25 == 0) {
        down();
    }
}

void draw(Graphics2D g) {
    Color[] colours = {Color.WHITE, new Color(255, 180, 60), new Color(60, 180, 240), new Color(170, 110, 230)};
    g.setColor(new Color(22, 28, 40));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 10; x++) {
            g.setColor(board[y][x] == 0 ? new Color(45, 50, 65) : colours[board[y][x]]);
            g.fillRect(30 + x * 24, 25 + y * 24, 23, 23);
        }
    }
    g.setColor(colours[kind + 1]);
    for (var c : piece) {
        g.fillRect(30 + (px + c[0]) * 24, 25 + (py + c[1]) * 24, 23, 23);
    }
    g.setColor(Color.WHITE);
    g.drawString("Lines: " + lines, 295, 65);
    g.drawString(over ? "TOP OUT - R: retry" : !started ? "Press an arrow" : "Three shapes; no wall kicks", 295, 95);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && !over && code != KeyEvent.VK_R) {
        if (code == KeyEvent.VK_LEFT) {
            started = true;
            move(-1);
        }
        if (code == KeyEvent.VK_RIGHT) {
            started = true;
            move(1);
        }
        if (code == KeyEvent.VK_UP) {
            started = true;
            rotate();
        }
        if (code == KeyEvent.VK_DOWN) {
            started = true;
            down();
        }
        if (code == KeyEvent.VK_SPACE) {
            started = true;
            drop();
        }
    }
}

void showWindow() {
    var frame = new JFrame("Falling Blocks");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: move   Up: rotate   Down: lower   Space: drop   R: restart</div></html>", SwingConstants.CENTER);
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
