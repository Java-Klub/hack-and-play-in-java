// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M11_HexapawnTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I005 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M11_Hexapawn();
        check(game.goalRow(2) == 2, "player two must have the bottom goal row");
        check(game.goalRow(1) == 0, "player one must have the top goal row");
        game.reset();
        game.player = 2;
        game.board = new int[][]{{0,0,0},{2,0,0},{0,0,1}};
        game.command("2 1 3 1");
        check(game.outcome.contains("2 wins"), "the real promotion uses goalRow for the current player");
        System.out.println("PASS " + checks + " checks");
    }
}
