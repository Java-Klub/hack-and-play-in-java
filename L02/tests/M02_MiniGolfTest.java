// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M02_MiniGolfTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I066 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_MiniGolf();
        check(Math.abs(game.dragVelocity(100) - 8) < 1e-8, "100 pixels must produce speed 8 before the supplied cap");
        check(Math.abs(game.dragVelocity(-25) + 2) < 1e-8, "negative distance must produce -2");
        check(game.dragVelocity(0) == 0, "no pull means no velocity");
        game.restart();
        game.begin(90, 110);
        game.shoot(-10, 110);
        check(Math.abs(game.vx - 8) < 1e-8 && game.strokes == 1, "the actual putt uses the repaired scale");
        System.out.println("PASS " + checks + " checks");
    }
}
