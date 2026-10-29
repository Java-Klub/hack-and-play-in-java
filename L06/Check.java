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
/// Check one game: `java Check.java M01_ConnectFour.java` (use its actual filename).
/// Run from any folder by giving the path to this file, for example `java L06/Check.java`.
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
            run("L06.01 ConnectFour · F14", () -> M01_ConnectFourTest.main(new String[0]));
            run("L06.02 MancalaKalah · F14", () -> M02_MancalaKalahTest.main(new String[0]));
            run("L06.03 SlidingTiles · F14", () -> M03_SlidingTilesTest.main(new String[0]));
            run("L06.04 MiniCrossword · F14", () -> M04_MiniCrosswordTest.main(new String[0]));
            run("L06.05 Sokoban · F15", () -> M05_SokobanTest.main(new String[0]));
            run("L06.06 PegDrop · F15", () -> M06_PegDropTest.main(new String[0]));
            run("L06.07 TrafficJamEscape · F15", () -> M07_TrafficJamEscapeTest.main(new String[0]));
            run("L06.08 FallingBlocks · F15", () -> M08_FallingBlocksTest.main(new String[0]));
            run("L06.09 LightsOut · F33", () -> M09_LightsOutTest.main(new String[0]));
            run("L06.10 Tents · F33", () -> M10_TentsTest.main(new String[0]));
            run("L06.11 PegSolitaire · F33", () -> M11_PegSolitaireTest.main(new String[0]));
            run("L06.12 Merge2048 · F33", () -> M12_Merge2048Test.main(new String[0]));
            IO.println("12 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

    // Functional checks: game behaviour only.
    class M01_ConnectFourTest {
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
            var g = new M01_ConnectFour();
            g.reset();
            g.drop(1);
            g.drop(2);
            equal(2, g.moves, "I003/F14: setup played two moves");
            g.reset();
            equal(0, g.moves, "I003/F14: reset must clear the round field, not a shadow local");
            equal('X', g.player, "I003/F14: first player restored");
            equal('.', g.winner, "I003/F14: winner cleared");
            expect(g.drop(7) && g.moves == 1, "I003/F14: next round starts counting at one");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M02_MancalaKalahTest {
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
            var g = new M02_MancalaKalah();
            g.reset();
            g.player = 1;
            g.pits[6] = 20;
            g.reset();
            equal(0, g.player, "I012/F14: restart resets the shared active-player field");
            equal(0, g.pits[6], "I012/F14: stores belong to the old round");
            equal(24, g.side(0), "I012/F14: six pits regain four stones");
            g.command("3");
            equal(0, g.player, "I012/F14: finishing in own store grants another turn");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M03_SlidingTilesTest {
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
            var g = new M03_SlidingTiles();
            g.moves = 17;
            g.reset(42);
            equal(0, g.moves, "I016/F14: new puzzle clears previous move counter");
            int[] sorted = g.tiles.clone();
            java.util.Arrays.sort(sorted);
            equal(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, sorted, "I016/F14: shuffle keeps all tile values");
            int chosen = -1;
            for (int n = 0; n < 9; n++) {
                if (g.canMove(n)) {
                    chosen = n;
                    break;
                }
            }
            expect(g.play(chosen) && g.moves == 1, "I016/F14: first slide is move one");
            g.reset(42);
            equal(0, g.moves, "I016/F14: repeated restart remains independent");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M04_MiniCrosswordTest {
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
            var g = new M04_MiniCrossword();
            g.reset();
            g.command("1 CAT");
            g.command("2 ARE");
            g.command("3 TEN");
            expect(g.outcome.isEmpty(), "I030/F14: recount is local to each command, not accumulated across calls");
            g.command("4 CAT");
            g.command("5 ARE");
            g.command("6 TEN");
            expect(!g.outcome.isEmpty(), "I030/F14: all six clues finish crossword");
            g.reset();
            g.command("1 CAT");
            g.command("erase 1");
            expect(!g.solved[0] && g.outcome.isEmpty(), "I030/F14: erasing updates the next recount");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M05_SokobanTest {
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
            var g = new M05_Sokoban();
            g.reset();
            expect(!g.floor(0, 2), "I017/F15: wall is not walkable floor");
            expect(g.floor(4, 2), "I017/F15: open cell accepted");
            expect(!g.floor(-1, 2) && !g.floor(2, 5), "I017/F15: outside coordinates rejected");
            int x = g.x, y = g.y, bx = g.bx, by = g.by;
            g.floor(3, 3);
            expect(g.x == x && g.y == y && g.bx == bx && g.by == by, "I017/F15: asking about floor must not move anything");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M06_PegDropTest {
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
            var g = new M06_PegDrop();
            g.restart();
            equal(100, g.scoreAt(3), "I081/F15: return the score of the requested bin");
            equal(10, g.scoreAt(7), "I081/F15: final bin accepted");
            int old = g.score;
            g.scoreAt(2);
            equal(old, g.score, "I081/F15: score query does not award points");
            boolean rejected = false;
            try {
                g.scoreAt(-1);
            } catch (IllegalArgumentException e) {
                rejected = true;
            }
            expect(rejected, "I081/F15: invalid index has an explicit contract");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M07_TrafficJamEscapeTest {
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
            var g = new M07_TrafficJamEscape();
            g.reset();
            int[] oldX = g.xs.clone(), oldY = g.ys.clone();
            expect(!g.fits(0, 1, 2), "I019/F15: blocker detected");
            equal(oldX, g.xs, "I019/F15: failed placement probe must not move the car");
            equal(oldY, g.ys, "I019/F15: query preserves every y position");
            expect(g.fits(1, 2, 3), "I019/F15: legal downward position accepted");
            equal(oldY, g.ys, "I019/F15: successful query is pure too");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M08_FallingBlocksTest {
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
            var g = new M08_FallingBlocks();
            g.restart();
            int oldX = g.px, oldY = g.py;
            expect(g.fits(g.piece, 6, 5), "I054/F15: free trial position fits");
            expect(g.px == oldX && g.py == oldY, "I054/F15: fit query must not teleport the falling piece");
            expect(!g.fits(g.piece, -1, 0), "I054/F15: out-of-bounds trial rejected");
            expect(g.px == oldX && g.py == oldY, "I054/F15: rejected query is also pure");
            g.board[5][6] = 1;
            expect(!g.fits(g.piece, 6, 5), "I054/F15: occupied cells block placement");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M09_LightsOutTest {
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
            var g = new M09_LightsOut();
            var cases = g.learnerChecks();
            for (var example : cases) {
                expect(example.row() >= 0 && example.row()<5 && example.column() >= 0 && example.column()<5,
                "I014/F33: examples must use actual game cells 0..4");
                g.lights = new boolean[5][5];
                g.flip(example.row(), example.column());
                int lit = 0;
                for (var row : g.lights) {
                    for (boolean on : row) {
                        if (on) {
                            lit++;
                        }
                    }
                }
                equal(example.expected(), lit, "I014/F33: your expectation must accept the real flip rule");
            }
            String[] names = {"always-five", "centre-only", "missing-centre", "edge-has-five"};
            for (int mutant = 0; mutant < 4; mutant++) {
                boolean rejected = false;
                for (var e : cases) {
                    int correct = 1 + (e.row()>0 ? 1 : 0) + (e.row()<4 ? 1 : 0) + (e.column()>0 ? 1 : 0) + (e.column()<4 ? 1 : 0);
                    int wrong = switch (mutant) {
                        case 0 -> 5;
                        case 1 -> 1;
                        case 2 -> correct - 1;
                        default -> (e.row() == 0 || e.row() == 4) && (e.column() == 0 || e.column() == 4) ? 3 : 5;
                    };
                    if (wrong != e.expected()) {
                        rejected = true;
                    }
                }
                expect(rejected, "I014/F33: your examples did not reject " + names[mutant]);
            }
            g.verifyExamples();
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M10_TentsTest {
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
            var g = new M10_Tents();
            g.restart();
            var cases = g.learnerChecks();
            for (var e : cases) {
                equal(e.expected(), g.treeAt(e.x(), e.y()), "I089/F33: expectations must accept actual tree positions");
            }
            String[] names = {"only-first-tree", "swapped-coordinates", "every-cell-is-a-tree"};
            for (int mutant = 0; mutant < 3; mutant++) {
                boolean rejected = false;
                for (var e : cases) {
                    boolean wrong = false;
                    if (mutant == 0) {
                        wrong = e.x() == g.trees[0].x && e.y() == g.trees[0].y;
                    }
                    if (mutant == 1) {
                        for (var tree : g.trees) {
                            if (e.x() == tree.y && e.y() == tree.x) {
                                wrong = true;
                            }
                        }
                    }
                    if (mutant == 2) {
                        wrong = true;
                    }
                    if (wrong != e.expected()) {
                        rejected = true;
                    }
                }
                expect(rejected, "I089/F33: your examples did not reject " + names[mutant]);
            }
            g.verifyExamples();
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M11_PegSolitaireTest {
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
            var g = new M11_PegSolitaire();
            g.reset();
            var cases = g.learnerChecks();
            for (var e : cases) {
                equal(e.expected(), g.legal(e.row(), e.column(), e.toRow(), e.toColumn()), "I015/F33: expectations must accept actual legal()");
            }
            String[] names = {"missing-middle-peg", "allows-diagonal", "always-legal"};
            for (int mutant = 0; mutant < 3; mutant++) {
                boolean rejected = false;
                for (var e : cases) {
                    int r = e.row(), c = e.column(), nr = e.toRow(), nc = e.toColumn();
                    boolean inside = r >= 0 && c >= 0 && nr >= 0 && nc >= 0 && r < 5 && c < 5 && nr < 5 && nc < 5;
                    boolean base = inside && Math.abs(nr - r) + Math.abs(nc - c) == 2 && g.board[r][c] == 1 && g.board[nr][nc] == 0;
                    boolean wrong = switch (mutant) {
                        case 0 -> base && (r == nr || c == nc);
                        case 1 -> base && g.board[(r + nr) / 2][(c + nc) / 2] == 1;
                        default -> true;
                    };
                    if (wrong != e.expected()) {
                        rejected = true;
                    }
                }
                expect(rejected, "I015/F33: your examples did not reject " + names[mutant]);
            }
            g.verifyExamples();
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M12_Merge2048Test {
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
            var g = new M12_Merge2048();
            var cases = g.learnerChecks();
            for (var e : cases) {
                expect(e.input().length == 4 && e.expected().length == 4 && e.score() >= 0, "I018/F33: examples use four-cell rows and nonnegative scores");
                for (int value : e.input()) {
                    expect(value == 0 || value >= 2 && value <= 2048 && (value&(value - 1)) == 0, "I018/F33: example inputs must be legal 2048 tile values");
                }
                g.score = 0;
                equal(e.expected(), g.merge(e.input().clone()), "I018/F33: real merge output");
                equal(e.score(), g.score, "I018/F33: real merge score");
            }
            String[] names = {"never-merge", "does-not-compress-before-merge", "chain-merges-new-tile", "drops-score", "only-first-merge"};
            for (int mutant = 0; mutant < 5; mutant++) {
                boolean rejected = false;
                for (var e : cases) {
                    int[] values = mutant == 1 ? e.input().clone() : java.util.Arrays.stream(e.input()).filter(n -> n != 0).toArray();
                    int[] out = new int[4];
                    int at = 0, points = 0;
                    for (int n = 0; n < values.length; n++) {
                        int value = values[n];
                        if (value == 0) {
                            continue;
                        }
                        if (mutant != 0 && (mutant != 4 || points == 0) && n + 1 < values.length && value == values[n + 1]) {
                            value *= 2;
                            points += value;
                            n++;
                            if (mutant == 2) {
                                while (n + 1 < values.length && value == values[n + 1]) {
                                    value *= 2;
                                    points += value;
                                    n++;
                                }
                            }
                        }
                        out[at++] = value;
                    }
                    if (mutant == 3) {
                        points = 0;
                    }
                    if (!java.util.Arrays.equals(out, e.expected()) || points != e.score()) {
                        rejected = true;
                    }
                }
                expect(rejected, "I018/F33: your examples did not reject " + names[mutant]);
            }
            g.verifyExamples();
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
