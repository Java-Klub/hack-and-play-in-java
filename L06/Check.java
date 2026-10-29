import module java.base;


/// All level checks: `java Check.java`
/// One game: `java Check.java M01_ConnectFour.java`
/// Checks call the test methods directly in this JVM.
/// Games remain compact sources; the checks below access them through reflection.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_ConnectFour", "L06.01 ConnectFour · F14", () -> M01_ConnectFourTest.main(new String[0]));
        run("M02_MancalaKalah", "L06.02 MancalaKalah · F14", () -> M02_MancalaKalahTest.main(new String[0]));
        run("M03_SlidingTiles", "L06.03 SlidingTiles · F14", () -> M03_SlidingTilesTest.main(new String[0]));
        run("M04_MiniCrossword", "L06.04 MiniCrossword · F14", () -> M04_MiniCrosswordTest.main(new String[0]));
        run("M05_Sokoban", "L06.05 Sokoban · F15", () -> M05_SokobanTest.main(new String[0]));
        run("M06_PegDrop", "L06.06 PegDrop · F15", () -> M06_PegDropTest.main(new String[0]));
        run("M07_TrafficJamEscape", "L06.07 TrafficJamEscape · F15", () -> M07_TrafficJamEscapeTest.main(new String[0]));
        run("M08_FallingBlocks", "L06.08 FallingBlocks · F15", () -> M08_FallingBlocksTest.main(new String[0]));
        run("M09_LightsOut", "L06.09 LightsOut · F33", () -> M09_LightsOutTest.main(new String[0]));
        run("M10_Tents", "L06.10 Tents · F33", () -> M10_TentsTest.main(new String[0]));
        run("M11_PegSolitaire", "L06.11 PegSolitaire · F33", () -> M11_PegSolitaireTest.main(new String[0]));
        run("M12_Merge2048", "L06.12 Merge2048 · F33", () -> M12_Merge2048Test.main(new String[0]));
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
class M01_ConnectFourTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L06.01/F14: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M01_ConnectFour");
        char expectedOpener = 'X';
        for (int length : new int[]{0, 1, 4, 2, 0}) {
            Reflect.call(game, "reset");
            equal(expectedOpener, Reflect.get(game, "player"), "opener alternates regardless of previous round length");
            equal(0, Reflect.get(game, "moves"), "a rematch resets move count");
            equal('.', Reflect.get(game, "winner"), "a rematch clears any winner");
            for (char[] row : (char[][]) Reflect.get(game, "board")) {
                for (char cell : row) equal('.', cell, "a rematch clears every old disc");
            }
            for (int n = 0; n < length; n++) {
                expect((boolean) Reflect.call(game, "drop", n + 1), "ordinary valid drops remain playable");
            }
            expectedOpener = expectedOpener == 'X' ? 'O' : 'X';
        }
        Reflect.call(game, "reset");
        equal(expectedOpener, Reflect.get(game, "player"), "next round keeps the opener schedule");
        for (int column : new int[]{1, 2, 1, 2, 1, 2, 1}) Reflect.call(game, "drop", column);
        equal(expectedOpener, Reflect.get(game, "winner"), "normal four-in-a-row still wins");
        Reflect.call(game, "reset");
        char next = expectedOpener == 'X' ? 'O' : 'X';
        equal(next, Reflect.get(game, "player"), "a completed win does not break alternation");
        expect(!((boolean) Reflect.call(game, "drop", 0)), "invalid input is rejected");
        equal(next, Reflect.get(game, "player"), "invalid input does not advance any turn");
        Reflect.call(game, "drop", 7);
        equal(next, ((char[][]) Reflect.get(game, "board"))[5][6], "the first real disc belongs to the scheduled opener");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M02_MancalaKalah");
        Reflect.call(g, "reset");
        Reflect.set(g, "player", 1);
        ((int[]) Reflect.get(g, "pits"))[6] = 20;
        Reflect.call(g, "reset");
        equal(0, ((int) Reflect.get(g, "player")), "L06.02/F14: restart resets the shared active-player field");
        equal(0, ((int[]) Reflect.get(g, "pits"))[6], "L06.02/F14: stores belong to the old round");
        equal(24, ((int) Reflect.call(g, "side", 0)), "L06.02/F14: six pits regain four stones");
        Reflect.call(g, "command", "3");
        equal(0, ((int) Reflect.get(g, "player")), "L06.02/F14: finishing in own store grants another turn");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M03_SlidingTiles");
        Reflect.set(g, "moves", 17);
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "L06.03/F14: new puzzle clears previous move counter");
        int[] sorted = ((int[]) Reflect.get(g, "tiles")).clone();
        java.util.Arrays.sort(sorted);
        equal(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, sorted, "L06.03/F14: shuffle keeps all tile values");
        int chosen = -1;
        for (int n = 0; n < 9; n++) {
            if (((boolean) Reflect.call(g, "canMove", n))) {
                chosen = n;
                break;
            }
        }
        expect(((boolean) Reflect.call(g, "play", chosen)) && ((int) Reflect.get(g, "moves")) == 1, "L06.03/F14: first slide is move one");
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "L06.03/F14: repeated restart remains independent");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M04_MiniCrossword");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "2 ARE");
        Reflect.call(g, "command", "3 TEN");
        expect(((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "L06.04/F14: recount is local to each command, not accumulated across calls");
        Reflect.call(g, "command", "4 CAT");
        Reflect.call(g, "command", "5 ARE");
        Reflect.call(g, "command", "6 TEN");
        expect(!((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "L06.04/F14: all six clues finish crossword");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "erase 1");
        expect(!((boolean[]) Reflect.get(g, "solved"))[0] && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "L06.04/F14: erasing updates the next recount");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_SokobanTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L06.05/F15: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M05_Sokoban");
        Reflect.call(game, "reset");
        expect((boolean) Reflect.call(game, "canMove", 1, 0), "a crate with free floor beyond can be pushed");
        Reflect.call(game, "command", "d");
        Reflect.call(game, "command", "d");
        int x = (int) Reflect.get(game, "x");
        int y = (int) Reflect.get(game, "y");
        int bx = (int) Reflect.get(game, "bx");
        int by = (int) Reflect.get(game, "by");
        String outcome = (String) Reflect.get(game, "outcome");
        String[] terrain = ((String[]) Reflect.get(game, "terrain")).clone();
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "the radar rejects a crate pushed into the right wall");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "repeated queries are equally safe");
        expect((boolean) Reflect.call(game, "canMove", -1, 0), "walking away from the crate remains legal");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 1)), "diagonal candidates are not moves");
        expect(!((boolean) Reflect.call(game, "canMove", 0, 0)), "standing still is not a move");
        expect(!((boolean) Reflect.call(game, "canMove", 2, 0)), "a two-cell leap is not a move");
        equal(x, Reflect.get(game, "x"), "queries preserve player x");
        equal(y, Reflect.get(game, "y"), "queries preserve player y");
        equal(bx, Reflect.get(game, "bx"), "queries preserve crate x");
        equal(by, Reflect.get(game, "by"), "queries preserve crate y");
        equal(outcome, Reflect.get(game, "outcome"), "queries preserve the outcome");
        equal(terrain, Reflect.get(game, "terrain"), "queries preserve the map");
        Reflect.set(game, "x", 1);
        Reflect.set(game, "y", 1);
        expect(!((boolean) Reflect.call(game, "canMove", 0, -1)), "a wall blocks the radar");
        Reflect.set(game, "outcome", "finished");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "a finished round offers no move");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M06_PegDrop");
        Reflect.call(g, "restart");
        equal(100, ((int) Reflect.call(g, "scoreAt", 3)), "L06.06/F15: return the score of the requested bin");
        equal(10, ((int) Reflect.call(g, "scoreAt", 7)), "L06.06/F15: final bin accepted");
        int old = ((int) Reflect.get(g, "score"));
        Reflect.call(g, "scoreAt", 2);
        equal(old, ((int) Reflect.get(g, "score")), "L06.06/F15: score query does not award points");
        boolean rejected = false;
        try {
            Reflect.call(g, "scoreAt", -1);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        expect(rejected, "L06.06/F15: invalid index has an explicit contract");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M07_TrafficJamEscape");
        Reflect.call(g, "reset");
        int[] oldX = ((int[]) Reflect.get(g, "xs")).clone(), oldY = ((int[]) Reflect.get(g, "ys")).clone();
        expect(!((boolean) Reflect.call(g, "fits", 0, 1, 2)), "L06.07/F15: blocker detected");
        equal(oldX, ((int[]) Reflect.get(g, "xs")), "L06.07/F15: failed placement probe must not move the car");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "L06.07/F15: query preserves every y position");
        expect(((boolean) Reflect.call(g, "fits", 1, 2, 3)), "L06.07/F15: legal downward position accepted");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "L06.07/F15: successful query is pure too");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M08_FallingBlocks");
        Reflect.call(g, "restart");
        int oldX = ((int) Reflect.get(g, "px")), oldY = ((int) Reflect.get(g, "py"));
        expect(((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "L06.08/F15: free trial position fits");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "L06.08/F15: fit query must not teleport the falling piece");
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), -1, 0)), "L06.08/F15: out-of-bounds trial rejected");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "L06.08/F15: rejected query is also pure");
        ((int[][]) Reflect.get(g, "board"))[5][6] = 1;
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "L06.08/F15: occupied cells block placement");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_LightsOutTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("L06.09/F33: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    static int count(boolean[][] board) {
        int total = 0;
        for (boolean[] row : board) {
            for (boolean lit : row) if (lit) total++;
        }
        return total;
    }

    static boolean[][] counterfeit(Object sample, int kind) {
        int[][] offsets = switch (kind) {
            case 0 -> new int[][]{{0, 0}};
            case 1 -> new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            case 2 -> new int[][]{{0, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
            default -> new int[][]{{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        };
        boolean[][] board = new boolean[5][5];
        for (int click = 0; click < 2; click++) {
            if (kind == 5 && click == 1) continue;
            int row = (int) Reflect.call(sample, click == 0 ? "firstRow" : "secondRow");
            int column = (int) Reflect.call(sample, click == 0 ? "firstColumn" : "secondColumn");
            for (int[] offset : offsets) {
                int r = row + offset[0];
                int c = column + offset[1];
                if (kind == 3) {
                    r = Math.floorMod(r, 5);
                    c = Math.floorMod(c, 5);
                }
                if (r >= 0 && r < 5 && c >= 0 && c < 5) {
                    board[r][c] = kind == 4 || !board[r][c];
                }
            }
        }
        return board;
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M09_LightsOut");
        var cases = (Object[]) Reflect.call(game, "learnerChecks");
        expect(cases.length >= 1 && cases.length <= 2, "supply one or two concise two-click experiments");
        for (var sample : cases) {
            for (String coordinate : new String[]{"firstRow", "firstColumn", "secondRow", "secondColumn"}) {
                int value = (int) Reflect.call(sample, coordinate);
                expect(value >= 0 && value < 5, "experiment coordinates must name real cells 0..4");
            }
            int expected = (int) Reflect.call(sample, "expected");
            expect(expected >= 0 && expected <= 25, "expected count must fit the board");
            Reflect.set(game, "lights", new boolean[5][5]);
            Reflect.call(game, "flip", Reflect.call(sample, "firstRow"), Reflect.call(sample, "firstColumn"));
            Reflect.call(game, "flip", Reflect.call(sample, "secondRow"), Reflect.call(sample, "secondColumn"));
            equal(expected, count((boolean[][]) Reflect.get(game, "lights")), "your two-click prediction must accept the real rule");
        }
        String[] names = {"only the pressed cell changes", "the pressed cell is omitted", "diagonal neighbours replace side neighbours",
                "crosses wrap around board edges", "clicks set lights on instead of toggling", "the second click is ignored"};
        for (int mutant = 0; mutant < names.length; mutant++) {
            boolean caught = false;
            for (var sample : cases) {
                if (count(counterfeit(sample, mutant)) != (int) Reflect.call(sample, "expected")) caught = true;
            }
            expect(caught, "your experiments still accept this wrong rule: " + names[mutant]);
        }
        boolean[][] before = new boolean[5][5];
        before[1][3] = true;
        Reflect.set(game, "lights", before);
        Reflect.set(game, "moves", 17);
        Reflect.call(game, "verifyExamples");
        expect(before == Reflect.get(game, "lights") && before[1][3], "startup experiments restore the real board");
        equal(17, Reflect.get(game, "moves"), "startup experiments do not spend player moves");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M10_Tents");
        Reflect.call(g, "restart");
        var cases = ((Object[]) Reflect.call(g, "learnerChecks"));
        for (var e : cases) {
            equal(((boolean) Reflect.call(e, "expected")), ((boolean) Reflect.call(g, "treeAt", ((int) Reflect.call(e, "x")), ((int) Reflect.call(e, "y")))), "L06.10/F33: expectations must accept actual tree positions");
        }
        String[] names = {"only the first tree is recognised", "x and y are exchanged", "every cell is called a tree"};
        for (int mutant = 0; mutant < 3; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                boolean wrong = false;
                if (mutant == 0) {
                    wrong = ((int) Reflect.call(e, "x")) == ((java.awt.Point[]) Reflect.get(g, "trees"))[0].x && ((int) Reflect.call(e, "y")) == ((java.awt.Point[]) Reflect.get(g, "trees"))[0].y;
                }
                if (mutant == 1) {
                    for (var tree : ((java.awt.Point[]) Reflect.get(g, "trees"))) {
                        if (((int) Reflect.call(e, "x")) == tree.y && ((int) Reflect.call(e, "y")) == tree.x) {
                            wrong = true;
                        }
                    }
                }
                if (mutant == 2) {
                    wrong = true;
                }
                if (wrong != ((boolean) Reflect.call(e, "expected"))) {
                    rejected = true;
                }
            }
            expect(rejected, "L06.10/F33: your examples still accept this wrong rule: " + names[mutant]);
        }
        Reflect.call(g, "verifyExamples");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M11_PegSolitaire");
        Reflect.call(g, "reset");
        var cases = ((Object[]) Reflect.call(g, "learnerChecks"));
        for (var e : cases) {
            equal(((boolean) Reflect.call(e, "expected")), ((boolean) Reflect.call(g, "legal", ((int) Reflect.call(e, "row")), ((int) Reflect.call(e, "column")), ((int) Reflect.call(e, "toRow")), ((int) Reflect.call(e, "toColumn")))), "L06.11/F33: expectations must accept actual legal()");
        }
        String[] names = {"jumping over an empty middle cell is allowed", "a diagonal jump is allowed", "every proposed jump is accepted"};
        for (int mutant = 0; mutant < 3; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                int r = ((int) Reflect.call(e, "row")), c = ((int) Reflect.call(e, "column")), nr = ((int) Reflect.call(e, "toRow")), nc = ((int) Reflect.call(e, "toColumn"));
                boolean inside = r >= 0 && c >= 0 && nr >= 0 && nc >= 0 && r < 5 && c < 5 && nr < 5 && nc < 5;
                boolean base = inside && Math.abs(nr - r) + Math.abs(nc - c) == 2 && ((int[][]) Reflect.get(g, "board"))[r][c] == 1 && ((int[][]) Reflect.get(g, "board"))[nr][nc] == 0;
                boolean wrong = switch (mutant) {
                    case 0 -> base && (r == nr || c == nc);
                    case 1 -> base && ((int[][]) Reflect.get(g, "board"))[(r + nr) / 2][(c + nc) / 2] == 1;
                    default -> true;
                };
                if (wrong != ((boolean) Reflect.call(e, "expected"))) {
                    rejected = true;
                }
            }
            expect(rejected, "L06.11/F33: your examples still accept this wrong rule: " + names[mutant]);
        }
        Reflect.call(g, "verifyExamples");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
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
        var g = Reflect.create("M12_Merge2048");
        var cases = ((Object[]) Reflect.call(g, "learnerChecks"));
        for (var e : cases) {
            expect(((int[]) Reflect.call(e, "input")).length == 4 && ((int[]) Reflect.call(e, "expected")).length == 4 && ((int) Reflect.call(e, "score")) >= 0, "L06.12/F33: examples use four-cell rows and nonnegative scores");
            for (int value : ((int[]) Reflect.call(e, "input"))) {
                expect(value == 0 || value >= 2 && value <= 2048 && (value&(value - 1)) == 0, "L06.12/F33: example inputs must be legal 2048 tile values");
            }
            Reflect.set(g, "score", 0);
            equal(((int[]) Reflect.call(e, "expected")), ((int[]) Reflect.call(g, "merge", ((int[]) Reflect.call(e, "input")).clone())), "L06.12/F33: real merge output");
            equal(((int) Reflect.call(e, "score")), ((int) Reflect.get(g, "score")), "L06.12/F33: real merge score");
        }
        String[] names = {"equal neighbours never merge", "gaps prevent equal tiles from meeting", "a newly merged tile merges again in the same move", "the row changes but earned points disappear", "only the first independent pair merges"};
        for (int mutant = 0; mutant < 5; mutant++) {
            boolean rejected = false;
            for (var e : cases) {
                int[] values = mutant == 1 ? ((int[]) Reflect.call(e, "input")).clone() : java.util.Arrays.stream(((int[]) Reflect.call(e, "input"))).filter(n -> n != 0).toArray();
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
                if (!java.util.Arrays.equals(out, ((int[]) Reflect.call(e, "expected"))) || points != ((int) Reflect.call(e, "score"))) {
                    rejected = true;
                }
            }
            expect(rejected, "L06.12/F33: your examples still accept this wrong rule: " + names[mutant]);
        }
        Reflect.call(g, "verifyExamples");
        System.out.println("PASS " + checks + " checks");
    }
}

// Prepared access to compact programs. Tests keep ordinary assertions, arrays and JDK collections.
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
