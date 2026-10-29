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
        run("P03_Task_Flood", () -> P03_Task_FloodTest.main(new String[0]));
        run("P04_Task_SameGame", () -> P04_Task_SameGameTest.main(new String[0]));
        run("P05_Task_DragonCurve", () -> P05_Task_DragonCurveTest.main(new String[0]));
        run("P06_Task_EvilHangman", () -> P06_Task_EvilHangmanTest.main(new String[0]));
        run("P07_Task_WordChain", () -> P07_Task_WordChainTest.main(new String[0]));
        run("P08_Task_Hex", () -> P08_Task_HexTest.main(new String[0]));
        run("P09_Task_SetStyleTripleHunt", () -> P09_Task_SetStyleTripleHuntTest.main(new String[0]));
        run("P10_Task_Shikaku", () -> P10_Task_ShikakuTest.main(new String[0]));
        run("P11_Task_GapHopper", () -> P11_Task_GapHopperTest.main(new String[0]));
        run("P12_Task_FlipbookStudio", () -> P12_Task_FlipbookStudioTest.main(new String[0]));
        run("P15_Task_Hangman", () -> P15_Task_HangmanTest.main(new String[0]));
        run("P16_Task_WordSearch", () -> P16_Task_WordSearchTest.main(new String[0]));
        run("P17_Task_TinyStepSequencer", () -> P17_Task_TinyStepSequencerTest.main(new String[0]));
        run("P18_Task_CirclePacking", () -> P18_Task_CirclePackingTest.main(new String[0]));
        run("P19_Task_WireframeCube", () -> P19_Task_WireframeCubeTest.main(new String[0]));
        run("P22_Task_TruchetTileLab", () -> P22_Task_TruchetTileLabTest.main(new String[0]));
        run("P23_Task_MarkovMischief", () -> P23_Task_MarkovMischiefTest.main(new String[0]));
        run("P24_Task_DungeonBlueprints", () -> P24_Task_DungeonBlueprintsTest.main(new String[0]));
        run("P27_Task_PhotoRemixLab", () -> P27_Task_PhotoRemixLabTest.main(new String[0]));
        run("P30_Task_Nonogram", () -> P30_Task_NonogramTest.main(new String[0]));
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
class P03_Task_FloodTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Task_Flood");
        Reflect.set(game, "board", new int[8][8]);
        ((int[][]) Reflect.get(game, "board"))[7][7] = 1;
        Reflect.call(game, "fill", 2);
        check(((int[][]) Reflect.get(game, "board"))[7][6] == 2, "spread must keep processing newly discovered cells; the connected cell near the far corner must also change colour.");
        int changed = 0;
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) { for (int value : row) { if (value == 2) changed++; } }
        check(changed == 63 && ((int[][]) Reflect.get(game, "board"))[7][7] == 1, "fill must recolour all 63 connected cells, while preserving the differently coloured corner.");
        check(((int) Reflect.get(game, "moves")) == 1, "One completed fill must increase moves by exactly one.");
        Reflect.call(game, "spread", new ArrayList<>(), 0, 1);
        check(((int[][]) Reflect.get(game, "board"))[7][7] == 1, "Calling spread with an empty work list must leave the board unchanged.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_SameGameTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P04_Task_SameGame");
        Reflect.set(game, "board", new int[5][5]);
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) Arrays.fill(row, 1);
        check(((java.util.List<int[]>) Reflect.call(game, "group", 0, 0)).size() == 25, "group(0, 0) must find all 25 connected cells on this single-colour board.");
        check(((java.util.List<int[]>) Reflect.call(game, "group", -1, 0)).isEmpty(), "group(-1, 0) must return an empty group without accessing an invalid cell.");
        Reflect.set(game, "outcome", "");
        Reflect.call(game, "command", "1 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).startsWith("Board clear") && ((int) Reflect.get(game, "score")) == 600, "Removing the entire 25-cell group must clear the board and award 600 points.");
    }
}

