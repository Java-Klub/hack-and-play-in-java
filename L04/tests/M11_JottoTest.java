// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M11_JottoTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I024 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M11_Jotto();
        game.secret = "PLANT";
        check(game.matches("PLANT") == 5, "all five shared letters must accumulate");
        check(game.matches("STONE") == 2, "T and N are counted regardless of positions");
        check(game.matches("BRICK") == 0, "no shared letters leaves zero");
        game.reset();
        game.secret = "PLANT";
        game.command("STONE");
        check(game.history.getFirst().contains("2 shared"), "the actual clue uses the repaired total");
        System.out.println("PASS " + checks + " checks");
    }
}
