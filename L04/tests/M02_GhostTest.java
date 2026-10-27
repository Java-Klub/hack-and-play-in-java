// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M02_GhostTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I025 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_Ghost();
        check(game.possiblePrefix("RO"), "the search must reach dictionary entries beyond the first word");
        check(game.possiblePrefix("GHO"), "the first matching word may return immediately");
        check(!game.possiblePrefix("XY"), "a missing prefix terminates with false");
        game.reset();
        game.command("R");
        check(game.outcome.isEmpty() && game.player == 2, "the actual legal R prefix must keep the game alive");
        game.words = new String[0];
        check(!game.possiblePrefix("A"), "an empty dictionary terminates without access");
        System.out.println("PASS " + checks + " checks");
    }
}
