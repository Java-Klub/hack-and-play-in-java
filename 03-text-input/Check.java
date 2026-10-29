import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Chapter checks: java Check.java
/// One game: java Check.java P07_TypeMelody.java
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
        run("P07_TypeMelody", "03.07 TypeMelody · F02", () -> P07_TypeMelodyTest.main(new String[0]));
        run("P08_BitmapBanner", "03.08 BitmapBanner · F02", () -> P08_BitmapBannerTest.main(new String[0]));
        run("P09_ProgramMirror", "03.09 ProgramMirror · F02", () -> P09_ProgramMirrorTest.main(new String[0]));
        run("P10_MadLibsStoryMachine", "03.10 MadLibsStoryMachine · F05", () -> P10_MadLibsStoryMachineTest.main(new String[0]));
        run("P11_AnagramRounds", "03.11 AnagramRounds · F05", () -> P11_AnagramRoundsTest.main(new String[0]));
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
class P07_TypeMelodyTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("03.07 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_TypeMelody");
        check(((java.lang.String) Reflect.call(game, "defaultMessage")).equals("JAVA"), "the opening melody must use the literal JAVA");
        byte[] audio = ((byte[]) Reflect.call(game, "synthesize", ((java.lang.String) Reflect.call(game, "defaultMessage")), 42));
        boolean audible = false;
        for (byte sample : audio) {
            audible |= sample != 0;
        }
        check(audible, "letters generate actual non-silent audio samples");
        check(audio.length == 4 * 2205 * 2, "four characters retain four time slots");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P08_BitmapBannerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("03.08 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_BitmapBanner");
        check(((java.lang.String) Reflect.call(game, "bannerMessage")).equals("JAVA CLUB!"), "the default banner must say JAVA CLUB!");
        String pixels = ((java.lang.String) Reflect.call(game, "banner", ((java.lang.String) Reflect.call(game, "bannerMessage")), '#'));
        check(pixels.contains("#") && pixels.lines().count() == 5, "the message produces five visible pixel rows");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P09_ProgramMirrorTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("03.09 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_ProgramMirror");
        check(((java.lang.String) Reflect.call(game, "templateLine")).equals("IO.println(\"HELLO\");"), "the printed statement needs literal quotation marks around HELLO");
        check(!((java.lang.String) Reflect.call(game, "templateLine")).equals("HELLO"), "the template is a printed Java statement, not its output");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class P10_MadLibsStoryMachineTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("03.10 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P10_MadLibsStoryMachine");
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
class P11_AnagramRoundsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("03.11 F05: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P11_AnagramRounds");
        check(((java.lang.String) Reflect.call(game, "normalize", "  silent  ")).equals("SILENT"), "a whole lower-case answer must become the accepted upper-case word");
        check(((java.lang.String) Reflect.call(game, "normalize", "LiStEn")).equals("LISTEN"), "mixed case is normalized");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", " silent ");
        check(((int) Reflect.get(game, "score")) == 1 && ((int) Reflect.get(game, "round")) == 1, "the real anagram round accepts the normalized answer");
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
