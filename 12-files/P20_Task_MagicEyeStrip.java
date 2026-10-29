import java.awt.*;
import java.awt.image.BufferedImage;

/// # Task · MagicEyeStrip
/// Lesson: 12.5 Save only after the checks pass — 05-save.html
///
/// Keep a notebook of dot-field experiments rather than one anonymous number. Comments should explain
/// your choices while the last saved experiment recreates the picture.
///
/// **Your mission:** Let seed.txt become an experiment notebook: skip blank lines and lines beginning
/// with # after surrounding whitespace is removed, and use the last remaining integer as the selected
/// seed. Bad numeric lines and a notebook with no seed must still report failure. The ordinary
/// single-number file must keep working.
///
/// Run once to create seed.txt if absent. Edit it to include a # comment, 42 and a later -7 on
/// separate lines, then run again. The terminal reports the chosen seed and the Java window shows its
/// dot field.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Make a reproducibility challenge: give a friend only your seed file and ask them to recreate your
/// dot field. Then alter the hidden depth shape while keeping that file unchanged. Explain which part
/// the saved number reproduces and which part belongs to the program.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
long readSeed(Path path) throws IOException {
    return Long.parseLong(Files.readString(path).strip());
}

BufferedImage render(long seed, double option) {
    int depth = (int)option;
    if (!Double.isFinite(option) || depth < 1 || depth > 24) {
        throw new IllegalArgumentException("Disparity: 1..24 pixels.");
    }
    var image = new BufferedImage(600, 240, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    for (int y = 0; y < 240; y++) {
        for (int x = 0; x < 600; x++) {
            int shift = x > 230 && x < 420 && y > 60 && y < 180 ? 80 - depth : 80;
            int colour = x < 80 ? random.nextBoolean() ? 0xFFFFFF : 0 : image.getRGB(x - shift, y);
            image.setRGB(x, y, colour);
        }
    }
    return image;
}

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
    Path input = Path.of("seed.txt");
    try {
        if (!Files.exists(input)) Files.writeString(input, "42");
        long seed = readSeed(input);
        IO.println("Loaded seed from seed.txt: " + seed);
        showImages("Magic Eye Strip", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 16)});
    } catch (IOException | IllegalArgumentException failure) {
        IO.println("Cannot load seed.txt: " + failure.getMessage() + ". Check the notebook data lines.");
    }
}
// </editor-fold>
