// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M01_CafeWallLabTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I097 F01: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_CafeWallLab();
        game.restart();
        var image = new BufferedImage(640, 450, BufferedImage.TYPE_INT_RGB);
        var pen = image.createGraphics();
        game.paintWall(pen);
        pen.dispose();
        check(image.getRGB(100, 50) != new Color(145, 145, 145).getRGB(), "wall tiles must remain visible above the background");
        check(image.getRGB(5, 420) == new Color(145, 145, 145).getRGB(), "the unused border keeps its background");
        System.out.println("PASS " + checks + " checks");
    }
}
