// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M08_SudokuTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I053 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_Sudoku();
        game.reset();
        check(game.place(0, 1, 2) && game.board[0][1] == 2, "a valid empty square must accept a number");
        check(!game.place(0, 0, 0) && game.board[0][0] == 1, "a printed clue must never be erased");
        check(!game.place(-1, 0, 1) && !game.place(0, 4, 1), "invalid coordinates are rejected before clue access");
        check(!game.place(0, 2, 2) && game.board[0][2] == 0, "a conflicting number is rejected before writing");
        System.out.println("PASS " + checks + " checks");
    }
}
