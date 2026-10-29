import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_ReactionDiffusionSkins.java`
/// Checks call the test methods directly in this JVM.
/// Games remain compact sources; the checks below access them through reflection.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_ReactionDiffusionSkins", "L12.01 ReactionDiffusionSkins · F34", () -> M01_ReactionDiffusionSkinsTest.main(new String[0]));
        run("M02_FlockPlayground", "L12.02 FlockPlayground · F34", () -> M02_FlockPlaygroundTest.main(new String[0]));
        run("M03_PhasingRhythms", "L12.03 PhasingRhythms · F34", () -> M03_PhasingRhythmsTest.main(new String[0]));
        run("M04_PendulumPainter", "L12.04 PendulumPainter · F34", () -> M04_PendulumPainterTest.main(new String[0]));
        run("M05_KnightTour", "L12.05 KnightTour · F35", () -> M05_KnightTourTest.main(new String[0]));
        run("M06_CipherPostcards", "L12.06 CipherPostcards · F35", () -> M06_CipherPostcardsTest.main(new String[0]));
        run("M07_RetroSoundForge", "L12.07 RetroSoundForge · F35", () -> M07_RetroSoundForgeTest.main(new String[0]));
        run("M08_FireflySynchrony", "L12.08 FireflySynchrony · F35", () -> M08_FireflySynchronyTest.main(new String[0]));
        run("M09_CrystalGrowth", "L12.09 CrystalGrowth · F36", () -> M09_CrystalGrowthTest.main(new String[0]));
        run("M10_PictureMusic", "L12.10 PictureMusic · F36", () -> M10_PictureMusicTest.main(new String[0]));
        run("M11_SecretBeeps", "L12.11 SecretBeeps · F36", () -> M11_SecretBeepsTest.main(new String[0]));
        run("M12_MountainPostcards", "L12.12 MountainPostcards · F36", () -> M12_MountainPostcardsTest.main(new String[0]));
        if (checked == 0) throw new IllegalArgumentException("Unknown game: " + selected);
        IO.println(checked + " missions; " + failed + " mission goals remaining.");
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

@SuppressWarnings("unchecked")
class M01_ReactionDiffusionSkinsTest {
    static int checks;
    static final String[] CASES = {
        "The experiment uses the three specified recipes with equal seed and duration.",
        "Swapping the role changes the actual images rather than only labels.",
        "The original role is restored on success and failure."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.01: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("M01_ReactionDiffusionSkins");
        Object originalRole = Reflect.function("M01_ReactionDiffusionSkins$Chemistry", values -> .03);
        Reflect.set(p, "chemistry", originalRole);
        BufferedImage[] pictures = (BufferedImage[]) Reflect.call(p, "compareSkins", 37L, 60);
        check(pictures.length == 3, 0, pictures.length);
        var expected = Reflect.create("M01_ReactionDiffusionSkins");
        for (int i = 0; i < 3; i++) {
            final int recipe = i;
            Reflect.set(expected, "chemistry", Reflect.function("M01_ReactionDiffusionSkins$Chemistry", values -> {
                double a = (double) values[0];
                double b = (double) values[1];
                return recipe == 0 ? a * b * b : recipe == 1 ? .5 * a * b * b : 0;
            }));
            check(hash(pictures[i]) == hash((BufferedImage) Reflect.call(expected, "render", 37L, 60.0)), 0, "recipe " + i);
        }
        check(hash(pictures[0]) != hash(pictures[1]) && hash(pictures[1]) != hash(pictures[2]), 1, "three observable results");
        check(Reflect.get(p, "chemistry") == originalRole, 2, "restored after success");
        reject(() -> Reflect.call(p, "compareSkins", 37L, 0), 2);
        check(Reflect.get(p, "chemistry") == originalRole, 2, "restored after failure");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_FlockPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Curious uses the exact negative of the Shy response.",
        "Switching preserves bird state, pause and the obstacle; switching twice restores Shy.",
        "The actual movement observes the role and a new flock preserves its selected personality."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.02: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("M02_FlockPlayground");
        Reflect.call(p, "reset", 42L);
        double[] oldX = ((double[]) Reflect.get(p, "x")).clone();
        double[] oldY = ((double[]) Reflect.get(p, "y")).clone();
        double[] oldVx = ((double[]) Reflect.get(p, "vx")).clone();
        double[] oldVy = ((double[]) Reflect.get(p, "vy")).clone();
        Reflect.set(p, "running", false);
        Reflect.set(p, "obstacleX", 100.0);
        Reflect.call(p, "changePersonality");
        check(Math.abs((double) Reflect.call(p, "obstaclePush", 37.5) + .1) < 1e-12, 0, Reflect.call(p, "obstaclePush", 37.5));
        check((double) Reflect.call(p, "obstaclePush", 75.0) == 0, 0, "same radius boundary");
        check(Arrays.equals(oldX,(double[])Reflect.get(p,"x")) && Arrays.equals(oldY,(double[])Reflect.get(p,"y")) && Arrays.equals(oldVx,(double[])Reflect.get(p,"vx")) && Arrays.equals(oldVy,(double[])Reflect.get(p,"vy")), 1, "all bird arrays preserved");
        check(!(boolean)Reflect.get(p,"running") && (double)Reflect.get(p,"obstacleX") == 100, 1, "pause and obstacle preserved");
        Reflect.call(p, "reset", 23L);
        check((boolean)Reflect.get(p,"curious") && (double)Reflect.call(p,"obstaclePush",37.5) < 0, 2, "new flock, same personality");
        Reflect.call(p, "changePersonality");
        check(!(boolean)Reflect.get(p,"curious") && Math.abs((double)Reflect.call(p,"obstaclePush",37.5) - .1) < 1e-12, 1, "back to Shy");
        var other = Reflect.create("M02_FlockPlayground");
        Reflect.call(p,"reset",42L);Reflect.call(other,"reset",42L);
        Reflect.set(p,"obstacleX",300.0);Reflect.set(other,"obstacleX",300.0);
        for (Object bird : new Object[]{p,other}) {
            ((double[])Reflect.get(bird,"x"))[0]=310;
            ((double[])Reflect.get(bird,"y"))[0]=200;
            ((double[])Reflect.get(bird,"vx"))[0]=0;
            ((double[])Reflect.get(bird,"vy"))[0]=0;
        }
        Reflect.call(p,"changePersonality");
        Reflect.call(p,"step");Reflect.call(other,"step");
        check(((double[])Reflect.get(p,"vx"))[0] < ((double[])Reflect.get(other,"vx"))[0], 2, "Curious changes real steering");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_PhasingRhythmsTest {
    static int checks;
    static final String[] CASES = {
        "Both role results contribute with gain 0.17.",
        "Opposite replacement voices cancel.",
        "Actual synthesis uses the composition and keeps duration guards."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.03: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M03_PhasingRhythms");
            Object authored = Reflect.call(p, "makeVoice", .2, .5);
            double expectedTap = .5 * (double) Reflect.call(p, "tap", .001, .2);
            check(Math.abs((double) Reflect.call(authored, "sample", .001) - expectedTap) < 1e-12, 0, "authored Voice gain");
            check((double) Reflect.call(Reflect.call(p, "makeVoice", .2, 0.0), "sample", .001) == 0, 0, "zero gain");
            double expectedMix = .17 * ((double) Reflect.call(p, "tap", .001, .12) + .7 * (double) Reflect.call(p, "tap", .001, .13));
            check(Math.abs((double) Reflect.call(p, "mix", .001) - expectedMix) < 1e-12, 0, "both authored voices installed");
            reject(() -> Reflect.call(p, "makeVoice", .1, 2.0), 0);
            Reflect.set(p, "first", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .2; }));Reflect.set(p, "second", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .5; }));check(Math.abs(((double) Reflect.call(p, "mix", .4))-.119)<1e-12,0,((double) Reflect.call(p, "mix", .4)));
            Reflect.set(p, "first", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return t+.2; }));Reflect.set(p, "second", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .5-2*t; }));
            check(Math.abs(((double) Reflect.call(p, "mix", .4))-.051)<1e-12&&Math.abs(((double) Reflect.call(p, "mix", .8))+.017)<1e-12,0,((double) Reflect.call(p, "mix", .8)));
            Reflect.set(p, "first", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return -.2; }));Reflect.set(p, "second", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .2; }));check(Math.abs(((double) Reflect.call(p, "mix", .8)))<1e-12,1,((double) Reflect.call(p, "mix", .8)));
            Reflect.set(p, "first", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .25; }));Reflect.set(p, "second", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return 0; }));var samples=((double[]) Reflect.call(p, "signal", 1));check(samples.length==22050&&Math.abs(samples[100]-.0425)<1e-12,2,samples[100]);
            Reflect.set(p, "first", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return t*.25; }));Reflect.set(p, "second", Reflect.function("M03_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return -t*.1; }));samples=((double[]) Reflect.call(p, "signal", 1));
            check(Math.abs(samples[100]-.17*.15*100/22050)<1e-12&&Math.abs(samples[1000]-.17*.15*1000/22050)<1e-12,2,samples[1000]);
            reject(()->Reflect.call(p, "signal", .5),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.03: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_PendulumPainterTest {
    static int checks;
    static final String[] CASES = {
        "The default role sees current angle, length and gravity.",
        "A constant alternate acceleration changes the actual velocity.",
        "A zero alternate rule keeps an initially resting angle fixed."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.04: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M04_PendulumPainter");
            Object authored = Reflect.call(p, "makeAcceleration");
            Reflect.set(p, "gravity", 200.0);
            check(Math.abs((double) Reflect.call(authored, "at", Math.PI / 2, 100.0) + 2) < 1e-12, 0, "authored restoring rule");
            Reflect.set(p, "gravity", 100.0);
            check(Math.abs((double) Reflect.call(authored, "at", Math.PI / 2, 100.0) + 1) < 1e-12, 0, "current gravity read by existing rule");
            Reflect.set(p, "angle", Math.PI / 2);
            Reflect.set(p, "length", 100.0);
            check(Math.abs((double) Reflect.call(p, "accelerationNow") + 1) < 1e-12, 0, "authored rule installed");
            Reflect.call(p, "reset", 42);double expected=-((double) Reflect.get(p, "gravity"))/((double) Reflect.get(p, "length"))*Math.sin(((double) Reflect.get(p, "angle")));check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-expected)<1e-12,0,((double) Reflect.call(p, "accelerationNow")));
            Reflect.set(p, "angle", .4);Reflect.set(p, "length", 120);expected=-((double) Reflect.get(p, "gravity"))/((double) Reflect.get(p, "length"))*Math.sin(((double) Reflect.get(p, "angle")));check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-expected)<1e-12,0,((double) Reflect.call(p, "accelerationNow")));
            Reflect.set(p, "acceleration", Reflect.function("M04_PendulumPainter$Acceleration", values -> { double angle = (double) values[0]; double length = (double) values[1]; return angle+length/100; }));
            check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-1.6)<1e-12,1,((double) Reflect.call(p, "accelerationNow")));
            Reflect.set(p, "angle", -.2);Reflect.set(p, "length", 50);check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-.3)<1e-12,1,((double) Reflect.call(p, "accelerationNow")));
            Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "velocity"))-.3*.03*.999)<1e-12,1,((double) Reflect.get(p, "velocity")));
            Reflect.call(p, "reset", 42);
            Reflect.set(p, "acceleration", Reflect.function("M04_PendulumPainter$Acceleration", values -> { double angle = (double) values[0]; double length = (double) values[1]; return 2; }));Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "velocity"))-.05994)<1e-12,1,((double) Reflect.get(p, "velocity")));
            Reflect.set(p, "acceleration", Reflect.function("M04_PendulumPainter$Acceleration", values -> { double angle = (double) values[0]; double length = (double) values[1]; return 0; }));Reflect.call(p, "reset", 42);double before=((double) Reflect.get(p, "angle"));Reflect.call(p, "step");check(((double) Reflect.get(p, "angle"))==before&&((int) Reflect.get(p, "ticks"))==1,2,((double) Reflect.get(p, "angle")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.04: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_KnightTourTest {
    static int checks;
    static final String[] CASES = {
        "Move preserves the record component order.",
        "Further legal moves advance count once.",
        "Bad coordinates preserve state and Restart restores the initial board."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.05: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M05_KnightTour");
            Reflect.call(p, "restart");check(((boolean) Reflect.call(p, "apply", Reflect.create("M05_KnightTour$Move", 1, 2)))&&((int) Reflect.get(p, "x"))==1&&((int) Reflect.get(p, "y"))==2,0,((int) Reflect.get(p, "x"))+","+((int) Reflect.get(p, "y")));
            check(((boolean) Reflect.call(p, "apply", Reflect.create("M05_KnightTour$Move", 3, 3)))&&((int) Reflect.get(p, "count"))==3,1,((int) Reflect.get(p, "count")));
            int x=((int) Reflect.get(p, "x")),y=((int) Reflect.get(p, "y"));check(!((boolean) Reflect.call(p, "apply", Reflect.create("M05_KnightTour$Move", 99, 0)))&&((int) Reflect.get(p, "x"))==x&&((int) Reflect.get(p, "y"))==y,2,((int) Reflect.get(p, "count")));
            check(((boolean) Reflect.call(p, "apply", Reflect.create("M05_KnightTour$Restart")))&&((int) Reflect.get(p, "count"))==1&&((int) Reflect.get(p, "x"))==0&&((int) Reflect.get(p, "y"))==0,2,((int) Reflect.get(p, "count")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.05: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_CipherPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "The crack mode enumerates all 26 decoded shifts with exact labels.",
        "The tool handles different keys, case and punctuation without guessing a winner.",
        "The three normal cipher alternatives still encode and decode correctly."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.06: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("M06_CipherPostcards");
        Object card;
        try {
            card = Reflect.call(p, "chooseCipher", "crack", "");
        } catch (IllegalArgumentException missing) {
            check(false, 0, "crack mode rejected: " + missing.getMessage());
            return;
        }
        String report = (String) Reflect.call(p, "transform", "KHOOR", card, false);
        String[] lines = report.split("\n", -1);
        check(lines.length == 26 && lines[0].equals("Shift 0: KHOOR") && lines[3].equals("Shift 3: HELLO") && lines[25].startsWith("Shift 25: "), 0, report);
        for (int key = 0; key < 26; key++) {
            String encoded = (String) Reflect.call(p, "applyText", "Meet π at 5!", "caesar", Integer.toString(key), false);
            String decoded = (String) Reflect.call(p, "transform", encoded, card, false);
            check(decoded.split("\n", -1)[key].equals("Shift " + key + ": Meet π at 5!"), 1, "key " + key);
        }
        for (String mode : new String[]{"caesar", "substitution", "vigenere"}) {
            String key = mode.equals("caesar") ? "7" : mode.equals("substitution") ? "ZYXWVUTSRQPONMLKJIHGFEDCBA" : "LEMON";
            Object normal = Reflect.call(p, "chooseCipher", mode, key);
            String encoded = (String) Reflect.call(p, "transform", "Hello, Moon!", normal, false);
            check(Reflect.call(p, "transform", encoded, normal, true).equals("Hello, Moon!"), 2, mode);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_RetroSoundForgeTest {
    static int checks;
    static final String[] CASES = {
        "Wobble uses arbitrary carried base and depth values.",
        "Jump and Laser retain their independent formulas.",
        "Invalid time is rejected and real sound presets differ."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.07: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M07_RetroSoundForge");
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 300, 90), 1.0/32))-390)<1e-9,0,((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 300, 90), 1.0/32)));
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 300, 90), 3.0/32))-210)<1e-9,0,((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 300, 90), 3.0/32)));
            check(((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 410, 25), 0))==410,0,((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 410, 25), 0)));
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Jump", 100, 200), .5))-200)<1e-9,1,"jump");
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Laser", 100, .25), 1))-25)<1e-9,1,"laser");
            reject(()->Reflect.call(p, "frequency", Reflect.create("M07_RetroSoundForge$Wobble", 300, 90), Double.NaN),2);
            check(!Arrays.equals(((double[]) Reflect.call(p, "signal", "wobble", .1)),((double[]) Reflect.call(p, "signal", "laser", .1))),2,"different waves");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.07: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_FireflySynchronyTest {
    static int checks;
    static final String[] CASES = {
        "A valid Coupling value reaches the model.",
        "Both the upper boundary and invalid-value preservation work.",
        "Toggle and seeded Restart dispatch to the correct independent rules."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.08: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M08_FireflySynchrony");
            Reflect.call(p, "reset", 42);Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", .35));check(((double) Reflect.get(p, "coupling"))==.35,0,((double) Reflect.get(p, "coupling")));
            Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", 2));check(((double) Reflect.get(p, "coupling"))==2,1,((double) Reflect.get(p, "coupling")));
            Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", 2.1));Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", Double.NaN));check(((double) Reflect.get(p, "coupling"))==2,1,((double) Reflect.get(p, "coupling")));
            for(double invalid:new double[]{-.1,2.1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.NaN}){
                Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", .35));Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", invalid));check(((double) Reflect.get(p, "coupling"))==.35,1,((double) Reflect.get(p, "coupling")));
            }
            Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Coupling", 0));check(((double) Reflect.get(p, "coupling"))==0,1,((double) Reflect.get(p, "coupling")));
            boolean run=((boolean) Reflect.get(p, "running"));Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Toggle"));check(((boolean) Reflect.get(p, "running"))!=run,2,((boolean) Reflect.get(p, "running")));
            var expected=Reflect.create("M08_FireflySynchrony");Reflect.call(expected, "reset", 17);Reflect.call(p, "apply", Reflect.create("M08_FireflySynchrony$Restart", 17));check(Arrays.equals(((double[]) Reflect.get(p, "phase")),((double[]) Reflect.get(expected, "phase"))),2,((double[]) Reflect.get(p, "phase"))[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.08: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_CrystalGrowthTest {
    static int checks;
    static final String[] CASES = {
        "The added mode sees a diagonal-only neighbour.",
        "The old four-neighbour mode still excludes diagonals and accepts orthogonal contact.",
        "Edges/empty data are safe and real crystal growth still attaches cells."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.09: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M09_CrystalGrowth");
            boolean[][] grid=new boolean[3][3];grid[0][0]=true;
            check(((boolean) Reflect.call(p, "touching", grid, 1, 1, true)),0,"diagonal");check(!((boolean) Reflect.call(p, "touching", grid, 1, 1, false)),1,"old mode");
            grid[0][1]=true;check(((boolean) Reflect.call(p, "touching", grid, 1, 1, false)),1,"orthogonal");
            check(((boolean) Reflect.call(p, "touching", grid, 0, 1, true)),2,"edge");check(!((boolean) Reflect.call(p, "touching", new boolean[0][0], 0, 0, true)),2,"empty");
            Reflect.set(p, "diagonalGrowth", true);var grown=((boolean[][]) Reflect.call(p, "grow", 42, 20));int cells=0;for(var row:grown)for(boolean cell:row)if(cell)cells++;check(cells>1&&cells<=21,2,cells);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.09: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_PictureMusicTest {
    static int checks;
    static final String[] CASES = {
        "The new +12 shift doubles frequency and changes actual samples.",
        "Zero and -12 retain the old mapping and octave relation.",
        "Invalid rows and shifts are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.10: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M10_PictureMusic");
            double base=((double) Reflect.call(p, "pitchFrequency", 4, 0));check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 4, 12))-base*2)<1e-9,0,((double) Reflect.call(p, "pitchFrequency", 4, 12)));
            check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 4, -12))-base/2)<1e-9,1,((double) Reflect.call(p, "pitchFrequency", 4, -12)));
            check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 11, 0))-220)<1e-9,1,((double) Reflect.call(p, "pitchFrequency", 11, 0)));
            reject(()->Reflect.call(p, "pitchFrequency", 12, 0),2);reject(()->Reflect.call(p, "pitchFrequency", 0, 13),2);
            var image=((java.awt.image.BufferedImage) Reflect.call(p, "demo"));Reflect.set(p, "transpose", 0);var a=((double[]) Reflect.call(p, "signal", image));Reflect.set(p, "transpose", 12);check(!Arrays.equals(a,((double[]) Reflect.call(p, "signal", image))),0,"transposed WAV");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.10: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_SecretBeepsTest {
    static int checks;
    static final String[] CASES = {
        "The new word gap changes the actual waveform length.",
        "The original seven-unit mode and three-unit letter gap remain unchanged.",
        "Word-gap values outside 3–12 are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.11: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M11_SecretBeeps");
            check(((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E", 10)).equals(List.of(1,-10,1)),0,((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E", 10)));
            check(((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E")).equals(List.of(1,-7,1))&&((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "EE", 10)).equals(List.of(1,-3,1)),1,((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "EE", 10)));
            reject(()->Reflect.call(p, "units", "E E", 2),2);reject(()->Reflect.call(p, "units", "E E", 13),2);
            Reflect.set(p, "wordGap", 7);int original=((double[]) Reflect.call(p, "signal", "E E", 20)).length;Reflect.set(p, "wordGap", 10);check(((double[]) Reflect.call(p, "signal", "E E", 20)).length-original==3*(22050*20/1000),0,((double[]) Reflect.call(p, "signal", "E E", 20)).length);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.11: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M12_MountainPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "The returned ridge is reversed; mirrored and original views differ.",
        "The returned array is independent of the original.",
        "Empty and one-element ridges preserve their meaning."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L12.12: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M12_MountainPostcards");
            double[] input={3,7,2};var result=((double[]) Reflect.call(p, "reverseRidge", input));check(Arrays.equals(result,new double[]{2,7,3}),0,Arrays.toString(result));
            result[0]=99;check(Arrays.equals(input,new double[]{3,7,2}),1,Arrays.toString(input));
            check(((double[]) Reflect.call(p, "reverseRidge", new double[0])).length==0&&Arrays.equals(((double[]) Reflect.call(p, "reverseRidge", new double[]{4})),new double[]{4}),2,"bounds");
            Reflect.set(p, "mirrored", false);var original=((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, .65));Reflect.set(p, "mirrored", true);check(hash(original)!=hash(((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, .65))),0,"mirrored image");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L12.12: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

// Prepared access to compact programs. Tests keep ordinary assertions, arrays and JDK collections.
class Reflect {
    static Class<?> type(String name) {
        return switch (name) {
            case "boolean" -> boolean.class;
            case "byte" -> byte.class;
            case "short" -> short.class;
            case "char" -> char.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "float" -> float.class;
            case "double" -> double.class;
            case "void" -> void.class;
            default -> load(name);
        };
    }

    private static Class<?> load(String name) {
        try {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException missing) {
                int nested = name.indexOf('$');
                if (nested < 0) throw missing;
                Class.forName(name.substring(0, nested));
                return Class.forName(name);
            }
        } catch (ClassNotFoundException failure) {
            throw new IllegalStateException("Cannot find compact program type: " + name, failure);
        }
    }

    static Object create(String name, Object... args) {
        var matches = new ArrayList<Constructor<?>>();
        for (var constructor : type(name).getDeclaredConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) matches.add(constructor);
        }
        var constructor = one(matches, "constructor " + name);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot construct " + name, failure);
        }
    }

    static Object get(Object target, String name) {
        try {
            return field(target, name).get(target instanceof Class<?> ? null : target);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot read field " + name, failure);
        }
    }

    static Object set(Object target, String name, Object value) {
        try {
            var field = field(target, name);
            Object receiver = target instanceof Class<?> ? null : target;
            field.set(receiver, value);
            return field.get(receiver);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot write field " + name, failure);
        }
    }

    private static Field field(Object target, String name) throws NoSuchFieldException {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            try {
                var field = current.getDeclaredField(name);
                if (target instanceof Class<?> && !Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("An instance is required for field " + name);
                }
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException missing) {
                // Continue with inherited fields.
            }
        }
        throw new NoSuchFieldException(owner.getName() + "." + name);
    }

    static Object call(Object target, String name, Object... args) {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        var methods = new LinkedHashMap<String, Method>();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            for (var method : current.getDeclaredMethods()) {
                methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
            }
        }
        for (var method : owner.getMethods()) {
            methods.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
        }
        var matches = new ArrayList<Method>();
        for (var method : methods.values()) {
            if (!method.isBridge() && !method.isSynthetic()
                    && method.getName().equals(name) && accepts(method.getParameterTypes(), args)
                    && (!(target instanceof Class<?>) || Modifier.isStatic(method.getModifiers()))) {
                matches.add(method);
            }
        }
        var method = one(matches, "method " + owner.getName() + "." + name);
        try {
            method.setAccessible(true);
            return method.invoke(target instanceof Class<?> ? null : target, args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot call " + owner.getName() + "." + name, failure);
        }
    }

    static Object array(String component, Object... values) {
        Object result = Array.newInstance(type(component), values.length);
        for (int index = 0; index < values.length; index++) Array.set(result, index, values[index]);
        return result;
    }

    private static boolean accepts(Class<?>[] parameters, Object[] args) {
        if (parameters.length != args.length) return false;
        for (int index = 0; index < parameters.length; index++) {
            Class<?> to = parameters[index];
            Object value = args[index];
            if (value == null) {
                if (to.isPrimitive()) return false;
            } else if (to.isPrimitive()) {
                String targets = switch (value) {
                    case Boolean ignored -> "boolean";
                    case Byte ignored -> "byte short int long float double";
                    case Short ignored -> "short int long float double";
                    case Character ignored -> "char int long float double";
                    case Integer ignored -> "int long float double";
                    case Long ignored -> "long float double";
                    case Float ignored -> "float double";
                    case Double ignored -> "double";
                    default -> "";
                };
                if (!Arrays.asList(targets.split(" ")).contains(to.getName())) return false;
            } else if (!to.isInstance(value)) return false;
        }
        return true;
    }

    private static <T> T one(List<T> matches, String description) {
        if (matches.size() != 1) {
            throw new IllegalStateException((matches.isEmpty() ? "No compatible " : "Ambiguous ") + description);
        }
        return matches.getFirst();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException raise(Throwable failure) throws T {
        throw (T) failure;
    }

    static Object function(String typeName, java.util.function.Function<Object[], Object> body) {
        Class<?> role = type(typeName);
        return java.lang.reflect.Proxy.newProxyInstance(role.getClassLoader(), new Class<?>[]{role},
                (proxy, method, arguments) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" -> proxy == arguments[0];
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "toString" -> "Function(" + typeName + ")";
                            default -> throw new AssertionError(method.getName());
                        };
                    }
                    Object result = body.apply(arguments == null ? new Object[0] : arguments);
                    // All four course roles return double; a constant lambda may box an int.
                    if (method.getReturnType() == double.class) return ((Number) result).doubleValue();
                    return result;
                });
    }
}
