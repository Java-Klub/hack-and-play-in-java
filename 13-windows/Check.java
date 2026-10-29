import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Game task checks for this chapter. Run short textbook tasks directly.
class Check {
    int failed;
    int checked;
    String selected;
    final ArrayList<String> available = new ArrayList<>();
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) {
            IO.println("Supply at most one game filename. With no argument, all game tasks in this chapter are checked.");
            System.exit(2);
        }
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P03_Task_PixelMonsters", () -> P03_Task_PixelMonstersTest.main(new String[0]));
        run("P04_Task_Untangle", () -> P04_Task_UntangleTest.main(new String[0]));
        run("P05_Task_MirrorArt", () -> P05_Task_MirrorArtTest.main(new String[0]));
        run("P06_Task_Moire", () -> P06_Task_MoireTest.main(new String[0]));
        run("P07_Task_StarTunnel", () -> P07_Task_StarTunnelTest.main(new String[0]));
        run("P10_Task_CrossingRush", () -> P10_Task_CrossingRushTest.main(new String[0]));
        run("P11_Task_Snake", () -> P11_Task_SnakeTest.main(new String[0]));
        run("P14_Task_MiniInvaders", () -> P14_Task_MiniInvadersTest.main(new String[0]));
        run("P15_Task_SoftLanding", () -> P15_Task_SoftLandingTest.main(new String[0]));
        run("P16_Task_AsteroidDrift", () -> P16_Task_AsteroidDriftTest.main(new String[0]));
        run("P17_Task_FireworkPainter", () -> P17_Task_FireworkPainterTest.main(new String[0]));
        run("P18_Task_ShadowPlayground", () -> P18_Task_ShadowPlaygroundTest.main(new String[0]));
        run("P21_Task_TiltTray", () -> P21_Task_TiltTrayTest.main(new String[0]));
        run("P22_Task_PlatformDash", () -> P22_Task_PlatformDashTest.main(new String[0]));
        run("P23_Task_Pong", () -> P23_Task_PongTest.main(new String[0]));
        run("P24_Task_OrbitPainter", () -> P24_Task_OrbitPainterTest.main(new String[0]));
        run("P25_Task_PendulumWaves", () -> P25_Task_PendulumWavesTest.main(new String[0]));
        run("P28_Task_SpringToy", () -> P28_Task_SpringToyTest.main(new String[0]));
        run("P29_Task_Breakout", () -> P29_Task_BreakoutTest.main(new String[0]));
        run("P30_Task_RippleTank", () -> P30_Task_RippleTankTest.main(new String[0]));
        run("P31_Task_PictureMusic", () -> P31_Task_PictureMusicTest.main(new String[0]));
        run("P32_Task_SecretBeeps", () -> P32_Task_SecretBeepsTest.main(new String[0]));
        if (checked == 0) {
            IO.println("No game check is prepared here for this file: " + selected + ".java");
            IO.println("Choose one of these files: " + String.join(", ", available));
            System.exit(2);
        }
        IO.println("Checked: " + checked + "; complete: " + (checked - failed) + "; remaining: " + failed);
        if (failed > 0) System.exit(1);
    }

    void run(String game, Test test) {
        available.add(game + ".java");
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + game + ".java");
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + game + ".java");
            String reason = failure.getMessage();
            if (reason == null || reason.isBlank()) reason = "The check did not get the expected result.";
            if (!(failure instanceof AssertionError)) reason = "The program stopped during the check: " + failure.getClass().getSimpleName() + ": " + reason;
            IO.println("  " + reason.replace("\n", "\n  "));
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            if (cause != failure) IO.println("  " + cause.getClass().getSimpleName() + ": " + show(cause.getMessage()));
            for (var frame : cause.getStackTrace()) {
                if (frame.getClassName().equals(game) || frame.getClassName().startsWith(game + "$")) {
                    IO.println("  " + "Location in the program: " + frame.getFileName() + ":" + frame.getLineNumber());
                    break;
                }
            }
        }
    }

    static String show(Object value) {
        if (value != null && value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value);
            var parts = new ArrayList<String>();
            for (int i = 0; i < Math.min(length, 8); i++) parts.add(show(java.lang.reflect.Array.get(value, i)));
            return parts + (length > 8 ? " … (" + length + " items" + ")" : "");
        }
        String text = String.valueOf(value).replace("\n", "\\n").replace("\r", "\\r");
        return text.length() <= 180 ? text : text.substring(0, 177) + "…";
    }
}

