import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
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
        run("P03_Task_WordLadder", () -> P03_Task_WordLadderTest.main(new String[0]));
        run("P04_Task_TileStampStudio", () -> P04_Task_TileStampStudioTest.main(new String[0]));
        run("P07_Task_FractalTreeGarden", () -> P07_Task_FractalTreeGardenTest.main(new String[0]));
        run("P08_Task_ImageToASCII", () -> P08_Task_ImageToASCIITest.main(new String[0]));
        run("P11_Task_MazePostcards", () -> P11_Task_MazePostcardsTest.main(new String[0]));
        run("P14_Task_EndlessToneStaircase", () -> P14_Task_EndlessToneStaircaseTest.main(new String[0]));
        run("P15_Task_PixelSortingGlitch", () -> P15_Task_PixelSortingGlitchTest.main(new String[0]));
        run("P16_Task_MirrorCylinderPicture", () -> P16_Task_MirrorCylinderPictureTest.main(new String[0]));
        run("P19_Task_SecretPNGPostcard", () -> P19_Task_SecretPNGPostcardTest.main(new String[0]));
        run("P20_Task_MagicEyeStrip", () -> P20_Task_MagicEyeStripTest.main(new String[0]));
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
class P03_Task_WordLadderTest {
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
        var g = Reflect.create("P03_Task_WordLadder");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", " cord  card ");
        equal(List.of("COLD", "CORD", "CARD"), Reflect.get(g, "history"), "After command(\" cord  card \"), history should contain COLD, CORD, CARD in that order. Keep every accepted stop, including the starting word.");
        Object before = List.copyOf((List<?>) Reflect.get(g, "history"));
        boolean rejected = false;
        try { Reflect.call(g, "command", "WARD COLD"); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "From CARD, command(\"WARD COLD\") must reject the entire route: its second step is invalid. Validate all stops before accepting the plan.");
        equal(before, Reflect.get(g, "history"), "A rejected route changed history. In command, leave the complete previous journey unchanged when any planned step fails.");
        equal("CARD", Reflect.get(g, "current"), "A rejected route changed current. After refusing WARD COLD from CARD, the current word must still be CARD.");
        Reflect.call(g, "command", "WARD WARM");
        equal("Reached WARM in 4 moves!", Reflect.get(g, "outcome"), "The route to WARM has four individual moves, even when submitted in two batches. Build the winning message from moves, not submission count.");
        Reflect.call(g, "reset");
        rejected = false;
        try { Reflect.call(g, "command", "CORD CARD WARD WARM WARD"); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected && ((List<?>) Reflect.get(g, "history")).equals(List.of("COLD")), "command must reject a batch that continues after WARM. The rejected batch must leave history at its original COLD entry.");
        Reflect.call(g, "command", "CORD");
        equal("CORD", Reflect.get(g, "current"), "A single CORD move stopped working. command must still accept one legal word and make it current.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_TileStampStudioTest {
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
        var g = Reflect.create("P04_Task_TileStampStudio");
        expect((boolean) Reflect.call(g, "applyRepeats", "3"), "applyRepeats(\"3\") must still accept a normal numeric repeat count.");
        expect((boolean) Reflect.call(g, "applyRepeats", " DENSER "), "applyRepeats must recognise \" DENSER \" despite outer spaces and uppercase letters.");
        equal(6, Reflect.get(g, "repeats"), "After setting 3, denser should change repeats to 6. Base the relative command on the last accepted setting.");
        Reflect.call(g, "applyRepeats", "denser");
        equal(12, Reflect.get(g, "repeats"), "A second denser command should change 6 repeats to 12, rather than starting from the original setting again.");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "denser") && (int) Reflect.get(g, "repeats") == 12, "Denser at 12 must be refused and leave repeats at 12. Do not commit a result outside 1..12.");
        Reflect.call(g, "applyRepeats", "sparser"); equal(6, Reflect.get(g, "repeats"), "Sparser at 12 should be accepted and change repeats to 6.");
        Reflect.call(g, "applyRepeats", "3");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "sparser") && (int) Reflect.get(g, "repeats") == 3, "Sparser at 3 must be refused without rounding or changing repeats.");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "oops") && (int) Reflect.get(g, "repeats") == 3, "applyRepeats(\"oops\") must report refusal and preserve the previous repeat count.");
        Reflect.call(g, "paint", 0, 0, 1);
        var three = (java.awt.image.BufferedImage) Reflect.call(g, "render", 3);
        var six = (java.awt.image.BufferedImage) Reflect.call(g, "render", 6);
        expect(three.getWidth() == 288 && six.getWidth() == 288 && three.getRGB(6, 0) != six.getRGB(6, 0), "The accepted repeat count must change the rendered tile density. render(3) and render(6) should produce different patterns at the same 288-pixel width.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_FractalTreeGardenTest {
    static int checks;
    static final String[] CASES = {
        "readAngle(\"oops\") should return the fallback angle 28, because the text cannot be parsed as a number.",
        "readAngle should accept 10 and 70, including outer spaces. Both endpoints belong to the permitted angle range.",
        "readAngle must reject 9, 71 and NaN with IllegalArgumentException. A readable number must still be finite and within 10..70."
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
            var p = Reflect.create("P07_Task_FractalTreeGarden");
            check(((double) Reflect.call(p, "readAngle", "oops"))==28,0,((double) Reflect.call(p, "readAngle", "oops")));
            check(((double) Reflect.call(p, "readAngle", " 10 "))==10&&((double) Reflect.call(p, "readAngle", "70"))==70,1,"bounds");
            reject(()->Reflect.call(p, "readAngle", "9"),2);
            reject(()->Reflect.call(p, "readAngle", "71"),2);
            reject(()->Reflect.call(p, "readAngle", "NaN"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P08_Task_ImageToASCIITest {
    static int checks;
    static final String[] CASES = {
        "readWidth(\"oops\") should return the fallback width 80, not the smallest permitted width.",
        "readWidth should accept 4 and 160, including outer spaces. Keep both width limits valid.",
        "readWidth must reject 3 and 161 with IllegalArgumentException. Use the fallback only for text that cannot be parsed."
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
            var p = Reflect.create("P08_Task_ImageToASCII");
            check(((int) Reflect.call(p, "readWidth", "oops"))==80,0,((int) Reflect.call(p, "readWidth", "oops")));
            check(((int) Reflect.call(p, "readWidth", " 4 "))==4&&((int) Reflect.call(p, "readWidth", "160"))==160,1,"bounds");
            reject(()->Reflect.call(p, "readWidth", "3"),2);
            reject(()->Reflect.call(p, "readWidth", "161"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P11_Task_MazePostcardsTest {
    static int checks;
    static final String[] CASES = {
        "readColumns must use 20 for malformed text and recognise tiny=6 and wide=40. The fallback must also feed the real maze generator.",
        "readColumns should accept 2 and 50, including outer spaces. Both ends of the allowed range are valid.",
        "readColumns must reject 1 and 51 with IllegalArgumentException instead of replacing a readable out-of-range number."
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
            var p = Reflect.create("P11_Task_MazePostcards");
            check(((int) Reflect.call(p, "readColumns", "oops"))==20,0,((int) Reflect.call(p, "readColumns", "oops")));
            check(((int) Reflect.call(p, "readColumns", " 2 "))==2&&((int) Reflect.call(p, "readColumns", "50"))==50,1,"limits");
            reject(()->Reflect.call(p, "readColumns", "1"),2);reject(()->Reflect.call(p, "readColumns", "51"),2);
            var maze=((boolean[][]) Reflect.call(p, "generate", 42, ((int) Reflect.call(p, "readColumns", "oops")), 2));check(maze[0].length==41,0,maze[0].length);
        check(((int) Reflect.call(p, "readColumns", " tiny ")) == 6 && ((int) Reflect.call(p, "readColumns", "WIDE")) == 40, 0, "named maze sizes");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P14_Task_EndlessToneStaircaseTest {
    static int checks;
    static final String[] CASES = {
        "write must create the WAV at the complete supplied destination, including its nested folder and filename. Check the Path passed to the output stream.",
        "The saved WAV should contain four frames at 22050 Hz for the four supplied samples. Check that write encodes the supplied audio stream.",
        "write must throw IOException when a required parent folder is actually a file. Do not silently choose another destination."
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
        Path root = Files.createTempDirectory("tone-check-");
        String filename = root.getFileName() + ".wav";
        Path misplaced = Path.of(filename).toAbsolutePath();
        boolean absent = Files.notExists(misplaced);
        try {
            var p = Reflect.create("P14_Task_EndlessToneStaircase");
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
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        } finally {
            if (absent) Files.deleteIfExists(misplaced);
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }
}

@SuppressWarnings("unchecked")
class P15_Task_PixelSortingGlitchTest {
    static int checks;
    static final String[] CASES = {
        "saveSorted did not create the requested nested/glitch.png file. Preserve the destination folder and filename supplied by the caller.",
        "saveSorted must save the sorted pixels while leaving the input image unchanged. For the test row, the first saved pixel should be grey 0x555555, but the input should still start white.",
        "saveSorted must throw IOException when its parent path is a file rather than a folder. Do not redirect the output elsewhere."
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
        Path root = Files.createTempDirectory("pixels-check-");
        try {
            var p = Reflect.create("P15_Task_PixelSortingGlitch");
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
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }
}

@SuppressWarnings("unchecked")
class P16_Task_MirrorCylinderPictureTest {
    static int checks;
    static final String[] CASES = {
        "readCaption should return MOON from a first line containing outer spaces. Read the selected file, strip that line, and ignore later lines.",
        "readCaption must preserve UTF-8 text such as π!, and render must use the loaded caption. JAVA and MOON should change the actual picture.",
        "readCaption must throw IOException for a missing file, no first line, a blank first line, or more than eight characters. Do not skip an invalid first line."
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
        Path root = Files.createTempDirectory("mirror-check-");
        try {
            var p = Reflect.create("P16_Task_MirrorCylinderPicture");
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
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }
}

@SuppressWarnings("unchecked")
class P19_Task_SecretPNGPostcardTest {
    static int checks;
    static final String[] CASES = {
        "postcardFromCover must keep the supplied cover artwork and its 48×20 dimensions. Only the low colour bits used for the message may change.",
        "The saved postcard must reopen with the full message \"Meet π at noon\", while the source cover stays unchanged. A refused input must also preserve any previous postcard.",
        "postcardFromCover must reject using the cover as its destination, and report IOException for missing or unreadable image data. Validate before replacing the saved postcard."
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
        Path root = Files.createTempDirectory("cover-check-");
        try {
            var p = Reflect.create("P19_Task_SecretPNGPostcard");
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
            check(Reflect.call(Reflect.create("P19_Task_SecretPNGPostcard"), "readMessage", card).equals("Meet π at noon"), 1, "persisted message");
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
    }
}

@SuppressWarnings("unchecked")
class P20_Task_MagicEyeStripTest {
    static int checks;
    static final String[] CASES = {
        "readSeed should select the last numeric data line: the notebook with 42 followed by -7 selects -7. Skip blank lines and whole-line # comments, and accept zero as data.",
        "Reading the notebook must not rewrite it. Use the selected seed so that rendering the selected -7 matches rendering -7 directly.",
        "readSeed must reject a notebook with no numbers or an invalid data line, even before a later valid number. A missing file must produce IOException."
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
        Path root = Files.createTempDirectory("notebook-check-");
        try {
            var p = Reflect.create("P20_Task_MagicEyeStrip");
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
