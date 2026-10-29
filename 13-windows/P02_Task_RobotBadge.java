import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/// # Task · 13.1 give the badge two eyes
/// Lesson: 01-drawing.html

void draw(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, 160, 100);
    g.setColor(Color.BLUE);
    g.fillRect(20, 20, 120, 60);
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                draw((Graphics2D) graphics);
            }
        };
        panel.setPreferredSize(new Dimension(160, 100));
        panel.setBackground(Color.WHITE);
        var frame = new JFrame("Robot Badge");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
