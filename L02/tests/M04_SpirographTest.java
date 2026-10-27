// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M04_SpirographTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I075 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_Spirograph();
        check(Math.abs(game.radiusRatio(120, 45) - 5.0 / 3) < 1e-8, "75 divided by 45 must retain its fractional ratio");
        check(game.radiusRatio(120, 60) == 1, "an exact ratio remains exact");
        check(game.radiusRatio(120, 80) == 0.5, "a ratio below one must not become zero");
        game.restart();
        var p = game.point(1);
        check(Math.abs(p.x - (75 * Math.cos(1) + 65 * Math.cos(5.0 / 3))) < 1e-8, "the visible curve uses the repaired ratio");
        System.out.println("PASS " + checks + " checks");
    }
}
