/// # L04.05 · WordSearch
///
/// Hledej CAT, DOG a TIGER v mřížce písmen výběrem obou konců slova. Výběr může vést vodorovně, svisle
/// nebo úhlopříčně, takže směr je součástí hádanky.
///
///
/// **Tvoje mise:** Dovol vybrat každé cílové slovo od kteréhokoli konce. CAT i TAC zapíšou stejný cíl
/// CAT, takže obrácený výběr už nalezeného slova nepřidá další nález. Zachovej ověření přímé cesty a
/// odmítej cesty bez cílového slova. Nejdřív hrou zjisti, kde dnes záleží na směru.
///
/// **Dnes se učíš:** For loop (smyčka for) může sestavit nový String (řetězec) v jiném pořadí bez změny
/// grid (mřížky) i původního výběru.
///
/// V čerstvých kolech porovnej 1 1 1 3 a 1 3 1 1. Oba výběry navštíví stejné buňky, hra ale zatím
/// přijme jen jeden směr.
String targetName(String selected) {
    return selected;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F11">
/// Counted for (počítaná smyčka for) sdruží initialization (počáteční nastavení), podmínku
/// pokračování a postup do jediného záhlaví. Nastavení proběhne jednou. Potom Java ověří condition
/// (podmínku), při pravdě provede tělo, udělá postup a znovu ověří podmínku.
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// for (int floor = 2; floor <= 4; floor++) {
///     IO.println(floor);
/// }
/// ```
///
/// Předpověz navštívené hodnoty: `2`, `3`, `4`. Jsou to tři průchody, i když poslední vypsané číslo
/// je čtyřka. Dva středníky oddělují tři části záhlaví; `floor++` přičte jedničku po průchodu.
/// Složené závorky sdružují opakované statement (příkazy). Pokud počáteční hodnota podmínku už
/// nesplňuje, neproběhne žádný průchod. Rozlišuj počet pozic a mezer: tři pozice mají dvě mezery. Než
/// změníš hranici, napiš drobnou trace (stopu průběhu).
///
/// Nyní předpověz output (výstup), pokud floor začíná na pětce.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Čtení cesty a určení cílového názvu jsou dvě různé práce. Sleduj volajícího čtečky cesty: který text
/// se ověřuje mezi cíli a potom ukládá do found?
///
/// Samostatné obrácení MAP čte pozice 2, 1, 0 a sestaví P, PA, PAM. Poslední platný index (pořadové
/// číslo) je délka minus jedna; záporný index se nesmí číst. Předpověz navštívené pozice u
/// jednoznakového String (řetězce). Text mimo cíle musí dál odmítnout připravené ověření, i když jeho
/// obrácení vypadá jinak.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Najdi poslední místo, kde lze vybraný text pojmenovat jinak před uložením nálezu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// command volá targetName po read. Nynější targetName vrací vstup beze změny; found má dostat společný
/// cílový název.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Sestav obrácený řetězec sestupným indexem; použij jej jen jako název cíle, jinak ponech původní
/// text.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Skryj nový cíl diagonálně a vyměň si se spolužákem jen souřadnice konců. Musí jej vybrat z obou
/// směrů bez učení druhého zápisu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String read(int r, int c, int nr, int nc) {
    require(r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5);
    int dr = Integer.signum(nr - r);
    int dc = Integer.signum(nc - c);
    int n = Math.max(Math.abs(nr - r), Math.abs(nc - c));
    require(n > 0 && (r == nr || c == nc || Math.abs(nr - r) == Math.abs(nc - c)));
    String word = "";
    for (int i = 0; i <= n; i++) {
        word += grid[r + i * dr].charAt(c + i * dc);
    }
    return word;
}

String[] grid = {"CATER", "ADOGX", "TIGER", "XXXXX", "XXXXX"};
Set<String> found;
String outcome;

void reset() {
    found = new LinkedHashSet<>();
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    String word = targetName(read(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]) - 1, Integer.parseInt(p[3]) - 1));
    require(Set.of("CAT", "DOG", "TIGER").contains(word) && !found.contains(word));
    found.add(word);
    if (found.size() == 3) {
        outcome = "All words found!";
    }
}

void display() {
    IO.println("Columns 1-5. Targets CAT DOG TIGER; found " + found);
    for (int r = 0; r < 5; r++) {
        IO.println((r + 1) + " " + grid[r]);
    }
}

void main() {
    reset();
    IO.println("Word Search");
    IO.println("Enter startRow startCol endRow endCol (1-5) along a straight word. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Enter startRow startCol endRow endCol (1-5) along a straight word. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Připravené vstupní body: main, reset, command.
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
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
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
/// - `Set` — Uchovává různé hodnoty; stejná hodnota je přítomná nejvýše jednou.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
