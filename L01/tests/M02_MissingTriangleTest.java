// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M02_MissingTriangleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I099 F01: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_MissingTriangle();
        game.restart();
        var image = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
        var pen = image.createGraphics();
        game.paintDisc(pen, 0);
        pen.dispose();
        check(image.getRGB(300, 100) == Color.WHITE.getRGB(), "the top disc must keep its white bite");
        check(image.getRGB(300, 30) == Color.BLACK.getRGB(), "the uncut part remains black");
        System.out.println("PASS " + checks + " checks");
    }
}
