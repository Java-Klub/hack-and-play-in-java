import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M08_DungeonBlueprintsTest {
    static int checks;
    static final String[] CASES = {
        "Plocha 42 je SMALL a opakované druhy se sčítají.",
        "Skutečně generované podzemí započte jen přijaté místnosti.",
        "Tvorba dalšího podzemí smaže staré součty."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I141: " + CASES[example] + " | actual=" + actual);
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
            var p = new M08_DungeonBlueprints();
            p.countRoom(new M08_DungeonBlueprints.Room(1,1,6,7));p.countRoom(new M08_DungeonBlueprints.Room(1,1,4,4));p.countRoom(new M08_DungeonBlueprints.Room(1,1,7,7));
            check(p.roomCounts.equals(Map.of("SMALL",2,"LARGE",1)),0,p.roomCounts);
            p.generate(42,8);int total=p.roomCounts.values().stream().mapToInt(Integer::intValue).sum();
            check(total>=2&&total<=8,1,p.roomCounts);
            p.generate(42,2);check(p.roomCounts.values().stream().mapToInt(Integer::intValue).sum()==2,2,p.roomCounts);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I141: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
