import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_CafeWallLab.java`
/// Kontroly volají testovací metody přímo v této JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_CafeWallLab", "L01.01 CafeWallLab · F01", () -> M01_CafeWallLabTest.main(new String[0]));
        run("M02_MissingTriangle", "L01.02 MissingTriangle · F01", () -> M02_MissingTriangleTest.main(new String[0]));
        run("M03_TypeMelody", "L01.03 TypeMelody · F02", () -> M03_TypeMelodyTest.main(new String[0]));
        run("M04_BitmapBanner", "L01.04 BitmapBanner · F02", () -> M04_BitmapBannerTest.main(new String[0]));
        run("M05_ProgramMirror", "L01.05 ProgramMirror · F02", () -> M05_ProgramMirrorTest.main(new String[0]));
        run("M06_HigherOrLower", "L01.06 HigherOrLower · F03", () -> M06_HigherOrLowerTest.main(new String[0]));
        run("M07_SteppingFeet", "L01.07 SteppingFeet · F03", () -> M07_SteppingFeetTest.main(new String[0]));
        run("M08_SunflowerSpiral", "L01.08 SunflowerSpiral · F03", () -> M08_SunflowerSpiralTest.main(new String[0]));
        run("M09_PixelMonsters", "L01.09 PixelMonsters · F03", () -> M09_PixelMonstersTest.main(new String[0]));
        if (checked == 0) throw new IllegalArgumentException("Unknown game: " + selected);
        IO.println(checked + " missions; " + failed + " still need repair.");
        if (failed > 0) System.exit(1);
    }

    void run(String game, String label, Test test) {
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + label);
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + label + ": " + failure.getMessage());
        }
    }
}

class M01_CafeWallLabTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I097 F01: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_CafeWallLab();
        game.restart();
        var image = new BufferedImage(640, 450, BufferedImage.TYPE_INT_RGB);
        var pen = image.createGraphics();
        game.paintWall(pen);
        pen.dispose();
        check(image.getRGB(100, 50) != new Color(145, 145, 145).getRGB(), "wall tiles must remain visible above the background");
        check(image.getRGB(5, 420) == new Color(145, 145, 145).getRGB(), "the unused border keeps its background");
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_MissingTriangleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I099 F01: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_MissingTriangle();
        game.restart();
        var image = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
        var pen = image.createGraphics();
        game.paintDisc(pen, 0);
        pen.dispose();
        check(image.getRGB(300, 100) == Color.WHITE.getRGB(), "the top disc must keep its white bite");
        check(image.getRGB(300, 30) == Color.BLACK.getRGB(), "the uncut part remains black");
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_TypeMelodyTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I101 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_TypeMelody();
        check(game.defaultMessage().equals("JAVA"), "the opening melody must use the literal JAVA");
        byte[] audio = game.synthesize(game.defaultMessage(), 42);
        boolean audible = false;
        for (byte sample : audio) {
            audible |= sample != 0;
        }
        check(audible, "letters generate actual non-silent audio samples");
        check(audio.length == 4 * 2205 * 2, "four characters retain four time slots");
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_BitmapBannerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I121 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_BitmapBanner();
        check(game.bannerMessage().equals("JAVA CLUB!"), "the default banner must say JAVA CLUB!");
        String pixels = game.banner(game.bannerMessage(), '#');
        check(pixels.contains("#") && pixels.lines().count() == 5, "the message produces five visible pixel rows");
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_ProgramMirrorTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I136 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_ProgramMirror();
        check(game.templateLine().equals("IO.println(\"HELLO\");"), "the printed statement needs literal quotation marks around HELLO");
        check(!game.templateLine().equals("HELLO"), "the template is a printed Java statement, not its output");
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_HigherOrLowerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I032 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_HigherOrLower();
        game.reset();
        game.current = 2;
        game.deck = new ArrayDeque<>(List.of(3, 4, 5));
        game.command("h");
        check(game.outcome.isEmpty(), "one correct guess must not finish the ten-point round");
        check(game.target == 10, "one named target supplies the round goal");
        game.correct = 9;
        game.command("h");
        check(!game.outcome.isEmpty() && game.correct == 10, "the tenth correct guess ends the round");
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_SteppingFeetTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I098 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_SteppingFeet();
        game.restart();
        check(game.x == 80, "the later assignment must leave shared x at 80");
        check(game.paused, "the prepared animation still waits for Space");
        game.paused = false;
        game.step();
        check(Math.abs(game.x - 81.6) < 1e-8, "both supplied feet continue from the repaired shared coordinate");
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_SunflowerSpiralTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I110 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_SunflowerSpiral();
        check(Math.abs(game.defaultAngle - 137.5) < 1e-8, "the shared default angle must be 137.5 degrees");
        double[] second = game.position(2, game.defaultAngle);
        check(Math.abs(second[1] - 250) > 1, "the default must spread seeds beyond a single horizontal spoke");
        check(game.render(42, game.defaultAngle).getWidth() == 500, "the actual PNG renderer accepts the repaired default");
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_PixelMonstersTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I113 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_PixelMonsters();
        var image = game.render(42);
        check(image.getRGB(5 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "left eye must read the shared white eyeColour");
        check(image.getRGB(10 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "right eye must read that same white eyeColour");
        check(game.eyeColour == Color.WHITE.getRGB(), "one named value controls both eyes");
        System.out.println("PASS " + checks + " checks");
    }
}
