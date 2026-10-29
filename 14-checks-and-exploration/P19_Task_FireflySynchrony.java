import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Task · FireflySynchrony
/// Lesson: 14.3 Ask which kind of data arrived — 03-patterns.html
///
/// **Connect the firefly controls.** The fireflies already flash, but the control panel has no
/// effect yet.
///
/// **Your task:** Complete `apply` below for three control types. Coupling sets the strength to
/// its supplied finite value from 0 to 2 inclusive; ignore invalid values and keep the previous
/// setting. Toggle switches running on or off. Restart recreates initial phases using the
/// supplied `reset` and the seed carried by its command.
///
/// **Result:** The Coupling slider changes the shared rhythm, Pause / Run stops and resumes time,
/// and New phases restores the phases. Keep the supplied panel handlers sending their commands to
/// the single `apply` method.

void apply(Control control) {
    // Match all control alternatives and give each one its own effect.
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Add a new control card that sets every firefly phase to the same value. Wire one button through
/// apply instead of changing phases in its listener. Compare this instant reset with gradual
/// coupling: the two routes can look similar initially but have different responsibilities.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### A guarded match does not discard the matched value
///
/// Trace Coupling with value 1.5: the record type matches and all three guard conditions pass. The
/// value is still available to the chosen action. Next trace value -1: the guard fails and the
/// following Coupling case deliberately makes no change. Starting from an accepted setting, predict
/// the setting after that rejected command. Toggle carries no components, while Restart carries a
/// seed; neither should accidentally reuse a coupling value. This is why type matching, guard
/// checking and the case action are three separate questions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double[] phase = new double[40];
double[] frequency = new double[40];
double coupling = 1;
boolean running = true;

void reset(long seed) {
    var random = new Random(seed);
    for (int i = 0; i < phase.length; i++) {
        phase[i] = random.nextDouble() * Math.PI * 2;
        frequency[i] = 1 + (random.nextDouble() - 0.5) * 0.04;
    }
}

double order() {
    double x = 0;
    double y = 0;
    for (double value : phase) {
        x += Math.cos(value);
        y += Math.sin(value);
    }
    return Math.hypot(x, y) / phase.length;
}

void step() {
    double[] next = new double[phase.length];
    for (int i = 0; i < phase.length; i++) {
        double pull = 0;
        for (double other : phase) {
            pull += Math.sin(other - phase[i]);
        }
        next[i] = (phase[i] + 0.03 * (frequency[i] + coupling * pull / phase.length) + Math.PI * 2) % (Math.PI * 2);
    }
    phase = next;
}

void showWindow() {
    var frame = new JFrame("Firefly Synchrony");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(new Color(10, 20, 35));
            g.fillRect(0, 0, 600, 400);
            for (int i = 0; i < phase.length; i++) {
                g.setColor(Math.cos(phase[i]) > 0.94 ? Color.YELLOW : new Color(50, 70, 35));
                g.fillOval(35 + i % 8 * 70, 30 + i / 8 * 70, 24, 24);
            }
            g.setColor(Color.WHITE);
            g.drawString("Synchrony: " + String.format(Locale.ROOT, "%.2f", order()), 15, 390);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    var strength = new JSlider(0, 200, 100);
    strength.addChangeListener(event -> apply(new Coupling(strength.getValue() / 100.0)));
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> apply(new Toggle()));
    var reset = new JButton("New phases");
    reset.addActionListener(event -> {
        apply(new Restart(System.nanoTime()));
        canvas.repaint();
    });
    controls.add(new JLabel("Coupling"));
    controls.add(strength);
    controls.add(pause);
    controls.add(reset);
    var timer = new Timer(25, event -> {
        if (running) {
            step();
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

sealed interface Control permits Coupling, Toggle, Restart {}
record Coupling(double value) implements Control {}
record Toggle() implements Control {}
record Restart(long seed) implements Control {}

void main() {
    reset(42);
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
