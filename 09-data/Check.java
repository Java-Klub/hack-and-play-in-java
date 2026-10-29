import module java.base;

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
        run("P03_Task_Dominosa", () -> P03_Task_DominosaTest.main(new String[0]));
        run("P10_Task_MirrorLaser", () -> P10_Task_MirrorLaserTest.main(new String[0]));
        run("P11_Task_InequalityLatin", () -> P11_Task_InequalityLatinTest.main(new String[0]));
        run("P14_Task_TrailDuel", () -> P14_Task_TrailDuelTest.main(new String[0]));
        run("P15_Task_CarrotInABox", () -> P15_Task_CarrotInABoxTest.main(new String[0]));
        run("P16_Task_TargetPop", () -> P16_Task_TargetPopTest.main(new String[0]));
        run("P17_Task_MiniPacman", () -> P17_Task_MiniPacmanTest.main(new String[0]));
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
class P03_Task_DominosaTest {
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
        var g = Reflect.create("P03_Task_Dominosa");
        Reflect.call(g, "reset");
        equal(Reflect.call(g, "pairKey", 2, 3), Reflect.call(g, "pairKey", 3, 2), "pairKey must treat endpoints (2, 3) and (3, 2) as the same domino value.");
        equal(Reflect.create("P03_Task_Dominosa$Domino", 0, 1), Reflect.call(g, "pairKey", 2, 3), "For board positions 2 and 3, pairKey must return Domino(0, 1), with the smaller value first.");
        expect(!((boolean) Reflect.call(Reflect.call(g, "pairKey", 0, 1), "equals", Reflect.call(g, "pairKey", 2, 3))), "pairKey must keep two genuinely different number pairs distinct. Normalising order must not erase their values.");
        Reflect.call(g, "command", "1 3 1 4");
        expect(((java.util.Set<Object>) Reflect.get(g, "used")).contains(Reflect.create("P03_Task_Dominosa$Domino", 0, 1)), "After pairing cells 1 3 and 1 4, used must contain the normalised Domino(0, 1) value.");
    }
}

