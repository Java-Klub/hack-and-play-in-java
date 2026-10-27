// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M06_HigherOrLowerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I032 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_HigherOrLower();
        game.reset();
        game.current = 2;
        game.deck = new ArrayDeque<>(List.of(3, 4, 5));
        game.command("h");
        check(game.outcome.isEmpty(), "one correct guess must not finish the ten-point round");
        check(game.target == 10, "one named target supplies the round goal");
        game.correct = 9;
        game.command("h");
        check(!game.outcome.isEmpty() && game.correct == 10, "the tenth correct guess ends the round");
        System.out.println("PASS " + checks + " checks");
    }
}
