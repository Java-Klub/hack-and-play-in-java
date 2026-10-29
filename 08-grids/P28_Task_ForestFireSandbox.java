import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · ForestFireSandbox
/// Lekce: 8.5 Minulost čti, budoucnost zapisuj — 05-generations.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Sandbox už šíří modelový oheň ke čtyřem sousedním stromům. Změň svět na záměrně jednosměrný východní
/// vítr, navrhni protipožární pás a ověř předpověď.
///
/// **Tvoje mise:** V novém modelu strom vzplane jen tehdy, když cell (buňka) bezprostředně západně od
/// něj hořela v minulé generation (generaci). Starý oheň zhasne, prázdná cell zůstane prázdná.
/// Nepropojuj protější okraje, neměň starý les a nešiř dvakrát během tick (kroku). Šíření na sever,
/// jih, západ i diagonal (úhlopříčně) se zastaví. Zachovej kreslení a Step/Run.
///
/// Pomocí Erase udělej místo, Tree nakresli krátkou vodorovnou řadu a Fire zapal její střed. Step
/// pozastaví a provede jeden krok. Výchozí svět šíří oheň oběma směry; po zásahu hoří jen
/// východní/pravá polovina po jedné cell (buňce).

// <editor-fold defaultstate="expanded" desc="Program · prozkoumej pravidla">
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
    for (int y = 0; y < 40; y++) {
        for (int x = 0; x < 60; x++) {
            if (before[y][x] == 1) {
                next[y][x] = x > 0 && before[y][x - 1] == 2 ? 2 : 1;
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
