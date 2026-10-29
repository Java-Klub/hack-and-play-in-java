import java.awt.*;
import java.awt.image.BufferedImage;

/// # Solved task · KochSnowflakeCards
/// Lesson: 7.2 Visit values without picking each one — 02-traversal.html
///
/// Original assignment (this version is complete):
///
/// Make a snowflake postcard whose three sides grow smaller zigzags. The supplied edge builder can add
/// intricate detail, while your part decides whether the shape closes all the way around.
///
/// **Your mission:** The displayed snowflake is missing a whole side. Repair the side loop in
/// snowflake so corner indices 0, 1 and 2 are each visited once; keep the prepared edge builder.
///
/// Run the program: the picture appears immediately in a Java window.

int snowflake(Graphics2D g, int level) {
    int[] xs = {270, 70, 470};
    int[] ys = {45, 390, 390};
    int segments = 0;
    for (int side = 0; side < 3; side++) {
        int next = (side + 1) % 3;
        segments += edge(g, xs[side], ys[side], xs[next], ys[next], level);
    }
    return segments;
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int edge(Graphics2D g, double x, double y, double nx, double ny, int level) {
    if (level == 0) {
        g.drawLine((int)x, (int)y, (int)nx, (int)ny);
        return 1;
    }
    double dx = (nx - x) / 3;
    double dy = (ny - y) / 3;
    double ax = x + dx;
    double ay = y + dy;
    double bx = ax + dx * 0.5 - dy * Math.sqrt(3) / 2;
    double by = ay + dx * Math.sqrt(3) / 2 + dy * 0.5;
    return edge(g, x, y, ax, ay, level - 1)
        + edge(g, ax, ay, bx, by, level - 1)
        + edge(g, bx, by, x + 2 * dx, y + 2 * dy, level - 1)
        + edge(g, x + 2 * dx, y + 2 * dy, nx, ny, level - 1);
}

BufferedImage render(long seed, double option) {
    int level = (int)option;
    if (!Double.isFinite(option) || level < 0 || level > 6) {
        throw new IllegalArgumentException("Level: 0..6.");
    }
    var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 540, 480);
        g.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.8F, 0.7F));
        snowflake(g, level);
    } finally {
        g.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42, 4);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("KochSnowflakeCards");
        var picture = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
        picture.setOpaque(true);
        picture.setBackground(new java.awt.Color(35, 39, 48));
        frame.add(picture);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    showPicture();
}

// </editor-fold>
