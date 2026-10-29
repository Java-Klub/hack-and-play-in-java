import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Task · AntHighway
/// Lesson: 8.3 Ask the neighbouring cells — 03-neighbours.html
///
/// Let a tiny ant turn, repaint tiles, and slowly build a surprising trail. Watch for a highway emerging
/// from its simple repeated rule.
///
/// **Your mission:** The west direction sends the ant east, distorting its trail. Repair the direction
/// offsets in step() while preserving turning, repainting, and edge wrapping.
///
/// Run the program and click One step to inspect one ant move before using Run/Pause to grow a
/// longer trail.

void step() {
    direction = Math.floorMod(direction + (black[y][x] ? -1 : 1), 4);
    black[y][x] = !black[y][x];
    int[] dx = {0, 1, 0, 1};
    int[] dy = {-1, 0, 1, 0};
    x = Math.floorMod(x + dx[direction], 81);
    y = Math.floorMod(y + dy[direction], 61);
    steps++;
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Reverse the left/right turning rule and compare the first hundred steps from the same blank board.
/// Draw a small initial patch that changes the ant’s journey.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] black = new boolean[61][81];
int x = 40;
int y = 30;
int direction;
long steps;
boolean running;

void reset() {
    black = new boolean[61][81];
    x = 40;
    y = 30;
    direction = 0;
    steps = 0;
}

void showWindow() {
    var frame = new JFrame("Ant Highway");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel() {

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 648, 488);
            g.setColor(Color.BLACK);
            for (int row = 0; row < 61; row++) {
                for (int col = 0; col < 81; col++) {
                    if (black[row][col]) {
                        g.fillRect(col * 8, row * 8, 8, 8);
                    }
                }
            }
            g.setColor(Color.RED);
            g.fillRect(x * 8, y * 8, 8, 8);
        }
    };
    canvas.setPreferredSize(new Dimension(648, 488));
    var controls = new JPanel();
    var label = new JLabel("Steps: 0");
    var run = new JButton("Run");
    run.addActionListener(event -> {
        running = !running;
        run.setText(running ? "Pause" : "Run");
    });
    var single = new JButton("One step");
    single.addActionListener(event -> {
        running = false;
        run.setText("Run");
        step();
        label.setText("Steps: " + steps);
        canvas.repaint();
    });
    var restart = new JButton("Reset");
    restart.addActionListener(event -> {
        running = false;
        run.setText("Run");
        reset();
        label.setText("Steps: 0");
        canvas.repaint();
    });
    controls.add(run);
    controls.add(single);
    controls.add(restart);
    controls.add(label);
    var timer = new Timer(25, event -> {
        if (running) {
            for (int i = 0; i < 100; i++) {
                step();
            }
        }
        label.setText("Steps: " + steps);
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter() {

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
