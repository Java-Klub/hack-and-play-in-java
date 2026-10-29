import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_Hangman.java`
/// Kontroly volají testovací metody přímo v této JVM.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_Hangman", "L04.01 Hangman · F10", () -> M01_HangmanTest.main(new String[0]));
        run("M02_Ghost", "L04.02 Ghost · F10", () -> M02_GhostTest.main(new String[0]));
        run("M03_Flood", "L04.03 Flood · F10", () -> M03_FloodTest.main(new String[0]));
        run("M04_SameGame", "L04.04 SameGame · F10", () -> M04_SameGameTest.main(new String[0]));
        run("M05_WordSearch", "L04.05 WordSearch · F11", () -> M05_WordSearchTest.main(new String[0]));
        run("M06_VirtualScribbler", "L04.06 VirtualScribbler · F11", () -> M06_VirtualScribblerTest.main(new String[0]));
        run("M07_KochSnowflakeCards", "L04.07 KochSnowflakeCards · F11", () -> M07_KochSnowflakeCardsTest.main(new String[0]));
        run("M08_DragonCurve", "L04.08 DragonCurve · F11", () -> M08_DragonCurveTest.main(new String[0]));
        run("M09_EvilHangman", "L04.09 EvilHangman · F12", () -> M09_EvilHangmanTest.main(new String[0]));
        run("M10_WordleStyleWordHunt", "L04.10 WordleStyleWordHunt · F12", () -> M10_WordleStyleWordHuntTest.main(new String[0]));
        run("M11_Jotto", "L04.11 Jotto · F12", () -> M11_JottoTest.main(new String[0]));
        run("M12_WordChain", "L04.12 WordChain · F12", () -> M12_WordChainTest.main(new String[0]));
        run("M13_YatzyScorecard", "L04.13 YatzyScorecard · F12", () -> M13_YatzyScorecardTest.main(new String[0]));
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

class M01_HangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I021 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_Hangman();
        game.word = "BANANA";
        game.tried = "A";
        check(game.mask().equals("_A_A_A"), "the while loop must include the last letter");
        game.word = "A";
        game.tried = "";
        check(game.mask().equals("_"), "a one-letter word runs once");
        game.word = "";
        check(game.mask().isEmpty(), "an empty word runs zero times without indexing it");
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_GhostTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I025 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_Ghost();
        check(game.possiblePrefix("RO"), "the search must reach dictionary entries beyond the first word");
        check(game.possiblePrefix("GHO"), "the first matching word may return immediately");
        check(!game.possiblePrefix("XY"), "a missing prefix terminates with false");
        game.reset();
        game.command("R");
        check(game.outcome.isEmpty() && game.player == 2, "the actual legal R prefix must keep the game alive");
        game.words = new String[0];
        check(!game.possiblePrefix("A"), "an empty dictionary terminates without access");
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_FloodTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I055 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_Flood();
        game.board = new int[8][8];
        game.board[7][7] = 1;
        game.fill(2);
        check(game.board[7][6] == 2, "the expanding queue must be processed beyond the first cell");
        int changed = 0;
        for (int[] row : game.board) { for (int value : row) { if (value == 2) changed++; } }
        check(changed == 63 && game.board[7][7] == 1, "all and only the connected old colour must change");
        check(game.moves == 1, "one completed flood counts as one move");
        game.spread(new ArrayList<>(), 0, 1);
        check(game.board[7][7] == 1, "an empty work queue performs no iteration");
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_SameGameTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I082 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_SameGame();
        game.board = new int[5][5];
        for (int[] row : game.board) Arrays.fill(row, 1);
        check(game.group(0, 0).size() == 25, "the queue loop must discover the full connected group");
        check(game.group(-1, 0).isEmpty(), "an invalid start is rejected before queue access");
        game.outcome = "";
        game.command("1 1");
        check(game.outcome.startsWith("Board clear") && game.score == 600, "the actual complete group can be removed to win");
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_WordSearchTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I028 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_WordSearch();
        check(game.read(0,0,0,2).equals("CAT"), "the counted loop must include the final selected cell");
        check(game.read(0,2,0,0).equals("TAC"), "the same range must work in reverse");
        check(game.read(2,0,2,4).equals("TIGER"), "five selected cells produce five letters");
        game.reset();
        game.command("1 1 1 3");
        check(game.found.contains("CAT"), "the actual word selection uses the complete loop result");
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_VirtualScribblerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I100 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_VirtualScribbler();
        game.restart();
        game.paused = false;
        game.burst(1);
        check(game.steps == 1, "a one-step burst must execute once");
        game.burst(0);
        check(game.steps == 1, "a zero-step burst must execute zero times");
        game.burst(4);
        check(game.steps == 5, "a four-step burst adds exactly four pen moves");
        game.paused = true;
        game.burst(4);
        check(game.steps == 5, "the supplied pause rule remains active");
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_KochSnowflakeCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I107 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_KochSnowflakeCards();
        var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
        var pen = image.createGraphics();
        check(game.snowflake(pen, 0) == 3, "the counted loop must draw all three base sides");
        boolean firstSide = false;
        for (int y = 215; y <= 221; y++) {
            for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
        }
        check(firstSide, "the actual first edge must be painted, not merely counted");
        check(game.snowflake(pen, 1) == 12, "three supplied four-segment edges give twelve segments");
        pen.dispose();
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_DragonCurveTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I108 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_DragonCurve();
        check(game.foldTurns(1).equals(List.of(1)), "one requested fold must run the outer loop once");
        check(game.foldTurns(0).isEmpty(), "zero folds must run zero times");
        check(game.foldTurns(2).equals(List.of(1,1,-1)), "two folds use two outer passes");
        check(game.path(3).size() == 9, "the actual three-fold path has eight segments and nine points");
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_EvilHangmanTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I022 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_EvilHangman();
        check(game.familySize(List.of("ALLY","COOL","GOOD")) == 3, "the family count must accumulate across all words");
        check(game.familySize(List.of()) == 0 && game.familySize(List.of("ALLY")) == 1, "zero and one member are counted correctly");
        game.reset();
        game.pool = new ArrayList<>(List.of("ALLY","COOL","GOOD","BETA"));
        game.command("E");
        check(game.pool.size() == 3 && game.mask.equals("____"), "the actual adversary must keep the largest consistent family");
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_WordleStyleWordHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I023 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M10_WordleStyleWordHunt();
        game.secret = "LEVEL";
        char[] marks = ".....".toCharArray();
        int[] counts = game.leftoverLetters("ALLEY", marks);
        check(counts['L'-'A'] == 2, "two unmatched L letters need an accumulated count of two");
        check(game.grade("ALLEY").equals(".YYG."), "both guessed L letters receive the actual remaining matches");
        check(game.grade("LEVEL").equals("GGGGG"), "exact matches stay green and consume no leftover count");
        check(game.grade("STONE").equals("....Y"), "an absent letter cannot gain a false match");
        System.out.println("PASS " + checks + " checks");
    }
}

class M11_JottoTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I024 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M11_Jotto();
        game.secret = "PLANT";
        check(game.matches("PLANT") == 5, "all five shared letters must accumulate");
        check(game.matches("STONE") == 2, "T and N are counted regardless of positions");
        check(game.matches("BRICK") == 0, "no shared letters leaves zero");
        game.reset();
        game.secret = "PLANT";
        game.command("STONE");
        check(game.history.getFirst().contains("2 shared"), "the actual clue uses the repaired total");
        System.out.println("PASS " + checks + " checks");
    }
}

class M12_WordChainTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I027 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M12_WordChain();
        game.reset();
        check(game.choices().size() == 20, "the accumulated choices must retain all initially legal words");
        game.last = "APPLE";
        check(game.choices().equals(List.of("EARTH","EAGLE","ELEPHANT","ECHO","ENGINE")), "all five E continuations survive the loop");
        game.used.add("EARTH");
        check(!game.choices().contains("EARTH") && game.choices().size() == 4, "used words are excluded without losing other choices");
        game.reset();
        game.command("APPLE");
        check(game.last.equals("APPLE"), "the actual game accepts an early legal word, not only the final dictionary entry");
        System.out.println("PASS " + checks + " checks");
    }
}

class M13_YatzyScorecardTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I040 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M13_YatzyScorecard();
        game.reset();
        game.scores[0] = 1;
        game.scores[1] = 4;
        game.scores[2] = 9;
        check(game.total() == 14, "filled categories must accumulate rather than replace the running sum");
        game.reset();
        check(game.total() == 0, "unfilled negative entries contribute nothing");
        game.scores = new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1};
        check(game.total() == 98, "upper sum 63 adds the bonus exactly once");
        game.scores[5] = 17;
        check(game.total() == 62, "upper sum 62 earns no bonus");
        System.out.println("PASS " + checks + " checks");
    }
}
