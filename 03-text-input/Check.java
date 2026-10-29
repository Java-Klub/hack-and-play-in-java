import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// Kontrola herních úloh této kapitoly. Krátké učebnicové úkoly spouštěj samostatně.
class Check {
    int failed;
    int checked;
    String selected;
    final ArrayList<String> available = new ArrayList<>();
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) {
            IO.println("Zadej nejvýše jeden název herního souboru. Bez argumentu se zkontrolují všechny herní úlohy kapitoly.");
            System.exit(2);
        }
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("P07_Task_TypeMelody", () -> P07_Task_TypeMelodyTest.main(new String[0]));
        run("P08_Task_BitmapBanner", () -> P08_Task_BitmapBannerTest.main(new String[0]));
        run("P09_Task_ProgramMirror", () -> P09_Task_ProgramMirrorTest.main(new String[0]));
        run("P10_Task_MadLibsStoryMachine", () -> P10_Task_MadLibsStoryMachineTest.main(new String[0]));
        run("P11_Task_AnagramRounds", () -> P11_Task_AnagramRoundsTest.main(new String[0]));
        if (checked == 0) {
            IO.println("Pro tento soubor zde není připravená herní kontrola: " + selected + ".java");
            IO.println("Vyber jeden z těchto souborů: " + String.join(", ", available));
            System.exit(2);
        }
        IO.println("Kontroly: " + checked + "; hotovo: " + (checked - failed) + "; zbývá: " + failed);
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
            if (reason == null || reason.isBlank()) reason = "Kontrola nedostala očekávaný výsledek.";
            if (!(failure instanceof AssertionError)) reason = "Program se při kontrole zastavil: " + failure.getClass().getSimpleName() + ": " + reason;
            IO.println("  " + reason.replace("\n", "\n  "));
            Throwable cause = failure;
            while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
            if (cause != failure) IO.println("  " + cause.getClass().getSimpleName() + ": " + show(cause.getMessage()));
            for (var frame : cause.getStackTrace()) {
                if (frame.getClassName().equals(game) || frame.getClassName().startsWith(game + "$")) {
                    IO.println("  " + "Místo v programu: " + frame.getFileName() + ":" + frame.getLineNumber());
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
            return parts + (length > 8 ? " … (" + length + " položek" + ")" : "");
        }
        String text = String.valueOf(value).replace("\n", "\\n").replace("\r", "\\r");
        return text.length() <= 180 ? text : text.substring(0, 177) + "…";
    }
}

@SuppressWarnings("unchecked")
class P07_Task_TypeMelodyTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_TypeMelody");
        check(((java.lang.String) Reflect.call(game, "defaultMessage")).equals("JAVA"), "defaultMessage má vracet přesně JAVA. Zkontroluj znaky mezi uvozovkami.");
        byte[] audio = ((byte[]) Reflect.call(game, "synthesize", ((java.lang.String) Reflect.call(game, "defaultMessage")), 42));
        boolean audible = false;
        for (byte sample : audio) {
            audible |= sample != 0;
        }
        check(audible, "synthesize pro JAVA vytvořilo jen ticho. Zachovej připravený převod písmen na tóny.");
        check(audio.length == 4 * 2205 * 2, "JAVA má vytvořit čtyři stejně dlouhé úseky tónů. Zachovej připravené časování všech čtyř znaků.");
    }
}

@SuppressWarnings("unchecked")
class P08_Task_BitmapBannerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Task_BitmapBanner");
        check(((java.lang.String) Reflect.call(game, "bannerMessage")).equals("JAVA CLUB!"), "bannerMessage má vracet přesně JAVA CLUB!, včetně mezery a vykřičníku.");
        String pixels = ((java.lang.String) Reflect.call(game, "banner", ((java.lang.String) Reflect.call(game, "bannerMessage")), '#'));
        check(pixels.contains("#") && pixels.lines().count() == 5, "banner má vykreslit zprávu do pěti řádků se znaky #. Zachovej připravené kreslení písmen.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_ProgramMirrorTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Task_ProgramMirror");
        check(((java.lang.String) Reflect.call(game, "templateLine")).equals("IO.println(\"HELLO\");"), "V řádku z templateLine chybějí uvozovky kolem HELLO. Zkontroluj text, který program vypisuje.");
        check(!((java.lang.String) Reflect.call(game, "templateLine")).equals("HELLO"), "templateLine má vrátit text příkazu Javy k vypsání, ne jen pozdrav HELLO.");
    }
}

