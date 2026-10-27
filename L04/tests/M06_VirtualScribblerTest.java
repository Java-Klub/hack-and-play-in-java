// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M06_VirtualScribblerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I100 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_VirtualScribbler();
        game.restart();
        game.paused = false;
        game.burst(1);
        check(game.steps == 1, "a one-step burst must execute once");
        game.burst(0);
        check(game.steps == 1, "a zero-step burst must execute zero times");
        game.burst(4);
        check(game.steps == 5, "a four-step burst adds exactly four pen moves");
        game.paused = true;
        game.burst(4);
        check(game.steps == 5, "the supplied pause rule remains active");
        System.out.println("PASS " + checks + " checks");
    }
}
