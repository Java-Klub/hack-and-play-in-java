import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M12_MagicEyeStripTest {
    static int checks;
    static final String[] CASES = {
        "Seed (počáteční hodnota) pochází z obsahu, nikoli názvu.",
        "Mezery i záporný seed fungují ve skutečném vstupu vykreslení.",
        "Nečíselný text a chybějící cesta zůstanou viditelné očekávané chyby."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I120: " + CASES[example] + " | actual=" + actual);
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
            var p = new M12_MagicEyeStrip();
            Path root=Files.createTempDirectory("eye-check-");Path file=root.resolve("seed.txt");Files.writeString(file," 42\n");
            check(p.readSeed(file)==42,0,p.readSeed(file));
            check(hash(p.render(p.readSeed(file),16))==hash(p.render(42,16)),1,"same picture");
            Files.writeString(file,"-7");check(p.readSeed(file)==-7,1,p.readSeed(file));
            Files.writeString(file,"banana");try{p.readSeed(file);check(false,2,"accepted");}catch(NumberFormatException expected){check(true,2,"bad number");}
            ioFailure(()->p.readSeed(root.resolve("missing.txt")),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I120: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
