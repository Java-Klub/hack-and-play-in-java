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
        run("P05_Task_NumberMachine", () -> P05_Task_NumberMachineTest.main(new String[0]));
        run("P09_Task_ShutTheBox", () -> P09_Task_ShutTheBoxTest.main(new String[0]));
        run("P10_Task_CannonPuzzle", () -> P10_Task_CannonPuzzleTest.main(new String[0]));
        run("P11_Task_BouncingBalls", () -> P11_Task_BouncingBallsTest.main(new String[0]));
        run("P14_Task_SOS", () -> P14_Task_SOSTest.main(new String[0]));
        run("P17_Task_ToadsAndFrogs", () -> P17_Task_ToadsAndFrogsTest.main(new String[0]));
        run("P18_Task_MiniGolf", () -> P18_Task_MiniGolfTest.main(new String[0]));
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

class P05_Task_NumberMachineTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P05_Task_NumberMachine");
        for(int n=-20;n<=20;n++) {
            int expected=n*n+1;
            check((int)Reflect.call(p,"machine",n)==expected,"The reference machine no longer follows its supplied rule. Restore machine and work in imitation.");
            check((int)Reflect.call(p,"imitation",n)==expected,"imitation returns a different result from the reference machine for input "+n);
        }
    }
}

@SuppressWarnings("unchecked")
class P09_Task_ShutTheBoxTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Task_ShutTheBox");
        check(((int) Reflect.call(game, "diceTotal", 2, 5)) == 7, "diceTotal(2, 5) must return 7. Use both supplied dice values.");
        check(((int) Reflect.call(game, "diceTotal", 6, 6)) == 12 && ((int) Reflect.call(game, "diceTotal", 1, 1)) == 2, "diceTotal must return 2 for (1, 1) and 12 for (6, 6). Check both ends of the dice range.");
        Reflect.set(game, "random", new Random(42));
        Reflect.call(game, "reset");
        var random = new Random(42);
        int expected = random.nextInt(1,7) + random.nextInt(1,7);
        check(((int) Reflect.get(game, "target")) == expected, "The rolled target does not match the sum of the two dice. In roll, use the result of diceTotal.");
    }
}

@SuppressWarnings("unchecked")
class P10_Task_CannonPuzzleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P10_Task_CannonPuzzle");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", 100)) - 9) < 1e-8, "launchSpeed(100) must return 9. Check whether the scale keeps its fractional part during calculation.");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", -80)) + 7.2) < 1e-8, "launchSpeed(-80) must return -7.2. Keep the negative sign and fractional part for an upward drag.");
        Reflect.call(game, "restart");
        Reflect.call(game, "launch");
        check(((double) Reflect.get(game, "vx")) > 0 && ((double) Reflect.get(game, "vy")) < 0, "The starting shot must travel right and upward. Keep launch using the calculated speeds on both axes.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_BouncingBallsTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P11_Task_BouncingBalls");
        for (double value : new double[]{-8,-1.5,0,3.25,8}) {
            check(Math.abs((double)Reflect.call(p,"bounceSpeed",value)-Math.abs(value))<1e-8,"bounceSpeed must preserve the full speed magnitude for input "+value);
        }
        Reflect.call(p,"restart");
        Reflect.call(p,"add",11.0,100.0,-4.0,0.0);
        Reflect.call(p,"step");
        Object left=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(left,"vx")-3.992)<1e-8,"After the test ball hits the left wall, its horizontal speed should be +3.992. Keep the direction reversal without extra wall loss.");
        Reflect.call(p,"restart");
        int width=(int)Reflect.get(p,"WIDTH");
        Reflect.call(p,"add",width-11.0,100.0,4.0,0.0);
        Reflect.call(p,"step");
        Object right=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(right,"vx")+3.992)<1e-8,"After the test ball hits the right wall, its horizontal speed should be -3.992. Check the outgoing direction.");
        Reflect.call(p,"restart");
        Reflect.call(p,"add",100.0,11.0,0.0,-4.0);
        Reflect.call(p,"step");
        Object ceiling=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(ceiling,"vy")-3.85)<1e-8,"The ceiling bounce should give vertical speed +3.85 in this test. Keep the shared bounce rule and the supplied gravity.");
    }
}