@SuppressWarnings("unchecked")
class P10_Task_MirrorLaserTest {
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
        var g = Reflect.create("P10_Task_MirrorLaser");
        var r = Reflect.create("P10_Task_MirrorLaser$RayState", 1, 2, 1, 0);
        var next = Reflect.call(r, "advance");
        equal(Reflect.create("P10_Task_MirrorLaser$RayState", 2, 2, 1, 0), next, "RayState.advance must move (1, 2) to (2, 2) when the direction is (1, 0), preserving that direction.");
        equal(1, ((int) Reflect.call(r, "x")), "Calling advance must leave the original ray at x = 1. Return a new state instead of changing the old one.");
        equal(Reflect.create("P10_Task_MirrorLaser$RayState", 1, 1, 0, -1), Reflect.call(Reflect.create("P10_Task_MirrorLaser$RayState", 1, 2, 0, -1), "advance"), "RayState.advance must move (1, 2) to (1, 1) when the direction is (0, -1).");
        Reflect.call(g, "restart");
        ((int[][]) Reflect.get(g, "mirrors"))[2][2] = 1;
        ((int[][]) Reflect.get(g, "mirrors"))[0][2] = 1;
        ((int[][]) Reflect.get(g, "mirrors"))[0][5] = 2;
        Reflect.call(g, "trace");
        equal("Target reached!", ((java.lang.String) Reflect.get(g, "result")), "The real trace must reach the target through the three supplied mirrors. Check that advance follows each ray's own direction.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_InequalityLatinTest {
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
        var g = Reflect.create("P11_Task_InequalityLatin");
        var rule = Reflect.create("P11_Task_InequalityLatin$Rule", 1, 0);
        int[][] values = {{4, 1, 3, 2}};
        expect(((boolean) Reflect.call(rule, "satisfiedBy", (Object) values)), "Rule(1, 0).satisfiedBy must compare 1 with 3 in row [4, 1, 3, 2] and accept the strict less-than relation.");
        values[0][1] = 3;
        expect(!((boolean) Reflect.call(rule, "satisfiedBy", (Object) values)), "Equal neighbouring values 3 and 3 must not satisfy the less-than rule.");
        int[][] other = {{4, 1, 3, 2}, {1, 4, 2, 3}};
        expect(!((boolean) Reflect.call(Reflect.create("P11_Task_InequalityLatin$Rule", 0, 0), "satisfiedBy", (Object) other)), "Rule(0, 0) must reject 4 < 1. Use the rule's actual x coordinate when choosing the pair.");
        expect(((boolean) Reflect.call(Reflect.create("P11_Task_InequalityLatin$Rule", 0, 1), "satisfiedBy", (Object) other)), "Rule(0, 1) must accept 1 < 4 in the second row. Use its y coordinate too.");
        expect(!((boolean) Reflect.call(Reflect.create("P11_Task_InequalityLatin$Rule", 1, 1), "satisfiedBy", (Object) other)), "Rule(1, 1) must reject 4 < 2 rather than reuse a pair from another row or column.");
        expect(((boolean) Reflect.call(Reflect.create("P11_Task_InequalityLatin$Rule", 2, 1), "satisfiedBy", (Object) other)), "Rule(2, 1) must accept the rightmost pair 2 < 3 in the second row.");
        Reflect.call(g, "restart");
        Reflect.set(g, "cells", new int[][]{{1, 2, 3, 4}, {2, 3, 4, 1}, {3, 4, 1, 2}, {4, 1, 2, 3}});
        expect(((boolean) Reflect.call(g, "won")), "The supplied Latin square satisfies every original inequality and must satisfy won.");
        Reflect.set(g, "rules", ((Object[]) Reflect.array("P11_Task_InequalityLatin$Rule", Reflect.create("P11_Task_InequalityLatin$Rule", 0, 3))));
        expect(!((boolean) Reflect.call(g, "won")), "A Latin square alone is not enough: won must reject the added rule requiring 4 < 1 in the last row.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_TrailDuelTest {
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
        var g = Reflect.create("P14_Task_TrailDuel");
        int[][] keys = {{java.awt.event.KeyEvent.VK_W, java.awt.event.KeyEvent.VK_UP}, {java.awt.event.KeyEvent.VK_D, java.awt.event.KeyEvent.VK_RIGHT}, {java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.VK_DOWN}, {java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.VK_LEFT}};
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int i = 0; i < keys.length; i++) {
            for (int key : keys[i]) {
                Object d = Reflect.call(g, "directionFor", key);
                expect(d != null && (int) Reflect.call(d, "dx") == directions[i][0] && (int) Reflect.call(d, "dy") == directions[i][1], "directionFor must rotate both WASD and arrow-key requests clockwise: up→right, right→down, down→left, left→up.");
            }
        }
        expect(Reflect.call(g, "directionFor", java.awt.event.KeyEvent.VK_SPACE) == null, "directionFor must return null for Space; an unrelated key must not steer either rider.");
        Reflect.call(g, "restart");
        Reflect.call(g, "key", java.awt.event.KeyEvent.VK_W, true);
        Reflect.call(g, "restart");
        Reflect.call(g, "key", java.awt.event.KeyEvent.VK_D, true);
        expect((int) Reflect.get(g, "anx") == 0 && (int) Reflect.get(g, "any") == 1, "After restart, pressing D must request a downward turn in the actual game. Check that key uses the changed direction mapping.");
    }
}

