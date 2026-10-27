// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M10_ShutTheBoxTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I038 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M10_ShutTheBox();
        check(game.diceTotal(2, 5) == 7, "both dice must contribute to the target");
        check(game.diceTotal(6, 6) == 12 && game.diceTotal(1, 1) == 2, "the smallest and largest two-dice totals are included");
        game.random = new Random(42);
        game.reset();
        var random = new Random(42);
        int expected = random.nextInt(1,7) + random.nextInt(1,7);
        check(game.target == expected, "the actual roll uses the two supplied arguments");
        System.out.println("PASS " + checks + " checks");
    }
}
