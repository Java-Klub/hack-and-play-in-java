import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_WordLadder.java
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
        run("P03_WordLadder", "12.03 WordLadder · F22", () -> P03_WordLadderTest.main(new String[0]));
        run("P04_TileStampStudio", "12.04 TileStampStudio · F31", () -> P04_TileStampStudioTest.main(new String[0]));
        run("P07_FractalTreeGarden", "12.07 FractalTreeGarden · F31", () -> P07_FractalTreeGardenTest.main(new String[0]));
        run("P08_ImageToASCII", "12.08 ImageToASCII · F31", () -> P08_ImageToASCIITest.main(new String[0]));
        run("P11_MazePostcards", "12.11 MazePostcards · F31", () -> P11_MazePostcardsTest.main(new String[0]));
        run("P14_EndlessToneStaircase", "12.14 EndlessToneStaircase · F32", () -> P14_EndlessToneStaircaseTest.main(new String[0]));
        run("P15_PixelSortingGlitch", "12.15 PixelSortingGlitch · F32", () -> P15_PixelSortingGlitchTest.main(new String[0]));
        run("P16_MirrorCylinderPicture", "12.16 MirrorCylinderPicture · F32", () -> P16_MirrorCylinderPictureTest.main(new String[0]));
        run("P19_SecretPNGPostcard", "12.19 SecretPNGPostcard · F32", () -> P19_SecretPNGPostcardTest.main(new String[0]));
        run("P20_MagicEyeStrip", "12.20 MagicEyeStrip · F32", () -> P20_MagicEyeStripTest.main(new String[0]));
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
class P03_WordLadderTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P03_WordLadder");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", " cord  card ");
        equal(List.of("COLD", "CORD", "CARD"), Reflect.get(g, "history"), "12.03: a planned route preserves every stop");
        Object before = List.copyOf((List<?>) Reflect.get(g, "history"));
        boolean rejected = false;
        try { Reflect.call(g, "command", "WARD COLD"); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "12.03: an invalid later step refuses the whole route");
        equal(before, Reflect.get(g, "history"), "12.03: refused route keeps its original history");
        equal("CARD", Reflect.get(g, "current"), "12.03: refused route keeps its current word");
        Reflect.call(g, "command", "WARD WARM");
        equal("Reached WARM in 4 moves!", Reflect.get(g, "outcome"), "12.03: winning batch counts moves, not batches");
        Reflect.call(g, "reset");
        rejected = false;
        try { Reflect.call(g, "command", "CORD CARD WARD WARM WARD"); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected && ((List<?>) Reflect.get(g, "history")).equals(List.of("COLD")), "12.03: nothing may continue past the target in one batch");
        Reflect.call(g, "command", "CORD");
        equal("CORD", Reflect.get(g, "current"), "12.03: a single ordinary move still works");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_TileStampStudioTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P04_TileStampStudio");
        expect((boolean) Reflect.call(g, "applyRepeats", "3"), "12.04: ordinary numeric choice still works");
        expect((boolean) Reflect.call(g, "applyRepeats", " DENSER "), "12.04: a relative command accepts whitespace and case");
        equal(6, Reflect.get(g, "repeats"), "12.04: denser doubles the current setting");
        Reflect.call(g, "applyRepeats", "denser");
        equal(12, Reflect.get(g, "repeats"), "12.04: a command sequence reaches twelve");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "denser") && (int) Reflect.get(g, "repeats") == 12, "12.04: oversized relative choice preserves its setting");
        Reflect.call(g, "applyRepeats", "sparser"); equal(6, Reflect.get(g, "repeats"), "12.04: sparser halves an even setting");
        Reflect.call(g, "applyRepeats", "3");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "sparser") && (int) Reflect.get(g, "repeats") == 3, "12.04: odd halving is refused without rounding");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "oops") && (int) Reflect.get(g, "repeats") == 3, "12.04: typo preserves the live setting");
        Reflect.call(g, "paint", 0, 0, 1);
        var three = (java.awt.image.BufferedImage) Reflect.call(g, "render", 3);
        var six = (java.awt.image.BufferedImage) Reflect.call(g, "render", 6);
        expect(three.getWidth() == 288 && six.getWidth() == 288 && three.getRGB(6, 0) != six.getRGB(6, 0), "12.04: relative choices have visible density feedback");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_FractalTreeGardenTest {
    static int checks;
    static final String[] CASES = {
        "Nečíselný text vrátí 28.",
        "Obě zahrnuté hranice i okolní mezery fungují.",
        "Číselný input (vstup) mimo rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.07: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P07_FractalTreeGarden");
            check(((double) Reflect.call(p, "readAngle", "oops"))==28,0,((double) Reflect.call(p, "readAngle", "oops")));
            check(((double) Reflect.call(p, "readAngle", " 10 "))==10&&((double) Reflect.call(p, "readAngle", "70"))==70,1,"bounds");
            reject(()->Reflect.call(p, "readAngle", "9"),2);
            reject(()->Reflect.call(p, "readAngle", "71"),2);
            reject(()->Reflect.call(p, "readAngle", "NaN"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 12.07: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P08_ImageToASCIITest {
    static int checks;
    static final String[] CASES = {
        "Nečíselný text vrátí 80.",
        "Obě zahrnuté hranice i okolní mezery fungují.",
        "Číselný input (vstup) mimo rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.08: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P08_ImageToASCII");
            check(((int) Reflect.call(p, "readWidth", "oops"))==80,0,((int) Reflect.call(p, "readWidth", "oops")));
            check(((int) Reflect.call(p, "readWidth", " 4 "))==4&&((int) Reflect.call(p, "readWidth", "160"))==160,1,"bounds");
            reject(()->Reflect.call(p, "readWidth", "3"),2);
            reject(()->Reflect.call(p, "readWidth", "161"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 12.08: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_MazePostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Nečíselný text dá bludiště s 20 sloupci.",
        "Obě zahrnuté hranice se přijmou.",
        "Celá čísla mimo rozsah se odmítnou."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.11: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P11_MazePostcards");
            check(((int) Reflect.call(p, "readColumns", "oops"))==20,0,((int) Reflect.call(p, "readColumns", "oops")));
            check(((int) Reflect.call(p, "readColumns", " 2 "))==2&&((int) Reflect.call(p, "readColumns", "50"))==50,1,"limits");
            reject(()->Reflect.call(p, "readColumns", "1"),2);reject(()->Reflect.call(p, "readColumns", "51"),2);
            var maze=((boolean[][]) Reflect.call(p, "generate", 42, ((int) Reflect.call(p, "readColumns", "oops")), 2));check(maze[0].length==41,0,maze[0].length);
        check(((int) Reflect.call(p, "readColumns", " tiny ")) == 6 && ((int) Reflect.call(p, "readColumns", "WIDE")) == 40, 0, "named maze sizes");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 12.11: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P14_EndlessToneStaircaseTest {
    static int checks;
    static final String[] CASES = {
        "Vznikne celý zadaný vnořený cíl.",
        "Skutečný WAV obsahuje očekávaný počet a frekvenci frame (rámců).",
        "Nadřazený file (soubor) místo adresáře způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.14: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("tone-check-");
        String filename = root.getFileName() + ".wav";
        Path misplaced = Path.of(filename).toAbsolutePath();
        boolean absent = Files.notExists(misplaced);
        try {
            var p = Reflect.create("P14_EndlessToneStaircase");
            if (!absent) throw new IllegalStateException("Unique test filename already exists: " + misplaced);
            Path file = root.resolve("nested").resolve(filename);
            Reflect.call(p, "write", file, new double[]{0, .2, -.2, 0});
            check(Files.isRegularFile(file), 0, file);
            try (var audio = javax.sound.sampled.AudioSystem.getAudioInputStream(file.toFile())) {
                check(audio.getFrameLength() == 4 && audio.getFormat().getSampleRate() == 22050, 1, audio.getFrameLength());
            }
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> Reflect.call(p, "write", blocked.resolve(filename), new double[1]), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 12.14: Unexpected failure while checking the active rule.", failure);
        } finally {
            if (absent) Files.deleteIfExists(misplaced);
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_PixelSortingGlitchTest {
    static int checks;
    static final String[] CASES = {
        "Po exportu existuje přesně zadaný vnořený název.",
        "Načtený PNG je seřazený a původní pixel (body obrazu) zůstanou stejné.",
        "Nepoužitelný nadřazený cíl způsobí očekávanou IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.15: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("pixels-check-");
        try {
            var p = Reflect.create("P15_PixelSortingGlitch");
            var input = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
            input.setRGB(0, 0, 0xFFFFFFFF);
            input.setRGB(1, 0, 0xFF555555);
            input.setRGB(2, 0, 0xFFAAAAAA);
            Path file = root.resolve("nested/glitch.png");
            Reflect.call(p, "saveSorted", file, input, 0);
            check(Files.isRegularFile(file), 0, file);
            var result = ImageIO.read(file.toFile());
            check(result.getWidth() == 3 && (result.getRGB(0, 0) & 0xFFFFFF) == 0x555555, 1, Integer.toHexString(result.getRGB(0, 0)));
            check(input.getRGB(0, 0) == 0xFFFFFFFF, 1, Integer.toHexString(input.getRGB(0, 0)));
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> Reflect.call(p, "saveSorted", blocked.resolve("x.png"), input, 0), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 12.15: Unexpected failure while checking the active rule.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_MirrorCylinderPictureTest {
    static int checks;
    static final String[] CASES = {
        "Vybraný soubor dodá první řádek bez okrajových whitespace (bílých znaků).",
        "Obsah UTF-8 se zachová a caption (nápis) ovlivní skutečný obrázek.",
        "Chybějící soubor i neplatný první řádek ohlásí I/O failure (chybu vstupu/výstupu)."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.16: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("mirror-check-");
        try {
            var p = Reflect.create("P16_MirrorCylinderPicture");
            Path file = root.resolve("nested/word.txt");
            Files.createDirectories(file.getParent());
            Files.writeString(file, "  MOON  \nignored second line\n");
            check(((String) Reflect.call(p, "readCaption", file)).equals("MOON"), 0, Reflect.call(p, "readCaption", file));
            Files.writeString(file, "π!\n");
            check(((String) Reflect.call(p, "readCaption", file)).equals("π!"), 1, Reflect.call(p, "readCaption", file));
            Files.writeString(file, "");
            ioFailure(() -> Reflect.call(p, "readCaption", file), 2);
            Files.writeString(file, "   \nMOON");
            ioFailure(() -> Reflect.call(p, "readCaption", file), 2);
            Files.writeString(file, "ABCDEFGHI");
            ioFailure(() -> Reflect.call(p, "readCaption", file), 2);
            ioFailure(() -> Reflect.call(p, "readCaption", root.resolve("missing.txt")), 2);
            Reflect.set(p, "caption", "JAVA");
            var first = (BufferedImage) Reflect.call(p, "render", 42L, 125.0);
            Reflect.set(p, "caption", "MOON");
            check(hash(first) != hash((BufferedImage) Reflect.call(p, "render", 42L, 125.0)), 1, "caption affects artwork");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 12.16: Unexpected failure while checking the active rule.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P19_SecretPNGPostcardTest {
    static int checks;
    static final String[] CASES = {
        "Skutečná obálka dodá nové pohlednici pixely i rozměry.",
        "Zpráva přetrvá a původní soubor zůstane beze změny.",
        "Nečitelné vstupy a obálka jako vlastní cíl se odmítnou."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.19: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("cover-check-");
        try {
            var p = Reflect.create("P19_SecretPNGPostcard");
            Path cover = root.resolve("cover.png");
            Path card = root.resolve("nested/card.png");
            var original = new BufferedImage(48, 20, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < 20; y++) for (int x = 0; x < 48; x++) original.setRGB(x, y, java.awt.Color.HSBtoRGB(x / 48.0F, .7F, .6F + y / 60.0F));
            ImageIO.write(original, "png", cover.toFile());
            byte[] before = Files.readAllBytes(cover);
            Reflect.call(p, "postcardFromCover", cover, card, "Meet π at noon");
            var result = ImageIO.read(card.toFile());
            check(result != null && result.getWidth() == 48 && result.getHeight() == 20, 0, result == null ? "no image" : result.getWidth() + "x" + result.getHeight());
            boolean sameArtwork = true;
            for (int y = 0; y < 20; y++) for (int x = 0; x < 48; x++) sameArtwork &= (original.getRGB(x,y) & 0xFEFEFE) == (result.getRGB(x,y) & 0xFEFEFE);
            check(sameArtwork, 0, "only the message bits may change");
            check(Reflect.call(Reflect.create("P19_SecretPNGPostcard"), "readMessage", card).equals("Meet π at noon"), 1, "persisted message");
            check(Arrays.equals(before, Files.readAllBytes(cover)), 1, "cover bytes");
            reject(() -> Reflect.call(p, "postcardFromCover", cover, cover, "overwrite"), 2);
            check(Arrays.equals(before, Files.readAllBytes(cover)), 1, "cover after rejected write");
            ioFailure(() -> Reflect.call(p, "postcardFromCover", root.resolve("missing.png"), card, "x"), 2);
            Path invalid = root.resolve("not-image.txt");
            Files.writeString(invalid, "not a PNG");
            ioFailure(() -> Reflect.call(p, "postcardFromCover", invalid, card, "x"), 2);
            check(Reflect.call(p, "readMessage", card).equals("Meet π at noon"), 1, "bad input preserves the prior card");
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P20_MagicEyeStripTest {
    static int checks;
    static final String[] CASES = {
        "Deník vybere poslední číselný řádek a přeskočí prázdné řádky i # komentáře.",
        "Zvolený seed zopakuje skutečný obrázek bez přepsání deníku.",
        "Překlepy, žádná data a chybějící soubor zůstanou jasnými chybami."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 12.20: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("notebook-check-");
        try {
            var p = Reflect.create("P20_MagicEyeStrip");
            Path file = root.resolve("seed.txt");
            Files.writeString(file, "  # first trial\n42\n\n # selected trial\n -7 \n");
            byte[] before = Files.readAllBytes(file);
            long selected;
            try {
                selected = (long) Reflect.call(p, "readSeed", file);
            } catch (NumberFormatException failure) {
                check(false, 0, "valid notebook rejected: " + failure.getMessage());
                return;
            }
            check(selected == -7, 0, selected);
            check(Arrays.equals(before, Files.readAllBytes(file)), 1, "notebook bytes");
            check(hash((BufferedImage) Reflect.call(p, "render", selected, 16.0)) == hash((BufferedImage) Reflect.call(p, "render", -7L, 16.0)), 1, "same dot field");
            Files.writeString(file, " 0 ");
            check((long) Reflect.call(p, "readSeed", file) == 0, 0, "zero is real data");
            Files.writeString(file, "# notes only\n  ");
            reject(() -> Reflect.call(p, "readSeed", file), 2);
            Files.writeString(file, "banana\n42");
            reject(() -> Reflect.call(p, "readSeed", file), 2);
            Files.writeString(file, "42 # not a whole-line comment");
            reject(() -> Reflect.call(p, "readSeed", file), 2);
            ioFailure(() -> Reflect.call(p, "readSeed", root.resolve("missing.txt")), 2);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
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
