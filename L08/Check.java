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
/// Check one game: `java Check.java M01_Minesweeper.java` (use its actual filename).
/// Run from any folder by giving the path to this file, for example `java L08/Check.java`.
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
            run("L08.01 Minesweeper · F18", () -> M01_MinesweeperTest.main(new String[0]));
            run("L08.02 Net · F18", () -> M02_NetTest.main(new String[0]));
            run("L08.03 BlackBox · F18", () -> M03_BlackBoxTest.main(new String[0]));
            run("L08.04 LightUp · F18", () -> M04_LightUpTest.main(new String[0]));
            run("L08.05 LoopBuilder · F18", () -> M05_LoopBuilderTest.main(new String[0]));
            run("L08.06 AntHighway · F18", () -> M06_AntHighwayTest.main(new String[0]));
            run("L08.07 Bridges · F20", () -> M07_BridgesTest.main(new String[0]));
            run("L08.08 MagnetDominoes · F20", () -> M08_MagnetDominoesTest.main(new String[0]));
            run("L08.09 Shikaku · F20", () -> M09_ShikakuTest.main(new String[0]));
            run("L08.10 Slant · F20", () -> M10_SlantTest.main(new String[0]));
            run("L08.11 BinaryBalance · F20", () -> M11_BinaryBalanceTest.main(new String[0]));
            run("L08.12 RuleGarden · F26", () -> M12_RuleGardenTest.main(new String[0]));
            run("L08.13 ForestFireSandbox · F26", () -> M13_ForestFireSandboxTest.main(new String[0]));
            IO.println("13 missions; " + failed + " still need repair.");
            if (failed > 0) {
                throw new AssertionError("Check one mission with: java Check.java <game filename>; other red missions can wait.");
            }
        }
    }

    // Functional checks: game behaviour only.
    class M01_MinesweeperTest {
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
            var g = new M01_Minesweeper();
            g.mines = new boolean[6][6];
            g.mines[0][0] = true;
            equal(1, g.adjacent(1, 1), "I043/F18: diagonal mine is a neighbour");
            equal(0, g.adjacent(0, 0), "I043/F18: centre does not count itself");
            g.mines[0][1] = g.mines[1][0] = g.mines[1][1] = true;
            equal(3, g.adjacent(0, 0), "I043/F18: clipped corner has three neighbours");
            g.mines = new boolean[6][6];
            for (int r = 1; r <= 3; r++) {
                for (int c = 1; c <= 3; c++) {
                    g.mines[r][c] = true;
                }
            }
            equal(8, g.adjacent(2, 2), "I043/F18: all eight surrounding cells counted");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M02_NetTest {
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
            var g = new M02_Net();
            g.tiles = new int[4][4];
            g.tiles[0][0] = 2;
            expect(!g.connected()[0][1], "I058/F18: neighbour needs a reciprocal connector");
            g.tiles[0][1] = 8;
            expect(g.connected()[0][1], "I058/F18: right-left ports form a connection");
            expect(!g.connected()[1][1], "I058/F18: diagonal tile is not automatically connected");
            g.solvedBoard();
            expect(g.won(), "I058/F18: authored pipe tree remains valid");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M03_BlackBoxTest {
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
            var g = new M03_BlackBox();
            g.reset();
            boolean[][] box = new boolean[4][4];
            box[1][1] = true;
            equal("W1", g.trace(box, "N1"), "I084/F18: a right-side diagonal ball deflects the ray left");
            box = new boolean[4][4];
            equal("S1", g.trace(box, "N1"), "I084/F18: empty corridor crosses straight");
            box[0][0] = true;
            equal("HIT", g.trace(box, "N1"), "I084/F18: direct hit retains priority");
            equal("REFLECT", g.trace(box, "N2"), "I084/F18: entry-edge diagonal reflects");
            g.command("mark 1 1");
            g.command("mark 1 2");
            g.command("check");
            expect(g.message.contains("disagree"), "I084/F18: incorrect guesses give a current disagreement");
            g.command("ray N1");
            equal("Probe recorded", g.message, "I084/F18: a new probe replaces the earlier failed-check event");
            g.command("mark 1 1");
            equal("Guess removed", g.message, "I084/F18: a changed guess reports its own event");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M04_LightUpTest {
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
            var g = new M04_LightUp();
            g.reset();
            g.bulbs[0][2] = true;
            equal(1, g.visible(0, 0), "I086/F18: light travels beyond the immediate neighbour");
            g.bulbs = new boolean[3][3];
            g.bulbs[1][2] = true;
            equal(0, g.visible(1, 0), "I086/F18: numbered wall blocks a ray");
            g.bulbs = new boolean[3][3];
            g.bulbs[2][2] = true;
            equal(0, g.visible(0, 0), "I086/F18: diagonal bulb is not visible");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M05_LoopBuilderTest {
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
            var g = new M05_LoopBuilder();
            equal(new int[]{0, 4}, g.vertices(12), "I088/F18: vertical edge joins the next vertex row");
            equal(new int[]{11, 15}, g.vertices(23), "I088/F18: final vertical edge stays in its column");
            equal(new int[]{14, 15}, g.vertices(11), "I088/F18: horizontal edge joins adjacent columns");
            g.reset();
            for (int n : new int[]{0, 1, 2, 9, 10, 11, 12, 15, 16, 19, 20, 23}) {
                g.edges[n] = true;
            }
            expect(g.oneLoop(), "I088/F18: perimeter edges form one real loop");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M06_AntHighwayTest {
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
            var g = new M06_AntHighway();
            g.reset();
            g.x = 0;
            g.y = 0;
            g.direction = 2;
            g.step();
            equal(80, g.x, "I124/F18: west neighbour wraps to the final column");
            equal(0, g.y, "I124/F18: horizontal step does not change row");
            g.reset();
            g.x = 0;
            g.y = 0;
            g.direction = 1;
            g.black[0][0] = true;
            g.step();
            equal(60, g.y, "I124/F18: north neighbour wraps to final row");
            expect(!g.black[0][0] && g.steps == 1, "I124/F18: old tile flipped exactly once");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M07_BridgesTest {
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
            var g = new M07_Bridges();
            g.restart();
            g.choose(140, 45);
            equal(1, g.bridges[0], "I090/F20: edge changed");
            g.undo();
            equal(0, g.bridges[0], "I090/F20: undo needs an independent previous array");
            int[] copy = g.snapshot();
            copy[0] = 2;
            equal(0, g.bridges[0], "I090/F20: changing snapshot cannot alter live network");
            g.choose(140, 45);
            g.choose(140, 45);
            g.undo();
            equal(1, g.bridges[0], "I090/F20: undo restores exactly the preceding state");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M08_MagnetDominoesTest {
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
            var g = new M08_MagnetDominoes();
            g.restart();
            g.cycle(0);
            g.undo();
            equal(-1, g.state[0], "I092/F20: undo restores undecided magnet");
            g.cycle(0);
            g.cycle(1);
            g.undo();
            equal(1, g.state[0], "I092/F20: undo keeps earlier domino change");
            equal(-1, g.state[1], "I092/F20: latest change reverted");
            int[] copy = g.copyState();
            copy[0] = 0;
            equal(1, g.state[0], "I092/F20: saved state does not alias live poles");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M09_ShikakuTest {
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
            var g = new M09_Shikaku();
            g.restart();
            expect(g.add(0, 0, 1, 2), "I094/F20: valid rectangle added");
            g.startX = 2;
            g.message = "Rejected: earlier drag";
            g.undo();
            equal(0, g.boxes.size(), "I094/F20: undo restores an independent list");
            equal("Previous layout restored", g.message, "I094/F20: undo feedback describes the restored board");
            equal(-1, g.startX, "I094/F20: undo leaves no unfinished drag outline");
            g.add(0, 0, 1, 2);
            g.remove(0, 0);
            g.undo();
            equal(1, g.boxes.size(), "I094/F20: removed rectangle can be restored");
            var copy = g.snapshot();
            copy.clear();
            equal(1, g.boxes.size(), "I094/F20: list edits are not shared");
            expect(copy != g.boxes, "I094/F20: snapshot is a distinct mutable collection");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M10_SlantTest {
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
            var g = new M10_Slant();
            g.reset();
            g.command("1 1 /");
            g.command("undo");
            equal('.', g.cells[0][0], "I085/F20: undo needs independent row arrays");
            g.command("1 1 /");
            g.command("2 2 /");
            g.command("undo");
            equal('/', g.cells[0][0], "I085/F20: older diagonal retained");
            equal('.', g.cells[1][1], "I085/F20: latest diagonal reverted");
            char[][] copy = g.snapshot();
            copy[0][0] = '.';
            equal('/', g.cells[0][0], "I085/F20: snapshot edits cannot change current board");
            g.reset();
            for (int r = 1; r <= 3; r++) {
                for (int c = 1; c <= 3; c++) {
                    g.command(r + " " + c + " /");
                }
            }
            expect(g.solved() && !g.outcome.isEmpty(), "I085/F20: authored all-slash board completes");
            g.command("undo");
            expect(!g.solved() && g.outcome.isEmpty(), "I085/F20: undo after completion restores an unfinished round");
            g.command("3 3 /");
            expect(g.solved() && !g.outcome.isEmpty(), "I085/F20: restored round can be completed again");
            StringBuilder input = new StringBuilder();
            for (int r = 1; r <= 3; r++) {
                for (int c = 1; c <= 3; c++) {
                    input.append(r + " " + c + " /\\n");
                }
            }
            input.append("undo\\n/q\\n");
            var process = new ProcessBuilder(java.nio.file.Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-cp", System.getProperty("java.class.path"), M10_Slant.class.getName()).redirectErrorStream(true).start();
            try {
                try(var stream = process.getOutputStream()) {
                    stream.write(input.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
                }
                expect(process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS), "I085/F20: console replay terminates");
                String output = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                int last = output.lastIndexOf("Cell diagonals");
                expect(process.exitValue() == 0 && last >= 0 && output.substring(last).contains("3 [/, /, .]"), "I085/F20: real finished input loop permits undo");
            } finally {
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M11_BinaryBalanceTest {
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
            var g = new M11_BinaryBalance();
            g.restart();
            g.cycle(1, 0);
            equal(0, g.givens[0][1], "I091/F20: playing must not rewrite the fixed clue template");
            g.restart();
            equal(0, g.cells[0][1], "I091/F20: reset restores originally empty cell");
            int[][] copy = g.copyBoard(g.givens);
            copy[2][1] = 2;
            equal(0, g.givens[2][1], "I091/F20: every inner row is independent");
            equal(1, copy[0][0], "I091/F20: copying retains given values");
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M12_RuleGardenTest {
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
            var g = new M12_RuleGarden();
            equal(java.util.List.of("...#...", "..#.#..", ".#...#."), g.grow(90, 7, 3), "I123/F26: each row reads only the previous generation");
            boolean[] before = {false, true, false, false, false};
            boolean[] saved = before.clone();
            g.nextRow(before, 90);
            equal(saved, before, "I123/F26: computing next row does not overwrite its input");
            equal(java.util.List.of(".#.", "#.#", "..."), g.grow(90, 3, 3), "I123/F26: outside cells stay off");
            var random = new java.util.Random(3);
            for (int sample = 0; sample < 10; sample++) {
                boolean[] row = new boolean[13];
                for (int c = 0; c < 13; c++) {
                    row[c] = random.nextBoolean();
                }
                boolean[] reverseOracle = new boolean[13];
                int rule = random.nextInt(256);
                for (int c = 12; c >= 0; c--) {
                    int pattern = row[c] ? 2 : 0;
                    if (c > 0 && row[c - 1]) {
                        pattern += 4;
                    }
                    if (c < 12 && row[c + 1]) {
                        pattern++;
                    }
                    reverseOracle[c] = ((rule>>pattern)&1) != 0;
                }
                equal(reverseOracle, g.nextRow(row, rule), "I123/F26: traversal order does not affect a generation");
            }
            System.out.println("PASS " + checks + " checks");
        }
    }

    // Functional checks: game behaviour only.
    class M13_ForestFireSandboxTest {
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
            var g = new M13_ForestFireSandbox();
            g.cells = new int[40][60];
            g.cells[20][10] = 2;
            for (int x = 11; x <= 14; x++) {
                g.cells[20][x] = 1;
            }
            g.cells[21][11] = 1;
            g.step();
            equal(1, g.cells[20][12], "I125/F26: fire advances one neighbour, not across the row in one tick");
            equal(0, g.cells[20][10], "I125/F26: old burning tree becomes empty");
            equal(2, g.cells[20][11], "I125/F26: adjacent old tree ignites");
            equal(1, g.cells[21][11], "I125/F26: newly burning tree cannot ignite another during the same tick");
            g.step();
            equal(2, g.cells[20][12], "I125/F26: next tick advances the front");
            int[][] old = new int[40][60];
            old[0][0] = 2;
            old[0][1] = 1;
            int[][] copy = java.util.Arrays.stream(old).map(int[]::clone).toArray(int[][]::new);
            g.nextForest(old);
            equal(copy, old, "I125/F26: source forest remains unchanged");
            var random = new java.util.Random(7);
            int[][] forest = new int[40][60];
            for (var row : forest) {
                for (int x = 0; x < 60; x++) {
                    row[x] = random.nextInt(3);
                }
            }
            int[][] expected = new int[40][60];
            for (int y = 39; y >= 0; y--) {
                for (int x = 59; x >= 0; x--) {
                    if (forest[y][x] == 1) {
                        expected[y][x] = 1;
                        for (var d : new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
                            int xx = x + d[0], yy = y + d[1];
                            if (xx >= 0 && xx < 60 && yy >= 0 && yy < 40 && forest[yy][xx] == 2) {
                                expected[y][x] = 2;
                            }
                        }
                    }
                }
            }
            equal(expected, g.nextForest(forest), "I125/F26: reversing both traversals leaves simultaneous update unchanged");
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
