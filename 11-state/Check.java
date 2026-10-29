import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_Pig.java
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
        run("P03_Pig", "11.03 Pig · F07", () -> P03_PigTest.main(new String[0]));
        run("P04_MancalaKalah", "11.04 MancalaKalah · F14", () -> P04_MancalaKalahTest.main(new String[0]));
        run("P05_SlidingTiles", "11.05 SlidingTiles · F14", () -> P05_SlidingTilesTest.main(new String[0]));
        run("P06_MiniCrossword", "11.06 MiniCrossword · F14", () -> P06_MiniCrosswordTest.main(new String[0]));
        run("P09_ConnectFour", "11.09 ConnectFour · F14", () -> P09_ConnectFourTest.main(new String[0]));
        run("P12_MemoryPairs", "11.12 MemoryPairs · F16", () -> P12_MemoryPairsTest.main(new String[0]));
        run("P13_Sokoban", "11.13 Sokoban · F15", () -> P13_SokobanTest.main(new String[0]));
        run("P14_TrafficJamEscape", "11.14 TrafficJamEscape · F15", () -> P14_TrafficJamEscapeTest.main(new String[0]));
        run("P15_FallingBlocks", "11.15 FallingBlocks · F15", () -> P15_FallingBlocksTest.main(new String[0]));
        run("P16_GoFish", "11.16 GoFish · F25", () -> P16_GoFishTest.main(new String[0]));
        run("P19_ChaosGame", "11.19 ChaosGame · F27", () -> P19_ChaosGameTest.main(new String[0]));
        run("P22_VoronoiStainedGlass", "11.22 VoronoiStainedGlass · F22", () -> P22_VoronoiStainedGlassTest.main(new String[0]));
        run("P23_MandelbrotPostcards", "11.23 MandelbrotPostcards · F27", () -> P23_MandelbrotPostcardsTest.main(new String[0]));
        run("P24_ColourFilterSecrets", "11.24 ColourFilterSecrets · F27", () -> P24_ColourFilterSecretsTest.main(new String[0]));
        run("P25_TwoNoisyPictures", "11.25 TwoNoisyPictures · F27", () -> P25_TwoNoisyPicturesTest.main(new String[0]));
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
class P03_PigTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("11.03 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Pig");
        Reflect.call(game, "reset");
        Reflect.set(game, "turnTotal", 9);
        Reflect.call(game, "roll", 1);
        check(((int) Reflect.get(game, "turnTotal")) == 0 && ((int) Reflect.get(game, "player")) == 1, "rolling one must lose the turn and pass the die");
        Reflect.call(game, "reset");
        Reflect.call(game, "roll", 6);
        check(((int) Reflect.get(game, "turnTotal")) == 6 && ((int) Reflect.get(game, "player")) == 0, "six adds points and keeps the turn");
        check(!((boolean) Reflect.call(game, "roll", 0)) && ((int) Reflect.get(game, "turnTotal")) == 6, "the supplied invalid-die guard remains intact");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_MancalaKalahTest {
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
        var g = Reflect.create("P04_MancalaKalah");
        Reflect.call(g, "reset");
        Reflect.set(g, "player", 1);
        ((int[]) Reflect.get(g, "pits"))[6] = 20;
        Reflect.call(g, "reset");
        equal(0, ((int) Reflect.get(g, "player")), "11.04/F14: restart resets the shared active-player field");
        equal(0, ((int[]) Reflect.get(g, "pits"))[6], "11.04/F14: stores belong to the old round");
        equal(24, ((int) Reflect.call(g, "side", 0)), "11.04/F14: six pits regain four stones");
        Reflect.call(g, "command", "3");
        equal(0, ((int) Reflect.get(g, "player")), "11.04/F14: finishing in own store grants another turn");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P05_SlidingTilesTest {
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
        var g = Reflect.create("P05_SlidingTiles");
        Reflect.set(g, "moves", 17);
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "11.05/F14: new puzzle clears previous move counter");
        int[] sorted = ((int[]) Reflect.get(g, "tiles")).clone();
        java.util.Arrays.sort(sorted);
        equal(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, sorted, "11.05/F14: shuffle keeps all tile values");
        int chosen = -1;
        for (int n = 0; n < 9; n++) {
            if (((boolean) Reflect.call(g, "canMove", n))) {
                chosen = n;
                break;
            }
        }
        expect(((boolean) Reflect.call(g, "play", chosen)) && ((int) Reflect.get(g, "moves")) == 1, "11.05/F14: first slide is move one");
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "11.05/F14: repeated restart remains independent");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P06_MiniCrosswordTest {
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
        var g = Reflect.create("P06_MiniCrossword");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "2 ARE");
        Reflect.call(g, "command", "3 TEN");
        expect(((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "11.06/F14: recount is local to each command, not accumulated across calls");
        Reflect.call(g, "command", "4 CAT");
        Reflect.call(g, "command", "5 ARE");
        Reflect.call(g, "command", "6 TEN");
        expect(!((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "11.06/F14: all six clues finish crossword");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "erase 1");
        expect(!((boolean[]) Reflect.get(g, "solved"))[0] && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "11.06/F14: erasing updates the next recount");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P09_ConnectFourTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("11.09/F14: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_ConnectFour");
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
class P12_MemoryPairsTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("11.12/F16: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_MemoryPairs");
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
class P13_SokobanTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("11.13/F15: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P13_Sokoban");
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
class P14_TrafficJamEscapeTest {
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
        var g = Reflect.create("P14_TrafficJamEscape");
        Reflect.call(g, "reset");
        int[] oldX = ((int[]) Reflect.get(g, "xs")).clone(), oldY = ((int[]) Reflect.get(g, "ys")).clone();
        expect(!((boolean) Reflect.call(g, "fits", 0, 1, 2)), "11.14/F15: blocker detected");
        equal(oldX, ((int[]) Reflect.get(g, "xs")), "11.14/F15: failed placement probe must not move the car");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "11.14/F15: query preserves every y position");
        expect(((boolean) Reflect.call(g, "fits", 1, 2, 3)), "11.14/F15: legal downward position accepted");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "11.14/F15: successful query is pure too");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_FallingBlocksTest {
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
        var g = Reflect.create("P15_FallingBlocks");
        Reflect.call(g, "restart");
        int oldX = ((int) Reflect.get(g, "px")), oldY = ((int) Reflect.get(g, "py"));
        expect(((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "11.15/F15: free trial position fits");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "11.15/F15: fit query must not teleport the falling piece");
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), -1, 0)), "11.15/F15: out-of-bounds trial rejected");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "11.15/F15: rejected query is also pure");
        ((int[][]) Reflect.get(g, "board"))[5][6] = 1;
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "11.15/F15: occupied cells block placement");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_GoFishTest {
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
        var g = Reflect.create("P16_GoFish");
        Reflect.set(g, "hands", java.util.List.of(new java.util.ArrayList<>(java.util.List.of(7, 7, 7, 7)), new java.util.ArrayList<>()));
        Reflect.set(g, "books", new int[2]);
        Reflect.call(g, "collect", 0);
        equal(0, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size(), "11.16/F25: booking four cards removes exactly four");
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "11.16/F25: one complete rank awards one book");
        equal(4, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size() + 4 * ((int[]) Reflect.get(g, "books"))[0], "11.16/F25: card-plus-book value conserved");
        Reflect.call(g, "collect", 0);
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "11.16/F25: collecting again cannot duplicate the book");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P19_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "Všechny tři platné vrcholy jsou dosažitelné.",
        "Pevný seed (počáteční hodnota) zopakuje sequence (posloupnost) indexů.",
        "Jediná možnost dá nulu a prázdný rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 11.19: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P19_ChaosGame");
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(((int) Reflect.call(p, "chooseVertex", random, 3)));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(((int) Reflect.call(p, "chooseVertex", a, 3))==((int) Reflect.call(p, "chooseVertex", b, 3)),1,n);
            check(((int) Reflect.call(p, "chooseVertex", new Random(42), 1))==0,2,"single");reject(()->Reflect.call(p, "chooseVertex", new Random(42), 0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 11.19: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P22_VoronoiStainedGlassTest {
    static int checks;
    static final String[] CASES = {
        "Tři zárodky přibudou v platných coordinate (souřadnicích).",
        "Vrácený List (seznam) umožní odstranění.",
        "Nula dá prázdný výsledek a záporný počet se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 11.22: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P22_VoronoiStainedGlass");
            var seeds=((java.util.List<Object>) Reflect.call(p, "seedList", new Random(42), 3));
            check(seeds.size()==3,0,seeds.size());
            var first=seeds.getFirst();check(((int) Reflect.call(first, "x"))>=0&&((int) Reflect.call(first, "x"))<400&&((int) Reflect.call(first, "y"))>=0&&((int) Reflect.call(first, "y"))<300,0,first);
            seeds.removeLast();check(seeds.size()==2,1,seeds.size());
            check(((java.util.List<Object>) Reflect.call(p, "seedList", new Random(42), 0)).isEmpty(),2,"empty");
            reject(()->Reflect.call(p, "seedList", new Random(42), -1),2);
        var expected = new ArrayList<>();
        var sameRandom = new Random(43);
        for (int n = 0; n < 3; n++) expected.add(Reflect.call(p, "nextSeed", sameRandom));
        check(expected.equals(Reflect.call(p, "seedList", new Random(43), 3)), 0, "successive seeds in generation order");
        check(((List<?>) Reflect.call(p, "seedList", new Random(42), 100)).size() == 100, 2, "upper bound");
        reject(() -> Reflect.call(p, "seedList", new Random(42), 101), 2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 11.22: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P23_MandelbrotPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Nástroj najde všechny odpovídající seed ve včetně zahrnutém rozsahu 0..255.",
        "Prefix (začátek), žádná shoda i zachování vstupu fungují.",
        "Běžná paleta i fraktál dál zachovají opakovatelnost."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 11.23: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        var p = Reflect.create("P23_MandelbrotPostcards");
        int[] palette = (int[]) Reflect.call(p, "palette", 7L);
        int[] sample = Arrays.copyOf(palette, 4);
        int[] before = sample.clone();
        var found = (java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) sample);
        check(found.equals(java.util.List.of(7L)), 0, found);
        check(Arrays.equals(sample, before), 1, "sample unchanged");
        var edge = (java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) (int[]) Reflect.call(p, "palette", 255L));
        check(edge.equals(java.util.List.of(255L)), 0, edge);
        var expected = new java.util.ArrayList<Long>();
        for (long seed = 0; seed <= 255; seed++) {
            var random = new Random(seed);
            int colour = java.awt.Color.HSBtoRGB(random.nextInt(360) / 360.0F, .8F, 1);
            if (colour == palette[0]) expected.add(seed);
        }
        check(Reflect.call(p, "matchingSeeds", (Object) new int[]{palette[0]}).equals(expected), 1, "all prefix candidates");
        check(((java.util.List<?>) Reflect.call(p, "matchingSeeds", (Object) new int[]{0})).isEmpty(), 1, "impossible colour");
        reject(() -> Reflect.call(p, "matchingSeeds", (Object) new int[0]), 1);
        reject(() -> Reflect.call(p, "matchingSeeds", (Object) new int[17]), 1);
        var random = new Random(7);
        for (int colour : palette) check(colour == java.awt.Color.HSBtoRGB(random.nextInt(360) / 360.0F, .8F, 1), 2, "unchanged palette recipe");
        var a = (BufferedImage) Reflect.call(p, "render", 42L, 3.2);
        check(hash(a) == hash((BufferedImage) Reflect.call(p, "render", 42L, 3.2)), 2, "repeatable picture");
        check((int) Reflect.call(p, "escape", 0.0, 0.0) == 160, 2, "fractal geometry");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P24_ColourFilterSecretsTest {
    static int checks;
    static final String[] CASES = {
        "Změna seed (počáteční hodnoty) změní maskování.",
        "Stejný seed zopakuje všechny skryté pixel (body obrazu).",
        "Odhalená zpráva nezávisí na seed šumu."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 11.24: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P24_ColourFilterSecrets");
            var mask=((java.awt.image.BufferedImage) Reflect.call(p, "mask", "HI"));var a=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42));var b=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 43));check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42))),1,hash(a));check(hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a)))==hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", b))),2,hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 11.24: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P25_TwoNoisyPicturesTest {
    static int checks;
    static final String[] CASES = {
        "Sousední prázdné dlaždice mají různé vzory teček.",
        "Stejné seed (počáteční hodnoty) opakují a jiné mění první část.",
        "Každá dlaždice části má dál přesně dvě černé tečky."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 11.25: " + CASES[example] + " | actual=" + actual);
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "accepted");
    }
    static long hash(BufferedImage image) {
        long result = 1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) result = result * 31 + image.getRGB(x, y);
        }
        return result;
    }
    static BufferedImage draw(java.util.function.Consumer<java.awt.Graphics2D> painter, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        try { painter.accept(graphics); } finally { graphics.dispose(); }
        return image;
    }
    public static void main(String[] args) throws Exception {
        try {
            var p = Reflect.create("P25_TwoNoisyPictures");
            var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42));var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
            check(hash(a[0])==hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42))[0]),1,hash(a[0]));check(hash(a[0])!=hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 43))[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 11.25: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
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
