import java.awt.*;
import java.awt.image.BufferedImage;

/// # Úkol · ImageToASCII
/// Lekce: 12.2 Oznam neúspěšnou operaci — 02-exceptions.html
///
/// Proměň obrázek ve stěnu znaků. Měň šířku textu a zjisti, kolik detailů přežije, když se obrazové body
/// změní v tečky, dvojtečky a další značky.
///
/// **Tvoje mise:** Překlep v šířce smrskne výsledek na čtyři znaky místo slíbené šířky 80. Oprav náhradní
/// hodnotu a nepřijímej čísla mimo 4–160.
///
/// Po spuštění programu napiš šířku od 4 do 160 nebo zkus oops; prohlédni označený ASCII obrázek
/// přímo v terminálu a dalším spuštěním programu porovnej jinou šířku.

int readWidth(String text) {
    try {
        int value = Integer.parseInt(text.strip());
        if (value < 4 || value > 160) throw new IllegalArgumentException("width: 4..160.");
        return value;
    } catch (NumberFormatException invalid) {
        return 4;
    }
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Neúspěšný převod není totéž co chybná číselná šířka
///
/// Trasuj `" 12 "`: strip odstraní krajní mezery, parseInt vytvoří 12, guard (kontrola) projde a
/// return vrátí 12. `"twelve"` selže uvnitř parseInt, takže konkrétní catch vybere náhradní hodnotu.
/// `"200"` se převede, ale výslovná kontrola rozsahu vyvolá IllegalArgumentException; catch pro
/// NumberFormatException tuto jinou chybu nezachytí. Předpověz cesty pro `"4"`, `"160"` a `"3"`.
/// Zachovej přijetí obou krajních hodnot a porovnej cestu překlepu se šířkou slíbenou v prompt (výzvě
/// ke vstupu).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
BufferedImage demo() {
    var image = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 160; y++) {
        for (int x = 0; x < 240; x++) {
            image.setRGB(x, y, Color.HSBtoRGB(x / 240.0F, 0.7F, 0.2F + y / 200.0F));
        }
    }
    return image;
}

String convert(BufferedImage image, int width) {
    if (width < 4 || width > 160) {
        throw new IllegalArgumentException("Width: 4..160.");
    }
    int rows = Math.max(1, width * image.getHeight() / image.getWidth() / 2);
    String ramp = " .:-=+*#%@";
    var result = new StringBuilder();
    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < width; col++) {
            int rgb = image.getRGB(col * image.getWidth() / width, row * image.getHeight() / rows);
            int light = (((rgb >> 16) & 255) * 299 + ((rgb >> 8) & 255) * 587 + (rgb & 255) * 114) / 1000;
            result.append(ramp.charAt(light * (ramp.length() - 1) / 255));
        }
        result.append('\n');
    }
    return result.toString();
}

void main() {
    String input = IO.readln("Width 4..160 (blank/default 80, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    try {
        int width = readWidth(input);
        IO.println("ASCII picture, width " + width + ":");
        IO.print(convert(demo(), width));
    } catch (IllegalArgumentException failure) {
        IO.println("Invalid width: " + failure.getMessage());
    }
}
// </editor-fold>
