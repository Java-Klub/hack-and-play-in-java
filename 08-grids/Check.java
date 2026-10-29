import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Game task checks for this chapter. Run short textbook tasks directly.
class Check {
    int failed;
    int checked;
    String selected;
    final ArrayList<String> available = new ArrayList<>();
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) {
            IO.println("Supply at most one game filename. With no argument, all game tasks in this chapter are checked.");
            System.exit(2);
        }
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P03_Task_MiniBattleshipHunt", () -> P03_Task_MiniBattleshipHuntTest.main(new String[0]));
        run("P04_Task_LoopBuilder", () -> P04_Task_LoopBuilderTest.main(new String[0]));
        run("P07_Task_TicTacToe", () -> P07_Task_TicTacToeTest.main(new String[0]));
        run("P08_Task_Chomp", () -> P08_Task_ChompTest.main(new String[0]));
        run("P09_Task_Sudoku", () -> P09_Task_SudokuTest.main(new String[0]));
        run("P12_Task_Hexapawn", () -> P12_Task_HexapawnTest.main(new String[0]));
        run("P13_Task_FourColourMap", () -> P13_Task_FourColourMapTest.main(new String[0]));
        run("P14_Task_Minesweeper", () -> P14_Task_MinesweeperTest.main(new String[0]));
        run("P15_Task_Net", () -> P15_Task_NetTest.main(new String[0]));
        run("P16_Task_BlackBox", () -> P16_Task_BlackBoxTest.main(new String[0]));
        run("P17_Task_AntHighway", () -> P17_Task_AntHighwayTest.main(new String[0]));
        run("P20_Task_Quarto", () -> P20_Task_QuartoTest.main(new String[0]));
        run("P21_Task_Reversi", () -> P21_Task_ReversiTest.main(new String[0]));
        run("P22_Task_LightUp", () -> P22_Task_LightUpTest.main(new String[0]));
        run("P25_Task_Slant", () -> P25_Task_SlantTest.main(new String[0]));
        run("P26_Task_BinaryBalance", () -> P26_Task_BinaryBalanceTest.main(new String[0]));
        run("P27_Task_RuleGarden", () -> P27_Task_RuleGardenTest.main(new String[0]));
        run("P28_Task_ForestFireSandbox", () -> P28_Task_ForestFireSandboxTest.main(new String[0]));
        if (checked == 0) {
            IO.println("No game check is prepared here for this file: " + selected + ".java");
            IO.println("Choose one of these files: " + String.join(", ", available));
            System.exit(2);
        }
        IO.println("Checked: " + checked + "; complete: " + (checked - failed) + "; remaining: " + failed);
        if (failed > 0) System.exit(1);
    }

    void run(String game, Test test) {
        available.add(game + ".java");
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + game + ".java");
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + game + ".java");
            String reason = failure.getMessage();
            if (reason == null || reason.isBlank()) reason = "The check did not get the expected result.";
            if (!(failure instanceof AssertionError)) reason = "The program stopped during the check: " + failure.getClass().getSimpleName() + ": " + reason;
            IO.println("  " + reason.replace("\n", "\n  "));
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            if (cause != failure) IO.println("  " + cause.getClass().getSimpleName() + ": " + show(cause.getMessage()));
            for (var frame : cause.getStackTrace()) {
                if (frame.getClassName().equals(game) || frame.getClassName().startsWith(game + "$")) {
                    IO.println("  " + "Location in the program: " + frame.getFileName() + ":" + frame.getLineNumber());
                    break;
                }
            }
        }
    }

    static String show(Object value) {
        if (value != null && value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value);
            var parts = new ArrayList<String>();
            for (int i = 0; i < Math.min(length, 8); i++) parts.add(show(java.lang.reflect.Array.get(value, i)));
            return parts + (length > 8 ? " … (" + length + " items" + ")" : "");
        }
        String text = String.valueOf(value).replace("\n", "\\n").replace("\r", "\\r");
        return text.length() <= 180 ? text : text.substring(0, 177) + "…";
    }
}

