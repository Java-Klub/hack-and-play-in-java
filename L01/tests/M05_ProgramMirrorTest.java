// Focused behaviour checks.
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

class M05_ProgramMirrorTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I136 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_ProgramMirror();
        check(game.templateLine().equals("IO.println(\"HELLO\");"), "the printed statement needs literal quotation marks around HELLO");
        check(!game.templateLine().equals("HELLO"), "the template is a printed Java statement, not its output");
        System.out.println("PASS " + checks + " checks");
    }
}
