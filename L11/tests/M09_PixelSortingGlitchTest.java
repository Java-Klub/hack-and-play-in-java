import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M09_PixelSortingGlitchTest {
    static int checks;
    static final String[] CASES = {
        "The exact nested filename exists after export.",
        "The decoded PNG is sorted while input pixels remain unchanged.",
        "An unusable parent produces an expected IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I115: " + CASES[example] + " | actual=" + actual);
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
            var p = new M09_PixelSortingGlitch();
            var input=new BufferedImage(3,1,BufferedImage.TYPE_INT_ARGB);input.setRGB(0,0,0xFFFFFFFF);input.setRGB(1,0,0xFF555555);input.setRGB(2,0,0xFFAAAAAA);
            Path root=Files.createTempDirectory("pixels-check-");Path file=root.resolve("nested/glitch.png");p.saveSorted(file,input,0);
            check(Files.isRegularFile(file),0,file);var result=ImageIO.read(file.toFile());
            check(result.getWidth()==3&&(result.getRGB(0,0)&0xFFFFFF)==0x555555,1,Integer.toHexString(result.getRGB(0,0)));
            check(input.getRGB(0,0)==0xFFFFFFFF,1,Integer.toHexString(input.getRGB(0,0)));
            Path blocked=root.resolve("blocked");Files.writeString(blocked,"x");ioFailure(()->p.saveSorted(blocked.resolve("x.png"),input,0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I115: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
