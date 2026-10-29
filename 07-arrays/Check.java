import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_DotsAndBoxes.java
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
        run("P03_DotsAndBoxes", "07.03 DotsAndBoxes · F16", () -> P03_DotsAndBoxesTest.main(new String[0]));
        run("P04_PegDrop", "07.04 PegDrop · F15", () -> P04_PegDropTest.main(new String[0]));
        run("P07_KochSnowflakeCards", "07.07 KochSnowflakeCards · F11", () -> P07_KochSnowflakeCardsTest.main(new String[0]));
        run("P08_Jotto", "07.08 Jotto · F12", () -> P08_JottoTest.main(new String[0]));
        run("P09_YatzyScorecard", "07.09 YatzyScorecard · F12", () -> P09_YatzyScorecardTest.main(new String[0]));
        run("P12_Chopsticks", "07.12 Chopsticks · F08", () -> P12_ChopsticksTest.main(new String[0]));
        run("P13_WordleStyleWordHunt", "07.13 WordleStyleWordHunt · F12", () -> P13_WordleStyleWordHuntTest.main(new String[0]));
        run("P16_Ghost", "07.16 Ghost · F10", () -> P16_GhostTest.main(new String[0]));
        run("P19_Bridges", "07.19 Bridges · F20", () -> P19_BridgesTest.main(new String[0]));
        run("P20_MagnetDominoes", "07.20 MagnetDominoes · F20", () -> P20_MagnetDominoesTest.main(new String[0]));
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
class P03_DotsAndBoxesTest {
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
        var g = Reflect.create("P03_DotsAndBoxes");
        equal(new int[]{0, 2, 6, 7}, ((int[]) Reflect.call(g, "box", 0)), "07.03/F16: first box needs its four real edges");
        equal(new int[]{1, 3, 7, 8}, ((int[]) Reflect.call(g, "box", 1)), "07.03/F16: neighbouring box shares one edge");
        equal(new int[]{3, 5, 10, 11}, ((int[]) Reflect.call(g, "box", 3)), "07.03/F16: final box has different indices");
        Reflect.call(g, "reset");
        for (String edge : new String[]{"1", "3", "7", "8"}) {
            Reflect.call(g, "command", edge);
        }
        expect(((int[]) Reflect.get(g, "owners"))[0] != 0, "07.03/F16: closing a real box awards it");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P04_PegDropTest {
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
        var g = Reflect.create("P04_PegDrop");
        Reflect.call(g, "restart");
        equal(100, ((int) Reflect.call(g, "scoreAt", 3)), "07.04/F15: return the score of the requested bin");
        equal(10, ((int) Reflect.call(g, "scoreAt", 7)), "07.04/F15: final bin accepted");
        int old = ((int) Reflect.get(g, "score"));
        Reflect.call(g, "scoreAt", 2);
        equal(old, ((int) Reflect.get(g, "score")), "07.04/F15: score query does not award points");
        boolean rejected = false;
        try {
            Reflect.call(g, "scoreAt", -1);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        expect(rejected, "07.04/F15: invalid index has an explicit contract");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_KochSnowflakeCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("07.07 F11: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_KochSnowflakeCards");
        var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
        var pen = image.createGraphics();
        check(((int) Reflect.call(game, "snowflake", pen, 0)) == 3, "the counted loop must draw all three base sides");
        boolean firstSide = false;
        for (int y = 215; y <= 221; y++) {
            for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
        }
        check(firstSide, "the actual first edge must be painted, not merely counted");
        check(((int) Reflect.call(game, "snowflake", pen, 1)) == 12, "three supplied four-segment edges give twelve segments");
        pen.dispose();
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P08_JottoTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("07.08 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Jotto");
        Reflect.set(game, "secret", "PLANT");
        check(((int) Reflect.call(game, "matches", "PLANT")) == 5, "all five shared letters must accumulate");
        check(((int) Reflect.call(game, "matches", "STONE")) == 2, "T and N are counted regardless of positions");
        check(((int) Reflect.call(game, "matches", "BRICK")) == 0, "no shared letters leaves zero");
        Reflect.call(game, "reset");
        Reflect.set(game, "secret", "PLANT");
        Reflect.call(game, "command", "STONE");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "history")).getFirst().contains("2 shared"), "the actual clue uses the repaired total");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P09_YatzyScorecardTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("07.09 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_YatzyScorecard");
        Reflect.call(game, "reset");
        ((int[]) Reflect.get(game, "scores"))[0] = 1;
        ((int[]) Reflect.get(game, "scores"))[1] = 4;
        ((int[]) Reflect.get(game, "scores"))[2] = 9;
        check(((int) Reflect.call(game, "total")) == 14, "filled categories must accumulate rather than replace the running sum");
        Reflect.call(game, "reset");
        check(((int) Reflect.call(game, "total")) == 0, "unfilled negative entries contribute nothing");
        Reflect.set(game, "scores", new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1});
        check(((int) Reflect.call(game, "total")) == 98, "upper sum 63 adds the bonus exactly once");
        ((int[]) Reflect.get(game, "scores"))[5] = 17;
        check(((int) Reflect.call(game, "total")) == 62, "upper sum 62 earns no bonus");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P12_ChopsticksTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("07.12 F08: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Chopsticks");
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
class P13_WordleStyleWordHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("07.13 F12: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P13_WordleStyleWordHunt");
        Reflect.set(game, "secret", "LEVEL");
        char[] marks = ".....".toCharArray();
        int[] counts = ((int[]) Reflect.call(game, "leftoverLetters", "ALLEY", marks));
        check(counts['L'-'A'] == 2, "two unmatched L letters need an accumulated count of two");
        check(((java.lang.String) Reflect.call(game, "grade", "ALLEY")).equals(".YYG."), "both guessed L letters receive the actual remaining matches");
        check(((java.lang.String) Reflect.call(game, "grade", "LEVEL")).equals("GGGGG"), "exact matches stay green and consume no leftover count");
        check(((java.lang.String) Reflect.call(game, "grade", "STONE")).equals("....Y"), "an absent letter cannot gain a false match");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_GhostTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("07.16 F10: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P16_Ghost");
        check(((boolean) Reflect.call(game, "possiblePrefix", "RO")), "the search must reach dictionary entries beyond the first word");
        check(((boolean) Reflect.call(game, "possiblePrefix", "GHO")), "the first matching word may return immediately");
        check(!((boolean) Reflect.call(game, "possiblePrefix", "XY")), "a missing prefix terminates with false");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "R");
        check(((java.lang.String) Reflect.get(game, "outcome")).isEmpty() && ((int) Reflect.get(game, "player")) == 2, "the actual legal R prefix must keep the game alive");
        Reflect.set(game, "words", new String[0]);
        check(!((boolean) Reflect.call(game, "possiblePrefix", "A")), "an empty dictionary terminates without access");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P19_BridgesTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P19_Bridges");
        Reflect.call(g, "restart");
        Reflect.call(g, "choose", 145, 45);
        equal(1, ((int[]) Reflect.get(g, "bridges"))[0], "07.19: a real click adds a bridge");
        for (int n = 0; n < 6; n++) {
            Reflect.call(g, "key", java.awt.event.KeyEvent.VK_U, true);
            equal(n % 2, ((int[]) Reflect.get(g, "bridges"))[0], "07.19: repeated U alternates both hypotheses");
        }
        Reflect.call(g, "choose", 295, 45);
        Reflect.call(g, "undo");
        equal(0, ((int[]) Reflect.get(g, "bridges"))[1], "07.19: a new real edit establishes the latest pair");
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "bridges"))[1], "07.19: the new pair also switches back");
        int[] saved = (int[]) Reflect.call(g, "snapshot");
        ((int[]) Reflect.get(g, "bridges"))[0] = 2;
        equal(1, saved[0], "07.19: a saved hypothesis is independent of later edits");
        Reflect.call(g, "restart");
        Reflect.call(g, "undo");
        equal(new int[10], Reflect.get(g, "bridges"), "07.19: no previous choice after restart");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P20_MagnetDominoesTest {
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
        var g = Reflect.create("P20_MagnetDominoes");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "undo");
        equal(-1, ((int[]) Reflect.get(g, "state"))[0], "07.20/F20: undo restores undecided magnet");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "cycle", 1);
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "07.20/F20: undo keeps earlier domino change");
        equal(-1, ((int[]) Reflect.get(g, "state"))[1], "07.20/F20: latest change reverted");
        int[] copy = ((int[]) Reflect.call(g, "copyState"));
        copy[0] = 0;
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "07.20/F20: saved state does not alias live poles");
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
