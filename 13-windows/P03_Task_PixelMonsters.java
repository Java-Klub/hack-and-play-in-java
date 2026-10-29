import java.awt.Color;
import java.awt.image.BufferedImage;

/// # Úkol · PixelMonsters
/// Lekce: 13.1 Kresli v okně Javy — 01-drawing.html
///
/// **Rozsviť příšeře oči.** Program už vytváří barevnou příšerku, ale obě její oči jsou černé.
///
/// **Úkol:** Na řádku s `eyeColour` níže vyber bílou barvu místo černé. Zachovej zbytek řádku
/// včetně `.getRGB();`; nekresli každé oko zvlášť. Dostupná jména barev jsou `Color.BLACK`,
/// `Color.WHITE`, `Color.RED` a `Color.YELLOW`.
///
/// **Výsledek:** V novém okně budou obě oči bílé a tělo zůstane stejné. Jedna změněná hodnota se
/// totiž používá pro obě oči.

int eyeColour = Color.BLACK.getRGB();

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Obrázek používá stále stejný seed (počáteční hodnotu) 42, aby šly oči před úpravou a po ní přímo
/// porovnat. Color poskytuje pojmenované barvy jako BLACK, WHITE, RED a YELLOW; getRGB() vrací
/// jejich číselný zápis. Upravuješ jedinou sdílenou barvu, nikoli dvě pozice očí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Změnou eyeColour na dvě dostupná jména Color dej stejné příšeře přátelský a výhružný výraz. Ponech
/// seed 42, aby tělo zůstalo srovnatelné. Než odhalíš změněnou barvu, nech kamaráda vybrat výraz.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
