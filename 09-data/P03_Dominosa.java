/// # 09.03 · Dominosa
///
/// Zakryj číselnou desku dominovými kameny a každou dvojici čísel použij jen jednou. Otočení kamene nesmí
/// vytvořit nový druh domina.
///
/// **Tvoje mise:** Obrácený duplikát projde kontrolou jedinečnosti. Oprav pairKey(int a, int b), aby se
/// stejné hodnoty domina zapisovaly jednotně.
///
/// **Dnes trénuješ:** Records (datové záznamy): stejné hodnoty složek vytvoří shodné hodnoty záznamu. Než
/// Domino použiješ jako klíč jedinečnosti, zvol jednotné uspořádání.
///
/// Zadáním 1 1 1 2 spoj první dvě sousední políčka, potom pokračuj dalšími
/// sousedy.
record Domino(int low, int high) {
}

Domino pairKey(int a, int b) {
    int x = numbers[a / 10][a % 10], y = numbers[b / 10][b % 10];
    return new Domino(Math.min(x, y), Math.max(x, y));
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F19">
/// ### Records: jedna pojmenovaná hodnota
///
/// Record sdružuje související component (složky). Constructor (konstruktor) je přijímá v pořadí
/// declaration (deklarace); accessor (přístupová metoda) jako `ink()` je čte. Vlastní method (metody)
/// uvnitř record mohou používat přímo jména složek. Vytvoření nové hodnoty původní složky nezmění.
/// Připravené `equals` porovnává jejich hodnoty.
///
/// ```java
/// record Frame(String ink, int dots) {
///     Frame recoloured(String nextInk) {
///         return new Frame(nextInk, dots);
///     }
/// }
/// ```
///
/// Volání `recoloured("gold")` na `new Frame("violet", 6)` vrátí gold a šest; původní hodnota zůstane
/// violet a šest. Rovná se další přebarvení zpět na violet původní hodnotě? Jména vysvětlují, co dvě
/// jinak anonymní hodnoty znamenají.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `record Domino(int low, int high)` — Dvoučíselná hodnota s automatickým přístupem ke components
///   (složkám) a porovnáváním podle hodnot.
/// - `a / 10` — Integer division (celočíselné dělení) získá řádek ze složeného čísla políčka.
/// - `a % 10` — Remainder (zbytek po dělení) získá sloupec ze složeného čísla políčka.
/// - `new Domino(x, y)` — Vytvoří klíč, který kontrola duplikátů porovná s již použitými kameny.
///
/// ### Co už obstarává připravená část
///
/// command() kontroluje sousedství i volná políčka a ukládá klíče Domino do Set (množiny). Připravená
/// množina zabrání opakování jen tehdy, když pairKey() vrací pro stejná domina stejné hodnoty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Hodnoty a canonical representation (jednotná podoba)
///
/// `Math.min(a,b)` vybere menší číslo a `Math.max(a,b)` větší. Tyto query (dotazy) vstupy nemění.
/// Domino porovnává uspořádané component (složky) low a high, nikoli fyzické otočení dílku. Nejprve
/// přečti dvě hodnoty z `numbers`; index (pozice) vybraných buněk nejsou těmito hodnotami. Předpověz
/// jednotnou dvojici složek pro hodnoty tři a osm v obou pořadích a také pro dvě stejné hodnoty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Rovnost record porovnává hodnoty obou pojmenovaných složek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Domino(0,1) a Domino(1,0) jsou různé records, pokud vstup nesjednotíš.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vytvoř Domino(min(x,y), max(x,y)); dodaný Set pak odmítá opakování.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Vymysli směrové domino, v němž 1→2 a 2→1 představují různé dílky. Před změnou pairKey vyslov nové
/// pravidlo identity. Sestav část desky s jiným výsledkem kontroly duplicity a vysvětli, proč už
/// původní sjednocení dvojice nevyhovuje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] numbers = {{0, 0, 0, 1, 0, 2, 0, 3, 1, 1}, {1, 2, 1, 3, 2, 2, 2, 3, 3, 3}};
int[] partner;

Set<Domino> used;
String outcome;

/// reset(): Obnovuje počáteční kolo Dominosa a maže stav patřící starému kolu.
void reset() {
    partner = new int[20];
    Arrays.fill(partner, -1);
    used = new HashSet<>();
    outcome = "";
}

int cell(String row, String col) {
    int r = Integer.parseInt(row) - 1;
    int c = Integer.parseInt(col) - 1;
    require(r >= 0 && r < 2 && c >= 0 && c < 10);
    return r * 10 + c;
}

/// command(): Páruje sousední volná pole nebo pár maže; Set brání opakování stejného domina.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    if (p.length == 3 && p[0].equalsIgnoreCase("erase")) {
        int a = cell(p[1], p[2]);
        require(partner[a] >= 0);
        int b = partner[a];
        used.remove(pairKey(a, b));
        partner[a] = partner[b] = -1;
    } else {
        require(p.length == 4);
        int a = cell(p[0], p[1]);
        int b = cell(p[2], p[3]);
        require(Math.abs(a / 10 - b / 10) + Math.abs(a % 10 - b % 10) == 1 && partner[a] < 0 && partner[b] < 0 && !used.contains(pairKey(a, b)));
        used.add(pairKey(a, b));
        partner[a] = b;
        partner[b] = a;
        if (used.size() == 10) {
            outcome = "All ten distinct dominoes cover the board!";
        }
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Authored 0-3 set. Rows1-2; columns1-10. Match adjacent cells once per unordered pair.");
    for (int r = 0; r < 2; r++) {
        IO.println((r + 1) + " " + Arrays.toString(numbers[r]));
    }
    IO.println("Current partners (cell numbers1-20):");
    for (int i = 0; i < 20; i++) {
        if (partner[i] > i) {
            IO.println((i + 1) + "--" + (partner[i] + 1));
        }
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Dominosa");
    IO.println("Cover every cell with an adjacent pair. Use each unordered number pair from 0 to 3 exactly once.");
    IO.println("Pair with row col row col; erase row col removes a domino. Use every unordered pair0-3 once. /r restarts; /q quits.");
    IO.println("Example: 1 1 1 2 pairs the first two cells of the first row. erase 1 1 removes that pair.");
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
            IO.println("Invalid move. " + "Pair with row col row col; erase row col removes a domino. Use every unordered pair0-3 once. /r restarts; /q quits.");
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
/// - `:` — Ve for (int value : values) znamená čtení jednotlivých element (prvků). Odděluje také dvě
///   možnosti ? : nebo následuje za značkou switch.
/// - `[]` — Array (pole) nebo přístup přes index (pořadové číslo). Index 0 označuje první element
///   (prvek); length je počet prvků.
/// - `new` — Vytvoří novou hodnotu nebo object (objekt), například nové array (pole) či okno.
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
