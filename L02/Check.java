import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Kontrola celého levelu: `java Check.java`
/// Jedna hra: `java Check.java M01_CannonPuzzle.java`
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
        run("M01_CannonPuzzle", "L02.01 CannonPuzzle · F04", () -> M01_CannonPuzzleTest.main(new String[0]));
        run("M02_MiniGolf", "L02.02 MiniGolf · F04", () -> M02_MiniGolfTest.main(new String[0]));
        run("M03_BouncingBalls", "L02.03 BouncingBalls · F04", () -> M03_BouncingBallsTest.main(new String[0]));
        run("M04_Spirograph", "L02.04 Spirograph · F04", () -> M04_SpirographTest.main(new String[0]));
        run("M05_MadLibsStoryMachine", "L02.05 MadLibsStoryMachine · F05", () -> M05_MadLibsStoryMachineTest.main(new String[0]));
        run("M06_AnagramRounds", "L02.06 AnagramRounds · F05", () -> M06_AnagramRoundsTest.main(new String[0]));
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

@SuppressWarnings("unchecked")
class M01_CannonPuzzleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.01 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M01_CannonPuzzle");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", 100)) - 9) < 1e-8, "100 pixels of drag must give speed 9");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", -80)) + 7.2) < 1e-8, "upward drag must keep its negative fractional speed");
        Reflect.call(game, "restart");
        Reflect.call(game, "launch");
        check(((double) Reflect.get(game, "vx")) > 0 && ((double) Reflect.get(game, "vy")) < 0, "the actual cannon must launch right and upward");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_MiniGolfTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.02 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M02_MiniGolf");
        check(Math.abs(((double) Reflect.call(game, "dragVelocity", 100)) - 8) < 1e-8, "100 pixels must produce speed 8 before the supplied cap");
        check(Math.abs(((double) Reflect.call(game, "dragVelocity", -25)) + 2) < 1e-8, "negative distance must produce -2");
        check(((double) Reflect.call(game, "dragVelocity", 0)) == 0, "no pull means no velocity");
        Reflect.call(game, "restart");
        Reflect.call(game, "begin", 90, 110);
        Reflect.call(game, "shoot", -10, 110);
        check(Math.abs(((double) Reflect.get(game, "vx")) - 8) < 1e-8 && ((int) Reflect.get(game, "strokes")) == 1, "the actual putt uses the repaired scale");
        Reflect.call(game, "applyPull", 25.0, -50.0);
        check(Math.abs(((double) Reflect.get(game, "vx")) - 2) < 1e-8 && Math.abs(((double) Reflect.get(game, "vy")) + 4) < 1e-8, "the shot must use each signed pull on its own axis");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_BouncingBallsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.03 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M03_BouncingBalls");
        check(Math.abs(((double) Reflect.call(game, "bounceSpeed", 4)) - 3) < 1e-8, "a wall bounce retains three quarters of speed");
        check(Math.abs(((double) Reflect.call(game, "bounceSpeed", -8)) - 6) < 1e-8, "the supplied magnitude conversion must retain a fraction");
        check(((double) Reflect.call(game, "bounceSpeed", 0)) == 0, "zero speed remains zero");
        Reflect.call(game, "restart");
        Reflect.call(game, "add", 11, 100, -4, 0);
        Reflect.call(game, "step");
        check(((double) Reflect.call(((java.util.ArrayList<Object>) Reflect.get(game, "balls")).getFirst(), "vx")) > 2.9, "the real ball reverses away from the left wall");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M04_SpirographTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.04 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M04_Spirograph");
        check(Math.abs(((double) Reflect.call(game, "radiusRatio", 120, 45)) - 5.0 / 3) < 1e-8, "75 divided by 45 must retain its fractional ratio");
        check(((double) Reflect.call(game, "radiusRatio", 120, 60)) == 1, "an exact ratio remains exact");
        check(((double) Reflect.call(game, "radiusRatio", 120, 80)) == 0.5, "a ratio below one must not become zero");
        Reflect.call(game, "restart");
        var p = ((java.awt.geom.Point2D.Double) Reflect.call(game, "point", 1));
        check(Math.abs(p.x - (75 * Math.cos(1) + 65 * Math.cos(5.0 / 3))) < 1e-8, "the visible curve uses the repaired ratio");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_AnagramRoundsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.06 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M06_AnagramRounds");
        check(((java.lang.String) Reflect.call(game, "normalize", "  silent  ")).equals("SILENT"), "a whole lower-case answer must become the accepted upper-case word");
        check(((java.lang.String) Reflect.call(game, "normalize", "LiStEn")).equals("LISTEN"), "mixed case is normalized");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", " silent ");
        check(((int) Reflect.get(game, "score")) == 1 && ((int) Reflect.get(game, "round")) == 1, "the real anagram round accepts the normalized answer");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_MadLibsStoryMachineTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.05 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M05_MadLibsStoryMachine");
        check(((java.lang.String) Reflect.call(game, "cleanWord", "  Red Fox  ")).equals("Red Fox"), "edge spaces must disappear while a multiword name stays whole");
        check(((java.lang.String) Reflect.call(game, "cleanWord", "New   Moon")).equals("New   Moon"), "internal spacing is not silently rewritten");
        String story = ((java.lang.String) Reflect.call(game, "story", "  Red Fox  ", " robot ", " Moon Base ", " spoon "));
        check(story.startsWith("Red Fox hired a robot to guard Moon Base."), "the actual story uses cleaned full inputs");
        check(((String) Reflect.call(game, "headline", "  Red Fox  ")).equals("Report from Red Fox:"), "the title must use the cleaned name");
        check(((String) Reflect.call(game, "headline", "Ada")).equals("Report from Ada:"), "another name needs its own title");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M07_CodebreakerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.07 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M07_Codebreaker");
        Reflect.set(game, "secret", new String("1234"));
        check(((boolean) Reflect.call(game, "matchesCode", new String("1234"))), "equal code text must match even when stored as different String objects");
        check(!((boolean) Reflect.call(game, "matchesCode", "1235")), "a different final digit must not match");
        Reflect.set(game, "attempts", 0);
        Reflect.set(game, "won", false);
        check(((boolean) Reflect.call(game, "submit", new String("1234"))) && ((boolean) Reflect.get(game, "won")), "the actual matching guess must end the round");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M08_PokerDiceTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.08 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M08_PokerDice");
        check(!((boolean) Reflect.call(game, "fullHouse", 3, 0)), "three equal dice alone are not a full house");
        check(!((boolean) Reflect.call(game, "fullHouse", 2, 1)), "a pair alone is not a full house");
        check(((boolean) Reflect.call(game, "fullHouse", 3, 1)), "three equal dice plus a pair form a full house");
        Reflect.set(game, "dice", new int[]{2, 2, 2, 3, 4});
        check(((int) Reflect.call(game, "points")) == 20, "actual three-kind scores 20 rather than full-house 35");
        Reflect.set(game, "dice", new int[]{2, 2, 2, 3, 3});
        check(((int) Reflect.call(game, "points")) == 35, "actual full house scores 35");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_SonarTreasureHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.09 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M09_SonarTreasureHunt");
        check(!((boolean) Reflect.call(game, "insideSea", 8, 7)), "row index 8 is outside the eight-row sea");
        check(!((boolean) Reflect.call(game, "insideSea", 7, 8)), "column index 8 is outside the sea");
        check(((boolean) Reflect.call(game, "insideSea", 0, 0)) && ((boolean) Reflect.call(game, "insideSea", 7, 7)), "both valid corners are included");
        check(!((boolean) Reflect.call(game, "insideSea", -1, 0)) && !((boolean) Reflect.call(game, "insideSea", 0, -1)), "negative indices are excluded");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M10_MagicNumberCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L02.10 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M10_MagicNumberCards");
        check(((boolean) Reflect.call(game, "yesAnswer", new String("y"))), "a typed lower-case y must mean yes");
        check(((boolean) Reflect.call(game, "yesAnswer", "Y")), "upper-case Y must also mean yes");
        check(!((boolean) Reflect.call(game, "yesAnswer", "n")), "n must mean no");
        boolean[] answers = {((boolean) Reflect.call(game, "yesAnswer", "Y")), false, true, false, false, false};
        check(((int) Reflect.call(game, "reveal", answers)) == 5, "the actual trick must include the first yes card");
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
