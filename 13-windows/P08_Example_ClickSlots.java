import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/// # Hotový příklad · 13.2 Proměň kliknutí ve volbu
/// Lekce: 02-input-events.html

int picked = -1;

void choose(int x, int y) {
    if (x < 0 || x >= 120 || y < 0 || y >= 40) return;
    picked = x / 40;
}

void draw(Graphics2D g) {
    for (int slot = 0; slot < 3; slot++) {
        g.setColor(slot == picked ? Color.GREEN : Color.LIGHT_GRAY);
        g.fillRect(slot * 40 + 2, 2, 36, 36);
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
        panel.setPreferredSize(new Dimension(120, 40));
        panel.setBackground(Color.WHITE);
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent event) {
                choose(event.getX(), event.getY());
                panel.repaint();
            }
        });
        var frame = new JFrame("Click Slots");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