@SuppressWarnings("unchecked")
class P05_Task_DragonCurveTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P05_Task_DragonCurve");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 1)).equals(List.of(1)), "foldTurns(1) must perform one fold and return [1].");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 0)).isEmpty(), "foldTurns(0) must perform no folds and return an empty list.");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 2)).equals(List.of(1,1,-1)), "foldTurns(2) must perform two folds and return [1, 1, -1].");
        check(((java.util.List<java.awt.Point>) Reflect.call(game, "path", 3)).size() == 9, "path(3) must produce nine points joined by eight segments after three folds.");
    }
}

@SuppressWarnings("unchecked")
class P06_Task_EvilHangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P06_Task_EvilHangman");
        check(((int) Reflect.call(game, "familySize", List.of("ALLY","COOL","GOOD"))) == 3, "familySize must count all three words ALLY, COOL and GOOD, not only the final word.");
        check(((int) Reflect.call(game, "familySize", List.of())) == 0 && ((int) Reflect.call(game, "familySize", List.of("ALLY"))) == 1, "familySize must return 0 for an empty family and 1 for a one-word family.");
        Reflect.call(game, "reset");
        Reflect.set(game, "pool", new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA")));
        Reflect.call(game, "command", "E");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "pool")).size() == 3 && ((java.lang.String) Reflect.get(game, "mask")).equals("____"), "After the tested guess, the adversary must retain the largest family of three words with mask ____.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_WordChainTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_WordChain");
        Reflect.call(game, "reset");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 20, "choices must retain all 20 initially legal words, in dictionary order.");
        Reflect.set(game, "last", "APPLE");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "After a word ending in E, choices must contain EARTH, EAGLE, ELEPHANT, ECHO and ENGINE in that order.");
        ((java.util.Set<java.lang.String>) Reflect.get(game, "used")).add("EARTH");
        check(!((java.util.List<java.lang.String>) Reflect.call(game, "choices")).contains("EARTH") && ((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 4, "After EARTH has been used, choices must exclude it and keep the other four E continuations.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "APPLE");
        check(((java.lang.String) Reflect.get(game, "last")).equals("APPLE"), "The actual command must accept APPLE when it is legal, even though it is not the last word in the dictionary.");
    }
}

@SuppressWarnings("unchecked")
class P08_Task_HexTest {
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
        var g = Reflect.create("P08_Task_Hex");
        Reflect.call(g, "reset");
        for (int r = 0; r < 5; r++) {
            ((int[][]) Reflect.get(g, "board"))[r][4 - r] = 1;
        }
        expect(((boolean) Reflect.call(g, "connected", 1)), "connected(1) must recognise the supplied diagonal chain as a complete hex path. Check all six hex-neighbour directions.");
        ((int[][]) Reflect.get(g, "board"))[2][2] = 0;
        expect(!((boolean) Reflect.call(g, "connected", 1)), "Removing a cell from that chain must break player 1's connection.");
        Reflect.call(g, "reset");
        for (int c = 0; c < 5; c++) {
            ((int[][]) Reflect.get(g, "board"))[2][c] = 2;
        }
        expect(((boolean) Reflect.call(g, "connected", 2)), "connected(2) must recognise player 2's path from the left border to the right border.");
        expect(!((boolean) Reflect.call(g, "connected", 1)), "Player 1's path must not cross cells occupied by player 2.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_SetStyleTripleHuntTest {
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
        var g = Reflect.create("P09_Task_SetStyleTripleHunt");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 2)));
        expect(((boolean) Reflect.call(g, "hasSet")), "hasSet must find the only valid triple even when it includes the last card on the table.");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 3)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "hasSet must return false for this near-match: not every triple satisfies the attribute rule.");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "hasSet must return false when fewer than three cards are available.");
        expect(((boolean) Reflect.call(g, "triple", 0, 1, 2)), "The supplied triple(0, 1, 2) rule must still recognise a valid attribute combination.");
    }
}

