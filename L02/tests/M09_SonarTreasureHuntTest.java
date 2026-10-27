// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M09_SonarTreasureHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I042 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_SonarTreasureHunt();
        check(!game.insideSea(8, 7), "row index 8 is outside the eight-row sea");
        check(!game.insideSea(7, 8), "column index 8 is outside the sea");
        check(game.insideSea(0, 0) && game.insideSea(7, 7), "both valid corners are included");
        check(!game.insideSea(-1, 0) && !game.insideSea(0, -1), "negative indices are excluded");
        System.out.println("PASS " + checks + " checks");
    }
}
