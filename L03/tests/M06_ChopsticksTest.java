// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M06_ChopsticksTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I010 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_Chopsticks();
        game.reset();
        game.hands[0][0] = 0;
        check(!game.handsAlive(0, 0), "a dead attacking hand must be rejected");
        game.hands[0][0] = 1;
        game.hands[1][1] = 0;
        check(!game.handsAlive(0, 1), "a dead target hand must be rejected");
        check(game.handsAlive(1, 0), "two living hands remain legal");
        check(!game.handsAlive(-1, 0) && !game.handsAlive(0, 2), "invalid indices are rejected before hand access");
        int player = game.player;
        try { game.command("1 2"); } catch (IllegalArgumentException expected) { }
        check(game.player == player && game.hands[1][1] == 0, "rejected input leaves actual game state untouched");
        System.out.println("PASS " + checks + " checks");
    }
}
