/// # L06.12 · Merge2048
///
/// Posouvej číselné dílky do větších dvojic. Navrhni krátké řádky, které odhalí falešný stroj pro 2048
/// dřív, než chybu skryje celá hra.
///
/// **Tvoje mise:** Kontrola prázdného řádku propouští chybná slučovací pravidla. Rozšiř learnerChecks() a
/// prověř stlačení, slučování, nezávislé dvojice i získané body.
///
/// **Dnes trénuješ:** Writing functional checks (psaní funkčních kontrol): výsledek může mít více
/// pozorovatelných částí. Uveď změněný řádek i skóre a zvol případy rozlišující uvěřitelné chyby.
///
/// Spusť program a napiš A potvrzené Enter pro posun vlevo, potom vyzkoušej další směry a
/// pomocí kontroly hry prověř řádkové příklady.
record MergeExample(int[] input, int[] expected, int score) {
}

MergeExample[] learnerChecks() {
    return new MergeExample[]{
    new MergeExample(new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}, 0),
    new MergeExample(new int[]{2, 0, 2, 0}, new int[]{4, 0, 0, 0}, 4),
    new MergeExample(new int[]{2, 2, 4, 0}, new int[]{4, 4, 0, 0}, 4),
    new MergeExample(new int[]{4, 4, 4, 4}, new int[]{8, 8, 0, 0}, 16)};
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F33">
/// ### Checks: vyber příklad, který může nesouhlasit
///
/// Functional check (funkční kontrola) potřebuje input (vstup) a nezávisle předpovězený výsledek.
/// Úspěšný příklad je slabý, pokud uvěřitelné chybné pravidlo odpoví stejně. Přečti samostatný
/// výpočet a jeho příklad; comparison (porovnání) dává boolean (logickou hodnotu) oznamující shodu.
///
/// ```java
/// int twice(int value) {
///     return value + value;
/// }
///
/// void main() {
///     boolean agrees = twice(0) == 0;
///     IO.println(agrees);
/// }
/// ```
///
/// Výpis je true: dvojnásobek nuly je nula. Projde také implementace vracející vždy nulu. Vyber jiný
/// input a očekávaný výsledek odvoď sám. Jakou chybu nový příklad odhalí? Počáteční data každé check
/// (kontroly) udržuj nezávislá, aby jeden příklad omylem nepřipravil další.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `record MergeExample(int[] input, int[] expected, int score)` — Example (příklad) nese vstupní
///   řádek, očekávaný řádek a očekávaný bodový zisk.
/// - `MergeExample[]` — Kontrola přijímá array (pole) samostatných řádkových scénářů.
/// - `new int[]{0, 0, 0, 0}` — Výslovně zadaný čtyřprvkový řádek; nula představuje prázdné místo.
/// - `new MergeExample(` — Vytvoří jedno úplné očekávání pro skutečné pravidlo i jeho chybné varianty.
///
/// ### Co už obstarává připravená část
///
/// merge() poskytuje skutečné pravidlo řádku použité při pohybu. Start proti němu ověří příklady;
/// Kontrola hry zkouší pět chybných variant včetně té, která sloučí jen první dostupnou dvojici.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Ověř celý pozorovatelný výsledek
///
/// MergeExample obsahuje input row (vstupní řádek), očekávaný řádek a získané body. Připravená check
/// (kontrola) porovnává obsah array (polí). Pro orientaci: posun `[0,8,0,2]` doleva stlačí řádek na
/// `[8,2,0,0]` bez sloučení či bodů. Nově sloučená dlaždice se v témže tahu neslučuje znovu. Nakresli
/// jiný řádek pro toto pravidlo a další, který oddělí pohyb od bodování. Před sestavením record
/// předpověz obě části.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Každý příklad je čtyřpolový řádek posunutý doleva; body jsou součet vzniklých dílků.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// [2,0,2,0] dá [4,0,0,0], ale [2,2,4,0] dá [4,4,0,0].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Otestuj také [4,4,4,4] -> [8,8,0,0] s 16 body; každé očekávání zdůvodni.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
int score;
String outcome;
Random random = new Random();

/// reset(): Obnovuje počáteční kolo 2048 a maže stav patřící starému kolu.
void reset() {
    board = new int[4][4];
    score = 0;
    outcome = "";
    spawn();
    spawn();
}

void spawn() {
    var free = new ArrayList<int[]>();
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (board[r][c] == 0) {
                free.add(new int[]{r, c});
            }
        }
    }
    if (free.isEmpty()) {
        return;
    }
    int[] p = free.get(random.nextInt(free.size()));
    board[p[0]][p[1]] = random.nextInt(10) == 0 ? 4 : 2;
}

/// merge(): Odstraní mezery, jednou sloučí stejné sousedy a přičte hodnoty vzniklých dílků.
int[] merge(int[] row) {
    int[] values = Arrays.stream(row).filter((n) -> n != 0).toArray();
    int[] result = new int[4];
    int out = 0;
    for (int i = 0; i < values.length; i++) {
        int n = values[i];
        if (i + 1 < values.length && n == values[i + 1]) {
            n *= 2;
            score += n;
            i++;
        }
        result[out++] = n;
    }
    return result;
}

boolean canMove() {
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (board[r][c] == 0 || r < 3 && board[r][c] == board[r + 1][c] || c < 3 && board[r][c] == board[r][c + 1]) {
                return true;
            }
        }
    }
    return false;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[wasd]"));
    char d = input.toLowerCase(Locale.ROOT).charAt(0);
    boolean changed = false;
    for (int line = 0; line < 4; line++) {
        int[] values = new int[4];
        for (int i = 0; i < 4; i++) {
            int k = d == 'd' || d == 's' ? 3 - i : i;
            values[i] = d == 'a' || d == 'd' ? board[line][k] : board[k][line];
        }
        int[] result = merge(values);
        changed |= !Arrays.equals(values, result);
        for (int i = 0; i < 4; i++) {
            int k = d == 'd' || d == 's' ? 3 - i : i;
            if (d == 'a' || d == 'd') {
                board[line][k] = result[i];
            } else {
                board[k][line] = result[i];
            }
        }
    }
    if (changed) {
        spawn();
    }
    for (int[] row : board) {
        for (int n : row) {
            if (n >= 2048) {
                outcome = "2048: you win!";
            }
        }
    }
    if (outcome.isEmpty() && !canMove()) {
        outcome = "No legal move: game over.";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    for (int[] row : board) {
        IO.println(Arrays.toString(row));
    }
    IO.println("Score: " + score);
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    verifyExamples();
    IO.println("2048");
    IO.println("Slide all tiles with W/A/S/D. Equal neighbours merge once per move; aim for 2048 before the board locks.");
    IO.println("W/A/S/D slides up/left/down/right. Equal values merge once. /r restarts; /q quits.");
    IO.println("Example: a slides left. A slide that changes nothing does not add another tile.");
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
            IO.println("Invalid move. " + "W/A/S/D slides up/left/down/right. Equal values merge once. /r restarts; /q quits.");
        }
    }
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// verifyExamples(): Start zkouší tvé příklady proti skutečnému pravidlu hry; dodané
/// uložení/obnovení ponechá kolo beze změny.
void verifyExamples() {
    int saved = score;
    try {
        for (var e : learnerChecks()) {
            score = 0;
            if (!Arrays.equals(merge(e.input().clone()), e.expected()) || score != e.score()) {
                throw new IllegalStateException("Merge example has wrong expected result");
            }
        }
    } finally {
        score = saved;
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
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
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
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
