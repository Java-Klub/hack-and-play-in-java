// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M09_PixelMonstersTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I113 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_PixelMonsters();
        var image = game.render(42);
        check(image.getRGB(5 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "left eye must read the shared white eyeColour");
        check(image.getRGB(10 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "right eye must read that same white eyeColour");
        check(game.eyeColour == Color.WHITE.getRGB(), "one named value controls both eyes");
        System.out.println("PASS " + checks + " checks");
    }
}
