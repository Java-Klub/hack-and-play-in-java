import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/// # Úkol · TwoNoisyPictures
/// Lekce: 11.5 Stejný začátek, stejný pokus — 05-seeds.html
///
/// Rozděl tajemství do dvou zašuměných černobílých obrázků. Každý díl má sám vypadat bezradně, zatímco
/// jejich překrytí odhalí skrytý tvar.
///
/// **Tvoje mise:** Každý malý blok znovu použije stejné promíchání, protože se generátor restartuje
/// uvnitř pixel loop (cyklu obrazových bodů). Obnov různorodou, opakovatelnou sequence (posloupnost) pro
/// celý obrázek.
///
/// Po spuštění programu napiš seed (počáteční číslo) nebo stiskni Enter pro 42; prohlédni Java
/// obrázek, potom jej zavři a zopakuj stejný seed nebo porovnej 43.

BufferedImage[] shares(BufferedImage secret, long seed) {
    var images = new BufferedImage[3];
    for (int i = 0; i < 3; i++) {
        images[i] = new BufferedImage(secret.getWidth() * 2, secret.getHeight() * 2, BufferedImage.TYPE_INT_RGB);
    }
    var random = new Random(seed);
    for (int y = 0; y < secret.getHeight(); y++) {
        for (int x = 0; x < secret.getWidth(); x++) {
            var order = new ArrayList<>(List.of(0, 1, 2, 3));
            Collections.shuffle(order, new Random(seed));
            boolean ink = (secret.getRGB(x, y) & 255) > 127;
            for (int i = 0; i < 4; i++) {
                int dx = order.get(i) % 2;
                int dy = order.get(i) / 2;
                boolean first = i < 2;
                boolean second = ink ? !first : first;
                images[0].setRGB(x * 2 + dx, y * 2 + dy, first ? 0 : 0xFFFFFF);
                images[1].setRGB(x * 2 + dx, y * 2 + dy, second ? 0 : 0xFFFFFF);
                images[2].setRGB(x * 2 + dx, y * 2 + dy, first || second ? 0 : 0xFFFFFF);
            }
        }
    }
    return images;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř pozvánku na dvě části: do tajné masky vlož krátký znak a nech kamaráda zvolit seed
/// (počáteční stav). Porovnej dvojice z různých seedů: zamýšlený overlay (překryv) vzniká ze správné
/// dvojice. Vysvětli, proč se samotná zpráva se seedem měnit nemusí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String message = "JAVA";

BufferedImage mask() {
    if (message.isBlank() || message.length() > 7) {
        throw new IllegalArgumentException("Message: 1..7 characters.");
    }
    var image = new BufferedImage(180, 60, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 38));
        g.drawString(message, 5, 45);
    } finally {
        g.dispose();
    }
    return image;
}

BufferedImage render(long seed, double option) {
    return shares(mask(), seed)[2];
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
        var pair = shares(mask(), seed);
        showImages("Two Noisy Pictures", new String[]{"Overlay", "Share 1", "Share 2"},
                new java.awt.image.BufferedImage[]{render(seed, 1), pair[0], pair[1]});
    } catch (IllegalArgumentException failure) {
        IO.println("Use a whole-number seed: " + failure.getMessage());
    }
}
// </editor-fold>