@SuppressWarnings("unchecked")
class P14_Task_SOSTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P14_Task_SOS");
        check((int)Reflect.call(p,"gainedPoints",3,3)==0,"gainedPoints(3, 3) must return 0. No new SOS line was created.");
        check((int)Reflect.call(p,"gainedPoints",2,3)==1,"gainedPoints(2, 3) must return 1. One new SOS line earns one point.");
        check((int)Reflect.call(p,"gainedPoints",1,3)==4,"gainedPoints(1, 3) must return 4. Two new lines earn the combo reward.");
        check((int)Reflect.call(p,"gainedPoints",0,3)==6,"gainedPoints(0, 3) must return 6. Apply the combo to the number of new lines.");
        Reflect.call(p,"reset");
        Reflect.set(p,"board","S.S...S.S".toCharArray());
        Reflect.set(p,"moves",4);
        Reflect.call(p,"command","5 O");
        check(((int[])Reflect.get(p,"scores"))[1]==4 && (int)Reflect.call(p,"triples")==2,"The prepared centre move 5 O makes two SOS lines and should award 4 points. Keep scoring connected to gainedPoints.");
        check((int)Reflect.get(p,"player")==1,"After a scoring move, the same player must keep the next turn. Preserve the supplied bonus-turn rule.");
    }
}

@SuppressWarnings("unchecked")
class P17_Task_ToadsAndFrogsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P17_Task_ToadsAndFrogs");
        check(((int) Reflect.call(game, "direction", 2)) == -1, "direction(2) must return -1: frogs move left.");
        check(((int) Reflect.call(game, "direction", 1)) == 1, "direction(1) must return 1: toads move right.");
        Reflect.call(game, "restart");
        check(((boolean) Reflect.call(game, "move", 4)) && ((int[]) Reflect.get(game, "cells"))[3] == 2 && ((int[]) Reflect.get(game, "cells"))[4] == 0, "At the start, the frog beside the gap must be able to step left into it. Check the result passed from destination to move.");
        Reflect.call(game, "restart");
        check(((boolean) Reflect.call(game, "move", 2)) && ((int[]) Reflect.get(game, "cells"))[3] == 1, "At the start, the toad beside the gap must be able to step right into it. Preserve the supplied move rules.");
        check(((int) Reflect.call(game, "destination", 5, 2, 2)) == 3, "destination(5, 2, 2) must return 3: the frog jumps two places left from internal position 5.");
        check(((int) Reflect.call(game, "destination", 1, 2, 1)) == 3, "destination(1, 2, 1) must return 3: the toad jumps two places right from internal position 1.");
        check(((int) Reflect.call(game, "destination", 4, 0, 2)) == 4, "destination(4, 0, 2) must return 4. A zero-length movement keeps the starting position.");
    }
}

@SuppressWarnings("unchecked")
class P18_Task_MiniGolfTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P18_Task_MiniGolf");
        check(Math.abs(((double) Reflect.call(game, "dragVelocity", 100)) - 8) < 1e-8, "dragVelocity(100) must return 8 before the supplied speed limit is applied.");
        check(Math.abs(((double) Reflect.call(game, "dragVelocity", -25)) + 2) < 1e-8, "dragVelocity(-25) must return -2. Preserve the pull direction as well as its strength.");
        check(((double) Reflect.call(game, "dragVelocity", 0)) == 0, "dragVelocity(0) must return 0. A pull of zero must not move the ball.");
        Reflect.call(game, "restart");
        Reflect.call(game, "begin", 90, 110);
        Reflect.call(game, "shoot", -10, 110);
        check(Math.abs(((double) Reflect.get(game, "vx")) - 8) < 1e-8 && ((int) Reflect.get(game, "strokes")) == 1, "The test putt should give horizontal speed 8 and count one stroke. Keep shoot connected to the calculated pull.");
        Reflect.call(game, "applyPull", 25.0, -50.0);
        check(Math.abs(((double) Reflect.get(game, "vx")) - 2) < 1e-8 && Math.abs(((double) Reflect.get(game, "vy")) + 4) < 1e-8, "applyPull(25, -50) must set horizontal speed 2 and vertical speed -4. Use each input on its matching axis.");
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
