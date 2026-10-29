import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.Timer;

/// # Task · TargetPop
/// Lesson: 9.5 Name the allowed choices — 05-enums.html
///
/// Pop moving targets against a thirty-second clock. Try a second round to beat your own accuracy and
/// score.
///
/// **Your mission:** At the deadline the game returns to its ready state instead of finishing. Repair
/// afterTime() so the final score survives until an intentional restart.
///
/// Run the program and click a circle to begin the timed round, then keep clicking targets
/// until the clock expires.

enum Phase {
    READY, RUNNING, FINISHED
}

Phase afterTime(long now) {
    if (phase == Phase.RUNNING && now >= deadline) {
        return Phase.READY;
    }
    return phase;
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// In restart(), shorten the round and compare a ten-second sprint with the original thirty seconds.
/// Decide whether your personal challenge rewards score or accuracy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
final int WIDTH = 600, HEIGHT = 400, RADIUS = 26;
final long ROUND_NANOS = 30_000_000_000L, TARGET_NANOS = 1_200_000_000L;
final Random random = new Random();
int targetX, targetY, hits, misses, expired;
long deadline, targetDeadline;

Phase phase;

void spawn(long now) {
    targetX = RADIUS + random.nextInt(WIDTH - RADIUS * 2 + 1);
    targetY = RADIUS + random.nextInt(HEIGHT - RADIUS * 2 + 1);
    targetDeadline = now + TARGET_NANOS;
}

void restart() {
    hits = misses = expired = 0;
    deadline = 0;
    phase = Phase.READY;
    spawn(0);
}

void tick(long now) {
    phase = afterTime(now);
    if (phase != Phase.RUNNING) {
        return;
    }
    if (now >= targetDeadline) {
        expired++;
        spawn(now);
    }
}

void click(int x, int y, long now) {
    tick(now);
    if (phase == Phase.FINISHED) {
        return;
    }
    if (phase == Phase.READY) {
        phase = Phase.RUNNING;
        deadline = now + ROUND_NANOS;
        targetDeadline = now + TARGET_NANOS;
    }
    int dx = x - targetX, dy = y - targetY;
    if (dx * dx + dy * dy <= RADIUS * RADIUS) {
        hits++;
        spawn(now);
    } else {
        misses++;
    }
}

void draw(Graphics g) {
    g.setColor(new Color(25, 26, 47));
    g.fillRect(0, 0, WIDTH, HEIGHT + 58);
    if (!(phase == Phase.FINISHED)) {
        g.setColor(new Color(255, 128, 155));
        g.fillOval(targetX - RADIUS, targetY - RADIUS, RADIUS * 2, RADIUS * 2);
        g.setColor(new Color(255, 223, 149));
        g.fillOval(targetX - 10, targetY - 10, 20, 20);
        g.setColor(Color.WHITE);
        g.drawLine(targetX - 5, targetY, targetX + 5, targetY);
        g.drawLine(targetX, targetY - 5, targetX, targetY + 5);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    long seconds = (phase != Phase.READY) ? Math.max(0, (deadline - System.nanoTime() + 999_999_999) / 1_000_000_000) : 30;
    String state = (phase == Phase.FINISHED) ? "TIME!" : (phase != Phase.READY) ? seconds + " seconds" : "CLICK TO START";
    g.drawString(state + "    Hits: " + hits + "    Misses: " + misses, 14, HEIGHT + 24);
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
    g.drawString("Click the circles before they disappear.    Expired: " + expired + "    R: restart", 14, HEIGHT + 47);
    if ((phase == Phase.FINISHED)) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        g.drawString("You popped " + hits + "!  Press R", 110, HEIGHT / 2);
    }
}

void showWindow() {
    var frame = new JFrame("Target Pop");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT + 58));
    panel.setFocusable(true);
    panel.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent event) {
            panel.requestFocusInWindow();
            if (event.getY() < HEIGHT) {
                click(event.getX(), event.getY(), System.nanoTime());
            }
            panel.repaint();
        }
    });
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_R) {
                restart();
            }
        }
    });
    var timer = new Timer(25, event -> {
        tick(System.nanoTime());
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
