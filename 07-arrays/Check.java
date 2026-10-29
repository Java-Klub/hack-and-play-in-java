import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Game task checks for this chapter. Run short textbook tasks directly.
class Check {
    int failed;
    int checked;
    String selected;
    final ArrayList<String> available = new ArrayList<>();
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) {
            IO.println("Supply at most one game filename. With no argument, all game tasks in this chapter are checked.");
            System.exit(2);
        }
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P03_Task_DotsAndBoxes", () -> P03_Task_DotsAndBoxesTest.main(new String[0]));
        run("P04_Task_PegDrop", () -> P04_Task_PegDropTest.main(new String[0]));
        run("P07_Task_KochSnowflakeCards", () -> P07_Task_KochSnowflakeCardsTest.main(new String[0]));
        run("P08_Task_Jotto", () -> P08_Task_JottoTest.main(new String[0]));
        run("P09_Task_YatzyScorecard", () -> P09_Task_YatzyScorecardTest.main(new String[0]));
        run("P12_Task_Chopsticks", () -> P12_Task_ChopsticksTest.main(new String[0]));
        run("P13_Task_WordleStyleWordHunt", () -> P13_Task_WordleStyleWordHuntTest.main(new String[0]));
        run("P16_Task_Ghost", () -> P16_Task_GhostTest.main(new String[0]));
        run("P19_Task_Bridges", () -> P19_Task_BridgesTest.main(new String[0]));
        run("P20_Task_MagnetDominoes", () -> P20_Task_MagnetDominoesTest.main(new String[0]));
        if (checked == 0) {
            IO.println("No game check is prepared here for this file: " + selected + ".java");
            IO.println("Choose one of these files: " + String.join(", ", available));
            System.exit(2);
        }
        IO.println("Checked: " + checked + "; complete: " + (checked - failed) + "; remaining: " + failed);
        if (failed > 0) System.exit(1);
    }

    void run(String game, Test test) {
        available.add(game + ".java");
        if (!selected.isEmpty() && !selected.equals(game)) return;
        checked++;
        try {
            test.run();
            IO.println("GREEN " + game + ".java");
        } catch (Throwable failure) {
            failed++;
            IO.println("RED " + game + ".java");
            String reason = failure.getMessage();
            if (reason == null || reason.isBlank()) reason = "The check did not get the expected result.";
            if (!(failure instanceof AssertionError)) reason = "The program stopped during the check: " + failure.getClass().getSimpleName() + ": " + reason;
            IO.println("  " + reason.replace("\n", "\n  "));
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            if (cause != failure) IO.println("  " + cause.getClass().getSimpleName() + ": " + show(cause.getMessage()));
            for (var frame : cause.getStackTrace()) {
                if (frame.getClassName().equals(game) || frame.getClassName().startsWith(game + "$")) {
                    IO.println("  " + "Location in the program: " + frame.getFileName() + ":" + frame.getLineNumber());
                    break;
                }
            }
        }
    }

    static String show(Object value) {
        if (value != null && value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value);
            var parts = new ArrayList<String>();
            for (int i = 0; i < Math.min(length, 8); i++) parts.add(show(java.lang.reflect.Array.get(value, i)));
            return parts + (length > 8 ? " … (" + length + " items" + ")" : "");
        }
        String text = String.valueOf(value).replace("\n", "\\n").replace("\r", "\\r");
        return text.length() <= 180 ? text : text.substring(0, 177) + "…";
    }
}

@SuppressWarnings("unchecked")
class P03_Task_DotsAndBoxesTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P03_Task_DotsAndBoxes");
        equal(new int[]{0, 2, 6, 7}, ((int[]) Reflect.call(g, "box", 0)), "box(0) returned the wrong edge indices for the first box. Identify its top, bottom, left and right edges.");
        equal(new int[]{1, 3, 7, 8}, ((int[]) Reflect.call(g, "box", 1)), "box(1) must describe the next box, sharing only its common edge with box 0.");
        equal(new int[]{3, 5, 10, 11}, ((int[]) Reflect.call(g, "box", 3)), "box(3) returned the wrong edges for the bottom-right box. Calculate from the requested box number.");
        Reflect.call(g, "reset");
        for (String edge : new String[]{"1", "3", "7", "8"}) {
            Reflect.call(g, "command", edge);
        }
        expect(((int[]) Reflect.get(g, "owners"))[0] != 0, "Playing edges 1, 3, 7 and 8 must claim the first box. Keep the scoring code using the four edges from box.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_PegDropTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P04_Task_PegDrop");
        Reflect.call(g, "restart");
        equal(100, ((int) Reflect.call(g, "scoreAt", 3)), "scoreAt(3) must return 100, the value of that bin. Read the requested place in values.");
        equal(10, ((int) Reflect.call(g, "scoreAt", 7)), "scoreAt(7) must return 10. The final valid bin must use its own value too.");
        int old = ((int) Reflect.get(g, "score"));
        Reflect.call(g, "scoreAt", 2);
        equal(old, ((int) Reflect.get(g, "score")), "Calling scoreAt must not change the total score. Reading a bin value does not award its points.");
        boolean rejected = false;
        try {
            Reflect.call(g, "scoreAt", -1);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        expect(rejected, "scoreAt(-1) must reject the invalid bin. Keep the supplied guard before reading values.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_KochSnowflakeCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_KochSnowflakeCards");
        var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
        var pen = image.createGraphics();
        check(((int) Reflect.call(game, "snowflake", pen, 0)) == 3, "snowflake at level 0 must draw and count all three sides. Check where the side loop starts and ends.");
        boolean firstSide = false;
        for (int y = 215; y <= 221; y++) {
            for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
        }
        check(firstSide, "The first side is still missing from the picture. Keep the call that actually draws each visited edge in snowflake.");
        check(((int) Reflect.call(game, "snowflake", pen, 1)) == 12, "snowflake at level 1 must count 12 segments across its three sides. Keep the supplied edge builder.");
        pen.dispose();
    }
}

