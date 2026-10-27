// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M05_AnagramRoundsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I029 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_AnagramRounds();
        check(game.normalize("  silent  ").equals("SILENT"), "a whole lower-case answer must become the accepted upper-case word");
        check(game.normalize("LiStEn").equals("LISTEN"), "mixed case is normalized");
        game.reset();
        game.command(" silent ");
        check(game.score == 1 && game.round == 1, "the real anagram round accepts the normalized answer");
        System.out.println("PASS " + checks + " checks");
    }
}
