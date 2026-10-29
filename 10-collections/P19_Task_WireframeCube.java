import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · WireframeCube
/// Lekce: 10.2 Pamatuj si první návštěvu — 02-sets.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Otáčej drátěnou krychlí a sleduj, jak plochá kresba naznačuje hloubku. Počítadlo hran má popsat tvar,
/// i když vstup uvede hranu dvakrát v opačném směru.
///
/// **Tvoje mise:** Sestav uniqueEdges od prázdného Set (množiny). Projdi dvojice, přeskoč konce mimo
/// rozsah i hranu do stejného bodu, menší konec dej první a vlož normalised Edge (sjednocenou hranu).
/// Vrať různé hrany v pořadí prvního výskytu.
///
/// Po spuštění programu pomocí Pause/Run zastav otáčení krychle, přečti Distinct edges a pomocí
/// Reset angle obnov pohled.

Set<Edge> uniqueEdges(int[][] candidates) {
    Set<Edge> result = new LinkedHashSet<>();
    for (int[] pair : candidates) {
        int a = pair[0];
        int b = pair[1];
        if (a < 0 || b < 0 || a >= 8 || b >= 8 || a == b) continue;
        Edge edge = new Edge(Math.min(a, b), Math.max(a, b));
        result.add(edge);
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Přidej krychli tajný podpis úhlopříček pomocí nových candidate edges (kandidátních hran), včetně
/// několika obrácených duplicit. Na papíře předpověz počet různých hran a ověř jej v okně dřív, než
/// změníš otáčení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double angle;
boolean running = true;
int[][] edges = {{0, 1}, {0, 2}, {0, 4}, {1, 3}, {1, 5}, {2, 3}, {2, 6}, {3, 7}, {4, 5}, {4, 6}, {5, 7}, {6, 7}, {1, 0}};

void reset(long seed) {
    angle = 0;
}

int[][] project(double angle) {
    int[][] result = new int[8][2];
    for (int i = 0; i < 8; i++) {
        double x = (i & 1) == 0 ? -1 : 1;
        double y = (i & 2) == 0 ? -1 : 1;
        double z = (i & 4) == 0 ? -1 : 1;
        double rotatedX = x * Math.cos(angle) + z * Math.sin(angle);
        double rotatedZ = -x * Math.sin(angle) + z * Math.cos(angle);
        double rotatedY = y * Math.cos(angle * 0.7) - rotatedZ * Math.sin(angle * 0.7);
        double depth = y * Math.sin(angle * 0.7) + rotatedZ * Math.cos(angle * 0.7);
        result[i][0] = 300 + (int)(rotatedX * 350 / (4 + depth));
        result[i][1] = 200 + (int)(rotatedY * 350 / (4 + depth));
    }
    return result;
}

void showWindow() {
    var frame = new JFrame("Wireframe Cube");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 600, 400);
            g.setColor(Color.CYAN);
            var points = project(angle);
            var distinct = uniqueEdges(edges);
            for (var edge : distinct) {
                g.drawLine(points[edge.a()][0], points[edge.a()][1], points[edge.b()][0], points[edge.b()][1]);
            }
            g.drawString("Distinct edges: " + distinct.size(), 12, 20);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var restart = new JButton("Reset angle");
    restart.addActionListener(event -> {
        reset(42);
        canvas.repaint();
    });
    controls.add(pause);
    controls.add(restart);
    var timer = new Timer(25, event -> {
        if (running) {
            angle += 0.025;
        }
        canvas.repaint();
    });
    frame.addWindowListener(new WindowAdapter(){

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

record Edge(int a, int b) {}

void main() {
    reset(42);
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
