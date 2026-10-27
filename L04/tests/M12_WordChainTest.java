// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M12_WordChainTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I027 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M12_WordChain();
        game.reset();
        check(game.choices().size() == 20, "the accumulated choices must retain all initially legal words");
        game.last = "APPLE";
        check(game.choices().equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "all five E continuations survive the loop");
        game.used.add("EARTH");
        check(!game.choices().contains("EARTH") && game.choices().size() == 4, "used words are excluded without losing other choices");
        game.reset();
        game.command("APPLE");
        check(game.last.equals("APPLE"), "the actual game accepts an early legal word, not only the final dictionary entry");
        System.out.println("PASS " + checks + " checks");
    }
}
