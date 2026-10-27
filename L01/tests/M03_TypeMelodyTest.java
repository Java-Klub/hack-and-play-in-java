// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M03_TypeMelodyTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I101 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_TypeMelody();
        check(game.defaultMessage().equals("JAVA"), "the opening melody must use the literal JAVA");
        byte[] audio = game.synthesize(game.defaultMessage(), 42);
        boolean audible = false;
        for (byte sample : audio) {
            audible |= sample != 0;
        }
        check(audible, "letters generate actual non-silent audio samples");
        check(audio.length == 4 * 2205 * 2, "four characters retain four time slots");
        System.out.println("PASS " + checks + " checks");
    }
}
