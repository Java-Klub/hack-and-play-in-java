import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M07_MarkovMischiefTest {
    static int checks;
    static final String[] CASES = {
        "Opakované moon zachová cat, dog, cat v tomto pořadí.",
        "Existující List (seznamy) následníků přežijí další výskyty.",
        "Jediné slovo má prázdný List a prázdný text se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I130: " + CASES[example] + " | actual=" + actual);
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
            var p = new M07_MarkovMischief();
            var links=p.train("moon cat moon dog moon cat");
            check(links.get("moon").equals(List.of("cat","dog","cat")),0,links);
            check(links.containsKey("cat")&&links.get("cat").equals(List.of("moon")),1,links);
            var single=p.train("solo");check(single.containsKey("solo")&&single.get("solo").isEmpty(),2,single);
            reject(()->p.train("   "),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I130: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
