import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M08_EndlessToneStaircaseTest {
    static int checks;
    static final String[] CASES = {
        "Vznikne celý zadaný vnořený cíl.",
        "Skutečný WAV obsahuje očekávaný počet a frekvenci frame (rámců).",
        "Nadřazený file (soubor) místo adresáře způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I103: " + CASES[example] + " | actual=" + actual);
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
            var p = new M08_EndlessToneStaircase();
            Path root=Files.createTempDirectory("tone-check-");Path file=root.resolve("nested/tone.wav");
            p.write(file,new double[]{0,.2,-.2,0});check(Files.isRegularFile(file),0,file);
            try(var audio=javax.sound.sampled.AudioSystem.getAudioInputStream(file.toFile())){check(audio.getFrameLength()==4&&audio.getFormat().getSampleRate()==22050,1,audio.getFrameLength());}
            Path blocked=root.resolve("blocked");Files.writeString(blocked,"x");ioFailure(()->p.write(blocked.resolve("tone.wav"),new double[1]),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I103: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
