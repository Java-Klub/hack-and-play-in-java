// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M07_KochSnowflakeCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I107 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_KochSnowflakeCards();
        var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
        var pen = image.createGraphics();
        check(game.snowflake(pen, 0) == 3, "the counted loop must draw all three base sides");
        boolean firstSide = false;
        for (int y = 215; y <= 221; y++) {
            for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
        }
        check(firstSide, "the actual first edge must be painted, not merely counted");
        check(game.snowflake(pen, 1) == 12, "three supplied four-segment edges give twelve segments");
        pen.dispose();
        System.out.println("PASS " + checks + " checks");
    }
}
