import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · SunflowerSpiral
/// Lekce: 2.3 Nech program počítat — 03-calculations.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// **Vypěstuj spirálu ze dvou paprsků.** Program sází barevná semínka jedno po druhém. Teď mezi
/// nimi otočí směr vždy o půl otáčky, takže leží ve dvou protilehlých řadách.
///
/// **Úkol:** Najdi nastavení úhlu mezi sousedními semínky a změň hodnotu použitou při běžném
/// spuštění na `137.5` stupně. Použij desetinnou tečku. Připravený výpočet souřadnic zachovej.
///
/// **Výsledek:** Nově spuštěný obrázek rozprostře semínka do spirálovitého květu. Když nastavení
/// najdeš, sleduj také, jak se jeho hodnota dostane ke kreslení jednoho semínka.

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Zkus tři blízké hodnoty a kamarádovi dej číselný recept bez obrázku. Nech jej předpovědět paprsky
/// nebo spirálu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program · prozkoumej podle potřeby">
double defaultAngle = 137.5;

double[] position(int index, double degrees) {
    double angle = Math.toRadians(degrees) * index;
    double radius = 7 * Math.sqrt(index);
    return new double[]{250 + radius * Math.cos(angle), 250 + radius * Math.sin(angle)};
}

BufferedImage render(long seed, double degrees) {
    if (!Double.isFinite(degrees) || degrees < 1 || degrees > 360) {
        throw new IllegalArgumentException("Angle: 1..360 degrees.");
    }
    var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        float hue = new Random(seed).nextFloat();
        for (int i = 0; i < 1000; i++) {
            var point = position(i, degrees);
            g.setColor(Color.getHSBColor(hue + i / 1000.0F, 0.65F, 1));
            g.fillOval((int)point[0] - 3, (int)point[1] - 3, 6, 6);
        }
    } finally {
        g.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42, defaultAngle);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("SunflowerSpiral");
        var picture = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
        picture.setOpaque(true);
        picture.setBackground(new java.awt.Color(35, 39, 48));
        frame.add(picture);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    });
}

void main() {
    showPicture();
}
// </editor-fold>