@SuppressWarnings("unchecked")
class P03_Task_PixelMonstersTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Task_PixelMonsters");
        var image = ((java.awt.image.BufferedImage) Reflect.call(game, "render", 42));
        check(image.getRGB(5 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "The left eye is not white. render must use the shared eyeColour value for that eye.");
        check(image.getRGB(10 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "The right eye is not white. Both eye positions must use the same eyeColour value.");
        check(((int) Reflect.get(game, "eyeColour")) == Color.WHITE.getRGB(), "Set the shared eyeColour value to white so both eyes change together; do not repaint just one eye separately.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_UntangleTest {
    static int checks;
    static final String[] CASES = {
        "drawNode must centre the node at the supplied Point. For Point(120,70), the node should cover pixel (128,70); check x and y roles.",
        "Drawing a node changed its Point. drawNode and draw must read positions without moving the model.",
        "The full-scene draw must use the repaired node coordinates too. A node at (80,90) should remain centred there."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P04_Task_Untangle");
            Reflect.call(p, "restart");var point=new Point(120,70);var image=draw(g->Reflect.call(p, "drawNode", g, point, 3),600,420);
            check((image.getRGB(128,70)&0xFFFFFF)==0x23375A,0,Integer.toHexString(image.getRGB(128,70)));
            check(point.equals(new Point(120,70)),1,point);
            ((java.awt.Point[]) Reflect.get(p, "points"))[0]=new Point(80,90);var scene=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),600,420);
            check((scene.getRGB(88,90)&0xFFFFFF)==0x23375A,2,Integer.toHexString(scene.getRGB(88,90)));
            var copy=((java.awt.Point[]) Reflect.get(p, "points"))[0].getLocation();draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),600,420);check(((java.awt.Point[]) Reflect.get(p, "points"))[0].equals(copy),1,((java.awt.Point[]) Reflect.get(p, "points"))[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P05_Task_MirrorArtTest {
    static int checks;
    static final String[] CASES = {
        "transform must leave the drawing centre (320,220) fixed.",
        "With four axes, one sector must rotate (350,220) to (320,250). Reflection must turn (320,250) into (320,190).",
        "stroke must use transform to add visible ink to the canvas. The transformed drawing should not leave the canvas unchanged."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P05_Task_MirrorArt");
            Reflect.call(p, "restart");var c=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 320, 220, 0, false));check(c.distance(320,220)<1e-9,0,c);Reflect.set(p, "axes", 4);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 350, 220, 1, false));check(q.distance(320,250)<1e-9,1,q);var r=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 320, 250, 0, true));check(r.distance(320,190)<1e-9,1,r);long before=hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")));Reflect.call(p, "stroke", 350, 220, 360, 230);check(hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")))!=before,2,hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas"))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P06_Task_MoireTest {
    static int checks;
    static final String[] CASES = {
        "gridPoint(0,0,0) must return the screen centre (320,220). Check the translation from grid coordinates to screen coordinates.",
        "gridPoint(1,0,Math.PI/2) must return (320,242), one spacing below the centre after a quarter-turn.",
        "draw must preserve angle and produce identical pixels on repeated calls. Repainting must not rotate the model."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P06_Task_Moire");
            Reflect.call(p, "restart");var c=((java.awt.geom.Point2D.Double) Reflect.call(p, "gridPoint", 0, 0, 0));check(c.distance(320,220)<1e-9,0,c);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "gridPoint", 1, 0, Math.PI/2));check(q.distance(320,242)<1e-8,1,q);double old=((double) Reflect.get(p, "angle"));var a=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));var b=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(((double) Reflect.get(p, "angle"))==old&&hash(a)==hash(b),2,((double) Reflect.get(p, "angle")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P07_Task_StarTunnelTest {
    static int checks;
    static final String[] CASES = {
        "project for Star(10,20,100) at depth 100 must place the point 26 pixels right and 52 down from the screen centre.",
        "project must clamp a depth of zero before dividing. The test star should then be offset by (520,1040) from the centre.",
        "draw changed the stored stars. Project and paint them without changing their model values or their order."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P07_Task_StarTunnel");
            Reflect.call(p, "restart");var star=Reflect.create("P07_Task_StarTunnel$Star", 10, 20, 100);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "project", star, 100));check(q.distance(((double) Reflect.get(p, "centreX"))+26,((double) Reflect.get(p, "centreY"))+52)<1e-9,0,q);var near=((java.awt.geom.Point2D.Double) Reflect.call(p, "project", star, 0));check(near.distance(((double) Reflect.get(p, "centreX"))+520,((double) Reflect.get(p, "centreY"))+1040)<1e-9,1,near);var before=List.copyOf(((java.util.ArrayList<Object>) Reflect.get(p, "stars")));draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(((java.util.ArrayList<Object>) Reflect.get(p, "stars")).equals(before),2,((java.util.ArrayList<Object>) Reflect.get(p, "stars")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P10_Task_CrossingRushTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P10_Task_CrossingRush");
        Reflect.call(g, "restart");
        Reflect.call(g, "hop", -1000, 0);
        equal(12, ((int) Reflect.get(g, "x")), "After a large left hop, the player centre must stop at x=12 so the whole player stays on the road.");
        Reflect.call(g, "hop", 1000, 0);
        equal(588, ((int) Reflect.get(g, "x")), "After a large right hop, the player centre must stop at x=588 so its right edge stays inside the road.");
        ((java.util.ArrayList<Object>) Reflect.get(g, "cars")).clear();
        Reflect.call(g, "hop", 0, -500);
        expect(((boolean) Reflect.get(g, "won")), "Reaching the top goal without a car collision must still set won. Preserve the successful crossing when repairing hop.");
        int x = ((int) Reflect.get(g, "x"));
        Reflect.call(g, "hop", -30, 0);
        equal(x, ((int) Reflect.get(g, "x")), "hop moved the player after victory. Once the round has ended, later movement requests must leave the position unchanged.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_SnakeTest {
    static int checks;
    static final String[] CASES = {
        "turn changed the committed direction or moved the head before a tick. Queue the requested turn and leave dx, dy and the body unchanged until step.",
        "The next step must apply the first queued turn exactly once. A second key before that step must not replace it or move the snake early.",
        "step must leave a lost snake still. restart must clear the loss and restore score zero."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P11_Task_Snake");
            Reflect.call(p, "restart");var head=((java.util.List<Object>) Reflect.get(p, "body")).getFirst();int dx=((int) Reflect.get(p, "dx")),dy=((int) Reflect.get(p, "dy"));Reflect.call(p, "turn", 0, -1);check(((int) Reflect.get(p, "dx"))==dx&&((int) Reflect.get(p, "dy"))==dy&&((boolean) Reflect.call(((java.util.List<Object>) Reflect.get(p, "body")).getFirst(), "equals", head)),0,((java.util.List<Object>) Reflect.get(p, "body")).getFirst());Reflect.call(p, "turn", -1, 0);Reflect.call(p, "step");check(((boolean) Reflect.call(((java.util.List<Object>) Reflect.get(p, "body")).getFirst(), "equals", Reflect.create("P11_Task_Snake$Cell", ((int) Reflect.call(head, "x")), ((int) Reflect.call(head, "y"))-1))),1,((java.util.List<Object>) Reflect.get(p, "body")).getFirst());Reflect.set(p, "lost", true);var stopped=List.copyOf(((java.util.List<Object>) Reflect.get(p, "body")));Reflect.call(p, "step");check(((java.util.List<Object>) Reflect.get(p, "body")).equals(stopped),2,((java.util.List<Object>) Reflect.get(p, "body")));Reflect.call(p, "restart");check(!((boolean) Reflect.get(p, "lost"))&&((int) Reflect.get(p, "score"))==0,2,((int) Reflect.get(p, "score")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P14_Task_MiniInvadersTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P14_Task_MiniInvaders");
        Reflect.call(g, "restart");
        Reflect.set(g, "speedX", 0.0);
        Reflect.set(g, "fire", true);
        Reflect.call(g, "step");
        Reflect.set(g, "fire", false);
        Reflect.set(g, "bulletX", (double) Reflect.get(g, "formationX") + 10);
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 2 * 34 + 17);
        Reflect.call(g, "step");
        equal(1, Reflect.get(g, "score"), "The first real collision should remove one alien and add exactly one point. Check the hit handling used by step.");
        expect((boolean) Reflect.get(g, "bulletActive"), "The piercing pulse disappeared after its first alien. Keep it active until its second valid hit.");
        Reflect.call(g, "hitAlien", 2, 0);
        equal(1, Reflect.get(g, "score"), "hitAlien counted an already removed alien again. Repeated contact with the same empty cell must not add another point.");
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 34 + 17);
        Reflect.call(g, "step");
        equal(2, Reflect.get(g, "score"), "The same pulse should reach a second alien and bring the score to two. Preserve it between those two collisions.");
        expect(!(boolean) Reflect.get(g, "bulletActive"), "The pulse is still active after its second valid hit. Each pulse may remove at most two aliens.");
        Reflect.call(g, "hitAlien", 0, 0);
        equal(2, Reflect.get(g, "score"), "A spent pulse added a third point. hitAlien must ignore further targets once that pulse has ended.");
        Reflect.set(g, "fire", true); Reflect.call(g, "step"); Reflect.set(g, "fire", false);
        Reflect.call(g, "hitAlien", 0, 0);
        expect((boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 3, "A new pulse needs its own two-hit allowance. Its first hit should add one point and leave that pulse active.");
        Reflect.call(g, "hitAlien", 0, 1);
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 4, "The new pulse must also stop after two valid hits. Reset its allowance per shot without making it unlimited.");
        Reflect.call(g, "restart");
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 0 && (int) Reflect.call(g, "countAliens") == 15, "restart must restore all 15 aliens, clear the score and remove any active pulse.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_SoftLandingTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P15_Task_SoftLanding");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "fuel", 0);
        Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "Up thrust must still work with fuel=0. After one step, vertical velocity should be -0.05 with the original thrust and gravity.");
        equal(0, Reflect.get(g, "fuel"), "Training thrust changed the zero fuel counter. In this mode, using engines must leave fuel unchanged.");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "left", true); Reflect.set(g, "right", true); Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        equal(240, Reflect.get(g, "fuel"), "Using several engines spent fuel. Training must preserve the original counter value of 240 as well as zero.");
        expect(Math.abs((double) Reflect.get(g, "velocityX") - 1.2) < 1e-9 && Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "With Left, Right and Up together, horizontal thrust should cancel while Up still brakes the fall. Preserve both original engine strengths.");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") - 0.04) < 1e-9, "With no thrust key held, step must apply gravity alone; vertical velocity should increase to 0.04.");
        Reflect.set(g, "state", "CRASH");
        double y = (double) Reflect.get(g, "y");
        Reflect.set(g, "up", true); Reflect.call(g, "step");
        equal(y, Reflect.get(g, "y"), "step moved a crashed lander. A CRASH state must stay terminal even when Up is held.");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        expect(!(boolean) Reflect.get(g, "left") && !(boolean) Reflect.get(g, "right") && !(boolean) Reflect.get(g, "up"), "restart left an engine key active. Clear Left, Right and Up so the next flight starts without held thrust.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_AsteroidDriftTest {
    static int checks;
    static final String[] CASES = {
        "Holding F must fire through key and step, with the original cooldown. The first two ticks should contain only one shot.",
        "Space must brake both velocity components by 8% per active tick and create no shot. Check the held-key action used by step.",
        "R must clear held keys and cooldown and wait for a new start. A lost flight must not fire through firingAction."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
        var p = Reflect.create("P16_Task_AsteroidDrift");
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
    }
}

@SuppressWarnings("unchecked")
class P17_Task_FireworkPainterTest {
    static int checks;
    static final String[] CASES = {
        "Calling burstAction must only create the action, not particles. Wait until the returned action is invoked before launching fireworks.",
        "Invoking the action at (43,91) must create 120 particles at that point and its reflected partner, then resume animation.",
        "press must use the same paired-firework action: another click adds another reflected pair. restart must then clear every particle."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P17_Task_FireworkPainter");
            Reflect.call(p, "restart");var action=((java.util.function.BiConsumer<java.lang.Integer, java.lang.Integer>) Reflect.call(p, "burstAction"));check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).isEmpty(),0,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            action.accept(43,91);check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size()==120&&!((boolean) Reflect.get(p, "paused")),1,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            check(((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst(), "x"))==43&&((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst(), "y"))==91,1,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst());
            Reflect.call(p, "press", 120, 80, 1);check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size()==240&&((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getLast(), "x"))==((int) Reflect.get(p,"WIDTH"))-120,2,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            Reflect.call(p, "restart");check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).isEmpty(),2,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P18_Task_ShadowPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Creating lightAction moved the lamp. Leave its initial position (100,80) unchanged until the returned action receives coordinates.",
        "The light action must snap (43,91) down to (40,80). The real press and drag paths must use that same 20-pixel grid rule.",
        "Out-of-bounds coordinates must still pass through moveLight. A request at (-20,999) should leave the light at (0,HEIGHT)."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P18_Task_ShadowPlayground");
            Reflect.call(p, "restart");var action=((java.util.function.BiConsumer<java.lang.Integer, java.lang.Integer>) Reflect.call(p, "lightAction"));check(((double) Reflect.get(p, "lightX"))==100&&((double) Reflect.get(p, "lightY"))==80,0,((double) Reflect.get(p, "lightX")));
            action.accept(43,91);check(((double) Reflect.get(p, "lightX"))==40&&((double) Reflect.get(p, "lightY"))==80,1,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
            action.accept(-20,999);check(((double) Reflect.get(p, "lightX"))==0&&((double) Reflect.get(p, "lightY"))==((int) Reflect.get(p, "HEIGHT")),2,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
            Reflect.call(p, "press", 140, 70, 1);Reflect.call(p, "drag", 160, 90);check(((double) Reflect.get(p, "lightX"))==160&&((double) Reflect.get(p, "lightY"))==80,1,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P21_Task_TiltTrayTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P21_Task_TiltTray");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 168);
        Reflect.set(g, "y", 150);
        Reflect.set(g, "vx", 3);
        Reflect.set(g, "vy", 0);
        Reflect.call(g, "step");
        equal(170.0, ((double) Reflect.get(g, "x")), "After a side collision, step must place the marble at x=170, outside the obstacle. Reversing velocity alone leaves it trapped inside.");
        expect(((double) Reflect.get(g, "vx")) < 0 && !((boolean) Reflect.call(g, "hit", ((java.awt.Rectangle[]) Reflect.get(g, "obstacles"))[0])), "The bounced marble must have reversed horizontal velocity and no longer overlap the obstacle. Check position correction as well as reflection.");
        Reflect.call(g, "clearKeys");
        for (int n = 0; n < 100; n++) {
            Reflect.call(g, "step");
        }
        expect(Double.isFinite(((double) Reflect.get(g, "x"))) && Double.isFinite(((double) Reflect.get(g, "y"))), "After later steps, marble coordinates must remain finite numbers. The collision repair must not create NaN or infinite positions.");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 530);
        Reflect.set(g, "y", 345);
        Reflect.set(g, "vx", ((double) Reflect.set(g, "vy", 0)));
        Reflect.call(g, "step");
        expect(((boolean) Reflect.get(g, "won")), "A slow entry into the green target must still win. Preserve the existing goal test when repairing collision handling.");
    }
}

@SuppressWarnings("unchecked")
class P22_Task_PlatformDashTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P22_Task_PlatformDash");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 285);
        Reflect.set(g, "vy", -4);
        Reflect.call(g, "step");
        expect(!((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "y")) < 285, "step snapped the player onto a platform while rising from below. Upward movement through its underside must not count as landing.");
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 240);
        Reflect.set(g, "vy", 12);
        Reflect.call(g, "step");
        equal(252.0, ((double) Reflect.get(g, "y")), "A downward crossing of the platform top must still land at y=252. Check the previous bottom edge as well as the new position.");
        expect(((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "vy")) == 0, "A valid landing must set grounded and stop vertical velocity. Do not leave the player falling after placing it on the platform.");
        Reflect.call(g, "restart");
        ((boolean[]) Reflect.get(g, "held"))[java.awt.event.KeyEvent.VK_RIGHT] = true;
        Reflect.call(g, "jump");
        for (int tick = 0; tick < 200 && !((boolean) Reflect.get(g, "won")); tick++) {
            Reflect.call(g, "step");
            if (((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "x")) < 380) {
                Reflect.call(g, "jump");
            }
        }
        expect(((boolean) Reflect.get(g, "won")) && ((int) Reflect.get(g, "falls")) == 0, "The original route should still reach the flag with two jumps and no falls. Keep ordinary movement and landing playable after the repair.");
    }
}

