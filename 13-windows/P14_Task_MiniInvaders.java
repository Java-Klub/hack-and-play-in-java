import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · MiniInvaders
/// Lekce: 13.3 Dej událostem jeden jasný úkol — 03-handlers.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Loď už bojuje poctivě: jedna střela zničí jednoho mimozemšťana. Uprav zbraň na řízený průrazný výboj
/// pro dva cíle; nesmí se změnit v nekonečný vrtací laser.
///
/// **Tvoje mise:** Každý nový výstřel musí přežít první odlišný zásah a skončit po druhém. Každý
/// mimozemšťan boduje jednou; spotřebovaná střela neboduje a nový výstřel má čerstvou zásobu dvou
/// zásahů. Zachovej pohyb, míření, konec a restart. Nejdřív sleduj běžnou jednorázovou životnost a
/// potom najdi vznik i konec střely.
///
/// Řiď Left/Right a střílej Space; R začíná znovu. Miř do svislého sloupce. Před úpravou výstřel skončí
/// na prvním mimozemšťanovi; potom sleduj, jak stejná střela odstraní druhého a zmizí.

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
void step() {
    if (!state.equals("PLAY")) {
        return;
    }
    shipX = Math.max(16, Math.min(WIDTH - 16, shipX + (right ? 5 : 0) - (left ? 5 : 0)));
    if (fire && !bulletActive) {
        bulletActive = true;
        hitsLeft = 2;
        bulletX = shipX;
        bulletY = SHIP_Y - 14;
    }
    if (bulletActive) {
        bulletY -= 7;
        if (bulletY < -10) {
            bulletActive = false;
        }
    }
    int minColumn = 5, maxColumn = -1;
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (!aliens[row][column]) {
                continue;
            }
            minColumn = Math.min(minColumn, column);
            maxColumn = Math.max(maxColumn, column);
        }
    }
    if (maxColumn == -1) {
        state = "YOU WIN";
        return;
    }
    formationX += speedX;
    double minimum = -minColumn * 60, maximum = WIDTH - maxColumn * 60 - 28;
    if (formationX < minimum || formationX > maximum) {
        formationX = Math.max(minimum, Math.min(maximum, formationX));
        speedX = -speedX;
        formationY += 18;
    }
    hit : for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            double x = formationX + column * 60, y = formationY + row * 34;
            if (aliens[row][column] && bulletActive && bulletX >= x && bulletX <= x + 28
            && bulletY <= y + 18 && bulletY + 10 >= y) {
                hitAlien(row, column);
                break hit;
            }
        }
    }
    if (countAliens() == 0) {
        state = "YOU WIN";
    }
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (aliens[row][column] && formationY + row * 34 + 18 >= SHIP_Y - 12) {
                state = "INVASION";
            }
        }
    }
}

void hitAlien(int row, int column) {
    if (!bulletActive || hitsLeft <= 0 || !aliens[row][column]) return;
    aliens[row][column] = false;
    score++;
    hitsLeft--;
    if (hitsLeft == 0) bulletActive = false;
}

final int WIDTH = 600, HEIGHT = 420, SHIP_Y = 388;
final boolean[][] aliens = new boolean[3][5];
double shipX, formationX, formationY, speedX, bulletX, bulletY;
boolean left, right, fire, bulletActive;
int score;
int hitsLeft;
String state;

void clearKeys() {
    left = right = fire = false;
}

void restart() {
    for (var row : aliens) {
        java.util.Arrays.fill(row, true);
    }
    shipX = WIDTH / 2.0;
    formationX = 60;
    formationY = 45;
    speedX = 1.2;
    bulletActive = false;
    score = 0;
    hitsLeft = 0;
    state = "PLAY";
    clearKeys();
}

int countAliens() {
    int count = 0;
    for (var row : aliens) {
        for (boolean alive : row) {
            if (alive) {
                count++;
            }
        }
    }
    return count;
}

void draw(Graphics g) {
    g.setColor(new Color(12, 20, 34));
    g.fillRect(0, 0, WIDTH, HEIGHT + 42);
    g.setColor(new Color(39, 51, 72));
    for (int x = 15; x < WIDTH; x += 47) {
        for (int y = 15; y < HEIGHT; y += 61) {
            g.fillRect(x, y, 2, 2);
        }
    }
    g.setColor(new Color(130, 237, 151));
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 5; column++) {
            if (!aliens[row][column]) {
                continue;
            }
            int x = (int) formationX + column * 60, y = (int) formationY + row * 34;
            g.fillRect(x, y, 28, 18);
            g.clearRect(x + 6, y + 5, 4, 4);
            g.clearRect(x + 18, y + 5, 4, 4);
        }
    }
    g.setColor(new Color(82, 201, 250));
    g.fillPolygon(new int[]{(int) shipX - 15, (int) shipX, (int) shipX + 15},
    new int[]{SHIP_Y + 12, SHIP_Y - 12, SHIP_Y + 12}, 3);
    if (bulletActive) {
        g.setColor(new Color(255, 222, 93));
        g.fillRect((int) bulletX - 2, (int) bulletY, 4, 10);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Hits: " + score + "/15    " + state, 14, HEIGHT + 18);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Left / Right: move     Space: fire     R: restart", 14, HEIGHT + 36);
}

void showWindow() {
    var frame = new JFrame("Mini Invaders - Last Row");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 42));
    var status = new JLabel("Left / Right / Space: start   Clear all aliens before they descend   R: restart");
    frame.add(status, BorderLayout.NORTH);
    panel.setFocusable(true);
    boolean[] started = {false};
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            if (pressed && (event.getKeyCode() == KeyEvent.VK_LEFT
                    || event.getKeyCode() == KeyEvent.VK_RIGHT || event.getKeyCode() == KeyEvent.VK_SPACE)) {
                started[0] = true;
            }
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> left = pressed;
                case KeyEvent.VK_RIGHT -> right = pressed;
                case KeyEvent.VK_SPACE -> fire = pressed;
                case KeyEvent.VK_R -> {
                    if (pressed) {
                        restart();
                        started[0] = false;
                    }
                }
            }
        }
        @Override public void keyPressed(KeyEvent event) {
            change(event, true);
        }
        @Override public void keyReleased(KeyEvent event) {
            change(event, false);
        }
    });
    var timer = new Timer(20, event -> {
        if (started[0]) {
            step();
        }
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) {
            clearKeys();
        }
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
