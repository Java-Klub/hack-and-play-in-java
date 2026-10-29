import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · ColourFilterSecrets
/// Lekce: 11.5 Stejný začátek, stejný pokus — 05-seeds.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Ukryj jasný nápis do barevného šumu a potom jej odhal pohledem na červenou složku. Změna seed
/// (počáteční hodnoty) má změnit maskování a zachovat čitelnou zprávu.
///
/// **Tvoje mise:** Šum zůstává stejný při každém požadovaném seed (počáteční hodnotě). Oprav zdroj náhody
/// a zachovej složku, která nese ukrytou masku.
///
/// Po spuštění programu napiš seed (počáteční číslo) nebo stiskni Enter pro 42; prohlédni Java
/// obrázek, potom jej zavři a zopakuj stejný seed nebo porovnej 43.

BufferedImage hide(BufferedImage mask, long seed) {
    var random = new Random(seed);
    var image = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int red = (mask.getRGB(x, y) & 255) > 127 ? 240 : 15;
            image.setRGB(x, y, (red << 16) | (random.nextInt(256) << 8) | random.nextInt(256));
        }
    }
    return image;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Schovej vlastní malý znak změnou kresby mask (masky). Potom přesuň zprávu do green channel
/// (zeleného kanálu) a odpovídajícím způsobem změň odhalení. Kamarádovi nejdřív ukaž jen zašuměný
/// obrázek, potom správný filtr.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String message = "SECRET";

BufferedImage mask(String text) {
    if (text.isBlank() || text.length() > 16) {
        throw new IllegalArgumentException("Message: 1..16 characters.");
    }
    var image = new BufferedImage(600, 120, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        g.drawString(text, 12, 80);
    } finally {
        g.dispose();
    }
    return image;
}

BufferedImage reveal(BufferedImage image) {
    var result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int red = (image.getRGB(x, y) >> 16) & 255;
            result.setRGB(x, y, red * 0x010101);
        }
    }
    return result;
}

BufferedImage render(long seed, double option) {
    var hidden = hide(mask(message), seed);
    var clear = reveal(hidden);
    var image = new BufferedImage(600, 240, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.drawImage(hidden, 0, 0, null);
        g.drawImage(clear, 0, 120, null);
    } finally {
        g.dispose();
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
    String input = IO.readln("Seed (blank 42, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
        showImages("Colour Filter Secrets", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 1)});
    } catch (IllegalArgumentException failure) {
        IO.println("Use a whole-number seed: " + failure.getMessage());
    }
}
// </editor-fold>
