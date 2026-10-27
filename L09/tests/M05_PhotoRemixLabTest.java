import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M05_PhotoRemixLabTest {
    static int checks;
    static final String[] CASES = {
        "Repeated colour keys accumulate instead of overwriting.",
        "A real two-pixel remix counts both occurrences.",
        "Remixing a new source removes stale keys."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I079: " + CASES[example] + " | actual=" + actual);
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
            var p = new M05_PhotoRemixLab();
            p.countColour(0x123456);p.countColour(0x123456);p.countColour(0xABCDEF);
            check(p.colourCounts.get(0x123456)==2&&p.colourCounts.get(0xABCDEF)==1,0,p.colourCounts);
            p.source=new BufferedImage(2,1,BufferedImage.TYPE_INT_RGB);p.source.setRGB(0,0,0x112233);p.source.setRGB(1,0,0x112233);p.remix();
            check(p.colourCounts.size()==1&&p.colourCounts.values().iterator().next()==2,1,p.colourCounts);
            p.source=new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);p.source.setRGB(0,0,0xFFFFFF);p.remix();
            check(p.colourCounts.size()==1&&p.colourCounts.values().iterator().next()==1,2,p.colourCounts);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I079: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
