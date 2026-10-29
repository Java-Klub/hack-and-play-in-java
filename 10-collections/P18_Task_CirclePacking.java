import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/// # Úkol · CirclePacking
/// Lekce: 10.2 Pamatuj si první návštěvu — 02-sets.html
///
/// Nech kruhy vtěsnat se do mezer mezi dřívějšími kruhy. Rostoucí shluk má používat nové středy místo
/// opakovaných pokusů na stejném místě.
///
/// **Tvoje mise:** Nové středy se odmítají, protože se method (metoda) ptá, zda už byly viděny. Oprav
/// přijetí středu tak, aby uspěl právě první pokus.
///
/// Spusť program a porovnej obrázek před opravou a po ní.

boolean claimCentre(Set<Centre> seen, int x, int y) {
    return seen.contains(new Centre(x, y));
}

// <editor-fold defaultstate="collapsed" desc="Tvoje další verze · volitelné">
/// ### Udělej z toho vlastní věc
///
/// Vytvoř tečkované souhvězdí: přijímej středy jen uvnitř jednoduché siluety, například dvou
/// překrývajících se kruhů. Metodě claimCentre nech jedinečnost, nikoli siluetu. Porovnej prázdná a
/// zaplněná místa svého plakátu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
record Bubble(double x, double y, double radius) {}

List<Bubble> pack(long seed, int count) {
    if (count < 1 || count > 200) {
        throw new IllegalArgumentException("Bubbles: 1..200.");
    }
    var random = new Random(seed);
    var bubbles = new ArrayList<Bubble>();
    Set<Centre> seen = new HashSet<>();
    for (int attempt = 0; attempt < 10000 && bubbles.size() < count; attempt++) {
        var candidate = new Bubble(2 + random.nextInt(396), 2 + random.nextInt(396), 1);
        if (!claimCentre(seen, (int)candidate.x(), (int)candidate.y())) continue;
        boolean free = true;
        for (var other : bubbles) {
            free &= Math.hypot(candidate.x - other.x, candidate.y - other.y) >= 3;
        }
        if (free) {
            bubbles.add(candidate);
        }
    }
    boolean[] stopped = new boolean[bubbles.size()];
    for (int step = 0; step < 400; step++) {
        for (int i = 0; i < bubbles.size(); i++) {
            if (stopped[i]) {
                continue;
            }
            var b = bubbles.get(i);
            double radius = b.radius + 0.5;
            boolean free = b.x >= radius && b.y >= radius && b.x + radius < 400 && b.y + radius < 400;
            for (int j = 0; j < bubbles.size() && free; j++) {
                var other = bubbles.get(j);
                if (i != j && Math.hypot(b.x - other.x, b.y - other.y) < radius + other.radius) {
                    free = false;
                }
            }
            if (free) {
                bubbles.set(i, new Bubble(b.x, b.y, radius));
            } else {
                stopped[i] = true;
            }
        }
    }
    return bubbles;
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite bubble count required.");
    }
    var bubbles = pack(seed, (int)option);
    var image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int i = 0; i < bubbles.size(); i++) {
            var b = bubbles.get(i);
            g.setColor(Color.getHSBColor(i * 0.618F, 0.6F, 0.9F));
            g.fillOval((int)(b.x - b.radius), (int)(b.y - b.radius), (int)(b.radius * 2), (int)(b.radius * 2));
        }
    } finally {
        g.dispose();
    }
    return image;
}

record Centre(int x, int y) {}

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
        showImages("Circle Packing", new String[]{"Result"}, new java.awt.image.BufferedImage[]{render(42, 80)});
    } catch (Exception failure) {
        IO.println("Cannot draw this result; check the editable rule: " + failure);
    }
}
// </editor-fold>
