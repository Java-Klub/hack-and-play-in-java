import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M12_TileStampStudioTest {
    static int checks;
    static final String[] CASES = {
        "A valid typed value changes repeats.",
        "Malformed and out-of-range values preserve the old value.",
        "Bounds 1 and 12 change the real rendered dimensions."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I142: " + CASES[example] + " | actual=" + actual);
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
            var p = new M12_TileStampStudio();
            check(p.applyRepeats(" 3 ")&&p.repeats==3,0,p.repeats);
            check(!p.applyRepeats("banana")&&p.repeats==3,1,p.repeats);
            check(!p.applyRepeats("0")&&!p.applyRepeats("13")&&p.repeats==3,1,p.repeats);
            check(p.applyRepeats("1")&&p.render(p.repeats).getWidth()==48,2,p.repeats);
            check(p.applyRepeats("12")&&p.render(p.repeats).getWidth()==576,2,p.repeats);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I142: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