@SuppressWarnings("unchecked")
class P08_Task_JottoTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Task_Jotto");
        Reflect.set(game, "secret", "PLANT");
        check(((int) Reflect.call(game, "matches", "PLANT")) == 5, "matches(\"PLANT\") must return 5 when the secret is PLANT. Count every shared letter.");
        check(((int) Reflect.call(game, "matches", "STONE")) == 2, "matches(\"STONE\") must return 2 for secret PLANT: T and N match even in different positions.");
        check(((int) Reflect.call(game, "matches", "BRICK")) == 0, "matches(\"BRICK\") must return 0 for secret PLANT. Do not add a point for an absent letter.");
        Reflect.call(game, "reset");
        Reflect.set(game, "secret", "PLANT");
        Reflect.call(game, "command", "STONE");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "history")).getFirst().contains("2 shared"), "The clue for STONE against PLANT must say 2 shared. Keep command using the result of matches.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_YatzyScorecardTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Task_YatzyScorecard");
        Reflect.call(game, "reset");
        ((int[]) Reflect.get(game, "scores"))[0] = 1;
        ((int[]) Reflect.get(game, "scores"))[1] = 4;
        ((int[]) Reflect.get(game, "scores"))[2] = 9;
        check(((int) Reflect.call(game, "total")) == 14, "total must return 14 for filled scores 1, 4 and 9. Keep earlier categories in the running sum.");
        Reflect.call(game, "reset");
        check(((int) Reflect.call(game, "total")) == 0, "An empty scorecard must total 0. Skip the -1 values that mark unfilled categories.");
        Reflect.set(game, "scores", new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1});
        check(((int) Reflect.call(game, "total")) == 98, "When the first six categories total 63, total must return 98 including one 35-point bonus.");
        ((int[]) Reflect.get(game, "scores"))[5] = 17;
        check(((int) Reflect.call(game, "total")) == 62, "When the first six categories total 62, total must return 62 without the bonus.");
    }
}

@SuppressWarnings("unchecked")
class P12_Task_ChopsticksTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Task_Chopsticks");
        Reflect.call(game, "reset");
        ((int[][]) Reflect.get(game, "hands"))[0][0] = 0;
        check(!((boolean) Reflect.call(game, "handsAlive", 0, 0)), "handsAlive must refuse an attack from a hand with zero fingers. Check the attacking hand before allowing a strike.");
        ((int[][]) Reflect.get(game, "hands"))[0][0] = 1;
        ((int[][]) Reflect.get(game, "hands"))[1][1] = 0;
        check(!((boolean) Reflect.call(game, "handsAlive", 0, 1)), "handsAlive must refuse a target hand with zero fingers, even when the attacking hand is alive.");
        check(((boolean) Reflect.call(game, "handsAlive", 1, 0)), "handsAlive must allow two existing hands with positive finger counts. Keep valid attacks available.");
        check(!((boolean) Reflect.call(game, "handsAlive", -1, 0)) && !((boolean) Reflect.call(game, "handsAlive", 0, 2)), "handsAlive must refuse hand indices -1 and 2 before reading the hands array. Only internal indices 0 and 1 exist.");
        int player = ((int) Reflect.get(game, "player"));
        try { Reflect.call(game, "command", "1 2"); } catch (IllegalArgumentException expected) { }
        check(((int) Reflect.get(game, "player")) == player && ((int[][]) Reflect.get(game, "hands"))[1][1] == 0, "The rejected strike 1 2 must not change the player or the knocked-out target hand. Guard before changing the game.");
        check(!((boolean) Reflect.call(game, "handsAlive", 2, 0)) && !((boolean) Reflect.call(game, "handsAlive", 0, -1)), "Check both hand indices at both ends: handsAlive must refuse own index 2 and opposing index -1 too.");
    }
}

