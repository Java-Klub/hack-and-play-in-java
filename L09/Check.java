import module java.base;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_WordLadder.java`
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
        IO.println(checked + " missions; " + failed + " still need repair.");
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
    static final String[] CASES = {
        "Two accepted moves keep all three route words in order.",
        "A rejected move leaves the route unchanged.",
        "Winning history and restart remain correct."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I026: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M01_WordLadder");
            Reflect.call(p, "reset"); Reflect.call(p, "command", "CORD"); Reflect.call(p, "command", "CARD");
            check(((java.util.List<java.lang.String>) Reflect.get(p, "history")).equals(List.of("COLD","CORD","CARD")),0,((java.util.List<java.lang.String>) Reflect.get(p, "history")));
            var before = List.copyOf(((java.util.List<java.lang.String>) Reflect.get(p, "history")));
            reject(() -> Reflect.call(p, "command", "WARM"),1);
            check(((java.util.List<java.lang.String>) Reflect.get(p, "history")).equals(before),1,((java.util.List<java.lang.String>) Reflect.get(p, "history")));
            Reflect.call(p, "command", "WARD"); Reflect.call(p, "command", "WARM");
            check(((java.util.List<java.lang.String>) Reflect.get(p, "history")).size()==5 && !((java.lang.String) Reflect.get(p, "outcome")).isEmpty(),2,((java.util.List<java.lang.String>) Reflect.get(p, "history")));
            Reflect.call(p, "reset"); check(((java.util.List<java.lang.String>) Reflect.get(p, "history")).equals(List.of("COLD")),2,((java.util.List<java.lang.String>) Reflect.get(p, "history")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I026: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_GapHopperTest {
    static int checks;
    static final String[] CASES = {
        "The expired object is removed and its successor survives.",
        "The new obstacle is appended beyond the last one; unchanged obstacles are not recycled.",
        "An empty list is safe."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I052: " + CASES[example] + " | actual=" + actual);
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
            throw new AssertionError("MISSION I052: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_FlipbookStudioTest {
    static int checks;
    static final String[] CASES = {
        "Two undos restore two latest colours in reverse order.",
        "Undo on empty history returns false.",
        "Undo targets the recorded frame; restart clears history."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I080: " + CASES[example] + " | actual=" + actual);
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
            throw new AssertionError("MISSION I080: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_VoronoiStainedGlassTest {
    static int checks;
    static final String[] CASES = {
        "Three seeds are appended in legal coordinate ranges.",
        "The returned List supports removal.",
        "Zero is empty and negative counts are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I112: " + CASES[example] + " | actual=" + actual);
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
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I112: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_PhotoRemixLabTest {
    static int checks;
    static final String[] CASES = {
        "Repeated colour keys accumulate instead of overwriting.",
        "A real two-pixel remix counts both occurrences.",
        "Remixing a new source removes stale keys."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I079: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M05_PhotoRemixLab");
            Reflect.call(p, "countColour", 0x123456);Reflect.call(p, "countColour", 0x123456);Reflect.call(p, "countColour", 0xABCDEF);
            check(((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")).get(0x123456)==2&&((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")).get(0xABCDEF)==1,0,((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")));
            Reflect.set(p, "source", new BufferedImage(2,1,BufferedImage.TYPE_INT_RGB));((java.awt.image.BufferedImage) Reflect.get(p, "source")).setRGB(0,0,0x112233);((java.awt.image.BufferedImage) Reflect.get(p, "source")).setRGB(1,0,0x112233);Reflect.call(p, "remix");
            check(((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")).size()==1&&((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")).values().iterator().next()==2,1,((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")));
            Reflect.set(p, "source", new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB));((java.awt.image.BufferedImage) Reflect.get(p, "source")).setRGB(0,0,0xFFFFFF);Reflect.call(p, "remix");
            check(((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")).size()==1&&((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")).values().iterator().next()==1,2,((java.util.Map<java.lang.Integer, java.lang.Integer>) Reflect.get(p, "colourCounts")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I079: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_TruchetTileLabTest {
    static int checks;
    static final String[] CASES = {
        "Repeated LEFT tiles increase only the LEFT count.",
        "A real 4×4 render records sixteen tiles.",
        "A later 2×2 render resets the total to four."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I109: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M06_TruchetTileLab");
            Reflect.call(p, "countOrientation", "LEFT");Reflect.call(p, "countOrientation", "RIGHT");Reflect.call(p, "countOrientation", "LEFT");
            check(((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "orientationCounts")).equals(Map.of("LEFT",2,"RIGHT",1)),0,((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "orientationCounts")));
            Reflect.call(p, "render", 42, 4);check(((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "orientationCounts")).values().stream().mapToInt(Integer::intValue).sum()==16,1,((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "orientationCounts")));
            Reflect.call(p, "render", 42, 2);check(((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "orientationCounts")).values().stream().mapToInt(Integer::intValue).sum()==4,2,((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "orientationCounts")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I109: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_MarkovMischiefTest {
    static int checks;
    static final String[] CASES = {
        "Repeated moon keeps cat, dog, cat in that order.",
        "Existing follower lists survive later occurrences.",
        "A single word has an empty list and blank corpora are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I130: " + CASES[example] + " | actual=" + actual);
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
            throw new AssertionError("MISSION I130: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_DungeonBlueprintsTest {
    static int checks;
    static final String[] CASES = {
        "Area 42 is SMALL and repeated kinds accumulate.",
        "The real generated dungeon counts only accepted rooms.",
        "Generating another dungeon clears old totals."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I141: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M08_DungeonBlueprints");
            Reflect.call(p, "countRoom", Reflect.create("M08_DungeonBlueprints$Room", 1, 1, 6, 7));Reflect.call(p, "countRoom", Reflect.create("M08_DungeonBlueprints$Room", 1, 1, 4, 4));Reflect.call(p, "countRoom", Reflect.create("M08_DungeonBlueprints$Room", 1, 1, 7, 7));
            check(((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "roomCounts")).equals(Map.of("SMALL",2,"LARGE",1)),0,((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "roomCounts")));
            Reflect.call(p, "generate", 42, 8);int total=((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "roomCounts")).values().stream().mapToInt(Integer::intValue).sum();
            check(total>=2&&total<=8,1,((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "roomCounts")));
            Reflect.call(p, "generate", 42, 2);check(((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "roomCounts")).values().stream().mapToInt(Integer::intValue).sum()==2,2,((java.util.Map<java.lang.String, java.lang.Integer>) Reflect.get(p, "roomCounts")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I141: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_FractalTreeGardenTest {
    static int checks;
    static final String[] CASES = {
        "Malformed numeric text returns 28.",
        "Both inclusive bounds and surrounding spaces work.",
        "Out-of-range numeric input is rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I106: " + CASES[example] + " | actual=" + actual);
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
            throw new AssertionError("MISSION I106: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_ImageToASCIITest {
    static int checks;
    static final String[] CASES = {
        "Malformed numeric text returns 80.",
        "Both inclusive bounds and surrounding spaces work.",
        "Out-of-range numeric input is rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I114: " + CASES[example] + " | actual=" + actual);
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
            throw new AssertionError("MISSION I114: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_MazePostcardsTest {
    static int checks;
    static final String[] CASES = {
        "Malformed text gives the 20-column maze.",
        "Both inclusive bounds are accepted.",
        "Out-of-range integers are rejected."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I137: " + CASES[example] + " | actual=" + actual);
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
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I137: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M12_TileStampStudioTest {
    static int checks;
    static final String[] CASES = {
        "A valid typed value changes repeats.",
        "Malformed and out-of-range values preserve the old value.",
        "Bounds 1 and 12 change the real rendered dimensions."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION I142: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("M12_TileStampStudio");
            check(((boolean) Reflect.call(p, "applyRepeats", " 3 "))&&((int) Reflect.get(p, "repeats"))==3,0,((int) Reflect.get(p, "repeats")));
            check(!((boolean) Reflect.call(p, "applyRepeats", "banana"))&&((int) Reflect.get(p, "repeats"))==3,1,((int) Reflect.get(p, "repeats")));
            check(!((boolean) Reflect.call(p, "applyRepeats", "0"))&&!((boolean) Reflect.call(p, "applyRepeats", "13"))&&((int) Reflect.get(p, "repeats"))==3,1,((int) Reflect.get(p, "repeats")));
            check(((boolean) Reflect.call(p, "applyRepeats", "1"))&&((java.awt.image.BufferedImage) Reflect.call(p, "render", ((int) Reflect.get(p, "repeats")))).getWidth()==48,2,((int) Reflect.get(p, "repeats")));
            check(((boolean) Reflect.call(p, "applyRepeats", "12"))&&((java.awt.image.BufferedImage) Reflect.call(p, "render", ((int) Reflect.get(p, "repeats")))).getWidth()==576,2,((int) Reflect.get(p, "repeats")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I142: Unexpected failure while checking the active rule.", failure);
        }
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
