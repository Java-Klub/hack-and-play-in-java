import java.awt.*;
import java.awt.image.BufferedImage;

/// # Úkol · MountainPostcards
/// Lekce: 14.4 Vyčleň jednu společnou myšlenku — 04-refactoring.html
///
/// Vytvoř horskou siluetu a porovnej ji se zrcadleným dvojčetem. Zrcadlení má změnit levé a pravé
/// vrcholy, ne vymyslet jinou horu.
///
/// **Tvoje mise:** Proveď refactoring (změnu stavby) fungujícího obrázku. render nyní obrací hřeben
/// přímo uvnitř; tuto odpovědnost vyjmi do reverseRidge a v render nahraď blok voláním své metody.
/// Zachovej původní neobrácenou variantu, neměň vstup a zachovej chování obrázku.
///
/// Nejdřív prohlédni New rule i Original rule. Po vyjmutí pomocné metody zopakuj stejný seed: oba
/// pohledy mají vypadat stejně jako před změnou. Kontrola helper začíná RED, přestože inline obrázek
/// už funguje.

double[] reverseRidge(double[] heights) {
    // Do této metody vyjmi odpovědnost za obracení z render.
    return heights.clone();
}

BufferedImage render(long seed, double roughness) {
    if (!Double.isFinite(roughness) || roughness < 0.2 || roughness > 0.9) {
        throw new IllegalArgumentException("Roughness: 0.2..0.9.");
    }
    var image = new BufferedImage(640, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    var random = new Random(seed);
    try {
        for (int y = 0; y < 400; y++) {
            g.setColor(new Color(255 - y / 4, 120 - y / 6, 120 + y / 4));
            g.drawLine(0, y, 640, y);
        }
        for (int layer = 0; layer < 5; layer++) {
            var heights = ridge(random, roughness);
            if (mirrored) {
                double[] reversed = new double[heights.length];
                for (int i = 0; i < heights.length; i++) {
                    reversed[i] = heights[heights.length - 1 - i];
                }
                heights = reversed;
            }
            var polygon = new Polygon();
            polygon.addPoint(0, 400);
            for (int x = 0; x < 257; x++) {
                polygon.addPoint(x * 640 / 256, Math.max(20, Math.min(390, 170 + layer * 38 + (int)heights[x])));
            }
            polygon.addPoint(640, 400);
            g.setColor(new Color(100 - layer * 18, 95 - layer * 15, 135 - layer * 20));
            g.fillPolygon(polygon);
        }
    } finally {
        g.dispose();
    }
    return image;
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Navrhni horské logo spojením původního hřebene a jeho obrácené kopie v jednom obrázku, každého
/// jinou barvou. Rozhodni, zda se potkají uprostřed, nebo překryjí. Generování stále musí dostávat
/// nezměněná původní data hřebene.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double[] ridge(Random random, double roughness) {
    double[] heights = new double[257];
    heights[0] = random.nextDouble() * 40;
    heights[256] = random.nextDouble() * 40;
    double amplitude = 90;
    for (int gap = 256; gap > 1; gap /= 2) {
        for (int left = 0; left < 256; left += gap) {
            heights[left + gap / 2] = (heights[left] + heights[left + gap]) / 2 + (random.nextDouble() * 2 - 1) * amplitude;
        }
        amplitude *= roughness;
    }
    return heights;
}

boolean mirrored = true;

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
    mirrored = true;
    var changed = render(42, 0.65);
    mirrored = false;
    var original = render(42, 0.65);
    mirrored = true;
    showImages("Mountain Postcards", new String[]{"New rule", "Original rule"}, new java.awt.image.BufferedImage[]{changed, original});
}
// </editor-fold>
