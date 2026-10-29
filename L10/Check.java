import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_AsteroidDrift.java`
/// Kontroly volají testovací metody přímo v této JVM.
/// Hry zůstávají compact sources; následující kontroly k nim přistupují pomocí reflection.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_AsteroidDrift", "L10.01 AsteroidDrift · F28", () -> M01_AsteroidDriftTest.main(new String[0]));
        run("M02_FireworkPainter", "L10.02 FireworkPainter · F28", () -> M02_FireworkPainterTest.main(new String[0]));
        run("M03_ShadowPlayground", "L10.03 ShadowPlayground · F28", () -> M03_ShadowPlaygroundTest.main(new String[0]));
        run("M04_Untangle", "L10.04 Untangle · F29", () -> M04_UntangleTest.main(new String[0]));
        run("M05_SpringToy", "L10.05 SpringToy · F29", () -> M05_SpringToyTest.main(new String[0]));
        run("M06_MirrorArt", "L10.06 MirrorArt · F29", () -> M06_MirrorArtTest.main(new String[0]));
        run("M07_Moire", "L10.07 Moire · F29", () -> M07_MoireTest.main(new String[0]));
        run("M08_StarTunnel", "L10.08 StarTunnel · F29", () -> M08_StarTunnelTest.main(new String[0]));
        run("M09_Pong", "L10.09 Pong · F30", () -> M09_PongTest.main(new String[0]));
        run("M10_Breakout", "L10.10 Breakout · F30", () -> M10_BreakoutTest.main(new String[0]));
        run("M11_OrbitPainter", "L10.11 OrbitPainter · F30", () -> M11_OrbitPainterTest.main(new String[0]));
        run("M12_PendulumWaves", "L10.12 PendulumWaves · F30", () -> M12_PendulumWavesTest.main(new String[0]));
        run("M13_RippleTank", "L10.13 RippleTank · F30", () -> M13_RippleTankTest.main(new String[0]));
        run("M14_Snake", "L10.14 Snake · F30", () -> M14_SnakeTest.main(new String[0]));
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

@SuppressWarnings("unchecked")
class M01_AsteroidDriftTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) nevystřelí.",
        "Spuštění callback i skutečná držená klávesa vystřelí jednou.",
        "Cooldown i prohra dál brání nové střele."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I051: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M01_AsteroidDrift");
            Reflect.call(p, "restart");var action=((java.lang.Runnable) Reflect.call(p, "firingAction"));check(((java.util.ArrayList<Object>) Reflect.get(p, "shots")).isEmpty(),0,((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size());
            action.run();check(((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size()==1&&((int) Reflect.get(p, "cooldown"))==10,1,((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size());
            action.run();check(((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size()==1,2,((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size());
            Reflect.call(p, "restart");Reflect.call(p, "key", java.awt.event.KeyEvent.VK_SPACE, true);Reflect.call(p, "step");check(((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size()==1,1,((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size());
            Reflect.set(p, "lost", true);Reflect.set(p, "cooldown", 0);((java.lang.Runnable) Reflect.call(p, "firingAction")).run();check(((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size()==1,2,((java.util.ArrayList<Object>) Reflect.get(p, "shots")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I051: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_FireworkPainterTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) nevytvoří částice.",
        "Spuštění vytvoří šedesát částic v zadaných nestejných coordinate (souřadnicích).",
        "Skutečný press přidá další výbuch a restart vše smaže."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I072: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M02_FireworkPainter");
            Reflect.call(p, "restart");var action=((java.util.function.BiConsumer<java.lang.Integer, java.lang.Integer>) Reflect.call(p, "burstAction"));check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).isEmpty(),0,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            action.accept(43,91);check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size()==60&&!((boolean) Reflect.get(p, "paused")),1,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            check(((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst(), "x"))==43&&((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst(), "y"))==91,1,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getFirst());
            Reflect.call(p, "press", 120, 80, 1);check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size()==120&&((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).getLast(), "x"))==120,2,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
            Reflect.call(p, "restart");check(((java.util.ArrayList<Object>) Reflect.get(p, "particles")).isEmpty(),2,((java.util.ArrayList<Object>) Reflect.get(p, "particles")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I072: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_ShadowPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření callback (předané akce) zachová počáteční pozici světla.",
        "Spuštění i skutečné press/drag použijí zadané x/y.",
        "Coordinate (souřadnice) mimo plátno omezí moveLight."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I078: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M03_ShadowPlayground");
            Reflect.call(p, "restart");var action=((java.util.function.BiConsumer<java.lang.Integer, java.lang.Integer>) Reflect.call(p, "lightAction"));check(((double) Reflect.get(p, "lightX"))==100&&((double) Reflect.get(p, "lightY"))==80,0,((double) Reflect.get(p, "lightX")));
            action.accept(43,91);check(((double) Reflect.get(p, "lightX"))==43&&((double) Reflect.get(p, "lightY"))==91,1,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
            action.accept(-20,999);check(((double) Reflect.get(p, "lightX"))==0&&((double) Reflect.get(p, "lightY"))==((int) Reflect.get(p, "HEIGHT")),2,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
            Reflect.call(p, "press", 140, 70, 1);Reflect.call(p, "drag", 160, 90);check(((double) Reflect.get(p, "lightX"))==160&&((double) Reflect.get(p, "lightY"))==90,1,((double) Reflect.get(p, "lightX"))+","+((double) Reflect.get(p, "lightY")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I078: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_UntangleTest {
    static int checks;
    static final String[] CASES = {
        "Nestejný bod vykreslí správně vystředěný uzel.",
        "Kreslení nezmění Point ani model (model pravidel).",
        "Skutečná celá scéna používá opravený helper (pomocnou funkci)."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I056: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M04_Untangle");
            Reflect.call(p, "restart");var point=new Point(120,70);var image=draw(g->Reflect.call(p, "drawNode", g, point, 3),600,420);
            check((image.getRGB(128,70)&0xFFFFFF)==0x23375A,0,Integer.toHexString(image.getRGB(128,70)));
            check(point.equals(new Point(120,70)),1,point);
            ((java.awt.Point[]) Reflect.get(p, "points"))[0]=new Point(80,90);var scene=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),600,420);
            check((scene.getRGB(88,90)&0xFFFFFF)==0x23375A,2,Integer.toHexString(scene.getRGB(88,90)));
            var copy=((java.awt.Point[]) Reflect.get(p, "points"))[0].getLocation();draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),600,420);check(((java.awt.Point[]) Reflect.get(p, "points"))[0].equals(copy),1,((java.awt.Point[]) Reflect.get(p, "points"))[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I056: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_SpringToyTest {
    static int checks;
    static final String[] CASES = {
        "Rendering (vykreslení) zachová všechny hodnoty pohybu.",
        "Opakované kreslení dá stejné pixel (body obrazu) a nepřidá stopu.",
        "Výslovný step model dál posouvá."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I070: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M05_SpringToy");
            Reflect.call(p, "restart");Reflect.set(p, "paused", false);double x=((double) Reflect.get(p, "x")),y=((double) Reflect.get(p, "y")),vx=((double) Reflect.get(p, "vx")),vy=((double) Reflect.get(p, "vy"));
            var first=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));
            check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&((double) Reflect.get(p, "vx"))==vx&&((double) Reflect.get(p, "vy"))==vy,0,((double) Reflect.get(p, "y")));
            check(((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).isEmpty(),1,((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).size());
            var second=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(hash(first)==hash(second),1,hash(second));
            Reflect.call(p, "step");check(((double) Reflect.get(p, "y"))!=y&&!((java.util.ArrayList<java.awt.geom.Point2D.Double>) Reflect.get(p, "trail")).isEmpty(),2,((double) Reflect.get(p, "y")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I070: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_MirrorArtTest {
    static int checks;
    static final String[] CASES = {
        "Běžný nestejný bod má popsanou pozici na obrazovce.",
        "Střed/otočení či hranice hloubky zůstanou správné.",
        "Skutečné kreslení používá transformaci a zachová state (stav) modelu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I074: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M06_MirrorArt");
            Reflect.call(p, "restart");var c=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 320, 220, 0, false));check(c.distance(320,220)<1e-9,0,c);Reflect.set(p, "axes", 4);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 350, 220, 1, false));check(q.distance(320,250)<1e-9,1,q);var r=((java.awt.geom.Point2D.Double) Reflect.call(p, "transform", 320, 250, 0, true));check(r.distance(320,190)<1e-9,1,r);long before=hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")));Reflect.call(p, "stroke", 350, 220, 360, 230);check(hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")))!=before,2,hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas"))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I074: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_MoireTest {
    static int checks;
    static final String[] CASES = {
        "Běžný nestejný bod má popsanou pozici na obrazovce.",
        "Střed/otočení či hranice hloubky zůstanou správné.",
        "Skutečné kreslení používá transformaci a zachová state (stav) modelu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I076: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M07_Moire");
            Reflect.call(p, "restart");var c=((java.awt.geom.Point2D.Double) Reflect.call(p, "gridPoint", 0, 0, 0));check(c.distance(320,220)<1e-9,0,c);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "gridPoint", 1, 0, Math.PI/2));check(q.distance(320,242)<1e-8,1,q);double old=((double) Reflect.get(p, "angle"));var a=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));var b=draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(((double) Reflect.get(p, "angle"))==old&&hash(a)==hash(b),2,((double) Reflect.get(p, "angle")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I076: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_StarTunnelTest {
    static int checks;
    static final String[] CASES = {
        "Běžný nestejný bod má popsanou pozici na obrazovce.",
        "Střed/otočení či hranice hloubky zůstanou správné.",
        "Skutečné kreslení používá transformaci a zachová state (stav) modelu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I077: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M08_StarTunnel");
            Reflect.call(p, "restart");var star=Reflect.create("M08_StarTunnel$Star", 10, 20, 100);var q=((java.awt.geom.Point2D.Double) Reflect.call(p, "project", star, 100));check(q.distance(((double) Reflect.get(p, "centreX"))+26,((double) Reflect.get(p, "centreY"))+52)<1e-9,0,q);var near=((java.awt.geom.Point2D.Double) Reflect.call(p, "project", star, 0));check(near.distance(((double) Reflect.get(p, "centreX"))+520,((double) Reflect.get(p, "centreY"))+1040)<1e-9,1,near);var before=List.copyOf(((java.util.ArrayList<Object>) Reflect.get(p, "stars")));draw(((java.util.function.Consumer<java.awt.Graphics2D>) ((value0) -> { Reflect.call(p, "draw", value0); })),((int) Reflect.get(p, "WIDTH")),((int) Reflect.get(p, "HEIGHT")));check(((java.util.ArrayList<Object>) Reflect.get(p, "stars")).equals(before),2,((java.util.ArrayList<Object>) Reflect.get(p, "stars")).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I077: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_PongTest {
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
            var p = Reflect.create("M09_Pong");
            Reflect.call(p, "restart");double x=((double) Reflect.get(p, "ballX")),y=((double) Reflect.get(p, "ballY"));Reflect.call(p, "step");check(((double) Reflect.get(p, "ballX"))==x+4&&((double) Reflect.get(p, "ballY"))==y+2.5,0,((double) Reflect.get(p, "ballX")));Reflect.set(p, "winner", 1);x=((double) Reflect.get(p, "ballX"));y=((double) Reflect.get(p, "ballY"));Reflect.set(p, "leftDown", true);Reflect.call(p, "step");check(((double) Reflect.get(p, "ballX"))==x&&((double) Reflect.get(p, "ballY"))==y&&((double) Reflect.get(p, "leftY"))==160,1,((double) Reflect.get(p, "ballX")));Reflect.call(p, "clearKeys");check(!((boolean) Reflect.get(p, "leftDown"))&&!((boolean) Reflect.get(p, "leftUp"))&&!((boolean) Reflect.get(p, "rightUp"))&&!((boolean) Reflect.get(p, "rightDown")),2,((boolean) Reflect.get(p, "leftDown")));Reflect.call(p, "restart");check(((int) Reflect.get(p, "winner"))==0&&((int) Reflect.get(p, "leftScore"))==0&&((int) Reflect.get(p, "rightScore"))==0,2,((int) Reflect.get(p, "winner")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I047: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_BreakoutTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
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
            var p = Reflect.create("M10_Breakout");
            Reflect.call(p, "restart");double x=((double) Reflect.get(p, "ballX")),y=((double) Reflect.get(p, "ballY"));Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "ballX"))-x-3.2)<1e-9&&Math.abs(((double) Reflect.get(p, "ballY"))-y+3.5)<1e-9,0,((double) Reflect.get(p, "ballX")));Reflect.set(p, "state", "GAME OVER");x=((double) Reflect.get(p, "ballX"));y=((double) Reflect.get(p, "ballY"));Reflect.call(p, "step");check(((double) Reflect.get(p, "ballX"))==x&&((double) Reflect.get(p, "ballY"))==y&&((int) Reflect.get(p, "lives"))==3,1,((double) Reflect.get(p, "ballY")));Reflect.set(p, "state", "YOU WIN");Reflect.call(p, "step");check(((double) Reflect.get(p, "ballX"))==x&&((double) Reflect.get(p, "ballY"))==y,1,((double) Reflect.get(p, "ballX")));Reflect.call(p, "restart");check(((java.lang.String) Reflect.get(p, "state")).equals("PLAY")&&((int) Reflect.get(p, "lives"))==3&&((int) Reflect.get(p, "score"))==0,2,((java.lang.String) Reflect.get(p, "state")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I048: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_OrbitPainterTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I068: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M11_OrbitPainter");
            Reflect.call(p, "restart");double x=((double) Reflect.get(p, "x")),y=((double) Reflect.get(p, "y"));Reflect.call(p, "step");check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&((int) Reflect.get(p, "ticks"))==0,0,((int) Reflect.get(p, "ticks")));Reflect.set(p, "paused", false);Reflect.call(p, "step");check(((int) Reflect.get(p, "ticks"))==1&&(((double) Reflect.get(p, "x"))!=x||((double) Reflect.get(p, "y"))!=y),1,((int) Reflect.get(p, "ticks")));Reflect.set(p, "paused", true);x=((double) Reflect.get(p, "x"));y=((double) Reflect.get(p, "y"));long ink=hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")));Reflect.call(p, "step");check(((double) Reflect.get(p, "x"))==x&&((double) Reflect.get(p, "y"))==y&&hash(((java.awt.image.BufferedImage) Reflect.get(p, "canvas")))==ink,0,((int) Reflect.get(p, "ticks")));Reflect.call(p, "restart");check(((boolean) Reflect.get(p, "paused"))&&((int) Reflect.get(p, "ticks"))==0&&((double) Reflect.get(p, "x"))==440,2,((double) Reflect.get(p, "x")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I068: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M12_PendulumWavesTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I071: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M12_PendulumWaves");
            Reflect.call(p, "restart");Reflect.call(p, "step");check(((double) Reflect.get(p, "time"))==0,0,((double) Reflect.get(p, "time")));Reflect.set(p, "paused", false);Reflect.set(p, "speed", 2);Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "time"))-.04)<1e-12,1,((double) Reflect.get(p, "time")));Reflect.call(p, "adjust", 100);check(((double) Reflect.get(p, "speed"))==2,2,((double) Reflect.get(p, "speed")));Reflect.call(p, "adjust", -100);check(((double) Reflect.get(p, "speed"))==.25,2,((double) Reflect.get(p, "speed")));Reflect.call(p, "restart");check(((double) Reflect.get(p, "time"))==0&&((boolean) Reflect.get(p, "paused"))&&((double) Reflect.get(p, "speed"))==1,2,((double) Reflect.get(p, "time")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I071: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M13_RippleTankTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I073: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M13_RippleTank");
            Reflect.call(p, "restart");Reflect.call(p, "tap", 20, 10);check(!((boolean) Reflect.get(p, "paused"))&&((double[][]) Reflect.get(p, "height"))[10][20]==60,0,((double[][]) Reflect.get(p, "height"))[10][20]);Reflect.call(p, "step");check(((double[][]) Reflect.get(p, "previous"))[10][20]==60&&Math.abs(((double[][]) Reflect.get(p, "height"))[10][21]-29.76)<1e-9,1,((double[][]) Reflect.get(p, "height"))[10][21]);Reflect.call(p, "restart");Reflect.call(p, "tap", 0, 0);check(((boolean) Reflect.get(p, "paused"))&&((double) Reflect.call(p, "energy"))==0,2,((double) Reflect.call(p, "energy")));Reflect.call(p, "tap", 20, 10);Reflect.set(p, "paused", true);double energy=((double) Reflect.call(p, "energy"));Reflect.call(p, "step");check(((double) Reflect.call(p, "energy"))==energy,2,((double) Reflect.call(p, "energy")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I073: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M14_SnakeTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený, čekající či běžný input (vstup) respektuje popsanou hranici kroku.",
        "Aktivní tick (časový krok) změní model právě jednou; dokončený či pozastavený jej neposouvá.",
        "Neplatné hranice, restart či úklid vstupu zůstanou správné."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I046: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M14_Snake");
            Reflect.call(p, "restart");var head=((java.util.List<Object>) Reflect.get(p, "body")).getFirst();int dx=((int) Reflect.get(p, "dx")),dy=((int) Reflect.get(p, "dy"));Reflect.call(p, "turn", 0, -1);check(((int) Reflect.get(p, "dx"))==dx&&((int) Reflect.get(p, "dy"))==dy&&((boolean) Reflect.call(((java.util.List<Object>) Reflect.get(p, "body")).getFirst(), "equals", head)),0,((java.util.List<Object>) Reflect.get(p, "body")).getFirst());Reflect.call(p, "turn", -1, 0);Reflect.call(p, "step");check(((boolean) Reflect.call(((java.util.List<Object>) Reflect.get(p, "body")).getFirst(), "equals", Reflect.create("M14_Snake$Cell", ((int) Reflect.call(head, "x")), ((int) Reflect.call(head, "y"))-1))),1,((java.util.List<Object>) Reflect.get(p, "body")).getFirst());Reflect.set(p, "lost", true);var stopped=List.copyOf(((java.util.List<Object>) Reflect.get(p, "body")));Reflect.call(p, "step");check(((java.util.List<Object>) Reflect.get(p, "body")).equals(stopped),2,((java.util.List<Object>) Reflect.get(p, "body")));Reflect.call(p, "restart");check(!((boolean) Reflect.get(p, "lost"))&&((int) Reflect.get(p, "score"))==0,2,((int) Reflect.get(p, "score")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I046: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

// Připravený přístup ke compact programům. Testy používají běžné assertions, pole a JDK collections.
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
}
