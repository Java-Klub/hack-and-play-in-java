import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/// # Vyřešený úkol · DragonCurve
/// Lekce: 10.1 Batoh nemusí znát délku předem — 01-lists.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Nech z posloupnosti levých a pravých odbočení vyrůst papírového draka. Opakování malého
/// skládacího receptu vytváří překvapivě složitou křivku, kterou program zobrazí jako barevný
/// obrázek.
///
/// **Tvoje mise:** Křivka dostane o jeden přehyb méně, než bylo požadováno. Oprav pouze vnější smyčku ve
/// foldTurns tak, aby nula přehybů neudělala nic a kladný požadavek provedl právě tolik průchodů
/// skládání.
///
/// Spusť program: obrázek se ihned objeví v Java okně.

List<Integer> foldTurns(int folds) {
    var turns = new ArrayList<Integer>();
    for (int fold = 0; fold < folds; fold++) {
        int old = turns.size();
        turns.add(1);
        for (int i = old - 1; i >= 0; i--) {
            turns.add(-turns.get(i));
        }
    }
    return turns;
}

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<Point> path(int folds) {
    if (folds < 0 || folds > 15) {
        throw new IllegalArgumentException("Folds: 0..15.");
    }
    var turns = foldTurns(folds);
    var points = new ArrayList<Point>();
    int x = 0;
    int y = 0;
    int direction = 0;
    int[] dx = {1, 0, -1, 0};
    int[] dy = {0, 1, 0, -1};
    points.add(new Point(x, y));
    x++;
    points.add(new Point(x, y));
    for (int turn : turns) {
        direction = Math.floorMod(direction + turn, 4);
        x += dx[direction];
        y += dy[direction];
        points.add(new Point(x, y));
    }
    return points;
}

BufferedImage render(long seed, double option) {
    if (!Double.isFinite(option)) {
        throw new IllegalArgumentException("Finite fold count required.");
    }
    var points = path((int)option);
    int minX = points.stream().mapToInt((p)->p.x).min().orElseThrow();
    int minY = points.stream().mapToInt((p)->p.y).min().orElseThrow();
    int maxX = points.stream().mapToInt((p)->p.x).max().orElseThrow();
    int maxY = points.stream().mapToInt((p)->p.y).max().orElseThrow();
    double scale = 450.0 / Math.max(1, Math.max(maxX - minX, maxY - minY));
    var image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
    var g = image.createGraphics();
    try {
        for (int i = 1; i < points.size(); i++) {
            g.setColor(Color.getHSBColor((seed % 100) / 100.0F + i / (float)points.size(), 0.7F, 1));
            Point a = points.get(i - 1);
            Point b = points.get(i);
            g.drawLine(25 + (int)((a.x - minX) * scale), 25 + (int)((a.y - minY) * scale),
                25 + (int)((b.x - minX) * scale), 25 + (int)((b.y - minY) * scale));
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
    var image = render(42, 11);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("DragonCurve");
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
