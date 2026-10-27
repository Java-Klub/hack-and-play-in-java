// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M01_HangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I021 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_Hangman();
        game.word = "BANANA";
        game.tried = "A";
        check(game.mask().equals("_A_A_A"), "the while loop must include the last letter");
        game.word = "A";
        game.tried = "";
        check(game.mask().equals("_"), "a one-letter word runs once");
        game.word = "";
        check(game.mask().isEmpty(), "an empty word runs zero times without indexing it");
        System.out.println("PASS " + checks + " checks");
    }
}
