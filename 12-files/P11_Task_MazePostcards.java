import java.awt.*;
import java.awt.image.BufferedImage;

/// # Vyřešený úkol · MazePostcards
/// Lekce: 12.3 Nech hráče opravit odpověď — 03-retry.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Vytvoř pohlednici s bludištěm, jehož rozložení mění seed (počáteční hodnota). Šířka určuje, kolik
/// klikatých chodeb se na stránku vejde.
///
/// **Tvoje mise:** Vytvoř readColumns(text): přijmi integer (celá čísla) 2–50 včetně a po odstranění
/// okolních mezer aliases (zkratky) tiny=6 a wide=40 bez ohledu na velikost písmen. Nečíselný text
/// dostane 20; čitelné číslo mimo bounds (hranice) odmítni. Nedokončený výběr nyní ignoruje požadovanou
/// velikost i alias.
///
/// Spusť program a napiš tiny, wide nebo číslo 2–50; oops vyzkouší náhradní hodnotu. Přečti Chosen
/// value, prohlédni obrázek a po zavření porovnej další vstup.

int readColumns(String text) {
    String word = text.strip();
    if (word.equalsIgnoreCase("tiny")) return 6;
    if (word.equalsIgnoreCase("wide")) return 40;
    try {
        int columns = Integer.parseInt(word);
        if (columns < 2 || columns > 50) throw new IllegalArgumentException("Columns: 2..50.");
        return columns;
    } catch (NumberFormatException invalid) {
        return 20;
    }
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[][] generate(long seed, int columns, int rows) {
    if (columns < 2 || columns > 50 || rows < 2 || rows > 40) {
        throw new IllegalArgumentException("Maze dimensions: 2..50 by 2..40.");
    }
    boolean[][] open = new boolean[rows * 2 + 1][columns * 2 + 1];
    var random = new Random(seed);
    var stack = new ArrayDeque<Point>();
    stack.push(new Point(1, 1));
    open[1][1] = true;
    int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
    while (!stack.isEmpty()) {
        Point cell = stack.peek();
        var choices = new ArrayList<Point>();
        for (var offset : offsets) {
            int x = cell.x + offset[0];
            int y = cell.y + offset[1];
            if (x > 0 && x < columns * 2 && y > 0 && y < rows * 2 && !open[y][x]) {
                choices.add(new Point(x, y));
            }
        }
        if (choices.isEmpty()) {
            stack.pop();
        } else {
            Point next = choices.get(random.nextInt(choices.size()));
            open[(cell.y + next.y) / 2][(cell.x + next.x) / 2] = true;
            open[next.y][next.x] = true;
            stack.push(next);
        }
    }
    open[1][0] = true;
    open[rows * 2 - 1][columns * 2] = true;
    return open;
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite dimensions required.");
    }
    var open = generate(seed, (int)option, 15);
    var image = new BufferedImage(open[0].length * 12, open.length * 12, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        g.setColor(Color.WHITE);
        for (int y = 0; y < open.length; y++) {
            for (int x = 0; x < open[y].length; x++) {
                if (open[y][x]) {
                    g.fillRect(x * 12, y * 12, 12, 12);
                }
            }
        }
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
    String input = IO.readln("Columns 2..50 (blank/default 20, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        var option = readColumns(input);
        IO.println("Chosen value: " + option);
        showImages("Maze Postcards", new String[]{"Value " + option}, new java.awt.image.BufferedImage[]{render(42, option)});
    } catch (IllegalArgumentException failure) {
        IO.println("Invalid input: " + failure.getMessage());
    }
}
// </editor-fold>