@SuppressWarnings("unchecked")
class P10_Task_MadLibsStoryMachineTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P10_Task_MadLibsStoryMachine");
        check(((java.lang.String) Reflect.call(game, "cleanWord", "  Red Fox  ")).equals("Red Fox"), "cleanWord má ze vstupu s mezerami kolem Red Fox vrátit Red Fox a zachovat obě slova.");
        check(((java.lang.String) Reflect.call(game, "cleanWord", "New   Moon")).equals("New   Moon"), "cleanWord změnilo mezery uvnitř New   Moon. Odstraňuj jen mezery na okrajích.");
        String story = ((java.lang.String) Reflect.call(game, "story", "  Red Fox  ", " robot ", " Moon Base ", " spoon "));
        check(story.startsWith("Red Fox hired a robot to guard Moon Base."), "Příběh má začít Red Fox hired a robot to guard Moon Base. Zachovej celé vstupní fráze a očisti jejich okraje.");
        check(((String) Reflect.call(game, "headline", "  Red Fox  ")).equals("Report from Red Fox:"), "headline pro jméno s mezerami kolem Red Fox má vrátit \"Report from Red Fox:\". Použij v titulku očištěné jméno.");
        check(((String) Reflect.call(game, "headline", "Ada")).equals("Report from Ada:"), "headline(\"Ada\") má vrátit \"Report from Ada:\". Sestav titulek z předaného argumentu.");
    }
}

@SuppressWarnings("unchecked")
class P11_Task_AnagramRoundsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P11_Task_AnagramRounds");
        check(((java.lang.String) Reflect.call(game, "normalize", "  silent  ")).equals("SILENT"), "normalize má ze silent s okrajovými mezerami vytvořit SILENT. Zachovej očištění okrajů a sjednoť velikost písmen.");
        check(((java.lang.String) Reflect.call(game, "normalize", "LiStEn")).equals("LISTEN"), "normalize(\"LiStEn\") má vrátit LISTEN. Stejně zpracuj i střídání malých a velkých písmen.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", " silent ");
        check(((int) Reflect.get(game, "score")) == 1 && ((int) Reflect.get(game, "round")) == 1, "Úvodní kolo má přijmout silent s okrajovými mezerami, přidat bod a přejít o jedno kolo dál. Zachovej, aby command používalo normalize.");
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
            throw new IllegalStateException("Nelze načíst program. Ověř jeho soubor vedle Check.java: " + name, failure);
        }
    }

    static Object create(String name, Object... args) {
        var matches = new ArrayList<Constructor<?>>();
        for (var constructor : type(name).getDeclaredConstructors()) {
            if (accepts(constructor.getParameterTypes(), args)) matches.add(constructor);
        }
        var constructor = one(matches, "vytvoření objektu " + name);
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze vytvořit objekt programu: " + name, failure);
        }
    }

    static Object get(Object target, String name) {
        try {
            return field(target, name).get(target instanceof Class<?> ? null : target);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze přečíst proměnnou: " + name, failure);
        }
    }

    static Object set(Object target, String name, Object value) {
        try {
            var field = field(target, name);
            Object receiver = target instanceof Class<?> ? null : target;
            field.set(receiver, value);
            return field.get(receiver);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze změnit proměnnou: " + name, failure);
        }
    }

    private static Field field(Object target, String name) throws NoSuchFieldException {
        Class<?> owner = target instanceof Class<?> type ? type : target.getClass();
        for (var current = owner; current != null; current = current.getSuperclass()) {
            try {
                var field = current.getDeclaredField(name);
                if (target instanceof Class<?> && !Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("Pro tuto proměnnou je potřeba objekt: " + name);
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
        var method = one(matches, "metodu " + owner.getName() + "." + name);
        try {
            method.setAccessible(true);
            return method.invoke(target instanceof Class<?> ? null : target, args);
        } catch (InvocationTargetException failure) {
            throw Reflect.<RuntimeException>raise(failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Nelze zavolat metodu: " + owner.getName() + "." + name, failure);
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
            throw new IllegalStateException((matches.isEmpty() ? "Nenalezeno odpovídající " : "Nalezeno více možností pro ") + description);
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
