import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M03_ShadowPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) zachová počáteční pozici světla.",
        "Spuštění i skutečné press/drag použijí zadané x/y.",
        "Coordinate (souřadnice) mimo plátno omezí moveLight."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I078: " + CASES[example] + " | actual=" + actual);
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
            var p = new M03_ShadowPlayground();
            p.restart();var action=p.lightAction();check(p.lightX==100&&p.lightY==80,0,p.lightX);
            action.accept(43,91);check(p.lightX==43&&p.lightY==91,1,p.lightX+","+p.lightY);
            action.accept(-20,999);check(p.lightX==0&&p.lightY==p.HEIGHT,2,p.lightX+","+p.lightY);
            p.press(140,70,1);p.drag(160,90);check(p.lightX==160&&p.lightY==90,1,p.lightX+","+p.lightY);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I078: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
