// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M03_BouncingBallsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I069 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_BouncingBalls();
        check(Math.abs(game.bounceSpeed(4) - 3) < 1e-8, "a wall bounce retains three quarters of speed");
        check(Math.abs(game.bounceSpeed(-8) - 6) < 1e-8, "the supplied magnitude conversion must retain a fraction");
        check(game.bounceSpeed(0) == 0, "zero speed remains zero");
        game.restart();
        game.add(11, 100, -4, 0);
        game.step();
        check(game.balls.getFirst().vx() > 2.9, "the real ball reverses away from the left wall");
        System.out.println("PASS " + checks + " checks");
    }
}
