import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

/// # Solved task · PhotoRemixLab
/// Lesson: 10.4 Pass an ordering rule — 04-ordering.html
///
/// Original assignment (this version is complete):
///
/// A huge moon steals the spotlight while a tiny key is almost lost in the poster. Become an image
/// detective: use colour-frequency evidence to reveal the small object instead.
///
/// **Your mission:** The working starter highlights the most frequent source colour. Change the lens to
/// select the least frequent positive count, using the smaller signed RGB key to break ties. Empty data
/// returns 0; examining counts must not change them. Keep the real cyan highlight connected to your
/// choice so the tiny key becomes visible, not only a new caption.
///
/// Run the poster and note what glows cyan. Up/Down changes the poster threshold; R restores the scene.
/// After the detective lens is changed, the little key at the upper left glows cyan while the common
/// moon does not.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
void countColour(int rgb) {
    colourCounts.put(rgb, colourCounts.getOrDefault(rgb, 0) + 1);
}

final int WIDTH = 640, HEIGHT = 440;

BufferedImage source, result;
int threshold;
void restart() {
    source = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    var picture = source.createGraphics();
    for (int stripe = 0; stripe < 8; stripe++) {
        picture.setColor(new Color(20 + stripe * 12, 35, 70));
        picture.fillRect(stripe * 50, 0, 50, 300);
    }
    picture.setColor(Color.WHITE);
    picture.fillOval(120, 70, 150, 150);
    picture.setColor(Color.YELLOW);
    picture.fillRect(310, 40, 10, 10);
    picture.fillRect(335, 65, 10, 10);
    picture.setColor(Color.MAGENTA);
    picture.fillRect(35, 40, 6, 6);
    picture.fillRect(40, 42, 18, 2);
    picture.fillRect(52, 44, 2, 5);
    picture.dispose();
    threshold = 128;
    remix();
}

int spotlightColour() {
    int bestColour = 0, bestCount = 0;
    for (var entry : colourCounts.entrySet()) {
        int colour = entry.getKey(), count = entry.getValue();
        if (count > 0 && (bestCount == 0 || count < bestCount || count == bestCount && colour < bestColour)) {
            bestColour = colour;
            bestCount = count;
        }
    }
    return bestColour;
}

void remix() {
    int width = source.getWidth(), height = source.getHeight();
    colourCounts.clear();
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) countColour(source.getRGB(x, y));
    }
    int selected = spotlightColour();
    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            var colour = new Color(source.getRGB(x, y));
            int brightness = (colour.getRed() * 2126 + colour.getGreen() * 7152 + colour.getBlue() * 722) / 10000;
            int poster = (brightness < threshold ? new Color(25, 45, 80) : new Color(255, 205, 120)).getRGB();
            result.setRGB(x, y, source.getRGB(x, y) == selected ? Color.CYAN.getRGB() : poster);
        }
    }
}

void adjust(int amount) {
    threshold = Math.max(16, Math.min(240, threshold + amount));
    remix();
}

void draw(Graphics2D g) {
    g.drawImage(result, (WIDTH - result.getWidth()) / 2, 45, null);
    g.setColor(Color.BLACK);
    g.drawString("Spotlight | Threshold " + threshold, 12, 22);
    g.drawString("Pixels counted: " + countedPixels() + " / " + source.getWidth() * source.getHeight(), 12, 420);
}

void key(int code, boolean pressed) {
    if (!pressed) return;
    switch (code) {
        case KeyEvent.VK_R -> restart();
        case KeyEvent.VK_UP -> adjust(8);
        case KeyEvent.VK_DOWN -> adjust(-8);
    }
}

void showWindow() {
    var frame = new JFrame("Photo Remix Lab");
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
    var help = new JLabel("<html><div style=\"text-align:center\">Up/Down: poster threshold   R: restart</div></html>", SwingConstants.CENTER);
    help.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    frame.add(help, BorderLayout.SOUTH);
    frame.pack();
    frame.setResizable(false);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    panel.requestFocusInWindow();
}

Map<Integer, Integer> colourCounts = new HashMap<>();

int countedPixels() {
    int total = 0;
    for (int count : colourCounts.values()) total += count;
    return total;
}

void main() {
    restart();
    SwingUtilities.invokeLater(this::showWindow);
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Hide your own small single-colour symbol in a scene with larger colour regions. Give a partner the
/// normal most-common-colour lens and ask them to recover your symbol.
// </editor-fold>