@SuppressWarnings("unchecked")
class P23_Task_PongTest {
    static int checks;
    static final String[] CASES = {
        "With bot mode selected, step must move the right paddle three pixels toward the current ball. Check that the running update actually uses the bot decision.",
        "The bot must stay still within 8 pixels of the paddle centre and move outside that band. Keep human speed at five pixels and clamp the paddle inside the court.",
        "Switching opponent must not move the paddle or keep a held direction. A finished match must stay still; restart must clear the winner and return to human control."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
        var p = Reflect.create("P23_Task_Pong");
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
    }
}

@SuppressWarnings("unchecked")
class P24_Task_OrbitPainterTest {
    static int checks;
    static final String[] CASES = {
        "A paused step must preserve position, tick count and the painted trail. Move the orbit only while paused is false.",
        "One active step must advance the position and increment ticks exactly once. The running orbit currently did not make that update.",
        "restart must restore x=440, ticks=0 and paused=true so a fresh orbit waits for the player."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P24_Task_OrbitPainter");
            Reflect.call(p, "restart");double x=((double) Reflect.get(p, "x")),y=((double) Reflect.get(p, "y"));Reflect.call(p, "step");check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&((int) Reflect.get(p, "ticks"))==0,0,((int) Reflect.get(p, "ticks")));Reflect.set(p, "paused", false);Reflect.call(p, "step");check(((int) Reflect.get(p, "ticks"))==1&&(((double) Reflect.get(p, "x"))!=x||((double) Reflect.get(p, "y"))!=y),1,((int) Reflect.get(p, "ticks")));Reflect.set(p, "paused", true);x=((double) Reflect.get(p, "x"));y=((double) Reflect.get(p, "y"));long ink=hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")));Reflect.call(p, "step");check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")))==ink,0,((int) Reflect.get(p, "ticks")));Reflect.call(p, "restart");check(((boolean) Reflect.get(p, "paused"))&&((int) Reflect.get(p, "ticks"))==0&&((double) Reflect.get(p, "x"))==440,2,((double) Reflect.get(p, "x")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P25_Task_PendulumWavesTest {
    static int checks;
    static final String[] CASES = {
        "A paused step must leave the shared time at zero. Repainting or waiting while paused must not advance the pendulums.",
        "With speed=2, one running step must advance time by 0.04. Update the shared clock once per active tick.",
        "Keep speed within 0.25..2. restart must restore speed=1, time=0 and the paused state."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P25_Task_PendulumWaves");
            Reflect.call(p, "restart");Reflect.call(p, "step");check(((double) Reflect.get(p, "time"))==0,0,((double) Reflect.get(p, "time")));Reflect.set(p, "paused", false);Reflect.set(p, "speed", 2);Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "time"))-.04)<1e-12,1,((double) Reflect.get(p, "time")));Reflect.call(p, "adjust", 100);check(((double) Reflect.get(p, "speed"))==2,2,((double) Reflect.get(p, "speed")));Reflect.call(p, "adjust", -100);check(((double) Reflect.get(p, "speed"))==.25,2,((double) Reflect.get(p, "speed")));Reflect.call(p, "restart");check(((double) Reflect.get(p, "time"))==0&&((boolean) Reflect.get(p, "paused"))&&((double) Reflect.get(p, "speed"))==1,2,((double) Reflect.get(p, "time")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P28_Task_SpringToyTest {
    static int checks;
    static final String[] CASES = {
        "draw changed x, y or velocity. Painting must only observe the spring model; step owns motion.",
        "Repeated draw calls must produce the same pixels without adding trail points. A repaint is not another simulation step.",
        "The explicit step must still move the weight and add a trail point. Remove motion from draw without disabling the real update."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P28_Task_SpringToy");
            Reflect.call(p, "restart");Reflect.set(p, "paused", false);double x=((double) Reflect.get(p, "x")),y=((double) Reflect.get(p, "y")),vx=((double) Reflect.get(p, "vx")),vy=((double) Reflect.get(p, "vy"));
            var first=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));
            check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&((double) Reflect.get(p, "vx"))==vx&&((double) Reflect.get(p, "vy"))==vy,0,((double) Reflect.get(p, "y")));
            check(((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).isEmpty(),1,((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).size());
            var second=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(hash(first)==hash(second),1,hash(second));
            Reflect.call(p, "step");check(((double) Reflect.get(p, "y"))!=y&&!((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).isEmpty(),2,((double) Reflect.get(p, "y")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P29_Task_BreakoutTest {
    static int checks;
    static final String[] CASES = {
        "A missed ball ended the practice round or failed to serve again. step must allow unlimited misses, not just a larger life count.",
        "A miss erased progress. Serving again must preserve the score and every brick already removed.",
        "restart must restore all 18 bricks and score zero. The last brick must still win, and later steps must leave the won round motionless."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
        var p = Reflect.create("P29_Task_Breakout");
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
    }
}

@SuppressWarnings("unchecked")
class P30_Task_RippleTankTest {
    static int checks;
    static final String[] CASES = {
        "A valid tap at (20,10) must add a disturbance of 60 and start the water moving. Do not leave the tank paused after that input.",
        "After a valid tap, step must propagate the disturbance: the neighbouring cell should become 29.76. Check that the input really resumed the update.",
        "A border tap must leave a fresh tank still and empty. Once paused, step must also preserve the existing water energy."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P30_Task_RippleTank");
            Reflect.call(p, "restart");Reflect.call(p, "tap", 20, 10);check(!((boolean) Reflect.get(p, "paused"))&&((double[][]) Reflect.get(p, "height"))[10][20]==60,0,((double[][]) Reflect.get(p, "height"))[10][20]);Reflect.call(p, "step");check(((double[][]) Reflect.get(p, "previous"))[10][20]==60&&Math.abs(((double[][]) Reflect.get(p, "height"))[10][21]-29.76)<1e-9,1,((double[][]) Reflect.get(p, "height"))[10][21]);Reflect.call(p, "restart");Reflect.call(p, "tap", 0, 0);check(((boolean) Reflect.get(p, "paused"))&&((double) Reflect.call(p, "energy"))==0,2,((double) Reflect.call(p, "energy")));Reflect.call(p, "tap", 20, 10);Reflect.set(p, "paused", true);double energy=((double) Reflect.call(p, "energy"));Reflect.call(p, "step");check(((double) Reflect.call(p, "energy"))==energy,2,((double) Reflect.call(p, "energy")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P31_Task_PictureMusicTest {
    static int checks;
    static final String[] CASES = {
        "pitchFrequency with shift +12 must double the original frequency. signal must use that shift so the actual sound samples change too.",
        "Keep the original frequency at shift 0 and halve it at shift -12. In particular, row 11 at shift 0 must still give 220 Hz.",
        "pitchFrequency must reject row 12 and shift 13 with IllegalArgumentException. Preserve the original input limits when adding transposition."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P31_Task_PictureMusic");
            double base=((double) Reflect.call(p, "pitchFrequency", 4, 0));check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 4, 12))-base*2)<1e-9,0,((double) Reflect.call(p, "pitchFrequency", 4, 12)));
            check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 4, -12))-base/2)<1e-9,1,((double) Reflect.call(p, "pitchFrequency", 4, -12)));
            check(Math.abs(((double) Reflect.call(p, "pitchFrequency", 11, 0))-220)<1e-9,1,((double) Reflect.call(p, "pitchFrequency", 11, 0)));
            reject(()->Reflect.call(p, "pitchFrequency", 12, 0),2);reject(()->Reflect.call(p, "pitchFrequency", 0, 13),2);
            var image=((java.awt.image.BufferedImage) Reflect.call(p, "demo"));Reflect.set(p, "transpose", 0);var a=((double[]) Reflect.call(p, "signal", image));Reflect.set(p, "transpose", 12);check(!Arrays.equals(a,((double[]) Reflect.call(p, "signal", image))),0,"transposed WAV");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P32_Task_SecretBeepsTest {
    static int checks;
    static final String[] CASES = {
        "units(\"E E\",10) must use a ten-unit word gap, and signal must include the longer silence. Changing only a displayed setting is not enough.",
        "Preserve the original seven-unit word gap and the three-unit gap between letters. Only the requested space between words should change.",
        "units must reject word gaps 2 and 13 with IllegalArgumentException; the permitted range is 3..12."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P32_Task_SecretBeeps");
            check(((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E", 10)).equals(List.of(1,-10,1)),0,((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E", 10)));
            check(((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "E E")).equals(List.of(1,-7,1))&&((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "EE", 10)).equals(List.of(1,-3,1)),1,((java.util.List<java.lang.Integer>) Reflect.call(p, "units", "EE", 10)));
            reject(()->Reflect.call(p, "units", "E E", 2),2);reject(()->Reflect.call(p, "units", "E E", 13),2);
            Reflect.set(p, "wordGap", 7);int original=((double[]) Reflect.call(p, "signal", "E E", 20)).length;Reflect.set(p, "wordGap", 10);check(((double[]) Reflect.call(p, "signal", "E E", 20)).length-original==3*(22050*20/1000),0,((double[]) Reflect.call(p, "signal", "E E", 20)).length);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
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
