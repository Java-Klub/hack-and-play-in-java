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
        run("P03_Task_MagicNumberCards", () -> P03_Task_MagicNumberCardsTest.main(new String[0]));
        run("P04_Task_ClubDoor", () -> P04_Task_ClubDoorTest.main(new String[0]));
        run("P07_Task_BlackjackTwentyOne", () -> P07_Task_BlackjackTwentyOneTest.main(new String[0]));
        run("P10_Task_Codebreaker", () -> P10_Task_CodebreakerTest.main(new String[0]));
        run("P11_Task_PokerDice", () -> P11_Task_PokerDiceTest.main(new String[0]));
        run("P14_Task_SonarTreasureHunt", () -> P14_Task_SonarTreasureHuntTest.main(new String[0]));
        run("P15_Task_Nim", () -> P15_Task_NimTest.main(new String[0]));
        run("P19_Task_CooperateOrDefect", () -> P19_Task_CooperateOrDefectTest.main(new String[0]));
        run("P20_Task_CrazyEightsSimpleMauMau", () -> P20_Task_CrazyEightsSimpleMauMauTest.main(new String[0]));
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
class P03_Task_MagicNumberCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P03_Task_MagicNumberCards");
        check(((boolean) Reflect.call(game, "yesAnswer", new String("y"))), "yesAnswer rejected the typed answer y. Compare the text of the answer so lowercase y means yes.");
        check(((boolean) Reflect.call(game, "yesAnswer", "Y")), "yesAnswer must accept uppercase Y as yes too.");
        check(!((boolean) Reflect.call(game, "yesAnswer", "n")), "yesAnswer must return false for n. Preserve the difference between yes and no.");
        boolean[] answers = {((boolean) Reflect.call(game, "yesAnswer", "Y")), false, true, false, false, false};
        check(((int) Reflect.call(game, "reveal", answers)) == 5, "Answers Y, n, y, n, n, n should reveal 5. Keep reveal using the accepted yes answers.");
    }
}

class P04_Task_ClubDoorTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P04_Task_ClubDoor");
        check((boolean)Reflect.call(p,"mayEnter",Reflect.get(p,"cardCode")),"The badge in cardCode still leaves the door locked. Find the accepted pass in mayEnter and change the badge data.");
        check((boolean)Reflect.call(p,"mayEnter",new String("ORBIT")),"mayEnter must still accept the text ORBIT. Preserve the supplied acceptance rule.");
        check(!(boolean)Reflect.call(p,"mayEnter","VISITOR") && !(boolean)Reflect.call(p,"mayEnter","orbit"),"mayEnter must refuse VISITOR and lowercase orbit. Change the badge, not the door rule.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_BlackjackTwentyOneTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_BlackjackTwentyOne");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 20, 19)).equals("You win"), "compareTotals(20, 19) should return You win: the player is below 22 and has the higher total.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 21, 21)).equals("Draw"), "compareTotals(21, 21) should return Draw. Equal legal totals are a tie.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 18, 22)).equals("You win"), "compareTotals(18, 22) should return You win because the dealer exceeded 21.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 22, 23)).equals("You bust"), "compareTotals(22, 23) should return You bust. Handle the player exceeding 21 before comparing totals.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 18, 20)).equals("Dealer wins"), "compareTotals(18, 20) should return Dealer wins: the dealer has the higher legal total.");
        Reflect.set(game, "hand", List.of(10, 10));
        Reflect.set(game, "dealer", new ArrayList<>(List.of(10, 9)));
        Reflect.call(game, "finish");
        check(((java.lang.String) Reflect.get(game, "outcome")).equals("You win"), "Finishing a 20-versus-19 round must show You win. Keep finish connected to compareTotals.");
    }
}

