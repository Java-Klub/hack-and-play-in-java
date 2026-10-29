import module java.base;


/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_Minesweeper.java`
/// Kontroly volají testovací metody přímo v této JVM.
/// Hry zůstávají compact sources; následující kontroly k nim přistupují pomocí reflection.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_Minesweeper", "L08.01 Minesweeper · F18", () -> M01_MinesweeperTest.main(new String[0]));
        run("M02_Net", "L08.02 Net · F18", () -> M02_NetTest.main(new String[0]));
        run("M03_BlackBox", "L08.03 BlackBox · F18", () -> M03_BlackBoxTest.main(new String[0]));
        run("M04_LightUp", "L08.04 LightUp · F18", () -> M04_LightUpTest.main(new String[0]));
        run("M05_LoopBuilder", "L08.05 LoopBuilder · F18", () -> M05_LoopBuilderTest.main(new String[0]));
        run("M06_AntHighway", "L08.06 AntHighway · F18", () -> M06_AntHighwayTest.main(new String[0]));
        run("M07_Bridges", "L08.07 Bridges · F20", () -> M07_BridgesTest.main(new String[0]));
        run("M08_MagnetDominoes", "L08.08 MagnetDominoes · F20", () -> M08_MagnetDominoesTest.main(new String[0]));
        run("M09_Shikaku", "L08.09 Shikaku · F20", () -> M09_ShikakuTest.main(new String[0]));
        run("M10_Slant", "L08.10 Slant · F20", () -> M10_SlantTest.main(new String[0]));
        run("M11_BinaryBalance", "L08.11 BinaryBalance · F20", () -> M11_BinaryBalanceTest.main(new String[0]));
        run("M12_RuleGarden", "L08.12 RuleGarden · F26", () -> M12_RuleGardenTest.main(new String[0]));
        run("M13_ForestFireSandbox", "L08.13 ForestFireSandbox · F26", () -> M13_ForestFireSandboxTest.main(new String[0]));
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
class M01_MinesweeperTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M01_Minesweeper");
        boolean[][] mines = new boolean[6][6];
        mines[1][1] = true; mines[2][2] = true; mines[3][3] = true; mines[5][5] = true;
        Reflect.set(g, "mines", (Object) mines);
        Reflect.call(g, "inspect", 2, 2);
        boolean[][] lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[1][1] && lens[2][2] && lens[3][3] && !lens[5][5], "L08.01: a lens includes diagonal, centre and all local mines only");
        var buttons = (javax.swing.JButton[][]) Reflect.get(g, "buttons");
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) buttons[r][c] = new javax.swing.JButton();
        }
        Reflect.call(g, "refresh");
        equal("*", buttons[1][1].getText(), "L08.01: a scanned diagonal mine is really shown on its button");
        expect((int) Reflect.get(g, "opened") == 0 && !(boolean) Reflect.get(g, "lost"), "L08.01: inspection does not play a turn or lose");
        equal(new boolean[6][6], Reflect.get(g, "revealed"), "L08.01: the safe-cell progress is untouched");
        Reflect.call(g, "inspect", 5, 5);
        lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[5][5] && !lens[1][1], "L08.01: moving the lens replaces rather than accumulates its view");
        Reflect.call(g, "inspect", -1, 0);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "L08.01: outside inspection clears the lens safely");
        equal(2, Reflect.call(g, "adjacent", 2, 2), "L08.01: ordinary mine clues remain correct");
        Reflect.call(g, "reset", 42L);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "L08.01: a new board clears the inspection");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M02_Net");
        Reflect.set(g, "tiles", new int[4][4]);
        ((int[][]) Reflect.get(g, "tiles"))[0][0] = 2;
        expect(!((boolean[][]) Reflect.call(g, "connected"))[0][1], "L08.02/F18: neighbour needs a reciprocal connector");
        ((int[][]) Reflect.get(g, "tiles"))[0][1] = 8;
        expect(((boolean[][]) Reflect.call(g, "connected"))[0][1], "L08.02/F18: right-left ports form a connection");
        expect(!((boolean[][]) Reflect.call(g, "connected"))[1][1], "L08.02/F18: diagonal tile is not automatically connected");
        Reflect.call(g, "solvedBoard");
        expect(((boolean) Reflect.call(g, "won")), "L08.02/F18: authored pipe tree remains valid");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M03_BlackBox");
        Reflect.call(g, "reset");
        boolean[][] box = new boolean[4][4];
        box[1][1] = true;
        equal("W1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "L08.03/F18: a right-side diagonal ball deflects the ray left");
        box = new boolean[4][4];
        equal("S1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "L08.03/F18: empty corridor crosses straight");
        box[0][0] = true;
        equal("HIT", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "L08.03/F18: direct hit retains priority");
        equal("REFLECT", ((java.lang.String) Reflect.call(g, "trace", box, "N2")), "L08.03/F18: entry-edge diagonal reflects");
        Reflect.call(g, "command", "mark 1 1");
        Reflect.call(g, "command", "mark 1 2");
        Reflect.call(g, "command", "check");
        expect(((java.lang.String) Reflect.get(g, "message")).contains("disagree"), "L08.03/F18: incorrect guesses give a current disagreement");
        Reflect.call(g, "command", "ray N1");
        equal("Probe recorded", ((java.lang.String) Reflect.get(g, "message")), "L08.03/F18: a new probe replaces the earlier failed-check event");
        Reflect.call(g, "command", "mark 1 1");
        equal("Guess removed", ((java.lang.String) Reflect.get(g, "message")), "L08.03/F18: a changed guess reports its own event");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M04_LightUp");
        Reflect.call(g, "reset");
        ((boolean[][]) Reflect.get(g, "bulbs"))[0][2] = true;
        equal(1, ((int) Reflect.call(g, "visible", 0, 0)), "L08.04/F18: light travels beyond the immediate neighbour");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[1][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 1, 0)), "L08.04/F18: numbered wall blocks a ray");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[2][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 0, 0)), "L08.04/F18: diagonal bulb is not visible");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M05_LoopBuilder");
        equal(new int[]{0, 4}, ((int[]) Reflect.call(g, "vertices", 12)), "L08.05/F18: vertical edge joins the next vertex row");
        equal(new int[]{11, 15}, ((int[]) Reflect.call(g, "vertices", 23)), "L08.05/F18: final vertical edge stays in its column");
        equal(new int[]{14, 15}, ((int[]) Reflect.call(g, "vertices", 11)), "L08.05/F18: horizontal edge joins adjacent columns");
        Reflect.call(g, "reset");
        for (int n : new int[]{0, 1, 2, 9, 10, 11, 12, 15, 16, 19, 20, 23}) {
            ((boolean[]) Reflect.get(g, "edges"))[n] = true;
        }
        expect(((boolean) Reflect.call(g, "oneLoop")), "L08.05/F18: perimeter edges form one real loop");
        equal(new int[]{0, 1}, Reflect.call(g, "vertices", 0), "first horizontal edge");
        equal(new int[]{14, 15}, Reflect.call(g, "vertices", 11), "last horizontal edge");
        boolean rejected = false;
        try { Reflect.call(g, "vertices", 24); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "outside edge index is rejected");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M06_AntHighway");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 2);
        Reflect.call(g, "step");
        equal(80, ((int) Reflect.get(g, "x")), "L08.06/F18: west neighbour wraps to the final column");
        equal(0, ((int) Reflect.get(g, "y")), "L08.06/F18: horizontal step does not change row");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 1);
        ((boolean[][]) Reflect.get(g, "black"))[0][0] = true;
        Reflect.call(g, "step");
        equal(60, ((int) Reflect.get(g, "y")), "L08.06/F18: north neighbour wraps to final row");
        expect(!((boolean[][]) Reflect.get(g, "black"))[0][0] && ((long) Reflect.get(g, "steps")) == 1, "L08.06/F18: old tile flipped exactly once");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_BridgesTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M07_Bridges");
        Reflect.call(g, "restart");
        Reflect.call(g, "choose", 145, 45);
        equal(1, ((int[]) Reflect.get(g, "bridges"))[0], "L08.07: a real click adds a bridge");
        for (int n = 0; n < 6; n++) {
            Reflect.call(g, "key", java.awt.event.KeyEvent.VK_U, true);
            equal(n % 2, ((int[]) Reflect.get(g, "bridges"))[0], "L08.07: repeated U alternates both hypotheses");
        }
        Reflect.call(g, "choose", 295, 45);
        Reflect.call(g, "undo");
        equal(0, ((int[]) Reflect.get(g, "bridges"))[1], "L08.07: a new real edit establishes the latest pair");
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "bridges"))[1], "L08.07: the new pair also switches back");
        int[] saved = (int[]) Reflect.call(g, "snapshot");
        ((int[]) Reflect.get(g, "bridges"))[0] = 2;
        equal(1, saved[0], "L08.07: a saved hypothesis is independent of later edits");
        Reflect.call(g, "restart");
        Reflect.call(g, "undo");
        equal(new int[10], Reflect.get(g, "bridges"), "L08.07: no previous choice after restart");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M08_MagnetDominoes");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "undo");
        equal(-1, ((int[]) Reflect.get(g, "state"))[0], "L08.08/F20: undo restores undecided magnet");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "cycle", 1);
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "L08.08/F20: undo keeps earlier domino change");
        equal(-1, ((int[]) Reflect.get(g, "state"))[1], "L08.08/F20: latest change reverted");
        int[] copy = ((int[]) Reflect.call(g, "copyState"));
        copy[0] = 0;
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "L08.08/F20: saved state does not alias live poles");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M09_Shikaku");
        Reflect.call(g, "restart");
        expect(((boolean) Reflect.call(g, "add", 0, 0, 1, 2)), "L08.09/F20: valid rectangle added");
        Reflect.set(g, "startX", 2);
        Reflect.set(g, "message", "Rejected: earlier drag");
        Reflect.call(g, "undo");
        equal(0, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "L08.09/F20: undo restores an independent list");
        equal("Previous layout restored", ((java.lang.String) Reflect.get(g, "message")), "L08.09/F20: undo feedback describes the restored board");
        equal(-1, ((int) Reflect.get(g, "startX")), "L08.09/F20: undo leaves no unfinished drag outline");
        Reflect.call(g, "add", 0, 0, 1, 2);
        Reflect.call(g, "remove", 0, 0);
        Reflect.call(g, "undo");
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "L08.09/F20: removed rectangle can be restored");
        var copy = ((java.util.ArrayList<Object>) Reflect.call(g, "snapshot"));
        copy.clear();
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "L08.09/F20: list edits are not shared");
        expect(copy != ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")), "L08.09/F20: snapshot is a distinct mutable collection");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M10_Slant");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "undo");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[0][0], "L08.10/F20: undo needs independent row arrays");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "2 2 /");
        Reflect.call(g, "command", "undo");
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "L08.10/F20: older diagonal retained");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[1][1], "L08.10/F20: latest diagonal reverted");
        char[][] copy = ((char[][]) Reflect.call(g, "snapshot"));
        copy[0][0] = '.';
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "L08.10/F20: snapshot edits cannot change current board");
        Reflect.call(g, "reset");
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                Reflect.call(g, "command", r + " " + c + " /");
            }
        }
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "L08.10/F20: authored all-slash board completes");
        Reflect.call(g, "command", "undo");
        expect(!((boolean) Reflect.call(g, "solved")) && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "L08.10/F20: undo after completion restores an unfinished round");
        Reflect.call(g, "command", "3 3 /");
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "L08.10/F20: restored round can be completed again");
        StringBuilder input = new StringBuilder();
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                input.append(r + " " + c + " /\n");
            }
        }
        input.append("undo\n/q\n");
        var originalIn = System.in;
        var originalOut = System.out;
        var captured = new java.io.ByteArrayOutputStream();
        try (var redirected = new java.io.PrintStream(captured, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setIn(new java.io.ByteArrayInputStream(input.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            System.setOut(redirected);
            Reflect.call(Reflect.create("M10_Slant"), "main");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        String output = captured.toString(java.nio.charset.StandardCharsets.UTF_8);
        int last = output.lastIndexOf("Cell diagonals");
        expect(last >= 0 && output.substring(last).contains("3 [/, /, .]"), "L08.10/F20: real finished input loop permits undo");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M11_BinaryBalance");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 1, 0);
        equal(0, ((int[][]) Reflect.get(g, "givens"))[0][1], "L08.11/F20: playing must not rewrite the fixed clue template");
        Reflect.call(g, "restart");
        equal(0, ((int[][]) Reflect.get(g, "cells"))[0][1], "L08.11/F20: reset restores originally empty cell");
        int[][] copy = ((int[][]) Reflect.call(g, "copyBoard", (Object) ((int[][]) Reflect.get(g, "givens"))));
        copy[2][1] = 2;
        equal(0, ((int[][]) Reflect.get(g, "givens"))[2][1], "L08.11/F20: every inner row is independent");
        equal(1, copy[0][0], "L08.11/F20: copying retains given values");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M12_RuleGarden");
        equal(java.util.List.of("...#...", "..#.#..", ".#...#."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 7, 3)), "L08.12/F26: each row reads only the previous generation");
        boolean[] before = {false, true, false, false, false};
        boolean[] saved = before.clone();
        Reflect.call(g, "nextRow", before, 90);
        equal(saved, before, "L08.12/F26: computing next row does not overwrite its input");
        equal(java.util.List.of(".#.", "#.#", "..."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 3, 3)), "L08.12/F26: outside cells stay off");
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
            equal(reverseOracle, ((boolean[]) Reflect.call(g, "nextRow", row, rule)), "L08.12/F26: traversal order does not affect a generation");
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M13_ForestFireSandboxTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M13_ForestFireSandbox");
        int[][] old = new int[40][60];
        old[20][10] = 2; old[20][9] = 1; old[20][11] = 1; old[20][12] = 1;
        old[19][10] = 1; old[21][10] = 1; old[21][11] = 1;
        int[][] snapshot = java.util.Arrays.stream(old).map(int[]::clone).toArray(int[][]::new);
        int[][] next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[20][11] == 2 && next[20][12] == 1, "L08.13: wind advances exactly one cell east");
        expect(next[20][9] == 1 && next[19][10] == 1 && next[21][10] == 1 && next[21][11] == 1, "L08.13: no west, north, south or diagonal ignition");
        equal(0, next[20][10], "L08.13: old flame burns out");
        equal(snapshot, old, "L08.13: old forest remains unchanged");
        Reflect.set(g, "cells", (Object) old); Reflect.call(g, "step");
        equal(next, Reflect.get(g, "cells"), "L08.13: actual Step uses the wind rule");
        Reflect.call(g, "step");
        equal(2, ((int[][]) Reflect.get(g, "cells"))[20][12], "L08.13: replay advances the next generation");
        old = new int[40][60]; old[0][59] = 2; old[0][0] = 1;
        next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[0][0] == 1 && next[0][59] == 0, "L08.13: wind does not wrap across a border");
        System.out.println("PASS " + checks + " checks");
    }
}

// Připravený přístup ke compact programům. Testy používají běžné assertions, pole a JDK collections.
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
