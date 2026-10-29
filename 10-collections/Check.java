import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_Flood.java
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
        run("P03_Flood", "10.03 Flood · F10", () -> P03_FloodTest.main(new String[0]));
        run("P04_SameGame", "10.04 SameGame · F10", () -> P04_SameGameTest.main(new String[0]));
        run("P05_DragonCurve", "10.05 DragonCurve · F11", () -> P05_DragonCurveTest.main(new String[0]));
        run("P06_EvilHangman", "10.06 EvilHangman · F12", () -> P06_EvilHangmanTest.main(new String[0]));
        run("P07_WordChain", "10.07 WordChain · F12", () -> P07_WordChainTest.main(new String[0]));
        run("P08_Hex", "10.08 Hex · F17", () -> P08_HexTest.main(new String[0]));
        run("P09_SetStyleTripleHunt", "10.09 SetStyleTripleHunt · F17", () -> P09_SetStyleTripleHuntTest.main(new String[0]));
        run("P10_Shikaku", "10.10 Shikaku · F20", () -> P10_ShikakuTest.main(new String[0]));
        run("P11_GapHopper", "10.11 GapHopper · F22", () -> P11_GapHopperTest.main(new String[0]));
        run("P12_FlipbookStudio", "10.12 FlipbookStudio · F22", () -> P12_FlipbookStudioTest.main(new String[0]));
        run("P15_Hangman", "10.15 Hangman · F10", () -> P15_HangmanTest.main(new String[0]));
        run("P16_WordSearch", "10.16 WordSearch · F11", () -> P16_WordSearchTest.main(new String[0]));
        run("P17_TinyStepSequencer", "10.17 TinyStepSequencer · F23", () -> P17_TinyStepSequencerTest.main(new String[0]));
        run("P18_CirclePacking", "10.18 CirclePacking · F23", () -> P18_CirclePackingTest.main(new String[0]));
        run("P19_WireframeCube", "10.19 WireframeCube · F23", () -> P19_WireframeCubeTest.main(new String[0]));
        run("P22_TruchetTileLab", "10.22 TruchetTileLab · F24", () -> P22_TruchetTileLabTest.main(new String[0]));
        run("P23_MarkovMischief", "10.23 MarkovMischief · F24", () -> P23_MarkovMischiefTest.main(new String[0]));
        run("P24_DungeonBlueprints", "10.24 DungeonBlueprints · F24", () -> P24_DungeonBlueprintsTest.main(new String[0]));
        run("P27_PhotoRemixLab", "10.27 PhotoRemixLab · F24", () -> P27_PhotoRemixLabTest.main(new String[0]));
        run("P30_Nonogram", "10.30 Nonogram · F17", () -> P30_NonogramTest.main(new String[0]));
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
class P03_FloodTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("10.03 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Flood");
        Reflect.set(game, "board", new int[8][8]);
        ((int[][]) Reflect.get(game, "board"))[7][7] = 1;
        Reflect.call(game, "fill", 2);
        check(((int[][]) Reflect.get(game, "board"))[7][6] == 2, "the expanding queue must be processed beyond the first cell");
        int changed = 0;
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) { for (int value : row) { if (value == 2) changed++; } }
        check(changed == 63 && ((int[][]) Reflect.get(game, "board"))[7][7] == 1, "all and only the connected old colour must change");
        check(((int) Reflect.get(game, "moves")) == 1, "one completed flood counts as one move");
        Reflect.call(game, "spread", new ArrayList<>(), 0, 1);
        check(((int[][]) Reflect.get(game, "board"))[7][7] == 1, "an empty work queue performs no iteration");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_SameGameTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("10.04 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P04_SameGame");
        Reflect.set(game, "board", new int[5][5]);
        for (int[] row : ((int[][]) Reflect.get(game, "board"))) Arrays.fill(row, 1);
        check(((java.util.List<int[]>) Reflect.call(game, "group", 0, 0)).size() == 25, "the queue loop must discover the full connected group");
        check(((java.util.List<int[]>) Reflect.call(game, "group", -1, 0)).isEmpty(), "an invalid start is rejected before queue access");
        Reflect.set(game, "outcome", "");
        Reflect.call(game, "command", "1 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).startsWith("Board clear") && ((int) Reflect.get(game, "score")) == 600, "the actual complete group can be removed to win");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P05_DragonCurveTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("10.05 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P05_DragonCurve");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 1)).equals(List.of(1)), "one requested fold must run the outer loop once");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 0)).isEmpty(), "zero folds must run zero times");
        check(((java.util.List<java.lang.Integer>) Reflect.call(game, "foldTurns", 2)).equals(List.of(1,1,-1)), "two folds use two outer passes");
        check(((java.util.List<java.awt.Point>) Reflect.call(game, "path", 3)).size() == 9, "the actual three-fold path has eight segments and nine points");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P06_EvilHangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("10.06 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P06_EvilHangman");
        check(((int) Reflect.call(game, "familySize", List.of("ALLY","COOL","GOOD"))) == 3, "the family count must accumulate across all words");
        check(((int) Reflect.call(game, "familySize", List.of())) == 0 && ((int) Reflect.call(game, "familySize", List.of("ALLY"))) == 1, "zero and one member are counted correctly");
        Reflect.call(game, "reset");
        Reflect.set(game, "pool", new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA")));
        Reflect.call(game, "command", "E");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "pool")).size() == 3 && ((java.lang.String) Reflect.get(game, "mask")).equals("____"), "the actual adversary must keep the largest consistent family");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_WordChainTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("10.07 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_WordChain");
        Reflect.call(game, "reset");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 20, "the accumulated choices must retain all initially legal words");
        Reflect.set(game, "last", "APPLE");
        check(((java.util.List<java.lang.String>) Reflect.call(game, "choices")).equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "all five E continuations survive the loop");
        ((java.util.Set<java.lang.String>) Reflect.get(game, "used")).add("EARTH");
        check(!((java.util.List<java.lang.String>) Reflect.call(game, "choices")).contains("EARTH") && ((java.util.List<java.lang.String>) Reflect.call(game, "choices")).size() == 4, "used words are excluded without losing other choices");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "APPLE");
        check(((java.lang.String) Reflect.get(game, "last")).equals("APPLE"), "the actual game accepts an early legal word, not only the final dictionary entry");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P08_HexTest {
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
        var g = Reflect.create("P08_Hex");
        Reflect.call(g, "reset");
        for (int r = 0; r < 5; r++) {
            ((int[][]) Reflect.get(g, "board"))[r][4 - r] = 1;
        }
        expect(((boolean) Reflect.call(g, "connected", 1)), "10.08/F17: hex diagonal is a connected path");
        ((int[][]) Reflect.get(g, "board"))[2][2] = 0;
        expect(!((boolean) Reflect.call(g, "connected", 1)), "10.08/F17: gap breaks the path");
        Reflect.call(g, "reset");
        for (int c = 0; c < 5; c++) {
            ((int[][]) Reflect.get(g, "board"))[2][c] = 2;
        }
        expect(((boolean) Reflect.call(g, "connected", 2)), "10.08/F17: player two connects left to right");
        expect(!((boolean) Reflect.call(g, "connected", 1)), "10.08/F17: opposing pieces are not traversed");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P09_SetStyleTripleHuntTest {
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
        var g = Reflect.create("P09_SetStyleTripleHunt");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 2)));
        expect(((boolean) Reflect.call(g, "hasSet")), "10.09/F17: the last card can complete the only triple");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1, 3)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "10.09/F17: near miss is not a triple");
        Reflect.set(g, "table", new java.util.ArrayList<>(java.util.List.of(0, 1)));
        expect(!((boolean) Reflect.call(g, "hasSet")), "10.09/F17: fewer than three cards cannot work");
        expect(((boolean) Reflect.call(g, "triple", 0, 1, 2)), "10.09/F17: supplied attribute rule still used");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P10_ShikakuTest {
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
        var g = Reflect.create("P10_Shikaku");
        Reflect.call(g, "restart");
        expect(((boolean) Reflect.call(g, "add", 0, 0, 1, 2)), "10.10/F20: valid rectangle added");
        Reflect.set(g, "startX", 2);
        Reflect.set(g, "message", "Rejected: earlier drag");
        Reflect.call(g, "undo");
        equal(0, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "10.10/F20: undo restores an independent list");
        equal("Previous layout restored", ((java.lang.String) Reflect.get(g, "message")), "10.10/F20: undo feedback describes the restored board");
        equal(-1, ((int) Reflect.get(g, "startX")), "10.10/F20: undo leaves no unfinished drag outline");
        Reflect.call(g, "add", 0, 0, 1, 2);
        Reflect.call(g, "remove", 0, 0);
        Reflect.call(g, "undo");
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "10.10/F20: removed rectangle can be restored");
        var copy = ((java.util.ArrayList<Object>) Reflect.call(g, "snapshot"));
        copy.clear();
        equal(1, ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")).size(), "10.10/F20: list edits are not shared");
        expect(copy != ((java.util.ArrayList<Object>) Reflect.get(g, "boxes")), "10.10/F20: snapshot is a distinct mutable collection");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_GapHopperTest {
    static int checks;
    static final String[] CASES = {
        "Prošlý object (objekt) zmizí a jeho následník zůstane.",
        "Nová překážka vznikne za poslední; neprošlé překážky se neobnovují.",
        "Prázdný List (seznam) je bezpečný."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.11: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P11_GapHopper");
            Reflect.call(p, "restart"); var old = ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst(); var next = ((java.util.List<Object>) Reflect.get(p, "pipes")).getLast();
            Reflect.set(old, "x", -60); Reflect.set(next, "x", 200); Reflect.call(p, "recyclePipes");
            check(((java.util.List<Object>) Reflect.get(p, "pipes")).size()==2 && ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst()==next && !((java.util.List<Object>) Reflect.get(p, "pipes")).contains(old),0,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            check(((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x"))==460 && !((boolean) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "scored")),1,((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x")));
            var stable=List.copyOf(((java.util.List<Object>) Reflect.get(p, "pipes")));Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).equals(stable),1,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            ((java.util.List<Object>) Reflect.get(p, "pipes")).clear();Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).isEmpty(),2,((java.util.List<Object>) Reflect.get(p, "pipes")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 10.11: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P12_FlipbookStudioTest {
    static int checks;
    static final String[] CASES = {
        "Dvě undo obnoví poslední dvě barvy v opačném pořadí.",
        "Undo s prázdnou historií vrátí false.",
        "Undo míří na uložený snímek; restart historii smaže."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.12: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P12_FlipbookStudio");
            Reflect.call(p, "restart");Reflect.call(p, "paintCell", 2, 3, 1);Reflect.call(p, "paintCell", 2, 3, 4);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==1 && ((java.util.List<Object>) Reflect.get(p, "history")).size()==1,0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==0 && ((java.util.List<Object>) Reflect.get(p, "history")).isEmpty(),0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(!((boolean) Reflect.call(p, "undo")),1,((java.util.List<Object>) Reflect.get(p, "history")));
            Reflect.set(p, "edited", 2);Reflect.call(p, "paintCell", 1, 4, 3);Reflect.set(p, "edited", 0);Reflect.call(p, "undo");check(((int[][][]) Reflect.get(p, "frames"))[2][4][1]==0,2,((int[][][]) Reflect.get(p, "frames"))[2][4][1]);
            Reflect.call(p, "paintCell", 0, 0, 2);Reflect.call(p, "restart");check(((java.util.List<Object>) Reflect.get(p, "history")).isEmpty()&&!((boolean) Reflect.call(p, "undo")),2,((java.util.List<Object>) Reflect.get(p, "history")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 10.12: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_HangmanTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("10.15/F10: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P15_Hangman");
        String[][] samples = {{"BANANA", "", "A"}, {"BANANA", "A", "B"}, {"IDEA", "", "I"}, {"AUDIO", "A", "U"},
                {"BYE", "Y", "E"}, {"RHYTHM", "H", "R"}, {"METER", "E", "M"},
                {"EYE", "E", "Y"}, {"ALPHA", "ALPH", ""}, {"", "", ""}};
        for (String[] sample : samples) {
            Reflect.set(game, "word", sample[0]);
            Reflect.set(game, "tried", sample[1]);
            Reflect.set(game, "misses", 3);
            equal(sample[2], Reflect.call(game, "hintLetter"), "advice for " + Arrays.toString(sample));
            equal(sample[2], Reflect.call(game, "hintLetter"), "repeated advice is stable");
            equal(sample[0], Reflect.get(game, "word"), "advice preserves the secret");
            equal(sample[1], Reflect.get(game, "tried"), "advice is not a guess");
            equal(3, Reflect.get(game, "misses"), "advice costs no miss");
        }
        Reflect.set(game, "word", "BANANA");
        Reflect.set(game, "tried", "A");
        equal("_A_A_A", Reflect.call(game, "mask"), "ordinary word display remains available");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_WordSearchTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("10.16/F11: " + message);
    }

    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + " | expected=" + java.util.Arrays.deepToString(new Object[]{expected})
                + " actual=" + java.util.Arrays.deepToString(new Object[]{actual}));
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P16_WordSearch");
        for (String[] sample : new String[][]{{"TAC", "CAT"}, {"GOD", "DOG"}, {"REGIT", "TIGER"},
                {"CAT", "CAT"}, {"CAX", "CAX"}, {"", ""}}) {
            equal(sample[1], Reflect.call(game, "targetName", sample[0]), "target identity for " + sample[0]);
        }
        equal("TAC", Reflect.call(game, "read", 0, 2, 0, 0), "the raw path still preserves selection order");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "1 3 1 1");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "reverse selection finds the canonical target");
        boolean rejected = false;
        try {
            Reflect.call(game, "command", "1 1 1 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "the same word cannot be found again in its other direction");
        equal(Set.of("CAT"), Reflect.get(game, "found"), "a duplicate changes no finds");
        Reflect.call(game, "command", "2 4 2 2");
        Reflect.call(game, "command", "3 5 3 1");
        equal(Set.of("CAT", "DOG", "TIGER"), Reflect.get(game, "found"), "all reverse targets are playable");
        equal("All words found!", Reflect.get(game, "outcome"), "real round ends after three distinct finds");
        Reflect.call(game, "reset");
        rejected = false;
        try {
            Reflect.call(game, "command", "4 1 4 3");
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        expect(rejected, "reversing does not make an unrelated path a target");
        for (String invalid : new String[]{"1 1 2 3", "0 1 1 3", "1 1 1 1"}) {
            rejected = false;
            try {
                Reflect.call(game, "command", invalid);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }
            expect(rejected, "straight in-bounds multi-cell selection is still required: " + invalid);
            equal(Set.of(), Reflect.get(game, "found"), "invalid selections do not invent a find");
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P17_TinyStepSequencerTest {
    static int checks;
    static final String[] CASES = {
        "Dvě přepnutí obnoví stejný Set (množinu) aktivních not.",
        "Nová nota se objeví právě jednou.",
        "Neplatná cell (buňka) zachová členství; prázdný Set (množina) vytvoří tiché zvukové vzorky."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.17: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P17_TinyStepSequencer");
            Reflect.call(p, "reset");var original=Set.copyOf(((java.util.Set<Object>) Reflect.get(p, "notes")));Reflect.call(p, "toggle", 0, 1);Reflect.call(p, "toggle", 0, 1);
            check(((java.util.Set<Object>) Reflect.get(p, "notes")).equals(original),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            ((java.util.Set<Object>) Reflect.get(p, "notes")).clear();check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).contains(Reflect.create("P17_TinyStepSequencer$Note", 2, 4)),1,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(((boolean) Reflect.call(p, "toggle", 2, 4))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),0,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(!((boolean) Reflect.call(p, "toggle", -1, 0))&&!((boolean) Reflect.call(p, "toggle", 4, 0))&&!((boolean) Reflect.call(p, "toggle", 0, 8))&&((java.util.Set<Object>) Reflect.get(p, "notes")).isEmpty(),2,((java.util.Set<Object>) Reflect.get(p, "notes")));
            check(Arrays.stream(((double[]) Reflect.call(p, "sequence", 1))).allMatch(v->v==0),2,"silence");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 10.17: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P18_CirclePackingTest {
    static int checks;
    static final String[] CASES = {
        "První střed se uloží a přijme.",
        "Stejný střed se odmítne; prohozené coordinate (souřadnice) jsou jiná hodnota.",
        "Skutečná zahrada dál umístí osm různých bublin."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.18: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P18_CirclePacking");
            Set<Object> seen=new HashSet<>();
            check(((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,0,seen);
            check(!((boolean) Reflect.call(p, "claimCentre", seen, 4, 7))&&seen.size()==1,1,seen);
            check(((boolean) Reflect.call(p, "claimCentre", seen, 7, 4))&&seen.size()==2,1,seen);
            var bubbles=((java.util.List<Object>) Reflect.call(p, "pack", 42, 8));check(bubbles.size()==8,2,bubbles.size());
            check(bubbles.stream().map(b->Reflect.create("P18_CirclePacking$Centre", (int)((double) Reflect.call(b, "x")), (int)((double) Reflect.call(b, "y")))).distinct().count()==8,2,bubbles);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 10.18: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P19_WireframeCubeTest {
    static int checks;
    static final String[] CASES = {
        "Stejné a obrácené dvojice se sloučí na dvě různé hrany.",
        "Zbývající edge key (klíče hran) mají pořadí menší/větší.",
        "Neplatná data, smyčky i prázdný vstup jsou bezpečné; skutečná krychle má dvanáct hran."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.19: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P19_WireframeCube");
            var edges=((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{0,1},{1,0},{0,1},{1,2}}));
            check(edges.size()==2,0,edges);
            check(edges.contains(Reflect.create("P19_WireframeCube$Edge", 0, 1))&&edges.contains(Reflect.create("P19_WireframeCube$Edge", 1, 2)),1,edges);
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[][]{{-1,0},{0,8},{2,2}})).isEmpty(),2,"invalid");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) new int[0][2])).isEmpty(),2,"empty");
            check(((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size()==12,0,((java.util.Set<Object>) Reflect.call(p, "uniqueEdges", (Object) ((int[][]) Reflect.get(p, "edges")))).size());
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 10.19: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P22_TruchetTileLabTest {
    static int checks;
    static final String[] CASES = {
        "První přijatá orientace se vrátí a započte.",
        "Preference s náskokem dvou se přesměruje na druhou orientaci.",
        "Skutečné oblouky používají zvolenou orientaci; neznámý vstup nic nemění."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.22: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P22_TruchetTileLab");
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
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P23_MarkovMischiefTest {
    static int checks;
    static final String[] CASES = {
        "Opakované moon zachová cat, dog, cat v tomto pořadí.",
        "Existující List (seznamy) následníků přežijí další výskyty.",
        "Jediné slovo má prázdný List a prázdný text se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.23: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P23_MarkovMischief");
            var links=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "moon cat moon dog moon cat"));
            check(links.get("moon").equals(List.of("cat","dog","cat")),0,links);
            check(links.containsKey("cat")&&links.get("cat").equals(List.of("moon")),1,links);
            var single=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "solo"));check(single.containsKey("solo")&&single.get("solo").isEmpty(),2,single);
            reject(()->Reflect.call(p, "train", "   "),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 10.23: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P24_DungeonBlueprintsTest {
    static int checks;
    static final String[] CASES = {
        "Plocha 42 patří do SMALL; první tři místnosti se přijmou.",
        "Plné limity SMALL/LARGE odmítají beze změny Map.",
        "Skutečně vytvořené místnosti dodržují oba limity kategorií."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 10.24: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P24_DungeonBlueprints");
        var counts = (Map<String, Integer>) Reflect.get(p, "roomCounts");
        Object small = Reflect.create("P24_DungeonBlueprints$Room", 1, 1, 6, 7);
        Object large = Reflect.create("P24_DungeonBlueprints$Room", 1, 1, 7, 7);
        check((boolean) Reflect.call(p, "admitRoom", small), 0, counts);
        check((boolean) Reflect.call(p, "admitRoom", small) && (boolean) Reflect.call(p, "admitRoom", small), 0, counts);
        check(!(boolean) Reflect.call(p, "admitRoom", small) && counts.get("SMALL") == 3, 1, counts);
        check((boolean) Reflect.call(p, "admitRoom", large) && (boolean) Reflect.call(p, "admitRoom", large), 1, counts);
        var before = new HashMap<>(counts);
        check(!(boolean) Reflect.call(p, "admitRoom", large) && counts.equals(before), 1, counts);
        Reflect.call(p, "generate", 42, 8);
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        check(total > 0 && total <= 5 && counts.getOrDefault("SMALL", 0) <= 3 && counts.getOrDefault("LARGE", 0) <= 2, 2, counts);
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P27_PhotoRemixLabTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P27_PhotoRemixLab");
        var counts = (Map<Integer, Integer>) Reflect.get(g, "colourCounts");
        counts.put(-5, 2); counts.put(-7, 4); counts.put(-9, 2); counts.put(-11, 0);
        var before = new HashMap<>(counts);
        equal(-9, Reflect.call(g, "spotlightColour"), "10.27: the rarest positive count wins, with smaller RGB for ties");
        equal(before, counts, "10.27: examining the histogram does not rewrite it");
        counts.clear(); equal(0, Reflect.call(g, "spotlightColour"), "10.27: empty histogram has neutral result");
        counts.put(7, Integer.MAX_VALUE); equal(7, Reflect.call(g, "spotlightColour"), "10.27: any positive frequency is eligible");
        Reflect.call(g, "restart");
        var result = (java.awt.image.BufferedImage) Reflect.get(g, "result");
        equal(java.awt.Color.CYAN.getRGB(), result.getRGB(35, 40), "10.27: the hidden key is visibly revealed cyan");
        expect(result.getRGB(200, 150) != java.awt.Color.CYAN.getRGB(), "10.27: the common moon no longer steals the spotlight");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P30_NonogramTest {
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
        var g = Reflect.create("P30_Nonogram");
        equal(new int[]{3}, ((int[]) Reflect.call(g, "runs", new int[]{0, 1, 1, 1})), "10.30/F17: flush the run at the end of the array");
        equal(new int[]{2, 1}, ((int[]) Reflect.call(g, "runs", new int[]{1, 1, 0, 1})), "10.30/F17: separate groups remain separate");
        equal(new int[]{}, ((int[]) Reflect.call(g, "runs", new int[]{0, 0, 0})), "10.30/F17: blank line has no runs");
        equal(new int[]{1}, ((int[]) Reflect.call(g, "runs", new int[]{1})), "10.30/F17: single-cell run");
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
