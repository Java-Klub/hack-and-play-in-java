import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/// Kontrola všech her v tomto levelu: `java Check.java`
/// Kontrola jedné hry: `java Check.java M01_Hangman.java` (použij její skutečný název souboru).
/// Z jiné složky přidej cestu k tomuto souboru, například `java L04/Check.java`.
/// Hrám i kontrolám stačí JDK 26 nebo novější. Není potřeba projekt, stahování knihoven ani build tool (sestavovací nástroj).
// <editor-fold defaultstate="collapsed" desc="Spouštění kontrol · pouze JDK">
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
            // Nejprve přeložíme compact programy. Kontroly pak použijí hotové class files (soubory tříd).
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
        // Testy běží v dočasné pracovní složce; ani rozbitá souborová mise tak nezanechá soubory mezi hrami.
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

// Kód testů je uložený jako text, aby source launcher spustil tento soubor ještě před přeložením her.
// Testy ověřují chování programu. Neobsahují řešení ani nekontrolují formát zdrojového kódu.
// <editor-fold defaultstate="collapsed" desc="Všechny funkční kontroly tohoto levelu">
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
            run("L04.01 Hangman · F10", () -> M01_HangmanTest.main(new String[0]));
            run("L04.02 Ghost · F10", () -> M02_GhostTest.main(new String[0]));
            run("L04.03 Flood · F10", () -> M03_FloodTest.main(new String[0]));
            run("L04.04 SameGame · F10", () -> M04_SameGameTest.main(new String[0]));
            run("L04.05 WordSearch · F11", () -> M05_WordSearchTest.main(new String[0]));
            run("L04.06 VirtualScribbler · F11", () -> M06_VirtualScribblerTest.main(new String[0]));
            run("L04.07 KochSnowflakeCards · F11", () -> M07_KochSnowflakeCardsTest.main(new String[0]));
            run("L04.08 DragonCurve · F11", () -> M08_DragonCurveTest.main(new String[0]));
            run("L04.09 EvilHangman · F12", () -> M09_EvilHangmanTest.main(new String[0]));
            run("L04.10 WordleStyleWordHunt · F12", () -> M10_WordleStyleWordHuntTest.main(new String[0]));
            run("L04.11 Jotto · F12", () -> M11_JottoTest.main(new String[0]));
            run("L04.12 WordChain · F12", () -> M12_WordChainTest.main(new String[0]));
            run("L04.13 YatzyScorecard · F12", () -> M13_YatzyScorecardTest.main(new String[0]));
            IO.println("13 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M01_HangmanTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I021 F10: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M01_Hangman();
            game.word = "BANANA";
            game.tried = "A";
            check(game.mask().equals("_A_A_A"), "the while loop must include the last letter");
            game.word = "A";
            game.tried = "";
            check(game.mask().equals("_"), "a one-letter word runs once");
            game.word = "";
            check(game.mask().isEmpty(), "an empty word runs zero times without indexing it");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M02_GhostTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I025 F10: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M02_Ghost();
            check(game.possiblePrefix("RO"), "the search must reach dictionary entries beyond the first word");
            check(game.possiblePrefix("GHO"), "the first matching word may return immediately");
            check(!game.possiblePrefix("XY"), "a missing prefix terminates with false");
            game.reset();
            game.command("R");
            check(game.outcome.isEmpty() && game.player == 2, "the actual legal R prefix must keep the game alive");
            game.words = new String[0];
            check(!game.possiblePrefix("A"), "an empty dictionary terminates without access");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M03_FloodTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I055 F10: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M03_Flood();
            game.board = new int[8][8];
            game.board[7][7] = 1;
            game.fill(2);
            check(game.board[7][6] == 2, "the expanding queue must be processed beyond the first cell");
            int changed = 0;
            for (int[] row : game.board) { for (int value : row) { if (value == 2) changed++; } }
            check(changed == 63 && game.board[7][7] == 1, "all and only the connected old colour must change");
            check(game.moves == 1, "one completed flood counts as one move");
            game.spread(new ArrayList<>(), 0, 1);
            check(game.board[7][7] == 1, "an empty work queue performs no iteration");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M04_SameGameTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I082 F10: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M04_SameGame();
            game.board = new int[5][5];
            for (int[] row : game.board) Arrays.fill(row, 1);
            check(game.group(0, 0).size() == 25, "the queue loop must discover the full connected group");
            check(game.group(-1, 0).isEmpty(), "an invalid start is rejected before queue access");
            game.outcome = "";
            game.command("1 1");
            check(game.outcome.startsWith("Board clear") && game.score == 600, "the actual complete group can be removed to win");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M05_WordSearchTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I028 F11: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M05_WordSearch();
            check(game.read(0,0,0,2).equals("CAT"), "the counted loop must include the final selected cell");
            check(game.read(0,2,0,0).equals("TAC"), "the same range must work in reverse");
            check(game.read(2,0,2,4).equals("TIGER"), "five selected cells produce five letters");
            game.reset();
            game.command("1 1 1 3");
            check(game.found.contains("CAT"), "the actual word selection uses the complete loop result");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M06_VirtualScribblerTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I100 F11: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M06_VirtualScribbler();
            game.restart();
            game.paused = false;
            game.burst(1);
            check(game.steps == 1, "a one-step burst must execute once");
            game.burst(0);
            check(game.steps == 1, "a zero-step burst must execute zero times");
            game.burst(4);
            check(game.steps == 5, "a four-step burst adds exactly four pen moves");
            game.paused = true;
            game.burst(4);
            check(game.steps == 5, "the supplied pause rule remains active");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M07_KochSnowflakeCardsTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I107 F11: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M07_KochSnowflakeCards();
            var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
            var pen = image.createGraphics();
            check(game.snowflake(pen, 0) == 3, "the counted loop must draw all three base sides");
            boolean firstSide = false;
            for (int y = 215; y <= 221; y++) {
                for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
            }
            check(firstSide, "the actual first edge must be painted, not merely counted");
            check(game.snowflake(pen, 1) == 12, "three supplied four-segment edges give twelve segments");
            pen.dispose();
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M08_DragonCurveTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I108 F11: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M08_DragonCurve();
            check(game.foldTurns(1).equals(List.of(1)), "one requested fold must run the outer loop once");
            check(game.foldTurns(0).isEmpty(), "zero folds must run zero times");
            check(game.foldTurns(2).equals(List.of(1,1,-1)), "two folds use two outer passes");
            check(game.path(3).size() == 9, "the actual three-fold path has eight segments and nine points");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M09_EvilHangmanTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I022 F12: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M09_EvilHangman();
            check(game.familySize(List.of("ALLY","COOL","GOOD")) == 3, "the family count must accumulate across all words");
            check(game.familySize(List.of()) == 0 && game.familySize(List.of("ALLY")) == 1, "zero and one member are counted correctly");
            game.reset();
            game.pool = new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA"));
            game.command("E");
            check(game.pool.size() == 3 && game.mask.equals("____"), "the actual adversary must keep the largest consistent family");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M10_WordleStyleWordHuntTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I023 F12: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M10_WordleStyleWordHunt();
            game.secret = "LEVEL";
            char[] marks = ".....".toCharArray();
            int[] counts = game.leftoverLetters("ALLEY", marks);
            check(counts['L'-'A'] == 2, "two unmatched L letters need an accumulated count of two");
            check(game.grade("ALLEY").equals(".YYG."), "both guessed L letters receive the actual remaining matches");
            check(game.grade("LEVEL").equals("GGGGG"), "exact matches stay green and consume no leftover count");
            check(game.grade("STONE").equals("....Y"), "an absent letter cannot gain a false match");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M11_JottoTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I024 F12: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M11_Jotto();
            game.secret = "PLANT";
            check(game.matches("PLANT") == 5, "all five shared letters must accumulate");
            check(game.matches("STONE") == 2, "T and N are counted regardless of positions");
            check(game.matches("BRICK") == 0, "no shared letters leaves zero");
            game.reset();
            game.secret = "PLANT";
            game.command("STONE");
            check(game.history.getFirst().contains("2 shared"), "the actual clue uses the repaired total");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M12_WordChainTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I027 F12: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M12_WordChain();
            game.reset();
            check(game.choices().size() == 20, "the accumulated choices must retain all initially legal words");
            game.last = "APPLE";
            check(game.choices().equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "all five E continuations survive the loop");
            game.used.add("EARTH");
            check(!game.choices().contains("EARTH") && game.choices().size() == 4, "used words are excluded without losing other choices");
            game.reset();
            game.command("APPLE");
            check(game.last.equals("APPLE"), "the actual game accepts an early legal word, not only the final dictionary entry");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Kontroly chování právě opravovaného pravidla.
    class M13_YatzyScorecardTest {
        static int checks;

        static void check(boolean okay, String reason) {
            checks++;
            if (!okay) {
                throw new AssertionError("I040 F12: " + reason);
            }
        }

        public static void main(String[] args) throws Exception {
            var game = new M13_YatzyScorecard();
            game.reset();
            game.scores[0] = 1;
            game.scores[1] = 4;
            game.scores[2] = 9;
            check(game.total() == 14, "filled categories must accumulate rather than replace the running sum");
            game.reset();
            check(game.total() == 0, "unfilled negative entries contribute nothing");
            game.scores = new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1};
            check(game.total() == 98, "upper sum 63 adds the bonus exactly once");
            game.scores[5] = 17;
            check(game.total() == 62, "upper sum 62 earns no bonus");
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
