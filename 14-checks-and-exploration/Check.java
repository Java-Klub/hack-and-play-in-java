import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.awt.Point;
import javax.imageio.ImageIO;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_Hanoi.java
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
        run("P03_Hanoi", "14.03 Hanoi · F08", () -> P03_HanoiTest.main(new String[0]));
        run("P04_LightsOut", "14.04 LightsOut · F33", () -> P04_LightsOutTest.main(new String[0]));
        run("P05_Tents", "14.05 Tents · F33", () -> P05_TentsTest.main(new String[0]));
        run("P06_PegSolitaire", "14.06 PegSolitaire · F33", () -> P06_PegSolitaireTest.main(new String[0]));
        run("P07_Merge2048", "14.07 Merge2048 · F33", () -> P07_Merge2048Test.main(new String[0]));
        run("P10_ReactionDiffusionSkins", "14.10 ReactionDiffusionSkins · F34", () -> P10_ReactionDiffusionSkinsTest.main(new String[0]));
        run("P11_FlockPlayground", "14.11 FlockPlayground · F34", () -> P11_FlockPlaygroundTest.main(new String[0]));
        run("P12_PhasingRhythms", "14.12 PhasingRhythms · F34", () -> P12_PhasingRhythmsTest.main(new String[0]));
        run("P13_PendulumPainter", "14.13 PendulumPainter · F34", () -> P13_PendulumPainterTest.main(new String[0]));
        run("P16_KnightTour", "14.16 KnightTour · F35", () -> P16_KnightTourTest.main(new String[0]));
        run("P17_CipherPostcards", "14.17 CipherPostcards · F35", () -> P17_CipherPostcardsTest.main(new String[0]));
        run("P18_RetroSoundForge", "14.18 RetroSoundForge · F35", () -> P18_RetroSoundForgeTest.main(new String[0]));
        run("P19_FireflySynchrony", "14.19 FireflySynchrony · F35", () -> P19_FireflySynchronyTest.main(new String[0]));
        run("P22_CrystalGrowth", "14.22 CrystalGrowth · F36", () -> P22_CrystalGrowthTest.main(new String[0]));
        run("P23_MountainPostcards", "14.23 MountainPostcards · F36", () -> P23_MountainPostcardsTest.main(new String[0]));
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
class P03_HanoiTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P03_Hanoi");
        Reflect.call(p,"reset");
        String[] lines=((String)Reflect.call(p,"demonstration")).strip().split("\\R");
        check(lines.length==2,"The shortest witness contains two attempts, got "+lines.length);
        for(int i=0;i<2;i++) {
            String[] pair=lines[i].strip().toUpperCase(Locale.ROOT).split("\\s+");
            check(pair.length==2 && pair[0].matches("[ABC]") && pair[1].matches("[ABC]"),"Use real peg commands, not invalid syntax.");
            int from=pair[0].charAt(0)-'A',to=pair[1].charAt(0)-'A';
            int[] sizes=((int[])Reflect.get(p,"sizes"));
            int[][] pegs=((int[][])Reflect.get(p,"pegs"));
            check(from!=to && sizes[from]>0,"Each attempt needs a real movable top disc.");
            if(i==1)check(sizes[to]>0 && pegs[from][sizes[from]-1]>pegs[to][sizes[to]-1],"The second attempt must expose the larger-on-smaller rule.");
            int[][] before=Arrays.stream(pegs).map(int[]::clone).toArray(int[][]::new);
            boolean moved=(boolean)Reflect.call(p,"move",from,to);
            check(moved==(i==0),"Only the first attempt is accepted.");
            if(i==1)check(Arrays.deepEquals(before,(int[][])Reflect.get(p,"pegs")),"Rejection preserves both towers.");
        }
        check((int)Reflect.get(p,"moves")==1,"Rejected evidence is not counted as a move.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_LightsOutTest {
    static int checks;

    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError("14.04/F33: " + message);
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
        var game = Reflect.create("P04_LightsOut");
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
class P05_TentsTest {
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
        var g = Reflect.create("P05_Tents");
        Reflect.call(g, "restart");
        var cases = ((Object[]) Reflect.call(g, "learnerChecks"));
        for (var e : cases) {
            equal(((boolean) Reflect.call(e, "expected")), ((boolean) Reflect.call(g, "treeAt", ((int) Reflect.call(e, "x")), ((int) Reflect.call(e, "y")))), "14.05/F33: expectations must accept actual tree positions");
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
            expect(rejected, "14.05/F33: your examples still accept this wrong rule: " + names[mutant]);
        }
        Reflect.call(g, "verifyExamples");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P06_PegSolitaireTest {
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
        var g = Reflect.create("P06_PegSolitaire");
        Reflect.call(g, "reset");
        var cases = ((Object[]) Reflect.call(g, "learnerChecks"));
        for (var e : cases) {
            equal(((boolean) Reflect.call(e, "expected")), ((boolean) Reflect.call(g, "legal", ((int) Reflect.call(e, "row")), ((int) Reflect.call(e, "column")), ((int) Reflect.call(e, "toRow")), ((int) Reflect.call(e, "toColumn")))), "14.06/F33: expectations must accept actual legal()");
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
            expect(rejected, "14.06/F33: your examples still accept this wrong rule: " + names[mutant]);
        }
        Reflect.call(g, "verifyExamples");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_Merge2048Test {
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
        var g = Reflect.create("P07_Merge2048");
        var cases = ((Object[]) Reflect.call(g, "learnerChecks"));
        for (var e : cases) {
            expect(((int[]) Reflect.call(e, "input")).length == 4 && ((int[]) Reflect.call(e, "expected")).length == 4 && ((int) Reflect.call(e, "score")) >= 0, "14.07/F33: examples use four-cell rows and nonnegative scores");
            for (int value : ((int[]) Reflect.call(e, "input"))) {
                expect(value == 0 || value >= 2 && value <= 2048 && (value&(value - 1)) == 0, "14.07/F33: example inputs must be legal 2048 tile values");
            }
            Reflect.set(g, "score", 0);
            equal(((int[]) Reflect.call(e, "expected")), ((int[]) Reflect.call(g, "merge", ((int[]) Reflect.call(e, "input")).clone())), "14.07/F33: real merge output");
            equal(((int) Reflect.call(e, "score")), ((int) Reflect.get(g, "score")), "14.07/F33: real merge score");
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
            expect(rejected, "14.07/F33: your examples still accept this wrong rule: " + names[mutant]);
        }
        Reflect.call(g, "verifyExamples");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P10_ReactionDiffusionSkinsTest {
    static int checks;
    static final String[] CASES = {
        "The experiment uses the three specified recipes with equal seed and duration.",
        "Swapping the role changes the actual images rather than only labels.",
        "The original role is restored on success and failure."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.10: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P10_ReactionDiffusionSkins");
        Object originalRole = Reflect.function("P10_ReactionDiffusionSkins$Chemistry", values -> .03);
        Reflect.set(p, "chemistry", originalRole);
        BufferedImage[] pictures = (BufferedImage[]) Reflect.call(p, "compareSkins", 37L, 60);
        check(pictures.length == 3, 0, pictures.length);
        var expected = Reflect.create("P10_ReactionDiffusionSkins");
        for (int i = 0; i < 3; i++) {
            final int recipe = i;
            Reflect.set(expected, "chemistry", Reflect.function("P10_ReactionDiffusionSkins$Chemistry", values -> {
                double a = (double) values[0];
                double b = (double) values[1];
                return recipe == 0 ? a * b * b : recipe == 1 ? .5 * a * b * b : 0;
            }));
            check(hash(pictures[i]) == hash((BufferedImage) Reflect.call(expected, "render", 37L, 60.0)), 0, "recipe " + i);
        }
        check(hash(pictures[0]) != hash(pictures[1]) && hash(pictures[1]) != hash(pictures[2]), 1, "three observable results");
        check(Reflect.get(p, "chemistry") == originalRole, 2, "restored after success");
        reject(() -> Reflect.call(p, "compareSkins", 37L, 0), 2);
        check(Reflect.get(p, "chemistry") == originalRole, 2, "restored after failure");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_FlockPlaygroundTest {
    static int checks;
    static final String[] CASES = {
        "Curious uses the exact negative of the Shy response.",
        "Switching preserves bird state, pause and the obstacle; switching twice restores Shy.",
        "The actual movement observes the role and a new flock preserves its selected personality."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.11: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P11_FlockPlayground");
        Reflect.call(p, "reset", 42L);
        double[] oldX = ((double[]) Reflect.get(p, "x")).clone();
        double[] oldY = ((double[]) Reflect.get(p, "y")).clone();
        double[] oldVx = ((double[]) Reflect.get(p, "vx")).clone();
        double[] oldVy = ((double[]) Reflect.get(p, "vy")).clone();
        Reflect.set(p, "running", false);
        Reflect.set(p, "obstacleX", 100.0);
        Reflect.call(p, "changePersonality");
        check(Math.abs((double) Reflect.call(p, "obstaclePush", 37.5) + .1) < 1e-12, 0, Reflect.call(p, "obstaclePush", 37.5));
        check((double) Reflect.call(p, "obstaclePush", 75.0) == 0, 0, "same radius boundary");
        check(Arrays.equals(oldX,(double[])Reflect.get(p,"x")) && Arrays.equals(oldY,(double[])Reflect.get(p,"y")) && Arrays.equals(oldVx,(double[])Reflect.get(p,"vx")) && Arrays.equals(oldVy,(double[])Reflect.get(p,"vy")), 1, "all bird arrays preserved");
        check(!(boolean)Reflect.get(p,"running") && (double)Reflect.get(p,"obstacleX") == 100, 1, "pause and obstacle preserved");
        Reflect.call(p, "reset", 23L);
        check((boolean)Reflect.get(p,"curious") && (double)Reflect.call(p,"obstaclePush",37.5) < 0, 2, "new flock, same personality");
        Reflect.call(p, "changePersonality");
        check(!(boolean)Reflect.get(p,"curious") && Math.abs((double)Reflect.call(p,"obstaclePush",37.5) - .1) < 1e-12, 1, "back to Shy");
        var other = Reflect.create("P11_FlockPlayground");
        Reflect.call(p,"reset",42L);Reflect.call(other,"reset",42L);
        Reflect.set(p,"obstacleX",300.0);Reflect.set(other,"obstacleX",300.0);
        for (Object bird : new Object[]{p,other}) {
            ((double[])Reflect.get(bird,"x"))[0]=310;
            ((double[])Reflect.get(bird,"y"))[0]=200;
            ((double[])Reflect.get(bird,"vx"))[0]=0;
            ((double[])Reflect.get(bird,"vy"))[0]=0;
        }
        Reflect.call(p,"changePersonality");
        Reflect.call(p,"step");Reflect.call(other,"step");
        check(((double[])Reflect.get(p,"vx"))[0] < ((double[])Reflect.get(other,"vx"))[0], 2, "Curious changes real steering");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P12_PhasingRhythmsTest {
    static int checks;
    static final String[] CASES = {
        "Both role results contribute with gain 0.17.",
        "Opposite replacement voices cancel.",
        "Actual synthesis uses the composition and keeps duration guards."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.12: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P12_PhasingRhythms");
            Object authored = Reflect.call(p, "makeVoice", .2, .5);
            double expectedTap = .5 * (double) Reflect.call(p, "tap", .001, .2);
            check(Math.abs((double) Reflect.call(authored, "sample", .001) - expectedTap) < 1e-12, 0, "authored Voice gain");
            check((double) Reflect.call(Reflect.call(p, "makeVoice", .2, 0.0), "sample", .001) == 0, 0, "zero gain");
            double expectedMix = .17 * ((double) Reflect.call(p, "tap", .001, .12) + .7 * (double) Reflect.call(p, "tap", .001, .13));
            check(Math.abs((double) Reflect.call(p, "mix", .001) - expectedMix) < 1e-12, 0, "both authored voices installed");
            reject(() -> Reflect.call(p, "makeVoice", .1, 2.0), 0);
            Reflect.set(p, "first", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .2; }));Reflect.set(p, "second", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .5; }));check(Math.abs(((double) Reflect.call(p, "mix", .4))-.119)<1e-12,0,((double) Reflect.call(p, "mix", .4)));
            Reflect.set(p, "first", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return t+.2; }));Reflect.set(p, "second", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .5-2*t; }));
            check(Math.abs(((double) Reflect.call(p, "mix", .4))-.051)<1e-12&&Math.abs(((double) Reflect.call(p, "mix", .8))+.017)<1e-12,0,((double) Reflect.call(p, "mix", .8)));
            Reflect.set(p, "first", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return -.2; }));Reflect.set(p, "second", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .2; }));check(Math.abs(((double) Reflect.call(p, "mix", .8)))<1e-12,1,((double) Reflect.call(p, "mix", .8)));
            Reflect.set(p, "first", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return .25; }));Reflect.set(p, "second", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return 0; }));var samples=((double[]) Reflect.call(p, "signal", 1));check(samples.length==22050&&Math.abs(samples[100]-.0425)<1e-12,2,samples[100]);
            Reflect.set(p, "first", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return t*.25; }));Reflect.set(p, "second", Reflect.function("P12_PhasingRhythms$Voice", values -> { double t = (double) values[0]; return -t*.1; }));samples=((double[]) Reflect.call(p, "signal", 1));
            check(Math.abs(samples[100]-.17*.15*100/22050)<1e-12&&Math.abs(samples[1000]-.17*.15*1000/22050)<1e-12,2,samples[1000]);
            reject(()->Reflect.call(p, "signal", .5),2);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.12: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P13_PendulumPainterTest {
    static int checks;
    static final String[] CASES = {
        "The default role sees current angle, length and gravity.",
        "A constant alternate acceleration changes the actual velocity.",
        "A zero alternate rule keeps an initially resting angle fixed."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.13: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P13_PendulumPainter");
            Object authored = Reflect.call(p, "makeAcceleration");
            Reflect.set(p, "gravity", 200.0);
            check(Math.abs((double) Reflect.call(authored, "at", Math.PI / 2, 100.0) + 2) < 1e-12, 0, "authored restoring rule");
            Reflect.set(p, "gravity", 100.0);
            check(Math.abs((double) Reflect.call(authored, "at", Math.PI / 2, 100.0) + 1) < 1e-12, 0, "current gravity read by existing rule");
            Reflect.set(p, "angle", Math.PI / 2);
            Reflect.set(p, "length", 100.0);
            check(Math.abs((double) Reflect.call(p, "accelerationNow") + 1) < 1e-12, 0, "authored rule installed");
            Reflect.call(p, "reset", 42);double expected=-((double) Reflect.get(p, "gravity"))/((double) Reflect.get(p, "length"))*Math.sin(((double) Reflect.get(p, "angle")));check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-expected)<1e-12,0,((double) Reflect.call(p, "accelerationNow")));
            Reflect.set(p, "angle", .4);Reflect.set(p, "length", 120);expected=-((double) Reflect.get(p, "gravity"))/((double) Reflect.get(p, "length"))*Math.sin(((double) Reflect.get(p, "angle")));check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-expected)<1e-12,0,((double) Reflect.call(p, "accelerationNow")));
            Reflect.set(p, "acceleration", Reflect.function("P13_PendulumPainter$Acceleration", values -> { double angle = (double) values[0]; double length = (double) values[1]; return angle+length/100; }));
            check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-1.6)<1e-12,1,((double) Reflect.call(p, "accelerationNow")));
            Reflect.set(p, "angle", -.2);Reflect.set(p, "length", 50);check(Math.abs(((double) Reflect.call(p, "accelerationNow"))-.3)<1e-12,1,((double) Reflect.call(p, "accelerationNow")));
            Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "velocity"))-.3*.03*.999)<1e-12,1,((double) Reflect.get(p, "velocity")));
            Reflect.call(p, "reset", 42);
            Reflect.set(p, "acceleration", Reflect.function("P13_PendulumPainter$Acceleration", values -> { double angle = (double) values[0]; double length = (double) values[1]; return 2; }));Reflect.call(p, "step");check(Math.abs(((double) Reflect.get(p, "velocity"))-.05994)<1e-12,1,((double) Reflect.get(p, "velocity")));
            Reflect.set(p, "acceleration", Reflect.function("P13_PendulumPainter$Acceleration", values -> { double angle = (double) values[0]; double length = (double) values[1]; return 0; }));Reflect.call(p, "reset", 42);double before=((double) Reflect.get(p, "angle"));Reflect.call(p, "step");check(((double) Reflect.get(p, "angle"))==before&&((int) Reflect.get(p, "ticks"))==1,2,((double) Reflect.get(p, "angle")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.13: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_KnightTourTest {
    static int checks;
    static final String[] CASES = {
        "Move preserves the record component order.",
        "Further legal moves advance count once.",
        "Bad coordinates preserve state and Restart restores the initial board."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.16: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P16_KnightTour");
            Reflect.call(p, "restart");check(((boolean) Reflect.call(p, "apply", Reflect.create("P16_KnightTour$Move", 1, 2)))&&((int) Reflect.get(p, "x"))==1&&((int) Reflect.get(p, "y"))==2,0,((int) Reflect.get(p, "x"))+","+((int) Reflect.get(p, "y")));
            check(((boolean) Reflect.call(p, "apply", Reflect.create("P16_KnightTour$Move", 3, 3)))&&((int) Reflect.get(p, "count"))==3,1,((int) Reflect.get(p, "count")));
            int x=((int) Reflect.get(p, "x")),y=((int) Reflect.get(p, "y"));check(!((boolean) Reflect.call(p, "apply", Reflect.create("P16_KnightTour$Move", 99, 0)))&&((int) Reflect.get(p, "x"))==x&&((int) Reflect.get(p, "y"))==y,2,((int) Reflect.get(p, "count")));
            check(((boolean) Reflect.call(p, "apply", Reflect.create("P16_KnightTour$Restart")))&&((int) Reflect.get(p, "count"))==1&&((int) Reflect.get(p, "x"))==0&&((int) Reflect.get(p, "y"))==0,2,((int) Reflect.get(p, "count")));
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.16: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P17_CipherPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "The crack mode enumerates all 26 decoded shifts with exact labels.",
        "The tool handles different keys, case and punctuation without guessing a winner.",
        "The three normal cipher alternatives still encode and decode correctly."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.17: " + CASES[example] + " | actual=" + actual);
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
        var p = Reflect.create("P17_CipherPostcards");
        Object card;
        try {
            card = Reflect.call(p, "chooseCipher", "crack", "");
        } catch (IllegalArgumentException missing) {
            check(false, 0, "crack mode rejected: " + missing.getMessage());
            return;
        }
        String report = (String) Reflect.call(p, "transform", "KHOOR", card, false);
        String[] lines = report.split("\n", -1);
        check(lines.length == 26 && lines[0].equals("Shift 0: KHOOR") && lines[3].equals("Shift 3: HELLO") && lines[25].startsWith("Shift 25: "), 0, report);
        for (int key = 0; key < 26; key++) {
            String encoded = (String) Reflect.call(p, "applyText", "Meet π at 5!", "caesar", Integer.toString(key), false);
            String decoded = (String) Reflect.call(p, "transform", encoded, card, false);
            check(decoded.split("\n", -1)[key].equals("Shift " + key + ": Meet π at 5!"), 1, "key " + key);
        }
        for (String mode : new String[]{"caesar", "substitution", "vigenere"}) {
            String key = mode.equals("caesar") ? "7" : mode.equals("substitution") ? "ZYXWVUTSRQPONMLKJIHGFEDCBA" : "LEMON";
            Object normal = Reflect.call(p, "chooseCipher", mode, key);
            String encoded = (String) Reflect.call(p, "transform", "Hello, Moon!", normal, false);
            check(Reflect.call(p, "transform", encoded, normal, true).equals("Hello, Moon!"), 2, mode);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P18_RetroSoundForgeTest {
    static int checks;
    static final String[] CASES = {
        "Wobble uses arbitrary carried base and depth values.",
        "Jump and Laser retain their independent formulas.",
        "Invalid time is rejected and real sound presets differ."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.18: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P18_RetroSoundForge");
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 300, 90), 1.0/32))-390)<1e-9,0,((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 300, 90), 1.0/32)));
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 300, 90), 3.0/32))-210)<1e-9,0,((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 300, 90), 3.0/32)));
            check(((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 410, 25), 0))==410,0,((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 410, 25), 0)));
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Jump", 100, 200), .5))-200)<1e-9,1,"jump");
            check(Math.abs(((double) Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Laser", 100, .25), 1))-25)<1e-9,1,"laser");
            reject(()->Reflect.call(p, "frequency", Reflect.create("P18_RetroSoundForge$Wobble", 300, 90), Double.NaN),2);
            check(!Arrays.equals(((double[]) Reflect.call(p, "signal", "wobble", .1)),((double[]) Reflect.call(p, "signal", "laser", .1))),2,"different waves");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.18: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P19_FireflySynchronyTest {
    static int checks;
    static final String[] CASES = {
        "A valid Coupling value reaches the model.",
        "Both the upper boundary and invalid-value preservation work.",
        "Toggle and seeded Restart dispatch to the correct independent rules."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.19: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P19_FireflySynchrony");
            Reflect.call(p, "reset", 42);Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", .35));check(((double) Reflect.get(p, "coupling"))==.35,0,((double) Reflect.get(p, "coupling")));
            Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", 2));check(((double) Reflect.get(p, "coupling"))==2,1,((double) Reflect.get(p, "coupling")));
            Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", 2.1));Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", Double.NaN));check(((double) Reflect.get(p, "coupling"))==2,1,((double) Reflect.get(p, "coupling")));
            for(double invalid:new double[]{-.1,2.1,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.NaN}){
                Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", .35));Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", invalid));check(((double) Reflect.get(p, "coupling"))==.35,1,((double) Reflect.get(p, "coupling")));
            }
            Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Coupling", 0));check(((double) Reflect.get(p, "coupling"))==0,1,((double) Reflect.get(p, "coupling")));
            boolean run=((boolean) Reflect.get(p, "running"));Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Toggle"));check(((boolean) Reflect.get(p, "running"))!=run,2,((boolean) Reflect.get(p, "running")));
            var expected=Reflect.create("P19_FireflySynchrony");Reflect.call(expected, "reset", 17);Reflect.call(p, "apply", Reflect.create("P19_FireflySynchrony$Restart", 17));check(Arrays.equals(((double[]) Reflect.get(p, "phase")),((double[]) Reflect.get(expected, "phase"))),2,((double[]) Reflect.get(p, "phase"))[0]);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.19: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P22_CrystalGrowthTest {
    static int checks;
    static final String[] CASES = {
        "The added mode sees a diagonal-only neighbour.",
        "The old four-neighbour mode still excludes diagonals and accepts orthogonal contact.",
        "Edges/empty data are safe and real crystal growth still attaches cells."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.22: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P22_CrystalGrowth");
            boolean[][] grid=new boolean[3][3];grid[0][0]=true;
            check(((boolean) Reflect.call(p, "touching", grid, 1, 1, true)),0,"diagonal");check(!((boolean) Reflect.call(p, "touching", grid, 1, 1, false)),1,"old mode");
            grid[0][1]=true;check(((boolean) Reflect.call(p, "touching", grid, 1, 1, false)),1,"orthogonal");
            check(((boolean) Reflect.call(p, "touching", grid, 0, 1, true)),2,"edge");check(!((boolean) Reflect.call(p, "touching", new boolean[0][0], 0, 0, true)),2,"empty");
            Reflect.set(p, "diagonalGrowth", true);var grown=((boolean[][]) Reflect.call(p, "grow", 42, 20));int cells=0;for(var row:grown)for(boolean cell:row)if(cell)cells++;check(cells>1&&cells<=21,2,cells);
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.22: Unexpected failure while checking the active rule.", failure);
        }
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P23_MountainPostcardsTest {
    static int checks;
    static final String[] CASES = {
        "The returned ridge is reversed; mirrored and original views differ.",
        "The returned array is independent of the original.",
        "Empty and one-element ridges preserve their meaning."
    };
    static void check(boolean okay, int example, Object actual) {
        checks++;
        if (!okay) throw new AssertionError("MISSION 14.23: " + CASES[example] + " | actual=" + actual);
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
            var p = Reflect.create("P23_MountainPostcards");
            double[] input={3,7,2};var result=((double[]) Reflect.call(p, "reverseRidge", input));check(Arrays.equals(result,new double[]{2,7,3}),0,Arrays.toString(result));
            result[0]=99;check(Arrays.equals(input,new double[]{3,7,2}),1,Arrays.toString(input));
            check(((double[]) Reflect.call(p, "reverseRidge", new double[0])).length==0&&Arrays.equals(((double[]) Reflect.call(p, "reverseRidge", new double[]{4})),new double[]{4}),2,"bounds");
            Reflect.set(p, "mirrored", false);var original=((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, .65));Reflect.set(p, "mirrored", true);check(hash(original)!=hash(((java.awt.image.BufferedImage) Reflect.call(p, "render", 42, .65))),0,"mirrored image");
        } catch (AssertionError failure) {
            throw failure;
        } catch (Exception failure) {
            throw new AssertionError("MISSION 14.23: Unexpected failure while checking the active rule.", failure);
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