@SuppressWarnings("unchecked")
class P03_Task_MiniBattleshipHuntTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P03_Task_MiniBattleshipHunt");
        for (int layout = 0; layout < 3; layout++) {
            Reflect.call(g, "layout", layout);
            int[] counts = new int[3];
            for (var row : ((int[][]) Reflect.get(g, "board"))) {
                for (int cell : row) {
                    counts[cell]++;
                }
            }
            equal(3, counts[2], "layout must place all three cells of ship 2 in every fleet arrangement. Check that its loop visits every selected coordinate.");
            equal(2, counts[1], "layout must place exactly two cells of ship 1 in every fleet arrangement.");
        }
        Reflect.call(g, "layout", 0);
        equal(2, ((int[][]) Reflect.get(g, "board"))[3][4], "For layout(0), board[3][4] must contain ship 2. The last coordinate must not be skipped.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_LoopBuilderTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P04_Task_LoopBuilder");
        equal(new int[]{0, 4}, ((int[]) Reflect.call(g, "vertices", 12)), "vertices(12) must return [0, 4]: the first vertical edge joins adjacent vertex rows.");
        equal(new int[]{11, 15}, ((int[]) Reflect.call(g, "vertices", 23)), "vertices(23) must return [11, 15]: the last vertical edge stays in its column.");
        equal(new int[]{14, 15}, ((int[]) Reflect.call(g, "vertices", 11)), "vertices(11) must return [14, 15]: the final horizontal edge joins adjacent columns.");
        Reflect.call(g, "reset");
        for (int n : new int[]{0, 1, 2, 9, 10, 11, 12, 15, 16, 19, 20, 23}) {
            ((boolean[]) Reflect.get(g, "edges"))[n] = true;
        }
        expect(((boolean) Reflect.call(g, "oneLoop")), "oneLoop must recognise the twelve outer perimeter edges as one closed loop. Check the endpoints returned by vertices.");
        equal(new int[]{0, 1}, Reflect.call(g, "vertices", 0), "vertices(0) must return [0, 1] for the first horizontal edge.");
        equal(new int[]{14, 15}, Reflect.call(g, "vertices", 11), "vertices(11) must return [14, 15] for the last horizontal edge.");
        boolean rejected = false;
        try { Reflect.call(g, "vertices", 24); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "vertices must reject edge numbers outside 0–23 rather than invent endpoints.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_TicTacToeTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_TicTacToe");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "play", 9)) && ((char[]) Reflect.get(game, "board"))[8] == 'X', "play(9) must accept the empty final square and put X there. Check mayPlay's valid range.");
        check(!((boolean) Reflect.call(game, "play", 9)) && ((int) Reflect.get(game, "moves")) == 1, "Playing square 9 again must be rejected without increasing the move count.");
        check(!((boolean) Reflect.call(game, "play", 0)) && !((boolean) Reflect.call(game, "play", 10)), "play must reject squares 0 and 10 before reading the board. Valid player-facing squares are 1–9.");
        check(((char) Reflect.get(game, "player")) == 'O', "Rejected moves must leave O on turn after X has played once.");
        Reflect.set(game, "winner", 'X');
        check(!((boolean) Reflect.call(game, "play", 1)) && ((char[]) Reflect.get(game, "board"))[0] == '.', "After the round ends, play(1) must reject even an empty square and leave it empty.");
        check(((int) Reflect.get(game, "moves")) == 1 && ((char) Reflect.get(game, "player")) == 'O', "Rejecting a move after the round ends must preserve the move count and current player.");
    }
}

@SuppressWarnings("unchecked")
class P08_Task_ChompTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Task_Chomp");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 4, 5)) && ((int[]) Reflect.get(game, "widths"))[3] == 4, "bite(4, 5) must accept the remaining chocolate in the fourth row and shorten that row to four cells.");
        int[] before = ((int[]) Reflect.get(game, "widths")).clone();
        check(!((boolean) Reflect.call(game, "bite", 5, 1)) && Arrays.equals(before, ((int[]) Reflect.get(game, "widths"))), "bite(5, 1) must reject the nonexistent fifth row without changing any chocolate.");
        check(!((boolean) Reflect.call(game, "bite", 4, 5)), "A second bite(4, 5) must reject the piece already eaten by the first bite.");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 1, 1)) && ((int) Reflect.get(game, "winner")) == 2, "bite(1, 1) must still allow eating the poison cell and award the win to the other player.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_SudokuTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Task_Sudoku");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "place", 0, 1, 2)) && ((int[][]) Reflect.get(game, "board"))[0][1] == 2, "place(0, 1, 2) must accept 2 in that initially empty, legal square.");
        check(!((boolean) Reflect.call(game, "place", 0, 0, 0)) && ((int[][]) Reflect.get(game, "board"))[0][0] == 1, "place(0, 0, 0) must not erase the fixed 1 in the top-left square.");
        check(!((boolean) Reflect.call(game, "place", -1, 0, 1)) && !((boolean) Reflect.call(game, "place", 0, 4, 1)), "place must reject row -1 and column 4 before reading clues or writing the board.");
        check(!((boolean) Reflect.call(game, "place", 0, 2, 2)) && ((int[][]) Reflect.get(game, "board"))[0][2] == 0, "place(0, 2, 2) must reject the repeated 2 and leave that square empty.");
    }
}

