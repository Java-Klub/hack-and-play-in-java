import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

int score = 0;

boolean hit() {
    return false;
}

void reset() {
    score = 0;
}

void draw(Graphics2D g) {
    score++;
    g.setColor(score >= 3 ? Color.GREEN : Color.BLUE);
    g.fillRect(10, 10, 160, 40);
    g.setColor(Color.BLACK);
    g.drawString("Score: " + score + (score >= 3 ? "  Won" : ""), 10, 75);
}

void main() {
    SwingUtilities.invokeLater(() -> {
        var panel = new JPanel() {
            @Override protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                draw((Graphics2D) graphics);
            }
        };
        panel.setPreferredSize(new Dimension(180, 90));
        panel.setBackground(Color.WHITE);
        var hit = new JButton("Hit");
        hit.addActionListener(event -> { hit(); panel.repaint(); });
        var reset = new JButton("Reset");
        reset.addActionListener(event -> { reset(); panel.repaint(); });
        var buttons = new JPanel();
        buttons.add(hit);
        buttons.add(reset);
        var frame = new JFrame("Score Badge");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.add(buttons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}
