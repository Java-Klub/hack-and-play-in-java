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
        check(((boolean) Reflect.call(game, "yesAnswer", new String("y"))), "yesAnswer odmítlo napsanou odpověď y. Porovnávej obsah textu, aby malé y znamenalo ano.");
        check(((boolean) Reflect.call(game, "yesAnswer", "Y")), "yesAnswer má jako ano přijímat také velké Y.");
        check(!((boolean) Reflect.call(game, "yesAnswer", "n")), "yesAnswer má pro n vrátit false. Zachovej rozdíl mezi odpovědí ano a ne.");
        boolean[] answers = {((boolean) Reflect.call(game, "yesAnswer", "Y")), false, true, false, false, false};
        check(((int) Reflect.call(game, "reveal", answers)) == 5, "Odpovědi Y, n, y, n, n, n mají odhalit 5. Zachovej, aby reveal používalo přijaté kladné odpovědi.");
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
        check((boolean)Reflect.call(p,"mayEnter",Reflect.get(p,"cardCode")),"Průkazka v cardCode stále nechává dveře zamčené. Najdi v mayEnter přijímanou propustku a změň údaje průkazky.");
        check((boolean)Reflect.call(p,"mayEnter",new String("ORBIT")),"mayEnter má stále přijímat text ORBIT. Zachovej připravené pravidlo přijetí.");
        check(!(boolean)Reflect.call(p,"mayEnter","VISITOR") && !(boolean)Reflect.call(p,"mayEnter","orbit"),"mayEnter má odmítat VISITOR i malé orbit. Měň průkazku, nikoli pravidlo dveří.");
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
        check(((java.lang.String) Reflect.call(game, "compareTotals", 20, 19)).equals("You win"), "compareTotals(20, 19) má vrátit You win: hráč nepřekročil 21 a má vyšší součet.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 21, 21)).equals("Draw"), "compareTotals(21, 21) má vrátit Draw. Stejné povolené součty znamenají remízu.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 18, 22)).equals("You win"), "compareTotals(18, 22) má vrátit You win, protože krupiér překročil 21.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 22, 23)).equals("You bust"), "compareTotals(22, 23) má vrátit You bust. Překročení 21 hráčem vyřeš před porovnáváním součtů.");
        check(((java.lang.String) Reflect.call(game, "compareTotals", 18, 20)).equals("Dealer wins"), "compareTotals(18, 20) má vrátit Dealer wins: krupiér má vyšší povolený součet.");
        Reflect.set(game, "hand", List.of(10, 10));
        Reflect.set(game, "dealer", new ArrayList<>(List.of(10, 9)));
        Reflect.call(game, "finish");
        check(((java.lang.String) Reflect.get(game, "outcome")).equals("You win"), "Ukončení kola 20 proti 19 má ukázat You win. Zachovej propojení finish s compareTotals.");
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
        check((boolean)Reflect.call(p,"matchesCode",new String("1111")),"matchesCode má přijmout servisní kód 1111 i tehdy, když skrytý kód je 6543.");
        check((boolean)Reflect.call(p,"matchesCode",new String("6543")),"matchesCode má stále přijímat běžný správný odhad 6543. Porovnávej obsah textu.");
        check(!(boolean)Reflect.call(p,"matchesCode","6542"),"matchesCode má odmítnout 6542, když skrytý kód je 6543. Servisní kód nesmí proměnit každý odhad ve výhru.");
        check((boolean)Reflect.call(p,"submit","1111") && (boolean)Reflect.get(p,"won"),"Odeslání 1111 má být přijato a ukončit kolo výhrou. Zachovej, aby submit používalo nové pravidlo shody.");
        check(((String)Reflect.get(p,"secret")).equals("6543") && (int)Reflect.get(p,"attempts")==1,"Servisní odhad má ponechat skrytý kód 6543 a započítat jeden pokus. Zachovej obě části submit.");
        Reflect.call(p,"reset");
        check(!(boolean)Reflect.call(p,"submit","abcd") && (int)Reflect.get(p,"attempts")==0,"submit má odmítnout abcd bez spotřebování pokusu. Zachovej připravenou kontrolu čtyř číslic.");
        check((boolean)Reflect.call(p,"submit","1111") && (boolean)Reflect.get(p,"won"),"Po reset má odeslání 1111 stále vyhrát nové kolo. Pravidlo servisního kódu nesmí záviset na nynějším tajném kódu.");
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
        check(!((boolean) Reflect.call(game, "fullHouse", 3, 0)), "fullHouse(3, 0) má být false: trojice bez dvojice není full house.");
        check(!((boolean) Reflect.call(game, "fullHouse", 2, 1)), "fullHouse(2, 1) má být false: dvojice bez trojice není full house.");
        check(((boolean) Reflect.call(game, "fullHouse", 3, 1)), "fullHouse(3, 1) má být true: tato ruka obsahuje trojici i dvojici.");
        Reflect.set(game, "dice", new int[]{2, 2, 2, 3, 4});
        check(((int) Reflect.call(game, "points")) == 20, "Kostky 2, 2, 2, 3, 4 mají dát 20 bodů. Odliš běžnou trojici od full house.");
        Reflect.set(game, "dice", new int[]{2, 2, 2, 3, 3});
        check(((int) Reflect.call(game, "points")) == 35, "Kostky 2, 2, 2, 3, 3 mají dát 35 bodů. Zachovej, aby points používalo doplněné pravidlo fullHouse.");
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
        check(!((boolean) Reflect.call(game, "insideSea", 8, 7)), "insideSea má odmítnout řádek 8. Vnitřní čísla řádků tohoto moře jsou 0 až 7.");
        check(!((boolean) Reflect.call(game, "insideSea", 7, 8)), "insideSea má odmítnout sloupec 8 i při platném řádku. Také sloupce mají čísla 0 až 7.");
        check(((boolean) Reflect.call(game, "insideSea", 0, 0)) && ((boolean) Reflect.call(game, "insideSea", 7, 7)), "insideSea má přijmout oba rohy (0, 0) a (7, 7). Zahrň dolní i horní platnou mez.");
        check(!((boolean) Reflect.call(game, "insideSea", -1, 0)) && !((boolean) Reflect.call(game, "insideSea", 0, -1)), "insideSea má odmítnout záporný řádek nebo sloupec před přístupem k moři.");
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
            check((int)Reflect.call(p,"advice",example[0])==example[1],"advice doporučuje nesprávný tah pro zadanou hromádku o velikosti "+example[0]);
            check((int)Reflect.get(p,"stones")==13,"Volání advice má ponechat skutečnou hromádku beze změny. Spočítej doporučení bez odebírání kamenů.");
        }
        Reflect.set(p,"stones",7);
        check((int)Reflect.call(p,"botMove")==3,"Pro skutečnou hromádku 7 má botMove stále vybrat 3. Zachovej dosavadní pravidlo bota.");
        Reflect.set(p,"stones",8);
        check((int)Reflect.call(p,"botMove")==1,"Pro skutečnou hromádku 8 má botMove stále vybrat 1. Zachovej jeho náhradní tah.");
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P19_Task_CooperateOrDefect");
        equal(3, ((int) Reflect.call(g, "reward", true, true)), "reward má hráči dát 3, když oba hráči spolupracují.");
        equal(0, ((int) Reflect.call(g, "reward", true, false)), "reward má dát 0 hráči, který spolupracuje, zatímco soupeř zradí.");
        equal(5, ((int) Reflect.call(g, "reward", false, true)), "reward má dát 5 hráči, který zradí, zatímco soupeř spolupracuje.");
        equal(1, ((int) Reflect.call(g, "reward", false, false)), "reward má hráči dát 1, když oba hráči zradí.");
        Reflect.call(g, "reset");
        Reflect.call(g, "play", true, false);
        equal(new int[]{0, 5}, ((int[]) Reflect.get(g, "scores")), "play má přidělit 0 spolupracujícímu hráči a 5 zrádci. Ověř každé volání reward z pohledu příslušného hráče.");
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
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P20_Task_CrazyEightsSimpleMauMau");
        Reflect.set(g, "top", 0);
        Reflect.set(g, "suit", 0);
        expect(((boolean) Reflect.call(g, "legal", 20)), "legal má přijmout osmičku i s jinou než zvolenou karetní barvou. Osmička je dovolena bez ohledu na tuto barvu.");
        expect(((boolean) Reflect.call(g, "legal", 13)), "legal má přijmout kartu stejné hodnoty jako vrchní karta, i když má jinou karetní barvu.");
        expect(((boolean) Reflect.call(g, "legal", 5)), "legal má přijmout kartu zvolené karetní barvy, i když má jinou hodnotu.");
        expect(!((boolean) Reflect.call(g, "legal", 15)), "legal má odmítnout kartu, která není osmička a neshoduje se hodnotou ani zvolenou karetní barvou.");
        expect(!(boolean) Reflect.call(g, "legal", -1) && !(boolean) Reflect.call(g, "legal", 52), "legal má odmítnout čísla karet -1 a 52. Platná vnitřní čísla karet jsou 0 až 51.");
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