@SuppressWarnings("unchecked")
class P10_Task_ShikakuTest {
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
        var g = Reflect.create("P10_Task_Shikaku");
        Reflect.call(g, "restart");
        expect(((boolean) Reflect.call(g, "add", 0, 0, 1, 2)), "add(0, 0, 1, 2) must still accept the supplied valid rectangle.");
        Reflect.set(g, "startX", 2);
        Reflect.set(g, "message", "Rejected: earlier drag");
        Reflect.call(g, "undo");
        equal(0, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "Undo after adding the first rectangle must restore an empty box list. The saved list must not share later membership changes.");
        equal("Previous layout restored", ((java.lang.String) Reflect.get(g, "message")), "Undo must report Previous layout restored after restoring the old rectangle layout.");
        equal(-1, ((int) Reflect.get(g, "startX")), "Undo must cancel any unfinished drag selection as well as restoring the boxes.");
        Reflect.call(g, "add", 0, 0, 1, 2);
        Reflect.call(g, "remove", 0, 0);
        Reflect.call(g, "undo");
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "Undo after removing a rectangle must restore that rectangle.");
        var copy = ((java.util.ArrayList<Object>) Reflect.call(g, "snapshot"));
        copy.clear();
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "Clearing the list returned by snapshot must not clear the live rectangle list.");
        expect(copy != ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")), "snapshot must return a separate, mutable list rather than another reference to boxes.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_GapHopperTest {
    static int checks;
    static final String[] CASES = {
        "recyclePipes must remove the expired first pipe while preserving the existing next pipe.",
        "recyclePipes must append the new pipe beyond the current last pipe, with its scoring flag cleared; pipes that have not expired stay in place.",
        "recyclePipes must accept an empty pipe list without adding obstacles or failing."
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
            var p = Reflect.create("P11_Task_GapHopper");
            Reflect.call(p, "restart"); var old = ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst(); var next = ((java.util.List<Object>) Reflect.get(p, "pipes")).getLast();
            Reflect.set(old, "x", -60); Reflect.set(next, "x", 200); Reflect.call(p, "recyclePipes");
            check(((java.util.List<Object>) Reflect.get(p, "pipes")).size()==2 && ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst()==next && !((java.util.List<Object>) Reflect.get(p, "pipes")).contains(old),0,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            check(((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x"))==460 && !((boolean) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "scored")),1,((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x")));
            var stable=List.copyOf(((java.util.List<Object>) Reflect.get(p, "pipes")));Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).equals(stable),1,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            ((java.util.List<Object>) Reflect.get(p, "pipes")).clear();Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).isEmpty(),2,((java.util.List<Object>) Reflect.get(p, "pipes")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P12_Task_FlipbookStudioTest {
    static int checks;
    static final String[] CASES = {
        "Two undo calls must restore the two latest old colours in reverse edit order, removing each edit from history.",
        "undo must return false when history is empty, without changing a frame.",
        "undo must restore the edit's recorded frame and coordinates; restart must clear the old edit history."
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
            var p = Reflect.create("P12_Task_FlipbookStudio");
            Reflect.call(p, "restart");Reflect.call(p, "paintCell", 2, 3, 1);Reflect.call(p, "paintCell", 2, 3, 4);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==1 && ((java.util.List<Object>) Reflect.get(p, "history")).size()==1,0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==0 && ((java.util.List<Object>) Reflect.get(p, "history")).isEmpty(),0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(!((boolean) Reflect.call(p, "undo")),1,((java.util.List<Object>) Reflect.get(p, "history")));
            Reflect.set(p, "edited", 2);Reflect.call(p, "paintCell", 1, 4, 3);Reflect.set(p, "edited", 0);Reflect.call(p, "undo");check(((int[][][]) Reflect.get(p, "frames"))[2][4][1]==0,2,((int[][][]) Reflect.get(p, "frames"))[2][4][1]);
            Reflect.call(p, "paintCell", 0, 0, 2);Reflect.call(p, "restart");check(((java.util.List<Object>) Reflect.get(p, "history")).isEmpty()&&!((boolean) Reflect.call(p, "undo")),2,((java.util.List<Object>) Reflect.get(p, "history")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P15_Task_HangmanTest {
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
        var game = Reflect.create("P15_Task_Hangman");
        String[][] samples = {{"BANANA", "", "A"}, {"BANANA", "A", "B"}, {"IDEA", "", "I"}, {"AUDIO", "A", "U"},
                {"BYE", "Y", "E"}, {"RHYTHM", "H", "R"}, {"METER", "E", "M"},
                {"EYE", "E", "Y"}, {"ALPHA", "ALPH", ""}, {"", "", ""}};
        for (String[] sample : samples) {
            Reflect.set(game, "word", sample[0]);
            Reflect.set(game, "tried", sample[1]);
            Reflect.set(game, "misses", 3);
            equal(sample[2], Reflect.call(game, "hintLetter"), "hintLetter must prefer the first hidden vowel, otherwise the first hidden letter. Case [word, tried, advice]: " + Arrays.toString(sample));
            equal(sample[2], Reflect.call(game, "hintLetter"), "Repeating hintLetter without a move must return the same advice.");
            equal(sample[0], Reflect.get(game, "word"), "Asking hintLetter must not change the secret word.");
            equal(sample[1], Reflect.get(game, "tried"), "Asking hintLetter must not add a letter to tried; advice is not a guess.");
            equal(3, Reflect.get(game, "misses"), "Asking hintLetter must preserve the miss count, which is 3 in this case.");
        }
        Reflect.set(game, "word", "BANANA");
        Reflect.set(game, "tried", "A");
        equal("_A_A_A", Reflect.call(game, "mask"), "The ordinary mask must still show _A_A_A for BANANA after A has been tried.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_WordSearchTest {
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
        var game = Reflect.create("P16_Task_WordSearch");
        for (String[] sample : new String[][]{{"TAC", "CAT"}, {"GOD", "DOG"}, {"REGIT", "TIGER"},
                {"CAT", "CAT"}, {"CAX", "CAX"}, {"", ""}}) {
            equal(sample[1], Reflect.call(game, "targetName", sample[0]), "targetName must return the same target name for both reading directions, while preserving unrelated text. Selected text: " + sample[0]);
        }
        equal("TAC", Reflect.call(game, "read", 0, 2, 0, 0), "read must preserve the selected direction: reading CAT backwards produces TAC.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "1 3 1 1");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "Selecting TAC backwards must record the target CAT rather than a new target named TAC.");
        boolean rejected = false;
        try {
            Reflect.call(game, "command", "1 1 1 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "Finding CAT again from the other end must be rejected as a duplicate.");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "A rejected duplicate must leave found containing only CAT.");
        Reflect.call(game, "command", "2 4 2 2");
        Reflect.call(game, "command", "3 5 3 1");
        equal(Set.of("CAT", "DOG", "TIGER"), Reflect.get(game, "found"), "Selecting each target backwards must find CAT, DOG and TIGER exactly once each.");
        equal("All words found!", Reflect.get(game, "outcome"), "After three different targets are found, the actual round must announce All words found!");
        Reflect.call(game, "reset");
        rejected = false;
        try {
            Reflect.call(game, "command", "4 1 4 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "Reversing an unrelated path must not turn it into a valid target.");
        for (String invalid : new String[]{"1 1 2 3", "0 1 1 3", "1 1 1 1"}) {
            rejected = false;
            try {
                Reflect.call(game, "command", invalid);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }
            expect(rejected, "A selection must remain straight, inside the board and longer than one cell. Rejected input: " + invalid);
            equal(Set.of(), Reflect.get(game, "found"), "Rejected selections must leave found empty rather than invent a word.");
        }
    }
}

@SuppressWarnings("unchecked")
class P17_Task_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Toggling one note twice must restore the original active notes and leave other notes unchanged.",
        "Toggling an inactive valid cell must add exactly one matching Note and report success.",
        "Invalid note coordinates must be rejected without changing notes; an empty note set must generate silence."
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
            var p = Reflect.create("P17_Task_TinyStepSequencer");
            Reflect.call(p, "reset");var original=Set.copyOf(((java.util.Set<Object>) Reflect.get(p, "notes")));Reflect.call(p, "toggle", 0, 1);Reflect.call(p, "toggle", 0, 1);
            check(((java.util.Set<Object>) Reflect.get(p, "notes")).equals(original),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            ((java.util.Set<Object>) Reflect.get(p, "notes")).clear();check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).contains(Reflect.create("P17_Task_TinyStepSequencer$Note", 2, 4)),1,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(!((boolean) Reflect.call(p, "toggle", -1, 0))&&!((boolean) Reflect.call(p, "toggle", 4, 0))&&!((boolean) Reflect.call(p, "toggle", 0, 8))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),2,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(Arrays.stream(((double[]) Reflect.call(p, "sequence", 1))).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P18_Task_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "claimCentre must accept a new centre and remember it for future claims.",
        "claimCentre must reject a repeated centre, but treat (x, y) and (y, x) as different centres when x differs from y.",
        "The real pack operation must still be able to place eight distinct bubbles. Check that fresh centres are accepted."
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
            var p = Reflect.create("P18_Task_CirclePacking");
            Set<Object> seen=new HashSet<>();
            check(((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,0,seen);
            check(!((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,1,seen);
            check(((boolean) Reflect.call(p, "claimCentre", seen, 7, 4))&&seen.size()==2,1,seen);
            var bubbles=((java.util.List<Object>) Reflect.call(p, "pack", 42, 8));check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->Reflect.create("P18_Task_CirclePacking$Centre", (int)((double) Reflect.call(b, "x")), (int)((double) Reflect.call(b, "y")))).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P19_Task_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "uniqueEdges must merge repeated and reversed endpoint pairs, leaving two distinct edges in this example.",
        "Each returned Edge must store the smaller endpoint first, and surviving edges must keep first-seen order.",
        "uniqueEdges must ignore invalid and identical endpoints, accept empty input, and retain all twelve real cube edges."
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
            var p = Reflect.create("P19_Task_WireframeCube");
            var edges=((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{0,1},{1,0},{0,1},{1,2}}));
            check(edges.size()==2,0,edges);
            check(edges.contains(Reflect.create("P19_Task_WireframeCube$Edge", 0, 1))&&edges.contains(Reflect.create("P19_Task_WireframeCube$Edge", 1, 2)),1,edges);
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{-1,0},{0,8},{2,2}})).isEmpty(),2,"invalid");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[0][2])).isEmpty(),2,"empty");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size()==12,0,((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P22_Task_TruchetTileLabTest {
    static int checks;
    static final String[] CASES = {
        "chooseOrientation must return the first valid preference and increment only that orientation's count.",
        "When the preferred orientation already leads by at least two, chooseOrientation must choose and count the other orientation.",
        "Rendered tiles must follow the returned orientation, and an unknown preference must be rejected without changing either count."
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
        var p = Reflect.create("P22_Task_TruchetTileLab");
        var counts = (Map<String, Integer>) Reflect.get(p, "orientationCounts");
        check(Reflect.call(p, "chooseOrientation", "LEFT").equals("LEFT") && counts.equals(Map.of("LEFT", 1)), 0, counts);
        Reflect.call(p, "chooseOrientation", "LEFT");
        check(Reflect.call(p, "chooseOrientation", "LEFT").equals("RIGHT") && counts.equals(Map.of("LEFT", 2, "RIGHT", 1)), 1, counts);
        Reflect.call(p, "render", 42, 12);
        int left = counts.getOrDefault("LEFT", 0), right = counts.getOrDefault("RIGHT", 0);
        check(left + right == 144 && Math.abs(left - right) <= 2, 2, counts);
        var before = new HashMap<>(counts);
        boolean rejected = false;
        try { Reflect.call(p, "chooseOrientation", "UP"); } catch (IllegalArgumentException invalid) { rejected = true; }
        check(rejected && counts.equals(before), 2, counts);
    }
}

@SuppressWarnings("unchecked")
class P23_Task_MarkovMischiefTest {
    static int checks;
    static final String[] CASES = {
        "After training on moon cat moon dog moon cat, moon's followers must be [cat, dog, cat] in that order.",
        "train must keep earlier followers when their word appears again, including repeated followers.",
        "A one-word corpus must create an empty follower list for that word; a blank corpus must be rejected."
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
            var p = Reflect.create("P23_Task_MarkovMischief");
            var links=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "moon cat moon dog moon cat"));
            check(links.get("moon").equals(List.of("cat","dog","cat")),0,links);
            check(links.containsKey("cat")&&links.get("cat").equals(List.of("moon")),1,links);
            var single=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "solo"));check(single.containsKey("solo")&&single.get("solo").isEmpty(),2,single);
            reject(()->Reflect.call(p, "train", "   "),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("The program stopped while this rule was being checked. See the exception details below.", failure);
        }
    }
}

@SuppressWarnings("unchecked")
class P24_Task_DungeonBlueprintsTest {
    static int checks;
    static final String[] CASES = {
        "admitRoom must classify area 42 as SMALL and accept the first three SMALL rooms.",
        "admitRoom must refuse a fourth SMALL or third LARGE room without changing either category's count.",
        "The generated dungeon must contain only admitted rooms and obey both category quotas."
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
        var p = Reflect.create("P24_Task_DungeonBlueprints");
        var counts = (Map<String, Integer>) Reflect.get(p, "roomCounts");
        Object small = Reflect.create("P24_Task_DungeonBlueprints$Room", 1, 1, 6, 7);
        Object large = Reflect.create("P24_Task_DungeonBlueprints$Room", 1, 1, 7, 7);
        check((boolean) Reflect.call(p, "admitRoom", small), 0, counts);
        check((boolean) Reflect.call(p, "admitRoom", small) && (boolean) Reflect.call(p, "admitRoom", small), 0, counts);
        check(!(boolean) Reflect.call(p, "admitRoom", small) && counts.get("SMALL") == 3, 1, counts);
        check((boolean) Reflect.call(p, "admitRoom", large) && (boolean) Reflect.call(p, "admitRoom", large), 1, counts);
        var before = new HashMap<>(counts);
        check(!(boolean) Reflect.call(p, "admitRoom", large) && counts.equals(before), 1, counts);
        Reflect.call(p, "generate", 42, 8);
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        check(total > 0 && total <= 5 && counts.getOrDefault("SMALL", 0) <= 3 && counts.getOrDefault("LARGE", 0) <= 2, 2, counts);
    }
}

@SuppressWarnings("unchecked")
class P27_Task_PhotoRemixLabTest {
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
        var g = Reflect.create("P27_Task_PhotoRemixLab");
        var counts = (Map<Integer, Integer>) Reflect.get(g, "colourCounts");
        counts.put(-5, 2); counts.put(-7, 4); counts.put(-9, 2); counts.put(-11, 0);
        var before = new HashMap<>(counts);
        equal(-9, Reflect.call(g, "spotlightColour"), "spotlightColour must select the least frequent positive count; tied counts choose the smaller signed RGB key.");
        equal(before, counts, "Calling spotlightColour must not change the colour histogram.");
        counts.clear(); equal(0, Reflect.call(g, "spotlightColour"), "spotlightColour must return 0 when the histogram is empty.");
        counts.put(7, Integer.MAX_VALUE); equal(7, Reflect.call(g, "spotlightColour"), "A sole colour with frequency Integer.MAX_VALUE is still eligible and must be selected.");
        Reflect.call(g, "restart");
        var result = (java.awt.image.BufferedImage) Reflect.get(g, "result");
        equal(java.awt.Color.CYAN.getRGB(), result.getRGB(35, 40), "The real spotlight image must highlight the hidden key in cyan, including its pixel at (35, 40).");
        expect(result.getRGB(200, 150) != java.awt.Color.CYAN.getRGB(), "The more common moon must not receive the cyan spotlight instead of the rare key.");
    }
}

@SuppressWarnings("unchecked")
class P30_Task_NonogramTest {
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
        var g = Reflect.create("P30_Task_Nonogram");
        equal(new int[]{3}, ((int[]) Reflect.call(g, "runs", new int[]{0, 1, 1, 1})), "runs([0, 1, 1, 1]) must return [3], retaining the black group that reaches the final cell.");
        equal(new int[]{2, 1}, ((int[]) Reflect.call(g, "runs", new int[]{1, 1, 0, 1})), "runs([1, 1, 0, 1]) must return [2, 1], preserving both separate black groups.");
        equal(new int[]{}, ((int[]) Reflect.call(g, "runs", new int[]{0, 0, 0})), "runs on an all-white row must return an empty array, not a zero-length group.");
        equal(new int[]{1}, ((int[]) Reflect.call(g, "runs", new int[]{1})), "runs([1]) must return [1] for a single black cell.");
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
