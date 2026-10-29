import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M02_Nim.java`
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
        run("M01_Pig", "L03.01 Pig · F07", () -> M01_PigTest.main(new String[0]));
        run("M02_Nim", "L03.02 Nim · F07", () -> M02_NimTest.main(new String[0]));
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

@SuppressWarnings("unchecked")
class M02_NimTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.02 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M02_Nim");
        Reflect.set(game, "stones", 7);
        check(((int) Reflect.call(game, "botMove")) == 3, "seven stones need the nonzero remainder branch");
        Reflect.set(game, "stones", 8);
        check(((int) Reflect.call(game, "botMove")) == 1, "a multiple of four still needs a legal fallback move");
        Reflect.set(game, "stones", 1);
        check(((int) Reflect.call(game, "botMove")) == 1, "one remaining stone is taken");
        Reflect.set(game, "stones", 0);
        check(((int) Reflect.call(game, "botMove")) == 0, "an empty pile has no bot move");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M01_PigTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.01 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M01_Pig");
        Reflect.call(game, "reset");
        Reflect.set(game, "turnTotal", 9);
        Reflect.call(game, "roll", 1);
        check(((int) Reflect.get(game, "turnTotal")) == 0 && ((int) Reflect.get(game, "player")) == 1, "rolling one must lose the turn and pass the die");
        Reflect.call(game, "reset");
        Reflect.call(game, "roll", 6);
        check(((int) Reflect.get(game, "turnTotal")) == 6 && ((int) Reflect.get(game, "player")) == 0, "six adds points and keeps the turn");
        check(!((boolean) Reflect.call(game, "roll", 0)) && ((int) Reflect.get(game, "turnTotal")) == 6, "the supplied invalid-die guard remains intact");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_BlackjackTwentyOneTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.03 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M03_BlackjackTwentyOne");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 20, 19)).equals("You win"), "a higher unbusted player total must win");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 21, 21)).equals("Draw"), "equal totals must draw");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 18, 22)).equals("You win"), "a busted dealer loses");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 22, 23)).equals("You bust"), "the player bust guard has priority");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 18, 20)).equals("Dealer wins"), "the larger legal dealer total wins");
        Reflect.set(game, "hand", List.of(10, 10));
        Reflect.set(game, "dealer", new ArrayList<>(List.of(10, 9)));
        Reflect.call(game, "finish");
        check(((java.lang.String) Reflect.get(game, "outcome")).equals("You win"), "finish uses the repaired comparison rule");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_TicTacToeTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.04 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M04_TicTacToe");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "play", 9)) && ((char[]) Reflect.get(game, "board"))[8] == 'X', "cell nine is a legal empty boundary cell");
        check(!((boolean) Reflect.call(game, "play", 9)) && ((int) Reflect.get(game, "moves")) == 1, "an occupied cell must be rejected without consuming a move");
        check(!((boolean) Reflect.call(game, "play", 0)) && !((boolean) Reflect.call(game, "play", 10)), "out-of-range cells must be rejected before indexing the board");
        check(((char) Reflect.get(game, "player")) == 'O', "rejected commands must not pass play again");
        Reflect.set(game, "winner", 'X');
        check(!((boolean) Reflect.call(game, "play", 1)) && ((char[]) Reflect.get(game, "board"))[0] == '.', "a finished round rejects even an empty legal square");
        check(((int) Reflect.get(game, "moves")) == 1 && ((char) Reflect.get(game, "player")) == 'O', "rejecting a finished round preserves count and turn");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_ChompTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.05 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M05_Chomp");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 4, 5)) && ((int[]) Reflect.get(game, "widths"))[3] == 4, "the last row must accept a legal remaining bite");
        int[] before = ((int[]) Reflect.get(game, "widths")).clone();
        check(!((boolean) Reflect.call(game, "bite", 5, 1)) && Arrays.equals(before, ((int[]) Reflect.get(game, "widths"))), "an invalid row must not mutate chocolate");
        check(!((boolean) Reflect.call(game, "bite", 4, 5)), "already eaten chocolate must be rejected");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "bite", 1, 1)) && ((int) Reflect.get(game, "winner")) == 2, "the legal poison cell still causes a loss");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_ChopsticksTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.06 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M06_Chopsticks");
        Reflect.call(game, "reset");
        ((int[][]) Reflect.get(game, "hands"))[0][0] = 0;
        check(!((boolean) Reflect.call(game, "handsAlive", 0, 0)), "a dead attacking hand must be rejected");
        ((int[][]) Reflect.get(game, "hands"))[0][0] = 1;
        ((int[][]) Reflect.get(game, "hands"))[1][1] = 0;
        check(!((boolean) Reflect.call(game, "handsAlive", 0, 1)), "a dead target hand must be rejected");
        check(((boolean) Reflect.call(game, "handsAlive", 1, 0)), "two living hands remain legal");
        check(!((boolean) Reflect.call(game, "handsAlive", -1, 0)) && !((boolean) Reflect.call(game, "handsAlive", 0, 2)), "invalid indices are rejected before hand access");
        int player = ((int) Reflect.get(game, "player"));
        try { Reflect.call(game, "command", "1 2"); } catch (IllegalArgumentException expected) { }
        check(((int) Reflect.get(game, "player")) == player && ((int[][]) Reflect.get(game, "hands"))[1][1] == 0, "rejected input leaves actual game state untouched");
        check(!((boolean) Reflect.call(game, "handsAlive", 2, 0)) && !((boolean) Reflect.call(game, "handsAlive", 0, -1)), "both hand ranges must be guarded at both ends");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_HanoiTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.07 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M07_Hanoi");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "move", 0, 1)), "a small disc may move onto an empty peg");
        int before = ((int) Reflect.get(game, "moves"));
        check(!((boolean) Reflect.call(game, "move", 0, 1)), "a larger disc must be rejected above a smaller disc");
        check(((int) Reflect.get(game, "moves")) == before && ((int[]) Reflect.get(game, "sizes"))[0] == 2 && ((int[]) Reflect.get(game, "sizes"))[1] == 1, "rejected moves preserve both stacks and the counter");
        check(((boolean) Reflect.call(game, "move", 0, 2)) && ((boolean) Reflect.call(game, "move", 1, 2)), "a smaller disc may land above a larger one");
        check(!((boolean) Reflect.call(game, "move", -1, 0)) && !((boolean) Reflect.call(game, "move", 0, 3)) && !((boolean) Reflect.call(game, "move", 0, 0)), "outer guards still reject invalid or identical pegs");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_SudokuTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.08 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M08_Sudoku");
        Reflect.call(game, "reset");
        check(((boolean) Reflect.call(game, "place", 0, 1, 2)) && ((int[][]) Reflect.get(game, "board"))[0][1] == 2, "a valid empty square must accept a number");
        check(!((boolean) Reflect.call(game, "place", 0, 0, 0)) && ((int[][]) Reflect.get(game, "board"))[0][0] == 1, "a printed clue must never be erased");
        check(!((boolean) Reflect.call(game, "place", -1, 0, 1)) && !((boolean) Reflect.call(game, "place", 0, 4, 1)), "invalid coordinates are rejected before clue access");
        check(!((boolean) Reflect.call(game, "place", 0, 2, 2)) && ((int[][]) Reflect.get(game, "board"))[0][2] == 0, "a conflicting number is rejected before writing");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_SOSTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.09 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M09_SOS");
        check(((int) Reflect.call(game, "gainedPoints", 2, 3)) == 1, "one new SOS adds one point rather than rescoring old triples");
        check(((int) Reflect.call(game, "gainedPoints", 3, 3)) == 0, "no new triple adds no points");
        check(((int) Reflect.call(game, "gainedPoints", 0, 2)) == 2, "two simultaneous new triples add two points");
        Reflect.call(game, "reset");
        Reflect.set(game, "board", "SOS......".toCharArray());
        Reflect.set(game, "moves", 3);
        Reflect.call(game, "command", "4 S");
        check(((int[]) Reflect.get(game, "scores"))[1] == 0 && ((int) Reflect.get(game, "player")) == 2, "an actual non-scoring move must not score the old SOS again");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_ShutTheBoxTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.10 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M10_ShutTheBox");
        check(((int) Reflect.call(game, "diceTotal", 2, 5)) == 7, "both dice must contribute to the target");
        check(((int) Reflect.call(game, "diceTotal", 6, 6)) == 12 && ((int) Reflect.call(game, "diceTotal", 1, 1)) == 2, "the smallest and largest two-dice totals are included");
        Reflect.set(game, "random", new Random(42));
        Reflect.call(game, "reset");
        var random = new Random(42);
        int expected = random.nextInt(1,7) + random.nextInt(1,7);
        check(((int) Reflect.get(game, "target")) == expected, "the actual roll uses the two supplied arguments");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M11_HexapawnTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.11 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M11_Hexapawn");
        check(((int) Reflect.call(game, "goalRow", 2)) == 2, "player two must have the bottom goal row");
        check(((int) Reflect.call(game, "goalRow", 1)) == 0, "player one must have the top goal row");
        Reflect.call(game, "reset");
        Reflect.set(game, "player", 2);
        Reflect.set(game, "board", new int[][]{{0,0,0},{2,0,0},{0,0,1}});
        Reflect.call(game, "command", "2 1 3 1");
        check(((java.lang.String) Reflect.get(game, "outcome")).contains("2 wins"), "the real promotion uses goalRow for the current player");
        check(((int) Reflect.call(game, "forwardStep", 1)) == -1 && ((int) Reflect.call(game, "forwardStep", 2)) == 1, "the two armies need opposite forward directions");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M12_ToadsAndFrogsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L03.12 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M12_ToadsAndFrogs");
        check(((int) Reflect.call(game, "direction", 2)) == -1, "a frog must receive the leftward result");
        check(((int) Reflect.call(game, "direction", 1)) == 1, "a toad must receive the rightward result");
        Reflect.call(game, "restart");
        check(((boolean) Reflect.call(game, "move", 4)) && ((int[]) Reflect.get(game, "cells"))[3] == 2 && ((int[]) Reflect.get(game, "cells"))[4] == 0, "the actual right-hand frog can step left into the gap");
        Reflect.call(game, "restart");
        check(((boolean) Reflect.call(game, "move", 2)) && ((int[]) Reflect.get(game, "cells"))[3] == 1, "the actual left-hand toad can step right into the gap");
        check(((int) Reflect.call(game, "destination", 5, 2, 2)) == 3, "a frog jump uses origin, distance and leftward direction");
        check(((int) Reflect.call(game, "destination", 1, 2, 1)) == 3, "a toad jump uses the same calculation with its own direction");
        check(((int) Reflect.call(game, "destination", 4, 0, 2)) == 4, "zero distance preserves the origin");
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
