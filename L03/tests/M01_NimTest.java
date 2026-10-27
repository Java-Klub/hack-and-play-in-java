// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M01_NimTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I001 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_Nim();
        game.stones = 7;
        check(game.botMove() == 3, "seven stones need the nonzero remainder branch");
        game.stones = 8;
        check(game.botMove() == 1, "a multiple of four still needs a legal fallback move");
        game.stones = 1;
        check(game.botMove() == 1, "one remaining stone is taken");
        game.stones = 0;
        check(game.botMove() == 0, "an empty pile has no bot move");
        System.out.println("PASS " + checks + " checks");
    }
}
