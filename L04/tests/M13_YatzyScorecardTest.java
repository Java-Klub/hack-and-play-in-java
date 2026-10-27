// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M13_YatzyScorecardTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I040 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M13_YatzyScorecard();
        game.reset();
        game.scores[0] = 1;
        game.scores[1] = 4;
        game.scores[2] = 9;
        check(game.total() == 14, "filled categories must accumulate rather than replace the running sum");
        game.reset();
        check(game.total() == 0, "unfilled negative entries contribute nothing");
        game.scores = new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1};
        check(game.total() == 98, "upper sum 63 adds the bonus exactly once");
        game.scores[5] = 17;
        check(game.total() == 62, "upper sum 62 earns no bonus");
        System.out.println("PASS " + checks + " checks");
    }
}