@SuppressWarnings("unchecked")
class P12_Task_HexapawnTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Task_Hexapawn");
        check(((int) Reflect.call(game, "goalRow", 2)) == 2, "goalRow(2) must identify the bottom row, whose internal index is 2.");
        check(((int) Reflect.call(game, "goalRow", 1)) == 0, "goalRow(1) must identify the top row, whose internal index is 0.");
        Reflect.call(game, "reset");
        Reflect.set(game, "player", 2);
        Reflect.set(game, "board", new int[][]{{0,0,0},{2,0,0},{0,0,1}});
        Reflect.call(game, "command", "2 1 3 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).contains("2 wins"), "When player 2 reaches the bottom row in real play, the game must announce player 2's win.");
        check(((int) Reflect.call(game, "forwardStep", 1)) == -1 && ((int) Reflect.call(game, "forwardStep", 2)) == 1, "forwardStep must move player 1 up by one row and player 2 down by one row.");
    }
}

@SuppressWarnings("unchecked")
class P13_Task_FourColourMapTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P13_Task_FourColourMap");
        Reflect.call(game, "reset");
        Reflect.set(game, "map", new String[]{"AABCCDEE"});
        expect(!((boolean) Reflect.call(game, "touches", 0, 3)), "touches must read the changed map: A and D no longer share a side.");
        expect((boolean) Reflect.call(game, "touches", 0, 1), "touches must detect the side shared horizontally by A and B.");
        expect((boolean) Reflect.call(game, "touches", 1, 0), "touches must give the same answer for B–A as for A–B.");
        expect(!((boolean) Reflect.call(game, "touches", 0, 0)), "touches(A, A) must be false: a region is not its own neighbour.");
        Reflect.set(game, "map", new String[]{"AB", "CD"});
        expect((boolean) Reflect.call(game, "touches", 0, 2), "touches must detect that A is directly above C in this map.");
        expect((boolean) Reflect.call(game, "touches", 1, 3), "touches must detect that B is directly above D in the changed map.");
        expect(!((boolean) Reflect.call(game, "touches", 0, 3)), "A and D touch only at a corner here, so touches must return false.");
        Reflect.set(game, "map", new String[]{"AA", "BB"});
        int[] colours = {1, 1, 0, 0, 0};
        Reflect.set(game, "colours", colours.clone());
        equal(1, Reflect.call(game, "conflicts"), "conflicts must count a pair of same-coloured neighbours once, even when their border is two cells long.");
        equal(colours, Reflect.get(game, "colours"), "Reading neighbours and conflicts must not change the player's colours.");
        Reflect.set(game, "map", new String[]{"ABCDE", "ABCDE"});
        Reflect.set(game, "colours", new int[]{2, 2, 2, 2, 2});
        equal(4, Reflect.call(game, "conflicts"), "When every region has the same colour, this map has exactly four conflicting neighbour pairs.");
        Reflect.set(game, "colours", new int[]{1, 2, 1, 2, 1});
        equal(true, Reflect.call(game, "solved"), "solved must use the changed map's actual neighbours when deciding whether colouring is complete.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_MinesweeperTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P14_Task_Minesweeper");
        boolean[][] mines = new boolean[6][6];
        mines[1][1] = true; mines[2][2] = true; mines[3][3] = true; mines[5][5] = true;
        Reflect.set(g, "mines", (Object) mines);
        Reflect.call(g, "inspect", 2, 2);
        boolean[][] lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[1][1] && lens[2][2] && lens[3][3] && !lens[5][5], "inspect(2, 2) must include mines at the centre and diagonals inside its 3×3 view, but not the distant mine at (5, 5).");
        var buttons = (javax.swing.JButton[][]) Reflect.get(g, "buttons");
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) buttons[r][c] = new javax.swing.JButton();
        }
        Reflect.call(g, "refresh");
        equal("*", buttons[1][1].getText(), "refresh must show * on the button of a diagonal mine found by Inspect.");
        expect((int) Reflect.get(g, "opened") == 0 && !(boolean) Reflect.get(g, "lost"), "Inspect must neither increase opened nor set lost; looking is not a played move.");
        equal(new boolean[6][6], Reflect.get(g, "revealed"), "Inspect must leave every revealed square unchanged.");
        Reflect.call(g, "inspect", 5, 5);
        lens = (boolean[][]) Reflect.get(g, "inspected");
        expect(lens[5][5] && !lens[1][1], "A new inspection must replace the old view, not keep distant mines visible.");
        Reflect.call(g, "inspect", -1, 0);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "Inspect outside the board must safely clear the extra view without reading an invalid cell.");
        equal(2, Reflect.call(g, "adjacent", 2, 2), "adjacent(2, 2) must still count two neighbouring mines, excluding the centre itself.");
        Reflect.call(g, "reset", 42L);
        equal(new boolean[6][6], Reflect.get(g, "inspected"), "Starting a new board must clear all mines shown only by Inspect.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_NetTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P15_Task_Net");
        Reflect.set(g, "tiles", new int[4][4]);
        ((int[][]) Reflect.get(g, "tiles"))[0][0] = 2;
        expect(!((boolean[][]) Reflect.call(g, "connected"))[0][1], "linked must reject a neighbouring tile that has no connector facing back to the current tile.");
        ((int[][]) Reflect.get(g, "tiles"))[0][1] = 8;
        expect(((boolean[][]) Reflect.call(g, "connected"))[0][1], "A right-facing port and the neighbour's left-facing port must connect and carry power.");
        expect(!((boolean[][]) Reflect.call(g, "connected"))[1][1], "A diagonal tile must not receive power merely because it touches the source at a corner.");
        Reflect.call(g, "solvedBoard");
        expect(((boolean) Reflect.call(g, "won")), "The supplied correctly connected pipe tree must satisfy won. Check both ends of each linked connection.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_BlackBoxTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P16_Task_BlackBox");
        Reflect.call(g, "reset");
        boolean[][] box = new boolean[4][4];
        box[1][1] = true;
        equal("W1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "trace from N1 must exit at W1 when a ball lies diagonally to the ray's right. Check the two different side neighbours.");
        box = new boolean[4][4];
        equal("S1", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "trace from N1 through an empty box must continue straight to S1.");
        box[0][0] = true;
        equal("HIT", ((java.lang.String) Reflect.call(g, "trace", box, "N1")), "A ball directly ahead of the ray must produce HIT before any side deflection.");
        equal("REFLECT", ((java.lang.String) Reflect.call(g, "trace", box, "N2")), "A diagonal ball beside the entry must make the N2 ray return REFLECT.");
        Reflect.call(g, "command", "mark 1 1");
        Reflect.call(g, "command", "mark 1 2");
        Reflect.call(g, "command", "check");
        expect(((java.lang.String) Reflect.get(g, "message")).contains("disagree"), "Checking an incorrect arrangement must report that its ray results disagree with the observations.");
        Reflect.call(g, "command", "ray N1");
        equal("Probe recorded", ((java.lang.String) Reflect.get(g, "message")), "A new ray probe must replace the old disagreement message with Probe recorded.");
        Reflect.call(g, "command", "mark 1 1");
        equal("Guess removed", ((java.lang.String) Reflect.get(g, "message")), "Removing a guess must report Guess removed rather than repeat an earlier event.");
    }
}

