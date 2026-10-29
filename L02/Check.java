import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_CannonPuzzle.java`
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
        run("M01_CannonPuzzle", "L02.01 CannonPuzzle · F04", () -> M01_CannonPuzzleTest.main(new String[0]));
        run("M02_MiniGolf", "L02.02 MiniGolf · F04", () -> M02_MiniGolfTest.main(new String[0]));
        run("M03_BouncingBalls", "L02.03 BouncingBalls · F04", () -> M03_BouncingBallsTest.main(new String[0]));
        run("M04_Spirograph", "L02.04 Spirograph · F04", () -> M04_SpirographTest.main(new String[0]));
        run("M05_AnagramRounds", "L02.05 AnagramRounds · F05", () -> M05_AnagramRoundsTest.main(new String[0]));
        run("M06_MadLibsStoryMachine", "L02.06 MadLibsStoryMachine · F05", () -> M06_MadLibsStoryMachineTest.main(new String[0]));
        run("M07_Codebreaker", "L02.07 Codebreaker · F06", () -> M07_CodebreakerTest.main(new String[0]));
        run("M08_PokerDice", "L02.08 PokerDice · F06", () -> M08_PokerDiceTest.main(new String[0]));
        run("M09_SonarTreasureHunt", "L02.09 SonarTreasureHunt · F06", () -> M09_SonarTreasureHuntTest.main(new String[0]));
        run("M10_MagicNumberCards", "L02.10 MagicNumberCards · F06", () -> M10_MagicNumberCardsTest.main(new String[0]));
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

class M01_CannonPuzzleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I065 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_CannonPuzzle();
        check(Math.abs(game.launchSpeed(100) - 9) < 1e-8, "100 pixels of drag must give speed 9");
        check(Math.abs(game.launchSpeed(-80) + 7.2) < 1e-8, "upward drag must keep its negative fractional speed");
        game.restart();
        game.launch();
        check(game.vx > 0 && game.vy < 0, "the actual cannon must launch right and upward");
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_MiniGolfTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I066 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_MiniGolf();
        check(Math.abs(game.dragVelocity(100) - 8) < 1e-8, "100 pixels must produce speed 8 before the supplied cap");
        check(Math.abs(game.dragVelocity(-25) + 2) < 1e-8, "negative distance must produce -2");
        check(game.dragVelocity(0) == 0, "no pull means no velocity");
        game.restart();
        game.begin(90, 110);
        game.shoot(-10, 110);
        check(Math.abs(game.vx - 8) < 1e-8 && game.strokes == 1, "the actual putt uses the repaired scale");
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_BouncingBallsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I069 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_BouncingBalls();
        check(Math.abs(game.bounceSpeed(4) - 3) < 1e-8, "a wall bounce retains three quarters of speed");
        check(Math.abs(game.bounceSpeed(-8) - 6) < 1e-8, "the supplied magnitude conversion must retain a fraction");
        check(game.bounceSpeed(0) == 0, "zero speed remains zero");
        game.restart();
        game.add(11, 100, -4, 0);
        game.step();
        check(game.balls.getFirst().vx() > 2.9, "the real ball reverses away from the left wall");
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_SpirographTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I075 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_Spirograph();
        check(Math.abs(game.radiusRatio(120, 45) - 5.0 / 3) < 1e-8, "75 divided by 45 must retain its fractional ratio");
        check(game.radiusRatio(120, 60) == 1, "an exact ratio remains exact");
        check(game.radiusRatio(120, 80) == 0.5, "a ratio below one must not become zero");
        game.restart();
        var p = game.point(1);
        check(Math.abs(p.x - (75 * Math.cos(1) + 65 * Math.cos(5.0 / 3))) < 1e-8, "the visible curve uses the repaired ratio");
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_AnagramRoundsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I029 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_AnagramRounds();
        check(game.normalize("  silent  ").equals("SILENT"), "a whole lower-case answer must become the accepted upper-case word");
        check(game.normalize("LiStEn").equals("LISTEN"), "mixed case is normalized");
        game.reset();
        game.command(" silent ");
        check(game.score == 1 && game.round == 1, "the real anagram round accepts the normalized answer");
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_MadLibsStoryMachineTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I129 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_MadLibsStoryMachine();
        check(game.cleanWord("  Red Fox  ").equals("Red Fox"), "edge spaces must disappear while a multiword name stays whole");
        check(game.cleanWord("New   Moon").equals("New   Moon"), "internal spacing is not silently rewritten");
        String story = game.story("  Red Fox  ", " robot ", " Moon Base ", " spoon ");
        check(story.startsWith("Red Fox hired a robot to guard Moon Base."), "the actual story uses cleaned full inputs");
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_CodebreakerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I020 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_Codebreaker();
        game.secret = new String("1234");
        check(game.matchesCode(new String("1234")), "equal code text must match even when stored as different String objects");
        check(!game.matchesCode("1235"), "a different final digit must not match");
        game.attempts = 0;
        game.won = false;
        check(game.submit(new String("1234")) && game.won, "the actual matching guess must end the round");
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_PokerDiceTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I039 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_PokerDice();
        check(!game.fullHouse(3, 0), "three equal dice alone are not a full house");
        check(!game.fullHouse(2, 1), "a pair alone is not a full house");
        check(game.fullHouse(3, 1), "three equal dice plus a pair form a full house");
        game.dice = new int[]{2, 2, 2, 3, 4};
        check(game.points() == 20, "actual three-kind scores 20 rather than full-house 35");
        game.dice = new int[]{2, 2, 2, 3, 3};
        check(game.points() == 35, "actual full house scores 35");
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_SonarTreasureHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I042 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_SonarTreasureHunt();
        check(!game.insideSea(8, 7), "row index 8 is outside the eight-row sea");
        check(!game.insideSea(7, 8), "column index 8 is outside the sea");
        check(game.insideSea(0, 0) && game.insideSea(7, 7), "both valid corners are included");
        check(!game.insideSea(-1, 0) && !game.insideSea(0, -1), "negative indices are excluded");
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_MagicNumberCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I135 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M10_MagicNumberCards();
        check(game.yesAnswer(new String("y")), "a typed lower-case y must mean yes");
        check(game.yesAnswer("Y"), "upper-case Y must also mean yes");
        check(!game.yesAnswer("n"), "n must mean no");
        boolean[] answers = {game.yesAnswer("Y"), false, true, false, false, false};
        check(game.reveal(answers) == 5, "the actual trick must include the first yes card");
        System.out.println("PASS " + checks + " checks");
    }
}
