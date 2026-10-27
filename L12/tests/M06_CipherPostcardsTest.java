import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class M06_CipherPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "LEMON transforms ATTACK AT DAWN! into LXFOPV EF RNHR!.",
        "Decoding uses the same carried keyword and restores the text.",
        "Other alternatives, punctuation and invalid keys retain their contracts."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I131: " + CASES[example] + " | actual=" + actual);
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
            var p = new M06_CipherPostcards();
            var cipher=new M06_CipherPostcards.Vigenere("LEMON");String coded=p.transform("ATTACK AT DAWN!",cipher,false);
            check(coded.equals("LXFOPV EF RNHR!"),0,coded);
            check(p.transform(coded,cipher,true).equals("ATTACK AT DAWN!"),1,coded);
            var orange=new M06_CipherPostcards.Vigenere("ORANGE");String other=p.transform("ATTACK AT DAWN!",orange,false);
            check(other.equals("OKTNIO OK DNCR!")&&p.transform(other,orange,true).equals("ATTACK AT DAWN!"),1,other);
            check(p.transform("AbZ",new M06_CipherPostcards.Caesar(3),false).equals("DeC"),2,"Caesar");
            check(p.transform("A!",new M06_CipherPostcards.Substitution("ZYXWVUTSRQPONMLKJIHGFEDCBA"),false).equals("Z!"),2,"Substitution");
            reject(()->p.transform("A",new M06_CipherPostcards.Vigenere("123"),false),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I131: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
