import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// # Úkol · PixelSortingGlitch
/// Lekce: 12.4 Přečti data ze skutečného souboru — 04-files.html
///
/// Vytáhni z obrázku pruhy řazením vybraných úseků bodů. Práh mění místa, kde se glitch zachytí a kde
/// původní obraz přežije.
///
/// **Tvoje mise:** Upravený obrázek se uloží jako lost-pixels.png místo zvoleného názvu. Oprav cíl
/// uložení a neměň vstupní obrázek.
///
/// Program zapíše exports/pixel-sorted.png přes saveSorted a načte právě tento soubor do Java okna.
/// Pokud požadovaný soubor chybí, přečti výstup v terminálu a oprav jeho cíl.

void saveSorted(Path path, BufferedImage input, int threshold) throws IOException {
    Files.createDirectories(path.toAbsolutePath().getParent());
    if (!ImageIO.write(sort(input, threshold), "png", path.resolveSibling("lost-pixels.png").toFile())) {
        throw new IOException("PNG writer missing.");
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř glitch triptych (trojici poruchových obrazů): pro stejný vstup zvol tři threshold (prahové
/// hodnoty) a ulož tři záměrně pojmenované PNG. Předpověz, který práh zachová nejvíce původních
/// pixelů, a porovnej obrázky bez změny vstupu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
