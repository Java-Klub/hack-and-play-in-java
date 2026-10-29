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
/// Check one game: `java Check.java M01_CannonPuzzle.java` (use its actual filename).
/// Run from any folder by giving the path to this file, for example `java L02/Check.java`.
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
            run("L02.01 CannonPuzzle · F04", () -> M01_CannonPuzzleTest.main(new String[0]));
            run("L02.02 MiniGolf · F04", () -> M02_MiniGolfTest.main(new String[0]));
            run("L02.03 BouncingBalls · F04", () -> M03_BouncingBallsTest.main(new String[0]));
            run("L02.04 Spirograph · F04", () -> M04_SpirographTest.main(new String[0]));
            run("L02.05 AnagramRounds · F05", () -> M05_AnagramRoundsTest.main(new String[0]));
            run("L02.06 MadLibsStoryMachine · F05", () -> M06_MadLibsStoryMachineTest.main(new String[0]));
            run("L02.07 Codebreaker · F06", () -> M07_CodebreakerTest.main(new String[0]));
            run("L02.08 PokerDice · F06", () -> M08_PokerDiceTest.main(new String[0]));
            run("L02.09 SonarTreasureHunt · F06", () -> M09_SonarTreasureHuntTest.main(new String[0]));
            run("L02.10 MagicNumberCards · F06", () -> M10_MagicNumberCardsTest.main(new String[0]));
            IO.println("10 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

    // Focused behaviour checks.
    class M01_CannonPuzzleTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I065 F04: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M01_CannonPuzzle();
            check(Math.abs(game.launchSpeed(100) - 9) < 1e-8, "100 pixels of drag must give speed 9");
            check(Math.abs(game.launchSpeed(-80) + 7.2) < 1e-8, "upward drag must keep its negative fractional speed");
            game.restart();
            game.launch();
            check(game.vx > 0 && game.vy < 0, "the actual cannon must launch right and upward");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M02_MiniGolfTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I066 F04: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M02_MiniGolf();
            check(Math.abs(game.dragVelocity(100) - 8) < 1e-8, "100 pixels must produce speed 8 before the supplied cap");
            check(Math.abs(game.dragVelocity(-25) + 2) < 1e-8, "negative distance must produce -2");
            check(game.dragVelocity(0) == 0, "no pull means no velocity");
            game.restart();
            game.begin(90, 110);
            game.shoot(-10, 110);
            check(Math.abs(game.vx - 8) < 1e-8 && game.strokes == 1, "the actual putt uses the repaired scale");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M03_BouncingBallsTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I069 F04: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M03_BouncingBalls();
            check(Math.abs(game.bounceSpeed(4) - 3) < 1e-8, "a wall bounce retains three quarters of speed");
            check(Math.abs(game.bounceSpeed(-8) - 6) < 1e-8, "the supplied magnitude conversion must retain a fraction");
            check(game.bounceSpeed(0) == 0, "zero speed remains zero");
            game.restart();
            game.add(11, 100, -4, 0);
            game.step();
            check(game.balls.getFirst().vx() > 2.9, "the real ball reverses away from the left wall");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M04_SpirographTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I075 F04: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M04_Spirograph();
            check(Math.abs(game.radiusRatio(120, 45) - 5.0 / 3) < 1e-8, "75 divided by 45 must retain its fractional ratio");
            check(game.radiusRatio(120, 60) == 1, "an exact ratio remains exact");
            check(game.radiusRatio(120, 80) == 0.5, "a ratio below one must not become zero");
            game.restart();
            var p = game.point(1);
            check(Math.abs(p.x - (75 * Math.cos(1) + 65 * Math.cos(5.0 / 3))) < 1e-8, "the visible curve uses the repaired ratio");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M05_AnagramRoundsTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I029 F05: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M05_AnagramRounds();
            check(game.normalize("  silent  ").equals("SILENT"), "a whole lower-case answer must become the accepted upper-case word");
            check(game.normalize("LiStEn").equals("LISTEN"), "mixed case is normalized");
            game.reset();
            game.command(" silent ");
            check(game.score == 1 && game.round == 1, "the real anagram round accepts the normalized answer");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M06_MadLibsStoryMachineTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I129 F05: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M06_MadLibsStoryMachine();
            check(game.cleanWord("  Red Fox  ").equals("Red Fox"), "edge spaces must disappear while a multiword name stays whole");
            check(game.cleanWord("New   Moon").equals("New   Moon"), "internal spacing is not silently rewritten");
            String story = game.story("  Red Fox  ", " robot ", " Moon Base ", " spoon ");
            check(story.startsWith("Red Fox hired a robot to guard Moon Base."), "the actual story uses cleaned full inputs");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M07_CodebreakerTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I020 F06: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M07_Codebreaker();
            game.secret = new String("1234");
            check(game.matchesCode(new String("1234")), "equal code text must match even when stored as different String objects");
            check(!game.matchesCode("1235"), "a different final digit must not match");
            game.attempts = 0;
            game.won = false;
            check(game.submit(new String("1234")) && game.won, "the actual matching guess must end the round");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M08_PokerDiceTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I039 F06: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M08_PokerDice();
            check(!game.fullHouse(3, 0), "three equal dice alone are not a full house");
            check(!game.fullHouse(2, 1), "a pair alone is not a full house");
            check(game.fullHouse(3, 1), "three equal dice plus a pair form a full house");
            game.dice = new int[]{2, 2, 2, 3, 4};
            check(game.points() == 20, "actual three-kind scores 20 rather than full-house 35");
            game.dice = new int[]{2, 2, 2, 3, 3};
            check(game.points() == 35, "actual full house scores 35");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M09_SonarTreasureHuntTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I042 F06: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M09_SonarTreasureHunt();
            check(!game.insideSea(8, 7), "row index 8 is outside the eight-row sea");
            check(!game.insideSea(7, 8), "column index 8 is outside the sea");
            check(game.insideSea(0, 0) && game.insideSea(7, 7), "both valid corners are included");
            check(!game.insideSea(-1, 0) && !game.insideSea(0, -1), "negative indices are excluded");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Focused behaviour checks.
    class M10_MagicNumberCardsTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I135 F06: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M10_MagicNumberCards();
            check(game.yesAnswer(new String("y")), "a typed lower-case y must mean yes");
            check(game.yesAnswer("Y"), "upper-case Y must also mean yes");
            check(!game.yesAnswer("n"), "n must mean no");
            boolean[] answers = {game.yesAnswer("Y"), false, true, false, false, false};
            check(game.reveal(answers) == 5, "the actual trick must include the first yes card");
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
