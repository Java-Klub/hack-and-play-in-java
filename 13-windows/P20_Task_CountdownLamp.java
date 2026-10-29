import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/// # Vyřešený úkol · 13.4 počítej sekundy, nikoli callback
/// Lekce: 04-timers.html

double remaining = 3;

void step(double seconds) {
    if (seconds < 0) throw new IllegalArgumentException("Negative time.");
    remaining = Math.max(0, remaining - seconds);
}

void draw(Graphics2D g) {
    g.setColor(remaining > 0 ? Color.GREEN : Color.GRAY);
    g.fillRect(10, 10, (int) Math.round(remaining * 60), 30);
    g.setColor(Color.BLACK);
    g.drawString("Seconds: " + (int) Math.ceil(remaining), 10, 60);
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                draw((Graphics2D) graphics);
            }
        };
        panel.setPreferredSize(new Dimension(200, 80));
        panel.setBackground(Color.WHITE);
        var timer = new Timer(100, event -> {
            step(0.1);
            panel.repaint();
            if (remaining <= 0) ((Timer) event.getSource()).stop();
        });
        var play = new JButton("Start / Restart");
        play.addActionListener(event -> {
            remaining = 3;
            panel.repaint();
            timer.start();
        });
        var frame = new JFrame("Countdown Lamp");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.add(play, BorderLayout.SOUTH);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) {
                timer.stop();
            }
        });
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
