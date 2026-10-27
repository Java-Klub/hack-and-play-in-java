// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M09_EvilHangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I022 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_EvilHangman();
        check(game.familySize(List.of("ALLY","COOL","GOOD")) == 3, "the family count must accumulate across all words");
        check(game.familySize(List.of()) == 0 && game.familySize(List.of("ALLY")) == 1, "zero and one member are counted correctly");
        game.reset();
        game.pool = new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA"));
        game.command("E");
        check(game.pool.size() == 3 && game.mask.equals("____"), "the actual adversary must keep the largest consistent family");
        System.out.println("PASS " + checks + " checks");
    }
}
