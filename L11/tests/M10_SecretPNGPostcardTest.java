import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M10_SecretPNGPostcardTest {
    static int checks;
    static final String[] CASES = {
        "A nested PNG round-trips a UTF-8 message.",
        "An empty message survives the same path.",
        "Unsupported and missing input paths cause IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I117: " + CASES[example] + " | actual=" + actual);
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
            var p = new M10_SecretPNGPostcard();
            Path root=Files.createTempDirectory("secret-check-");Path file=root.resolve("nested/secret.png");p.message="Pixel π";p.save(file,42,1);
            check(p.readMessage(file).equals("Pixel π"),0,p.readMessage(file));
            p.message="";p.save(file,42,1);check(p.readMessage(file).isEmpty(),1,p.readMessage(file));
            Path text=root.resolve("plain.txt");Files.writeString(text,"not a PNG");ioFailure(()->p.readMessage(text),2);
            ioFailure(()->p.readMessage(root.resolve("missing.png")),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I117: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
