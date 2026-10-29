import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_Nim.java`
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
        run("M01_Nim", "L03.01 Nim · F07", () -> M01_NimTest.main(new String[0]));
        run("M02_Pig", "L03.02 Pig · F07", () -> M02_PigTest.main(new String[0]));
        run("M03_BlackjackTwentyOne", "L03.03 BlackjackTwentyOne · F07", () -> M03_BlackjackTwentyOneTest.main(new String[0]));
        run("M04_TicTacToe", "L03.04 TicTacToe · F08", () -> M04_TicTacToeTest.main(new String[0]));
        run("M05_Chomp", "L03.05 Chomp · F08", () -> M05_ChompTest.main(new String[0]));
        run("M06_Chopsticks", "L03.06 Chopsticks · F08", () -> M06_ChopsticksTest.main(new String[0]));
        run("M07_Hanoi", "L03.07 Hanoi · F08", () -> M07_HanoiTest.main(new String[0]));
        run("M08_Sudoku", "L03.08 Sudoku · F08", () -> M08_SudokuTest.main(new String[0]));
        run("M09_SOS", "L03.09 SOS · F13", () -> M09_SOSTest.main(new String[0]));
        run("M10_ShutTheBox", "L03.10 ShutTheBox · F13", () -> M10_ShutTheBoxTest.main(new String[0]));
        run("M11_Hexapawn", "L03.11 Hexapawn · F13", () -> M11_HexapawnTest.main(new String[0]));
        run("M12_ToadsAndFrogs", "L03.12 ToadsAndFrogs · F13", () -> M12_ToadsAndFrogsTest.main(new String[0]));
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

class M01_NimTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I001 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M01_Nim();
        game.stones = 7;
        check(game.botMove() == 3, "seven stones need the nonzero remainder branch");
        game.stones = 8;
        check(game.botMove() == 1, "a multiple of four still needs a legal fallback move");
        game.stones = 1;
        check(game.botMove() == 1, "one remaining stone is taken");
        game.stones = 0;
        check(game.botMove() == 0, "an empty pile has no bot move");
        System.out.println("PASS " + checks + " checks");
    }
}

class M02_PigTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I037 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M02_Pig();
        game.reset();
        game.turnTotal = 9;
        game.roll(1);
        check(game.turnTotal == 0 && game.player == 1, "rolling one must lose the turn and pass the die");
        game.reset();
        game.roll(6);
        check(game.turnTotal == 6 && game.player == 0, "six adds points and keeps the turn");
        check(!game.roll(0) && game.turnTotal == 6, "the supplied invalid-die guard remains intact");
        System.out.println("PASS " + checks + " checks");
    }
}

class M03_BlackjackTwentyOneTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I031 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M03_BlackjackTwentyOne();
        check(game.compareTotals(20, 19).equals("You win"), "a higher unbusted player total must win");
        check(game.compareTotals(21, 21).equals("Draw"), "equal totals must draw");
        check(game.compareTotals(18, 22).equals("You win"), "a busted dealer loses");
        check(game.compareTotals(22, 23).equals("You bust"), "the player bust guard has priority");
        check(game.compareTotals(18, 20).equals("Dealer wins"), "the larger legal dealer total wins");
        game.hand = List.of(10, 10);
        game.dealer = new ArrayList<>(List.of(10, 9));
        game.finish();
        check(game.outcome.equals("You win"), "finish uses the repaired comparison rule");
        System.out.println("PASS " + checks + " checks");
    }
}

class M04_TicTacToeTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I002 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M04_TicTacToe();
        game.reset();
        check(game.play(9) && game.board[8] == 'X', "cell nine is a legal empty boundary cell");
        check(!game.play(9) && game.moves == 1, "an occupied cell must be rejected without consuming a move");
        check(!game.play(0) && !game.play(10), "out-of-range cells must be rejected before indexing the board");
        check(game.player == 'O', "rejected commands must not pass play again");
        System.out.println("PASS " + checks + " checks");
    }
}

class M05_ChompTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I009 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M05_Chomp();
        game.reset();
        check(game.bite(4, 5) && game.widths[3] == 4, "the last row must accept a legal remaining bite");
        int[] before = game.widths.clone();
        check(!game.bite(5, 1) && Arrays.equals(before, game.widths), "an invalid row must not mutate chocolate");
        check(!game.bite(4, 5), "already eaten chocolate must be rejected");
        game.reset();
        check(game.bite(1, 1) && game.winner == 2, "the legal poison cell still causes a loss");
        System.out.println("PASS " + checks + " checks");
    }
}

class M06_ChopsticksTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I010 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M06_Chopsticks();
        game.reset();
        game.hands[0][0] = 0;
        check(!game.handsAlive(0, 0), "a dead attacking hand must be rejected");
        game.hands[0][0] = 1;
        game.hands[1][1] = 0;
        check(!game.handsAlive(0, 1), "a dead target hand must be rejected");
        check(game.handsAlive(1, 0), "two living hands remain legal");
        check(!game.handsAlive(-1, 0) && !game.handsAlive(0, 2), "invalid indices are rejected before hand access");
        int player = game.player;
        try { game.command("1 2"); } catch (IllegalArgumentException expected) { }
        check(game.player == player && game.hands[1][1] == 0, "rejected input leaves actual game state untouched");
        System.out.println("PASS " + checks + " checks");
    }
}

class M07_HanoiTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I013 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M07_Hanoi();
        game.reset();
        check(game.move(0, 1), "a small disc may move onto an empty peg");
        int before = game.moves;
        check(!game.move(0, 1), "a larger disc must be rejected above a smaller disc");
        check(game.moves == before && game.sizes[0] == 2 && game.sizes[1] == 1, "rejected moves preserve both stacks and the counter");
        check(game.move(0, 2) && game.move(1, 2), "a smaller disc may land above a larger one");
        check(!game.move(-1, 0) && !game.move(0, 3) && !game.move(0, 0), "outer guards still reject invalid or identical pegs");
        System.out.println("PASS " + checks + " checks");
    }
}

class M08_SudokuTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I053 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M08_Sudoku();
        game.reset();
        check(game.place(0, 1, 2) && game.board[0][1] == 2, "a valid empty square must accept a number");
        check(!game.place(0, 0, 0) && game.board[0][0] == 1, "a printed clue must never be erased");
        check(!game.place(-1, 0, 1) && !game.place(0, 4, 1), "invalid coordinates are rejected before clue access");
        check(!game.place(0, 2, 2) && game.board[0][2] == 0, "a conflicting number is rejected before writing");
        System.out.println("PASS " + checks + " checks");
    }
}

class M09_SOSTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I008 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M09_SOS();
        check(game.gainedPoints(2, 3) == 1, "one new SOS adds one point rather than rescoring old triples");
        check(game.gainedPoints(3, 3) == 0, "no new triple adds no points");
        check(game.gainedPoints(0, 2) == 2, "two simultaneous new triples add two points");
        game.reset();
        game.board = "SOS......".toCharArray();
        game.moves = 3;
        game.command("4 S");
        check(game.scores[1] == 0 && game.player == 2, "an actual non-scoring move must not score the old SOS again");
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_ShutTheBoxTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I038 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M10_ShutTheBox();
        check(game.diceTotal(2, 5) == 7, "both dice must contribute to the target");
        check(game.diceTotal(6, 6) == 12 && game.diceTotal(1, 1) == 2, "the smallest and largest two-dice totals are included");
        game.random = new Random(42);
        game.reset();
        var random = new Random(42);
        int expected = random.nextInt(1,7) + random.nextInt(1,7);
        check(game.target == expected, "the actual roll uses the two supplied arguments");
        System.out.println("PASS " + checks + " checks");
    }
}

class M11_HexapawnTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I005 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M11_Hexapawn();
        check(game.goalRow(2) == 2, "player two must have the bottom goal row");
        check(game.goalRow(1) == 0, "player one must have the top goal row");
        game.reset();
        game.player = 2;
        game.board = new int[][]{{0,0,0},{2,0,0},{0,0,1}};
        game.command("2 1 3 1");
        check(game.outcome.contains("2 wins"), "the real promotion uses goalRow for the current player");
        System.out.println("PASS " + checks + " checks");
    }
}

class M12_ToadsAndFrogsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("I095 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = new M12_ToadsAndFrogs();
        check(game.direction(2) == -1, "a frog must receive the leftward result");
        check(game.direction(1) == 1, "a toad must receive the rightward result");
        game.restart();
        check(game.move(4) && game.cells[3] == 2 && game.cells[4] == 0, "the actual right-hand frog can step left into the gap");
        game.restart();
        check(game.move(2) && game.cells[3] == 1, "the actual left-hand toad can step right into the gap");
        System.out.println("PASS " + checks + " checks");
    }
}
