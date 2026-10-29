import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · Pong
/// Lekce: 13.4 Posouvej simulaci po malých krocích — 04-timers.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Souboj dvou lidí už funguje. Proměň ho v tréninkového partnera, kterého lze porazit, a hledej
/// situace, kdy jednoduchý soupeř překvapí.
///
/// **Tvoje mise:** Zařiď, aby B přepínalo pravou pálku mezi člověkem a botem. Bot sleduje současný
/// míček, jde k němu rychlostí 3 pixely za tick a stojí, je-li míček do 8 pixelů od středu pálky.
/// Člověku zůstává rychlost 5 pixelů. Předpověz, zda pomalejší bot vždy prohraje, a hrou svou
/// hypotézu prověř.
///
/// **Zkoumání:** Vyslov hypotézu o jednoduchém soupeři, potom jej vytvoř a zapoj.
///
/// Space nebo kliknutí spustí hru. W/S řídí levou pálku, Up/Down lidskou pravou. B volí soupeře, R
/// obnovuje kolo. Vyhrává první s pěti body.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vymysli pravidlo návratu do hry: po gólu dostane prohrávající delší pálku, při remíze budou obě
/// normální. Nejdřív urči, kde končí skórování a začíná další podání. Potom někoho vyzvi na zápas do
/// pěti bodů.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
void step() {
    if (winner != 0) return;
    leftY = clampPaddle(leftY + (leftDown ? 5 : 0) - (leftUp ? 5 : 0));
    int direction = bot ? botDirection(ballY, rightY) : (rightDown ? 1 : 0) - (rightUp ? 1 : 0);
    rightY = clampPaddle(rightY + direction * (bot ? 3 : 5));
    ballX += velocityX;
    ballY += velocityY;
    if (ballY < RADIUS) {
        ballY = RADIUS;
        velocityY = Math.abs(velocityY);
    }
    if (ballY > HEIGHT - RADIUS) {
        ballY = HEIGHT - RADIUS;
        velocityY = -Math.abs(velocityY);
    }
    if (velocityX < 0 && ballX - RADIUS <= 36 && ballX + RADIUS >= 24
            && ballY + RADIUS >= leftY && ballY - RADIUS <= leftY + PADDLE) {
        bounce(1, leftY);
    }
    if (velocityX > 0 && ballX + RADIUS >= WIDTH - 36 && ballX - RADIUS <= WIDTH - 24
            && ballY + RADIUS >= rightY && ballY - RADIUS <= rightY + PADDLE) {
        bounce(-1, rightY);
    }
    if (ballX < -RADIUS) {
        rightScore++;
        serve(-1);
    } else if (ballX > WIDTH + RADIUS) {
        leftScore++;
        serve(1);
    }
    if (leftScore >= 5) winner = 1;
    if (rightScore >= 5) winner = 2;
}

final int WIDTH = 640, HEIGHT = 400, PADDLE = 80, RADIUS = 7;
double leftY, rightY, ballX, ballY, velocityX, velocityY;
int leftScore, rightScore, winner;
boolean leftUp, leftDown, rightUp, rightDown;

void restart() {
    leftY = rightY = (HEIGHT - PADDLE) / 2.0;
    leftScore = rightScore = winner = 0;
    bot = false;
    clearKeys();
    serve(1);
}

void clearKeys() {
    leftUp = leftDown = rightUp = rightDown = false;
}

void serve(int direction) {
    ballX = WIDTH / 2.0;
    ballY = HEIGHT / 2.0;
    velocityX = direction * 4;
    velocityY = 2.5;
}

double clampPaddle(double y) {
    return Math.max(0, Math.min(HEIGHT - PADDLE, y));
}

void bounce(int direction, double paddleY) {
    ballX = direction > 0 ? 36 + RADIUS : WIDTH - 36 - RADIUS;
    velocityX = direction * 4;
    velocityY = (ballY - paddleY - PADDLE / 2.0) / (PADDLE / 2.0) * 4;
    if (Math.abs(velocityY) < 0.8) velocityY = 0.8;
}

void draw(Graphics g) {
    g.setColor(new Color(16, 23, 40));
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(new Color(54, 65, 84));
    for (int y = 0; y < HEIGHT; y += 24) g.fillRect(WIDTH / 2 - 1, y, 2, 12);
    g.setColor(new Color(71, 230, 202));
    g.fillRect(24, (int) leftY, 12, PADDLE);
    g.setColor(new Color(255, 172, 91));
    g.fillRect(WIDTH - 36, (int) rightY, 12, PADDLE);
    g.setColor(Color.WHITE);
    g.fillOval((int) ballX - RADIUS, (int) ballY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
    g.drawString(leftScore + "    " + rightScore, WIDTH / 2 - 42, 38);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    g.drawString("W/S: left    Up/Down: human right    B: opponent    R: restart", 32, HEIGHT - 24);
    g.drawString(bot ? "Opponent: Bot (3 px/tick)" : "Opponent: Human (5 px/tick)", 32, HEIGHT - 8);
    if (winner != 0) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        g.drawString((winner == 1 ? "LEFT" : "RIGHT") + " WINS!  Press R", 150, HEIGHT / 2);
    }
}

void showWindow() {
    var frame = new JFrame("Pong - Paddle Duel");
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
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        void change(KeyEvent event, boolean pressed) {
            if (pressed && event.getKeyCode() == KeyEvent.VK_SPACE) launched[0] = true;
            switch (event.getKeyCode()) {
                case KeyEvent.VK_W -> leftUp = pressed;
                case KeyEvent.VK_S -> leftDown = pressed;
                case KeyEvent.VK_UP -> rightUp = pressed;
                case KeyEvent.VK_DOWN -> rightDown = pressed;
                case KeyEvent.VK_B -> { if (pressed) toggleBot(); }
                case KeyEvent.VK_R -> { if (pressed) { restart(); launched[0] = false; } }
            }
        }
        @Override public void keyPressed(KeyEvent event) { change(event, true); }
        @Override public void keyReleased(KeyEvent event) { change(event, false); }
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
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) { clearKeys(); }
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

boolean bot;

void toggleBot() {
    bot = !bot;
    rightUp = rightDown = false;
}

int botDirection(double targetY, double paddleTop) {
    double difference = targetY - (paddleTop + PADDLE / 2.0);
    if (Math.abs(difference) <= 8) return 0;
    return difference > 0 ? 1 : -1;
}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
