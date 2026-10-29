import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Kontrola herních úloh této kapitoly. Krátké učebnicové úkoly spouštěj samostatně.
class Check {
    int failed;
    int checked;
    String selected;
    final ArrayList<String> available = new ArrayList<>();
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) {
            IO.println("Zadej nejvýše jeden název herního souboru. Bez argumentu se zkontrolují všechny herní úlohy kapitoly.");
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
            IO.println("Pro tento soubor zde není připravená herní kontrola: " + selected + ".java");
            IO.println("Vyber jeden z těchto souborů: " + String.join(", ", available));
            System.exit(2);
        }
        IO.println("Kontroly: " + checked + "; hotovo: " + (checked - failed) + "; zbývá: " + failed);
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
            if (reason == null || reason.isBlank()) reason = "Kontrola nedostala očekávaný výsledek.";
            if (!(failure instanceof AssertionError)) reason = "Program se při kontrole zastavil: " + failure.getClass().getSimpleName() + ": " + reason;
            IO.println("  " + reason.replace("\n", "\n  "));
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            if (cause != failure) IO.println("  " + cause.getClass().getSimpleName() + ": " + show(cause.getMessage()));
            for (var frame : cause.getStackTrace()) {
                if (frame.getClassName().equals(game) || frame.getClassName().startsWith(game + "$")) {
                    IO.println("  " + "Místo v programu: " + frame.getFileName() + ":" + frame.getLineNumber());
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
            return parts + (length > 8 ? " … (" + length + " položek" + ")" : "");
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
        check(image.getRGB(5 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "Levé oko není bílé. Metoda render musí pro toto oko použít společnou hodnotu eyeColour.");
        check(image.getRGB(10 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "Pravé oko není bílé. Obě pozice očí musí používat stejnou hodnotu eyeColour.");
        check(((int) Reflect.get(game, "eyeColour")) == Color.WHITE.getRGB(), "Společná hodnota eyeColour má být bílá, aby se obě oči změnily spolu. Nepřekresluj jen jedno oko zvlášť.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_UntangleTest {
    static int checks;
    static final String[] CASES = {
        "Metoda drawNode musí umístit střed uzlu do předaného Point. Pro Point(120,70) má uzel pokrýt bod (128,70); ověř role x a y.",
        "Kreslení uzlu změnilo jeho Point. Metody drawNode a draw mají pozice číst, nikoli posouvat model.",
        "Opravené souřadnice uzlu musí používat také kreslení celé scény v draw. Uzel na (80,90) má mít střed právě tam."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P05_Task_MirrorArtTest {
    static int checks;
    static final String[] CASES = {
        "Metoda transform musí zachovat střed kreslení (320,220) na místě.",
        "Při čtyřech osách musí jeden sektor otočit (350,220) na (320,250). Zrcadlení musí změnit (320,250) na (320,190).",
        "Metoda stroke musí použít transform a přidat na plátno viditelnou kresbu. Transformované kreslení nesmí nechat plátno beze změny."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P06_Task_MoireTest {
    static int checks;
    static final String[] CASES = {
        "Volání gridPoint(0,0,0) musí vrátit střed obrazovky (320,220). Ověř převod souřadnic mřížky na souřadnice obrazovky.",
        "Volání gridPoint(1,0,Math.PI/2) musí vrátit (320,242), tedy bod o jednu rozteč pod středem po čtvrtotáčce.",
        "Metoda draw musí zachovat angle a při opakovaných voláních vytvořit stejné obrazové body. Překreslení nesmí model otáčet."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P07_Task_StarTunnelTest {
    static int checks;
    static final String[] CASES = {
        "Metoda project pro Star(10,20,100) v hloubce 100 musí umístit bod 26 bodů doprava a 52 dolů od středu obrazovky.",
        "Metoda project musí před dělením omezit nulovou hloubku. Testovací hvězda má potom mít posun (520,1040) od středu.",
        "Metoda draw změnila uložené hvězdy. Promítej a kresli je bez změny jejich hodnot nebo pořadí."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P10_Task_CrossingRush");
        Reflect.call(g, "restart");
        Reflect.call(g, "hop", -1000, 0);
        equal(12, ((int) Reflect.get(g, "x")), "Po velkém skoku doleva se musí střed hráče zastavit na x=12, aby celý hráč zůstal na cestě.");
        Reflect.call(g, "hop", 1000, 0);
        equal(588, ((int) Reflect.get(g, "x")), "Po velkém skoku doprava se musí střed hráče zastavit na x=588, aby pravý okraj zůstal uvnitř cesty.");
        ((java.util.ArrayList<Object>) Reflect.get(g, "cars")).clear();
        Reflect.call(g, "hop", 0, -500);
        expect(((boolean) Reflect.get(g, "won")), "Dosažení horního cíle bez srážky s autem musí dál nastavit won. Při opravě hop zachovej úspěšný přechod.");
        int x = ((int) Reflect.get(g, "x"));
        Reflect.call(g, "hop", -30, 0);
        equal(x, ((int) Reflect.get(g, "x")), "Metoda hop posunula hráče po výhře. Po skončení kola musí další požadavky na pohyb pozici zachovat.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_SnakeTest {
    static int checks;
    static final String[] CASES = {
        "Metoda turn změnila potvrzený směr nebo posunulo hlavu před tikem. Ulož požadovanou zatáčku a do step zachovej dx, dy i tělo.",
        "Následující step musí použít první čekající zatáčku právě jednou. Druhá klávesa před tímto krokem ji nesmí nahradit ani hada předčasně posunout.",
        "Metoda step musí hada po prohře nechat stát. Metoda restart musí zrušit prohru a obnovit nulové skóre."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
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
        equal(1, Reflect.get(g, "score"), "První skutečná srážka má odstranit jednoho vetřelce a přidat právě jeden bod. Ověř zpracování zásahu používané v step.");
        expect((boolean) Reflect.get(g, "bulletActive"), "Průrazný výstřel zmizel po prvním vetřelci. Nech jej aktivní až do druhého platného zásahu.");
        Reflect.call(g, "hitAlien", 2, 0);
        equal(1, Reflect.get(g, "score"), "Metoda hitAlien znovu započítala již odstraněného vetřelce. Opakovaný kontakt se stejnou prázdnou buňkou nesmí přidat další bod.");
        Reflect.set(g, "bulletY", (double) Reflect.get(g, "formationY") + 34 + 17);
        Reflect.call(g, "step");
        equal(2, Reflect.get(g, "score"), "Tentýž výstřel má zasáhnout druhého vetřelce a zvýšit skóre na dva. Mezi těmito dvěma srážkami jej zachovej.");
        expect(!(boolean) Reflect.get(g, "bulletActive"), "Výstřel zůstává aktivní po druhém platném zásahu. Každý výstřel smí odstranit nejvýše dva vetřelce.");
        Reflect.call(g, "hitAlien", 0, 0);
        equal(2, Reflect.get(g, "score"), "Vyčerpaný výstřel přidal třetí bod. Metoda hitAlien musí po ukončení tohoto výstřelu další cíle ignorovat.");
        Reflect.set(g, "fire", true); Reflect.call(g, "step"); Reflect.set(g, "fire", false);
        Reflect.call(g, "hitAlien", 0, 0);
        expect((boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 3, "Nový výstřel potřebuje vlastní povolené dva zásahy. Jeho první zásah má přidat bod a ponechat tento výstřel aktivní.");
        Reflect.call(g, "hitAlien", 0, 1);
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 4, "Také nový výstřel musí po dvou platných zásazích skončit. Povolený počet obnovuj pro každý výstřel, ale nedělej jej neomezený.");
        Reflect.call(g, "restart");
        expect(!(boolean) Reflect.get(g, "bulletActive") && (int) Reflect.get(g, "score") == 0 && (int) Reflect.call(g, "countAliens") == 15, "Metoda restart musí obnovit všech 15 vetřelců, vynulovat skóre a odstranit aktivní výstřel.");
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P15_Task_SoftLanding");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "fuel", 0);
        Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "Tah nahoru musí fungovat i při fuel=0. Po jednom volání step má být s původním tahem a gravitací svislá rychlost -0.05.");
        equal(0, Reflect.get(g, "fuel"), "Tréninkový tah změnil nulový ukazatel paliva. V tomto režimu musí použití motorů ponechat fuel beze změny.");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.set(g, "left", true); Reflect.set(g, "right", true); Reflect.set(g, "up", true);
        Reflect.call(g, "step");
        equal(240, Reflect.get(g, "fuel"), "Použití několika motorů spotřebovalo palivo. Trénink musí zachovat původní hodnotu ukazatele 240 stejně jako nulu.");
        expect(Math.abs((double) Reflect.get(g, "velocityX") - 1.2) < 1e-9 && Math.abs((double) Reflect.get(g, "velocityY") + 0.05) < 1e-9, "Při současném Left, Right a Up se má vodorovný tah vyrušit, ale Up má dál brzdit pád. Zachovej původní sílu obou druhů motorů.");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        Reflect.call(g, "step");
        expect(Math.abs((double) Reflect.get(g, "velocityY") - 0.04) < 1e-9, "Bez stisknuté klávesy tahu musí step použít samotnou gravitaci; svislá rychlost má vzrůst na 0.04.");
        Reflect.set(g, "state", "CRASH");
        double y = (double) Reflect.get(g, "y");
        Reflect.set(g, "up", true); Reflect.call(g, "step");
        equal(y, Reflect.get(g, "y"), "Metoda step posunula havarovaný modul. Stav CRASH musí zůstat konečný i při stisknutém Up.");
        Reflect.call(g, "restart"); Reflect.call(g, "begin");
        expect(!(boolean) Reflect.get(g, "left") && !(boolean) Reflect.get(g, "right") && !(boolean) Reflect.get(g, "up"), "Metoda restart ponechala aktivní klávesu motoru. Uvolni Left, Right a Up, aby další let nezačal se stisknutým tahem.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_AsteroidDriftTest {
    static int checks;
    static final String[] CASES = {
        "Držení F musí střílet přes key a step s původní prodlevou mezi výstřely. Po prvních dvou ticích má existovat jen jeden výstřel.",
        "Space musí při každém aktivním tiku zpomalit obě složky rychlosti o 8 % a nevytvořit výstřel. Ověř akci stisknuté klávesy používanou v step.",
        "R musí uvolnit klávesy, vynulovat prodlevu střelby a čekat na nový start. Prohraný let nesmí střílet přes firingAction."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
        "Volání burstAction má pouze vytvořit akci, nikoli částice. Ohňostroj spusť až při zavolání vrácené akce.",
        "Zavolání akce na (43,91) musí vytvořit 120 částic v tomto bodě a jeho zrcadleném protějšku a obnovit animaci.",
        "Metoda press musí použít stejnou akci dvojitého ohňostroje: další kliknutí přidá další zrcadlenou dvojici. Restart potom musí odstranit všechny částice."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P18_Task_ShadowPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Vytvoření lightAction posunulo lampu. Počáteční polohu (100,80) zachovej, dokud vrácená akce nedostane souřadnice.",
        "Akce lampy musí zaokrouhlit (43,91) dolů na (40,80). Stejné pravidlo mřížky po 20 bodech musí používat i skutečné press a drag.",
        "Souřadnice mimo plochu musí dál projít přes moveLight. Požadavek (-20,999) má ponechat lampu na (0,HEIGHT)."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
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
        equal(170.0, ((double) Reflect.get(g, "x")), "Po boční srážce musí step umístit kuličku na x=170 mimo překážku. Samotné obrácení rychlosti ji nechá uvnitř.");
        expect(((double) Reflect.get(g, "vx")) < 0 && !((boolean) Reflect.call(g, "hit", ((java.awt.Rectangle[]) Reflect.get(g, "obstacles"))[0])), "Odražená kulička musí mít obrácenou vodorovnou rychlost a už se nesmí překrývat s překážkou. Ověř opravu pozice i odraz.");
        Reflect.call(g, "clearKeys");
        for (int n = 0; n < 100; n++) {
            Reflect.call(g, "step");
        }
        expect(Double.isFinite(((double) Reflect.get(g, "x"))) && Double.isFinite(((double) Reflect.get(g, "y"))), "Také po dalších krocích musí souřadnice kuličky zůstat konečnými čísly. Oprava srážky nesmí vytvořit NaN ani nekonečné pozice.");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 530);
        Reflect.set(g, "y", 345);
        Reflect.set(g, "vx", ((double) Reflect.set(g, "vy", 0)));
        Reflect.call(g, "step");
        expect(((boolean) Reflect.get(g, "won")), "Pomalý vjezd do zeleného cíle musí dál vyhrát. Při opravě srážek zachovej dosavadní kontrolu cíle.");
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P22_Task_PlatformDash");
        Reflect.call(g, "restart");
        Reflect.set(g, "started", true);
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 285);
        Reflect.set(g, "vy", -4);
        Reflect.call(g, "step");
        expect(!((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "y")) < 285, "Metoda step přichytila hráče na plošinu při stoupání zespodu. Pohyb vzhůru přes její spodní stranu se nesmí počítat jako přistání.");
        Reflect.set(g, "x", 240);
        Reflect.set(g, "y", 240);
        Reflect.set(g, "vy", 12);
        Reflect.call(g, "step");
        equal(252.0, ((double) Reflect.get(g, "y")), "Překročení horní hrany plošiny směrem dolů musí dál přistát na y=252. Ověř předchozí spodní hranu hráče i novou pozici.");
        expect(((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "vy")) == 0, "Platné přistání musí nastavit grounded a zastavit svislou rychlost. Po umístění na plošinu nenechávej hráče dál padat.");
        Reflect.call(g, "restart");
        ((boolean[]) Reflect.get(g, "held"))[java.awt.event.KeyEvent.VK_RIGHT] = true;
        Reflect.call(g, "jump");
        for (int tick = 0; tick < 200 && !((boolean) Reflect.get(g, "won")); tick++) {
            Reflect.call(g, "step");
            if (((boolean) Reflect.get(g, "grounded")) && ((double) Reflect.get(g, "x")) < 380) {
                Reflect.call(g, "jump");
            }
        }
        expect(((boolean) Reflect.get(g, "won")) && ((int) Reflect.get(g, "falls")) == 0, "Původní cesta má dál dojít k vlajce dvěma skoky bez pádu. Po opravě zachovej hratelný běžný pohyb i přistávání.");
    }
}

@SuppressWarnings("unchecked")
class P23_Task_PongTest {
    static int checks;
    static final String[] CASES = {
        "Při zapnutém botovi musí step posunout pravou pálku o tři body k aktuální poloze míčku. Ověř, že běžící změna skutečně používá rozhodnutí bota.",
        "Bot musí stát do osmi bodů od středu pálky a pohybovat se mimo toto pásmo. Zachovej lidskou rychlost pět bodů a omez pálku na hřiště.",
        "Přepnutí soupeře nesmí posunout pálku ani zachovat stisknutý směr. Skončený zápas musí stát; restart musí zrušit vítěze a vrátit lidské ovládání."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
        "Pozastavený step musí zachovat pozici, počet tiků i nakreslenou stopu. Oběžnou dráhu posouvej jen při paused=false.",
        "Jeden aktivní step musí posunout pozici a zvětšit ticks právě o jedna. Běžící oběžná dráha tuto změnu neprovedla.",
        "Metoda restart musí obnovit x=440, ticks=0 a paused=true, aby nová oběžná dráha čekala na hráče."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P25_Task_PendulumWavesTest {
    static int checks;
    static final String[] CASES = {
        "Pozastavený step musí ponechat společný time na nule. Překreslení ani čekání při pauze nesmí kyvadla posouvat.",
        "Při speed=2 musí jeden běžící step posunout time o 0.04. Společné hodiny měň jednou za aktivní tik.",
        "Udrž rychlost v rozsahu 0.25..2. Metoda restart musí obnovit speed=1, time=0 a pozastavený stav."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P28_Task_SpringToyTest {
    static int checks;
    static final String[] CASES = {
        "Metoda draw změnila x, y nebo rychlost. Kreslení má model pružiny jen pozorovat; pohyb patří do step.",
        "Opakovaná volání draw musí dát stejné body bez přidávání bodů stopy. Překreslení není další krok simulace.",
        "Výslovné volání step musí dál posunout závaží a přidat bod stopy. Odstraň pohyb z draw, ale nevypni skutečnou změnu stavu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P29_Task_BreakoutTest {
    static int checks;
    static final String[] CASES = {
        "Minutý míček ukončil trénink nebo se znovu nepodal. Metoda step musí dovolit neomezeně minutí, nikoli jen větší počet životů.",
        "Minutí smazalo postup. Nové podání musí zachovat skóre i všechny již odstraněné cihly.",
        "Metoda restart musí obnovit všech 18 cihel a nulové skóre. Poslední cihla musí dál vyhrát a další kroky musí nechat vítězné kolo bez pohybu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
        "Platné volání tap na (20,10) musí přidat výchylku 60 a spustit pohyb vody. Po tomto vstupu nenechávej nádrž pozastavenou.",
        "Po platném volání tap musí step rozšířit vlnu: sousední buňka má mít hodnotu 29.76. Ověř, že vstup skutečně obnovil změny stavu.",
        "Klepnutí na okraj musí nechat novou nádrž klidnou a prázdnou. Při pauze musí step zachovat i dosavadní energii vody."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P31_Task_PictureMusicTest {
    static int checks;
    static final String[] CASES = {
        "Metoda pitchFrequency s posunem +12 musí zdvojnásobit původní frekvenci. Metoda signal musí posun použít, aby se změnily také skutečné zvukové vzorky.",
        "Při posunu 0 zachovej původní frekvenci a při -12 ji zmenši na polovinu. Konkrétně řádek 11 při posunu 0 musí dál dát 220 Hz.",
        "Metoda pitchFrequency musí odmítnout řádek 12 a posun 13 výjimkou IllegalArgumentException. Při přidání transpozice zachovej původní meze vstupu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P32_Task_SecretBeepsTest {
    static int checks;
    static final String[] CASES = {
        "Volání units(\"E E\",10) musí použít deset jednotek mezi slovy a signal musí zahrnout delší ticho. Samotná změna zobrazeného nastavení nestačí.",
        "Zachovej původní sedmijednotkovou mezeru mezi slovy i tříjednotkovou mezeru mezi písmeny. Měnit se má jen požadovaná mezera mezi slovy.",
        "Metoda units musí odmítnout mezery mezi slovy 2 a 13 výjimkou IllegalArgumentException; povolený rozsah je 3..12."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Kontrolovaný případ / výsledek: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "vstup byl přijat");
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
            throw new AssertionError("Program se při ověřování pravidla zastavil. Podrobnosti výjimky jsou níže.", failure);
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
            throw new IllegalStateException("Nelze načíst program. Ověř jeho soubor vedle Check.java: " + name, failure);
        }
    }

    static Object create(String name, Object... args) {
        var matches = new ArrayList<Constructor<?>>();
        for (var constructor : type(name).getDeclaredConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) matches.add(constructor);
        }
        var constructor = one(matches, "vytvoření objektu " + name);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze vytvořit objekt programu: " + name, failure);
        }
    }

    static Object get(Object target, String name) {
        try {
            return field(target, name).get(target instanceof Class<?> ? null : target);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze přečíst proměnnou: " + name, failure);
        }
    }

    static Object set(Object target, String name, Object value) {
        try {
            var field = field(target, name);
            Object receiver = target instanceof Class<?> ? null : target;
            field.set(receiver, value);
            return field.get(receiver);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze změnit proměnnou: " + name, failure);
        }
    }

    private static Field field(Object target, String name) throws NoSuchFieldException {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            try {
                var field = current.getDeclaredField(name);
                if (target instanceof Class<?> && !Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("Pro tuto proměnnou je potřeba objekt: " + name);
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
        var method = one(matches, "metodu " + owner.getName() + "." + name);
        try {
            method.setAccessible(true);
            return method.invoke(target instanceof Class<?> ? null : target, args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze zavolat metodu: " + owner.getName() + "." + name, failure);
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
            throw new IllegalStateException((matches.isEmpty() ? "Nenalezeno odpovídající " : "Nalezeno více možností pro ") + description);
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
