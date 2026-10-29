import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/// # Vyřešený úkol · Net
/// Lekce: 8.3 Ptej se okolních buněk — 03-neighbours.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Otáčej dílky potrubí, dokud nedostane energii celá síť ze zdroje. Zelené políčko má mít skutečné
/// oboustranné spojení.
///
/// **Tvoje mise:** Vytvoř linked(x,y,direction): spojení existuje, jen když neighbour (soused) leží
/// uvnitř bounds (hranic), aktuální tile (dílek) má výstup daným směrem a neighbour má opačný vstup.
/// Dotyk prázdné hrany není vodič. Připravené hledání tímto pravidlem rozsvěcí síť.
///
/// Spusť program a kliknutím otáčej potrubí dílku, přitom sleduj, které dílky při spojení se
/// zdrojem zezelenají.

boolean linked(int x, int y, int direction) {
    int nx = x + dx[direction], ny = y + dy[direction];
    if (nx < 0 || ny < 0 || nx >= 4 || ny >= 4) return false;
    int outgoing = 1 << direction;
    int incoming = 1 << ((direction + 2) % 4);
    return (tiles[y][x] & outgoing) != 0 && (tiles[ny][nx] & incoming) != 0;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[][] connected() {
    boolean[][] reached = new boolean[4][4];
    var queue = new ArrayList<Point>();
    queue.add(new Point(0, 0));
    reached[0][0] = true;
    for (int i = 0; i < queue.size(); i++) {
        var p = queue.get(i);
        for (int d = 0; d < 4; d++) {
            int x = p.x + dx[d], y = p.y + dy[d];
            if (linked(p.x, p.y, d) && !reached[y][x]) {
                reached[y][x] = true;
                queue.add(new Point(x, y));
            }
        }
    }
    return reached;
}

final int WIDTH = 460, HEIGHT = 420;

int[][] tiles = new int[4][4];
int[] dx = {0, 1, 0, -1}, dy = {-1, 0, 1, 0};
void solvedBoard() {
    tiles = new int[4][4];
    for (int i = 0; i < 15; i++) {
        int y = i / 4, x = y % 2 == 0 ? i % 4 : 3 - i % 4;
        int yy = (i + 1) / 4, xx = yy % 2 == 0 ? (i + 1) % 4 : 3 - (i + 1) % 4;
        int d = yy > y ? 2 : xx > x ? 1 : 3;
        tiles[y][x]|=1<<d;
        tiles[yy][xx]|=1<<((d + 2) % 4);
    }
}

void restart() {
    solvedBoard();
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            for (int i = 0; i < (x + 2 * y + 1) % 4; i++) {
                rotate(x, y);
            }
        }
    }
}

void rotate(int x, int y) {
    if (x >= 0 && y >= 0 && x < 4 && y < 4) {
        tiles[y][x] = ((tiles[y][x]<<1)&15)|(tiles[y][x]>>3);
    }
}

boolean won() {
    var reached = connected();
    int ports = 0;
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            if (!reached[y][x]) {
                return false;
            }
            for (int d = 0; d < 4; d++) {
                if ((tiles[y][x]&(1<<d)) != 0) {
                    ports++;
                    int xx = x + dx[d], yy = y + dy[d];
                    if (xx < 0 || yy < 0 || xx >= 4 || yy >= 4 || (tiles[yy][xx]&(1<<((d + 2) % 4))) == 0) {
                        return false;
                    }
                }
            }
        }
    }
    return ports == 30;
}

void draw(Graphics2D g) {
    var reached = connected();
    g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    for (int y = 0; y < 4; y++) {
        for (int x = 0; x < 4; x++) {
            int xx = 100 + x * 75, yy = 65 + y * 75;
            g.setColor(new Color(235, 238, 243));
            g.fillRect(xx - 35, yy - 35, 70, 70);
            g.setColor(reached[y][x] ? new Color(35, 165, 115) : Color.GRAY);
            for (int d = 0; d < 4; d++) {
                if ((tiles[y][x]&(1<<d)) != 0) {
                    g.drawLine(xx, yy, xx + dx[d] * 34, yy + dy[d] * 34);
                }
            }
            g.fillOval(xx - 7, yy - 7, 14, 14);
        }
    }
    g.setColor(Color.BLACK);
    g.drawString("SOURCE", 70, 18);
    g.drawString(won() ? "Connected tree complete!" : "Connect every tile; leave no open ports or loops", 35, 390);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
}

void press(int x, int y, int button) {
    if (x >= 65 && x < 365 && y >= 30 && y < 330) {
        rotate((x - 65) / 75, (y - 30) / 75);
    }
}

void showWindow() {
    var frame = new JFrame("Net");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Click tiles: rotate clockwise   Green: joined to source   R:<br>restart</div></html>", SwingConstants.CENTER);
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
