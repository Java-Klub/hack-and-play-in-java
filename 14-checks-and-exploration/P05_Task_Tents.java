import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

/// # Vyřešený úkol · Tents
/// Lekce: 14.1 Vyber příklady, které odhalí chyby — 01-checks.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Postav stany vedle stromů, dodrž počty na okrajích a nech mezi stany místo. Nejdřív ověř, že mapa
/// kempu hlásí stromy na správných místech.
///
/// **Tvoje mise:** Jediný příklad stromu neodhalí prohozené souřadnice ani zapomenuté stromy. Rozšiř
/// learnerChecks() o obsazené i prázdné pozice na připravené mapě.
///
/// Klikni na prázdné políčko vedle stromu pro postavení stanu, potom pomocí
/// kontroly hry prověř své příklady čtení mapy.

record TreeExample(int x, int y, boolean expected) {
}

TreeExample[] learnerChecks() {
    return new TreeExample[]{new TreeExample(0, 0, true), new TreeExample(3, 0, true),
    new TreeExample(0, 3, false), new TreeExample(1, 3, true), new TreeExample(4, 4, true), new TreeExample(-1, 0, false)};
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Očekávání vychází z mapy
///
/// `TreeExample(x,y,expected)` popisuje jeden dotaz na přítomnost, nikoli posloupnost stavění stanů.
/// Připravené `trees` array (pole) ukládá JDK Point s vodorovným `x` a svislým `y` field (položkou).
/// Z těchto dat či viditelných stromů předpověz true nebo false. Vyber nesouměrnou pozici, kde
/// prohození x a y změní odpověď, a potom prázdné místo. Vysvětli, co každý příklad vyloučí; kopie
/// původního příkladu nepřidají důkaz.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
final int WIDTH = 500, HEIGHT = 420;

Point[] trees = {new Point(0, 0), new Point(3, 0), new Point(1, 3), new Point(4, 4)};
int[] rowCounts = {1, 1, 0, 1, 1}, columnCounts = {0, 2, 0, 1, 1};
boolean[][] tents;
void restart() {
    tents = new boolean[5][5];
}

boolean treeAt(int x, int y) {
    for (var p : trees) {
        if (p.x == x && p.y == y) {
            return true;
        }
    }
    return false;
}

void toggle(int x, int y) {
    if (x >= 0 && y >= 0 && x < 5 && y < 5 && !treeAt(x, y)) {
        tents[y][x] = !tents[y][x];
    }
}

boolean separated() {
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            if (tents[y][x]) {
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx >= 0 && yy >= 0 && xx < 5 && yy < 5 && (xx != x || yy != y) && tents[yy][xx]) {
                            return false;
                        }
                    }
                }
            }
        }
    }
    return true;
}

boolean match(ArrayList<Point> all, int index, boolean[] used) {
    if (index == all.size()) {
        return true;
    }
    var p = all.get(index);
    for (int i = 0; i < trees.length; i++) {
        if (!used[i] && Math.abs(p.x - trees[i].x) + Math.abs(p.y - trees[i].y) == 1) {
            used[i] = true;
            if (match(all, index + 1, used)) {
                return true;
            }
            used[i] = false;
        }
    }
    return false;
}

boolean won() {
    var all = new ArrayList<Point>();
    int[] columns = new int[5];
    for (int y = 0; y < 5; y++) {
        int count = 0;
        for (int x = 0; x < 5; x++) {
            if (tents[y][x]) {
                if (treeAt(x, y)) {
                    return false;
                }
                count++;
                columns[x]++;
                all.add(new Point(x, y));
            }
        }
        if (count != rowCounts[y]) {
            return false;
        }
    }
    return Arrays.equals(columns, columnCounts) && separated() && all.size() == trees.length && match(all, 0, new boolean[trees.length]);
}

void draw(Graphics2D g) {
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    for (int i = 0; i < 5; i++) {
        g.setColor(Color.BLACK);
        g.drawString("" + columnCounts[i], 105 + i * 55, 40);
        g.drawString("" + rowCounts[i], 50, 80 + i * 55);
    }
    for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
            int xx = 85 + x * 55, yy = 50 + y * 55;
            g.setColor(new Color(232, 240, 222));
            g.fillRect(xx, yy, 53, 53);
            if (treeAt(x, y)) {
                g.setColor(new Color(45, 125, 65));
                g.fillOval(xx + 12, yy + 7, 30, 30);
                g.fillRect(xx + 24, yy + 28, 6, 18);
            }
            if (tents[y][x]) {
                g.setColor(Color.ORANGE);
                g.fillPolygon(new int[]{xx + 7, xx + 27, xx + 47}, new int[]{yy + 44, yy + 9, yy + 44}, 3);
            }
        }
    }
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.setColor(Color.BLACK);
    g.drawString(won() ? "Camp complete!" : "Match counts; tents may not touch, even diagonally", 20, 365);
    g.drawString("Each tent needs its own adjacent tree (not diagonally).", 20, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 85 && y >= 50 && x < 360 && y < 325) {
        toggle((x - 85) / 55, (y - 50) / 55);
    }
}

void showWindow() {
    var frame = new JFrame("Tents");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click: tent / empty   Match edge counts; tents must not touch   R:<br>reset</div></html>", SwingConstants.CENTER);
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
    verifyExamples();
    SwingUtilities.invokeLater(this::showWindow);
}

void verifyExamples() {
    for (var e : learnerChecks()) {
        if (treeAt(e.x(), e.y()) != e.expected()) {
            throw new IllegalStateException("Cell x=" + e.x() + ", y=" + e.y()
                    + ": expected tree=" + e.expected() + ", actual=" + treeAt(e.x(), e.y()));
        }
    }
}
// </editor-fold>
