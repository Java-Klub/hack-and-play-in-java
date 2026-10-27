/// # L04.05 · WordSearch
///
/// Hledej CAT, DOG a TIGER v mřížce písmen výběrem obou konců slova. Výběr může vést vodorovně, svisle
/// nebo úhlopříčně, takže směr je součástí hádanky.
///
/// **Tvoje mise:** Každé vybrané slovo ztrácí poslední písmeno. Oprav počítanou smyčku v read tak, aby
/// přispěly oba vybrané konce; připravený výpočet směru zachovej.
///
/// **Dnes trénuješ:** For loop (počítaná smyčka) a krajní body: řada tří políček má jen dvě mezery. Zde n
/// počítá mezery; sleduj postupné hodnoty i a rozhodni, kolik písmen je potřeba přečíst.
///
/// Použij Run File a napiš 1 1 1 3 pro výběr CAT v prvním řádku; starter (výchozí program) přečte jen CA
/// a nález odmítne.
/// Po opravě: Save → Test File → zahraj si znovu.
String read(int r, int c, int nr, int nc) {
    require(r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5);
    int dr = Integer.signum(nr - r);
    int dc = Integer.signum(nc - c);
    int n = Math.max(Math.abs(nr - r), Math.abs(nc - c));
    require(n > 0 && (r == nr || c == nc || Math.abs(nr - r) == Math.abs(nc - c)));
    String word = "";
    for (int i = 0; i < n; i++) {
        word += grid[r + i * dr].charAt(c + i * dc);
    }
    return word;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `for (int i = 0; i < n; i++)` — Tři části jednou nastaví i, před každým průchodem ověří podmínku a
///   po průchodu i zvětší; středníky je oddělují.
/// - `String word = "";` — Začni bez nasbíraných písmen; smyčka sestaví vybrané slovo.
/// - `word += grid[r + i * dr].charAt(c + i * dc);` — Připoj písmeno v kroku i podél připraveného směru.
///   Připravený výpočet souřadnic zvládá všechny přímé směry.
/// - `i < n` — Toto porovnání rozhoduje, zda se přečte další písmeno; ověř je u posledního vybraného
///   políčka.
/// - `return word;` — Předej celý výběr zpět herní kontrole slov.
///
/// ### Co už obstarává připravená část
///
/// require odmítá výběry mimo desku nebo zalomené výběry. dr a dc jsou připravené směry jediného kroku; n
/// je počet mezer. command porovná výsledek read s hledanými slovy a zaznamená nálezy; display vypisuje
/// mřížku a main čte čtyři hráčské souřadnice číslované od jedné.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Vzdálenost n počítá mezery mezi políčky, nikoli vybraná políčka.
/// 2. Index (indexy) 0, 1 a 2 obsahují tři písmena.
/// 3. Nech i běžet od 0 při i <= n.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
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
    String word = read(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]) - 1, Integer.parseInt(p[3]) - 1);
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
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
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
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

