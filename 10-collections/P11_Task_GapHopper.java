import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;

/// # Úkol · GapHopper
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Udrž malého letce mezi pohybujícími se trubkami. Překážky mají tvořit nekonečnou trať, ne postupně
/// zmizet za zády.
///
/// **Tvoje mise:** Obnovená trubka se vloží na chybný konec a postupně rozbije trať. Obnov pořadí
/// překážek beze změny pravidel letu.
///
/// Po spuštění programu udržuj letce mezi trubkami pomocí Space nebo klikání a po nárazu začni
/// znovu klávesou R.

void recyclePipes() {
    if (!pipes.isEmpty() && pipes.getFirst().x + PIPE_WIDTH < 0) {
        double furthest = pipes.getLast().x;
        pipes.removeFirst();
        pipes.addFirst(new Pipe(furthest + 260, newGap()));
    }
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
static class Pipe {
    double x;
    int gapY;
    boolean scored;
    Pipe(double x, int gapY) {
        this.x = x;
        this.gapY = gapY;
    }
}

final int WIDTH = 500, HEIGHT = 400, BIRD_X = 110, RADIUS = 10, PIPE_WIDTH = 55, GAP = 130;
final Random random = new Random();
final java.util.List<Pipe> pipes = new ArrayList<>(java.util.List.of(new Pipe(340, 200), new Pipe(600, 200)));
double birdY, velocityY;
int score;
boolean running, lost, spaceDown;

int newGap() {
    return 100 + random.nextInt(201);
}

void restart() {
    birdY = 180;
    velocityY = 0;
    score = 0;
    running = lost = spaceDown = false;
    for (int index = 0; index < pipes.size(); index++) {
        pipes.get(index).x = 340 + index * 260;
        pipes.get(index).gapY = newGap();
        pipes.get(index).scored = false;
    }
}

void flap() {
    if (lost) return;
    running = true;
    velocityY = -5.5;
}

void step() {
    if (!running || lost) return;
    velocityY += 0.28;
    birdY += velocityY;
    if (birdY - RADIUS < 0 || birdY + RADIUS > HEIGHT) lost = true;
    for (Pipe pipe : pipes) pipe.x -= 2.5;
    for (Pipe pipe : pipes) {
        boolean overlap = BIRD_X + RADIUS > pipe.x && BIRD_X - RADIUS < pipe.x + PIPE_WIDTH;
        if (overlap && (birdY - RADIUS < pipe.gapY - GAP / 2
                || birdY + RADIUS > pipe.gapY + GAP / 2)) lost = true;
        if (!pipe.scored && pipe.x + PIPE_WIDTH < BIRD_X - RADIUS) {
            pipe.scored = true;
            score++;
        }

    }
    recyclePipes();
}

void draw(Graphics g) {
    g.setColor(new Color(31, 51, 82));
    g.fillRect(0, 0, WIDTH, HEIGHT + 44);
    for (Pipe pipe : pipes) {
        g.setColor(new Color(74, 197, 158));
        g.fillRect((int) pipe.x, 0, PIPE_WIDTH, pipe.gapY - GAP / 2);
        g.fillRect((int) pipe.x, pipe.gapY + GAP / 2, PIPE_WIDTH, HEIGHT - pipe.gapY - GAP / 2);
        g.setColor(new Color(134, 239, 176));
        g.fillRect((int) pipe.x, pipe.gapY - GAP / 2 - 10, PIPE_WIDTH, 10);
        g.fillRect((int) pipe.x, pipe.gapY + GAP / 2, PIPE_WIDTH, 10);
    }
    g.setColor(new Color(255, 218, 87));
    g.fillOval(BIRD_X - RADIUS, (int) birdY - RADIUS, RADIUS * 2, RADIUS * 2);
    g.setColor(Color.BLACK);
    g.fillOval(BIRD_X + 3, (int) birdY - 5, 4, 4);
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
    String status = lost ? "CRASH! Press R" : running ? "Score: " + score : "SPACE OR CLICK TO START";
    g.drawString(status, 16, HEIGHT + 22);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Space / click: flap     R: restart", 16, HEIGHT + 40);
}

void showWindow() {
    var frame = new JFrame("Gap Hopper");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 44));
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) restart();
            if (event.getKeyCode() == KeyEvent.VK_SPACE && !spaceDown) {
                spaceDown = true;
                flap();
            }
        }
        @Override public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SPACE) spaceDown = false;
        }
    });
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) { flap(); }
    });
    var timer = new Timer(20, event -> {
        step();
        panel.repaint();
    });
    frame.addWindowFocusListener(new WindowAdapter() {
        @Override public void windowLostFocus(WindowEvent event) { spaceDown = false; }
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
