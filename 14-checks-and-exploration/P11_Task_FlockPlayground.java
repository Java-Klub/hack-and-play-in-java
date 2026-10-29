import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Úkol · FlockPlayground
/// Lekce: 14.2 Skládej objekty podle jejich chování — 02-interfaces.html
///
/// Hejno už uhýbá oranžové překážce. Dej mu druhou osobnost a předpověz, zda přitahování vytvoří
/// shluk, vír nebo něco jiného.
///
/// **Tvoje mise:** Ať tlačítko osobnosti změní chování, nejen nápis. Shy používá existující
/// odpuzování; Curious přesně jeho opačnou hodnotu při stejné vzdálenosti. Přepnutí zachová polohy i
/// rychlosti ptáků, pauzu i překážku. Dvojí přepnutí vrátí Shy; nové hejno zachová zvolenou osobnost.
///
/// **Zkoumání:** Za běhu vyměň roli chování a otestuj hypotézu pohybu.
///
/// Pohybuj oranžovou překážkou mezi ptáky. Porovnávej pomocí Shy/Curious, Pause/Run a New flock; ve
/// starter tlačítko osobnosti mění jen nápis.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// Navrhni třetí osobnost: velmi blízké ptáky odpuzuje, vzdálenější uvnitř stávajícího poloměru
/// přitahuje. Předpověz tvar a potom popiš skutečný pohyb hejna. Roli pojmenuj, ale neslibuj stabilní
/// kruh.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
Avoidance makeAvoidance(double radius) {
    if (!Double.isFinite(radius) || radius <= 0) {
        throw new IllegalArgumentException("Positive finite radius required.");
    }
    return distance -> {
        if (distance <= 0 || distance >= radius) return 0;
        return 0.2 * (1 - distance / radius);
    };
}

Avoidance avoidance = makeAvoidance(75);

double obstaclePush(double distance) {
    return avoidance.strength(distance);
}

final int COUNT = 36;
double[] x = new double[COUNT];
double[] y = new double[COUNT];
double[] vx = new double[COUNT];
double[] vy = new double[COUNT];
double obstacleX = 300;
double obstacleY = 200;
boolean running = true;

void reset(long seed) {
    var random = new Random(seed);
    for (int i = 0; i < COUNT; i++) {
        x[i] = random.nextDouble() * 600;
        y[i] = random.nextDouble() * 400;
        double angle = random.nextDouble() * Math.PI * 2;
        vx[i] = Math.cos(angle) * 2;
        vy[i] = Math.sin(angle) * 2;
    }
}

void step() {
    double[] nx = new double[COUNT];
    double[] ny = new double[COUNT];
    for (int i = 0; i < COUNT; i++) {
        double ax = 0;
        double ay = 0;
        int neighbours = 0;
        for (int j = 0; j < COUNT; j++) {
            if (i == j) {
                continue;
            }
            double dx = x[j] - x[i];
            double dy = y[j] - y[i];
            double distance = Math.hypot(dx, dy);
            if (distance > 0 && distance < 80) {
                ax += (vx[j] - vx[i]) * 0.04 + dx * 0.001;
                ay += (vy[j] - vy[i]) * 0.04 + dy * 0.001;
                if (distance < 24) {
                    ax -= dx * 1.8 / (distance * distance);
                    ay -= dy * 1.8 / (distance * distance);
                }
                neighbours++;
            }
        }
        if (neighbours > 0) {
            ax /= neighbours;
            ay /= neighbours;
        }
        double dx = x[i] - obstacleX;
        double dy = y[i] - obstacleY;
        double distance = Math.hypot(dx, dy);
        if (distance > 0) {
            ax += dx / distance * obstaclePush(distance);
            ay += dy / distance * obstaclePush(distance);
        }
        nx[i] = vx[i] + ax;
        ny[i] = vy[i] + ay;
        double speed = Math.hypot(nx[i], ny[i]);
        if (speed > 3) {
            nx[i] *= 3 / speed;
            ny[i] *= 3 / speed;
        }
    }
    vx = nx;
    vy = ny;
    for (int i = 0; i < COUNT; i++) {
        x[i] = (x[i] + vx[i] + 600) % 600;
        y[i] = (y[i] + vy[i] + 400) % 400;
    }
}

void showWindow() {
    var frame = new JFrame("Flock Playground");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.setColor(new Color(15, 25, 40));
            g.fillRect(0, 0, 600, 400);
            g.setColor(Color.ORANGE);
            g.drawOval((int)obstacleX - 35, (int)obstacleY - 35, 70, 70);
            g.setColor(Color.CYAN);
            for (int i = 0; i < COUNT; i++) {
                double angle = Math.atan2(vy[i], vx[i]);
                var bird = new Polygon();
                for (double turn : new double[]{0, 2.5, -2.5}) {
                    bird.addPoint((int)(x[i] + 8 * Math.cos(angle + turn)), (int)(y[i] + 8 * Math.sin(angle + turn)));
                }
                g.fillPolygon(bird);
            }
        }
    };
    canvas.setPreferredSize(new Dimension(600, 400));
    var controls = new JPanel();
    canvas.addMouseMotionListener(new MouseMotionAdapter(){

        public void mouseMoved(MouseEvent event) {
            obstacleX = event.getX();
            obstacleY = event.getY();
        }
    });
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var restart = new JButton("New flock");
    restart.addActionListener(event -> {
        reset(System.nanoTime());
        canvas.repaint();
    });
    var personality = new JButton("Shy: switch to Curious");
    personality.addActionListener(event -> {
        changePersonality();
        personality.setText(curious ? "Curious: switch to Shy" : "Shy: switch to Curious");
    });
    controls.add(personality);
    controls.add(pause);
    controls.add(restart);
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

interface Avoidance { double strength(double distance); }

boolean curious;

void changePersonality() {
    curious = !curious;
}

void main() {
    reset(42);
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
