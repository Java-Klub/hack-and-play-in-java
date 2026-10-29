import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

/// # Solved task · PendulumPainter
/// Lesson: 14.2 Compose objects by their behaviour — 02-interfaces.html
///
/// Original assignment (this version is complete):
///
/// Let a pendulum draw its own luminous trail. Change length and gravity to discover how the same
/// starting swing produces different motion.
///
/// **Your mission:** Implement makeAcceleration and install its result in acceleration. Its role
/// receives angle and length and returns -gravity × sin(angle) / length, using the current gravity
/// field. The supplied accelerationNow adapter and step integration stay unchanged.

Acceleration makeAcceleration() {
    return (angle, length) -> {
        double restoring = -gravity * Math.sin(angle);
        return restoring / length;
    };
}

Acceleration acceleration = makeAcceleration();

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Create an alien pendulum by replacing the sine restoring rule with a gentle linear rule near the
/// centre. Compare equal initial angle, length and gravity. Label it as a new simulation rule rather
/// than a more accurate physical model.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
double accelerationNow() {
    return acceleration.at(angle, length);
}

double angle = 0.9;
double velocity;
double length = 180;
double gravity = 300;
boolean running = true;
java.awt.image.BufferedImage trace = new java.awt.image.BufferedImage(600, 450, java.awt.image.BufferedImage.TYPE_INT_RGB);
int ticks;

Point bob() {
    return new Point(300 + (int)(length * Math.sin(angle)), 70 + (int)(length * Math.cos(angle)));
}

void reset(long seed) {
    angle = 0.9;
    velocity = 0;
    ticks = 0;
    trace = new java.awt.image.BufferedImage(600, 450, java.awt.image.BufferedImage.TYPE_INT_RGB);
}

void step() {
    Point before = bob();
    velocity += accelerationNow() * 0.03;
    velocity *= 0.999;
    angle += velocity * 0.03;
    Point after = bob();
    var g = trace.createGraphics();
    try {
        g.setColor(Color.getHSBColor(ticks++ / 700.0F, 0.8F, 1));
        g.drawLine(before.x, before.y, after.x, after.y);
    } finally {
        g.dispose();
    }
}

void showWindow() {
    var frame = new JFrame("Pendulum Painter");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    var canvas = new JPanel(){

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D)graphics;
            g.drawImage(trace, 0, 0, null);
            Point point = bob();
            g.setColor(Color.GRAY);
            g.drawLine(300, 70, point.x, point.y);
            g.setColor(Color.WHITE);
            g.fillOval(point.x - 7, point.y - 7, 14, 14);
        }
    };
    canvas.setPreferredSize(new Dimension(600, 450));
    canvas.setBackground(Color.BLACK);
    var controls = new JPanel();
    var lengthControl = new JSlider(80, 230, 180);
    lengthControl.setPreferredSize(new Dimension(110, 25));
    lengthControl.addChangeListener(event -> length = lengthControl.getValue());
    var gravityControl = new JSlider(50, 600, 300);
    gravityControl.setPreferredSize(new Dimension(110, 25));
    gravityControl.addChangeListener(event -> gravity = gravityControl.getValue());
    var pause = new JButton("Pause / Run");
    pause.addActionListener(event -> running = !running);
    var reset = new JButton("Reset");
    reset.addActionListener(event -> {
        reset(42);
        canvas.repaint();
    });
    controls.add(new JLabel("Length"));
    controls.add(lengthControl);
    controls.add(new JLabel("Gravity"));
    controls.add(gravityControl);
    controls.add(pause);
    controls.add(reset);
    var timer = new Timer(20, event -> {
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

interface Acceleration { double at(double angle, double length); }

void main() {
    reset(42);
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>
