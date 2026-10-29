import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

void draw(Graphics2D g) {
    g.setColor(Color.BLUE);
    g.fillRect(20, 20, 100, 60);
    g.setColor(Color.YELLOW);
    g.fillOval(50, 30, 40, 40);
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
        var frame = new JFrame("Shape Card");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
