import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M07_RetroSoundForgeTest {
    static int checks;
    static final String[] CASES = {
        "Wobble uses arbitrary carried base and depth values.",
        "Jump and Laser retain their independent formulas.",
        "Invalid time is rejected and real sound presets differ."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I132: " + CASES[example] + " | actual=" + actual);
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
            var p = new M07_RetroSoundForge();
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Wobble(300,90),1.0/32)-390)<1e-9,0,p.frequency(new M07_RetroSoundForge.Wobble(300,90),1.0/32));
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Wobble(300,90),3.0/32)-210)<1e-9,0,p.frequency(new M07_RetroSoundForge.Wobble(300,90),3.0/32));
            check(p.frequency(new M07_RetroSoundForge.Wobble(410,25),0)==410,0,p.frequency(new M07_RetroSoundForge.Wobble(410,25),0));
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Jump(100,200),.5)-200)<1e-9,1,"jump");
            check(Math.abs(p.frequency(new M07_RetroSoundForge.Laser(100,.25),1)-25)<1e-9,1,"laser");
            reject(()->p.frequency(new M07_RetroSoundForge.Wobble(300,90),Double.NaN),2);
            check(!Arrays.equals(p.signal("wobble",.1),p.signal("laser",.1)),2,"different waves");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I132: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
