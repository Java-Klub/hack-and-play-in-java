import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M13_RippleTankTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I073: " + CASES[example] + " | actual=" + actual);
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
            var p = new M13_RippleTank();
            p.restart();p.tap(20,10);check(!p.paused&&p.height[10][20]==60,0,p.height[10][20]);p.step();check(p.previous[10][20]==60&&Math.abs(p.height[10][21]-29.76)<1e-9,1,p.height[10][21]);p.restart();p.tap(0,0);check(p.paused&&p.energy()==0,2,p.energy());p.tap(20,10);p.paused=true;double energy=p.energy();p.step();check(p.energy()==energy,2,p.energy());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I073: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
