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
            var p = new M01_WordLadder();
            p.reset(); p.command("CORD"); p.command("CARD");
            check(p.history.equals(List.of("COLD","CORD","CARD")),0,p.history);
            var before = List.copyOf(p.history);
            reject(() -> p.command("WARM"),1);
            check(p.history.equals(before),1,p.history);
            p.command("WARD"); p.command("WARM");
            check(p.history.size()==5 && !p.outcome.isEmpty(),2,p.history);
            p.reset(); check(p.history.equals(List.of("COLD")),2,p.history);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I026: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M02_GapHopper();
            p.restart(); var old = p.pipes.getFirst(); var next = p.pipes.getLast();
            old.x = -60; next.x = 200; p.recyclePipes();
            check(p.pipes.size()==2 && p.pipes.getFirst()==next && !p.pipes.contains(old),0,p.pipes.size());
            check(p.pipes.getLast().x==460 && !p.pipes.getLast().scored,1,p.pipes.getLast().x);
            var stable=List.copyOf(p.pipes);p.recyclePipes();check(p.pipes.equals(stable),1,p.pipes.size());
            p.pipes.clear();p.recyclePipes();check(p.pipes.isEmpty(),2,p.pipes);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I052: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M03_FlipbookStudio();
            p.restart();p.paintCell(2,3,1);p.paintCell(2,3,4);
            check(p.undo() && p.frames[0][3][2]==1 && p.history.size()==1,0,p.frames[0][3][2]);
            check(p.undo() && p.frames[0][3][2]==0 && p.history.isEmpty(),0,p.frames[0][3][2]);
            check(!p.undo(),1,p.history);
            p.edited=2;p.paintCell(1,4,3);p.edited=0;p.undo();check(p.frames[2][4][1]==0,2,p.frames[2][4][1]);
            p.paintCell(0,0,2);p.restart();check(p.history.isEmpty()&&!p.undo(),2,p.history);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I080: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M04_VoronoiStainedGlass();
            var seeds=p.seedList(new Random(42),3);
            check(seeds.size()==3,0,seeds.size());
            var first=seeds.getFirst();check(first.x()>=0&&first.x()<400&&first.y()>=0&&first.y()<300,0,first);
            seeds.removeLast();check(seeds.size()==2,1,seeds.size());
            check(p.seedList(new Random(42),0).isEmpty(),2,"empty");
            reject(()->p.seedList(new Random(42),-1),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I112: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M05_PhotoRemixLab();
            p.countColour(0x123456);p.countColour(0x123456);p.countColour(0xABCDEF);
            check(p.colourCounts.get(0x123456)==2&&p.colourCounts.get(0xABCDEF)==1,0,p.colourCounts);
            p.source=new BufferedImage(2,1,BufferedImage.TYPE_INT_RGB);p.source.setRGB(0,0,0x112233);p.source.setRGB(1,0,0x112233);p.remix();
            check(p.colourCounts.size()==1&&p.colourCounts.values().iterator().next()==2,1,p.colourCounts);
            p.source=new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);p.source.setRGB(0,0,0xFFFFFF);p.remix();
            check(p.colourCounts.size()==1&&p.colourCounts.values().iterator().next()==1,2,p.colourCounts);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I079: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M06_TruchetTileLab();
            p.countOrientation("LEFT");p.countOrientation("RIGHT");p.countOrientation("LEFT");
            check(p.orientationCounts.equals(Map.of("LEFT",2,"RIGHT",1)),0,p.orientationCounts);
            p.render(42,4);check(p.orientationCounts.values().stream().mapToInt(Integer::intValue).sum()==16,1,p.orientationCounts);
            p.render(42,2);check(p.orientationCounts.values().stream().mapToInt(Integer::intValue).sum()==4,2,p.orientationCounts);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I109: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M07_MarkovMischief();
            var links=p.train("moon cat moon dog moon cat");
            check(links.get("moon").equals(List.of("cat","dog","cat")),0,links);
            check(links.containsKey("cat")&&links.get("cat").equals(List.of("moon")),1,links);
            var single=p.train("solo");check(single.containsKey("solo")&&single.get("solo").isEmpty(),2,single);
            reject(()->p.train("   "),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I130: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M08_DungeonBlueprints();
            p.countRoom(new M08_DungeonBlueprints.Room(1,1,6,7));p.countRoom(new M08_DungeonBlueprints.Room(1,1,4,4));p.countRoom(new M08_DungeonBlueprints.Room(1,1,7,7));
            check(p.roomCounts.equals(Map.of("SMALL",2,"LARGE",1)),0,p.roomCounts);
            p.generate(42,8);int total=p.roomCounts.values().stream().mapToInt(Integer::intValue).sum();
            check(total>=2&&total<=8,1,p.roomCounts);
            p.generate(42,2);check(p.roomCounts.values().stream().mapToInt(Integer::intValue).sum()==2,2,p.roomCounts);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I141: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M09_FractalTreeGarden();
            check(p.readAngle("oops")==28,0,p.readAngle("oops"));
            check(p.readAngle(" 10 ")==10&&p.readAngle("70")==70,1,"bounds");
            reject(()->p.readAngle("9"),2);
            reject(()->p.readAngle("71"),2);
            reject(()->p.readAngle("NaN"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I106: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M10_ImageToASCII();
            check(p.readWidth("oops")==80,0,p.readWidth("oops"));
            check(p.readWidth(" 4 ")==4&&p.readWidth("160")==160,1,"bounds");
            reject(()->p.readWidth("3"),2);
            reject(()->p.readWidth("161"),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I114: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M11_MazePostcards();
            check(p.readColumns("oops")==20,0,p.readColumns("oops"));
            check(p.readColumns(" 2 ")==2&&p.readColumns("50")==50,1,"limits");
            reject(()->p.readColumns("1"),2);reject(()->p.readColumns("51"),2);
            var maze=p.generate(42,p.readColumns("oops"),2);check(maze[0].length==41,0,maze[0].length);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I137: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

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
            var p = new M12_TileStampStudio();
            check(p.applyRepeats(" 3 ")&&p.repeats==3,0,p.repeats);
            check(!p.applyRepeats("banana")&&p.repeats==3,1,p.repeats);
            check(!p.applyRepeats("0")&&!p.applyRepeats("13")&&p.repeats==3,1,p.repeats);
            check(p.applyRepeats("1")&&p.render(p.repeats).getWidth()==48,2,p.repeats);
            check(p.applyRepeats("12")&&p.render(p.repeats).getWidth()==576,2,p.repeats);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION I142: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}
