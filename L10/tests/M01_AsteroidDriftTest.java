import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M01_AsteroidDriftTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) nevystřelí.",
        "Spuštění callback i skutečná držená klávesa vystřelí jednou.",
        "Cooldown i prohra dál brání nové střele."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I051: " + CASES[example] + " | actual=" + actual);
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
            var p = new M01_AsteroidDrift();
            p.restart();var action=p.firingAction();check(p.shots.isEmpty(),0,p.shots.size());
            action.run();check(p.shots.size()==1&&p.cooldown==10,1,p.shots.size());
            action.run();check(p.shots.size()==1,2,p.shots.size());
            p.restart();p.key(java.awt.event.KeyEvent.VK_SPACE,true);p.step();check(p.shots.size()==1,1,p.shots.size());
            p.lost=true;p.cooldown=0;p.firingAction().run();check(p.shots.size()==1,2,p.shots.size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I051: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