@SuppressWarnings("unchecked")
class P17_Task_AntHighwayTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P17_Task_AntHighway");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 2);
        Reflect.call(g, "step");
        equal(80, ((int) Reflect.get(g, "x")), "A westward step from column 0 must wrap to column 80, not move east.");
        equal(0, ((int) Reflect.get(g, "y")), "A horizontal ant step must preserve its row coordinate.");
        Reflect.call(g, "reset");
        Reflect.set(g, "x", 0);
        Reflect.set(g, "y", 0);
        Reflect.set(g, "direction", 1);
        ((boolean[][]) Reflect.get(g, "black"))[0][0] = true;
        Reflect.call(g, "step");
        equal(60, ((int) Reflect.get(g, "y")), "A northward step from row 0 must wrap to row 60.");
        expect(!((boolean[][]) Reflect.get(g, "black"))[0][0] && ((long) Reflect.get(g, "steps")) == 1, "One step must flip the old cell once and increase steps by exactly one.");
    }
}

@SuppressWarnings("unchecked")
class P20_Task_QuartoTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P20_Task_Quarto");
        Reflect.call(g, "reset");
        int[] diagonal = {0, 5, 10, 15};
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[diagonal[n]] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "won must recognise a completed main diagonal whose pieces share a property.");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[1 + n * 4] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "won must recognise a completed column, not only horizontal rows.");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[n] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "The existing horizontal winning row must still satisfy won.");
        Reflect.call(g, "reset");
        expect(!((boolean) Reflect.call(g, "won")), "An empty board must not satisfy won; empty squares cannot form a winning line.");
    }
}

