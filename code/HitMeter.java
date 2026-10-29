import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

int points = 0;

boolean hit() {
    if (points >= 5) return false;
    points++;
    return true;
}

void reset() {
    points = 0;
}

void draw(Graphics2D g) {
    for (int i = 0; i < 5; i++) {
        g.setColor(i < points ? Color.GREEN : Color.LIGHT_GRAY);
        g.fillRect(10 + i * 30, 10, 24, 40);
    }
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                draw((Graphics2D) graphics);
            }
        };
        panel.setPreferredSize(new Dimension(170, 60));
        panel.setBackground(Color.WHITE);
        var hit = new JButton("Hit");
        hit.addActionListener(event -> { hit(); panel.repaint(); });
        var reset = new JButton("Reset");
        reset.addActionListener(event -> { reset(); panel.repaint(); });
        var buttons = new JPanel();
        buttons.add(hit);
        buttons.add(reset);
        var frame = new JFrame("Hit Meter");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.add(buttons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
