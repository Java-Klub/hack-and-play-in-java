import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Vyřešený úkol · FireflySynchrony
/// Lekce: 14.3 Zjisti, která datová varianta přišla — 03-patterns.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Zprovozni ovládání světlušek.** Světlušky už blikají, ale ovládací panel je zatím
/// neovlivňuje.
///
/// **Úkol:** Doplň `apply` níže pro tři druhy ovládání. Coupling nastaví sílu vazby na předanou
/// konečnou hodnotu 0 až 2 včetně; neplatnou hodnotu ignoruje a ponechá staré nastavení. Toggle
/// přepne běh mezi zapnuto/vypnuto. Restart vytvoří počáteční fáze pomocí připravené `reset` a
/// seedu ze svého příkazu.
///
/// **Výsledek:** Posuvník Coupling změní společný rytmus, Pause / Run čas zastaví a znovu pustí a
/// New phases obnoví fáze. Připravené obsluhy panelu dál posílají své příkazy do jediné `apply`.

void apply(Control control) {
    switch (control) {
        case Coupling(var value) when Double.isFinite(value) && value >= 0 && value <= 2 -> coupling = value;
        case Coupling(var value) -> { }
        case Toggle() -> running = !running;
        case Restart(var seed) -> reset(seed);
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Přidej control card (ovládací kartu), která nastaví všem světluškám stejnou fázi. Nové tlačítko
/// zapoj přes apply, místo změny fází přímo v listener (obsluze události). Porovnej okamžité srovnání
/// s postupným coupling (vazbou): podobný obraz může vzniknout jinou cestou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Guard (podmínka) nemaže rozpoznanou hodnotu
///
/// Trasuj Coupling s hodnotou 1.5: typ záznamu souhlasí a všechny tři guard conditions (dodatečné
/// podmínky) platí. Hodnota je stále dostupná vybrané akci. Potom trasuj hodnotu -1: guard neprojde a
/// následující Coupling větev záměrně nic nezmění. Začni od přijatého nastavení a předpověz nastavení
/// po tomto odmítnutém příkazu. Toggle nemá žádné složky, zatímco Restart nese seed (počáteční
/// hodnotu); ani jeden nemá omylem použít sílu vazby. Proto jsou rozpoznání typu, ověření podmínky a
/// akce větve tři různé otázky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
