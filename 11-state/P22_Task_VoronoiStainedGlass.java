import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/// # Vyřešený úkol · VoronoiStainedGlass
/// Lekce: 11.5 Stejný začátek, stejný pokus — 05-seeds.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Rozhoď barevná semínka a nech každé místo obrázku připojit k nejbližšímu. Různá semínka promění
/// stejnou plochu v nové vitráže.
///
/// **Tvoje mise:** Vytvoř seedList(random,count): přijmi count 0–100, jiné počty odmítni a vrať mutable
/// List (měnitelný seznam) přesně tolika postupných výsledků nextSeed(random). Zachovej pořadí vzniku.
/// Prázdný builder (sestavovací postup) nyní nechává vitráž bez barevných seed (zárodků).
///
/// Spusť program: prázdný seed builder (postup sestavení zárodků) nevytvoří vitráž. Po dokončení
/// seedList stejný program otevře barevný obrázek.

List<Seed> seedList(Random random, int count) {
    if (count < 0 || count > 100) throw new IllegalArgumentException("Seeds: 0..100.");
    var seeds = new ArrayList<Seed>();
    for (int i = 0; i < count; i++) seeds.add(nextSeed(random));
    return seeds;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
Seed nextSeed(Random random) {
    return new Seed(random.nextInt(400), random.nextInt(300), Color.HSBtoRGB(random.nextFloat(), 0.65F, 1));
}

record Seed(int x, int y, int colour) {}

int nearest(List<Seed> seeds, int x, int y) {
    int winner = 0;
    long distance = Long.MAX_VALUE;
    for (int i = 0; i < seeds.size(); i++) {
        long dx = x - seeds.get(i).x;
        long dy = y - seeds.get(i).y;
        long candidate = dx * dx + dy * dy;
        if (candidate < distance) {
            distance = candidate;
            winner = i;
        }
    }
    return winner;
}

BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 2 || count > 100) {
        throw new IllegalArgumentException("Seeds: 2..100.");
    }
    var random = new Random(seed);
    var seeds = seedList(random, count);
    var image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
    int[][] cells = new int[300][400];
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            cells[y][x] = nearest(seeds, x, y);
        }
    }
    for (int y = 0; y < 300; y++) {
        for (int x = 0; x < 400; x++) {
            boolean border = x > 0 && cells[y][x] != cells[y][x - 1] || y > 0 && cells[y][x] != cells[y - 1][x];
            image.setRGB(x, y, border ? 0 : seeds.get(cells[y][x]).colour);
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
    try {
        showImages("Voronoi Stained Glass", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 20)});
    } catch (Exception failure) {
        IO.println("Cannot build the picture. Check seedList; Save the source, run its check command, then play again.");
    }
}
// </editor-fold>
