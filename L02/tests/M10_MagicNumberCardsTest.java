// Kontroly chování právě opravovaného pravidla.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M10_MagicNumberCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I135 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M10_MagicNumberCards();
        check(game.yesAnswer(new String("y")), "a typed lower-case y must mean yes");
        check(game.yesAnswer("Y"), "upper-case Y must also mean yes");
        check(!game.yesAnswer("n"), "n must mean no");
        boolean[] answers = {game.yesAnswer("Y"), false, true, false, false, false};
        check(game.reveal(answers) == 5, "the actual trick must include the first yes card");
        System.out.println("PASS " + checks + " checks");
    }
}
