import java.awt.*;
import java.awt.image.BufferedImage;

/// # Task · CrystalGrowth
/// Lesson: 14.4 Extract one shared idea — 04-refactoring.html
///
/// Grow a crystal by letting wandering particles stick to an existing cluster. Allowing diagonal contact
/// should give the growth a different character from edge-only contact.
///
/// **Your mission:** The new diagonal mode behaves exactly like the old four-neighbour mode. Extend the
/// extracted contact rule while preserving the original option and safe boundary checks.

boolean touching(boolean[][] grid, int x, int y, boolean diagonals) {
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) continue;
            if (dx != 0 && dy != 0) continue;
            int nx = x + dx, ny = y + dy;
            if (ny >= 0 && ny < grid.length && nx >= 0 && nx < grid[ny].length && grid[ny][nx]) return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Create a third contact rule that accepts only diagonals. Keep the original four-neighbour and
/// eight-neighbour options named and separate. Grow all three from the same seed and particle budget,
/// then describe the branches you actually obtain.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
boolean[][] grow(long seed, int particles) {
    if (particles < 1 || particles > 1200) {
        throw new IllegalArgumentException("Particles: 1..1200.");
    }
    boolean[][] stuck = new boolean[161][161];
    stuck[80][80] = true;
    var random = new Random(seed);
    double radius = 2;
    int[] dx = {1, -1, 0, 0};
    int[] dy = {0, 0, 1, -1};
    for (int particle = 0; particle < particles && radius < 65; particle++) {
        double angle = random.nextDouble() * Math.PI * 2;
        double launch = radius + 6;
        int x = 80 + (int)(Math.cos(angle) * launch);
        int y = 80 + (int)(Math.sin(angle) * launch);
        for (int walk = 0; walk < 12000; walk++) {
            if (x < 1 || x > 159 || y < 1 || y > 159 || Math.hypot(x - 80, y - 80) > launch + 15) {
                break;
            }
            if (!stuck[y][x] && touching(stuck, x, y, diagonalGrowth)) {
                stuck[y][x] = true;
                radius = Math.max(radius, Math.hypot(x - 80, y - 80));
                break;
            }
            int direction = random.nextInt(4);
            x += dx[direction];
            y += dy[direction];
        }
    }
    return stuck;
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite particle count required.");
    }
    var stuck = grow(seed, (int)option);
    var image = new BufferedImage(483, 483, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int y = 0; y < 161; y++) {
            for (int x = 0; x < 161; x++) {
                if (stuck[y][x]) {
                    g.setColor(Color.getHSBColor((float)Math.hypot(x - 80, y - 80) / 90, 0.6F, 1));
                    g.fillRect(x * 3, y * 3, 3, 3);
                }
            }
        }
    } finally {
        g.dispose();
    }
    return image;
}

boolean diagonalGrowth = true;

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
    diagonalGrowth = true;
    var changed = render(42, 600);
    diagonalGrowth = false;
    var original = render(42, 600);
    diagonalGrowth = true;
    showImages("Crystal Growth", new String[]{"New rule", "Original rule"}, new java.awt.image.BufferedImage[]{changed, original});
}
// </editor-fold>
