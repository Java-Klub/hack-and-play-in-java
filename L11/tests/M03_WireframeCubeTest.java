import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M03_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "Repeated and reversed pairs collapse to two distinct edges.",
        "The surviving edge keys have smaller/larger order.",
        "Invalid/self/empty data are safe; the real cube has twelve edges."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I122: " + CASES[example] + " | actual=" + actual);
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
            var p = new M03_WireframeCube();
            var edges=p.uniqueEdges(new int[][]{{0,1},{1,0},{0,1},{1,2}});
            check(edges.size()==2,0,edges);
            check(edges.contains(new M03_WireframeCube.Edge(0,1))&&edges.contains(new M03_WireframeCube.Edge(1,2)),1,edges);
            check(p.uniqueEdges(new int[][]{{-1,0},{0,8},{2,2}}).isEmpty(),2,"invalid");
            check(p.uniqueEdges(new int[0][2]).isEmpty(),2,"empty");
            check(p.uniqueEdges(p.edges).size()==12,0,p.uniqueEdges(p.edges).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I122: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
