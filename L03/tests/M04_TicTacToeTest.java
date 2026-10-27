// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M04_TicTacToeTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I002 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_TicTacToe();
        game.reset();
        check(game.play(9) && game.board[8] == 'X', "cell nine is a legal empty boundary cell");
        check(!game.play(9) && game.moves == 1, "an occupied cell must be rejected without consuming a move");
        check(!game.play(0) && !game.play(10), "out-of-range cells must be rejected before indexing the board");
        check(game.player == 'O', "rejected commands must not pass play again");
        System.out.println("PASS " + checks + " checks");
    }
}
