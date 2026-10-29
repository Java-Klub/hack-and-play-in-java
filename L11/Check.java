import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_TinyStepSequencer.java`
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
        run("M01_TinyStepSequencer", "L11.01 TinyStepSequencer · F23", () -> M01_TinyStepSequencerTest.main(new String[0]));
        run("M02_CirclePacking", "L11.02 CirclePacking · F23", () -> M02_CirclePackingTest.main(new String[0]));
        run("M03_WireframeCube", "L11.03 WireframeCube · F23", () -> M03_WireframeCubeTest.main(new String[0]));
        run("M04_MandelbrotPostcards", "L11.04 MandelbrotPostcards · F27", () -> M04_MandelbrotPostcardsTest.main(new String[0]));
        run("M05_ChaosGame", "L11.05 ChaosGame · F27", () -> M05_ChaosGameTest.main(new String[0]));
        run("M06_ColourFilterSecrets", "L11.06 ColourFilterSecrets · F27", () -> M06_ColourFilterSecretsTest.main(new String[0]));
        run("M07_TwoNoisyPictures", "L11.07 TwoNoisyPictures · F27", () -> M07_TwoNoisyPicturesTest.main(new String[0]));
        run("M08_EndlessToneStaircase", "L11.08 EndlessToneStaircase · F32", () -> M08_EndlessToneStaircaseTest.main(new String[0]));
        run("M09_PixelSortingGlitch", "L11.09 PixelSortingGlitch · F32", () -> M09_PixelSortingGlitchTest.main(new String[0]));
        run("M10_SecretPNGPostcard", "L11.10 SecretPNGPostcard · F32", () -> M10_SecretPNGPostcardTest.main(new String[0]));
        run("M11_MirrorCylinderPicture", "L11.11 MirrorCylinderPicture · F32", () -> M11_MirrorCylinderPictureTest.main(new String[0]));
        run("M12_MagicEyeStrip", "L11.12 MagicEyeStrip · F32", () -> M12_MagicEyeStripTest.main(new String[0]));
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
class M01_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Two toggles restore the same active-note Set.",
        "A new note appears exactly once.",
        "Invalid cells preserve membership; an empty Set exports silence."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I102: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M01_TinyStepSequencer");
            Reflect.call(p, "reset");var original=Set.copyOf(((java.util.Set<Object>) Reflect.get(p, "notes")));Reflect.call(p, "toggle", 0, 1);Reflect.call(p, "toggle", 0, 1);
            check(((java.util.Set<Object>) Reflect.get(p, "notes")).equals(original),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            ((java.util.Set<Object>) Reflect.get(p, "notes")).clear();check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).contains(Reflect.create("M01_TinyStepSequencer$Note", 2, 4)),1,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(!((boolean) Reflect.call(p, "toggle", -1, 0))&&!((boolean) Reflect.call(p, "toggle", 4, 0))&&!((boolean) Reflect.call(p, "toggle", 0, 8))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),2,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(Arrays.stream(((double[]) Reflect.call(p, "sequence", 1))).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I102: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "The first centre is stored and accepted.",
        "A repeated centre is rejected; swapped coordinates are a different value.",
        "The real garden can still place eight distinct bubbles."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I111: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M02_CirclePacking");
            Set<Object> seen=new HashSet<>();
            check(((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,0,seen);
            check(!((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,1,seen);
            check(((boolean) Reflect.call(p, "claimCentre", seen, 7, 4))&&seen.size()==2,1,seen);
            var bubbles=((java.util.List<Object>) Reflect.call(p, "pack", 42, 8));check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->Reflect.create("M02_CirclePacking$Centre", (int)((double) Reflect.call(b, "x")), (int)((double) Reflect.call(b, "y")))).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I111: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "Repeated and reversed pairs collapse to two distinct edges.",
        "The surviving edge keys have smaller/larger order.",
        "Invalid/self/empty data are safe; the real cube has twelve edges."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I122: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M03_WireframeCube");
            var edges=((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{0,1},{1,0},{0,1},{1,2}}));
            check(edges.size()==2,0,edges);
            check(edges.contains(Reflect.create("M03_WireframeCube$Edge", 0, 1))&&edges.contains(Reflect.create("M03_WireframeCube$Edge", 1, 2)),1,edges);
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{-1,0},{0,8},{2,2}})).isEmpty(),2,"invalid");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[0][2])).isEmpty(),2,"empty");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size()==12,0,((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I122: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_MandelbrotPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Changing seed changes the palette.",
        "The same seed and window reproduce every pixel.",
        "Escape geometry remains independent of palette choice."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I104: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M04_MandelbrotPostcards");
            var a=((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, 3.2));var b=((java.awt.image.BufferedImage) Reflect.call(p, "render", 43, 3.2));check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, 3.2))),1,hash(a));check(((int) Reflect.call(p, "escape", 0, 0))==160&&((int) Reflect.call(p, "escape", 3, 3))<160,2,((int) Reflect.call(p, "escape", 3, 3)));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I104: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "All three legal vertices are reachable.",
        "A fixed seed reproduces the index sequence.",
        "A single candidate is zero and an empty range is rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I105: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M05_ChaosGame");
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(((int) Reflect.call(p, "chooseVertex", random, 3)));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(((int) Reflect.call(p, "chooseVertex", a, 3))==((int) Reflect.call(p, "chooseVertex", b, 3)),1,n);
            check(((int) Reflect.call(p, "chooseVertex", new Random(42), 1))==0,2,"single");reject(()->Reflect.call(p, "chooseVertex", new Random(42), 0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I105: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_ColourFilterSecretsTest {
    static int checks;
    static final String[] CASES = {
        "Changing seed changes camouflage.",
        "Equal seeds reproduce all hidden pixels.",
        "The recovered message is independent of the noise seed."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I116: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M06_ColourFilterSecrets");
            var mask=((java.awt.image.BufferedImage) Reflect.call(p, "mask", "HI"));var a=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42));var b=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 43));check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42))),1,hash(a));check(hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a)))==hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", b))),2,hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I116: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_TwoNoisyPicturesTest {
    static int checks;
    static final String[] CASES = {
        "Neighbouring blank tiles use varied dot patterns.",
        "Equal seeds repeat and different seeds vary the first share.",
        "Each share tile still has exactly two black dots."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I118: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M07_TwoNoisyPictures");
            var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42));var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
            check(hash(a[0])==hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42))[0]),1,hash(a[0]));check(hash(a[0])!=hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 43))[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I118: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_EndlessToneStaircaseTest {
    static int checks;
    static final String[] CASES = {
        "The complete nested destination is created.",
        "The actual WAV contains the expected number/rate of frames.",
        "A parent that is a file causes an expected IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I103: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M08_EndlessToneStaircase");
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
            throw new AssertionError("MISSION I103: Unexpected failure while checking the active rule.", failure);
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
class M09_PixelSortingGlitchTest {
    static int checks;
    static final String[] CASES = {
        "The exact nested filename exists after export.",
        "The decoded PNG is sorted while input pixels remain unchanged.",
        "An unusable parent produces an expected IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I115: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M09_PixelSortingGlitch");
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
            throw new AssertionError("MISSION I115: Unexpected failure while checking the active rule.", failure);
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
class M10_SecretPNGPostcardTest {
    static int checks;
    static final String[] CASES = {
        "A nested PNG round-trips a UTF-8 message.",
        "An empty message survives the same path.",
        "Unsupported and missing input paths cause IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I117: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("secret-check-");
        try {
            var p = Reflect.create("M10_SecretPNGPostcard");
            Path file = root.resolve("nested/secret.png");
            Reflect.set(p, "message", "Pixel π");
            Reflect.call(p, "save", file, 42, 1);
            check(((java.lang.String) Reflect.call(p, "readMessage", file)).equals("Pixel π"), 0, ((java.lang.String) Reflect.call(p, "readMessage", file)));
            Reflect.set(p, "message", "");
            Reflect.call(p, "save", file, 42, 1);
            check(((java.lang.String) Reflect.call(p, "readMessage", file)).isEmpty(), 1, ((java.lang.String) Reflect.call(p, "readMessage", file)));
            Path text = root.resolve("plain.txt");
            Files.writeString(text, "not a PNG");
            ioFailure(() -> Reflect.call(p, "readMessage", text), 2);
            ioFailure(() -> Reflect.call(p, "readMessage", root.resolve("missing.png")), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I117: Unexpected failure while checking the active rule.", failure);
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
class M11_MirrorCylinderPictureTest {
    static int checks;
    static final String[] CASES = {
        "The requested nested file contains nonempty bytes.",
        "The actual saved PNG has the expected image dimensions.",
        "An invalid parent path reports IOException."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I119: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M11_MirrorCylinderPicture");
            Path file = root.resolve("nested/mirror.png");
            Reflect.call(p, "save", file, 42, 125);
            check(Files.exists(file) && Files.size(file) > 0, 0, Files.size(file));
            var image = ImageIO.read(file.toFile());
            check(image != null && image.getWidth() == 900 && image.getHeight() == 800, 1, image == null ? null : image.getWidth());
            Path blocked = root.resolve("blocked");
            Files.writeString(blocked, "x");
            ioFailure(() -> Reflect.call(p, "save", blocked.resolve("x.png"), 42, 125), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I119: Unexpected failure while checking the active rule.", failure);
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
class M12_MagicEyeStripTest {
    static int checks;
    static final String[] CASES = {
        "The seed comes from content rather than the filename.",
        "Whitespace and a negative seed work in the real rendering input.",
        "Bad numeric text and a missing path remain visible expected failures."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I120: " + CASES[example] + " | actual=" + actual);
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
        Path root = Files.createTempDirectory("eye-check-");
        try {
            var p = Reflect.create("M12_MagicEyeStrip");
            Path file = root.resolve("seed.txt");
            Files.writeString(file, " 42\n");
            check(((long) Reflect.call(p, "readSeed", file)) == 42, 0, ((long) Reflect.call(p, "readSeed", file)));
            check(hash(((java.awt.image.BufferedImage) Reflect.call(p, "render", ((long) Reflect.call(p, "readSeed", file)), 16))) == hash(((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, 16))), 1, "same picture");
            Files.writeString(file, "-7");
            check(((long) Reflect.call(p, "readSeed", file)) == -7, 1, ((long) Reflect.call(p, "readSeed", file)));
            Files.writeString(file, "banana");
            try {
                Reflect.call(p, "readSeed", file);
                check(false, 2, "accepted");
            } catch (NumberFormatException expected) {
                check(true, 2, "bad number");
            }
            ioFailure(() -> Reflect.call(p, "readSeed", root.resolve("missing.txt")), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I120: Unexpected failure while checking the active rule.", failure);
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
}
