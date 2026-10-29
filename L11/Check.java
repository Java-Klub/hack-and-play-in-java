import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/// Check every game in this level: `java Check.java`
/// Check one game: `java Check.java M01_TinyStepSequencer.java` (use its actual filename).
/// Run from any folder by giving the path to this file, for example `java L11/Check.java`.
/// Games and checks need only JDK 26 or newer. No project, download or build tool is needed.
// <editor-fold defaultstate="collapsed" desc="Check runner · JDK only">
void main(String[] args) {
    int result;
    try {
        result = verify(args);
    } catch (Throwable failure) {
        System.err.println("RED " + failure.getMessage());
        result = 1;
    }
    if (result != 0) System.exit(result);
}

int verify(String[] args) throws Exception {
    if (args.length > 1) {
        throw new IllegalArgumentException("Use: java Check.java [game filename]");
    }
    String source = System.getProperty("jdk.launcher.sourcefile");
    if (source == null) {
        throw new IllegalStateException("Use Java source launcher: java path/to/Check.java");
    }
    Path level = Path.of(source).toAbsolutePath().normalize().getParent();
    String game = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString();
    if (game.endsWith(".java")) game = game.substring(0, game.length() - 5);
    if (!game.isEmpty() && (!game.matches("M\\d+_.*")
            || !Files.isRegularFile(level.resolve(game + ".java")))) {
        throw new IllegalArgumentException("Game not found in this level: " + game);
    }
    Path classes = Files.createTempDirectory("hack-and-play-check-");
    try {
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("Use a full JDK 26 or newer.");
        try (var manager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
                var files = Files.list(level)) {
            var games = files.filter(path -> path.getFileName().toString().matches("M\\d+_.*\\.java"))
                    .sorted().toList();
            var options = List.of("--release", "26", "-encoding", "UTF-8", "-d", classes.toString(),
                    "-classpath", classes.toString(), "-sourcepath", "");
            // First compile compact games. Then their checks can use the finished class files.
            if (!compiler.getTask(null, manager, null, options, null,
                    manager.getJavaFileObjectsFromPaths(games)).call()) {
                throw new IllegalStateException("Fix the compilation errors above, then check again.");
            }
            var checks = new SimpleJavaFileObject(URI.create("string:///LevelCheck.java"),
                    JavaFileObject.Kind.SOURCE) {
                public CharSequence getCharContent(boolean ignoreErrors) { return testSource; }
            };
            if (!compiler.getTask(null, manager, null, options, null, List.of(checks)).call()) {
                throw new IllegalStateException("The level checks could not be compiled.");
            }
        }
        // Run checks in a temporary working folder, so a broken file mission leaves no game files.
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        return new ProcessBuilder(java, "-ea", "-Djava.awt.headless=true", "-Djava.io.tmpdir=" + classes,
                "-cp", classes.toString(),
                "CheckResult", game.isEmpty() ? "LevelCheck" : game + "Test")
                .directory(classes.toFile()).inheritIO().start().waitFor();
    } finally {
        try (var paths = Files.walk(classes)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }
}
// </editor-fold>

// Test source is kept as text so the source launcher can start this file before compiling games.
// These are functional checks, not a solution or a source-format checker.
// <editor-fold defaultstate="collapsed" desc="All functional checks for this level">
String testSource = """
    import java.util.*;
    import java.nio.file.*;
    import java.io.*;
    import java.awt.Point;
    import java.awt.image.BufferedImage;
    import javax.imageio.ImageIO;

    // Build support: aggregates independent functional checks; no game rules live here.
    class LevelCheck {
        interface Check { void run() throws Exception; }
        static int failed;
        static void run(String label, Check check) {
            try {
                check.run();
                IO.println("GREEN " + label);
            } catch (Throwable problem) {
                failed++;
                IO.println("RED " + label + ": " + problem.getMessage());
            }
        }
        public static void main(String[] args) {
            run("L11.01 TinyStepSequencer · F23", () -> M01_TinyStepSequencerTest.main(new String[0]));
            run("L11.02 CirclePacking · F23", () -> M02_CirclePackingTest.main(new String[0]));
            run("L11.03 WireframeCube · F23", () -> M03_WireframeCubeTest.main(new String[0]));
            run("L11.04 MandelbrotPostcards · F27", () -> M04_MandelbrotPostcardsTest.main(new String[0]));
            run("L11.05 ChaosGame · F27", () -> M05_ChaosGameTest.main(new String[0]));
            run("L11.06 ColourFilterSecrets · F27", () -> M06_ColourFilterSecretsTest.main(new String[0]));
            run("L11.07 TwoNoisyPictures · F27", () -> M07_TwoNoisyPicturesTest.main(new String[0]));
            run("L11.08 EndlessToneStaircase · F32", () -> M08_EndlessToneStaircaseTest.main(new String[0]));
            run("L11.09 PixelSortingGlitch · F32", () -> M09_PixelSortingGlitchTest.main(new String[0]));
            run("L11.10 SecretPNGPostcard · F32", () -> M10_SecretPNGPostcardTest.main(new String[0]));
            run("L11.11 MirrorCylinderPicture · F32", () -> M11_MirrorCylinderPictureTest.main(new String[0]));
            run("L11.12 MagicEyeStrip · F32", () -> M12_MagicEyeStripTest.main(new String[0]));
            IO.println("12 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

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
                var p = new M01_TinyStepSequencer();
                p.reset();var original=Set.copyOf(p.notes);p.toggle(0,1);p.toggle(0,1);
                check(p.notes.equals(original),0,p.notes);
                p.notes.clear();check(p.toggle(2,4)&&p.notes.contains(new M01_TinyStepSequencer.Note(2,4)),1,p.notes);
                check(p.toggle(2,4)&&p.notes.isEmpty(),0,p.notes);
                check(!p.toggle(-1,0)&&!p.toggle(4,0)&&!p.toggle(0,8)&&p.notes.isEmpty(),2,p.notes);
                check(Arrays.stream(p.sequence(1)).allMatch(v->v==0),2,"silence");
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I102: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
                var p = new M02_CirclePacking();
                Set<M02_CirclePacking.Centre> seen=new HashSet<>();
                check(p.claimCentre(seen,4,7)&&seen.size()==1,0,seen);
                check(!p.claimCentre(seen,4,7)&&seen.size()==1,1,seen);
                check(p.claimCentre(seen,7,4)&&seen.size()==2,1,seen);
                var bubbles=p.pack(42,8);check(bubbles.size()==8,2,bubbles.size());
                check(bubbles.stream().map(b->new M02_CirclePacking.Centre((int)b.x(),(int)b.y())).distinct().count()==8,2,bubbles);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I111: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
                var p = new M03_WireframeCube();
                var edges=p.uniqueEdges(new int[][]{{0,1},{1,0},{0,1},{1,2}});
                check(edges.size()==2,0,edges);
                check(edges.contains(new M03_WireframeCube.Edge(0,1))&&edges.contains(new M03_WireframeCube.Edge(1,2)),1,edges);
                check(p.uniqueEdges(new int[][]{{-1,0},{0,8},{2,2}}).isEmpty(),2,"invalid");
                check(p.uniqueEdges(new int[0][2]).isEmpty(),2,"empty");
                check(p.uniqueEdges(p.edges).size()==12,0,p.uniqueEdges(p.edges).size());
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I122: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
                var p = new M04_MandelbrotPostcards();
                var a=p.render(42,3.2);var b=p.render(43,3.2);check(hash(a)!=hash(b),0,hash(a));
                check(hash(a)==hash(p.render(42,3.2)),1,hash(a));check(p.escape(0,0)==160&&p.escape(3,3)<160,2,p.escape(3,3));
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I104: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
                var p = new M05_ChaosGame();
                var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(p.chooseVertex(random,3));
                check(chosen.equals(Set.of(0,1,2)),0,chosen);
                var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(p.chooseVertex(a,3)==p.chooseVertex(b,3),1,n);
                check(p.chooseVertex(new Random(42),1)==0,2,"single");reject(()->p.chooseVertex(new Random(42),0),2);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I105: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
                var p = new M06_ColourFilterSecrets();
                var mask=p.mask("HI");var a=p.hide(mask,42);var b=p.hide(mask,43);check(hash(a)!=hash(b),0,hash(a));
                check(hash(a)==hash(p.hide(mask,42)),1,hash(a));check(hash(p.reveal(a))==hash(p.reveal(b)),2,hash(p.reveal(a)));
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I116: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
                var p = new M07_TwoNoisyPictures();
                var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=p.shares(mask,42);var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
                check(hash(a[0])==hash(p.shares(mask,42)[0]),1,hash(a[0]));check(hash(a[0])!=hash(p.shares(mask,43)[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I118: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
            try {
                var p = new M08_EndlessToneStaircase();
                Path root=Files.createTempDirectory("tone-check-");Path file=root.resolve("nested/tone.wav");
                p.write(file,new double[]{0,.2,-.2,0});check(Files.isRegularFile(file),0,file);
                try(var audio=javax.sound.sampled.AudioSystem.getAudioInputStream(file.toFile())){check(audio.getFrameLength()==4&&audio.getFormat().getSampleRate()==22050,1,audio.getFrameLength());}
                Path blocked=root.resolve("blocked");Files.writeString(blocked,"x");ioFailure(()->p.write(blocked.resolve("tone.wav"),new double[1]),2);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I103: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
            try {
                var p = new M09_PixelSortingGlitch();
                var input=new BufferedImage(3,1,BufferedImage.TYPE_INT_ARGB);input.setRGB(0,0,0xFFFFFFFF);input.setRGB(1,0,0xFF555555);input.setRGB(2,0,0xFFAAAAAA);
                Path root=Files.createTempDirectory("pixels-check-");Path file=root.resolve("nested/glitch.png");p.saveSorted(file,input,0);
                check(Files.isRegularFile(file),0,file);var result=ImageIO.read(file.toFile());
                check(result.getWidth()==3&&(result.getRGB(0,0)&0xFFFFFF)==0x555555,1,Integer.toHexString(result.getRGB(0,0)));
                check(input.getRGB(0,0)==0xFFFFFFFF,1,Integer.toHexString(input.getRGB(0,0)));
                Path blocked=root.resolve("blocked");Files.writeString(blocked,"x");ioFailure(()->p.saveSorted(blocked.resolve("x.png"),input,0),2);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I115: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
            try {
                var p = new M10_SecretPNGPostcard();
                Path root=Files.createTempDirectory("secret-check-");Path file=root.resolve("nested/secret.png");p.message="Pixel π";p.save(file,42,1);
                check(p.readMessage(file).equals("Pixel π"),0,p.readMessage(file));
                p.message="";p.save(file,42,1);check(p.readMessage(file).isEmpty(),1,p.readMessage(file));
                Path text=root.resolve("plain.txt");Files.writeString(text,"not a PNG");ioFailure(()->p.readMessage(text),2);
                ioFailure(()->p.readMessage(root.resolve("missing.png")),2);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I117: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
            try {
                var p = new M11_MirrorCylinderPicture();
                Path root=Files.createTempDirectory("mirror-check-");Path file=root.resolve("nested/mirror.png");p.save(file,42,125);
                check(Files.exists(file)&&Files.size(file)>0,0,Files.size(file));
                var image=ImageIO.read(file.toFile());check(image!=null&&image.getWidth()==900&&image.getHeight()==800,1,image==null?null:image.getWidth());
                Path blocked=root.resolve("blocked");Files.writeString(blocked,"x");ioFailure(()->p.save(blocked.resolve("x.png"),42,125),2);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I119: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

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
            try {
                var p = new M12_MagicEyeStrip();
                Path root=Files.createTempDirectory("eye-check-");Path file=root.resolve("seed.txt");Files.writeString(file," 42\\n");
                check(p.readSeed(file)==42,0,p.readSeed(file));
                check(hash(p.render(p.readSeed(file),16))==hash(p.render(42,16)),1,"same picture");
                Files.writeString(file,"-7");check(p.readSeed(file)==-7,1,p.readSeed(file));
                Files.writeString(file,"banana");try{p.readSeed(file);check(false,2,"accepted");}catch(NumberFormatException expected){check(true,2,"bad number");}
                ioFailure(()->p.readSeed(root.resolve("missing.txt")),2);
            } catch (AssertionError failure) {
                throw failure;
            } catch (Exception failure) {
                throw new AssertionError("MISSION I120: Unexpected failure while checking the active rule.", failure);
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Report a failed check without a reflection stack trace; the launcher returns its exit status.
    class CheckResult {
        public static void main(String[] args) {
            try {
                var main = Class.forName(args[0]).getDeclaredMethod("main", String[].class);
                main.setAccessible(true);
                main.invoke(null, (Object) new String[0]);
            } catch (Throwable failure) {
                if (failure instanceof java.lang.reflect.InvocationTargetException wrapped) {
                    failure = wrapped.getCause();
                }
                System.err.println("RED " + failure.getMessage());
                System.exit(1);
            }
        }
    }
    """;
// </editor-fold>
