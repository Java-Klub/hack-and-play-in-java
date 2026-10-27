import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M09_PongTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I047: " + CASES[example] + " | actual=" + actual);
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
            var p = new M09_Pong();
            p.restart();double x=p.ballX,y=p.ballY;p.step();check(p.ballX==x+4&&p.ballY==y+2.5,0,p.ballX);p.winner=1;x=p.ballX;y=p.ballY;p.leftDown=true;p.step();check(p.ballX==x&&p.ballY==y&&p.leftY==160,1,p.ballX);p.clearKeys();check(!p.leftDown&&!p.leftUp&&!p.rightUp&&!p.rightDown,2,p.leftDown);p.restart();check(p.winner==0&&p.leftScore==0&&p.rightScore==0,2,p.winner);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I047: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
