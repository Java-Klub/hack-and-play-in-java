import java.awt.*;
import java.awt.image.BufferedImage;

/// # Solved task · ImageToASCII
/// Lesson: 12.2 Report a failed operation — 02-exceptions.html
///
/// Original assignment (this version is complete):
///
/// Turn a picture into a wall of characters. Change its text width to discover how much detail survives
/// when pixels become dots, colons and other marks.
///
/// **Your mission:** A mistyped width shrinks the result to four characters rather than using the
/// advertised width 80. Repair that fallback without accepting numbers outside 4–160.

int readWidth(String text) {
    try {
        int value = Integer.parseInt(text.strip());
        if (value < 4 || value > 160) throw new IllegalArgumentException("width: 4..160.");
        return value;
    } catch (NumberFormatException invalid) {
        return 80;
    }
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Conversion failure is not a bad numeric width
///
/// Trace `" 12 "`: strip removes outer spaces, parseInt produces 12, the guard passes and return
/// produces 12. `"twelve"` fails inside parseInt, so the narrow catch chooses a fallback. `"200"`
/// parses but the explicit range guard throws IllegalArgumentException; the NumberFormatException
/// catch does not intercept that different failure. Predict the paths for `"4"`, `"160"` and `"3"`.
/// Keep both inclusive limits and compare the typo path with the width promised by the prompt.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Invent your own character ramp from dark to light, including a space. Compare a face made of
/// punctuation with one made of letters at the same text width.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
