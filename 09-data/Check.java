import module java.base;

/// Chapter checks: java Check.java
/// One game: java Check.java P03_Dominosa.java
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
        run("P03_Dominosa", "09.03 Dominosa · F19", () -> P03_DominosaTest.main(new String[0]));
        run("P10_MirrorLaser", "09.10 MirrorLaser · F19", () -> P10_MirrorLaserTest.main(new String[0]));
        run("P11_InequalityLatin", "09.11 InequalityLatin · F19", () -> P11_InequalityLatinTest.main(new String[0]));
        run("P14_TrailDuel", "09.14 TrailDuel · F09", () -> P14_TrailDuelTest.main(new String[0]));
        run("P15_CarrotInABox", "09.15 CarrotInABox · F21", () -> P15_CarrotInABoxTest.main(new String[0]));
        run("P16_TargetPop", "09.16 TargetPop · F21", () -> P16_TargetPopTest.main(new String[0]));
        run("P17_MiniPacman", "09.17 MiniPacman · F21", () -> P17_MiniPacmanTest.main(new String[0]));
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
class P03_DominosaTest {
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
        var g = Reflect.create("P03_Dominosa");
        Reflect.call(g, "reset");
        equal(Reflect.call(g, "pairKey", 2, 3), Reflect.call(g, "pairKey", 3, 2), "09.03/F19: reversed endpoints describe the same domino value");
        equal(Reflect.create("P03_Dominosa$Domino", 0, 1), Reflect.call(g, "pairKey", 2, 3), "09.03/F19: record stores ordered endpoint values");
        expect(!((boolean) Reflect.call(Reflect.call(g, "pairKey", 0, 1), "equals", Reflect.call(g, "pairKey", 2, 3))), "09.03/F19: different unordered pairs stay distinct");
        Reflect.call(g, "command", "1 3 1 4");
        expect(((java.util.Set<Object>) Reflect.get(g, "used")).contains(Reflect.create("P03_Dominosa$Domino", 0, 1)), "09.03/F19: game tracks the record value");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P10_MirrorLaserTest {
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
        var g = Reflect.create("P10_MirrorLaser");
        var r = Reflect.create("P10_MirrorLaser$RayState", 1, 2, 1, 0);
        var next = Reflect.call(r, "advance");
        equal(Reflect.create("P10_MirrorLaser$RayState", 2, 2, 1, 0), next, "09.10/F19: record update follows its named direction");
        equal(1, ((int) Reflect.call(r, "x")), "09.10/F19: advancing leaves original record unchanged");
        equal(Reflect.create("P10_MirrorLaser$RayState", 1, 1, 0, -1), Reflect.call(Reflect.create("P10_MirrorLaser$RayState", 1, 2, 0, -1), "advance"), "09.10/F19: vertical update");
        Reflect.call(g, "restart");
        ((int[][]) Reflect.get(g, "mirrors"))[2][2] = 1;
        ((int[][]) Reflect.get(g, "mirrors"))[0][2] = 1;
        ((int[][]) Reflect.get(g, "mirrors"))[0][5] = 2;
        Reflect.call(g, "trace");
        equal("Target reached!", ((java.lang.String) Reflect.get(g, "result")), "09.10/F19: real beam follows the authored route");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_InequalityLatinTest {
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
        var g = Reflect.create("P11_InequalityLatin");
        var rule = Reflect.create("P11_InequalityLatin$Rule", 1, 0);
        int[][] values = {{4, 1, 3, 2}};
        expect(((boolean) Reflect.call(rule, "satisfiedBy", (Object) values)), "09.11/F19: record coordinates identify the left and right cells");
        values[0][1] = 3;
        expect(!((boolean) Reflect.call(rule, "satisfiedBy", (Object) values)), "09.11/F19: equality does not satisfy strict inequality");
        int[][] other = {{4, 1, 3, 2}, {1, 4, 2, 3}};
        expect(!((boolean) Reflect.call(Reflect.create("P11_InequalityLatin$Rule", 0, 0), "satisfiedBy", (Object) other)), "09.11/F19: changed x reads a different pair");
        expect(((boolean) Reflect.call(Reflect.create("P11_InequalityLatin$Rule", 0, 1), "satisfiedBy", (Object) other)), "09.11/F19: changed y reads a different row");
        expect(!((boolean) Reflect.call(Reflect.create("P11_InequalityLatin$Rule", 1, 1), "satisfiedBy", (Object) other)), "09.11/F19: record row and column cannot be ignored");
        expect(((boolean) Reflect.call(Reflect.create("P11_InequalityLatin$Rule", 2, 1), "satisfiedBy", (Object) other)), "09.11/F19: rightmost pair can satisfy the rule");
        Reflect.call(g, "restart");
        Reflect.set(g, "cells", new int[][]{{1, 2, 3, 4}, {2, 3, 4, 1}, {3, 4, 1, 2}, {4, 1, 2, 3}});
        expect(((boolean) Reflect.call(g, "won")), "09.11/F19: authored Latin square obeys all Rule records");
        Reflect.set(g, "rules", ((Object[]) Reflect.array("P11_InequalityLatin$Rule", Reflect.create("P11_InequalityLatin$Rule", 0, 3))));
        expect(!((boolean) Reflect.call(g, "won")), "09.11/F19: a valid Latin square can violate its actual Rule coordinates");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P14_TrailDuelTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message + " | expected=" + expected + " actual=" + actual);
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P14_TrailDuel");
        int[][] keys = {{java.awt.event.KeyEvent.VK_W, java.awt.event.KeyEvent.VK_UP}, {java.awt.event.KeyEvent.VK_D, java.awt.event.KeyEvent.VK_RIGHT}, {java.awt.event.KeyEvent.VK_S, java.awt.event.KeyEvent.VK_DOWN}, {java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.VK_LEFT}};
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        for (int i = 0; i < keys.length; i++) {
            for (int key : keys[i]) {
                Object d = Reflect.call(g, "directionFor", key);
                expect(d != null && (int) Reflect.call(d, "dx") == directions[i][0] && (int) Reflect.call(d, "dy") == directions[i][1], "09.14: both layouts turn their requested direction clockwise");
            }
        }
        expect(Reflect.call(g, "directionFor", java.awt.event.KeyEvent.VK_SPACE) == null, "09.14: unrelated keys do not steer");
        Reflect.call(g, "restart");
        Reflect.call(g, "key", java.awt.event.KeyEvent.VK_W, true);
        Reflect.call(g, "restart");
        Reflect.call(g, "key", java.awt.event.KeyEvent.VK_D, true);
        expect((int) Reflect.get(g, "anx") == 0 && (int) Reflect.get(g, "any") == 1, "09.14: real input uses the hacked direction");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P15_CarrotInABoxTest {
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
        var g = Reflect.create("P15_CarrotInABox");
        Reflect.set(g, "prizeA", true);
        equal(Reflect.get(Reflect.type("P15_CarrotInABox$Holder"), "PLAYER_TWO"), Reflect.call(g, "holderAfterSwap", true), "09.15/F21: swapping changes the named holder");
        equal(Reflect.get(Reflect.type("P15_CarrotInABox$Holder"), "PLAYER_ONE"), Reflect.call(g, "holderAfterSwap", false), "09.15/F21: keeping leaves prize with player one");
        Reflect.set(g, "prizeA", false);
        equal(Reflect.get(Reflect.type("P15_CarrotInABox$Holder"), "PLAYER_ONE"), Reflect.call(g, "holderAfterSwap", true), "09.15/F21: opposite starting box also swaps");
        Reflect.set(g, "outcome", "");
        Reflect.call(g, "choose", false);
        expect(((java.lang.String) Reflect.get(g, "outcome")).contains("Player 2"), "09.15/F21: actual reveal uses the enum result");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P16_TargetPopTest {
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
        var g = Reflect.create("P16_TargetPop");
        Reflect.call(g, "restart");
        Reflect.call(g, "tick", 999);
        equal(Reflect.get(Reflect.type("P16_TargetPop$Phase"), "READY"), Reflect.get(g, "phase"), "09.16/F21: idle game does not expire");
        Reflect.call(g, "click", ((int) Reflect.get(g, "targetX")), ((int) Reflect.get(g, "targetY")), 1000);
        equal(Reflect.get(Reflect.type("P16_TargetPop$Phase"), "RUNNING"), Reflect.get(g, "phase"), "09.16/F21: first click starts round");
        Reflect.call(g, "tick", ((long) Reflect.get(g, "deadline")));
        equal(Reflect.get(Reflect.type("P16_TargetPop$Phase"), "FINISHED"), Reflect.get(g, "phase"), "09.16/F21: exact deadline finishes instead of resetting");
        int hits = ((int) Reflect.get(g, "hits"));
        Reflect.call(g, "click", ((int) Reflect.get(g, "targetX")), ((int) Reflect.get(g, "targetY")), ((long) Reflect.get(g, "deadline")) + 1);
        equal(hits, ((int) Reflect.get(g, "hits")), "09.16/F21: finished round rejects late clicks");
        Reflect.call(g, "restart");
        equal(Reflect.get(Reflect.type("P16_TargetPop$Phase"), "READY"), Reflect.get(g, "phase"), "09.16/F21: explicit restart returns to ready");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P17_MiniPacmanTest {
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
        var g = Reflect.create("P17_MiniPacman");
        equal(Reflect.get(Reflect.type("P17_MiniPacman$RoundState"), "CAUGHT"), Reflect.call(g, "afterContact", true, 0), "09.17/F21: capture has priority over the last pellet");
        equal(Reflect.get(Reflect.type("P17_MiniPacman$RoundState"), "WON"), Reflect.call(g, "afterContact", false, 0), "09.17/F21: empty safe maze wins");
        equal(Reflect.get(Reflect.type("P17_MiniPacman$RoundState"), "PLAY"), Reflect.call(g, "afterContact", false, 1), "09.17/F21: unfinished round keeps playing");
        Reflect.call(g, "restart");
        Reflect.set(g, "playerX", 1);
        Reflect.set(g, "playerY", 1);
        Reflect.set(g, "ghostX", 2);
        Reflect.set(g, "ghostY", 1);
        Reflect.set(g, "remaining", 1);
        ((boolean[][]) Reflect.get(g, "pellets"))[1][2] = true;
        Reflect.call(g, "direction", 1, 0);
        Reflect.call(g, "step");
        equal(Reflect.get(Reflect.type("P17_MiniPacman$RoundState"), "CAUGHT"), Reflect.get(g, "state"), "09.17/F21: real step uses terminal-state priority");
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
