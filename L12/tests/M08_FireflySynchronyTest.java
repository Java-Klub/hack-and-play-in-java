import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M08_FireflySynchronyTest {
    static int checks;
    static final String[] CASES = {
        "A valid Coupling value reaches the model.",
        "Both the upper boundary and invalid-value preservation work.",
        "Toggle and seeded Restart dispatch to the correct independent rules."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I139: " + CASES[example] + " | actual=" + actual);
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
            var p = new M08_FireflySynchrony();
            p.reset(42);p.apply(new M08_FireflySynchrony.Coupling(.35));check(p.coupling==.35,0,p.coupling);
            p.apply(new M08_FireflySynchrony.Coupling(2));check(p.coupling==2,1,p.coupling);
            p.apply(new M08_FireflySynchrony.Coupling(2.1));p.apply(new M08_FireflySynchrony.Coupling(Double.NaN));check(p.coupling==2,1,p.coupling);
            for(double invalid:new double[]{-.1,2.1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.NaN}){
                p.apply(new M08_FireflySynchrony.Coupling(.35));p.apply(new M08_FireflySynchrony.Coupling(invalid));check(p.coupling==.35,1,p.coupling);
            }
            p.apply(new M08_FireflySynchrony.Coupling(0));check(p.coupling==0,1,p.coupling);
            boolean run=p.running;p.apply(new M08_FireflySynchrony.Toggle());check(p.running!=run,2,p.running);
            var expected=new M08_FireflySynchrony();expected.reset(17);p.apply(new M08_FireflySynchrony.Restart(17));check(Arrays.equals(p.phase,expected.phase),2,p.phase[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I139: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
