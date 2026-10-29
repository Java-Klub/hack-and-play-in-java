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
        run("P05_Task_NumberMachine", () -> P05_Task_NumberMachineTest.main(new String[0]));
        run("P09_Task_ShutTheBox", () -> P09_Task_ShutTheBoxTest.main(new String[0]));
        run("P10_Task_CannonPuzzle", () -> P10_Task_CannonPuzzleTest.main(new String[0]));
        run("P11_Task_BouncingBalls", () -> P11_Task_BouncingBallsTest.main(new String[0]));
        run("P14_Task_SOS", () -> P14_Task_SOSTest.main(new String[0]));
        run("P17_Task_ToadsAndFrogs", () -> P17_Task_ToadsAndFrogsTest.main(new String[0]));
        run("P18_Task_MiniGolf", () -> P18_Task_MiniGolfTest.main(new String[0]));
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
            check((int)Reflect.call(p,"machine",n)==expected,"Referenční stroj už nedodržuje připravené pravidlo. Vrať machine a pracuj v imitation.");
            check((int)Reflect.call(p,"imitation",n)==expected,"imitation vrací jiný výsledek než referenční stroj pro vstup "+n);
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
        check(((int) Reflect.call(game, "diceTotal", 2, 5)) == 7, "diceTotal(2, 5) má vrátit 7. Použij obě předané hodnoty kostek.");
        check(((int) Reflect.call(game, "diceTotal", 6, 6)) == 12 && ((int) Reflect.call(game, "diceTotal", 1, 1)) == 2, "diceTotal má vrátit 2 pro (1, 1) a 12 pro (6, 6). Ověř oba konce rozsahu kostek.");
        Reflect.set(game, "random", new Random(42));
        Reflect.call(game, "reset");
        var random = new Random(42);
        int expected = random.nextInt(1,7) + random.nextInt(1,7);
        check(((int) Reflect.get(game, "target")) == expected, "Cíl hodu neodpovídá součtu obou kostek. V roll použij výsledek diceTotal.");
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
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", 100)) - 9) < 1e-8, "launchSpeed(100) má vrátit 9. Ověř, zda se při výpočtu neztrácí desetinná část měřítka.");
        check(Math.abs(((double) Reflect.call(game, "launchSpeed", -80)) + 7.2) < 1e-8, "launchSpeed(-80) má vrátit -7.2. Při tažení vzhůru zachovej záporné znaménko i desetinnou část.");
        Reflect.call(game, "restart");
        Reflect.call(game, "launch");
        check(((double) Reflect.get(game, "vx")) > 0 && ((double) Reflect.get(game, "vy")) < 0, "Úvodní výstřel má letět doprava a vzhůru. Zachovej, aby launch používalo vypočtené rychlosti v obou osách.");
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
            check(Math.abs((double)Reflect.call(p,"bounceSpeed",value)-Math.abs(value))<1e-8,"bounceSpeed má zachovat celou velikost rychlosti pro vstup "+value);
        }
        Reflect.call(p,"restart");
        Reflect.call(p,"add",11.0,100.0,-4.0,0.0);
        Reflect.call(p,"step");
        Object left=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(left,"vx")-3.992)<1e-8,"Po nárazu zkušebního míčku do levé stěny má být vodorovná rychlost +3.992. Zachovej obrácení směru bez dodatečné ztráty na stěně.");
        Reflect.call(p,"restart");
        int width=(int)Reflect.get(p,"WIDTH");
        Reflect.call(p,"add",width-11.0,100.0,4.0,0.0);
        Reflect.call(p,"step");
        Object right=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(right,"vx")+3.992)<1e-8,"Po nárazu zkušebního míčku do pravé stěny má být vodorovná rychlost -3.992. Zkontroluj směr po odrazu.");
        Reflect.call(p,"restart");
        Reflect.call(p,"add",100.0,11.0,0.0,-4.0);
        Reflect.call(p,"step");
        Object ceiling=((List<?>)Reflect.get(p,"balls")).getFirst();
        check(Math.abs((double)Reflect.call(ceiling,"vy")-3.85)<1e-8,"Odraz od stropu má v tomto pokusu dát svislou rychlost +3.85. Zachovej společné pravidlo odrazu i připravenou gravitaci.");
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
        check((int)Reflect.call(p,"gainedPoints",3,3)==0,"gainedPoints(3, 3) má vrátit 0. Nevznikla žádná nová řada SOS.");
        check((int)Reflect.call(p,"gainedPoints",2,3)==1,"gainedPoints(2, 3) má vrátit 1. Jedna nová řada SOS dává jeden bod.");
        check((int)Reflect.call(p,"gainedPoints",1,3)==4,"gainedPoints(1, 3) má vrátit 4. Dvě nové řady dávají odměnu za kombinaci.");
        check((int)Reflect.call(p,"gainedPoints",0,3)==6,"gainedPoints(0, 3) má vrátit 6. Odměnu za kombinaci počítej z počtu nových řad.");
        Reflect.call(p,"reset");
        Reflect.set(p,"board","S.S...S.S".toCharArray());
        Reflect.set(p,"moves",4);
        Reflect.call(p,"command","5 O");
        check(((int[])Reflect.get(p,"scores"))[1]==4 && (int)Reflect.call(p,"triples")==2,"Připravený tah 5 O do středu vytvoří dvě řady SOS a má přidělit 4 body. Zachovej propojení skórování s gainedPoints.");
        check((int)Reflect.get(p,"player")==1,"Po bodujícím tahu má další tah zůstat stejnému hráči. Zachovej připravené pravidlo tahu navíc.");
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
        check(((int) Reflect.call(game, "direction", 2)) == -1, "direction(2) má vrátit -1: žáby se pohybují doleva.");
        check(((int) Reflect.call(game, "direction", 1)) == 1, "direction(1) má vrátit 1: ropuchy se pohybují doprava.");
        Reflect.call(game, "restart");
        check(((boolean) Reflect.call(game, "move", 4)) && ((int[]) Reflect.get(game, "cells"))[3] == 2 && ((int[]) Reflect.get(game, "cells"))[4] == 0, "Na začátku má žába vedle mezery umět vstoupit do ní doleva. Zkontroluj výsledek předaný z destination do move.");
        Reflect.call(game, "restart");
        check(((boolean) Reflect.call(game, "move", 2)) && ((int[]) Reflect.get(game, "cells"))[3] == 1, "Na začátku má ropucha vedle mezery umět vstoupit do ní doprava. Zachovej připravená pravidla move.");
        check(((int) Reflect.call(game, "destination", 5, 2, 2)) == 3, "destination(5, 2, 2) má vrátit 3: žába skáče o dvě místa doleva z vnitřní pozice 5.");
        check(((int) Reflect.call(game, "destination", 1, 2, 1)) == 3, "destination(1, 2, 1) má vrátit 3: ropucha skáče o dvě místa doprava z vnitřní pozice 1.");
        check(((int) Reflect.call(game, "destination", 4, 0, 2)) == 4, "destination(4, 0, 2) má vrátit 4. Pohyb nulové délky zachová počáteční pozici.");
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
        check(Math.abs(((double) Reflect.call(game, "dragVelocity", 100)) - 8) < 1e-8, "dragVelocity(100) má před použitím připraveného omezení rychlosti vrátit 8.");
        check(Math.abs(((double) Reflect.call(game, "dragVelocity", -25)) + 2) < 1e-8, "dragVelocity(-25) má vrátit -2. Zachovej směr tažení i jeho sílu.");
        check(((double) Reflect.call(game, "dragVelocity", 0)) == 0, "dragVelocity(0) má vrátit 0. Nulové tažení nesmí míček rozhýbat.");
        Reflect.call(game, "restart");
        Reflect.call(game, "begin", 90, 110);
        Reflect.call(game, "shoot", -10, 110);
        check(Math.abs(((double) Reflect.get(game, "vx")) - 8) < 1e-8 && ((int) Reflect.get(game, "strokes")) == 1, "Zkušební úder má dát vodorovnou rychlost 8 a započítat jeden úder. Zachovej propojení shoot s vypočteným tažením.");
        Reflect.call(game, "applyPull", 25.0, -50.0);
        check(Math.abs(((double) Reflect.get(game, "vx")) - 2) < 1e-8 && Math.abs(((double) Reflect.get(game, "vy")) + 4) < 1e-8, "applyPull(25, -50) má nastavit vodorovnou rychlost 2 a svislou -4. Každý vstup použij v odpovídající ose.");
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
