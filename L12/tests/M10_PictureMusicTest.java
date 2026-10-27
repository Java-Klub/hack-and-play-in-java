import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M10_PictureMusicTest {
    static int checks;
    static final String[] CASES = {
        "The new +12 shift doubles frequency and changes actual samples.",
        "Zero and -12 retain the old mapping and octave relation.",
        "Invalid rows and shifts are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I134: " + CASES[example] + " | actual=" + actual);
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
            var p = new M10_PictureMusic();
            double base=p.pitchFrequency(4,0);check(Math.abs(p.pitchFrequency(4,12)-base*2)<1e-9,0,p.pitchFrequency(4,12));
            check(Math.abs(p.pitchFrequency(4,-12)-base/2)<1e-9,1,p.pitchFrequency(4,-12));
            check(Math.abs(p.pitchFrequency(11,0)-220)<1e-9,1,p.pitchFrequency(11,0));
            reject(()->p.pitchFrequency(12,0),2);reject(()->p.pitchFrequency(0,13),2);
            var image=p.demo();p.transpose=0;var a=p.signal(image);p.transpose=12;check(!Arrays.equals(a,p.signal(image)),0,"transposed WAV");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I134: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
