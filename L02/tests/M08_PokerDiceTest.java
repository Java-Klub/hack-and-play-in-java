// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M08_PokerDiceTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I039 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_PokerDice();
        check(!game.fullHouse(3, 0), "three equal dice alone are not a full house");
        check(!game.fullHouse(2, 1), "a pair alone is not a full house");
        check(game.fullHouse(3, 1), "three equal dice plus a pair form a full house");
        game.dice = new int[]{2, 2, 2, 3, 4};
        check(game.points() == 20, "actual three-kind scores 20 rather than full-house 35");
        game.dice = new int[]{2, 2, 2, 3, 3};
        check(game.points() == 35, "actual full house scores 35");
        System.out.println("PASS " + checks + " checks");
    }
}
