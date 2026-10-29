import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_MiniBattleshipHunt.java
/// Tests call compact programs through reflection in this JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P03_MiniBattleshipHunt", "08.03 MiniBattleshipHunt · F16", () -> P03_MiniBattleshipHuntTest.main(new String[0]));
        run("P04_LoopBuilder", "08.04 LoopBuilder · F18", () -> P04_LoopBuilderTest.main(new String[0]));
        run("P07_TicTacToe", "08.07 TicTacToe · F08", () -> P07_TicTacToeTest.main(new String[0]));
        run("P08_Chomp", "08.08 Chomp · F08", () -> P08_ChompTest.main(new String[0]));
        run("P09_Sudoku", "08.09 Sudoku · F08", () -> P09_SudokuTest.main(new String[0]));
        run("P12_Hexapawn", "08.12 Hexapawn · F13", () -> P12_HexapawnTest.main(new String[0]));
        run("P13_FourColourMap", "08.13 FourColourMap · F17", () -> P13_FourColourMapTest.main(new String[0]));
        run("P14_Minesweeper", "08.14 Minesweeper · F18", () -> P14_MinesweeperTest.main(new String[0]));
        run("P15_Net", "08.15 Net · F18", () -> P15_NetTest.main(new String[0]));
        run("P16_BlackBox", "08.16 BlackBox · F18", () -> P16_BlackBoxTest.main(new String[0]));
        run("P17_AntHighway", "08.17 AntHighway · F18", () -> P17_AntHighwayTest.main(new String[0]));
        run("P20_Quarto", "08.20 Quarto · F16", () -> P20_QuartoTest.main(new String[0]));
        run("P21_Reversi", "08.21 Reversi · F17", () -> P21_ReversiTest.main(new String[0]));
        run("P22_LightUp", "08.22 LightUp · F18", () -> P22_LightUpTest.main(new String[0]));
        run("P25_Slant", "08.25 Slant · F20", () -> P25_SlantTest.main(new String[0]));
        run("P26_BinaryBalance", "08.26 BinaryBalance · F20", () -> P26_BinaryBalanceTest.main(new String[0]));
        run("P27_RuleGarden", "08.27 RuleGarden · F26", () -> P27_RuleGardenTest.main(new String[0]));
        run("P28_ForestFireSandbox", "08.28 ForestFireSandbox · F26", () -> P28_ForestFireSandboxTest.main(new String[0]));
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
class P03_MiniBattleshipHuntTest {
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
        var g = Reflect.create("P03_MiniBattleshipHunt");
        for (int layout = 0; layout < 3; layout++) {
            Reflect.call(g, "layout", layout);
            int[] counts = new int[3];
            for (var row : ((int[][]) Reflect.get(g, "board"))) {
                for (int cell : row) {
                    counts[cell]++;
                }
            }
            equal(3, counts[2], "08.03/F16: every layout has all three cells of ship two");
            equal(2, counts[1], "08.03/F16: ship one occupies two cells");
        }
        Reflect.call(g, "layout", 0);
        equal(2, ((int[][]) Reflect.get(g, "board"))[3][4], "08.03/F16: last coordinate is not omitted");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_LoopBuilderTest {
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
        var g = Reflect.create("P04_LoopBuilder");
        equal(new int[]{0, 4}, ((int[]) Reflect.call(g, "vertices", 12)), "08.04/F18: vertical edge joins the next vertex row");
        equal(new int[]{11, 15}, ((int[]) Reflect.call(g, "vertices", 23)), "08.04/F18: final vertical edge stays in its column");
        equal(new int[]{14, 15}, ((int[]) Reflect.call(g, "vertices", 11)), "08.04/F18: horizontal edge joins adjacent columns");
        Reflect.call(g, "reset");
        for (int n : new int[]{0, 1, 2, 9, 10, 11, 12, 15, 16, 19, 20, 23}) {
            ((boolean[]) Reflect.get(g, "edges"))[n] = true;
        }
        expect(((boolean) Reflect.call(g, "oneLoop")), "08.04/F18: perimeter edges form one real loop");
        equal(new int[]{0, 1}, Reflect.call(g, "vertices", 0), "first horizontal edge");
        equal(new int[]{14, 15}, Reflect.call(g, "vertices", 11), "last horizontal edge");
        boolean rejected = false;
        try { Reflect.call(g, "vertices", 24); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "outside edge index is rejected");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_TicTacToeTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("08.07 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_TicTacToe");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "play", 9)) && ((char[]) Reflect.get(game, "board"))[8] == 'X', "cell nine is a legal empty boundary cell");
        check(!((boolean) Reflect.call(game, "play", 9)) && ((int) Reflect.get(game, "moves")) == 1, "an occupied cell must be rejected without consuming a move");
        check(!((boolean) Reflect.call(game, "play", 0)) && !((boolean) Reflect.call(game, "play", 10)), "out-of-range cells must be rejected before indexing the board");
        check(((char) Reflect.get(game, "player")) == 'O', "rejected commands must not pass play again");
        Reflect.set(game, "winner", 'X');
        check(!((boolean) Reflect.call(game, "play", 1)) && ((char[]) Reflect.get(game, "board"))[0] == '.', "a finished round rejects even an empty legal square");
        check(((int) Reflect.get(game, "moves")) == 1 && ((char) Reflect.get(game, "player")) == 'O', "rejecting a finished round preserves count and turn");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P08_ChompTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("08.08 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Chomp");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 4, 5)) && ((int[]) Reflect.get(game, "widths"))[3] == 4, "the last row must accept a legal remaining bite");
        int[] before = ((int[]) Reflect.get(game, "widths")).clone();
        check(!((boolean) Reflect.call(game, "bite", 5, 1)) && Arrays.equals(before, ((int[]) Reflect.get(game, "widths"))), "an invalid row must not mutate chocolate");
        check(!((boolean) Reflect.call(game, "bite", 4, 5)), "already eaten chocolate must be rejected");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 1, 1)) && ((int) Reflect.get(game, "winner")) == 2, "the legal poison cell still causes a loss");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P09_SudokuTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("08.09 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Sudoku");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "place", 0, 1, 2)) && ((int[][]) Reflect.get(game, "board"))[0][1] == 2, "a valid empty square must accept a number");
        check(!((boolean) Reflect.call(game, "place", 0, 0, 0)) && ((int[][]) Reflect.get(game, "board"))[0][0] == 1, "a printed clue must never be erased");
        check(!((boolean) Reflect.call(game, "place", -1, 0, 1)) && !((boolean) Reflect.call(game, "place", 0, 4, 1)), "invalid coordinates are rejected before clue access");
        check(!((boolean) Reflect.call(game, "place", 0, 2, 2)) && ((int[][]) Reflect.get(game, "board"))[0][2] == 0, "a conflicting number is rejected before writing");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P12_HexapawnTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("08.12 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Hexapawn");
        check(((int) Reflect.call(game, "goalRow", 2)) == 2, "player two must have the bottom goal row");
        check(((int) Reflect.call(game, "goalRow", 1)) == 0, "player one must have the top goal row");
        Reflect.call(game, "reset");
        Reflect.set(game, "player", 2);
        Reflect.set(game, "board", new int[][]{{0,0,0},{2,0,0},{0,0,1}});
        Reflect.call(game, "command", "2 1 3 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).contains("2 wins"), "the real promotion uses goalRow for the current player");
        check(((int) Reflect.call(game, "forwardStep", 1)) == -1 && ((int) Reflect.call(game, "forwardStep", 2)) == 1, "the two armies need opposite forward directions");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P13_FourColourMapTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("08.13/F17: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P13_FourColourMap");
        Reflect.call(game, "reset");
        Reflect.set(game, "map", new String[]{"AABCCDEE"});
        expect(!((boolean) Reflect.call(game, "touches", 0, 3)), "changing geography removes an obsolete A-D border");
        expect((boolean) Reflect.call(game, "touches", 0, 1), "horizontal contact is discovered");
        expect((boolean) Reflect.call(game, "touches", 1, 0), "neighbour order is symmetric");
        expect(!((boolean) Reflect.call(game, "touches", 0, 0)), "a region is not its own neighbouring region");
        Reflect.set(game, "map", new String[]{"AB", "CD"});
        expect((boolean) Reflect.call(game, "touches", 0, 2), "vertical A-C contact is discovered");
        expect((boolean) Reflect.call(game, "touches", 1, 3), "a new vertical B-D border is discovered");
        expect(!((boolean) Reflect.call(game, "touches", 0, 3)), "corner-only A-D contact does not count");
        Reflect.set(game, "map", new String[]{"AA", "BB"});
        int[] colours = {1, 1, 0, 0, 0};
        Reflect.set(game, "colours", colours.clone());
        equal(1, Reflect.call(game, "conflicts"), "a two-cell shared boundary is one conflicting region pair");
        equal(colours, Reflect.get(game, "colours"), "inspection preserves the colouring");
        Reflect.set(game, "map", new String[]{"ABCDE", "ABCDE"});
        Reflect.set(game, "colours", new int[]{2, 2, 2, 2, 2});
        equal(4, Reflect.call(game, "conflicts"), "all and only the four neighbouring pairs conflict");
        Reflect.set(game, "colours", new int[]{1, 2, 1, 2, 1});
        equal(true, Reflect.call(game, "solved"), "real solved query follows the new geography");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P14_MinesweeperTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P14_Minesweeper");
        boolean[][] mines = new boolean[6][6];
        mines[1][1] = true; mines[2][2] = true; mines[3][3] = true; mines[5][5] = true;
        Reflect.set(g, "mines", (Object) mines);
        Reflect.call(g, "inspect", 2, 2);
        boolean[][] lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[1][1] && lens[2][2] && lens[3][3] && !lens[5][5], "08.14: a lens includes diagonal, centre and all local mines only");
        var buttons = (javax.swing.JButton[][]) Reflect.get(g, "buttons");
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) buttons[r][c] = new javax.swing.JButton();
        }
        Reflect.call(g, "refresh");
        equal("*", buttons[1][1].getText(), "08.14: a scanned diagonal mine is really shown on its button");
        expect((int) Reflect.get(g, "opened") == 0 && !(boolean) Reflect.get(g, "lost"), "08.14: inspection does not play a turn or lose");
        equal(new boolean[6][6], Reflect.get(g, "revealed"), "08.14: the safe-cell progress is untouched");
        Reflect.call(g, "inspect", 5, 5);
        lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[5][5] && !lens[1][1], "08.14: moving the lens replaces rather than accumulates its view");
        Reflect.call(g, "inspect", -1, 0);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "08.14: outside inspection clears the lens safely");
        equal(2, Reflect.call(g, "adjacent", 2, 2), "08.14: ordinary mine clues remain correct");
        Reflect.call(g, "reset", 42L);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "08.14: a new board clears the inspection");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_NetTest {
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
        var g = Reflect.create("P15_Net");
        Reflect.set(g, "tiles", new int[4][4]);
        ((int[][]) Reflect.get(g, "tiles"))[0][0] = 2;
        expect(!((boolean[][]) Reflect.call(g, "connected"))[0][1], "08.15/F18: neighbour needs a reciprocal connector");
        ((int[][]) Reflect.get(g, "tiles"))[0][1] = 8;
        expect(((boolean[][]) Reflect.call(g, "connected"))[0][1], "08.15/F18: right-left ports form a connection");
        expect(!((boolean[][]) Reflect.call(g, "connected"))[1][1], "08.15/F18: diagonal tile is not automatically connected");
        Reflect.call(g, "solvedBoard");
        expect(((boolean) Reflect.call(g, "won")), "08.15/F18: authored pipe tree remains valid");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_BlackBoxTest {
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
        var g = Reflect.create("P16_BlackBox");
        Reflect.call(g, "reset");
        boolean[][] box = new boolean[4][4];
        box[1][1] = true;
        equal("W1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "08.16/F18: a right-side diagonal ball deflects the ray left");
        box = new boolean[4][4];
        equal("S1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "08.16/F18: empty corridor crosses straight");
        box[0][0] = true;
        equal("HIT", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "08.16/F18: direct hit retains priority");
        equal("REFLECT", ((java.lang.String) Reflect.call(g, "trace", box, "N2")), "08.16/F18: entry-edge diagonal reflects");
        Reflect.call(g, "command", "mark 1 1");
        Reflect.call(g, "command", "mark 1 2");
        Reflect.call(g, "command", "check");
        expect(((java.lang.String) Reflect.get(g, "message")).contains("disagree"), "08.16/F18: incorrect guesses give a current disagreement");
        Reflect.call(g, "command", "ray N1");
        equal("Probe recorded", ((java.lang.String) Reflect.get(g, "message")), "08.16/F18: a new probe replaces the earlier failed-check event");
        Reflect.call(g, "command", "mark 1 1");
        equal("Guess removed", ((java.lang.String) Reflect.get(g, "message")), "08.16/F18: a changed guess reports its own event");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P17_AntHighwayTest {
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
        var g = Reflect.create("P17_AntHighway");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 2);
        Reflect.call(g, "step");
        equal(80, ((int) Reflect.get(g, "x")), "08.17/F18: west neighbour wraps to the final column");
        equal(0, ((int) Reflect.get(g, "y")), "08.17/F18: horizontal step does not change row");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 1);
        ((boolean[][]) Reflect.get(g, "black"))[0][0] = true;
        Reflect.call(g, "step");
        equal(60, ((int) Reflect.get(g, "y")), "08.17/F18: north neighbour wraps to final row");
        expect(!((boolean[][]) Reflect.get(g, "black"))[0][0] && ((long) Reflect.get(g, "steps")) == 1, "08.17/F18: old tile flipped exactly once");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P20_QuartoTest {
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
        var g = Reflect.create("P20_Quarto");
        Reflect.call(g, "reset");
        int[] diagonal = {0, 5, 10, 15};
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[diagonal[n]] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "08.20/F16: the main diagonal must be searched");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[1 + n * 4] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "08.20/F16: a full column can win");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[n] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "08.20/F16: existing row wins remain");
        Reflect.call(g, "reset");
        expect(!((boolean) Reflect.call(g, "won")), "08.20/F16: empty slots cannot form a line");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P21_ReversiTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("08.21/F17: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P21_Reversi");
        Reflect.call(game, "reset");
        equal(2, Reflect.call(game, "score", 1), "ordinary opening discs still earn one each");
        int[][] board = new int[6][6];
        board[0][0] = 1;
        board[2][2] = board[2][3] = 2;
        Reflect.set(game, "board", board);
        equal(3, Reflect.call(game, "score", 1), "one owned corner is worth three");
        equal(2, Reflect.call(game, "score", 2), "two ordinary discs are worth two");
        expect(!((boolean) Reflect.call(game, "hasMove", 1)) && !((boolean) Reflect.call(game, "hasMove", 2)), "counterexample board has no legal capture for either player");
        Reflect.call(game, "command", "pass");
        Reflect.call(game, "command", "pass");
        equal("Player 1 wins", Reflect.get(game, "outcome"), "the actual outcome uses prize score, not disc count");
        var output = new java.io.ByteArrayOutputStream();
        var before = System.out;
        try (var capture = new java.io.PrintStream(output)) {
            System.setOut(capture);
            Reflect.call(game, "display");
        } finally {
            System.setOut(before);
        }
        expect(output.toString().contains("Score P1: 3  P2: 2"), "the live display uses the same score");
        board = new int[6][6];
        board[0][0] = board[0][5] = board[5][0] = board[5][5] = 2;
        board[0][3] = board[4][5] = 1;
        Reflect.set(game, "board", board);
        int[][] snapshot = new int[6][];
        for (int row = 0; row < board.length; row++) snapshot[row] = board[row].clone();
        equal(12, Reflect.call(game, "score", 2), "all four distinct corners contribute");
        equal(2, Reflect.call(game, "score", 1), "a non-corner edge is still ordinary");
        equal(snapshot, Reflect.get(game, "board"), "scoring preserves every board cell");
        equal(2, board[5][5], "scoring leaves the board unchanged");
        equal(0, board[3][3], "empty squares remain empty and earn nothing");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P22_LightUpTest {
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
        var g = Reflect.create("P22_LightUp");
        Reflect.call(g, "reset");
        ((boolean[][]) Reflect.get(g, "bulbs"))[0][2] = true;
        equal(1, ((int) Reflect.call(g, "visible", 0, 0)), "08.22/F18: light travels beyond the immediate neighbour");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[1][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 1, 0)), "08.22/F18: numbered wall blocks a ray");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[2][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 0, 0)), "08.22/F18: diagonal bulb is not visible");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P25_SlantTest {
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
        var g = Reflect.create("P25_Slant");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "undo");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[0][0], "08.25/F20: undo needs independent row arrays");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "2 2 /");
        Reflect.call(g, "command", "undo");
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "08.25/F20: older diagonal retained");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[1][1], "08.25/F20: latest diagonal reverted");
        char[][] copy = ((char[][]) Reflect.call(g, "snapshot"));
        copy[0][0] = '.';
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "08.25/F20: snapshot edits cannot change current board");
        Reflect.call(g, "reset");
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                Reflect.call(g, "command", r + " " + c + " /");
            }
        }
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "08.25/F20: authored all-slash board completes");
        Reflect.call(g, "command", "undo");
        expect(!((boolean) Reflect.call(g, "solved")) && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "08.25/F20: undo after completion restores an unfinished round");
        Reflect.call(g, "command", "3 3 /");
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "08.25/F20: restored round can be completed again");
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
            Reflect.call(Reflect.create("P25_Slant"), "main");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        String output = captured.toString(java.nio.charset.StandardCharsets.UTF_8);
        int last = output.lastIndexOf("Cell diagonals");
        expect(last >= 0 && output.substring(last).contains("3 [/, /, .]"), "08.25/F20: real finished input loop permits undo");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P26_BinaryBalanceTest {
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
        var g = Reflect.create("P26_BinaryBalance");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 1, 0);
        equal(0, ((int[][]) Reflect.get(g, "givens"))[0][1], "08.26/F20: playing must not rewrite the fixed clue template");
        Reflect.call(g, "restart");
        equal(0, ((int[][]) Reflect.get(g, "cells"))[0][1], "08.26/F20: reset restores originally empty cell");
        int[][] copy = ((int[][]) Reflect.call(g, "copyBoard", (Object) ((int[][]) Reflect.get(g, "givens"))));
        copy[2][1] = 2;
        equal(0, ((int[][]) Reflect.get(g, "givens"))[2][1], "08.26/F20: every inner row is independent");
        equal(1, copy[0][0], "08.26/F20: copying retains given values");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P27_RuleGardenTest {
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
        var g = Reflect.create("P27_RuleGarden");
        equal(java.util.List.of("...#...", "..#.#..", ".#...#."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 7, 3)), "08.27/F26: each row reads only the previous generation");
        boolean[] before = {false, true, false, false, false};
        boolean[] saved = before.clone();
        Reflect.call(g, "nextRow", before, 90);
        equal(saved, before, "08.27/F26: computing next row does not overwrite its input");
        equal(java.util.List.of(".#.", "#.#", "..."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 3, 3)), "08.27/F26: outside cells stay off");
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
            equal(reverseOracle, ((boolean[]) Reflect.call(g, "nextRow", row, rule)), "08.27/F26: traversal order does not affect a generation");
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P28_ForestFireSandboxTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P28_ForestFireSandbox");
        int[][] old = new int[40][60];
        old[20][10] = 2; old[20][9] = 1; old[20][11] = 1; old[20][12] = 1;
        old[19][10] = 1; old[21][10] = 1; old[21][11] = 1;
        int[][] snapshot = java.util.Arrays.stream(old).map(int[]::clone).toArray(int[][]::new);
        int[][] next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[20][11] == 2 && next[20][12] == 1, "08.28: wind advances exactly one cell east");
        expect(next[20][9] == 1 && next[19][10] == 1 && next[21][10] == 1 && next[21][11] == 1, "08.28: no west, north, south or diagonal ignition");
        equal(0, next[20][10], "08.28: old flame burns out");
        equal(snapshot, old, "08.28: old forest remains unchanged");
        Reflect.set(g, "cells", (Object) old); Reflect.call(g, "step");
        equal(next, Reflect.get(g, "cells"), "08.28: actual Step uses the wind rule");
        Reflect.call(g, "step");
        equal(2, ((int[][]) Reflect.get(g, "cells"))[20][12], "08.28: replay advances the next generation");
        old = new int[40][60]; old[0][59] = 2; old[0][0] = 1;
        next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[0][0] == 1 && next[0][59] == 0, "08.28: wind does not wrap across a border");
        System.out.println("PASS " + checks + " checks");
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
