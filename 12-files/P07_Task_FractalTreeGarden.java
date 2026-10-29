import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · FractalTreeGarden
/// Lekce: 12.2 Oznam neúspěšnou operaci — 02-exceptions.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Vypěstuj větvící se strom změnou jediného úhlu. Malé pootočení promění úzkou větvičku v rozložitou
/// korunu, kterou stojí za to uložit jako pohlednici.
///
/// **Tvoje mise:** Vytvoř readAngle(text): odstraň okolní mezery, proveď parsing (převod) na double,
/// přijmi finite (konečné) hodnoty 10–70 včetně, pro nečíselný text použij 28 a čitelná neplatná čísla
/// odmítni. Nedokončený parser (převodní postup) nyní ignoruje každý požadovaný tvar stromu.
///
/// Po spuštění programu napiš úhel 10–70 nebo zkus oops; přečti Chosen value a prohlédni Java
/// obrázek. Pro další vstup zavři okno a znovu spusť program.

double readAngle(String text) {
    try {
        double value = Double.parseDouble(text.strip());
        if (!Double.isFinite(value) || value < 10 || value > 70) throw new IllegalArgumentException("angle: 10..70.");
        return value;
    } catch (NumberFormatException invalid) {
        return 28;
    }
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
double shortening = 0.7;

int branches(Graphics2D g, double x, double y, double length, double heading, double fork, int depth) {
    double nx = x + Math.cos(heading) * length;
    double ny = y + Math.sin(heading) * length;
    g.drawLine((int)x, (int)y, (int)nx, (int)ny);
    if (depth == 0) {
        return 1;
    }
    return 1
        + branches(g, nx, ny, length * shortening, heading - fork, fork, depth - 1)
        + branches(g, nx, ny, length * shortening, heading + fork, fork, depth - 1);
}

BufferedImage render(long seed, double angle) {
    if (!Double.isFinite(angle) || angle < 10 || angle > 70 || shortening < 0.4 || shortening > 0.78) {
        throw new IllegalArgumentException("Angle 10..70; optional shortening 0.4..0.78.");
    }
    var image = new BufferedImage(600, 480, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(new Color(15, 24, 32));
        g.fillRect(0, 0, 600, 480);
        g.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.6F, 1));
        branches(g, 300, 470, 120, -Math.PI / 2, Math.toRadians(angle), 8);
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
    String input = IO.readln("Angle 10..70 (blank/default 28, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        var option = readAngle(input);
        IO.println("Chosen value: " + option);
        showImages("Fractal Tree Garden", new String[]{"Value " + option}, new java.awt.image.BufferedImage[]{render(42, option)});
    } catch (IllegalArgumentException failure) {
        IO.println("Invalid input: " + failure.getMessage());
    }
}
// </editor-fold>
