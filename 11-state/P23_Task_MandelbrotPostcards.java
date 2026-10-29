import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · MandelbrotPostcards
/// Lekce: 11.5 Stejný začátek, stejný pokus — 05-seeds.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Pohlednice dorazila bez barevného receptu. Krajinu umíš znovu nakreslit; malý barevný proužek je
/// stopa k chybějícímu seed.
///
/// **Tvoje mise:** Tajemná paleta pochází z některého seed mezi 0 a 255. Vytvoř hledač všech seedů,
/// jejichž paleta začíná předaným vzorkem barev, ve vzestupném pořadí. Nemožný vzorek nemá kandidáty.
/// Běžné kreslení pohlednice zachovej; seed pro obrázek vyber svým nástrojem místo hádání.
///
/// **Zkoumání:** Vyřeš deterministickou záhadu vlastním nástrojem pro omezené hledání.
///
/// Přečti tajemný vzorek i zprávu o kandidátech, potom vyber seed palety a span (šířku záběru).
/// Obrázek ukáže referenční barevný proužek. Enter nechá běžné výchozí hodnoty; /q skončí.

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Uspořádej výstavu dvou pohlednic: zvol palette seed (počáteční stav barev) a span (šířku záběru)
/// pro celou množinu, potom jiný záběr a střed u okraje. Zapiš všechna čtyři čísla, ne jen seed.
/// Paleta mění barvu; span a střed mění viditelnou krajinu.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program k prozkoumání">
int[] palette(long seed) {
    var random = new Random(seed);
    int[] colours = new int[16];
    for (int i = 0; i < colours.length; i++) {
        float hue = random.nextInt(360) / 360.0F;
        colours[i] = Color.HSBtoRGB(hue, 0.8F, 1);
    }
    return colours;
}

BufferedImage render(long seed, double span) {
    if (!Double.isFinite(span) || span <= 1.0E-5 || span > 6 || !Double.isFinite(centerX + centerY)) {
        throw new IllegalArgumentException("Span must be 0.00001..6.");
    }
    var image = new BufferedImage(480, 360, BufferedImage.TYPE_INT_RGB);
    int[] colours = palette(seed);
    for (int y = 0; y < 360; y++) {
        for (int x = 0; x < 480; x++) {
            int n = escape(centerX + (x / 479.0 - 0.5) * span, centerY + (y / 359.0 - 0.5) * span * 0.75);
            image.setRGB(x, y, n == 160 ? 0 : colours[n % colours.length]);
        }
    }
    return image;
}

int escape(double x, double y) {
    double a = 0;
    double b = 0;
    int steps = 0;
    while (a * a + b * b <= 4 && steps < 160) {
        double next = a * a - b * b + x;
        b = 2 * a * b + y;
        a = next;
        steps++;
    }
    return steps;
}
double centerX = -0.6;
double centerY = 0;

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

java.util.List<Long> matchingSeeds(int[] sample) {
    if (sample.length < 1 || sample.length > 16) {
        throw new IllegalArgumentException("Supply 1..16 palette colours.");
    }
    var matches = new java.util.ArrayList<Long>();
    for (long seed = 0; seed <= 255; seed++) {
        int[] colours = palette(seed);
        boolean match = true;
        for (int i = 0; i < sample.length; i++) {
            if (sample[i] != colours[i]) {
                match = false;
                break;
            }
        }
        if (match) matches.add(seed);
    }
    return matches;
}

BufferedImage paletteStrip(int[] colours) {
    var image = new BufferedImage(colours.length * 30, 40, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) image.setRGB(x, y, colours[x / 30]);
    }
    return image;
}

void main() {
    int[] mystery = {-13420801, -13369474, -52415, -13369522, -13369416, -27085, -13369386, -6684877, -5112013, -52256, -13369355, -35789, -52405, -6684877, -13369372, -13409281};
    try {
        IO.println("Mystery palette: " + java.util.Arrays.toString(mystery));
        IO.println("Matching seeds 0..255: " + matchingSeeds(mystery));
        String seedText = IO.readln("Palette seed [42], /q to quit: ");
        if (seedText == null || seedText.strip().equalsIgnoreCase("/q")) return;
        String spanText = IO.readln("Horizontal span [3.2], /q to quit: ");
        if (spanText == null || spanText.strip().equalsIgnoreCase("/q")) return;
        long seed = seedText.isBlank() ? 42 : Long.parseLong(seedText.strip());
        double size = spanText.isBlank() ? 3.2 : Double.parseDouble(spanText.strip());
        showImages("Mandelbrot Mystery", new String[]{"Mystery palette", "Chosen seed " + seed},
                   new BufferedImage[]{paletteStrip(mystery), render(seed, size)});
    } catch (Exception failure) {
        IO.println("Cannot draw this experiment: " + failure.getMessage());
    }
}
// </editor-fold>