@SuppressWarnings("unchecked")
class P15_Task_CarrotInABoxTest {
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
        var g = Reflect.create("P15_Task_CarrotInABox");
        Reflect.set(g, "prizeA", true);
        equal(Reflect.get(Reflect.type("P15_Task_CarrotInABox$Holder"), "PLAYER_TWO"), Reflect.call(g, "holderAfterSwap", true), "When player 1 starts with the carrot, holderAfterSwap(true) must give it to player 2.");
        equal(Reflect.get(Reflect.type("P15_Task_CarrotInABox$Holder"), "PLAYER_ONE"), Reflect.call(g, "holderAfterSwap", false), "When player 1 starts with the carrot, holderAfterSwap(false) must keep it with player 1.");
        Reflect.set(g, "prizeA", false);
        equal(Reflect.get(Reflect.type("P15_Task_CarrotInABox$Holder"), "PLAYER_ONE"), Reflect.call(g, "holderAfterSwap", true), "When the carrot starts in the other box, swapping must give it to player 1.");
        Reflect.set(g, "outcome", "");
        Reflect.call(g, "choose", false);
        expect(((java.lang.String) Reflect.get(g, "outcome")).contains("Player 2"), "Keeping the boxes when the carrot starts with player 2 must announce Player 2 as the winner.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_TargetPopTest {
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
        var g = Reflect.create("P16_Task_TargetPop");
        Reflect.call(g, "restart");
        Reflect.call(g, "tick", 999);
        equal(Reflect.get(Reflect.type("P16_Task_TargetPop$Phase"), "READY"), Reflect.get(g, "phase"), "A tick before the first click must leave the game READY; an unstarted round cannot expire.");
        Reflect.call(g, "click", ((int) Reflect.get(g, "targetX")), ((int) Reflect.get(g, "targetY")), 1000);
        equal(Reflect.get(Reflect.type("P16_Task_TargetPop$Phase"), "RUNNING"), Reflect.get(g, "phase"), "Clicking the initial target must start the round and set RUNNING.");
        Reflect.call(g, "tick", ((long) Reflect.get(g, "deadline")));
        equal(Reflect.get(Reflect.type("P16_Task_TargetPop$Phase"), "FINISHED"), Reflect.get(g, "phase"), "At the exact deadline, afterTime must set FINISHED rather than READY, preserving the completed round.");
        int hits = ((int) Reflect.get(g, "hits"));
        Reflect.call(g, "click", ((int) Reflect.get(g, "targetX")), ((int) Reflect.get(g, "targetY")), ((long) Reflect.get(g, "deadline")) + 1);
        equal(hits, ((int) Reflect.get(g, "hits")), "A click after the deadline must not increase the finished round's hit count.");
        Reflect.call(g, "restart");
        equal(Reflect.get(Reflect.type("P16_Task_TargetPop$Phase"), "READY"), Reflect.get(g, "phase"), "An intentional restart must return the game to READY for a new round.");
    }
}

@SuppressWarnings("unchecked")
class P17_Task_MiniPacmanTest {
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
        var g = Reflect.create("P17_Task_MiniPacman");
        equal(Reflect.get(Reflect.type("P17_Task_MiniPacman$RoundState"), "CAUGHT"), Reflect.call(g, "afterContact", true, 0), "afterContact(true, 0) must return CAUGHT: meeting the ghost takes priority over collecting the last dot.");
        equal(Reflect.get(Reflect.type("P17_Task_MiniPacman$RoundState"), "WON"), Reflect.call(g, "afterContact", false, 0), "afterContact(false, 0) must return WON because every dot is collected and the player is safe.");
        equal(Reflect.get(Reflect.type("P17_Task_MiniPacman$RoundState"), "PLAY"), Reflect.call(g, "afterContact", false, 1), "afterContact(false, 1) must return PLAY because one dot remains and there was no capture.");
        Reflect.call(g, "restart");
        Reflect.set(g, "playerX", 1);
        Reflect.set(g, "playerY", 1);
        Reflect.set(g, "ghostX", 2);
        Reflect.set(g, "ghostY", 1);
        Reflect.set(g, "remaining", 1);
        ((boolean[][]) Reflect.get(g, "pellets"))[1][2] = true;
        Reflect.call(g, "direction", 1, 0);
        Reflect.call(g, "step");
        equal(Reflect.get(Reflect.type("P17_Task_MiniPacman$RoundState"), "CAUGHT"), Reflect.get(g, "state"), "The real step that collects the last dot and meets the ghost must end in CAUGHT, not a win or continued play.");
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
