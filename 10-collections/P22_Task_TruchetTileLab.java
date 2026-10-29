import java.awt.*;
import java.awt.image.BufferedImage;

/// # Solved task · TruchetTileLab
/// Lesson: 10.3 Each name has its own account — 03-maps.html
///
/// Original assignment (this version is complete):
///
/// Steer a winding tile pattern while it grows. Keep one orientation from running too far ahead, so a
/// Map becomes a drawing rule rather than only a score caption.
///
/// **Your mission:** Build chooseOrientation(preferred). Accept only LEFT or RIGHT. Usually return the
/// preferred orientation, but if its current count already leads the other by at least two, choose the
/// other. Increment only the chosen count and return that choice. Invalid text must throw without
/// changing the Map. Your decision steers the real arcs as the pattern grows.
///
/// Run the program and inspect the curved pattern and LEFT/RIGHT counts. After the repair, both counts
/// account for the tiles and the steering policy changes the actual curves.

String chooseOrientation(String preferred) {
    if (!preferred.equals("LEFT") && !preferred.equals("RIGHT")) throw new IllegalArgumentException("Use LEFT or RIGHT.");
    String other = preferred.equals("LEFT") ? "RIGHT" : "LEFT";
    int count = orientationCounts.getOrDefault(preferred, 0);
    int otherCount = orientationCounts.getOrDefault(other, 0);
    String chosen = count - otherCount >= 2 ? other : preferred;
    orientationCounts.put(chosen, orientationCounts.getOrDefault(chosen, 0) + 1);
    return chosen;
}

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Change the permitted lead to four, or deliberately favour LEFT. Compare the resulting loops using
/// the same random seed, so only your steering rule changes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 2 || count > 30) {
        throw new IllegalArgumentException("Tiles per side: 2..30.");
    }
    orientationCounts.clear();
    int size = 480 / count;
    var image = new BufferedImage(size * count, size * count, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    var g = image.createGraphics();
    try {
        g.setColor(new Color(245, 230, 205));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(new Color(20, 65, 90));
        g.setStroke(new BasicStroke(Math.max(2, size / 7)));
        for (int row = 0; row < count; row++) {
            for (int col = 0; col < count; col++) {
                int x = col * size;
                int y = row * size;
                String orientation = chooseOrientation(random.nextBoolean() ? "LEFT" : "RIGHT");
                if (orientation.equals("LEFT")) {
                    g.drawArc(x - size / 2, y - size / 2, size, size, 270, 90);
                    g.drawArc(x + size / 2, y + size / 2, size, size, 90, 90);
                } else {
                    g.drawArc(x + size / 2, y - size / 2, size, size, 180, 90);
                    g.drawArc(x - size / 2, y + size / 2, size, size, 0, 90);
                }
            }
        }
        g.drawString("LEFT " + orientationCounts.getOrDefault("LEFT", 0) + " / RIGHT " + orientationCounts.getOrDefault("RIGHT", 0), 8, 14);
    } finally {
        g.dispose();
    }
    return image;
}

Map<String, Integer> orientationCounts = new HashMap<>();

void showImages(String title, String[] captions, java.awt.image.BufferedImage[] images) {
    if (java.awt.GraphicsEnvironment.isHeadless()) return;
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame(title);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        var pictures = new javax.swing.JPanel(new java.awt.GridLayout(1, images.length, 8, 8));
        for (int i = 0; i < images.length; i++) {
            var picture = new javax.swing.JPanel(new java.awt.BorderLayout());
            picture.add(new javax.swing.JLabel(captions[i], javax.swing.SwingConstants.CENTER), java.awt.BorderLayout.NORTH);
            double scale = Math.min(1, Math.min(900.0 / images.length / images[i].getWidth(), 550.0 / images[i].getHeight()));
            var icon = new javax.swing.ImageIcon(images[i].getScaledInstance(
                    Math.max(1, (int)(images[i].getWidth() * scale)),
                    Math.max(1, (int)(images[i].getHeight() * scale)), java.awt.Image.SCALE_FAST));
            picture.add(new javax.swing.JLabel(icon), java.awt.BorderLayout.CENTER);
            pictures.add(picture);
        }
        frame.add(pictures);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    try {
        showImages("Truchet Tile Lab", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 12)});
    } catch (Exception failure) {
        IO.println("Cannot draw this result; check the editable rule: " + failure);
    }
}
// </editor-fold>
