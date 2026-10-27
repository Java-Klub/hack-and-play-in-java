// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M12_ToadsAndFrogsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I095 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M12_ToadsAndFrogs();
        check(game.direction(2) == -1, "a frog must receive the leftward result");
        check(game.direction(1) == 1, "a toad must receive the rightward result");
        game.restart();
        check(game.move(4) && game.cells[3] == 2 && game.cells[4] == 0, "the actual right-hand frog can step left into the gap");
        game.restart();
        check(game.move(2) && game.cells[3] == 1, "the actual left-hand toad can step right into the gap");
        System.out.println("PASS " + checks + " checks");
    }
}
