// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M04_BitmapBannerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I121 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_BitmapBanner();
        check(game.bannerMessage().equals("JAVA CLUB!"), "the default banner must say JAVA CLUB!");
        String pixels = game.banner(game.bannerMessage(), '#');
        check(pixels.contains("#") && pixels.lines().count() == 5, "the message produces five visible pixel rows");
        System.out.println("PASS " + checks + " checks");
    }
}
