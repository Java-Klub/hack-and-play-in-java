// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M09_SOSTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I008 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_SOS();
        check(game.gainedPoints(2, 3) == 1, "one new SOS adds one point rather than rescoring old triples");
        check(game.gainedPoints(3, 3) == 0, "no new triple adds no points");
        check(game.gainedPoints(0, 2) == 2, "two simultaneous new triples add two points");
        game.reset();
        game.board = "SOS......".toCharArray();
        game.moves = 3;
        game.command("4 S");
        check(game.scores[1] == 0 && game.player == 2, "an actual non-scoring move must not score the old SOS again");
        System.out.println("PASS " + checks + " checks");
    }
}
