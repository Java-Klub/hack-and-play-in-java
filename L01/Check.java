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
/// Check one game: `java Check.java M01_CafeWallLab.java` (use its actual filename).
/// Run from any folder by giving the path to this file, for example `java L01/Check.java`.
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
    import java.awt.Color;
    import java.awt.image.BufferedImage;

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
            run("L01.01 CafeWallLab · F01", () -> M01_CafeWallLabTest.main(new String[0]));
            run("L01.02 MissingTriangle · F01", () -> M02_MissingTriangleTest.main(new String[0]));
            run("L01.03 TypeMelody · F02", () -> M03_TypeMelodyTest.main(new String[0]));
            run("L01.04 BitmapBanner · F02", () -> M04_BitmapBannerTest.main(new String[0]));
            run("L01.05 ProgramMirror · F02", () -> M05_ProgramMirrorTest.main(new String[0]));
            run("L01.06 HigherOrLower · F03", () -> M06_HigherOrLowerTest.main(new String[0]));
            run("L01.07 SteppingFeet · F03", () -> M07_SteppingFeetTest.main(new String[0]));
            run("L01.08 SunflowerSpiral · F03", () -> M08_SunflowerSpiralTest.main(new String[0]));
            run("L01.09 PixelMonsters · F03", () -> M09_PixelMonstersTest.main(new String[0]));
            IO.println("9 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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
            check(game.templateLine().equals("IO.println(\\"HELLO\\");"), "the printed statement needs literal quotation marks around HELLO");
            check(!game.templateLine().equals("HELLO"), "the template is a printed Java statement, not its output");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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

    // Focused behaviour checks.
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
