import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M04_PendulumPainterTest {
    static int checks;
    static final String[] CASES = {
        "Výchozí role čte aktuální úhel, délku a gravitaci.",
        "Konstantní jiné zrychlení změní skutečnou rychlost.",
        "Nulové jiné pravidlo udrží původně klidný úhel."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I138: " + CASES[example] + " | actual=" + actual);
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
            var p = new M04_PendulumPainter();
            p.reset(42);double expected=-p.gravity/p.length*Math.sin(p.angle);check(Math.abs(p.accelerationNow()-expected)<1e-12,0,p.accelerationNow());
            p.angle=.4;p.length=120;expected=-p.gravity/p.length*Math.sin(p.angle);check(Math.abs(p.accelerationNow()-expected)<1e-12,0,p.accelerationNow());
            p.acceleration=(angle,length)->angle+length/100;
            check(Math.abs(p.accelerationNow()-1.6)<1e-12,1,p.accelerationNow());
            p.angle=-.2;p.length=50;check(Math.abs(p.accelerationNow()-.3)<1e-12,1,p.accelerationNow());
            p.step();check(Math.abs(p.velocity-.3*.03*.999)<1e-12,1,p.velocity);
            p.reset(42);
            p.acceleration=(angle,length)->2;p.step();check(Math.abs(p.velocity-.05994)<1e-12,1,p.velocity);
            p.acceleration=(angle,length)->0;p.reset(42);double before=p.angle;p.step();check(p.angle==before&&p.ticks==1,2,p.angle);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I138: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
