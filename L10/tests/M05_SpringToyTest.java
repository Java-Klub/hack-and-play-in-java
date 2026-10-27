import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M05_SpringToyTest {
    static int checks;
    static final String[] CASES = {
        "Rendering (vykreslení) zachová všechny hodnoty pohybu.",
        "Opakované kreslení dá stejné pixel (body obrazu) a nepřidá stopu.",
        "Výslovný step model dál posouvá."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I070: " + CASES[example] + " | actual=" + actual);
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
            var p = new M05_SpringToy();
            p.restart();p.paused=false;double x=p.x,y=p.y,vx=p.vx,vy=p.vy;
            var first=draw(p::draw,p.WIDTH,p.HEIGHT);
            check(p.x==x&&p.y==y&&p.vx==vx&&p.vy==vy,0,p.y);
            check(p.trail.isEmpty(),1,p.trail.size());
            var second=draw(p::draw,p.WIDTH,p.HEIGHT);check(hash(first)==hash(second),1,hash(second));
            p.step();check(p.y!=y&&!p.trail.isEmpty(),2,p.y);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I070: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