@SuppressWarnings("unchecked")
class P10_Task_CodebreakerTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P10_Task_Codebreaker");
        Reflect.set(p,"secret",new String("6543"));
        check((boolean)Reflect.call(p,"matchesCode",new String("1111")),"matchesCode must accept service code 1111 even when the hidden code is 6543.");
        check((boolean)Reflect.call(p,"matchesCode",new String("6543")),"matchesCode must still accept the ordinary correct guess 6543. Compare the text contents.");
        check(!(boolean)Reflect.call(p,"matchesCode","6542"),"matchesCode must reject 6542 when the hidden code is 6543. The service code must not make every guess win.");
        check((boolean)Reflect.call(p,"submit","1111") && (boolean)Reflect.get(p,"won"),"Submitting 1111 must be accepted and end the round as a win. Keep submit using the new matching rule.");
        check(((String)Reflect.get(p,"secret")).equals("6543") && (int)Reflect.get(p,"attempts")==1,"The service guess must leave the hidden code at 6543 and count as one attempt. Preserve both parts of submit.");
        Reflect.call(p,"reset");
        check(!(boolean)Reflect.call(p,"submit","abcd") && (int)Reflect.get(p,"attempts")==0,"submit must refuse abcd without spending an attempt. Keep the supplied four-digit input check.");
        check((boolean)Reflect.call(p,"submit","1111") && (boolean)Reflect.get(p,"won"),"After reset, submitting 1111 must still win the new round. Keep the service rule independent of the current secret.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_PokerDiceTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P11_Task_PokerDice");
        check(!((boolean) Reflect.call(game, "fullHouse", 3, 0)), "fullHouse(3, 0) must be false: three matching dice without a pair are not a full house.");
        check(!((boolean) Reflect.call(game, "fullHouse", 2, 1)), "fullHouse(2, 1) must be false: a pair without three matching dice is not a full house.");
        check(((boolean) Reflect.call(game, "fullHouse", 3, 1)), "fullHouse(3, 1) must be true: this hand contains both a triple and a pair.");
        Reflect.set(game, "dice", new int[]{2, 2, 2, 3, 4});
        check(((int) Reflect.call(game, "points")) == 20, "Dice 2, 2, 2, 3, 4 should score 20. Keep an ordinary triple separate from a full house.");
        Reflect.set(game, "dice", new int[]{2, 2, 2, 3, 3});
        check(((int) Reflect.call(game, "points")) == 35, "Dice 2, 2, 2, 3, 3 should score 35. Keep points using the completed fullHouse rule.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_SonarTreasureHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P14_Task_SonarTreasureHunt");
        check(!((boolean) Reflect.call(game, "insideSea", 8, 7)), "insideSea must refuse row 8. Internal row numbers in this sea run from 0 to 7.");
        check(!((boolean) Reflect.call(game, "insideSea", 7, 8)), "insideSea must refuse column 8 even when the row is valid. Columns run from 0 to 7 too.");
        check(((boolean) Reflect.call(game, "insideSea", 0, 0)) && ((boolean) Reflect.call(game, "insideSea", 7, 7)), "insideSea must accept both corners (0, 0) and (7, 7). Include the lower and upper valid bounds.");
        check(!((boolean) Reflect.call(game, "insideSea", -1, 0)) && !((boolean) Reflect.call(game, "insideSea", 0, -1)), "insideSea must refuse a negative row or column before the sea is accessed.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_NimTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P15_Task_Nim");
        Reflect.set(p,"stones",13);
        for (int[] example:new int[][]{{0,0},{1,1},{2,2},{3,3},{7,3},{8,1},{10,2},{12,1}}) {
            check((int)Reflect.call(p,"advice",example[0])==example[1],"advice recommends the wrong move for the requested pile of "+example[0]);
            check((int)Reflect.get(p,"stones")==13,"Calling advice must leave the real pile unchanged. Calculate a recommendation without taking stones.");
        }
        Reflect.set(p,"stones",7);
        check((int)Reflect.call(p,"botMove")==3,"For a real pile of 7, botMove must still choose 3. Preserve the existing bot rule.");
        Reflect.set(p,"stones",8);
        check((int)Reflect.call(p,"botMove")==1,"For a real pile of 8, botMove must still choose 1. Preserve its fallback move.");
    }
}

@SuppressWarnings("unchecked")
class P19_Task_CooperateOrDefectTest {
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
        var g = Reflect.create("P19_Task_CooperateOrDefect");
        equal(3, ((int) Reflect.call(g, "reward", true, true)), "reward must give 3 to a player when both players cooperate.");
        equal(0, ((int) Reflect.call(g, "reward", true, false)), "reward must give 0 to the player who cooperates while the opponent defects.");
        equal(5, ((int) Reflect.call(g, "reward", false, true)), "reward must give 5 to the player who defects while the opponent cooperates.");
        equal(1, ((int) Reflect.call(g, "reward", false, false)), "reward must give 1 to a player when both players defect.");
        Reflect.call(g, "reset");
        Reflect.call(g, "play", true, false);
        equal(new int[]{0, 5}, ((int[]) Reflect.get(g, "scores")), "play must award 0 to the cooperator and 5 to the defector. Check each call to reward from that player's viewpoint.");
    }
}

@SuppressWarnings("unchecked")
class P20_Task_CrazyEightsSimpleMauMauTest {
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
        var g = Reflect.create("P20_Task_CrazyEightsSimpleMauMau");
        Reflect.set(g, "top", 0);
        Reflect.set(g, "suit", 0);
        expect(((boolean) Reflect.call(g, "legal", 20)), "legal must accept an eight even when its suit differs from the chosen suit. Eights remain wild.");
        expect(((boolean) Reflect.call(g, "legal", 13)), "legal must accept a card with the same rank as the top card, even in another suit.");
        expect(((boolean) Reflect.call(g, "legal", 5)), "legal must accept a card in the chosen suit, even with a different rank.");
        expect(!((boolean) Reflect.call(g, "legal", 15)), "legal must refuse a card that is not an eight and matches neither the rank nor the chosen suit.");
        expect(!(boolean) Reflect.call(g, "legal", -1) && !(boolean) Reflect.call(g, "legal", 52), "legal must refuse card numbers -1 and 52. Valid internal card numbers are 0 through 51.");
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