@SuppressWarnings("unchecked")
class P21_Task_ReversiTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P21_Task_Reversi");
        Reflect.call(game, "reset");
        equal(2, Reflect.call(game, "score", 1), "score must give each player 2 points for their two ordinary opening discs.");
        int[][] board = new int[6][6];
        board[0][0] = 1;
        board[2][2] = board[2][3] = 2;
        Reflect.set(game, "board", board);
        equal(3, Reflect.call(game, "score", 1), "score must give player 1 three points for the single owned corner.");
        equal(2, Reflect.call(game, "score", 2), "score must give player 2 two points for two ordinary discs.");
        expect(!((boolean) Reflect.call(game, "hasMove", 1)) && !((boolean) Reflect.call(game, "hasMove", 2)), "On this final position neither player has a legal move. Preserve the existing capture rules while changing scoring.");
        Reflect.call(game, "command", "pass");
        Reflect.call(game, "command", "pass");
        equal("Player 1 wins", Reflect.get(game, "outcome"), "After both players pass, player 1's corner must beat player 2's two ordinary discs. Use the weighted score to decide the winner.");
        var output = new java.io.ByteArrayOutputStream();
        var before = System.out;
        try (var capture = new java.io.PrintStream(output)) {
            System.setOut(capture);
            Reflect.call(game, "display");
        } finally {
            System.setOut(before);
        }
        expect(output.toString().contains("Score P1: 3  P2: 2"), "The live display must show Score P1: 3  P2: 2 for this position.");
        board = new int[6][6];
        board[0][0] = board[0][5] = board[5][0] = board[5][5] = 2;
        board[0][3] = board[4][5] = 1;
        Reflect.set(game, "board", board);
        int[][] snapshot = new int[6][];
        for (int row = 0; row < board.length; row++) snapshot[row] = board[row].clone();
        equal(12, Reflect.call(game, "score", 2), "Four corners owned by player 2 must contribute 12 points in total.");
        equal(2, Reflect.call(game, "score", 1), "Two non-corner discs on an edge are still worth one point each.");
        equal(snapshot, Reflect.get(game, "board"), "Calling score for either player must preserve every board cell.");
        equal(2, board[5][5], "Scoring must leave the existing player-2 disc at board[5][5] unchanged.");
        equal(0, board[3][3], "Scoring must leave board[3][3] empty rather than create a disc there.");
    }
}

@SuppressWarnings("unchecked")
class P22_Task_LightUpTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P22_Task_LightUp");
        Reflect.call(g, "reset");
        ((boolean[][]) Reflect.get(g, "bulbs"))[0][2] = true;
        equal(1, ((int) Reflect.call(g, "visible", 0, 0)), "visible(0, 0) must detect the bulb farther along the open corridor, not stop at the next square.");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[1][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 1, 0)), "visible must stop at a numbered wall; a bulb behind that wall is not visible.");
        Reflect.set(g, "bulbs", new boolean[3][3]);
        ((boolean[][]) Reflect.get(g, "bulbs"))[2][2] = true;
        equal(0, ((int) Reflect.call(g, "visible", 0, 0)), "visible must not count a bulb on a diagonal. Only straight corridors carry light.");
    }
}

