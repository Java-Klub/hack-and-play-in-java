import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Chapter checks: java Check.java
/// One game: java Check.java P05_NumberMachine.java
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
        run("P05_NumberMachine", "05.05 NumberMachine · F04", () -> P05_NumberMachineTest.main(new String[0]));
        run("P09_ShutTheBox", "05.09 ShutTheBox · F13", () -> P09_ShutTheBoxTest.main(new String[0]));
        run("P10_CannonPuzzle", "05.10 CannonPuzzle · F04", () -> P10_CannonPuzzleTest.main(new String[0]));
        run("P11_BouncingBalls", "05.11 BouncingBalls · F04", () -> P11_BouncingBallsTest.main(new String[0]));
        run("P14_SOS", "05.14 SOS · F13", () -> P14_SOSTest.main(new String[0]));
        run("P17_ToadsAndFrogs", "05.17 ToadsAndFrogs · F13", () -> P17_ToadsAndFrogsTest.main(new String[0]));
        run("P18_MiniGolf", "05.18 MiniGolf · F04", () -> P18_MiniGolfTest.main(new String[0]));
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

class P05_NumberMachineTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P05_NumberMachine");
        for(int n=-20;n<=20;n++) {
            int expected=n*n+1;
            check((int)Reflect.call(p,"machine",n)==expected,"Keep the reference machine unchanged.");
            check((int)Reflect.call(p,"imitation",n)==expected,"Your copy disagrees for input "+n);
        }
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P09_ShutTheBoxTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("05.09 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_ShutTheBox");
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
class P10_CannonPuzzleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("05.10 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P10_CannonPuzzle");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", 100)) - 9) < 1e-8, "100 pixels of drag must give speed 9");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", -80)) + 7.2) < 1e-8, "upward drag must keep its negative fractional speed");
        Reflect.call(game, "restart");
        Reflect.call(game, "launch");
        check(((double) Reflect.get(game, "vx")) > 0 && ((double) Reflect.get(game, "vy")) < 0, "the actual cannon must launch right and upward");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_BouncingBallsTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P11_BouncingBalls");
        for (double value : new double[]{-8,-1.5,0,3.25,8}) {
            check(Math.abs((double)Reflect.call(p,"bounceSpeed",value)-Math.abs(value))<1e-8,"Elastic walls retain the full incoming magnitude: "+value);
        }
        Reflect.call(p,"restart");
        Reflect.call(p,"add",11.0,100.0,-4.0,0.0);
        Reflect.call(p,"step");
        Object left=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(left,"vx")-3.992)<1e-8,"A real left-wall bounce reverses the horizontal direction without extra loss.");
        Reflect.call(p,"restart");
        int width=(int)Reflect.get(p,"WIDTH");
        Reflect.call(p,"add",width-11.0,100.0,4.0,0.0);
        Reflect.call(p,"step");
        Object right=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(right,"vx")+3.992)<1e-8,"A real right-wall bounce keeps the opposite outgoing sign.");
        Reflect.call(p,"restart");
        Reflect.call(p,"add",100.0,11.0,0.0,-4.0);
        Reflect.call(p,"step");
        Object ceiling=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(ceiling,"vy")-3.85)<1e-8,"The ceiling shares the elastic rule; gravity is applied before collision.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P14_SOSTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("P14_SOS");
        check((int)Reflect.call(p,"gainedPoints",3,3)==0,"Existing lines are not rewarded again.");
        check((int)Reflect.call(p,"gainedPoints",2,3)==1,"A single new line keeps its reward.");
        check((int)Reflect.call(p,"gainedPoints",1,3)==4,"A double line earns the combo reward four.");
        check((int)Reflect.call(p,"gainedPoints",0,3)==6,"The combo doubles all new lines, not the old total.");
        Reflect.call(p,"reset");
        Reflect.set(p,"board","S.S...S.S".toCharArray());
        Reflect.set(p,"moves",4);
        Reflect.call(p,"command","5 O");
        check(((int[])Reflect.get(p,"scores"))[1]==4 && (int)Reflect.call(p,"triples")==2,"The actual centre move awards two new lines as four points.");
        check((int)Reflect.get(p,"player")==1,"The existing scoring bonus turn remains.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P17_ToadsAndFrogsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("05.17 F13: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P17_ToadsAndFrogs");
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

@SuppressWarnings("unchecked")
class P18_MiniGolfTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("05.18 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P18_MiniGolf");
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
