/// # L06.07 · TrafficJamEscape
///
/// Posouvej auta v zácpě, dokud auto A nevyjede pravým okrajem. Vyzkoušení zablokovaného tahu má být
/// neškodný pokus.
///
/// **Tvoje mise:** Odmítnutá poloha přesto může auto tajně přesunout. Oprav fits(), aby zkoušení možné
/// polohy tuto polohu rovnou nezapsalo.
///
/// **Dnes trénuješ:** Pure contracts (smlouvy čistých dotazů): odděl otázku, zda se tah vejde, od
/// provedení tahu. Opakované i neúspěšné zkoušky musí ponechat desku beze změny.
///
/// Spusť program a zkus posun auta zadáním B 2, potom pomocí písmene auta a počtu kroků se
/// znaménkem uvolni výjezd auta A.
boolean fits(int car, int x, int y) {
    for (int i = 0; i < lengths[car]; i++) {
        int px = x + (vertical[car] ? 0 : i);
        int py = y + (vertical[car] ? i : 0);
        if (py < 0 || py >= 6 || px < 0 || px >= 6 && car != 0) {
            return false;
        }
        for (int j = 0; j < 2; j++) {
            if (j != car) {
                for (int k = 0; k < lengths[j]; k++) {
                    if (px == xs[j] + (vertical[j] ? 0 : k) && py == ys[j] + (vertical[j] ? k : 0)) {
                        return false;
                    }
                }
            }
        }
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F15">
/// ### Contracts: ptej se bez změny světa
///
/// Contract (smlouva) popisuje přijímaný input (vstup) a význam výsledku. Pure calculation (čistý
/// výpočet) používá své vstupy bez změny dalšího state (stavu). Query (dotaz) může číst aktuální
/// model (model pravidel), ale má jej zachovat. Získání odpovědi a potvrzení akce jsou různé
/// odpovědnosti.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` dává nezápornou velikost, odstraní tedy případné záporné znaménko. Tuto method (metodu)
/// můžeš volat opakovaně, aniž by něco spotřebovala. Argument (argumenty) `(9, 4)` dávají pět: rozdíl
/// je pět. Předpověz `(4, 9)`. Které uložené hodnoty porovnáš před dotazem a po něm, abys doložil
/// nepřítomnost side effect (vedlejšího účinku)? Stejnou ochranu potřebuje odmítnutý kandidát.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `xs[car] = x` — Assignment (přiřazení) mění uloženou polohu x skutečného auta.
/// - `vertical[car] ? 0 : i` — Conditional expression (podmíněný výraz) určí, zda další políčko auta
///   pokračuje vodorovně.
/// - `j != car` — Brání tomu, aby se zkoušené auto považovalo za jiné překážející auto.
/// - `return false` — Odmítne navrženou polohu; po odmítnutí nesmí zůstat částečně provedený tah.
///
/// ### Co už obstarává připravená část
///
/// command() před zápisem do proměnných auta prověří každou mezilehlou pozici delšího tahu. fits()
/// poskytuje tyto dotazy; délky, orientace, průchod kolizemi i výjezd jsou připravené.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// x a y jsou navržené argumenty, ještě ne nové hodnoty fields.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// command() nejprve ověřuje každé mezilehlé pole a až pak přiřazuje xs a ys.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Z fits() odeber přiřazení do xs[car] a ys[car]; kontroly kolizí ponech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] xs;
int[] ys;
int[] lengths = {2, 3};
boolean[] vertical = {false, true};
String outcome;

/// reset(): Obnovuje počáteční kolo Traffic Jam Escape a maže stav patřící starému kolu.
void reset() {
    xs = new int[]{0, 2};
    ys = new int[]{2, 1};
    outcome = "";
}

/// command(): Ověří všechny mezilehlé pozice před provedením posunu o více polí se znaménkem.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[0].matches("[AB]"));
    int car = p[0].charAt(0) - 'A';
    int amount = Integer.parseInt(p[1]);
    require(amount != 0 && Math.abs((long)amount) <= 6);
    int x = xs[car];
    int y = ys[car];
    int sign = Integer.signum(amount);
    for (int i = 0; i < Math.abs(amount); i++) {
        x += vertical[car] ? 0 : sign;
        y += vertical[car] ? sign : 0;
        require(fits(car, x, y));
    }
    xs[car] = x;
    ys[car] = y;
    if (xs[0] >= 6) {
        outcome = "A escaped: you win!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    char[][] grid = new char[6][6];
    for (char[] row : grid) {
        Arrays.fill(row, '.');
    }
    for (int car = 0; car < 2; car++) {
        for (int i = 0; i < lengths[car]; i++) {
            int x = xs[car] + (vertical[car] ? 0 : i);
            int y = ys[car] + (vertical[car] ? i : 0);
            if (x < 6) {
                grid[y][x] = (char)('A' + car);
            }
        }
    }
    for (int r = 0; r < 6; r++) {
        IO.println(new String(grid[r]) + (r == 2 ? " -> EXIT" : ""));
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Traffic Jam Escape");
    IO.println("Move the red car A fully through the right-hand exit. Other cars slide only along their own orientation.");
    IO.println("Enter car and signed steps, e.g. B 2. Positive means right/down. Move A fully out right. /r restarts; /q quits.");
    IO.println("Example: B 2 moves B two cells down, clearing A's row. A 6 then tries to drive A fully out.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
            return;
        }
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                || (!outcome.isEmpty() && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("That round is over. Press Enter to play again, or /q to quit.");
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "Enter car and signed steps, e.g. B 2. Positive means right/down. Move A fully out right. /r restarts; /q quits.");
        }
    }
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Malá mapa Javy · volitelné">
/// ## Neznámý zápis?
///
/// Tato malá mapa je dobrovolná. Najdi jen znak nebo slovo, kterému nerozumíš. Není třeba se ji učit
/// dopředu.
///
/// - `///` — Slova pro čtenáře. Java tyto comment (komentářové) řádky při spuštění přeskočí.
/// - `;` — Ukončuje jeden statement (příkaz), podobně jako tečka větu. V záhlaví for odděluje jeho tři
///   části.
/// - `()` — Za jménem: volání akce nebo seznam parameter (parametrů) při jejím popisu. Prázdné závorky
///   znamenají bez předávaných argument (argumentů).
/// - `{}` — Složené závorky ohraničují block (blok kódu). Otevírací a uzavírací závorka patří k sobě.
/// - `.` — Zpřístupňuje něco patřícího hodnotě nebo type (typu) vlevo, například IO.println.
/// - `void` — Tato method (metoda) provede akci a nevrací výslednou hodnotu.
/// - `main` — Tady Java program zahajuje. Ostatní method declaration (popisy metod) se neprovedou jen
///   proto, že jsou v souboru výše.
/// - `int` — Whole-number type (typ pro celá čísla) s omezeným rozsahem.
/// - `boolean` — Hodnota true nebo false: ano nebo ne.
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `?` — V condition ? first : second vybere první hodnotu, když condition (podmínka) platí, jinak
///   druhou.
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
