// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M07_CodebreakerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I020 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_Codebreaker();
        game.secret = new String("1234");
        check(game.matchesCode(new String("1234")), "equal code text must match even when stored as different String objects");
        check(!game.matchesCode("1235"), "a different final digit must not match");
        game.attempts = 0;
        game.won = false;
        check(game.submit(new String("1234")) && game.won, "the actual matching guess must end the round");
        System.out.println("PASS " + checks + " checks");
    }
}
