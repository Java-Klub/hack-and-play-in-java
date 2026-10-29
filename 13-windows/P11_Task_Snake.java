import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · Snake
/// Lekce: 13.2 Proměň kliknutí ve volbu — 02-input-events.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Veď rostoucího hada k jídlu, aniž by se zakousl do sebe. Rychlé stisky kláves mají žádat rozumné
/// zatáčky, ne přepisovat pravidla mezi dvěma posuny.
///
/// **Tvoje mise:** Zatáčka okamžitě změní potvrzený směr místo čekání na další tick (časový krok). Oprav
/// čekající záměr a zachovej jednu platnou zatáčku na posun.
///
/// Po spuštění programu šipka spustí hada; dalšími šipkami jej veď k jídlu. Náraz nebo zaplněná
/// plocha ukončí kolo; R obnoví začátek a čeká na další šipku.

void turn(int x, int y) {
    if (turnQueued || lost || won || Math.abs(x) + Math.abs(y) != 1) return;
    if (x == -dx && y == -dy) return;
    plannedX = x;
    plannedY = y;
    turnQueued = true;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni mírnějšího hada: hlava se přes okraj přenese na protější stranu. Nejdřív rozhodni o nárazu
/// do těla v cíli, potom změň jen pravidlo stěny. Rychlé protichůdné klávesy stále musí dodržet
/// jedinou zatáčku za tick (krok).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Cell(int x, int y) { }
final int COLUMNS = 20, ROWS = 15, TILE = 24;
final Random random = new Random();
final List<Cell> body = new ArrayList<>();
Cell food;
int dx, dy, plannedX, plannedY, score;
boolean turnQueued, lost, won;

void restart() {
    body.clear();
    body.add(new Cell(5, 7));
    body.add(new Cell(4, 7));
    body.add(new Cell(3, 7));
    dx = plannedX = 1;
    dy = plannedY = score = 0;
    turnQueued = lost = won = false;
    placeFood();
}

void placeFood() {
    var free = new ArrayList<Cell>();
    for (int y = 0; y < ROWS; y++) {
        for (int x = 0; x < COLUMNS; x++) {
            var cell = new Cell(x, y);
            if (!body.contains(cell)) free.add(cell);
        }
    }
    won = free.isEmpty();
    food = won ? null : free.get(random.nextInt(free.size()));
}

void step() {
    if (lost || won) return;
    dx = plannedX;
    dy = plannedY;
    turnQueued = false;
    var head = body.getFirst();
    var next = new Cell(head.x() + dx, head.y() + dy);
    boolean growing = next.equals(food);
    int occupied = body.size() - (growing ? 0 : 1);
    if (next.x() < 0 || next.x() >= COLUMNS || next.y() < 0 || next.y() >= ROWS
            || body.subList(0, occupied).contains(next)) {
        lost = true;
        return;
    }
    body.addFirst(next);
    if (growing) {
        score++;
        placeFood();
    } else {
        body.removeLast();
    }
}

void draw(Graphics g) {
    g.setColor(new Color(14, 27, 30));
    g.fillRect(0, 0, COLUMNS * TILE, ROWS * TILE + 50);
    g.setColor(new Color(25, 44, 46));
    for (int x = 0; x <= COLUMNS; x++) g.drawLine(x * TILE, 0, x * TILE, ROWS * TILE);
    for (int y = 0; y <= ROWS; y++) g.drawLine(0, y * TILE, COLUMNS * TILE, y * TILE);
    if (food != null) {
        g.setColor(new Color(255, 116, 114));
        g.fillOval(food.x() * TILE + 4, food.y() * TILE + 4, TILE - 8, TILE - 8);
    }
    for (int index = 0; index < body.size(); index++) {
        Cell cell = body.get(index);
        g.setColor(index == 0 ? Color.WHITE : new Color(89, 222, 154));
        g.fillRoundRect(cell.x() * TILE + 2, cell.y() * TILE + 2, TILE - 4, TILE - 4, 6, 6);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    String status = lost ? "CRASH!" : won ? "FULL BOARD - YOU WIN!" : "SNACK SNAKE";
    g.drawString(status + "   Score: " + score, 12, ROWS * TILE + 21);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Arrow keys: turn     R: restart", 12, ROWS * TILE + 41);
}

void showWindow() {
    var frame = new JFrame("Snake - Snack Snake");
    boolean[] launched = {false};
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
            if (!launched[0]) {
                g.setColor(Color.WHITE);
                g.drawString("Arrow key to start", 20, 65);
            }
        }
    };
    panel.setPreferredSize(new Dimension(COLUMNS * TILE, ROWS * TILE + 50));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() >= KeyEvent.VK_LEFT && event.getKeyCode() <= KeyEvent.VK_DOWN) launched[0] = true;
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> turn(-1, 0);
                case KeyEvent.VK_RIGHT -> turn(1, 0);
                case KeyEvent.VK_UP -> turn(0, -1);
                case KeyEvent.VK_DOWN -> turn(0, 1);
                case KeyEvent.VK_R -> { restart(); launched[0] = false; }
            }
        }
    });
    var timer = new Timer(125, event -> {
        if (launched[0]) step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) { timer.stop(); }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
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
