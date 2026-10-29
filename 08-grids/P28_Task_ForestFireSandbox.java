import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Task · ForestFireSandbox
/// Lesson: 8.5 Read the past; write the future — 05-generations.html
///
/// The sandbox already spreads a toy fire to four neighbouring trees. Change the world to a
/// deliberately one-way east wind, then design a firebreak and test a prediction.
///
/// **Your mission:** In the new toy model, a tree ignites only if the cell immediately west of it was
/// burning in the previous generation. Old fire becomes empty; empty cells stay empty. Never wrap at
/// borders, mutate the old forest, or spread twice within one tick. North, south, west and diagonal
/// spread must stop. Keep the painting and Step/Run controls.
///
/// Use Erase to make space, Tree to draw a short horizontal line, and Fire to ignite its middle. Step
/// pauses and advances once. In the starter it spreads both ways; after the hack only the east/right
/// half burns one cell per step.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
int[][] cells = new int[40][60];
boolean running;
int brush = 1;

void reset(long seed) {
    var random = new Random(seed);
    cells = new int[40][60];
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            cells[y][x] = random.nextDouble() < 0.64 ? 1 : 0;
        }
    }
}

boolean edit(int x, int y, int value) {
    if (x < 0 || x >= 60 || y < 0 || y >= 40 || value < 0 || value > 2 || value == 2 && cells[y][x] != 1) {
        return false;
    }
    cells[y][x] = value;
    return true;
}

int[][] nextForest(int[][] before) {
    int[][] next = new int[40][60];
    int[][] offsets = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            if (before[y][x] != 1) {
                continue;
            }
            next[y][x] = 1;
            for (var d : offsets) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < 60 && ny >= 0 && ny < 40 && before[ny][nx] == 2) {
                    next[y][x] = 2;
                }
            }
        }
    }
    return next;
}

void step() {
    cells = nextForest(cells);
}

void showWindow() {
    var frame = new JFrame("Forest Fire Sandbox");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel() {

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            Color[] colours = {new Color(30, 25, 15), new Color(35, 145, 55), Color.ORANGE};
            for (int y = 0; y < 40; y++) {
                for (int x = 0; x < 60; x++) {
                    g.setColor(colours[cells[y][x]]);
                    g.fillRect(x * 10, y * 10, 10, 10);
                }
            }
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var brushes = new JComboBox<>(new String[]{"Tree", "Fire", "Erase"});
    brushes.addActionListener(event -> brush = new int[]{1, 2, 0}[brushes.getSelectedIndex()]);
    brushes.setSelectedIndex(1);
    canvas.addMouseListener(new MouseAdapter() {

        public void mousePressed(MouseEvent event) {
            edit(event.getX() / 10, event.getY() / 10, brush);
            canvas.repaint();
        }
    });
    var run = new JButton("Run");
    var single = new JButton("Step");
    single.addActionListener(event -> {
        running = false;
        run.setText("Run");
        step();
        canvas.repaint();
    });
    run.addActionListener(event -> {
        running = !running;
        run.setText(running ? "Pause" : "Run");
    });
    var restart = new JButton("New forest");
    restart.addActionListener(event -> {
        running = false;
        run.setText("Run");
        reset(System.nanoTime());
        canvas.repaint();
    });
    controls.add(brushes);
    controls.add(single);
    controls.add(run);
    controls.add(restart);
    var timer = new Timer(100, event -> {
        if (running) {
            step();
            canvas.repaint();
        }
    });
    frame.addWindowListener(new WindowAdapter() {

        public void windowClosed(WindowEvent event) {
            timer.stop();
        }
    });
    timer.start();
    frame.add(new JLabel("Choose Fire and click a tree to ignite it; Step pauses and advances once."), BorderLayout.NORTH);
    frame.add(canvas, BorderLayout.CENTER);
    frame.add(controls, BorderLayout.SOUTH);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
}

void main() {
    reset(42);
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent a north-east wind with two explicit incoming neighbours. Reuse the same painted test scene to
/// distinguish it from both earlier models.
// </editor-fold>
