import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M02_GapHopperTest {
    static int checks;
    static final String[] CASES = {
        "The expired object is removed and its successor survives.",
        "The new obstacle is appended beyond the last one; unchanged obstacles are not recycled.",
        "An empty list is safe."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I052: " + CASES[example] + " | actual=" + actual);
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
            var p = new M02_GapHopper();
            p.restart(); var old = p.pipes.getFirst(); var next = p.pipes.getLast();
            old.x = -60; next.x = 200; p.recyclePipes();
            check(p.pipes.size()==2 && p.pipes.getFirst()==next && !p.pipes.contains(old),0,p.pipes.size());
            check(p.pipes.getLast().x==460 && !p.pipes.getLast().scored,1,p.pipes.getLast().x);
            var stable=List.copyOf(p.pipes);p.recyclePipes();check(p.pipes.equals(stable),1,p.pipes.size());
            p.pipes.clear();p.recyclePipes();check(p.pipes.isEmpty(),2,p.pipes);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I052: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
