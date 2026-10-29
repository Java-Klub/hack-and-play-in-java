import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

/// # Vyřešený úkol · Flood
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Rozšiřuj jednobarevnou oblast z levého horního rohu, až pokryje desku. Vyber novou barvu, která
/// propojí užitečné sousední ostrůvky, a sleduj, kam se každá volba rozlije.
///
/// **Tvoje mise:** Změna barvy dosáhne k prvním sousedům a potom opustí již objevená políčka. Oprav
/// spread tak, aby zpracovala celý rostoucí pracovní seznam; připravené kontroly sousedů zachovej.
///
/// Zvol barvu kliknutím na její políčko v paletě nebo stiskem 1–4; sleduj změny
/// propojených políček a pomocí R obnov desku.

void spread(ArrayList<Point> queue, int old, int colour) {
    int index = 0;
    while (index < queue.size()) {
        Point point = queue.get(index);
        visitNeighbours(queue, point, old, colour);
        index++;
    }
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Zde je queue připravený pracovní seznam souřadnic objevených políček. index počítá již zpracované
/// položky; queue.size() oznamuje dosud objevený počet. Například průchod začínající s index 0 a
/// velikostí 1 může objevit dva sousedy: po posunu má index 1 a velikost je 3, takže práce zbývá.
/// Velikost se záměrně ověřuje znovu. visitNeighbours každé nové políčko přebarví před přidáním, čímž
/// brání opakovanému objevování téhož políčka. Deska má pouze 64 políček. Upravuješ pokračování loop
/// (smyčky), nikoli připravenou práci se souřadnicemi a sousedy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
void visitNeighbours(ArrayList<Point> queue, Point point, int old, int colour) {
    for (var delta : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int x = point.x + delta[0];
        int y = point.y + delta[1];
        if (x >= 0 && y >= 0 && x < 8 && y < 8 && board[y][x] == old) {
            board[y][x] = colour;
            queue.add(new Point(x, y));
        }
    }
}

final int WIDTH = 520, HEIGHT = 420;

int[][] board;
int moves;
Color[] colours = {new Color(239, 97, 97), new Color(65, 164, 222), new Color(250, 194, 63), new Color(109, 195, 121)};
void restart() {
    board = new int[8][8];
    moves = 0;
    var random = new Random(5421);
    for (var row : board) {
        for (int x = 0; x < 8; x++) {
            row[x] = random.nextInt(4);
        }
    }
}

void fill(int colour) {
    if (colour < 0 || colour >= 4 || colour == board[0][0] || won()) {
        return;
    }
    int old = board[0][0];
    var queue = new ArrayList<Point>();
    queue.add(new Point(0, 0));
    board[0][0] = colour;
    spread(queue, old, colour);
    moves++;
}

boolean won() {
    for (var row : board) {
        for (int c : row) {
            if (c != board[0][0]) {
                return false;
            }
        }
    }
    return true;
}

void draw(Graphics2D g) {
    for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++) {
            g.setColor(colours[board[y][x]]);
            g.fillRect(25 + x * 40, 25 + y * 40, 39, 39);
        }
    }
    for (int c = 0; c < 4; c++) {
        g.setColor(colours[c]);
        g.fillRect(380, 50 + c * 65, 100, 45);
        g.setColor(Color.BLACK);
        g.drawString("" + (c + 1), 425, 78 + c * 65);
    }
    g.setColor(Color.BLACK);
    g.drawString((won() ? "Filled! " : "") + "Moves: " + moves, 25, 380);
    g.drawString("Seed 5421 - compare your next attempt", 25, 403);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed && code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
        fill(code - KeyEvent.VK_1);
    }
}

void press(int x, int y, int button) {
    if (x >= 380 && x < 480 && y >= 50 && y < 310 && (y - 50) % 65 < 45) {
        fill((y - 50) / 65);
    }
}

void showWindow() {
    var frame = new JFrame("Flood");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click a colour or press 1-4   R: same board</div></html>", SwingConstants.CENTER);
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
