import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_PixelMonsters.java
/// Tests call compact programs through reflection in this JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P03_PixelMonsters", "13.03 PixelMonsters · F03", () -> P03_PixelMonstersTest.main(new String[0]));
        run("P04_Untangle", "13.04 Untangle · F29", () -> P04_UntangleTest.main(new String[0]));
        run("P05_MirrorArt", "13.05 MirrorArt · F29", () -> P05_MirrorArtTest.main(new String[0]));
        run("P06_Moire", "13.06 Moire · F29", () -> P06_MoireTest.main(new String[0]));
        run("P07_StarTunnel", "13.07 StarTunnel · F29", () -> P07_StarTunnelTest.main(new String[0]));
        run("P10_CrossingRush", "13.10 CrossingRush · F25", () -> P10_CrossingRushTest.main(new String[0]));
        run("P11_Snake", "13.11 Snake · F30", () -> P11_SnakeTest.main(new String[0]));
        run("P14_MiniInvaders", "13.14 MiniInvaders · F25", () -> P14_MiniInvadersTest.main(new String[0]));
        run("P15_SoftLanding", "13.15 SoftLanding · F25", () -> P15_SoftLandingTest.main(new String[0]));
        run("P16_AsteroidDrift", "13.16 AsteroidDrift · F28", () -> P16_AsteroidDriftTest.main(new String[0]));
        run("P17_FireworkPainter", "13.17 FireworkPainter · F28", () -> P17_FireworkPainterTest.main(new String[0]));
        run("P18_ShadowPlayground", "13.18 ShadowPlayground · F28", () -> P18_ShadowPlaygroundTest.main(new String[0]));
        run("P21_TiltTray", "13.21 TiltTray · F25", () -> P21_TiltTrayTest.main(new String[0]));
        run("P22_PlatformDash", "13.22 PlatformDash · F25", () -> P22_PlatformDashTest.main(new String[0]));
        run("P23_Pong", "13.23 Pong · F30", () -> P23_PongTest.main(new String[0]));
        run("P24_OrbitPainter", "13.24 OrbitPainter · F30", () -> P24_OrbitPainterTest.main(new String[0]));
        run("P25_PendulumWaves", "13.25 PendulumWaves · F30", () -> P25_PendulumWavesTest.main(new String[0]));
        run("P28_SpringToy", "13.28 SpringToy · F29", () -> P28_SpringToyTest.main(new String[0]));
        run("P29_Breakout", "13.29 Breakout · F30", () -> P29_BreakoutTest.main(new String[0]));
        run("P30_RippleTank", "13.30 RippleTank · F30", () -> P30_RippleTankTest.main(new String[0]));
        run("P31_PictureMusic", "13.31 PictureMusic · F36", () -> P31_PictureMusicTest.main(new String[0]));
        run("P32_SecretBeeps", "13.32 SecretBeeps · F36", () -> P32_SecretBeepsTest.main(new String[0]));
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
class P03_PixelMonstersTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("13.03 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_PixelMonsters");
        var image = ((java.awt.image.BufferedImage) Reflect.call(game, "render", 42));
        check(image.getRGB(5 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "left eye must read the shared white eyeColour");
        check(image.getRGB(10 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "right eye must read that same white eyeColour");
        check(((int) Reflect.get(game, "eyeColour")) == Color.WHITE.getRGB(), "one named value controls both eyes");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_UntangleTest {
    static int checks;
    static final String[] CASES = {
        "An asymmetric point draws a correctly centred node.",
        "Drawing does not change Point/model values.",
        "The real full scene uses the repaired helper."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.04: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P04_Untangle");
            Reflect.call(p, "restart");var point=new Point(120,70);var image=draw(g->Reflect.call(p, "drawNode", g, point, 3),600,420);
            check((image.getRGB(128,70)&0xFFFFFF)==0x23375A,0,Integer.toHexString(image.getRGB(128,70)));
            check(point.equals(new Point(120,70)),1,point);
            ((java.awt.Point[]) Reflect.get(p, "points"))[0]=new Point(80,90);var scene=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),600,420);
            check((scene.getRGB(88,90)&0xFFFFFF)==0x23375A,2,Integer.toHexString(scene.getRGB(88,90)));
            var copy=((java.awt.Point[]) Reflect.get(p, "points"))[0].getLocation();draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),600,420);check(((java.awt.Point[]) Reflect.get(p, "points"))[0].equals(copy),1,((java.awt.Point[]) Reflect.get(p, "points"))[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.04: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P05_MirrorArtTest {
    static int checks;
    static final String[] CASES = {
        "The normal asymmetric point has the specified screen position.",
        "Centre/rotation or near-depth boundary stays correct.",
        "The real drawing uses the transform and preserves model state."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.05: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P05_MirrorArt");
            Reflect.call(p, "restart");var c=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 320, 220, 0, false));check(c.distance(320,220)<1e-9,0,c);Reflect.set(p, "axes", 4);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 350, 220, 1, false));check(q.distance(320,250)<1e-9,1,q);var r=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 320, 250, 0, true));check(r.distance(320,190)<1e-9,1,r);long before=hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")));Reflect.call(p, "stroke", 350, 220, 360, 230);check(hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")))!=before,2,hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas"))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.05: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P06_MoireTest {
    static int checks;
    static final String[] CASES = {
        "The normal asymmetric point has the specified screen position.",
        "Centre/rotation or near-depth boundary stays correct.",
        "The real drawing uses the transform and preserves model state."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.06: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P06_Moire");
            Reflect.call(p, "restart");var c=((java.awt.geom.Point2D.Double) Reflect.call(p, "gridPoint", 0, 0, 0));check(c.distance(320,220)<1e-9,0,c);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "gridPoint", 1, 0, Math.PI/2));check(q.distance(320,242)<1e-8,1,q);double old=((double) Reflect.get(p, "angle"));var a=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));var b=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(((double) Reflect.get(p, "angle"))==old&&hash(a)==hash(b),2,((double) Reflect.get(p, "angle")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.06: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_StarTunnelTest {
    static int checks;
    static final String[] CASES = {
        "The normal asymmetric point has the specified screen position.",
        "Centre/rotation or near-depth boundary stays correct.",
        "The real drawing uses the transform and preserves model state."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.07: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P07_StarTunnel");
            Reflect.call(p, "restart");var star=Reflect.create("P07_StarTunnel$Star", 10, 20, 100);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "project", star, 100));check(q.distance(((double) Reflect.get(p, "centreX"))+26,((double) Reflect.get(p, "centreY"))+52)<1e-9,0,q);var near=((java.awt.geom.Point2D.Double) Reflect.call(p, "project", star, 0));check(near.distance(((double) Reflect.get(p, "centreX"))+520,((double) Reflect.get(p, "centreY"))+1040)<1e-9,1,near);var before=List.copyOf(((java.util.ArrayList<Object>) Reflect.get(p, "stars")));draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(((java.util.ArrayList<Object>) Reflect.get(p, "stars")).equals(before),2,((java.util.ArrayList<Object>) Reflect.get(p, "stars")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.07: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P10_CrossingRushTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
        + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
        + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P10_CrossingRush");
        Reflect.call(g, "restart");
        Reflect.call(g, "hop", -1000, 0);
        equal(12, ((int) Reflect.get(g, "x")), "13.10/F25: whole player remains inside left boundary");
        Reflect.call(g, "hop", 1000, 0);
        equal(588, ((int) Reflect.get(g, "x")), "13.10/F25: whole player remains inside right boundary");
        ((java.util.ArrayList<Object>) Reflect.get(g, "cars")).clear();
        Reflect.call(g, "hop", 0, -500);
        expect(((boolean) Reflect.get(g, "won")), "13.10/F25: legitimate top goal still wins");
        int x = ((int) Reflect.get(g, "x"));
        Reflect.call(g, "hop", -30, 0);
        equal(x, ((int) Reflect.get(g, "x")), "13.10/F25: terminal round does not keep moving");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_SnakeTest {
    static int checks;
    static final String[] CASES = {
        "Paused, queued or normal input respects the stated tick boundary.",
        "An active tick changes the model exactly once; a finished/paused tick cannot drift.",
        "Invalid boundaries, restart or input cleanup remain correct."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.11: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P11_Snake");
            Reflect.call(p, "restart");var head=((java.util.List<Object>) Reflect.get(p, "body")).getFirst();int dx=((int) Reflect.get(p, "dx")),dy=((int) Reflect.get(p, "dy"));Reflect.call(p, "turn", 0, -1);check(((int) Reflect.get(p, "dx"))==dx&&((int) Reflect.get(p, "dy"))==dy&&((boolean) Reflect.call(((java.util.List<Object>) Reflect.get(p, "body")).getFirst(), "equals", head)),0,((java.util.List<Object>) Reflect.get(p, "body")).getFirst());Reflect.call(p, "turn", -1, 0);Reflect.call(p, "step");check(((boolean) Reflect.call(((java.util.List<Object>) Reflect.get(p, "body")).getFirst(), "equals", Reflect.create("P11_Snake$Cell", ((int) Reflect.call(head, "x")), ((int) Reflect.call(head, "y"))-1))),1,((java.util.List<Object>) Reflect.get(p, "body")).getFirst());Reflect.set(p, "lost", true);var stopped=List.copyOf(((java.util.List<Object>) Reflect.get(p, "body")));Reflect.call(p, "step");check(((java.util.List<Object>) Reflect.get(p, "body")).equals(stopped),2,((java.util.List<Object>) Reflect.get(p, "body")));Reflect.call(p, "restart");check(!((boolean) Reflect.get(p, "lost"))&&((int) Reflect.get(p, "score"))==0,2,((int) Reflect.get(p, "score")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.11: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P14_MiniInvadersTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P14_MiniInvaders");
        Reflect.call(g, "restart");
        Reflect.set(g, "speedX", 0.0);
        Reflect.set(g, "fire", true);
        Reflect.call(g, "step");
        Reflect.set(g, "fire", false);
        Reflect.set(g, "bulletX", (double) Reflect.get(g, "formationX") + 10);
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 2 * 34 + 17);
        Reflect.call(g, "step");
        equal(1, Reflect.get(g, "score"), "13.14: first real hit scores once");
        expect((boolean) Reflect.get(g, "bulletActive"), "13.14: piercing pulse survives its first alien");
        Reflect.call(g, "hitAlien", 2, 0);
        equal(1, Reflect.get(g, "score"), "13.14: an already removed alien cannot score again");
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 34 + 17);
        Reflect.call(g, "step");
        equal(2, Reflect.get(g, "score"), "13.14: the same pulse reaches a second alien");
        expect(!(boolean) Reflect.get(g, "bulletActive"), "13.14: the two-hit pulse ends after its second hit");
        Reflect.call(g, "hitAlien", 0, 0);
        equal(2, Reflect.get(g, "score"), "13.14: no third score from a spent pulse");
        Reflect.set(g, "fire", true); Reflect.call(g, "step"); Reflect.set(g, "fire", false);
        Reflect.call(g, "hitAlien", 0, 0);
        expect((boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 3, "13.14: a newly fired pulse receives a fresh allowance");
        Reflect.call(g, "hitAlien", 0, 1);
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 4, "13.14: that new allowance is also finite");
        Reflect.call(g, "restart");
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 0 && (int) Reflect.call(g, "countAliens") == 15, "13.14: restart resets the battle");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_SoftLandingTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P15_SoftLanding");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "fuel", 0);
        Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "13.15: empty training tank still brakes against gravity");
        equal(0, Reflect.get(g, "fuel"), "13.15: training leaves the counter unchanged at zero");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "left", true); Reflect.set(g, "right", true); Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        equal(240, Reflect.get(g, "fuel"), "13.15: combined thrust does not spend training fuel");
        expect(Math.abs((double) Reflect.get(g, "velocityX") - 1.2) < 1e-9 && Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "13.15: opposing engines cancel while vertical thrust works");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") - 0.04) < 1e-9, "13.15: no key means gravity only");
        Reflect.set(g, "state", "CRASH");
        double y = (double) Reflect.get(g, "y");
        Reflect.set(g, "up", true); Reflect.call(g, "step");
        equal(y, Reflect.get(g, "y"), "13.15: a crash remains terminal");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        expect(!(boolean) Reflect.get(g, "left") && !(boolean) Reflect.get(g, "right") && !(boolean) Reflect.get(g, "up"), "13.15: restart releases thrust");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_AsteroidDriftTest {
    static int checks;
    static final String[] CASES = {
        "F fires through the actual held-key path and keeps cooldown.",
        "Space brakes both velocity components without firing.",
        "Restart and a lost game keep their original boundaries."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.16: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P16_AsteroidDrift");
        Reflect.call(p, "restart");
        Reflect.call(p, "key", java.awt.event.KeyEvent.VK_F, true);
        Reflect.call(p, "step");
        check(((java.util.List<?>) Reflect.get(p, "shots")).size() == 1, 0, "shots after F: " + ((java.util.List<?>) Reflect.get(p, "shots")).size());
        Reflect.call(p, "step");
        check(((java.util.List<?>) Reflect.get(p, "shots")).size() == 1, 0, "cooldown");
        Reflect.call(p, "key", java.awt.event.KeyEvent.VK_F, false);
        Reflect.call(p, "restart");
        Reflect.call(p, "key", java.awt.event.KeyEvent.VK_UP, true);
        Reflect.call(p, "key", java.awt.event.KeyEvent.VK_UP, false);
        Reflect.set(p, "vx", 2.0);
        Reflect.set(p, "vy", 1.0);
        Reflect.call(p, "key", java.awt.event.KeyEvent.VK_SPACE, true);
        Reflect.call(p, "step");
        check(((java.util.List<?>) Reflect.get(p, "shots")).isEmpty(), 1, "Space must not fire");
        check(Math.abs((double) Reflect.get(p, "vx") - 1.84) < 1e-12 && Math.abs((double) Reflect.get(p, "vy") - .92) < 1e-12, 1, Reflect.get(p, "vx") + ", " + Reflect.get(p, "vy"));
        Reflect.call(p, "key", java.awt.event.KeyEvent.VK_R, true);
        boolean clear = true;
        for (boolean held : (boolean[]) Reflect.get(p, "held")) clear &= !held;
        check(clear && !(boolean) Reflect.get(p, "started") && (int) Reflect.get(p, "cooldown") == 0, 2, "fresh controls");
        Reflect.set(p, "lost", true);
        ((Runnable) Reflect.call(p, "firingAction")).run();
        check(((java.util.List<?>) Reflect.get(p, "shots")).isEmpty(), 2, "lost flight");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P17_FireworkPainterTest {
    static int checks;
    static final String[] CASES = {
        "Creating the callback creates no particles.",
        "Invocation creates 120 particles in a clicked and reflected pair.",
        "A real press adds another mirrored pair and restart clears all particles."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.17: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P17_FireworkPainter");
            Reflect.call(p, "restart");var action=((java.util.function.BiConsumer<java.lang.Integer, java.lang.Integer>) Reflect.call(p, "burstAction"));check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).isEmpty(),0,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            action.accept(43,91);check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size()==120&&!((boolean) Reflect.get(p, "paused")),1,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            check(((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst(), "x"))==43&&((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst(), "y"))==91,1,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst());
            Reflect.call(p, "press", 120, 80, 1);check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size()==240&&((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getLast(), "x"))==((int) Reflect.get(p,"WIDTH"))-120,2,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            Reflect.call(p, "restart");check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).isEmpty(),2,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.17: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P18_ShadowPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Callback creation preserves the starting light position.",
        "Invocation and actual press/drag snap the supplied x/y to the 20-pixel grid.",
        "Out-of-bounds coordinates are clamped by moveLight."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.18: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P18_ShadowPlayground");
            Reflect.call(p, "restart");var action=((java.util.function.BiConsumer<java.lang.Integer, java.lang.Integer>) Reflect.call(p, "lightAction"));check(((double) Reflect.get(p, "lightX"))==100&&((double) Reflect.get(p, "lightY"))==80,0,((double) Reflect.get(p, "lightX")));
            action.accept(43,91);check(((double) Reflect.get(p, "lightX"))==40&&((double) Reflect.get(p, "lightY"))==80,1,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
            action.accept(-20,999);check(((double) Reflect.get(p, "lightX"))==0&&((double) Reflect.get(p, "lightY"))==((int) Reflect.get(p, "HEIGHT")),2,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
            Reflect.call(p, "press", 140, 70, 1);Reflect.call(p, "drag", 160, 90);check(((double) Reflect.get(p, "lightX"))==160&&((double) Reflect.get(p, "lightY"))==80,1,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.18: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P21_TiltTrayTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
        + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
        + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P21_TiltTray");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 168);
        Reflect.set(g, "y", 150);
        Reflect.set(g, "vx", 3);
        Reflect.set(g, "vy", 0);
        Reflect.call(g, "step");
        equal(170.0, ((double) Reflect.get(g, "x")), "13.21/F25: collision separates the ball outside obstacle");
        expect(((double) Reflect.get(g, "vx")) < 0 && !((boolean) Reflect.call(g, "hit", ((java.awt.Rectangle[]) Reflect.get(g, "obstacles"))[0])), "13.21/F25: reflected ball is not left penetrating");
        Reflect.call(g, "clearKeys");
        for (int n = 0; n < 100; n++) {
            Reflect.call(g, "step");
        }
        expect(Double.isFinite(((double) Reflect.get(g, "x"))) && Double.isFinite(((double) Reflect.get(g, "y"))), "13.21/F25: later updates remain finite");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 530);
        Reflect.set(g, "y", 345);
        Reflect.set(g, "vx", ((double) Reflect.set(g, "vy", 0)));
        Reflect.call(g, "step");
        expect(((boolean) Reflect.get(g, "won")), "13.21/F25: slow target entry still works");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P22_PlatformDashTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
        + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
        + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P22_PlatformDash");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 285);
        Reflect.set(g, "vy", -4);
        Reflect.call(g, "step");
        expect(!((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "y")) < 285, "13.22/F25: rising from below cannot snap onto platform top");
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 240);
        Reflect.set(g, "vy", 12);
        Reflect.call(g, "step");
        equal(252.0, ((double) Reflect.get(g, "y")), "13.22/F25: descending top crossing still lands");
        expect(((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "vy")) == 0, "13.22/F25: valid landing rests on top");
        Reflect.call(g, "restart");
        ((boolean[]) Reflect.get(g, "held"))[java.awt.event.KeyEvent.VK_RIGHT] = true;
        Reflect.call(g, "jump");
        for (int tick = 0; tick < 200 && !((boolean) Reflect.get(g, "won")); tick++) {
            Reflect.call(g, "step");
            if (((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "x")) < 380) {
                Reflect.call(g, "jump");
            }
        }
        expect(((boolean) Reflect.get(g, "won")) && ((int) Reflect.get(g, "falls")) == 0, "13.22/F25: original two-jump course remains playable");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P23_PongTest {
    static int checks;
    static final String[] CASES = {
        "The selected bot follows the current ball at three pixels per tick.",
        "Dead-zone boundaries, manual speed and paddle clamps remain correct.",
        "Switching, restart and a finished match preserve lifecycle promises."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.23: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P23_Pong");
        Reflect.call(p, "restart");
        Reflect.set(p, "ballY", 300.0);
        double before = (double) Reflect.get(p, "rightY");
        Reflect.set(p, "rightUp", true);
        Reflect.call(p, "toggleBot");
        check((double) Reflect.get(p, "rightY") == before && !(boolean) Reflect.get(p, "rightUp"), 2, "switch without a move or held key");
        Reflect.call(p, "step");
        check((double) Reflect.get(p, "rightY") == before + 3, 0, "rightY=" + Reflect.get(p, "rightY"));
        check((int) Reflect.call(p, "botDirection", 148.0, 100.0) == 0 && (int) Reflect.call(p, "botDirection", 132.0, 100.0) == 0, 1, "inclusive quiet boundaries");
        check((int) Reflect.call(p, "botDirection", 148.1, 100.0) == 1 && (int) Reflect.call(p, "botDirection", 131.9, 100.0) == -1, 1, "both directions");
        Reflect.set(p, "rightY", 319.0);
        Reflect.set(p, "ballY", 390.0);
        Reflect.call(p, "step");
        check((double) Reflect.get(p, "rightY") == 320, 1, "bottom clamp");
        Reflect.call(p, "toggleBot");
        Reflect.set(p, "rightUp", true);
        Reflect.call(p, "step");
        check((double) Reflect.get(p, "rightY") == 315, 1, "human speed");
        Reflect.set(p, "winner", 1);
        double stopped = (double) Reflect.get(p, "ballX");
        Reflect.call(p, "step");
        check((double) Reflect.get(p, "rightY") == 315 && (double) Reflect.get(p, "ballX") == stopped, 2, "finished match");
        Reflect.call(p, "restart");
        check(!(boolean) Reflect.get(p, "bot") && !(boolean) Reflect.get(p, "rightUp") && (int) Reflect.get(p, "winner") == 0, 2, "restart");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P24_OrbitPainterTest {
    static int checks;
    static final String[] CASES = {
        "Paused, queued or normal input respects the stated tick boundary.",
        "An active tick changes the model exactly once; a finished/paused tick cannot drift.",
        "Invalid boundaries, restart or input cleanup remain correct."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.24: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P24_OrbitPainter");
            Reflect.call(p, "restart");double x=((double) Reflect.get(p, "x")),y=((double) Reflect.get(p, "y"));Reflect.call(p, "step");check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&((int) Reflect.get(p, "ticks"))==0,0,((int) Reflect.get(p, "ticks")));Reflect.set(p, "paused", false);Reflect.call(p, "step");check(((int) Reflect.get(p, "ticks"))==1&&(((double) Reflect.get(p, "x"))!=x||((double) Reflect.get(p, "y"))!=y),1,((int) Reflect.get(p, "ticks")));Reflect.set(p, "paused", true);x=((double) Reflect.get(p, "x"));y=((double) Reflect.get(p, "y"));long ink=hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")));Reflect.call(p, "step");check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")))==ink,0,((int) Reflect.get(p, "ticks")));Reflect.call(p, "restart");check(((boolean) Reflect.get(p, "paused"))&&((int) Reflect.get(p, "ticks"))==0&&((double) Reflect.get(p, "x"))==440,2,((double) Reflect.get(p, "x")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.24: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P25_PendulumWavesTest {
    static int checks;
    static final String[] CASES = {
        "Paused, queued or normal input respects the stated tick boundary.",
        "An active tick changes the model exactly once; a finished/paused tick cannot drift.",
        "Invalid boundaries, restart or input cleanup remain correct."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.25: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P25_PendulumWaves");
            Reflect.call(p, "restart");Reflect.call(p, "step");check(((double) Reflect.get(p, "time"))==0,0,((double) Reflect.get(p, "time")));Reflect.set(p, "paused", false);Reflect.set(p, "speed", 2);Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "time"))-.04)<1e-12,1,((double) Reflect.get(p, "time")));Reflect.call(p, "adjust", 100);check(((double) Reflect.get(p, "speed"))==2,2,((double) Reflect.get(p, "speed")));Reflect.call(p, "adjust", -100);check(((double) Reflect.get(p, "speed"))==.25,2,((double) Reflect.get(p, "speed")));Reflect.call(p, "restart");check(((double) Reflect.get(p, "time"))==0&&((boolean) Reflect.get(p, "paused"))&&((double) Reflect.get(p, "speed"))==1,2,((double) Reflect.get(p, "time")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.25: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P28_SpringToyTest {
    static int checks;
    static final String[] CASES = {
        "Rendering preserves all motion fields.",
        "Repeated rendering has the same pixels and no new trail points.",
        "An explicit step still advances the model."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.28: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P28_SpringToy");
            Reflect.call(p, "restart");Reflect.set(p, "paused", false);double x=((double) Reflect.get(p, "x")),y=((double) Reflect.get(p, "y")),vx=((double) Reflect.get(p, "vx")),vy=((double) Reflect.get(p, "vy"));
            var first=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));
            check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&((double) Reflect.get(p, "vx"))==vx&&((double) Reflect.get(p, "vy"))==vy,0,((double) Reflect.get(p, "y")));
            check(((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).isEmpty(),1,((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).size());
            var second=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(hash(first)==hash(second),1,hash(second));
            Reflect.call(p, "step");check(((double) Reflect.get(p, "y"))!=y&&!((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).isEmpty(),2,((double) Reflect.get(p, "y")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.28: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P29_BreakoutTest {
    static int checks;
    static final String[] CASES = {
        "Repeated misses serve another ball without finishing the practice round.",
        "A miss preserves cleared bricks and score.",
        "The last brick still wins and freezes motion; restart restores the whole wall."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.29: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P29_Breakout");
        Reflect.call(p, "restart");
        boolean[][] bricks = (boolean[][]) Reflect.get(p, "bricks");
        bricks[0][0] = false;
        Reflect.set(p, "score", 1);
        for (int miss = 0; miss < 4096; miss++) {
            Reflect.set(p, "ballX", 320.0);
            Reflect.set(p, "ballY", 420.0);
            Reflect.set(p, "velocityY", 2.0);
            Reflect.call(p, "step");
            check(Reflect.get(p, "state").equals("PLAY") && (double) Reflect.get(p, "ballY") < 400, 0, "miss " + (miss + 1) + ": " + Reflect.get(p, "state"));
        }
        check((int) Reflect.get(p, "score") == 1 && !bricks[0][0], 1, "progress after misses");
        Reflect.call(p, "restart");
        int count = 0;
        for (boolean[] row : bricks) for (boolean brick : row) if (brick) count++;
        check(count == 18 && (int) Reflect.get(p, "score") == 0, 2, "fresh wall");
        for (boolean[] row : bricks) Arrays.fill(row, false);
        bricks[0][0] = true;
        Reflect.set(p, "score", 17);
        Reflect.set(p, "ballX", 76.0);
        Reflect.set(p, "ballY", 47.0);
        Reflect.set(p, "velocityX", 0.0);
        Reflect.set(p, "velocityY", 1.0);
        Reflect.call(p, "step");
        check(Reflect.get(p, "state").equals("YOU WIN") && (int) Reflect.get(p, "score") == 18, 2, "last brick");
        double stoppedX = (double) Reflect.get(p, "ballX");
        double stoppedY = (double) Reflect.get(p, "ballY");
        Reflect.call(p, "step");
        check((double) Reflect.get(p, "ballX") == stoppedX && (double) Reflect.get(p, "ballY") == stoppedY, 2, "win is stable");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P30_RippleTankTest {
    static int checks;
    static final String[] CASES = {
        "Paused, queued or normal input respects the stated tick boundary.",
        "An active tick changes the model exactly once; a finished/paused tick cannot drift.",
        "Invalid boundaries, restart or input cleanup remain correct."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.30: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P30_RippleTank");
            Reflect.call(p, "restart");Reflect.call(p, "tap", 20, 10);check(!((boolean) Reflect.get(p, "paused"))&&((double[][]) Reflect.get(p, "height"))[10][20]==60,0,((double[][]) Reflect.get(p, "height"))[10][20]);Reflect.call(p, "step");check(((double[][]) Reflect.get(p, "previous"))[10][20]==60&&Math.abs(((double[][]) Reflect.get(p, "height"))[10][21]-29.76)<1e-9,1,((double[][]) Reflect.get(p, "height"))[10][21]);Reflect.call(p, "restart");Reflect.call(p, "tap", 0, 0);check(((boolean) Reflect.get(p, "paused"))&&((double) Reflect.call(p, "energy"))==0,2,((double) Reflect.call(p, "energy")));Reflect.call(p, "tap", 20, 10);Reflect.set(p, "paused", true);double energy=((double) Reflect.call(p, "energy"));Reflect.call(p, "step");check(((double) Reflect.call(p, "energy"))==energy,2,((double) Reflect.call(p, "energy")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.30: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P31_PictureMusicTest {
    static int checks;
    static final String[] CASES = {
        "The new +12 shift doubles frequency and changes actual samples.",
        "Zero and -12 retain the old mapping and octave relation.",
        "Invalid rows and shifts are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.31: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P31_PictureMusic");
            double base=((double) Reflect.call(p, "pitchFrequency", 4, 0));check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 4, 12))-base*2)<1e-9,0,((double) Reflect.call(p, "pitchFrequency", 4, 12)));
            check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 4, -12))-base/2)<1e-9,1,((double) Reflect.call(p, "pitchFrequency", 4, -12)));
            check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 11, 0))-220)<1e-9,1,((double) Reflect.call(p, "pitchFrequency", 11, 0)));
            reject(()->Reflect.call(p, "pitchFrequency", 12, 0),2);reject(()->Reflect.call(p, "pitchFrequency", 0, 13),2);
            var image=((java.awt.image.BufferedImage) Reflect.call(p, "demo"));Reflect.set(p, "transpose", 0);var a=((double[]) Reflect.call(p, "signal", image));Reflect.set(p, "transpose", 12);check(!Arrays.equals(a,((double[]) Reflect.call(p, "signal", image))),0,"transposed WAV");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.31: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P32_SecretBeepsTest {
    static int checks;
    static final String[] CASES = {
        "The new word gap changes the actual waveform length.",
        "The original seven-unit mode and three-unit letter gap remain unchanged.",
        "Word-gap values outside 3–12 are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 13.32: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P32_SecretBeeps");
            check(((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E", 10)).equals(List.of(1,-10,1)),0,((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E", 10)));
            check(((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E")).equals(List.of(1,-7,1))&&((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "EE", 10)).equals(List.of(1,-3,1)),1,((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "EE", 10)));
            reject(()->Reflect.call(p, "units", "E E", 2),2);reject(()->Reflect.call(p, "units", "E E", 13),2);
            Reflect.set(p, "wordGap", 7);int original=((double[]) Reflect.call(p, "signal", "E E", 20)).length;Reflect.set(p, "wordGap", 10);check(((double[]) Reflect.call(p, "signal", "E E", 20)).length-original==3*(22050*20/1000),0,((double[]) Reflect.call(p, "signal", "E E", 20)).length);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 13.32: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
