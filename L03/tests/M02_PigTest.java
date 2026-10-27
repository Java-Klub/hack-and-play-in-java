// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M02_PigTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I037 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_Pig();
        game.reset();
        game.turnTotal = 9;
        game.roll(1);
        check(game.turnTotal == 0 && game.player == 1, "rolling one must lose the turn and pass the die");
        game.reset();
        game.roll(6);
        check(game.turnTotal == 6 && game.player == 0, "six adds points and keeps the turn");
        check(!game.roll(0) && game.turnTotal == 6, "the supplied invalid-die guard remains intact");
        System.out.println("PASS " + checks + " checks");
    }
}
