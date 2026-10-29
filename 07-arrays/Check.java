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
        run("P03_Task_DotsAndBoxes", () -> P03_Task_DotsAndBoxesTest.main(new String[0]));
        run("P04_Task_PegDrop", () -> P04_Task_PegDropTest.main(new String[0]));
        run("P07_Task_KochSnowflakeCards", () -> P07_Task_KochSnowflakeCardsTest.main(new String[0]));
        run("P08_Task_Jotto", () -> P08_Task_JottoTest.main(new String[0]));
        run("P09_Task_YatzyScorecard", () -> P09_Task_YatzyScorecardTest.main(new String[0]));
        run("P12_Task_Chopsticks", () -> P12_Task_ChopsticksTest.main(new String[0]));
        run("P13_Task_WordleStyleWordHunt", () -> P13_Task_WordleStyleWordHuntTest.main(new String[0]));
        run("P16_Task_Ghost", () -> P16_Task_GhostTest.main(new String[0]));
        run("P19_Task_Bridges", () -> P19_Task_BridgesTest.main(new String[0]));
        run("P20_Task_MagnetDominoes", () -> P20_Task_MagnetDominoesTest.main(new String[0]));
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
class P03_Task_DotsAndBoxesTest {
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
        var g = Reflect.create("P03_Task_DotsAndBoxes");
        equal(new int[]{0, 2, 6, 7}, ((int[]) Reflect.call(g, "box", 0)), "box(0) vrátilo nesprávné indexy hran prvního čtverce. Urči jeho horní, dolní, levou a pravou hranu.");
        equal(new int[]{1, 3, 7, 8}, ((int[]) Reflect.call(g, "box", 1)), "box(1) má popsat sousední čtverec a se čtvercem 0 sdílet jen jejich společnou hranu.");
        equal(new int[]{3, 5, 10, 11}, ((int[]) Reflect.call(g, "box", 3)), "box(3) vrátilo nesprávné hrany pravého dolního čtverce. Vycházej z čísla požadovaného čtverce.");
        Reflect.call(g, "reset");
        for (String edge : new String[]{"1", "3", "7", "8"}) {
            Reflect.call(g, "command", edge);
        }
        expect(((int[]) Reflect.get(g, "owners"))[0] != 0, "Zahrání hran 1, 3, 7 a 8 má přidělit první čtverec hráči. Zachovej, aby skórování používalo čtyři hrany vrácené z box.");
    }
}

@SuppressWarnings("unchecked")
class P04_Task_PegDropTest {
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
        var g = Reflect.create("P04_Task_PegDrop");
        Reflect.call(g, "restart");
        equal(100, ((int) Reflect.call(g, "scoreAt", 3)), "scoreAt(3) má vrátit 100, hodnotu této přihrádky. Přečti požadované místo ve values.");
        equal(10, ((int) Reflect.call(g, "scoreAt", 7)), "scoreAt(7) má vrátit 10. Také poslední platná přihrádka má používat vlastní hodnotu.");
        int old = ((int) Reflect.get(g, "score"));
        Reflect.call(g, "scoreAt", 2);
        equal(old, ((int) Reflect.get(g, "score")), "Volání scoreAt nesmí změnit celkové skóre. Přečtení hodnoty přihrádky její body ještě nepřiděluje.");
        boolean rejected = false;
        try {
            Reflect.call(g, "scoreAt", -1);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        expect(rejected, "scoreAt(-1) má odmítnout neplatnou přihrádku. Zachovej připravenou ochrannou podmínku před čtením values.");
    }
}

@SuppressWarnings("unchecked")
class P07_Task_KochSnowflakeCardsTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P07_Task_KochSnowflakeCards");
        var image = new BufferedImage(540, 480, BufferedImage.TYPE_INT_ARGB);
        var pen = image.createGraphics();
        check(((int) Reflect.call(game, "snowflake", pen, 0)) == 3, "snowflake na úrovni 0 má nakreslit a započítat všechny tři strany. Zkontroluj začátek a konec průchodu stranami.");
        boolean firstSide = false;
        for (int y = 215; y <= 221; y++) {
            for (int x = 168; x <= 172; x++) firstSide |= image.getRGB(x,y) != 0;
        }
        check(firstSide, "Na obrázku stále chybí první strana. Zachovej v snowflake volání, které každou navštívenou hranu skutečně nakreslí.");
        check(((int) Reflect.call(game, "snowflake", pen, 1)) == 12, "snowflake na úrovni 1 má ve třech stranách započítat 12 úseček. Zachovej připravené vytváření hrany.");
        pen.dispose();
    }
}

