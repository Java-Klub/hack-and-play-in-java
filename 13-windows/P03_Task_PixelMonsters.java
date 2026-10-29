import java.awt.Color;
import java.awt.image.BufferedImage;

/// # Task · PixelMonsters
/// Lesson: 13.1 Draw in a Java window — 01-drawing.html
///
/// **Light up the monster’s eyes.** The program already creates a colourful monster, but both
/// eyes are black.
///
/// **Your task:** On the `eyeColour` line below, select white instead of black. Keep the rest of
/// the line, including `.getRGB();`; do not draw each eye separately. Available colour names
/// include `Color.BLACK`, `Color.WHITE`, `Color.RED` and `Color.YELLOW`.
///
/// **Result:** A new window will show two white eyes on the same body. One changed value is used
/// for both eyes.

int eyeColour = Color.BLACK.getRGB();

// <editor-fold defaultstate="collapsed" desc="For this game">
/// The picture always uses seed 42, allowing a direct comparison of the eyes before and after an
/// edit. Color supplies named colours such as BLACK, WHITE, RED and YELLOW; getRGB() returns their
/// pixel encoding. You change one shared colour, not two eye positions.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Give the same monster a friendly and a threatening expression by changing eyeColour to two
/// available Color names. Keep seed 42 so the body stays comparable. Ask a friend to choose an
/// expression before you reveal which colour you changed.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
BufferedImage render(long seed) {
    var random = new Random(seed);
    int[][] cells = new int[16][16];
    int body = Color.HSBtoRGB(random.nextFloat(), 0.7f, 0.9f);
    for (int row = 2; row < 14; row++) {
        int width = 3 + random.nextInt(5);
        for (int col = 8 - width; col < 8; col++) {
            if (random.nextInt(5) != 0) {
                cells[row][col] = body;
                cells[row][15 - col] = body;
            }
        }
    }
    cells[5][5] = eyeColour;
    cells[5][10] = eyeColour;
    cells[6][5] = 0xFF111111;
    cells[6][10] = 0xFF111111;
    var image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    var drawing = image.createGraphics();
    try {
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                drawing.setColor(new Color(cells[row][col], true));
                drawing.fillRect(col * 16, row * 16, 16, 16);
            }
        }
    } finally {
        drawing.dispose();
    }
    return image;
}

// Prepared display of the picture calculated above.
void showPicture() {
    if (java.awt.GraphicsEnvironment.isHeadless()) {
        IO.println("Image preview needs a graphical display.");
        return;
    }
    var image = render(42);
    javax.swing.SwingUtilities.invokeLater(() -> {
        var frame = new javax.swing.JFrame("PixelMonsters");
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
