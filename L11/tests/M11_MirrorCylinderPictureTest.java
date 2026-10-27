import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M11_MirrorCylinderPictureTest {
    static int checks;
    static final String[] CASES = {
        "The requested nested file contains nonempty bytes.",
        "The actual saved PNG has the expected image dimensions.",
        "An invalid parent path reports IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I119: " + CASES[example] + " | actual=" + actual);
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
            var p = new M11_MirrorCylinderPicture();
            Path root=Files.createTempDirectory("mirror-check-");Path file=root.resolve("nested/mirror.png");p.save(file,42,125);
            check(Files.exists(file)&&Files.size(file)>0,0,Files.size(file));
            var image=ImageIO.read(file.toFile());check(image!=null&&image.getWidth()==900&&image.getHeight()==800,1,image==null?null:image.getWidth());
            Path blocked=root.resolve("blocked");Files.writeString(blocked,"x");ioFailure(()->p.save(blocked.resolve("x.png"),42,125),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I119: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
