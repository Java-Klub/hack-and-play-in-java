// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M05_ChompTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I009 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_Chomp();
        game.reset();
        check(game.bite(4, 5) && game.widths[3] == 4, "the last row must accept a legal remaining bite");
        int[] before = game.widths.clone();
        check(!game.bite(5, 1) && Arrays.equals(before, game.widths), "an invalid row must not mutate chocolate");
        check(!game.bite(4, 5), "already eaten chocolate must be rejected");
        game.reset();
        check(game.bite(1, 1) && game.winner == 2, "the legal poison cell still causes a loss");
        System.out.println("PASS " + checks + " checks");
    }
}
