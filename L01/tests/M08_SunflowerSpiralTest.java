// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M08_SunflowerSpiralTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I110 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_SunflowerSpiral();
        check(Math.abs(game.defaultAngle - 137.5) < 1e-8, "the shared default angle must be 137.5 degrees");
        double[] second = game.position(2, game.defaultAngle);
        check(Math.abs(second[1] - 250) > 1, "the default must spread seeds beyond a single horizontal spoke");
        check(game.render(42, game.defaultAngle).getWidth() == 500, "the actual PNG renderer accepts the repaired default");
        System.out.println("PASS " + checks + " checks");
    }
}
