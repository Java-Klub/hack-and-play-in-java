import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

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
        run("P03_Task_Pig", () -> P03_Task_PigTest.main(new String[0]));
        run("P04_Task_MancalaKalah", () -> P04_Task_MancalaKalahTest.main(new String[0]));
        run("P05_Task_SlidingTiles", () -> P05_Task_SlidingTilesTest.main(new String[0]));
        run("P06_Task_MiniCrossword", () -> P06_Task_MiniCrosswordTest.main(new String[0]));
        run("P09_Task_ConnectFour", () -> P09_Task_ConnectFourTest.main(new String[0]));
        run("P12_Task_MemoryPairs", () -> P12_Task_MemoryPairsTest.main(new String[0]));
        run("P13_Task_Sokoban", () -> P13_Task_SokobanTest.main(new String[0]));
        run("P14_Task_TrafficJamEscape", () -> P14_Task_TrafficJamEscapeTest.main(new String[0]));
        run("P15_Task_FallingBlocks", () -> P15_Task_FallingBlocksTest.main(new String[0]));
        run("P16_Task_GoFish", () -> P16_Task_GoFishTest.main(new String[0]));
        run("P19_Task_ChaosGame", () -> P19_Task_ChaosGameTest.main(new String[0]));
        run("P22_Task_VoronoiStainedGlass", () -> P22_Task_VoronoiStainedGlassTest.main(new String[0]));
        run("P23_Task_MandelbrotPostcards", () -> P23_Task_MandelbrotPostcardsTest.main(new String[0]));
        run("P24_Task_ColourFilterSecrets", () -> P24_Task_ColourFilterSecretsTest.main(new String[0]));
        run("P25_Task_TwoNoisyPictures", () -> P25_Task_TwoNoisyPicturesTest.main(new String[0]));
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
class P03_Task_PigTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Task_Pig");
        Reflect.call(game, "reset");
        Reflect.set(game, "turnTotal", 9);
        Reflect.call(game, "roll", 1);
        check(((int) Reflect.get(game, "turnTotal")) == 0 && ((int) Reflect.get(game, "player")) == 1, "roll(1) must discard the current turn total and pass the die to the other player.");
        Reflect.call(game, "reset");
        Reflect.call(game, "roll", 6);
        check(((int) Reflect.get(game, "turnTotal")) == 6 && ((int) Reflect.get(game, "player")) == 0, "roll(6) must add six to the turn total without passing play.");
        check(!((boolean) Reflect.call(game, "roll", 0)) && ((int) Reflect.get(game, "turnTotal")) == 6, "roll(0) must reject the invalid die without changing the accumulated six points.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_MancalaKalahTest {
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
        var g = Reflect.create("P04_Task_MancalaKalah");
        Reflect.call(g, "reset");
        Reflect.set(g, "player", 1);
        ((int[]) Reflect.get(g, "pits"))[6] = 20;
        Reflect.call(g, "reset");
        equal(0, ((int) Reflect.get(g, "player")), "reset must make player 1 start again, even if player 2 had the last turn.");
        equal(0, ((int[]) Reflect.get(g, "pits"))[6], "reset must empty the old scoring stores; their points belong to the previous round.");
        equal(24, ((int) Reflect.call(g, "side", 0)), "reset must restore four stones to each of player 1's six small pits, 24 in total.");
        Reflect.call(g, "command", "3");
        equal(0, ((int) Reflect.get(g, "player")), "A move ending in the player's own store must still grant that player another turn.");
    }
}

@SuppressWarnings("unchecked")
class P05_Task_SlidingTilesTest {
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
        var g = Reflect.create("P05_Task_SlidingTiles");
        Reflect.set(g, "moves", 17);
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "After creating and shuffling a new puzzle, reset must leave the player's move count at 0.");
        int[] sorted = ((int[]) Reflect.get(g, "tiles")).clone();
        java.util.Arrays.sort(sorted);
        equal(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, sorted, "Shuffling must preserve every tile value 0–8 exactly once, including the empty tile.");
        int chosen = -1;
        for (int n = 0; n < 9; n++) {
            if (((boolean) Reflect.call(g, "canMove", n))) {
                chosen = n;
                break;
            }
        }
        expect(((boolean) Reflect.call(g, "play", chosen)) && ((int) Reflect.get(g, "moves")) == 1, "The first legal slide after setup must increase the move count from 0 to 1.");
        Reflect.call(g, "reset", 42);
        equal(0, ((int) Reflect.get(g, "moves")), "Every new reset must clear the move count again, including moves made during its own shuffle.");
    }
}

