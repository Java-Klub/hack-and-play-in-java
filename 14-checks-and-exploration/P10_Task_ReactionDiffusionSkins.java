import java.awt.*;
import java.awt.image.BufferedImage;

/// # Solved task · ReactionDiffusionSkins
/// Lesson: 14.2 Compose objects by their behaviour — 02-interfaces.html
///
/// Original assignment (this version is complete):
///
/// The creature-skin simulation already runs. Become its experiment designer: change one local recipe
/// at a time and test whether your explanation matches the pattern you actually obtain.
///
/// **Your mission:** Build a fair three-skin experiment: use the same seed and step count for
/// reaction recipes a×b², half that reaction, and zero reaction. Swap only the selected chemistry
/// role; reuse the same diffusion and rendering. Return the three pictures in that order and restore
/// the caller’s original role afterwards, including when a run fails.
///
/// The window compares Standard, Half reaction and No reaction from the same starting state. In the
/// starter they are copies of the same recipe. After your experiment tool is connected, compare all
/// three without changing the seed.

// <editor-fold defaultstate="collapsed" desc="Your next version · optional">
/// Invent a fourth recipe after the three controlled comparisons. Change only the local reaction and
/// give the observed pattern a creature name. Keep the same seed and duration so another pupil can
/// reproduce your experiment.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Program to explore">
Chemistry makeChemistry() {
    return (a, b) -> {
        double activated = a * b;
        return activated * b;
    };
}

Chemistry chemistry = makeChemistry();

double reactionAt(int x, int y) {
    return chemistry.reaction(a[y][x], b[y][x]);
}

final int SIZE = 64;
double[][] a;
double[][] b;

void reset(long seed) {
    var random = new Random(seed);
    a = new double[SIZE][SIZE];
    b = new double[SIZE][SIZE];
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            a[y][x] = 1;
            if (x > 23 && x < 40 && y > 23 && y < 40) {
                a[y][x] = 0.5;
                b[y][x] = 0.8 + random.nextDouble() * 0.2;
            }
        }
    }
}

double laplace(double[][] field, int x, int y) {
    double sum = -field[y][x];
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) {
                continue;
            }
            double weight = dx == 0 || dy == 0 ? 0.2 : 0.05;
            sum += field[(y + dy + SIZE) % SIZE][(x + dx + SIZE) % SIZE] * weight;
        }
    }
    return sum;
}

void step() {
    double[][] nextA = new double[SIZE][SIZE];
    double[][] nextB = new double[SIZE][SIZE];
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            double reaction = reactionAt(x, y);
            nextA[y][x] = Math.clamp(a[y][x] + laplace(a, x, y) - reaction + 0.055 * (1 - a[y][x]), 0, 1);
            nextB[y][x] = Math.clamp(b[y][x] + 0.5 * laplace(b, x, y) + reaction - 0.117 * b[y][x], 0, 1);
        }
    }
    a = nextA;
    b = nextB;
}

BufferedImage render(long seed, double option) {
    int count = (int)option;
    if (!Double.isFinite(option) || count < 1 || count > 3000) {
        throw new IllegalArgumentException("Steps: 1..3000.");
    }
    reset(seed);
    for (int i = 0; i < count; i++) {
        step();
    }
    var image = new BufferedImage(SIZE * 6, SIZE * 6, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            float value = (float)Math.clamp((a[y / 6][x / 6] - b[y / 6][x / 6]), 0, 1);
            image.setRGB(x, y, Color.HSBtoRGB(0.6F + value * 0.3F, 0.65F, value));
        }
    }
    return image;
}

interface Chemistry { double reaction(double a, double b); }

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

BufferedImage[] compareSkins(long seed, int steps) {
    Chemistry before = chemistry;
    try {
        chemistry = makeChemistry();
        BufferedImage standard = render(seed, steps);
        chemistry = (a, b) -> 0.5 * a * b * b;
        BufferedImage half = render(seed, steps);
        chemistry = (a, b) -> 0;
        BufferedImage none = render(seed, steps);
        return new BufferedImage[]{standard, half, none};
    } finally {
        chemistry = before;
    }
}

void main() {
    try {
        showImages("Skin Experiments", new String[]{"Standard", "Half reaction", "No reaction"}, compareSkins(42, 1200));
    } catch (Exception failure) {
        IO.println("Cannot run the experiment: " + failure.getMessage());
    }
}
// </editor-fold>
