import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M03_PhasingRhythmsTest {
    static int checks;
    static final String[] CASES = {
        "Oba výsledky rolí přispívají se zesílením 0.17.",
        "Opačné náhradní hlasy se vyruší.",
        "Skutečná syntéza používá composition (skládání) a zachová guard (podmínky) trvání."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I133: " + CASES[example] + " | actual=" + actual);
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
            var p = new M03_PhasingRhythms();
            p.first=t->.2;p.second=t->.5;check(Math.abs(p.mix(.4)-.119)<1e-12,0,p.mix(.4));
            p.first=t->t+.2;p.second=t->.5-2*t;
            check(Math.abs(p.mix(.4)-.051)<1e-12&&Math.abs(p.mix(.8)+.017)<1e-12,0,p.mix(.8));
            p.first=t->-.2;p.second=t->.2;check(Math.abs(p.mix(.8))<1e-12,1,p.mix(.8));
            p.first=t->.25;p.second=t->0;var samples=p.signal(1);check(samples.length==22050&&Math.abs(samples[100]-.0425)<1e-12,2,samples[100]);
            p.first=t->t*.25;p.second=t->-t*.1;samples=p.signal(1);
            check(Math.abs(samples[100]-.17*.15*100/22050)<1e-12&&Math.abs(samples[1000]-.17*.15*1000/22050)<1e-12,2,samples[1000]);
            reject(()->p.signal(.5),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I133: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