@SuppressWarnings("unchecked")
class P06_Task_MiniCrosswordTest {
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
        var g = Reflect.create("P06_Task_MiniCrossword");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "2 ARE");
        Reflect.call(g, "command", "3 TEN");
        expect(((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Filling only three clues must not finish the six-clue crossword. command must recount current answers rather than accumulate old counts.");
        Reflect.call(g, "command", "4 CAT");
        Reflect.call(g, "command", "5 ARE");
        Reflect.call(g, "command", "6 TEN");
        expect(!((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Filling all six correct answers must finish the crossword.");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", "1 CAT");
        Reflect.call(g, "command", "erase 1");
        expect(!((boolean[]) Reflect.get(g, "solved"))[0] && ((java.lang.String) Reflect.get(g, "outcome")).isEmpty(), "Erasing the first answer must clear its solved flag and leave the crossword unfinished.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_ConnectFourTest {
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
        var game = Reflect.create("P09_Task_ConnectFour");
        char expectedOpener = 'X';
        for (int length : new int[]{0, 1, 4, 2, 0}) {
            Reflect.call(game, "reset");
            equal(expectedOpener, Reflect.get(game, "player"), "Round starters must alternate X, O, X, O regardless of how many moves the previous round lasted.");
            equal(0, Reflect.get(game, "moves"), "Starting a rematch must reset moves to 0.");
            equal('.', Reflect.get(game, "winner"), "Starting a rematch must clear the previous winner.");
            for (char[] row : (char[][]) Reflect.get(game, "board")) {
                for (char cell : row) equal('.', cell, "Starting a rematch must remove every disc left on the old board.");
            }
            for (int n = 0; n < length; n++) {
                expect((boolean) Reflect.call(game, "drop", n + 1), "Legal drops must remain accepted while the opening-player schedule is changed.");
            }
            expectedOpener = expectedOpener == 'X' ? 'O' : 'X';
        }
        Reflect.call(game, "reset");
        equal(expectedOpener, Reflect.get(game, "player"), "The next reset must follow the alternating starter schedule, not copy the player left on turn.");
        for (int column : new int[]{1, 2, 1, 2, 1, 2, 1}) Reflect.call(game, "drop", column);
        equal(expectedOpener, Reflect.get(game, "winner"), "The scheduled starter must still be able to win by forming four in a row.");
        Reflect.call(game, "reset");
        char next = expectedOpener == 'X' ? 'O' : 'X';
        equal(next, Reflect.get(game, "player"), "After a completed win, the rematch must start with the other scheduled opener.");
        expect(!((boolean) Reflect.call(game, "drop", 0)), "drop(0) must reject the invalid column. Player-facing columns start at 1.");
        equal(next, Reflect.get(game, "player"), "Rejecting column 0 must preserve the player whose turn is scheduled next.");
        Reflect.call(game, "drop", 7);
        equal(next, ((char[][]) Reflect.get(game, "board"))[5][6], "The first disc of the new round must belong to its scheduled opener, including in the last column.");
    }
}

@SuppressWarnings("unchecked")
class P12_Task_MemoryPairsTest {
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
        var game = Reflect.create("P12_Task_MemoryPairs");
        Reflect.call(game, "reset", 12L);
        int[] cards = {4, 2, 6, 3, 1, 5, 3, 6, 2, 5, 4, 1};
        Reflect.set(game, "cards", cards.clone());
        Reflect.set(game, "xray", true);
        equal(0, Reflect.call(game, "visible", 0), "With X-ray on but no selected card, visible must not expose unrelated hidden cards.");
        Reflect.call(game, "play", 4);
        equal(false, Reflect.get(game, "xray"), "An accepted card selection must turn off the previous extra X-ray view.");
        Reflect.set(game, "xray", true);
        equal(1, Reflect.call(game, "visible", 11), "With card index 4 selected, X-ray must reveal its matching partner at index 11.");
        equal(0, Reflect.call(game, "visible", 0), "X-ray must keep cards with a different value hidden.");
        equal(1, Reflect.call(game, "visible", 4), "The selected card itself must remain visible while X-ray shows its partner.");
        Reflect.set(game, "first", 2);
        equal(6, Reflect.call(game, "visible", 7), "Selecting the card with value 6 must reveal that value's partner, not a fixed favourite card.");
        equal(0, Reflect.call(game, "visible", 11), "After selecting a different value, the previous value's partner must be hidden again.");
        equal(cards, Reflect.get(game, "cards"), "Calling visible must preserve the order and values of all cards.");
        equal(0, Reflect.get(game, "attempts"), "Calling visible must not spend an attempt.");
        equal(0, Reflect.get(game, "pairs"), "Calling visible must not award a matched pair.");
        equal(new boolean[12], Reflect.get(game, "matched"), "Calling visible must not mark hidden cards as matched.");
        equal(2, Reflect.get(game, "first"), "Calling visible must preserve the first selected card's index.");
        expect(!((boolean) Reflect.call(game, "play", -1)), "play(-1) must reject the invalid card index.");
        equal(true, Reflect.get(game, "xray"), "A rejected card selection must preserve the current X-ray setting.");
        Reflect.set(game, "xray", false);
        equal(0, Reflect.call(game, "visible", 7), "Turning X-ray off must hide a partner shown only by that extra view.");
        ((boolean[]) Reflect.get(game, "matched"))[0] = true;
        equal(4, Reflect.call(game, "visible", 0), "Matched cards must remain visible even when X-ray is off.");
        Reflect.set(game, "second", 8);
        equal(2, Reflect.call(game, "visible", 8), "The ordinary second selected card must remain visible without X-ray.");
        Reflect.call(game, "reset", 99L);
        equal(false, Reflect.get(game, "xray"), "Starting a new deck must turn off the previous round's X-ray setting.");
    }
}

@SuppressWarnings("unchecked")
class P13_Task_SokobanTest {
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
        var game = Reflect.create("P13_Task_Sokoban");
        Reflect.call(game, "reset");
        expect((boolean) Reflect.call(game, "canMove", 1, 0), "canMove(1, 0) must accept pushing the crate right when the cell beyond it is free.");
        Reflect.call(game, "command", "d");
        Reflect.call(game, "command", "d");
        int x = (int) Reflect.get(game, "x");
        int y = (int) Reflect.get(game, "y");
        int bx = (int) Reflect.get(game, "bx");
        int by = (int) Reflect.get(game, "by");
        String outcome = (String) Reflect.get(game, "outcome");
        String[] terrain = ((String[]) Reflect.get(game, "terrain")).clone();
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "canMove(1, 0) must reject pushing the crate into the right wall. Check the cell beyond the crate.");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "Repeating the same blocked canMove query must still return false without changing the position.");
        expect((boolean) Reflect.call(game, "canMove", -1, 0), "canMove(-1, 0) must still allow walking left away from the crate when the cell is free.");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 1)), "canMove(1, 1) must reject a diagonal move; only one cardinal step is allowed.");
        expect(!((boolean) Reflect.call(game, "canMove", 0, 0)), "canMove(0, 0) must reject standing still as a proposed move.");
        expect(!((boolean) Reflect.call(game, "canMove", 2, 0)), "canMove(2, 0) must reject a two-cell leap.");
        equal(x, Reflect.get(game, "x"), "Asking canMove must preserve the player's x coordinate.");
        equal(y, Reflect.get(game, "y"), "Asking canMove must preserve the player's y coordinate.");
        equal(bx, Reflect.get(game, "bx"), "Asking canMove must preserve the crate's x coordinate.");
        equal(by, Reflect.get(game, "by"), "Asking canMove must preserve the crate's y coordinate.");
        equal(outcome, Reflect.get(game, "outcome"), "Asking canMove must preserve the round's result message.");
        equal(terrain, Reflect.get(game, "terrain"), "Asking canMove must preserve every terrain row.");
        Reflect.set(game, "x", 1);
        Reflect.set(game, "y", 1);
        expect(!((boolean) Reflect.call(game, "canMove", 0, -1)), "canMove(0, -1) must reject walking into the wall above the player.");
        Reflect.set(game, "outcome", "finished");
        expect(!((boolean) Reflect.call(game, "canMove", 1, 0)), "After the round is complete, canMove must reject further moves even if space is free.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_TrafficJamEscapeTest {
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
        var g = Reflect.create("P14_Task_TrafficJamEscape");
        Reflect.call(g, "reset");
        int[] oldX = ((int[]) Reflect.get(g, "xs")).clone(), oldY = ((int[]) Reflect.get(g, "ys")).clone();
        expect(!((boolean) Reflect.call(g, "fits", 0, 1, 2)), "fits(0, 1, 2) must reject the proposed position because another car blocks it.");
        equal(oldX, ((int[]) Reflect.get(g, "xs")), "A rejected fits query must preserve every car's x coordinate.");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "A fits query must preserve every car's y coordinate.");
        expect(((boolean) Reflect.call(g, "fits", 1, 2, 3)), "fits(1, 2, 3) must accept the supplied free downward position.");
        equal(oldY, ((int[]) Reflect.get(g, "ys")), "Even an accepted fits query must leave the cars at their original y coordinates.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_FallingBlocksTest {
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
        var g = Reflect.create("P15_Task_FallingBlocks");
        Reflect.call(g, "restart");
        int oldX = ((int) Reflect.get(g, "px")), oldY = ((int) Reflect.get(g, "py"));
        expect(((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "fits must accept the current piece at the free trial position (6, 5).");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "Asking fits about (6, 5) must not move the actual falling piece there.");
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), -1, 0)), "fits must reject the trial position (-1, 0) outside the board.");
        expect(((int) Reflect.get(g, "px")) == oldX && ((int) Reflect.get(g, "py")) == oldY, "Rejecting a fits query must preserve the falling piece's actual x and y.");
        ((int[][]) Reflect.get(g, "board"))[5][6] = 1;
        expect(!((boolean) Reflect.call(g, "fits", ((int[][]) Reflect.get(g, "piece")), 6, 5)), "fits must reject the trial position when one of its cells is already occupied.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_GoFishTest {
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
        var g = Reflect.create("P16_Task_GoFish");
        Reflect.set(g, "hands", java.util.List.of(new java.util.ArrayList<>(java.util.List.of(7, 7, 7, 7)), new java.util.ArrayList<>()));
        Reflect.set(g, "books", new int[2]);
        Reflect.call(g, "collect", 0);
        equal(0, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size(), "collect must remove all four cards of a completed rank from the hand, leaving no spare card.");
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "Collecting one complete rank must award exactly one book.");
        equal(4, ((java.util.List<java.util.List<java.lang.Integer>>) Reflect.get(g, "hands")).get(0).size() + 4 * ((int[]) Reflect.get(g, "books"))[0], "After collection, cards left in hand plus four per awarded book must still account for the original four cards.");
        Reflect.call(g, "collect", 0);
        equal(1, ((int[]) Reflect.get(g, "books"))[0], "Calling collect again without new cards must not award the same book twice.");
    }
}

