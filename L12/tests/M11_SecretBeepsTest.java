import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M11_SecretBeepsTest {
    static int checks;
    static final String[] CASES = {
        "Nová mezera mezi slovy změní skutečnou délku zvuku.",
        "Původních sedm jednotek i tři jednotky mezi písmeny zůstanou stejné.",
        "Mezery mezi slovy mimo 3–12 se odmítnou."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I143: " + CASES[example] + " | actual=" + actual);
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
            var p = new M11_SecretBeeps();
            check(p.units("E E",10).equals(List.of(1,-10,1)),0,p.units("E E",10));
            check(p.units("E E").equals(List.of(1,-7,1))&&p.units("EE",10).equals(List.of(1,-3,1)),1,p.units("EE",10));
            reject(()->p.units("E E",2),2);reject(()->p.units("E E",13),2);
            p.wordGap=7;int original=p.signal("E E",20).length;p.wordGap=10;check(p.signal("E E",20).length-original==3*(22050*20/1000),0,p.signal("E E",20).length);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I143: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