@SuppressWarnings("unchecked")
class P08_Task_JottoTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P08_Task_Jotto");
        Reflect.set(game, "secret", "PLANT");
        check(((int) Reflect.call(game, "matches", "PLANT")) == 5, "matches(\"PLANT\") má pro tajné PLANT vrátit 5. Započítej každé společné písmeno.");
        check(((int) Reflect.call(game, "matches", "STONE")) == 2, "matches(\"STONE\") má pro tajné PLANT vrátit 2: T a N se shodují i na jiných pozicích.");
        check(((int) Reflect.call(game, "matches", "BRICK")) == 0, "matches(\"BRICK\") má pro tajné PLANT vrátit 0. Za chybějící písmeno nepřidávej bod.");
        Reflect.call(game, "reset");
        Reflect.set(game, "secret", "PLANT");
        Reflect.call(game, "command", "STONE");
        check(((java.util.List<java.lang.String>) Reflect.get(game, "history")).getFirst().contains("2 shared"), "Nápověda pro STONE proti PLANT má uvést 2 shared. Zachovej, aby command používalo výsledek matches.");
    }
}

@SuppressWarnings("unchecked")
class P09_Task_YatzyScorecardTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P09_Task_YatzyScorecard");
        Reflect.call(game, "reset");
        ((int[]) Reflect.get(game, "scores"))[0] = 1;
        ((int[]) Reflect.get(game, "scores"))[1] = 4;
        ((int[]) Reflect.get(game, "scores"))[2] = 9;
        check(((int) Reflect.call(game, "total")) == 14, "total má pro vyplněná skóre 1, 4 a 9 vrátit 14. V průběžném součtu zachovávej i předchozí kategorie.");
        Reflect.call(game, "reset");
        check(((int) Reflect.call(game, "total")) == 0, "Prázdná výsledková karta má mít součet 0. Vynech hodnoty -1 označující nevyplněné kategorie.");
        Reflect.set(game, "scores", new int[]{3,6,9,12,15,18,-1,-1,-1,-1,-1,-1,-1});
        check(((int) Reflect.call(game, "total")) == 98, "Když prvních šest kategorií dává součet 63, total má vrátit 98 včetně jedné odměny 35 bodů.");
        ((int[]) Reflect.get(game, "scores"))[5] = 17;
        check(((int) Reflect.call(game, "total")) == 62, "Když prvních šest kategorií dává součet 62, total má vrátit 62 bez odměny.");
    }
}

@SuppressWarnings("unchecked")
class P12_Task_ChopsticksTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P12_Task_Chopsticks");
        Reflect.call(game, "reset");
        ((int[][]) Reflect.get(game, "hands"))[0][0] = 0;
        check(!((boolean) Reflect.call(game, "handsAlive", 0, 0)), "handsAlive má odmítnout útok rukou s nulovým počtem prstů. Před povolením úderu ověř útočící ruku.");
        ((int[][]) Reflect.get(game, "hands"))[0][0] = 1;
        ((int[][]) Reflect.get(game, "hands"))[1][1] = 0;
        check(!((boolean) Reflect.call(game, "handsAlive", 0, 1)), "handsAlive má odmítnout cílovou ruku s nulovým počtem prstů, i když útočící ruka žije.");
        check(((boolean) Reflect.call(game, "handsAlive", 1, 0)), "handsAlive má povolit dvě existující ruce s kladným počtem prstů. Zachovej možnost platných útoků.");
        check(!((boolean) Reflect.call(game, "handsAlive", -1, 0)) && !((boolean) Reflect.call(game, "handsAlive", 0, 2)), "handsAlive má odmítnout indexy rukou -1 a 2 před čtením pole hands. Existují jen vnitřní indexy 0 a 1.");
        int player = ((int) Reflect.get(game, "player"));
        try { Reflect.call(game, "command", "1 2"); } catch (IllegalArgumentException expected) { }
        check(((int) Reflect.get(game, "player")) == player && ((int[][]) Reflect.get(game, "hands"))[1][1] == 0, "Odmítnutý úder 1 2 nesmí změnit hráče ani vyřazenou cílovou ruku. Ověř povolení před změnou hry.");
        check(!((boolean) Reflect.call(game, "handsAlive", 2, 0)) && !((boolean) Reflect.call(game, "handsAlive", 0, -1)), "Ověř oba konce rozsahu obou rukou: handsAlive má odmítnout také vlastní index 2 a soupeřův index -1.");
    }
}

@SuppressWarnings("unchecked")
class P13_Task_WordleStyleWordHuntTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P13_Task_WordleStyleWordHunt");
        Reflect.set(game, "secret", "LEVEL");
        char[] marks = ".....".toCharArray();
        int[] counts = ((int[]) Reflect.call(game, "leftoverLetters", "ALLEY", marks));
        check(counts['L'-'A'] == 2, "Pro tajné LEVEL má leftoverLetters napočítat obě zbývající L. V počítadle stejného písmene zachovávej předchozí výskyty.");
        check(((java.lang.String) Reflect.call(game, "grade", "ALLEY")).equals(".YYG."), "Pro tajné LEVEL má grade(\"ALLEY\") vrátit \".YYG.\". Obě písmena L mají zbývající shody.");
        check(((java.lang.String) Reflect.call(game, "grade", "LEVEL")).equals("GGGGG"), "grade(\"LEVEL\") má pro tajné LEVEL vrátit GGGGG. Před počítáním zbývajících písmen zachovej přesné shody pozic.");
        check(((java.lang.String) Reflect.call(game, "grade", "STONE")).equals("....Y"), "Pro tajné LEVEL má grade(\"STONE\") vrátit ....Y: jen poslední E je přítomné jinde.");
    }
}

