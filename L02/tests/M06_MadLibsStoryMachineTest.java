// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M06_MadLibsStoryMachineTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I129 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_MadLibsStoryMachine();
        check(game.cleanWord("  Red Fox  ").equals("Red Fox"), "edge spaces must disappear while a multiword name stays whole");
        check(game.cleanWord("New   Moon").equals("New   Moon"), "internal spacing is not silently rewritten");
        String story = game.story("  Red Fox  ", " robot ", " Moon Base ", " spoon ");
        check(story.startsWith("Red Fox hired a robot to guard Moon Base."), "the actual story uses cleaned full inputs");
        System.out.println("PASS " + checks + " checks");
    }
}
