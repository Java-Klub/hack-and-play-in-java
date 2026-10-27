// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M04_SameGameTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I082 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_SameGame();
        game.board = new int[5][5];
        for (int[] row : game.board) Arrays.fill(row, 1);
        check(game.group(0, 0).size() == 25, "the queue loop must discover the full connected group");
        check(game.group(-1, 0).isEmpty(), "an invalid start is rejected before queue access");
        game.outcome = "";
        game.command("1 1");
        check(game.outcome.startsWith("Board clear") && game.score == 600, "the actual complete group can be removed to win");
        System.out.println("PASS " + checks + " checks");
    }
}
