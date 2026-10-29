import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Úkol · Breakout
/// Lekce: 13.5 Nech kreslení pozorovat model — 05-model-view.html
///
/// Hra s cihlami je už hratelná. Vytvoř tréninkovou edici na obtížné odrazy, kde špatný okamžik
/// nesmaže postupně vykousanou zeď.
///
/// **Tvoje mise:** Proměň běžnou hru se třemi pokusy v neomezený trénink. Každé minutí má znovu podat
/// míček a zachovat zničené cihly i skóre. Restart dál obnoví celou zeď; její vyčištění dál vyhrává a
/// zastaví pohyb. Velký počet pokusů není pravidlo: odstraň závislost prohry na počítadle a uprav
/// pravdivě displej.
///
/// **Zkoumání:** Změň pravidlo fungující hry sledováním všech následků minutí.
///
/// Space nebo kliknutí odstartuje; myší řiď pálku. Před úpravou několikrát záměrně miň a potom pokus
/// zopakuj. R obnoví zeď a počká na nový start.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni mozaiku cihel volbou položek bricks, které začnou jako true. Vytvoř vzor alespoň s jednou
/// záměrnou mezerou a změň cíl výhry na skutečný počet cihel. Dokáže kamarád tvůj návrh vyčistit?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
void step() {
    if (!state.equals("PLAY")) return;
    double previousX = ballX, previousY = ballY;
    ballX += velocityX;
    ballY += velocityY;
    if (ballX < RADIUS || ballX > WIDTH - RADIUS) {
        ballX = Math.max(RADIUS, Math.min(WIDTH - RADIUS, ballX));
        velocityX = -velocityX;
    }
    if (ballY < RADIUS) {
        ballY = RADIUS;
        velocityY = Math.abs(velocityY);
    }
    if (velocityY > 0 && previousY + RADIUS <= BAT_Y && ballY + RADIUS >= BAT_Y
            && ballX + RADIUS >= batX && ballX - RADIUS <= batX + BAT_WIDTH) {
        ballY = BAT_Y - RADIUS;
        velocityY = -Math.abs(velocityY);
        velocityX = (ballX - batX - BAT_WIDTH / 2.0) / (BAT_WIDTH / 2.0) * 4;
    }
    hit: for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 6; column++) {
            int x = 32 + column * 96, y = 40 + row * 28;
            if (!bricks[row][column] || ballX + RADIUS < x || ballX - RADIUS > x + 88
                    || ballY + RADIUS < y || ballY - RADIUS > y + 20) continue;
            bricks[row][column] = false;
            score++;
            if (previousY + RADIUS <= y || previousY - RADIUS >= y + 20) {
                velocityY = -velocityY;
                ballY = previousY < y ? y - RADIUS : y + 20 + RADIUS;
            } else {
                velocityX = -velocityX;
                ballX = previousX < x ? x - RADIUS : x + 88 + RADIUS;
            }
            break hit;
        }
    }
    if (score == 18) state = "YOU WIN";
    if (ballY > HEIGHT + RADIUS) {
        lives--;
        if (lives == 0) state = "GAME OVER";
        else serve();
    }
}

final int WIDTH = 640, HEIGHT = 400, RADIUS = 7, BAT_WIDTH = 90, BAT_Y = 370;
final boolean[][] bricks = new boolean[3][6];
double ballX, ballY, velocityX, velocityY;
int batX, lives, score;
String state;

void restart() {
    for (var row : bricks) java.util.Arrays.fill(row, true);
    batX = (WIDTH - BAT_WIDTH) / 2;
    lives = 3;
    score = 0;
    state = "PLAY";
    serve();
}

void serve() {
    ballX = WIDTH / 2.0;
    ballY = 270;
    velocityX = 3.2;
    velocityY = -3.5;
}

void aim(int mouseX) {
    batX = Math.max(0, Math.min(WIDTH - BAT_WIDTH, mouseX - BAT_WIDTH / 2));
}

void draw(Graphics g) {
    g.setColor(new Color(21, 22, 42));
    g.fillRect(0, 0, WIDTH, HEIGHT + 42);
    Color[] colours = {new Color(255, 113, 133), new Color(255, 191, 92), new Color(93, 217, 211)};
    for (int row = 0; row < 3; row++) {
        for (int column = 0; column < 6; column++) {
            if (!bricks[row][column]) continue;
            g.setColor(colours[row]);
            g.fillRoundRect(32 + column * 96, 40 + row * 28, 88, 20, 6, 6);
        }
    }
    g.setColor(Color.WHITE);
    g.fillRoundRect(batX, BAT_Y, BAT_WIDTH, 12, 8, 8);
    g.fillOval((int) ballX - RADIUS, (int) ballY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    g.drawString("Bricks: " + score + "/18    Attempts: " + lives + "    " + state, 16, HEIGHT + 18);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Mouse: move paddle     R: restart", 16, HEIGHT + 36);
    if (!state.equals("PLAY")) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g.drawString(state + " - press R", 160, 230);
    }
}

void showWindow() {
    var frame = new JFrame("Breakout - Brick Bash");
    boolean[] launched = {false};
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
            if (!launched[0]) {
                g.setColor(Color.WHITE);
                g.drawString("Space or click to start", 20, 65);
            }
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 42));
    panel.setFocusable(true);
    panel.addMouseMotionListener(new MouseMotionAdapter() {
        @Override public void mouseMoved(MouseEvent event) { aim(event.getX()); }
        @Override public void mouseDragged(MouseEvent event) { aim(event.getX()); }
    });
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) launched[0] = true;
            if (event.getKeyCode() == KeyEvent.VK_R) { restart(); launched[0] = false; }
        }
    });
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            launched[0] = true;
            panel.requestFocusInWindow();
        }
    });
    var timer = new Timer(16, event -> {
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