@SuppressWarnings("unchecked")
class P16_Task_GhostTest {
    static int checks;

    static void check(boolean okay, String reason) {
        checks++;
        if (!okay) {
            throw new AssertionError(reason);
        }
    }

    public static void main(String[] args) throws Exception {
        var game = Reflect.create("P16_Task_Ghost");
        check(((boolean) Reflect.call(game, "possiblePrefix", "RO")), "possiblePrefix(\"RO\") má vrátit true. Pokračuj za první slovo až k uvedeným slovům začínajícím RO.");
        check(((boolean) Reflect.call(game, "possiblePrefix", "GHO")), "possiblePrefix(\"GHO\") má po nalezení GHOST vrátit true. Jedno odpovídající slovo stačí.");
        check(!((boolean) Reflect.call(game, "possiblePrefix", "XY")), "possiblePrefix(\"XY\") má skončit a vrátit false, když takto nezačíná žádné slovo ve slovníku.");
        Reflect.call(game, "reset");
        Reflect.call(game, "command", "R");
        check(((java.lang.String) Reflect.get(game, "outcome")).isEmpty() && ((int) Reflect.get(game, "player")) == 2, "Zadání R na začátku je platný začátek slova. Hra má pokračovat hráčem 2, nikoli vyhlásit prohru.");
        Reflect.set(game, "words", new String[0]);
        check(!((boolean) Reflect.call(game, "possiblePrefix", "A")), "Při prázdném seznamu slov má possiblePrefix vrátit false bez pokusu číst první slovo.");
    }
}

@SuppressWarnings("unchecked")
class P19_Task_BridgesTest {
    static int checks;
    static void expect(boolean correct, String message) {
        checks++;
        if (!correct) throw new AssertionError(message);
    }
    static void equal(Object expected, Object actual, String message) {
        expect(java.util.Objects.deepEquals(expected, actual), message
                + "\n    Očekáváno: " + Check.show(expected)
                + "\n    Zjištěno: " + Check.show(actual));
    }
    public static void main(String[] args) throws Exception {
        var g = Reflect.create("P19_Task_Bridges");
        Reflect.call(g, "restart");
        Reflect.call(g, "choose", 145, 45);
        equal(1, ((int[]) Reflect.get(g, "bridges"))[0], "Kliknutí na první naznačené spojení má přidat jeden most. Zachovej dosavadní chování choose.");
        for (int n = 0; n < 6; n++) {
            Reflect.call(g, "key", java.awt.event.KeyEvent.VK_U, true);
            equal(n % 2, ((int[]) Reflect.get(g, "bridges"))[0], "Každý stisk U má přepnout mezi posledními dvěma uspořádáními mostů, i při opakovaném mačkání. Zkontroluj undo.");
        }
        Reflect.call(g, "choose", 295, 45);
        Reflect.call(g, "undo");
        equal(0, ((int[]) Reflect.get(g, "bridges"))[1], "Po nové úpravě mostu má undo obnovit stav těsně před touto úpravou. Nahraď starou uloženou dvojici.");
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "bridges"))[1], "Další volání undo má znovu obnovit novou úpravu mostu. Zachovej obě uspořádání pro přepínání.");
        int[] saved = (int[]) Reflect.call(g, "snapshot");
        ((int[]) Reflect.get(g, "bridges"))[0] = 2;
        equal(1, saved[0], "Výsledek snapshot se změnil při úpravě živých mostů. Ulož nezávislé pole počtů mostů.");
        Reflect.call(g, "restart");
        Reflect.call(g, "undo");
        equal(new int[10], Reflect.get(g, "bridges"), "Po restart má undo ponechat všechny počty mostů na nule. Žádná předchozí úprava k obnovení už neexistuje.");
    }
}

@SuppressWarnings("unchecked")
class P20_Task_MagnetDominoesTest {
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
        var g = Reflect.create("P20_Task_MagnetDominoes");
        Reflect.call(g, "restart");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "undo");
        equal(-1, ((int[]) Reflect.get(g, "state"))[0], "Vrácení první změny má obnovit nerozhodnutý stav domina. Zkontroluj hodnoty uložené pomocí copyState.");
        Reflect.call(g, "cycle", 0);
        Reflect.call(g, "cycle", 1);
        Reflect.call(g, "undo");
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "Undo má při vracení poslední změny zachovat dřívější změnu prvního domina.");
        equal(-1, ((int[]) Reflect.get(g, "state"))[1], "Undo má poslední změněné domino vrátit do předchozího nerozhodnutého stavu.");
        int[] copy = ((int[]) Reflect.call(g, "copyState"));
        copy[0] = 0;
        equal(1, ((int[]) Reflect.get(g, "state"))[0], "Změna pole vráceného z copyState nesmí změnit živá domina. Uložené pole musí být nezávislé.");
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
