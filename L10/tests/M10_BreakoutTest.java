import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M10_BreakoutTest {
    static int checks;
    static final String[] CASES = {
        "Paused, queued or normal input respects the stated tick boundary.",
        "An active tick changes the model exactly once; a finished/paused tick cannot drift.",
        "Invalid boundaries, restart or input cleanup remain correct."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I048: " + CASES[example] + " | actual=" + actual);
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
            var p = new M10_Breakout();
            p.restart();double x=p.ballX,y=p.ballY;p.step();check(Math.abs(p.ballX-x-3.2)<1e-9&&Math.abs(p.ballY-y+3.5)<1e-9,0,p.ballX);p.state="GAME OVER";x=p.ballX;y=p.ballY;p.step();check(p.ballX==x&&p.ballY==y&&p.lives==3,1,p.ballY);p.state="YOU WIN";p.step();check(p.ballX==x&&p.ballY==y,1,p.ballX);p.restart();check(p.state.equals("PLAY")&&p.lives==3&&p.score==0,2,p.state);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I048: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
