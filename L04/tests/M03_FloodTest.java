// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M03_FloodTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I055 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_Flood();
        game.board = new int[8][8];
        game.board[7][7] = 1;
        game.fill(2);
        check(game.board[7][6] == 2, "the expanding queue must be processed beyond the first cell");
        int changed = 0;
        for (int[] row : game.board) { for (int value : row) { if (value == 2) changed++; } }
        check(changed == 63 && game.board[7][7] == 1, "all and only the connected old colour must change");
        check(game.moves == 1, "one completed flood counts as one move");
        game.spread(new ArrayList<>(), 0, 1);
        check(game.board[7][7] == 1, "an empty work queue performs no iteration");
        System.out.println("PASS " + checks + " checks");
    }
}
