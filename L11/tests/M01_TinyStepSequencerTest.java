import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M01_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Two toggles restore the same active-note Set.",
        "A new note appears exactly once.",
        "Invalid cells preserve membership; an empty Set exports silence."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I102: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = new M01_TinyStepSequencer();
            p.reset();var original=Set.copyOf(p.notes);p.toggle(0,1);p.toggle(0,1);
            check(p.notes.equals(original),0,p.notes);
            p.notes.clear();check(p.toggle(2,4)&&p.notes.contains(new M01_TinyStepSequencer.Note(2,4)),1,p.notes);
            check(p.toggle(2,4)&&p.notes.isEmpty(),0,p.notes);
            check(!p.toggle(-1,0)&&!p.toggle(4,0)&&!p.toggle(0,8)&&p.notes.isEmpty(),2,p.notes);
            check(Arrays.stream(p.sequence(1)).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I102: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