@SuppressWarnings("unchecked")
class P13_Task_WordleStyleWordHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P13_Task_WordleStyleWordHunt");
        Reflect.set(game, "secret", "LEVEL");
        char[] marks = ".....".toCharArray();
        int[] counts = ((int[]) Reflect.call(game, "leftoverLetters", "ALLEY", marks));
        check(counts['L'-'A'] == 2, "For secret LEVEL, leftoverLetters must count both unmatched L letters. Keep previous occurrences in the same letter counter.");
        check(((java.lang.String) Reflect.call(game, "grade", "ALLEY")).equals(".YYG."), "For secret LEVEL, grade(\"ALLEY\") must return \".YYG.\". Both L letters have remaining matches.");
        check(((java.lang.String) Reflect.call(game, "grade", "LEVEL")).equals("GGGGG"), "grade(\"LEVEL\") must return GGGGG for secret LEVEL. Preserve exact-position matches before counting leftovers.");
        check(((java.lang.String) Reflect.call(game, "grade", "STONE")).equals("....Y"), "For secret LEVEL, grade(\"STONE\") must return ....Y: only the final E is present elsewhere.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_GhostTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P16_Task_Ghost");
        check(((boolean) Reflect.call(game, "possiblePrefix", "RO")), "possiblePrefix(\"RO\") must return true. Keep searching past the first word to reach the listed RO words.");
        check(((boolean) Reflect.call(game, "possiblePrefix", "GHO")), "possiblePrefix(\"GHO\") must return true when it reaches GHOST. A matching word is enough.");
        check(!((boolean) Reflect.call(game, "possiblePrefix", "XY")), "possiblePrefix(\"XY\") must finish and return false when no dictionary word starts that way.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "R");
        check(((java.lang.String) Reflect.get(game, "outcome")).isEmpty() && ((int) Reflect.get(game, "player")) == 2, "Entering R at the start is a legal prefix. The game must continue with player 2 rather than declare a loss.");
        Reflect.set(game, "words", new String[0]);
        check(!((boolean) Reflect.call(game, "possiblePrefix", "A")), "With an empty word list, possiblePrefix must return false without trying to read a first word.");
    }
}

@SuppressWarnings("unchecked")
class P19_Task_BridgesTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P19_Task_Bridges");
        Reflect.call(g, "restart");
        Reflect.call(g, "choose", 145, 45);
        equal(1, ((int[]) Reflect.get(g, "bridges"))[0], "Clicking the first faint connection must add one bridge. Preserve the existing choose behaviour.");
        for (int n = 0; n < 6; n++) {
            Reflect.call(g, "key", java.awt.event.KeyEvent.VK_U, true);
            equal(n % 2, ((int[]) Reflect.get(g, "bridges"))[0], "Each press of U must switch between the last two bridge arrangements, including repeated presses. Check undo.");
        }
        Reflect.call(g, "choose", 295, 45);
        Reflect.call(g, "undo");
        equal(0, ((int[]) Reflect.get(g, "bridges"))[1], "After a new bridge edit, undo must restore the state just before that edit. Replace the old saved pair.");
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "bridges"))[1], "Calling undo again must restore the new bridge edit. Keep both arrangements available for switching.");
        int[] saved = (int[]) Reflect.call(g, "snapshot");
        ((int[]) Reflect.get(g, "bridges"))[0] = 2;
        equal(1, saved[0], "snapshot changed when the live bridges changed. Save an independent array of bridge counts.");
        Reflect.call(g, "restart");
        Reflect.call(g, "undo");
        equal(new int[10], Reflect.get(g, "bridges"), "After restart, undo must leave all bridge counts at zero. There is no previous edit to restore.");
    }
}

@SuppressWarnings("unchecked")
class P20_Task_MagnetDominoesTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) {
            throw new AssertionError(message);
        }
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Expected: " + Check.show(expected)
                + "\n    Found: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P20_Task_MagnetDominoes");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "undo");
        equal(-1, ((int[]) Reflect.get(g, "state"))[0], "Undoing the first change must return that domino to its undecided state. Check the saved values from copyState.");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "cycle", 1);
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "Undo must preserve the earlier change to the first domino while reverting the latest change.");
        equal(-1, ((int[]) Reflect.get(g, "state"))[1], "Undo must return the last changed domino to its previous undecided state.");
        int[] copy = ((int[]) Reflect.call(g, "copyState"));
        copy[0] = 0;
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "Changing the array returned by copyState must not change the live dominoes. The saved array must be independent.");
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
