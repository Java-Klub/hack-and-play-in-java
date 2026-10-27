// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M07_HanoiTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I013 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_Hanoi();
        game.reset();
        check(game.move(0, 1), "a small disc may move onto an empty peg");
        int before = game.moves;
        check(!game.move(0, 1), "a larger disc must be rejected above a smaller disc");
        check(game.moves == before && game.sizes[0] == 2 && game.sizes[1] == 1, "rejected moves preserve both stacks and the counter");
        check(game.move(0, 2) && game.move(1, 2), "a smaller disc may land above a larger one");
        check(!game.move(-1, 0) && !game.move(0, 3) && !game.move(0, 0), "outer guards still reject invalid or identical pegs");
        System.out.println("PASS " + checks + " checks");
    }
}
