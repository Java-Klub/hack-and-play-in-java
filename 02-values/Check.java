import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Chapter checks: java Check.java
/// One game: java Check.java P06_HigherOrLower.java
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
        run("P06_HigherOrLower", "02.06 HigherOrLower · F03", () -> P06_HigherOrLowerTest.main(new String[0]));
        run("P07_SteppingFeet", "02.07 SteppingFeet · F03", () -> P07_SteppingFeetTest.main(new String[0]));
        run("P11_SunflowerSpiral", "02.11 SunflowerSpiral · F03", () -> P11_SunflowerSpiralTest.main(new String[0]));
        run("P12_Spirograph", "02.12 Spirograph · F04", () -> P12_SpirographTest.main(new String[0]));
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
class P06_HigherOrLowerTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p = Reflect.create("P06_HigherOrLower");
        Reflect.call(p, "reset");
        check(((int)Reflect.get(p,"target")) == 3, "The displayed and actual sprint goal must be three.");
        Reflect.set(p,"current",2);
        Reflect.set(p,"deck",new ArrayDeque<>(List.of(3,4,5,6)));
        Reflect.call(p,"command","h");
        check(((String)Reflect.get(p,"outcome")).isEmpty(),"One prediction is not yet the three-correct goal.");
        Reflect.call(p,"command","h");
        check(((String)Reflect.get(p,"outcome")).isEmpty(),"Two predictions are not yet the goal.");
        Reflect.call(p,"command","h");
        check(((int)Reflect.get(p,"correct")) == 3 && !((String)Reflect.get(p,"outcome")).isEmpty(),"The third correct prediction ends the sprint.");
        Reflect.call(p,"reset");
        check(((int)Reflect.get(p,"target"))==3 && ((int)Reflect.get(p,"correct"))==0,"Restart keeps the new goal and clears only the score.");
        IO.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P07_SteppingFeetTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("02.07 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_SteppingFeet");
        Reflect.call(game, "restart");
        check(((double) Reflect.get(game, "x")) == 80, "the later assignment must leave shared x at 80");
        check(((boolean) Reflect.get(game, "paused")), "the prepared animation still waits for Space");
        Reflect.set(game, "paused", false);
        Reflect.call(game, "step");
        check(Math.abs(((double) Reflect.get(game, "x")) - 81.6) < 1e-8, "both supplied feet continue from the repaired shared coordinate");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P11_SunflowerSpiralTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("02.11 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P11_SunflowerSpiral");
        check(Math.abs(((double) Reflect.get(game, "defaultAngle")) - 137.5) < 1e-8, "the shared default angle must be 137.5 degrees");
        double[] second = ((double[]) Reflect.call(game, "position", 2, ((double) Reflect.get(game, "defaultAngle"))));
        check(Math.abs(second[1] - 250) > 1, "the default must spread seeds beyond a single horizontal spoke");
        check(((java.awt.image.BufferedImage) Reflect.call(game, "render", 42, ((double) Reflect.get(game, "defaultAngle")))).getWidth() == 500, "the actual PNG renderer accepts the repaired default");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P12_SpirographTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("02.12 F04: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Spirograph");
        check(Math.abs(((double) Reflect.call(game, "radiusRatio", 120, 45)) - 5.0 / 3) < 1e-8, "75 divided by 45 must retain its fractional ratio");
        check(((double) Reflect.call(game, "radiusRatio", 120, 60)) == 1, "an exact ratio remains exact");
        check(((double) Reflect.call(game, "radiusRatio", 120, 80)) == 0.5, "a ratio below one must not become zero");
        Reflect.call(game, "restart");
        var p = ((java.awt.geom.Point2D.Double) Reflect.call(game, "point", 1));
        check(Math.abs(p.x - (75 * Math.cos(1) + 65 * Math.cos(5.0 / 3))) < 1e-8, "the visible curve uses the repaired ratio");
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
