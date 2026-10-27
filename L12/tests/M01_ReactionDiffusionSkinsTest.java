import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M01_ReactionDiffusionSkinsTest {
    static int checks;
    static final String[] CASES = {
        "Výchozí chemistry přijme skutečné aktuální hodnoty cell (buňky).",
        "Výměna implementace interface (rozhraní) změní výsledek.",
        "Skutečný step používá náhradní reakci v obou dalších polích."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I126: " + CASES[example] + " | actual=" + actual);
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
            var p = new M01_ReactionDiffusionSkins();
            p.reset(42);p.a[0][0]=.5;p.b[0][0]=.4;check(Math.abs(p.reactionAt(0,0)-.08)<1e-12,0,p.reactionAt(0,0));
            p.a[3][2]=.7;p.b[3][2]=.2;check(Math.abs(p.reactionAt(2,3)-.028)<1e-12,0,p.reactionAt(2,3));
            p.chemistry=(a,b)->.1*a+.05*b;check(Math.abs(p.reactionAt(2,3)-.08)<1e-12,1,p.reactionAt(2,3));
            double expectedA=Math.clamp(p.a[3][2]+p.laplace(p.a,2,3)-.08+.055*(1-p.a[3][2]),0,1);
            double expectedB=Math.clamp(p.b[3][2]+.5*p.laplace(p.b,2,3)+.08-.117*p.b[3][2],0,1);
            p.step();check(Math.abs(p.a[3][2]-expectedA)<1e-12&&Math.abs(p.b[3][2]-expectedB)<1e-12,2,p.b[3][2]);
            p.chemistry=(a,b)->.07;check(Math.abs(p.reactionAt(0,0)-.07)<1e-12,1,p.reactionAt(0,0));
            for(var row:p.a)Arrays.fill(row,.5);for(var row:p.b)Arrays.fill(row,.4);p.step();
            check(Math.abs(p.a[0][0]-.4575)<1e-9&&Math.abs(p.b[0][0]-.4232)<1e-9,2,p.b[0][0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I126: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
