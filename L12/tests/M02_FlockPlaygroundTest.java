import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M02_FlockPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Default strength respects the distance boundary.",
        "A replacement role is observed by obstaclePush.",
        "The real bird step changes when the role changes."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I128: " + CASES[example] + " | actual=" + actual);
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
            var p = new M02_FlockPlayground();
            p.reset(42);check(p.obstaclePush(20)==.2&&p.obstaclePush(75)==0,0,p.obstaclePush(20));
            p.avoidance=d->d/100;check(p.obstaclePush(35)==.35&&p.obstaclePush(90)==.9,1,p.obstaclePush(35));
            p.avoidance=d->.7;check(p.obstaclePush(20)==.7,1,p.obstaclePush(20));
            var other=new M02_FlockPlayground();other.reset(42);other.avoidance=d->0;
            p.x[0]=other.x[0]=310;p.y[0]=other.y[0]=200;p.vx[0]=other.vx[0]=0;p.vy[0]=other.vy[0]=0;
            p.step();other.step();check(Math.abs(p.vx[0]-other.vx[0])>1e-6,2,p.vx[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I128: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
