import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// # Task · PixelSortingGlitch
/// Lesson: 12.4 Read data from a real file — 04-files.html
///
/// Pull streaks out of a picture by sorting selected runs of pixels. The threshold changes where the
/// glitch catches and where the original image survives.
///
/// **Your mission:** The transformed picture is saved as lost-pixels.png instead of the chosen filename.
/// Repair its export destination without changing the input image.
///
/// The program writes exports/pixel-sorted.png through saveSorted and loads that exact file
/// into a Java window. If the requested file is missing, read terminal output and repair its
/// destination.

void saveSorted(Path path, BufferedImage input, int threshold) throws IOException {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(sort(input, threshold), "png", path.resolveSibling("lost-pixels.png").toFile())) {
        throw new IOException("PNG writer missing.");
    }
}

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// ### Make it yours
///
/// Make a glitch triptych: use three thresholds with the same input and save three deliberately named
/// PNG files. Predict which threshold will leave the most untouched pixels, then compare the actual
/// images without changing the input between trials.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage demo() {
    var image = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 160; y++) {
        for (int x = 0; x < 240; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 240.0F, 0.7F, 0.2F + y / 200.0F));
        }
    }
    return image;
}

int light(int rgb) {
    return (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 3;
}

BufferedImage sort(BufferedImage input, int threshold) {
    if (threshold < 0 || threshold > 255) {
        throw new IllegalArgumentException("Threshold: 0..255.");
    }
    var output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < input.getHeight(); y++) {
        int[] row = input.getRGB(0, y, input.getWidth(), 1, null, 0, input.getWidth());
        int start = 0;
        while (start < row.length) {
            if (light(row[start]) < threshold) {
                start++;
                continue;
            }
            int end = start;
            while (end < row.length && light(row[end]) >= threshold) {
                end++;
            }
            Integer[] streak = new Integer[end - start];
            for (int i = 0; i < streak.length; i++) {
                streak[i] = row[start + i];
            }
            Arrays.sort(streak, Comparator.comparingInt(this::light));
            for (int i = 0; i < streak.length; i++) {
                row[start + i] = streak[i];
            }
            start = end;
        }
        output.setRGB(0, y, row.length, 1, row, 0, row.length);
    }
    return output;
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
    Path output = Path.of("exports", "pixel-sorted.png");
    try {
        Files.deleteIfExists(output);
        saveSorted(output, demo(), 110);
        var image = ImageIO.read(output.toFile());
        if (image == null) throw new IOException("The requested PNG is empty or invalid.");
        IO.println("Loaded requested file: " + output.toAbsolutePath());
        showImages("Pixel Sorting Glitch", new String[]{output.toString()}, new java.awt.image.BufferedImage[]{image});
    } catch (IOException failure) {
        IO.println("Cannot load requested file " + output + ": " + failure.getMessage() + ". Repair saveSorted; Save the source, run its check command, then play again.");
    }
}
// </editor-fold>
