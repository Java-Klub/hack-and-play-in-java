import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · MagicEyeStrip
/// Lekce: 12.5 Ukládej až po ověření — 05-save.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Místo anonymního čísla si veď deník tečkovaných pokusů. Komentáře vysvětlí volby a poslední
/// uložený pokus znovu vytvoří obraz.
///
/// **Tvoje mise:** Proměň seed.txt v deník pokusů: po odstranění okrajových mezer přeskoč prázdné
/// řádky i řádky začínající # a jako zvolený seed použij poslední zbývající celé číslo. Chybný
/// číselný řádek i deník bez seed mají dál ohlásit chybu. Běžný soubor s jediným číslem musí stále
/// fungovat.
///
/// **Zkoumání:** Odvoď a implementuj malý formát uchovaného deníku pokusů.
///
/// Spusť jednou pro vytvoření chybějícího seed.txt. Na samostatné řádky přidej # komentář, 42 a
/// pozdější -7 a spusť znovu. Terminál oznámí vybraný seed a Java okno jeho tečkované pole.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Připrav výzvu opakovatelnosti: kamarádovi dej jen soubor se seed (počátečním stavem) a požádej ho
/// o stejnou tečkovanou plochu. Potom změň skrytý hloubkový tvar a soubor nech stejný. Vysvětli, co
/// reprodukuje uložené číslo a co patří programu.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
long readSeed(Path path) throws IOException {
    long selected = 0;
    boolean found = false;
    for (String line : Files.readAllLines(path)) {
        String text = line.strip();
        if (text.isEmpty() || text.startsWith("#")) continue;
        selected = Long.parseLong(text);
        found = true;
    }
    if (!found) throw new NumberFormatException("The notebook contains no seed.");
    return selected;
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
