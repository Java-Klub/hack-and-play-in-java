import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M02_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "První střed se uloží a přijme.",
        "Stejný střed se odmítne; prohozené coordinate (souřadnice) jsou jiná hodnota.",
        "Skutečná zahrada dál umístí osm různých bublin."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I111: " + CASES[example] + " | actual=" + actual);
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
            var p = new M02_CirclePacking();
            Set<M02_CirclePacking.Centre> seen=new HashSet<>();
            check(p.claimCentre(seen,4,7)&&seen.size()==1,0,seen);
            check(!p.claimCentre(seen,4,7)&&seen.size()==1,1,seen);
            check(p.claimCentre(seen,7,4)&&seen.size()==2,1,seen);
            var bubbles=p.pack(42,8);check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->new M02_CirclePacking.Centre((int)b.x(),(int)b.y())).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I111: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
