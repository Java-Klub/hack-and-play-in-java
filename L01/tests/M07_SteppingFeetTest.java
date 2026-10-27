// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M07_SteppingFeetTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I098 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_SteppingFeet();
        game.restart();
        check(game.x == 80, "the later assignment must leave shared x at 80");
        check(game.paused, "the prepared animation still waits for Space");
        game.paused = false;
        game.step();
        check(Math.abs(game.x - 81.6) < 1e-8, "both supplied feet continue from the repaired shared coordinate");
        System.out.println("PASS " + checks + " checks");
    }
}
