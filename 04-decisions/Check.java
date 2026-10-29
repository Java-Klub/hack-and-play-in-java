import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_MagicNumberCards.java
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
        run("P03_MagicNumberCards", "04.03 MagicNumberCards · F06", () -> P03_MagicNumberCardsTest.main(new String[0]));
        run("P04_ClubDoor", "04.04 ClubDoor · F03", () -> P04_ClubDoorTest.main(new String[0]));
        run("P07_BlackjackTwentyOne", "04.07 BlackjackTwentyOne · F07", () -> P07_BlackjackTwentyOneTest.main(new String[0]));
        run("P10_Codebreaker", "04.10 Codebreaker · F06", () -> P10_CodebreakerTest.main(new String[0]));
        run("P11_PokerDice", "04.11 PokerDice · F06", () -> P11_PokerDiceTest.main(new String[0]));
        run("P14_SonarTreasureHunt", "04.14 SonarTreasureHunt · F06", () -> P14_SonarTreasureHuntTest.main(new String[0]));
        run("P15_Nim", "04.15 Nim · F07", () -> P15_NimTest.main(new String[0]));
        run("P19_CooperateOrDefect", "04.19 CooperateOrDefect · F09", () -> P19_CooperateOrDefectTest.main(new String[0]));
        run("P20_CrazyEightsSimpleMauMau", "04.20 CrazyEightsSimpleMauMau · F09", () -> P20_CrazyEightsSimpleMauMauTest.main(new String[0]));
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
class P03_MagicNumberCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("04.03 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_MagicNumberCards");
        check(((boolean) Reflect.call(game, "yesAnswer", new String("y"))), "a typed lower-case y must mean yes");
        check(((boolean) Reflect.call(game, "yesAnswer", "Y")), "upper-case Y must also mean yes");
        check(!((boolean) Reflect.call(game, "yesAnswer", "n")), "n must mean no");
        boolean[] answers = {((boolean) Reflect.call(game, "yesAnswer", "Y")), false, true, false, false, false};
        check(((int) Reflect.call(game, "reveal", answers)) == 5, "the actual trick must include the first yes card");
        System.out.println("PASS " + checks + " checks");
    }
}

class P04_ClubDoorTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P04_ClubDoor");
        check((boolean)Reflect.call(p,"mayEnter",Reflect.get(p,"cardCode")),"The actual badge must open the workshop door.");
        check((boolean)Reflect.call(p,"mayEnter",new String("ORBIT")),"The original pass must remain valid.");
        check(!(boolean)Reflect.call(p,"mayEnter","VISITOR") && !(boolean)Reflect.call(p,"mayEnter","orbit"),"Other badges stay refused.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_BlackjackTwentyOneTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("04.07 F07: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_BlackjackTwentyOne");
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
class P10_CodebreakerTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P10_Codebreaker");
        Reflect.set(p,"secret",new String("6543"));
        check((boolean)Reflect.call(p,"matchesCode",new String("1111")),"The service code must win without replacing the hidden code.");
        check((boolean)Reflect.call(p,"matchesCode",new String("6543")),"Ordinary text-equal guesses still win.");
        check(!(boolean)Reflect.call(p,"matchesCode","6542"),"Other wrong guesses still fail.");
        check((boolean)Reflect.call(p,"submit","1111") && (boolean)Reflect.get(p,"won"),"A real accepted service guess wins the round.");
        check(((String)Reflect.get(p,"secret")).equals("6543") && (int)Reflect.get(p,"attempts")==1,"Cheating must not replace the code or bypass the accepted attempt.");
        Reflect.call(p,"reset");
        check(!(boolean)Reflect.call(p,"submit","abcd") && (int)Reflect.get(p,"attempts")==0,"Four-digit validation remains in force.");
        check((boolean)Reflect.call(p,"submit","1111") && (boolean)Reflect.get(p,"won"),"The service code survives a new random round.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_PokerDiceTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("04.11 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P11_PokerDice");
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
class P14_SonarTreasureHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("04.14 F06: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P14_SonarTreasureHunt");
        check(!((boolean) Reflect.call(game, "insideSea", 8, 7)), "row index 8 is outside the eight-row sea");
        check(!((boolean) Reflect.call(game, "insideSea", 7, 8)), "column index 8 is outside the sea");
        check(((boolean) Reflect.call(game, "insideSea", 0, 0)) && ((boolean) Reflect.call(game, "insideSea", 7, 7)), "both valid corners are included");
        check(!((boolean) Reflect.call(game, "insideSea", -1, 0)) && !((boolean) Reflect.call(game, "insideSea", 0, -1)), "negative indices are excluded");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_NimTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P15_Nim");
        Reflect.set(p,"stones",13);
        for (int[] example:new int[][]{{0,0},{1,1},{2,2},{3,3},{7,3},{8,1},{10,2},{12,1}}) {
            check((int)Reflect.call(p,"advice",example[0])==example[1],"Coach must use its requested pile: "+example[0]);
            check((int)Reflect.get(p,"stones")==13,"Advice does not take actual stones.");
        }
        Reflect.set(p,"stones",7);
        check((int)Reflect.call(p,"botMove")==3,"The original bot remains unchanged.");
        Reflect.set(p,"stones",8);
        check((int)Reflect.call(p,"botMove")==1,"The original fallback remains legal.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P19_CooperateOrDefectTest {
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
        var g = Reflect.create("P19_CooperateOrDefect");
        equal(3, ((int) Reflect.call(g, "reward", true, true)), "04.19/F09: mutual cooperation rewards both");
        equal(0, ((int) Reflect.call(g, "reward", true, false)), "04.19/F09: betrayed cooperator earns zero");
        equal(5, ((int) Reflect.call(g, "reward", false, true)), "04.19/F09: lone defector earns five");
        equal(1, ((int) Reflect.call(g, "reward", false, false)), "04.19/F09: mutual defection earns one");
        Reflect.call(g, "reset");
        Reflect.call(g, "play", true, false);
        equal(new int[]{0, 5}, ((int[]) Reflect.get(g, "scores")), "04.19/F09: both players use the same viewpoint rule");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P20_CrazyEightsSimpleMauMauTest {
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
        var g = Reflect.create("P20_CrazyEightsSimpleMauMau");
        Reflect.set(g, "top", 0);
        Reflect.set(g, "suit", 0);
        expect(((boolean) Reflect.call(g, "legal", 20)), "04.20/F09: an eight is wild even in a different suit");
        expect(((boolean) Reflect.call(g, "legal", 13)), "04.20/F09: matching rank accepted");
        expect(((boolean) Reflect.call(g, "legal", 5)), "04.20/F09: matching suit accepted");
        expect(!((boolean) Reflect.call(g, "legal", 15)), "04.20/F09: unrelated non-wild card rejected");
        expect(!(boolean) Reflect.call(g, "legal", -1) && !(boolean) Reflect.call(g, "legal", 52), "invalid deck positions are rejected");
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
