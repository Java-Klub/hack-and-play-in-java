import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/// # Solved task · CafeWallLab
/// Lesson: 1.2 Statement order — 02-order.html
///
/// Original assignment (this version is complete):
///
/// **Uncover the hidden picture.** Run the program. You will see a grey area: the tile drawing
/// has been painted over.
///
/// **Your task:** In `paintWall` immediately below, swap the two lines inside `{ }`. Move each
/// whole line, including `(g);`. Keep both lines; you do not need to edit the rest of the program
/// yet.
///
/// **Result:** After saving and running again, you will see eight rows of black and white tiles.
/// The grey paint should stay behind them.

void paintWall(Graphics2D g) {
    paintBackground(g);
    paintTiles(g);
}

// <editor-fold defaultstate="collapsed" desc="Once the picture appears">
/// Left/Right shifts the tiles, Up/Down changes the gaps, and Space shows a red ruler. Try to
/// convince a friend that the horizontal lines are tilted, then reveal the ruler.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void paintBackground(Graphics2D g) {
    g.setColor(new Color(145, 145, 145));
    g.fillRect(0, 0, WIDTH, HEIGHT);
}

void paintTiles(Graphics2D g) {
    for (int row = 0; row < 8; row++) {
        int shift = row % 2 == 0 ? 0 : offset;
        for (int tile = -1; tile < 12; tile++) {
            g.setColor(Math.floorMod(tile, 2) == 0 ? Color.WHITE : Color.BLACK);
            g.fillRect(tile * 60 + shift, rowY(row), 60, 40);
        }
    }
}

final int WIDTH = 640, HEIGHT = 450;

int offset, mortar;
boolean guide;
void restart() {
    offset = 25;
    mortar = 4;
    guide = false;
}

void adjust(int shift, int width) {
    offset = Math.max(0, Math.min(60, offset + shift));
    mortar = Math.max(1, Math.min(14, mortar + width));
}

int rowY(int row) {
    return 30 + row * (40 + mortar);
}

void draw(Graphics2D g) {
    paintWall(g);
    if (guide) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawLine(0, rowY(4) - mortar / 2, WIDTH, rowY(4) - mortar / 2);
    }
    g.setColor(new Color(255, 220, 90));
    g.fillRect(0, 0, WIDTH, 25);
    g.setColor(Color.BLACK);
    g.drawString("Offset: " + offset + "  Mortar: " + mortar + "   The horizontal edges remain straight.", 10, 18);
}

void key(int code, boolean pressed) {
    if (pressed && code == KeyEvent.VK_R) {
        restart();
    }
    if (pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT -> adjust(-2, 0);
            case KeyEvent.VK_RIGHT -> adjust(2, 0);
            case KeyEvent.VK_UP -> adjust(0, 1);
            case KeyEvent.VK_DOWN -> adjust(0, -1);
            case KeyEvent.VK_SPACE -> guide = !guide;
        }
    }
}

void showWindow() {
    var frame = new JFrame("Caf\u00e9 Wall Lab");
    var panel = new JPanel() {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            var g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            draw(g);
            g.dispose();
        }
    };
    panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    panel.setBackground(Color.WHITE);
    panel.setFocusable(true);
    panel.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
            key(e.getKeyCode(), true);
            panel.repaint();
        }
    });
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(panel);
    var help = new JLabel("<html><div style=\"text-align:center\">Left / Right: offset   Up / Down: mortar width   Space: red guide   R: reset</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

void main() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("This game needs a graphical display; model checks still work headlessly.");
        return;
    }
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}

// </editor-fold>
