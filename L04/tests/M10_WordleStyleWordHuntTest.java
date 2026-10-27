// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M10_WordleStyleWordHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I023 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M10_WordleStyleWordHunt();
        game.secret = "LEVEL";
        char[] marks = ".....".toCharArray();
        int[] counts = game.leftoverLetters("ALLEY", marks);
        check(counts['L'-'A'] == 2, "two unmatched L letters need an accumulated count of two");
        check(game.grade("ALLEY").equals(".YYG."), "both guessed L letters receive the actual remaining matches");
        check(game.grade("LEVEL").equals("GGGGG"), "exact matches stay green and consume no leftover count");
        check(game.grade("STONE").equals("....Y"), "an absent letter cannot gain a false match");
        System.out.println("PASS " + checks + " checks");
    }
}
