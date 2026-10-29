import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/// # Hotový příklad · 13.4 Posouvej simulaci po malých krocích
/// Lekce: 04-timers.html

double x = 20;

void step(double seconds) {
    if (seconds < 0) throw new IllegalArgumentException("Negative time.");
    x = Math.min(180, x + 80 * seconds);
}

void draw(Graphics2D g) {
    g.setColor(Color.BLUE);
    g.fillOval((int) Math.round(x) - 8, 22, 16, 16);
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                draw((Graphics2D) graphics);
            }
        };
        panel.setPreferredSize(new Dimension(200, 60));
        panel.setBackground(Color.WHITE);
        var timer = new Timer(20, event -> {
            step(0.02);
            panel.repaint();
            if (x >= 180) ((Timer) event.getSource()).stop();
        });
        var play = new JButton("Start / Restart");
        play.addActionListener(event -> {
            x = 20;
            panel.repaint();
            timer.start();
        });
        var frame = new JFrame("Moving Dot");
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
