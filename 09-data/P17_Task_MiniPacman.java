import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;

/// # Task · MiniPacman
/// Lesson: 9.5 Name the allowed choices — 05-enums.html
///
/// Collect every dot while a ghost chases you through the maze. Reaching the last dot and meeting the
/// ghost can happen in the same step.
///
/// **Your mission:** Build afterContact(): capture returns CAUGHT even on the last dot; otherwise zero
/// remaining dots returns WON, and every other situation returns PLAY. The unfinished transition
/// currently keeps every round alive.
///
/// Run the program and press an arrow key to start moving through the maze, steering toward
/// dots while avoiding the ghost.

enum RoundState {
    PLAY, CAUGHT, WON
}

RoundState afterContact(boolean caught, int dotsLeft) {
    return RoundState.PLAY;
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Make a clearly labelled alternate ending where the last dot wins a tie. Compare the two endings on
/// the same last-dot capture and explain which feels fairer.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
record Cell(int x, int y) {
}

final String[] map = {
"#############", "#...........#", "#.###...###.#",
"#...#...#...#", "#...#...#...#", "#...........#",
"#.###...###.#", "#...........#", "#############"
};
final int TILE = 30;
final boolean[][] pellets = new boolean[map.length][map[0].length()];
final Random random = new Random();
int playerX, playerY, ghostX, ghostY, dx, dy, wantedX, wantedY, remaining, ticks;
boolean started;

RoundState state;

boolean open(int x, int y) {
    return y >= 0 && y < map.length && x >= 0 && x < map[y].length() && map[y].charAt(x) != '#';
}

void restart() {
    playerX = playerY = 1;
    ghostX = 11;
    ghostY = 7;
    dx = dy = wantedX = wantedY = remaining = ticks = 0;
    started = false;
    state = RoundState.PLAY;
    for (int y = 0; y < map.length; y++) {
        for (int x = 0; x < map[y].length(); x++) {
            pellets[y][x] = open(x, y) && (x != playerX || y != playerY);
            if (pellets[y][x]) {
                remaining++;
            }
        }
    }
}

void direction(int x, int y) {
    if (Math.abs(x) + Math.abs(y) != 1) {
        return;
    }
    wantedX = x;
    wantedY = y;
    started = true;
}

void moveGhost() {
    var choices = new ArrayList<Cell>();
    int best = Integer.MAX_VALUE;
    for (var delta : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
        int x = ghostX + delta[0], y = ghostY + delta[1];
        if (!open(x, y)) {
            continue;
        }
        int distance = Math.abs(x - playerX) + Math.abs(y - playerY);
        if (distance < best) {
            choices.clear();
            best = distance;
        }
        if (distance == best) {
            choices.add(new Cell(x, y));
        }
    }
    if (!choices.isEmpty()) {
        var next = choices.get(random.nextInt(choices.size()));
        ghostX = next.x();
        ghostY = next.y();
    }
}

void step() {
    if (!started || state != RoundState.PLAY) {
        return;
    }
    if (open(playerX + wantedX, playerY + wantedY)) {
        dx = wantedX;
        dy = wantedY;
    }
    if (open(playerX + dx, playerY + dy)) {
        playerX += dx;
        playerY += dy;
    }
    if (pellets[playerY][playerX]) {
        pellets[playerY][playerX] = false;
        remaining--;
    }
    boolean caught = playerX == ghostX && playerY == ghostY;
    if (++ticks % 3 == 0 && !caught) {
        moveGhost();
    }
    caught|=playerX == ghostX && playerY == ghostY;
    state = afterContact(caught, remaining);
}

void draw(Graphics g) {
    int width = map[0].length() * TILE, height = map.length * TILE;
    g.setColor(new Color(16, 18, 38));
    g.fillRect(0, 0, width, height + 54);
    for (int y = 0; y < map.length; y++) {
        for (int x = 0; x < map[y].length(); x++) {
            if (!open(x, y)) {
                g.setColor(new Color(52, 78, 153));
                g.fillRoundRect(x * TILE + 2, y * TILE + 2, TILE - 4, TILE - 4, 8, 8);
            } else {
                if (pellets[y][x]) {
                    g.setColor(new Color(255, 219, 167));
                    g.fillOval(x * TILE + 12, y * TILE + 12, 6, 6);
                }
            }
        }
    }
    g.setColor(new Color(255, 222, 71));
    g.fillOval(playerX * TILE + 4, playerY * TILE + 4, TILE - 8, TILE - 8);
    g.setColor(new Color(255, 117, 173));
    g.fillRoundRect(ghostX * TILE + 4, ghostY * TILE + 4, TILE - 8, TILE - 8, 12, 12);
    g.setColor(Color.WHITE);
    g.fillOval(ghostX * TILE + 8, ghostY * TILE + 10, 5, 7);
    g.fillOval(ghostX * TILE + 17, ghostY * TILE + 10, 5, 7);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Dots left: " + remaining + "   " + (started ? state : "ARROWS TO START"), 10, height + 23);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Arrow keys: steer     R: restart", 10, height + 44);
}

void showWindow() {
    var frame = new JFrame("Mini Pacman - Maze Muncher");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(map[0].length() * TILE, map.length * TILE + 54));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> direction(-1, 0);
                case KeyEvent.VK_RIGHT -> direction(1, 0);
                case KeyEvent.VK_UP -> direction(0, -1);
                case KeyEvent.VK_DOWN -> direction(0, 1);
                case KeyEvent.VK_R -> restart();
            }
        }
    });
    var timer = new Timer(130, event -> {
        step();
        panel.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {
        @Override public void windowClosed(WindowEvent event) {
            timer.stop();
        }
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
