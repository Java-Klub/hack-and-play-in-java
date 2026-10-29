import module java.base;


/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_MemoryPairs.java`
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
        run("M01_MemoryPairs", "L05.01 MemoryPairs · F16", () -> M01_MemoryPairsTest.main(new String[0]));
        run("M02_MiniBattleshipHunt", "L05.02 MiniBattleshipHunt · F16", () -> M02_MiniBattleshipHuntTest.main(new String[0]));
        run("M03_DotsAndBoxes", "L05.03 DotsAndBoxes · F16", () -> M03_DotsAndBoxesTest.main(new String[0]));
        run("M04_Quarto", "L05.04 Quarto · F16", () -> M04_QuartoTest.main(new String[0]));
        run("M05_Reversi", "L05.05 Reversi · F17", () -> M05_ReversiTest.main(new String[0]));
        run("M06_Hex", "L05.06 Hex · F17", () -> M06_HexTest.main(new String[0]));
        run("M07_SetStyleTripleHunt", "L05.07 SetStyleTripleHunt · F17", () -> M07_SetStyleTripleHuntTest.main(new String[0]));
        run("M08_Nonogram", "L05.08 Nonogram · F17", () -> M08_NonogramTest.main(new String[0]));
        run("M09_FourColourMap", "L05.09 FourColourMap · F17", () -> M09_FourColourMapTest.main(new String[0]));
        run("M10_MirrorLaser", "L05.10 MirrorLaser · F19", () -> M10_MirrorLaserTest.main(new String[0]));
        run("M11_Dominosa", "L05.11 Dominosa · F19", () -> M11_DominosaTest.main(new String[0]));
        run("M12_InequalityLatin", "L05.12 InequalityLatin · F19", () -> M12_InequalityLatinTest.main(new String[0]));
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
class M01_MemoryPairsTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L05.01/F16: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M01_MemoryPairs");
        Reflect.call(game, "reset", 12L);
        int[] cards = {4, 2, 6, 3, 1, 5, 3, 6, 2, 5, 4, 1};
        Reflect.set(game, "cards", cards.clone());
        Reflect.set(game, "xray", true);
        equal(0, Reflect.call(game, "visible", 0), "x-ray without a selected card must not expose the deck");
        Reflect.call(game, "play", 4);
        equal(false, Reflect.get(game, "xray"), "a valid play clears the extra view");
        Reflect.set(game, "xray", true);
        equal(1, Reflect.call(game, "visible", 11), "the selected card's partner is revealed");
        equal(0, Reflect.call(game, "visible", 0), "unrelated cards stay private");
        equal(1, Reflect.call(game, "visible", 4), "the selected card remains visible");
        Reflect.set(game, "first", 2);
        equal(6, Reflect.call(game, "visible", 7), "a different selected value locates a different partner");
        equal(0, Reflect.call(game, "visible", 11), "the previous value receives no special privilege");
        equal(cards, Reflect.get(game, "cards"), "view queries never rearrange cards");
        equal(0, Reflect.get(game, "attempts"), "view queries spend no attempts");
        equal(0, Reflect.get(game, "pairs"), "view queries award no pairs");
        equal(new boolean[12], Reflect.get(game, "matched"), "view queries never mark hidden cards as matched");
        equal(2, Reflect.get(game, "first"), "queries preserve the selected position");
        expect(!((boolean) Reflect.call(game, "play", -1)), "an invalid card is rejected");
        equal(true, Reflect.get(game, "xray"), "a rejected click does not turn the view off");
        Reflect.set(game, "xray", false);
        equal(0, Reflect.call(game, "visible", 7), "turning x-ray off hides its extra reveal");
        ((boolean[]) Reflect.get(game, "matched"))[0] = true;
        equal(4, Reflect.call(game, "visible", 0), "matched cards remain visible without x-ray");
        Reflect.set(game, "second", 8);
        equal(2, Reflect.call(game, "visible", 8), "ordinary second selection remains visible");
        Reflect.call(game, "reset", 99L);
        equal(false, Reflect.get(game, "xray"), "new cards do not retain a previous x-ray");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M02_MiniBattleshipHunt");
        for (int layout = 0; layout < 3; layout++) {
            Reflect.call(g, "layout", layout);
            int[] counts = new int[3];
            for (var row : ((int[][]) Reflect.get(g, "board"))) {
                for (int cell : row) {
                    counts[cell]++;
                }
            }
            equal(3, counts[2], "L05.02/F16: every layout has all three cells of ship two");
            equal(2, counts[1], "L05.02/F16: ship one occupies two cells");
        }
        Reflect.call(g, "layout", 0);
        equal(2, ((int[][]) Reflect.get(g, "board"))[3][4], "L05.02/F16: last coordinate is not omitted");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M03_DotsAndBoxes");
        equal(new int[]{0, 2, 6, 7}, ((int[]) Reflect.call(g, "box", 0)), "L05.03/F16: first box needs its four real edges");
        equal(new int[]{1, 3, 7, 8}, ((int[]) Reflect.call(g, "box", 1)), "L05.03/F16: neighbouring box shares one edge");
        equal(new int[]{3, 5, 10, 11}, ((int[]) Reflect.call(g, "box", 3)), "L05.03/F16: final box has different indices");
        Reflect.call(g, "reset");
        for (String edge : new String[]{"1", "3", "7", "8"}) {
            Reflect.call(g, "command", edge);
        }
        expect(((int[]) Reflect.get(g, "owners"))[0] != 0, "L05.03/F16: closing a real box awards it");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M04_Quarto");
        Reflect.call(g, "reset");
        int[] diagonal = {0, 5, 10, 15};
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[diagonal[n]] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "L05.04/F16: the main diagonal must be searched");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[1 + n * 4] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "L05.04/F16: a full column can win");
        Reflect.call(g, "reset");
        for (int n = 0; n < 4; n++) {
            ((int[]) Reflect.get(g, "board"))[n] = n;
        }
        expect(((boolean) Reflect.call(g, "won")), "L05.04/F16: existing row wins remain");
        Reflect.call(g, "reset");
        expect(!((boolean) Reflect.call(g, "won")), "L05.04/F16: empty slots cannot form a line");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_ReversiTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L05.05/F17: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M05_Reversi");
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
        var g = Reflect.create("M06_Hex");
        Reflect.call(g, "reset");
        for (int r = 0; r < 5; r++) {
            ((int[][]) Reflect.get(g, "board"))[r][4 - r] = 1;
        }
        expect(((boolean) Reflect.call(g, "connected", 1)), "L05.06/F17: hex diagonal is a connected path");
        ((int[][]) Reflect.get(g, "board"))[2][2] = 0;
        expect(!((boolean) Reflect.call(g, "connected", 1)), "L05.06/F17: gap breaks the path");
        Reflect.call(g, "reset");
        for (int c = 0; c < 5; c++) {
            ((int[][]) Reflect.get(g, "board"))[2][c] = 2;
        }
        expect(((boolean) Reflect.call(g, "connected", 2)), "L05.06/F17: player two connects left to right");
        expect(!((boolean) Reflect.call(g, "connected", 1)), "L05.06/F17: opposing pieces are not traversed");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M07_SetStyleTripleHunt");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 2)));
        expect(((boolean) Reflect.call(g, "hasSet")), "L05.07/F17: the last card can complete the only triple");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 3)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "L05.07/F17: near miss is not a triple");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "L05.07/F17: fewer than three cards cannot work");
        expect(((boolean) Reflect.call(g, "triple", 0, 1, 2)), "L05.07/F17: supplied attribute rule still used");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M08_Nonogram");
        equal(new int[]{3}, ((int[]) Reflect.call(g, "runs", new int[]{0, 1, 1, 1})), "L05.08/F17: flush the run at the end of the array");
        equal(new int[]{2, 1}, ((int[]) Reflect.call(g, "runs", new int[]{1, 1, 0, 1})), "L05.08/F17: separate groups remain separate");
        equal(new int[]{}, ((int[]) Reflect.call(g, "runs", new int[]{0, 0, 0})), "L05.08/F17: blank line has no runs");
        equal(new int[]{1}, ((int[]) Reflect.call(g, "runs", new int[]{1})), "L05.08/F17: single-cell run");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_FourColourMapTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L05.09/F17: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M09_FourColourMap");
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
        var g = Reflect.create("M10_MirrorLaser");
        var r = Reflect.create("M10_MirrorLaser$RayState", 1, 2, 1, 0);
        var next = Reflect.call(r, "advance");
        equal(Reflect.create("M10_MirrorLaser$RayState", 2, 2, 1, 0), next, "L05.10/F19: record update follows its named direction");
        equal(1, ((int) Reflect.call(r, "x")), "L05.10/F19: advancing leaves original record unchanged");
        equal(Reflect.create("M10_MirrorLaser$RayState", 1, 1, 0, -1), Reflect.call(Reflect.create("M10_MirrorLaser$RayState", 1, 2, 0, -1), "advance"), "L05.10/F19: vertical update");
        Reflect.call(g, "restart");
        ((int[][]) Reflect.get(g, "mirrors"))[2][2] = 1;
        ((int[][]) Reflect.get(g, "mirrors"))[0][2] = 1;
        ((int[][]) Reflect.get(g, "mirrors"))[0][5] = 2;
        Reflect.call(g, "trace");
        equal("Target reached!", ((java.lang.String) Reflect.get(g, "result")), "L05.10/F19: real beam follows the authored route");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M11_Dominosa");
        Reflect.call(g, "reset");
        equal(Reflect.call(g, "pairKey", 2, 3), Reflect.call(g, "pairKey", 3, 2), "L05.11/F19: reversed endpoints describe the same domino value");
        equal(Reflect.create("M11_Dominosa$Domino", 0, 1), Reflect.call(g, "pairKey", 2, 3), "L05.11/F19: record stores ordered endpoint values");
        expect(!((boolean) Reflect.call(Reflect.call(g, "pairKey", 0, 1), "equals", Reflect.call(g, "pairKey", 2, 3))), "L05.11/F19: different unordered pairs stay distinct");
        Reflect.call(g, "command", "1 3 1 4");
        expect(((java.util.Set<Object>) Reflect.get(g, "used")).contains(Reflect.create("M11_Dominosa$Domino", 0, 1)), "L05.11/F19: game tracks the record value");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M12_InequalityLatin");
        var rule = Reflect.create("M12_InequalityLatin$Rule", 1, 0);
        int[][] values = {{4, 1, 3, 2}};
        expect(((boolean) Reflect.call(rule, "satisfiedBy", (Object) values)), "L05.12/F19: record coordinates identify the left and right cells");
        values[0][1] = 3;
        expect(!((boolean) Reflect.call(rule, "satisfiedBy", (Object) values)), "L05.12/F19: equality does not satisfy strict inequality");
        int[][] other = {{4, 1, 3, 2}, {1, 4, 2, 3}};
        expect(!((boolean) Reflect.call(Reflect.create("M12_InequalityLatin$Rule", 0, 0), "satisfiedBy", (Object) other)), "L05.12/F19: changed x reads a different pair");
        expect(((boolean) Reflect.call(Reflect.create("M12_InequalityLatin$Rule", 0, 1), "satisfiedBy", (Object) other)), "L05.12/F19: changed y reads a different row");
        expect(!((boolean) Reflect.call(Reflect.create("M12_InequalityLatin$Rule", 1, 1), "satisfiedBy", (Object) other)), "L05.12/F19: record row and column cannot be ignored");
        expect(((boolean) Reflect.call(Reflect.create("M12_InequalityLatin$Rule", 2, 1), "satisfiedBy", (Object) other)), "L05.12/F19: rightmost pair can satisfy the rule");
        Reflect.call(g, "restart");
        Reflect.set(g, "cells", new int[][]{{1, 2, 3, 4}, {2, 3, 4, 1}, {3, 4, 1, 2}, {4, 1, 2, 3}});
        expect(((boolean) Reflect.call(g, "won")), "L05.12/F19: authored Latin square obeys all Rule records");
        Reflect.set(g, "rules", ((Object[]) Reflect.array("M12_InequalityLatin$Rule", Reflect.create("M12_InequalityLatin$Rule", 0, 3))));
        expect(!((boolean) Reflect.call(g, "won")), "L05.12/F19: a valid Latin square can violate its actual Rule coordinates");
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
