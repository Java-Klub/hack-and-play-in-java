import module java.base;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;

/// All level checks: `java Check.java`
/// One game: `java Check.java M01_CafeWallLab.java`
/// Checks call the test methods directly in this JVM.
/// Games remain compact sources; the checks below access them through reflection.
class Check {
    int failed;
    int checked;
    String selected;
    interface Test { void run() throws Exception; }

    void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        if (args.length > 1) throw new IllegalArgumentException("Use: java Check.java [game filename]");
        selected = args.length == 0 ? "" : Path.of(args[0]).getFileName().toString().replaceFirst("\\.java$", "");
        run("M01_CafeWallLab", "L01.01 CafeWallLab · F01", () -> M01_CafeWallLabTest.main(new String[0]));
        run("M02_MissingTriangle", "L01.02 MissingTriangle · F01", () -> M02_MissingTriangleTest.main(new String[0]));
        run("M03_TypeMelody", "L01.03 TypeMelody · F02", () -> M03_TypeMelodyTest.main(new String[0]));
        run("M04_BitmapBanner", "L01.04 BitmapBanner · F02", () -> M04_BitmapBannerTest.main(new String[0]));
        run("M05_ProgramMirror", "L01.05 ProgramMirror · F02", () -> M05_ProgramMirrorTest.main(new String[0]));
        run("M06_HigherOrLower", "L01.06 HigherOrLower · F03", () -> M06_HigherOrLowerTest.main(new String[0]));
        run("M07_SteppingFeet", "L01.07 SteppingFeet · F03", () -> M07_SteppingFeetTest.main(new String[0]));
        run("M08_SunflowerSpiral", "L01.08 SunflowerSpiral · F03", () -> M08_SunflowerSpiralTest.main(new String[0]));
        run("M09_PixelMonsters", "L01.09 PixelMonsters · F03", () -> M09_PixelMonstersTest.main(new String[0]));
        run("M10_ClubDoor", "L01.10 ClubDoor · F03", () -> M10_ClubDoorTest.main(new String[0]));
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
class M01_CafeWallLabTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.01 F01: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M01_CafeWallLab");
        Reflect.call(game, "restart");
        var image = new BufferedImage(640, 450, BufferedImage.TYPE_INT_RGB);
        var pen = image.createGraphics();
        Reflect.call(game, "paintWall", pen);
        pen.dispose();
        check(image.getRGB(100, 50) != new Color(145, 145, 145).getRGB(), "wall tiles must remain visible above the background");
        check(image.getRGB(5, 420) == new Color(145, 145, 145).getRGB(), "the unused border keeps its background");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M02_MissingTriangleTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.02 F01: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M02_MissingTriangle");
        Reflect.call(game, "restart");
        var image = new BufferedImage(600, 450, BufferedImage.TYPE_INT_RGB);
        var pen = image.createGraphics();
        Reflect.call(game, "paintDisc", pen, 0);
        pen.dispose();
        check(image.getRGB(300, 100) == Color.WHITE.getRGB(), "the top disc must keep its white bite");
        check(image.getRGB(300, 30) == Color.BLACK.getRGB(), "the uncut part remains black");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M03_TypeMelodyTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.03 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M03_TypeMelody");
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
class M04_BitmapBannerTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.04 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M04_BitmapBanner");
        check(((java.lang.String) Reflect.call(game, "bannerMessage")).equals("JAVA CLUB!"), "the default banner must say JAVA CLUB!");
        String pixels = ((java.lang.String) Reflect.call(game, "banner", ((java.lang.String) Reflect.call(game, "bannerMessage")), '#'));
        check(pixels.contains("#") && pixels.lines().count() == 5, "the message produces five visible pixel rows");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M05_ProgramMirrorTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.05 F02: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M05_ProgramMirror");
        check(((java.lang.String) Reflect.call(game, "templateLine")).equals("IO.println(\"HELLO\");"), "the printed statement needs literal quotation marks around HELLO");
        check(!((java.lang.String) Reflect.call(game, "templateLine")).equals("HELLO"), "the template is a printed Java statement, not its output");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M06_HigherOrLowerTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p = Reflect.create("M06_HigherOrLower");
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
class M07_SteppingFeetTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.07 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M07_SteppingFeet");
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
class M08_SunflowerSpiralTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.08 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M08_SunflowerSpiral");
        check(Math.abs(((double) Reflect.get(game, "defaultAngle")) - 137.5) < 1e-8, "the shared default angle must be 137.5 degrees");
        double[] second = ((double[]) Reflect.call(game, "position", 2, ((double) Reflect.get(game, "defaultAngle"))));
        check(Math.abs(second[1] - 250) > 1, "the default must spread seeds beyond a single horizontal spoke");
        check(((java.awt.image.BufferedImage) Reflect.call(game, "render", 42, ((double) Reflect.get(game, "defaultAngle")))).getWidth() == 500, "the actual PNG renderer accepts the repaired default");
        System.out.println("PASS " + checks + " checks");
    }
}

@SuppressWarnings("unchecked")
class M09_PixelMonstersTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError("L01.09 F03: " + reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("M09_PixelMonsters");
        var image = ((java.awt.image.BufferedImage) Reflect.call(game, "render", 42));
        check(image.getRGB(5 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "left eye must read the shared white eyeColour");
        check(image.getRGB(10 * 16 + 8, 5 * 16 + 8) == Color.WHITE.getRGB(), "right eye must read that same white eyeColour");
        check(((int) Reflect.get(game, "eyeColour")) == Color.WHITE.getRGB(), "one named value controls both eyes");
        System.out.println("PASS " + checks + " checks");
    }
}

class M10_ClubDoorTest {
    static int checks;
    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) throw new AssertionError(reason);
    }
    public static void main(String[] args) throws Exception {
        var p=Reflect.create("M10_ClubDoor");
        check((boolean)Reflect.call(p,"mayEnter",Reflect.get(p,"cardCode")),"The actual badge must open the workshop door.");
        check((boolean)Reflect.call(p,"mayEnter",new String("ORBIT")),"The original pass must remain valid.");
        check(!(boolean)Reflect.call(p,"mayEnter","VISITOR") && !(boolean)Reflect.call(p,"mayEnter","orbit"),"Other badges stay refused.");
        IO.println("PASS " + checks + " checks");
    }
}

// Prepared access to compact programs. Tests keep ordinary assertions, arrays and JDK collections.
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
