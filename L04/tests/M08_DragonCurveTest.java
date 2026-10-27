// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M08_DragonCurveTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I108 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_DragonCurve();
        check(game.foldTurns(1).equals(List.of(1)), "one requested fold must run the outer loop once");
        check(game.foldTurns(0).isEmpty(), "zero folds must run zero times");
        check(game.foldTurns(2).equals(List.of(1,1,-1)), "two folds use two outer passes");
        check(game.path(3).size() == 9, "the actual three-fold path has eight segments and nine points");
        System.out.println("PASS " + checks + " checks");
    }
}
