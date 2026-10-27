// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M05_WordSearchTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I028 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_WordSearch();
        check(game.read(0,0,0,2).equals("CAT"), "the counted loop must include the final selected cell");
        check(game.read(0,2,0,0).equals("TAC"), "the same range must work in reverse");
        check(game.read(2,0,2,4).equals("TIGER"), "five selected cells produce five letters");
        game.reset();
        game.command("1 1 1 3");
        check(game.found.contains("CAT"), "the actual word selection uses the complete loop result");
        System.out.println("PASS " + checks + " checks");
    }
}