@SuppressWarnings("unchecked")
class P19_Task_ChaosGameTest {
    static int checks;
    static final String[] CASES = {
        "chooseVertex with count 3 must be able to select each index 0, 1 and 2, including the last vertex.",
        "Equal seeds and equal chooseVertex calls must reproduce the same index sequence.",
        "chooseVertex with one candidate must return 0; count 0 must be rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P19_Task_ChaosGame");
            var random=new Random(42);Set<Integer> chosen=new HashSet<>();for(int n=0;n<200;n++)chosen.add(((int) Reflect.call(p, "chooseVertex", random, 3)));
            check(chosen.equals(Set.of(0,1,2)),0,chosen);
            var a=new Random(17);var b=new Random(17);for(int n=0;n<20;n++)check(((int) Reflect.call(p, "chooseVertex", a, 3))==((int) Reflect.call(p, "chooseVertex", b, 3)),1,n);
            check(((int) Reflect.call(p, "chooseVertex", new Random(42), 1))==0,2,"single");reject(()->Reflect.call(p, "chooseVertex", new Random(42), 0),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P22_Task_VoronoiStainedGlassTest {
    static int checks;
    static final String[] CASES = {
        "seedList must return the requested number of successive nextSeed results in generation order, with coordinates inside the picture.",
        "seedList must return a mutable list: removing one seed must be allowed.",
        "seedList must accept counts 0–100, including an empty result for 0, and reject counts outside that range."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P22_Task_VoronoiStainedGlass");
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
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P23_Task_MandelbrotPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "matchingSeeds must return every matching seed from 0 through 255 in increasing order, not just the first match.",
        "matchingSeeds must compare the whole supplied prefix, preserve that input and return an empty list when no palette matches.",
        "Keep the existing seeded palette recipe and fractal calculation: replaying the same inputs must produce the same picture."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
        var p = Reflect.create("P23_Task_MandelbrotPostcards");
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
    }
}

@SuppressWarnings("unchecked")
class P24_Task_ColourFilterSecretsTest {
    static int checks;
    static final String[] CASES = {
        "hide must produce different camouflage for the tested seeds 42 and 43. Use the requested seed rather than a fixed one.",
        "Calling hide again with the same mask and seed must reproduce every pixel.",
        "reveal must recover the same hidden message from both noise seeds. Changing camouflage must preserve the message channel."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P24_Task_ColourFilterSecrets");
            var mask=((java.awt.image.BufferedImage) Reflect.call(p, "mask", "HI"));var a=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42));var b=((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 43));check(hash(a)!=hash(b),0,hash(a));
            check(hash(a)==hash(((java.awt.image.BufferedImage) Reflect.call(p, "hide", mask, 42))),1,hash(a));check(hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a)))==hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", b))),2,hash(((java.awt.image.BufferedImage) Reflect.call(p, "reveal", a))));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P25_Task_TwoNoisyPicturesTest {
    static int checks;
    static final String[] CASES = {
        "Across blank tiles, shares must use varied dot patterns instead of repeating one shuffle everywhere.",
        "Repeating seed 42 must reproduce the first share, while the tested seed 43 must change it.",
        "Every small tile in each share must still contain exactly two black dots."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError(CASES[example] + "\n    Case / result: " + Check.show(actual));
    }
    static void reject(Runnable action, int example) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
    }
    interface IOAction { void run() throws Exception; }
    static void ioFailure(IOAction action, int example) throws Exception {
        try {
            action.run();
        } catch (IOException expected) {
            check(true, example, expected.getClass().getSimpleName());
            return;
        }
        check(false, example, "the input was accepted");
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
            var p = Reflect.create("P25_Task_TwoNoisyPictures");
            var mask=new BufferedImage(20,1,BufferedImage.TYPE_INT_RGB);var a=((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42));var patterns=new HashSet<Integer>();for(int cell=0;cell<20;cell++){int bits=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)bits|=1<<k;patterns.add(bits);}check(patterns.size()>1,0,patterns);
            check(hash(a[0])==hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 42))[0]),1,hash(a[0]));check(hash(a[0])!=hash(((java.awt.image.BufferedImage[]) Reflect.call(p, "shares", mask, 43))[0]),1,hash(a[0]));for(int cell=0;cell<20;cell++){int black=0;for(int k=0;k<4;k++)if((a[0].getRGB(cell*2+k%2,k/2)&0xFFFFFF)==0)black++;check(black==2,2,black);}
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
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
