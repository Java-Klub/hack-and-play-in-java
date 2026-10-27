import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M06_TruchetTileLabTest {
    static int checks;
    static final String[] CASES = {
        "Další LEFT zvýší jen počet LEFT.",
        "Skutečný render 4×4 započte šestnáct dlaždic.",
        "Pozdější render 2×2 obnoví součet na čtyři."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I109: " + CASES[example] + " | actual=" + actual);
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
            var p = new M06_TruchetTileLab();
            p.countOrientation("LEFT");p.countOrientation("RIGHT");p.countOrientation("LEFT");
            check(p.orientationCounts.equals(Map.of("LEFT",2,"RIGHT",1)),0,p.orientationCounts);
            p.render(42,4);check(p.orientationCounts.values().stream().mapToInt(Integer::intValue).sum()==16,1,p.orientationCounts);
            p.render(42,2);check(p.orientationCounts.values().stream().mapToInt(Integer::intValue).sum()==4,2,p.orientationCounts);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I109: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
