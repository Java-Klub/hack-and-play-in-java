import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M05_KnightTourTest {
    static int checks;
    static final String[] CASES = {
        "Move zachová pořadí record component (složek record).",
        "Další platné tahy zvýší počet jednou.",
        "Chybné coordinate (souřadnice) zachovají state (stav) a Restart obnoví počáteční desku."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I096: " + CASES[example] + " | actual=" + actual);
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
            var p = new M05_KnightTour();
            p.restart();check(p.apply(new M05_KnightTour.Move(1,2))&&p.x==1&&p.y==2,0,p.x+","+p.y);
            check(p.apply(new M05_KnightTour.Move(3,3))&&p.count==3,1,p.count);
            int x=p.x,y=p.y;check(!p.apply(new M05_KnightTour.Move(99,0))&&p.x==x&&p.y==y,2,p.count);
            check(p.apply(new M05_KnightTour.Restart())&&p.count==1&&p.x==0&&p.y==0,2,p.count);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I096: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
