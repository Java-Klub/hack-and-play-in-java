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
/// Kontrola jedné hry: `java Check.java M01_MemoryPairs.java` (použij její skutečný název souboru).
/// Z jiné složky přidej cestu k tomuto souboru, například `java L05/Check.java`.
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
            run("L05.01 MemoryPairs · F16", () -> M01_MemoryPairsTest.main(new String[0]));
            run("L05.02 MiniBattleshipHunt · F16", () -> M02_MiniBattleshipHuntTest.main(new String[0]));
            run("L05.03 DotsAndBoxes · F16", () -> M03_DotsAndBoxesTest.main(new String[0]));
            run("L05.04 Quarto · F16", () -> M04_QuartoTest.main(new String[0]));
            run("L05.05 Reversi · F17", () -> M05_ReversiTest.main(new String[0]));
            run("L05.06 Hex · F17", () -> M06_HexTest.main(new String[0]));
            run("L05.07 SetStyleTripleHunt · F17", () -> M07_SetStyleTripleHuntTest.main(new String[0]));
            run("L05.08 Nonogram · F17", () -> M08_NonogramTest.main(new String[0]));
            run("L05.09 FourColourMap · F17", () -> M09_FourColourMapTest.main(new String[0]));
            run("L05.10 MirrorLaser · F19", () -> M10_MirrorLaserTest.main(new String[0]));
            run("L05.11 Dominosa · F19", () -> M11_DominosaTest.main(new String[0]));
            run("L05.12 InequalityLatin · F19", () -> M12_InequalityLatinTest.main(new String[0]));
            IO.println("12 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M01_MemoryPairsTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M01_MemoryPairs();
            g.reset(12);
            g.cards = new int[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6};
            g.first = 4;
            equal(5, g.visible(4), "I033/F16: reveal the chosen slot, not card zero");
            equal(0, g.visible(3), "I033/F16: covered card remains hidden");
            g.second = 8;
            equal(3, g.visible(8), "I033/F16: second chosen slot visible");
            g.first = g.second = -1;
            g.matched[11] = true;
            equal(6, g.visible(11), "I033/F16: last matched card stays visible");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M02_MiniBattleshipHuntTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M02_MiniBattleshipHunt();
            for (int layout = 0; layout < 3; layout++) {
                g.layout(layout);
                int[] counts = new int[3];
                for (var row : g.board) {
                    for (int cell : row) {
                        counts[cell]++;
                    }
                }
                equal(3, counts[2], "I041/F16: every layout has all three cells of ship two");
                equal(2, counts[1], "I041/F16: ship one occupies two cells");
            }
            g.layout(0);
            equal(2, g.board[3][4], "I041/F16: last coordinate is not omitted");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M03_DotsAndBoxesTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M03_DotsAndBoxes();
            equal(new int[]{0, 2, 6, 7}, g.box(0), "I007/F16: first box needs its four real edges");
            equal(new int[]{1, 3, 7, 8}, g.box(1), "I007/F16: neighbouring box shares one edge");
            equal(new int[]{3, 5, 10, 11}, g.box(3), "I007/F16: final box has different indices");
            g.reset();
            for (String edge : new String[]{"1", "3", "7", "8"}) {
                g.command(edge);
            }
            expect(g.owners[0] != 0, "I007/F16: closing a real box awards it");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M04_QuartoTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M04_Quarto();
            g.reset();
            int[] diagonal = {0, 5, 10, 15};
            for (int n = 0; n < 4; n++) {
                g.board[diagonal[n]] = n;
            }
            expect(g.won(), "I011/F16: the main diagonal must be searched");
            g.reset();
            for (int n = 0; n < 4; n++) {
                g.board[1 + n * 4] = n;
            }
            expect(g.won(), "I011/F16: a full column can win");
            g.reset();
            for (int n = 0; n < 4; n++) {
                g.board[n] = n;
            }
            expect(g.won(), "I011/F16: existing row wins remain");
            g.reset();
            expect(!g.won(), "I011/F16: empty slots cannot form a line");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M05_ReversiTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M05_Reversi();
            g.reset();
            expect(g.hasMove(1), "I004/F17: search must find a move outside the top row");
            g.board = new int[6][6];
            g.board[5][4] = 2;
            g.board[5][5] = 1;
            expect(g.hasMove(1), "I004/F17: legal move in the final row is not skipped");
            g.board = new int[6][6];
            expect(!g.hasMove(1), "I004/F17: empty board has no bracketed captures");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M06_HexTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M06_Hex();
            g.reset();
            for (int r = 0; r < 5; r++) {
                g.board[r][4 - r] = 1;
            }
            expect(g.connected(1), "I006/F17: hex diagonal is a connected path");
            g.board[2][2] = 0;
            expect(!g.connected(1), "I006/F17: gap breaks the path");
            g.reset();
            for (int c = 0; c < 5; c++) {
                g.board[2][c] = 2;
            }
            expect(g.connected(2), "I006/F17: player two connects left to right");
            expect(!g.connected(1), "I006/F17: opposing pieces are not traversed");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M07_SetStyleTripleHuntTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M07_SetStyleTripleHunt();
            g.table = new java.util.ArrayList<>(java.util.List.of(0, 1, 2));
            expect(g.hasSet(), "I036/F17: the last card can complete the only triple");
            g.table = new java.util.ArrayList<>(java.util.List.of(0, 1, 3));
            expect(!g.hasSet(), "I036/F17: near miss is not a triple");
            g.table = new java.util.ArrayList<>(java.util.List.of(0, 1));
            expect(!g.hasSet(), "I036/F17: fewer than three cards cannot work");
            expect(g.triple(0, 1, 2), "I036/F17: supplied attribute rule still used");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M08_NonogramTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M08_Nonogram();
            equal(new int[]{3}, g.runs(new int[]{0, 1, 1, 1}), "I059/F17: flush the run at the end of the array");
            equal(new int[]{2, 1}, g.runs(new int[]{1, 1, 0, 1}), "I059/F17: separate groups remain separate");
            equal(new int[]{}, g.runs(new int[]{0, 0, 0}), "I059/F17: blank line has no runs");
            equal(new int[]{1}, g.runs(new int[]{1}), "I059/F17: single-cell run");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M09_FourColourMapTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M09_FourColourMap();
            g.reset();
            g.colours = new int[]{1, 2, 3, 4, 4};
            equal(1, g.conflicts(), "I087/F17: conflict on the last adjacency must count");
            g.colours = new int[]{1, 2, 3, 4, 1};
            equal(0, g.conflicts(), "I087/F17: non-adjacent equal colours allowed");
            g.colours = new int[]{1, 1, 1, 0, 0};
            equal(3, g.conflicts(), "I087/F17: count every coloured conflicting edge");
            g.reset();
            equal(0, g.conflicts(), "I087/F17: uncoloured regions are not conflicts");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M10_MirrorLaserTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M10_MirrorLaser();
            var r = new M10_MirrorLaser.RayState(1, 2, 1, 0);
            var next = r.advance();
            equal(new M10_MirrorLaser.RayState(2, 2, 1, 0), next, "I057/F19: record update follows its named direction");
            equal(1, r.x(), "I057/F19: advancing leaves original record unchanged");
            equal(new M10_MirrorLaser.RayState(1, 1, 0, -1), new M10_MirrorLaser.RayState(1, 2, 0, -1).advance(), "I057/F19: vertical update");
            g.restart();
            g.mirrors[2][2] = 1;
            g.mirrors[0][2] = 1;
            g.mirrors[0][5] = 2;
            g.trace();
            equal("Target reached!", g.result, "I057/F19: real beam follows the authored route");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M11_DominosaTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M11_Dominosa();
            g.reset();
            equal(g.pairKey(2, 3), g.pairKey(3, 2), "I083/F19: reversed endpoints describe the same domino value");
            equal(new M11_Dominosa.Domino(0, 1), g.pairKey(2, 3), "I083/F19: record stores ordered endpoint values");
            expect(!g.pairKey(0, 1).equals(g.pairKey(2, 3)), "I083/F19: different unordered pairs stay distinct");
            g.command("1 3 1 4");
            expect(g.used.contains(new M11_Dominosa.Domino(0, 1)), "I083/F19: game tracks the record value");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Funkční kontroly: pouze chování hry.
    class M12_InequalityLatinTest {
        static int checks;
        static void expect(boolean correct, String message) {
            checks++;
            if (!correct) {
                throw new AssertionError(message);
            }
        }
        static void equal(Object expected, Object actual, String message) {
            expect(java.util.Objects.deepEquals(expected, actual), message
            + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
            + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
        }
        public static void main(String[] args) throws Exception {
            var g = new M12_InequalityLatin();
            var rule = new M12_InequalityLatin.Rule(1, 0);
            int[][] values = {{4, 1, 3, 2}};
            expect(rule.satisfiedBy(values), "I093/F19: record coordinates identify the left and right cells");
            values[0][1] = 3;
            expect(!rule.satisfiedBy(values), "I093/F19: equality does not satisfy strict inequality");
            int[][] other = {{4, 1, 3, 2}, {1, 4, 2, 3}};
            expect(!new M12_InequalityLatin.Rule(0, 0).satisfiedBy(other), "I093/F19: changed x reads a different pair");
            expect(new M12_InequalityLatin.Rule(0, 1).satisfiedBy(other), "I093/F19: changed y reads a different row");
            expect(!new M12_InequalityLatin.Rule(1, 1).satisfiedBy(other), "I093/F19: record row and column cannot be ignored");
            expect(new M12_InequalityLatin.Rule(2, 1).satisfiedBy(other), "I093/F19: rightmost pair can satisfy the rule");
            g.restart();
            g.cells = new int[][]{{1, 2, 3, 4}, {2, 3, 4, 1}, {3, 4, 1, 2}, {4, 1, 2, 3}};
            expect(g.won(), "I093/F19: authored Latin square obeys all Rule records");
            g.rules = new M12_InequalityLatin.Rule[]{new M12_InequalityLatin.Rule(0, 3)};
            expect(!g.won(), "I093/F19: a valid Latin square can violate its actual Rule coordinates");
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
