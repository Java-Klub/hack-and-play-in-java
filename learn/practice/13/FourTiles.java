import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

int picked = -1;

void choose(int x, int y) {
    picked = x / 80;
}

void draw(Graphics2D g) {
    for (int tile = 0; tile < 4; tile++) {
        int x = tile % 2 * 80;
        int y = tile / 2 * 80;
        g.setColor(tile == picked ? Color.GREEN : Color.LIGHT_GRAY);
        g.fillRect(x + 2, y + 2, 76, 76);
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
        panel.setPreferredSize(new Dimension(160, 160));
        panel.setBackground(Color.WHITE);
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent event) {
                choose(event.getX(), event.getY());
                panel.repaint();
            }
        });
        var frame = new JFrame("Four Tiles");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
