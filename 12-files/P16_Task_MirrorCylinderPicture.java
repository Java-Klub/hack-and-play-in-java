import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · MirrorCylinderPicture
/// Lekce: 12.4 Přečti data ze skutečného souboru — 04-files.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Proměň slovo ze svého textového souboru ve vějíř zahnutých pásů jako přípravu pro válcové zrcadlo.
/// Zkreslení prozkoumej přímo v obrázku v Javě a při dalším spuštění zkus jiné slovo.
///
/// **Tvoje mise:** Vytvoř readCaption: otevři vybraný UTF-8 text pomocí newBufferedReader, přečti
/// první řádek, odstraň okolní whitespace (bílé znaky) a přijmi 1..8 znaků. Prázdný, bílý nebo příliš
/// dlouhý první řádek ohlas jako IOException. Kreslené slovo musí pocházet ze souboru.
///
/// První spuštění vytvoří data/mirror-word.txt s JAVA. Změň první řádek na MOON a spusť znovu:
/// obrázek v Javě se musí změnit. Použij nejvýše osm znaků, aby se slovo vešlo do obrázku.

String readCaption(Path path) throws IOException {
    try (var reader = Files.newBufferedReader(path)) {
        String line = reader.readLine();
        if (line == null) throw new IOException("The first line is empty.");
        String text = line.strip();
        if (text.isEmpty() || text.length() > 8) {
            throw new IOException("Use 1..8 characters on the first line.");
        }
        return text;
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř sbírku slovních obrazů úpravou prvního řádku data/mirror-word.txt mezi spuštěními. Zkus
/// MOON, krátké jméno a interpunkční znak. Pro každý návrh si nech textový soubor; porovnej deformaci
/// úzkého a širokého slova kolem stejného kruhu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String caption = "JAVA";

double[] ground(double angle, double height) {
    double x = 60 * Math.cos(angle);
    double y = 60 * Math.sin(angle);
    double dx = x;
    double dy = y + 600;
    double dz = height - 300;
    double dot = dx * Math.cos(angle) + dy * Math.sin(angle);
    dx -= 2 * dot * Math.cos(angle);
    dy -= 2 * dot * Math.sin(angle);
    double distance = -height / dz;
    return new double[]{x + distance * dx, y + distance * dy};
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option) || option < 30 || option > 140) {
        throw new IllegalArgumentException("Maximum mirror height: 30..140.");
    }
    var source = new BufferedImage(500, 160, BufferedImage.TYPE_INT_RGB);
    var ink = source.createGraphics();
    ink.setColor(Color.WHITE);
    ink.fillRect(0, 0, 500, 160);
    ink.setColor(Color.getHSBColor(new Random(seed).nextFloat(), 0.9F, 0.7F));
    ink.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.min(100, 440 / Math.max(1, caption.length()))));
    ink.drawString(caption, 30, 120);
    ink.dispose();
    var image = new BufferedImage(900, 800, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 900, 800);
        for (int v = 0; v < 160; v++) {
            for (int u = 0; u < 500; u++) {
                double angle = -Math.PI / 2 + (u / 499.0 - 0.5) * 1.4;
                var point = ground(angle, 5 + option * (1 - v / 159.0));
                g.setColor(new Color(source.getRGB(u, v)));
                g.fillRect(450 + (int)point[0], 700 + (int)point[1], 4, 4);
            }
        }
        g.setColor(Color.BLACK);
        g.drawOval(390, 640, 120, 120);
        g.drawString("Cylinder radius 60. View from (0,-600,300), relative to circle centre.", 15, 785);
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
    Path input = Path.of("data", "mirror-word.txt");
    try {
        if (!Files.exists(input)) {
            Files.createDirectories(input.toAbsolutePath().getParent());
            Files.writeString(input, "JAVA\n");
        }
        caption = readCaption(input);
        IO.println("Read " + caption + " from " + input.toAbsolutePath());
        IO.println("Edit the first line of that text file and run again to author another picture.");
        showImages("Mirror Cylinder Picture", new String[]{caption}, new BufferedImage[]{render(42, 125)});
    } catch (Exception failure) {
        IO.println("Cannot read the word artwork: " + failure.getMessage());
    }
}
// </editor-fold>