@SuppressWarnings("unchecked")
class P25_Task_SlantTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P25_Task_Slant");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "undo");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[0][0], "Undo must restore the empty first cell after placing a slash. Snapshot rows must remain independent of the live board.");
        Reflect.call(g, "command", "1 1 /");
        Reflect.call(g, "command", "2 2 /");
        Reflect.call(g, "command", "undo");
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "Undoing the newest move must keep the older slash at cells[0][0].");
        equal('.', ((char[][]) Reflect.get(g, "cells"))[1][1], "Undo must remove the latest slash at cells[1][1], leaving that cell empty.");
        char[][] copy = ((char[][]) Reflect.call(g, "snapshot"));
        copy[0][0] = '.';
        equal('/', ((char[][]) Reflect.get(g, "cells"))[0][0], "Editing a returned snapshot must not change the current slash at cells[0][0].");
        Reflect.call(g, "reset");
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                Reflect.call(g, "command", r + " " + c + " /");
            }
        }
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "The prepared board of slashes must complete the puzzle and report its result.");
        Reflect.call(g, "command", "undo");
        expect(!((boolean) Reflect.call(g, "solved")) && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Undo after a winning move must restore the unfinished board and clear the completed-round message.");
        Reflect.call(g, "command", "3 3 /");
        expect(((boolean) Reflect.call(g, "solved")) && !((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "The move undone after victory must be playable again and complete the puzzle again.");
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
            Reflect.call(Reflect.create("P25_Task_Slant"), "main");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        String output = captured.toString(java.nio.charset.StandardCharsets.UTF_8);
        int last = output.lastIndexOf("Cell diagonals");
        expect(last >= 0 && output.substring(last).contains("3 [/, /, .]"), "The actual console must accept undo after completion and show the final slash removed.");
    }
}

@SuppressWarnings("unchecked")
class P26_Task_BinaryBalanceTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P26_Task_BinaryBalance");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 1, 0);
        equal(0, ((int[][]) Reflect.get(g, "givens"))[0][1], "Editing cells[0][1] must not change its originally empty entry in givens.");
        Reflect.call(g, "restart");
        equal(0, ((int[][]) Reflect.get(g, "cells"))[0][1], "Reset must restore cells[0][1] to its originally empty value 0.");
        int[][] copy = ((int[][]) Reflect.call(g, "copyBoard", (Object) ((int[][]) Reflect.get(g, "givens"))));
        copy[2][1] = 2;
        equal(0, ((int[][]) Reflect.get(g, "givens"))[2][1], "Changing a later copied row must not change givens[2][1]. Copying only the outer array is insufficient.");
        equal(1, copy[0][0], "copyBoard must preserve the original clue value at [0][0].");
    }
}

@SuppressWarnings("unchecked")
class P27_Task_RuleGardenTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P27_Task_RuleGarden");
        equal(java.util.List.of("...#...", "..#.#..", ".#...#."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 7, 3)), "grow with rule 90 must produce ...#..., then ..#.#.., then .#...#. Every new cell reads the same previous row.");
        boolean[] before = {false, true, false, false, false};
        boolean[] saved = before.clone();
        Reflect.call(g, "nextRow", before, 90);
        equal(saved, before, "nextRow must preserve every value in its input row.");
        equal(java.util.List.of(".#.", "#.#", "..."), ((java.util.List<java.lang.String>) Reflect.call(g, "grow", 90, 3, 3)), "At width 3, rule 90 must give .#., then #.#, then ...; cells outside the row stay off.");
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
            equal(reverseOracle, ((boolean[]) Reflect.call(g, "nextRow", row, rule)), "nextRow must give the same generation whether cells are calculated left-to-right or right-to-left.");
        }
    }
}

@SuppressWarnings("unchecked")
class P28_Task_ForestFireSandboxTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P28_Task_ForestFireSandbox");
        int[][] old = new int[40][60];
        old[20][10] = 2; old[20][9] = 1; old[20][11] = 1; old[20][12] = 1;
        old[19][10] = 1; old[21][10] = 1; old[21][11] = 1;
        int[][] snapshot = java.util.Arrays.stream(old).map(int[]::clone).toArray(int[][]::new);
        int[][] next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[20][11] == 2 && next[20][12] == 1, "nextForest must ignite the tree immediately east of the old fire, but not the next tree beyond it.");
        expect(next[20][9] == 1 && next[19][10] == 1 && next[21][10] == 1 && next[21][11] == 1, "The eastward wind must not ignite trees west, north, south or diagonally from the old fire.");
        equal(0, next[20][10], "The cell that was already burning must become empty in the next generation.");
        equal(snapshot, old, "Calculating the next forest must not overwrite any cell of the old forest.");
        Reflect.set(g, "cells", (Object) old); Reflect.call(g, "step");
        equal(next, Reflect.get(g, "cells"), "The real Step operation must display the forest produced by the new wind rule.");
        Reflect.call(g, "step");
        equal(2, ((int[][]) Reflect.get(g, "cells"))[20][12], "A second Step must advance fire one more cell east, using the result of the previous step.");
        old = new int[40][60]; old[0][59] = 2; old[0][0] = 1;
        next = (int[][]) Reflect.call(g, "nextForest", (Object) old);
        expect(next[0][0] == 1 && next[0][59] == 0, "Fire at the right border must not ignite the left border; the old border fire still burns out.");
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
