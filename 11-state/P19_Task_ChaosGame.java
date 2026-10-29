import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · ChaosGame
/// Lekce: 11.4 Náhoda navrhuje, pravidlo rozhoduje — 04-random.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Opakovaně poskoč o polovinu vzdálenosti k náhodně vybranému rohu. Z velice malého náhodného pravidla
/// může vyrůst překvapivě uspořádaný fraktál.
///
/// **Tvoje mise:** Poslední platný vrchol se nikdy nevybere. Oprav rozmezí volby včetně jediného
/// dostupného vrcholu a chyby při nulovém počtu.
///
/// Po spuštění programu napiš seed (počáteční číslo) nebo stiskni Enter pro 42; prohlédni Java
/// obrázek, potom jej zavři a zopakuj stejný seed nebo porovnej 43.

int chooseVertex(Random random, int count) {
    if (count < 1) throw new IllegalArgumentException("No legal vertices.");
    return random.nextInt(count);
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vymysli nový zakázaný tah: po vrcholu nesmí následovat jeho soused po směru hodin. Před voláním
/// chooseVertex sestav seznam legálních kandidátů. Se stejným seed (počátečním stavem) porovnej nové
/// mezery s původním obrázkem.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] jump(double x, double y, double targetX, double targetY) {
    return new double[]{(x + targetX) / 2, (y + targetY) / 2};
}

BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 100 || count > 300000) {
        throw new IllegalArgumentException("Points: 100..300000.");
    }
    var image = new BufferedImage(500, 420, BufferedImage.TYPE_INT_RGB);
    var random = new Random(seed);
    double[][] vertices = {{250, 15}, {15, 405}, {485, 405}};
    double[] point = {250, 210};
    for (int i = 0; i < count + 20; i++) {
        int corner = chooseVertex(random, 3);
        point = jump(point[0], point[1], vertices[corner][0], vertices[corner][1]);
        if (i >= 20) {
            image.setRGB((int)point[0], (int)point[1], Color.HSBtoRGB(corner / 3.0F, 0.6F, 1));
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
    String input = IO.readln("Seed (blank 42, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        long seed = input.isBlank() ? 42 : Long.parseLong(input.strip());
        showImages("Chaos Game", new String[]{"Seed " + seed}, new java.awt.image.BufferedImage[]{render(seed, 50000)});
    } catch (IllegalArgumentException failure) {
        IO.println("Use a whole-number seed: " + failure.getMessage());
    }
}
// </editor-fold>
