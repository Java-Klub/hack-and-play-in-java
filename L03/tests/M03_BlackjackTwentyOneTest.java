// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M03_BlackjackTwentyOneTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I031 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_BlackjackTwentyOne();
        check(game.compareTotals(20, 19).equals("You win"), "a higher unbusted player total must win");
        check(game.compareTotals(21, 21).equals("Draw"), "equal totals must draw");
        check(game.compareTotals(18, 22).equals("You win"), "a busted dealer loses");
        check(game.compareTotals(22, 23).equals("You bust"), "the player bust guard has priority");
        check(game.compareTotals(18, 20).equals("Dealer wins"), "the larger legal dealer total wins");
        game.hand = List.of(10, 10);
        game.dealer = new ArrayList<>(List.of(10, 9));
        game.finish();
        check(game.outcome.equals("You win"), "finish uses the repaired comparison rule");
        System.out.println("PASS " + checks + " checks");
    }
}
