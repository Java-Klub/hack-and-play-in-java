// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M01_CannonPuzzleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I065 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_CannonPuzzle();
        check(Math.abs(game.launchSpeed(100) - 9) < 1e-8, "100 pixels of drag must give speed 9");
        check(Math.abs(game.launchSpeed(-80) + 7.2) < 1e-8, "upward drag must keep its negative fractional speed");
        game.restart();
        game.launch();
        check(game.vx > 0 && game.vy < 0, "the actual cannon must launch right and upward");
        System.out.println("PASS " + checks + " checks");
    }
}
