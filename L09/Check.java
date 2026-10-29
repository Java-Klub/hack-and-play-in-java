import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_WordLadder.java`
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
        run("M01_WordLadder", "L09.01 WordLadder · F22", () -> M01_WordLadderTest.main(new String[0]));
        run("M02_GapHopper", "L09.02 GapHopper · F22", () -> M02_GapHopperTest.main(new String[0]));
        run("M03_FlipbookStudio", "L09.03 FlipbookStudio · F22", () -> M03_FlipbookStudioTest.main(new String[0]));
        run("M04_VoronoiStainedGlass", "L09.04 VoronoiStainedGlass · F22", () -> M04_VoronoiStainedGlassTest.main(new String[0]));
        run("M05_PhotoRemixLab", "L09.05 PhotoRemixLab · F24", () -> M05_PhotoRemixLabTest.main(new String[0]));
        run("M06_TruchetTileLab", "L09.06 TruchetTileLab · F24", () -> M06_TruchetTileLabTest.main(new String[0]));
        run("M07_MarkovMischief", "L09.07 MarkovMischief · F24", () -> M07_MarkovMischiefTest.main(new String[0]));
        run("M08_DungeonBlueprints", "L09.08 DungeonBlueprints · F24", () -> M08_DungeonBlueprintsTest.main(new String[0]));
        run("M09_FractalTreeGarden", "L09.09 FractalTreeGarden · F31", () -> M09_FractalTreeGardenTest.main(new String[0]));
        run("M10_ImageToASCII", "L09.10 ImageToASCII · F31", () -> M10_ImageToASCIITest.main(new String[0]));
        run("M11_MazePostcards", "L09.11 MazePostcards · F31", () -> M11_MazePostcardsTest.main(new String[0]));
        run("M12_TileStampStudio", "L09.12 TileStampStudio · F31", () -> M12_TileStampStudioTest.main(new String[0]));
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
class M01_WordLadderTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M01_WordLadder");
        Reflect.call(g, "reset");
        Reflect.call(g, "command", " cord  card ");
        equal(List.of("COLD", "CORD", "CARD"), Reflect.get(g, "history"), "L09.01: a planned route preserves every stop");
        Object before = List.copyOf((List<?>) Reflect.get(g, "history"));
        boolean rejected = false;
        try { Reflect.call(g, "command", "WARD COLD"); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected, "L09.01: an invalid later step refuses the whole route");
        equal(before, Reflect.get(g, "history"), "L09.01: refused route keeps its original history");
        equal("CARD", Reflect.get(g, "current"), "L09.01: refused route keeps its current word");
        Reflect.call(g, "command", "WARD WARM");
        equal("Reached WARM in 4 moves!", Reflect.get(g, "outcome"), "L09.01: winning batch counts moves, not batches");
        Reflect.call(g, "reset");
        rejected = false;
        try { Reflect.call(g, "command", "CORD CARD WARD WARM WARD"); } catch (IllegalArgumentException invalid) { rejected = true; }
        expect(rejected && ((List<?>) Reflect.get(g, "history")).equals(List.of("COLD")), "L09.01: nothing may continue past the target in one batch");
        Reflect.call(g, "command", "CORD");
        equal("CORD", Reflect.get(g, "current"), "L09.01: a single ordinary move still works");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_GapHopperTest {
    static int checks;
    static final String[] CASES = {
        "Prošlý object (objekt) zmizí a jeho následník zůstane.",
        "Nová překážka vznikne za poslední; neprošlé překážky se neobnovují.",
        "Prázdný List (seznam) je bezpečný."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.02: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M02_GapHopper");
            Reflect.call(p, "restart"); var old = ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst(); var next = ((java.util.List<Object>) Reflect.get(p, "pipes")).getLast();
            Reflect.set(old, "x", -60); Reflect.set(next, "x", 200); Reflect.call(p, "recyclePipes");
            check(((java.util.List<Object>) Reflect.get(p, "pipes")).size()==2 && ((java.util.List<Object>) Reflect.get(p, "pipes")).getFirst()==next && !((java.util.List<Object>) Reflect.get(p, "pipes")).contains(old),0,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            check(((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x"))==460 && !((boolean) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "scored")),1,((double) Reflect.get(((java.util.List<Object>) Reflect.get(p, "pipes")).getLast(), "x")));
            var stable=List.copyOf(((java.util.List<Object>) Reflect.get(p, "pipes")));Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).equals(stable),1,((java.util.List<Object>) Reflect.get(p, "pipes")).size());
            ((java.util.List<Object>) Reflect.get(p, "pipes")).clear();Reflect.call(p, "recyclePipes");check(((java.util.List<Object>) Reflect.get(p, "pipes")).isEmpty(),2,((java.util.List<Object>) Reflect.get(p, "pipes")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L09.02: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_FlipbookStudioTest {
    static int checks;
    static final String[] CASES = {
        "Dvě undo obnoví poslední dvě barvy v opačném pořadí.",
        "Undo s prázdnou historií vrátí false.",
        "Undo míří na uložený snímek; restart historii smaže."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.03: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M03_FlipbookStudio");
            Reflect.call(p, "restart");Reflect.call(p, "paintCell", 2, 3, 1);Reflect.call(p, "paintCell", 2, 3, 4);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==1 && ((java.util.List<Object>) Reflect.get(p, "history")).size()==1,0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(((boolean) Reflect.call(p, "undo")) && ((int[][][]) Reflect.get(p, "frames"))[0][3][2]==0 && ((java.util.List<Object>) Reflect.get(p, "history")).isEmpty(),0,((int[][][]) Reflect.get(p, "frames"))[0][3][2]);
            check(!((boolean) Reflect.call(p, "undo")),1,((java.util.List<Object>) Reflect.get(p, "history")));
            Reflect.set(p, "edited", 2);Reflect.call(p, "paintCell", 1, 4, 3);Reflect.set(p, "edited", 0);Reflect.call(p, "undo");check(((int[][][]) Reflect.get(p, "frames"))[2][4][1]==0,2,((int[][][]) Reflect.get(p, "frames"))[2][4][1]);
            Reflect.call(p, "paintCell", 0, 0, 2);Reflect.call(p, "restart");check(((java.util.List<Object>) Reflect.get(p, "history")).isEmpty()&&!((boolean) Reflect.call(p, "undo")),2,((java.util.List<Object>) Reflect.get(p, "history")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L09.03: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_VoronoiStainedGlassTest {
    static int checks;
    static final String[] CASES = {
        "Tři zárodky přibudou v platných coordinate (souřadnicích).",
        "Vrácený List (seznam) umožní odstranění.",
        "Nula dá prázdný výsledek a záporný počet se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.04: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M04_VoronoiStainedGlass");
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
            throw new AssertionError("MISSION L09.04: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_PhotoRemixLabTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M05_PhotoRemixLab");
        var counts = (Map<Integer, Integer>) Reflect.get(g, "colourCounts");
        counts.put(-5, 2); counts.put(-7, 4); counts.put(-9, 2); counts.put(-11, 0);
        var before = new HashMap<>(counts);
        equal(-9, Reflect.call(g, "spotlightColour"), "L09.05: the rarest positive count wins, with smaller RGB for ties");
        equal(before, counts, "L09.05: examining the histogram does not rewrite it");
        counts.clear(); equal(0, Reflect.call(g, "spotlightColour"), "L09.05: empty histogram has neutral result");
        counts.put(7, Integer.MAX_VALUE); equal(7, Reflect.call(g, "spotlightColour"), "L09.05: any positive frequency is eligible");
        Reflect.call(g, "restart");
        var result = (java.awt.image.BufferedImage) Reflect.get(g, "result");
        equal(java.awt.Color.CYAN.getRGB(), result.getRGB(35, 40), "L09.05: the hidden key is visibly revealed cyan");
        expect(result.getRGB(200, 150) != java.awt.Color.CYAN.getRGB(), "L09.05: the common moon no longer steals the spotlight");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_TruchetTileLabTest {
    static int checks;
    static final String[] CASES = {
        "První přijatá orientace se vrátí a započte.",
        "Preference s náskokem dvou se přesměruje na druhou orientaci.",
        "Skutečné oblouky používají zvolenou orientaci; neznámý vstup nic nemění."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.06: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("M06_TruchetTileLab");
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
class M07_MarkovMischiefTest {
    static int checks;
    static final String[] CASES = {
        "Opakované moon zachová cat, dog, cat v tomto pořadí.",
        "Existující List (seznamy) následníků přežijí další výskyty.",
        "Jediné slovo má prázdný List a prázdný text se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.07: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M07_MarkovMischief");
            var links=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "moon cat moon dog moon cat"));
            check(links.get("moon").equals(List.of("cat","dog","cat")),0,links);
            check(links.containsKey("cat")&&links.get("cat").equals(List.of("moon")),1,links);
            var single=((java.util.Map<java.lang.String, java.util.List<java.lang.String>>) Reflect.call(p, "train", "solo"));check(single.containsKey("solo")&&single.get("solo").isEmpty(),2,single);
            reject(()->Reflect.call(p, "train", "   "),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L09.07: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_DungeonBlueprintsTest {
    static int checks;
    static final String[] CASES = {
        "Plocha 42 patří do SMALL; první tři místnosti se přijmou.",
        "Plné limity SMALL/LARGE odmítají beze změny Map.",
        "Skutečně vytvořené místnosti dodržují oba limity kategorií."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.08: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("M08_DungeonBlueprints");
        var counts = (Map<String, Integer>) Reflect.get(p, "roomCounts");
        Object small = Reflect.create("M08_DungeonBlueprints$Room", 1, 1, 6, 7);
        Object large = Reflect.create("M08_DungeonBlueprints$Room", 1, 1, 7, 7);
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
class M09_FractalTreeGardenTest {
    static int checks;
    static final String[] CASES = {
        "Nečíselný text vrátí 28.",
        "Obě zahrnuté hranice i okolní mezery fungují.",
        "Číselný input (vstup) mimo rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.09: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M09_FractalTreeGarden");
            check(((double) Reflect.call(p, "readAngle", "oops"))==28,0,((double) Reflect.call(p, "readAngle", "oops")));
            check(((double) Reflect.call(p, "readAngle", " 10 "))==10&&((double) Reflect.call(p, "readAngle", "70"))==70,1,"bounds");
            reject(()->Reflect.call(p, "readAngle", "9"),2);
            reject(()->Reflect.call(p, "readAngle", "71"),2);
            reject(()->Reflect.call(p, "readAngle", "NaN"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L09.09: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_ImageToASCIITest {
    static int checks;
    static final String[] CASES = {
        "Nečíselný text vrátí 80.",
        "Obě zahrnuté hranice i okolní mezery fungují.",
        "Číselný input (vstup) mimo rozsah se odmítne."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.10: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M10_ImageToASCII");
            check(((int) Reflect.call(p, "readWidth", "oops"))==80,0,((int) Reflect.call(p, "readWidth", "oops")));
            check(((int) Reflect.call(p, "readWidth", " 4 "))==4&&((int) Reflect.call(p, "readWidth", "160"))==160,1,"bounds");
            reject(()->Reflect.call(p, "readWidth", "3"),2);
            reject(()->Reflect.call(p, "readWidth", "161"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L09.10: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_MazePostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Nečíselný text dá bludiště s 20 sloupci.",
        "Obě zahrnuté hranice se přijmou.",
        "Celá čísla mimo rozsah se odmítnou."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION L09.11: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M11_MazePostcards");
            check(((int) Reflect.call(p, "readColumns", "oops"))==20,0,((int) Reflect.call(p, "readColumns", "oops")));
            check(((int) Reflect.call(p, "readColumns", " 2 "))==2&&((int) Reflect.call(p, "readColumns", "50"))==50,1,"limits");
            reject(()->Reflect.call(p, "readColumns", "1"),2);reject(()->Reflect.call(p, "readColumns", "51"),2);
            var maze=((boolean[][]) Reflect.call(p, "generate", 42, ((int) Reflect.call(p, "readColumns", "oops")), 2));check(maze[0].length==41,0,maze[0].length);
        check(((int) Reflect.call(p, "readColumns", " tiny ")) == 6 && ((int) Reflect.call(p, "readColumns", "WIDE")) == 40, 0, "named maze sizes");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION L09.11: Neočekávané selhání při kontrole aktivního pravidla.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M12_TileStampStudioTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("M12_TileStampStudio");
        expect((boolean) Reflect.call(g, "applyRepeats", "3"), "L09.12: ordinary numeric choice still works");
        expect((boolean) Reflect.call(g, "applyRepeats", " DENSER "), "L09.12: a relative command accepts whitespace and case");
        equal(6, Reflect.get(g, "repeats"), "L09.12: denser doubles the current setting");
        Reflect.call(g, "applyRepeats", "denser");
        equal(12, Reflect.get(g, "repeats"), "L09.12: a command sequence reaches twelve");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "denser") && (int) Reflect.get(g, "repeats") == 12, "L09.12: oversized relative choice preserves its setting");
        Reflect.call(g, "applyRepeats", "sparser"); equal(6, Reflect.get(g, "repeats"), "L09.12: sparser halves an even setting");
        Reflect.call(g, "applyRepeats", "3");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "sparser") && (int) Reflect.get(g, "repeats") == 3, "L09.12: odd halving is refused without rounding");
        expect(!(boolean) Reflect.call(g, "applyRepeats", "oops") && (int) Reflect.get(g, "repeats") == 3, "L09.12: typo preserves the live setting");
        Reflect.call(g, "paint", 0, 0, 1);
        var three = (java.awt.image.BufferedImage) Reflect.call(g, "render", 3);
        var six = (java.awt.image.BufferedImage) Reflect.call(g, "render", 6);
        expect(three.getWidth() == 288 && six.getWidth() == 288 && three.getRGB(6, 0) != six.getRGB(6, 0), "L09.12: relative choices have visible density feedback");
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
