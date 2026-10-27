import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M03_FlipbookStudioTest {
    static int checks;
    static final String[] CASES = {
        "Dvě undo obnoví poslední dvě barvy v opačném pořadí.",
        "Undo s prázdnou historií vrátí false.",
        "Undo míří na uložený snímek; restart historii smaže."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I080: " + CASES[example] + " | actual=" + actual);
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
            var p = new M03_FlipbookStudio();
            p.restart();p.paintCell(2,3,1);p.paintCell(2,3,4);
            check(p.undo() && p.frames[0][3][2]==1 && p.history.size()==1,0,p.frames[0][3][2]);
            check(p.undo() && p.frames[0][3][2]==0 && p.history.isEmpty(),0,p.frames[0][3][2]);
            check(!p.undo(),1,p.history);
            p.edited=2;p.paintCell(1,4,3);p.edited=0;p.undo();check(p.frames[2][4][1]==0,2,p.frames[2][4][1]);
            p.paintCell(0,0,2);p.restart();check(p.history.isEmpty()&&!p.undo(),2,p.history);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I080: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
