import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M05_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "Všechny tři platné vrcholy jsou dosažitelné.",
        "Pevný seed (počáteční hodnota) zopakuje sequence (posloupnost) indexů.",
        "Jediná možnost dá nulu a prázdný rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I105: " + CASES[example] + " | actual=" + actual);
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
            var p = new M05_ChaosGame();
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(p.chooseVertex(random,3));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(p.chooseVertex(a,3)==p.chooseVertex(b,3),1,n);
            check(p.chooseVertex(new Random(42),1)==0,2,"single");reject(()->p.chooseVertex(new Random(42),0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I105: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
