/// # L06.11 · PegSolitaire
///
/// **Prověř rozhodčího kolíkové hry.** Hra už funguje. Tentokrát vytváříš příklady, které usvědčí
/// falešné pravidlo skákání, nikoli opravu hry.
///
/// **Úkol:** Rozšiř `learnerChecks` níže. Ke každému zvolenému skoku zapiš začátek, cíl a
/// očekávání `true` pro povolený nebo `false` pro zakázaný skok. Všechny příklady se posuzují na
/// stejné úvodní desce, každý samostatně. V těchto datech počítej řádky i sloupce od nuly; při
/// hraní se zadávají od jedničky.
///
/// **Výsledek:** Příklady přijmou skutečné pravidlo, ale odhalí skok přes prázdný prostředek,
/// diagonální skok i pravidlo povolující všechno. Samotné dohrání hlavolamu tuto misi nesplní.

record JumpExample(int row, int column, int toRow, int toColumn, boolean expected) {
}

JumpExample[] learnerChecks() {
    return new JumpExample[]{new JumpExample(0, 1, 2, 1, true)};
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
/// - `record JumpExample` — Pojmenovaná hodnota ukládá navržený skok i očekávanou povolenost.
/// - `int toRow, int toColumn` — Tyto components (složky) popisují cíl odděleně od startovních souřadnic.
/// - `boolean expected` — true znamená, že se příklad musí přijmout; false znamená, že se musí odmítnout.
/// - `new JumpExample(0, 1, 2, 1, true)` — Dodaný příklad je jeden povolený skok na úvodní desce; tyto
///   souřadnice začínají od nuly.
///
/// ### Co už obstarává připravená část
///
/// legal() už kontroluje meze, přímou vzdálenost, startovní i prostřední kolík a prázdný cíl.
/// Kontrola hry na tvých příkladech zkouší toto pravidlo i záměrně chybná pravidla skoku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### I povolení je výsledek
///
/// Každý JumpExample se ptá na tutéž pevnou počáteční desku, nepokračuje po tahu předchozího
/// příkladu. `expected` znamená povolení, ne úspěch při vyřešení hry. Platný tah potřebuje kolík na
/// začátku, přeskakovaný kolík mezi konci a prázdný cíl v přímém směru. Z této pozice předpověz
/// přijatý i odmítnutý požadavek. Kterou část contract (smlouvy) by přehlíželo pravidlo přijímající
/// každý požadavek?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Jeden platný skok prokazuje možnost true, nikoli odmítání chybných tahů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Na startu chybí u (2,0)->(2,2) prostřední kolík; (0,1)->(1,2) je diagonála.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přidej tyto false případy a souřadnici mimo plochu; ponech platný (0,1)->(2,1).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Připrav skokovou hádanku s jedním platným a dvěma téměř platnými tahy: změň jen prostřední kámen,
/// cíl či směr. Skryj očekávané boolean (logické výsledky) a druhá dvojice je před spuštěním předpoví.
/// Každou těsnou neshodu zdůvodni zvlášť.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
String outcome;

/// reset(): Obnovuje počáteční kolo Peg Solitaire a maže stav patřící starému kolu.
void reset() {
    board = new int[5][5];
    outcome = "";
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            board[r][c] = (r > 0 && r < 4 || c > 0 && c < 4) ? 0 : -1;
        }
    }
    board[2][0] = board[0][1] = board[1][1] = 1;
}

/// legal(): Kontroluje hranice, kolmou vzdálenost dva, startovní kolík, prázdný cíl a prostřední
/// kolík.
boolean legal(int r, int c, int nr, int nc) {
    return r >= 0 && r < 5 && c >= 0 && c < 5 && nr >= 0 && nr < 5 && nc >= 0 && nc < 5 && Math.abs(nr - r) + Math.abs(nc - c) == 2 && (r == nr || c == nc) && board[r][c] == 1 && board[nr][nc] == 0 && board[(r + nr) / 2][(c + nc) / 2] == 1;
}

int pegs() {
    int n = 0;
    for (int[] row : board) {
        for (int v : row) {
            if (v == 1) {
                n++;
            }
        }
    }
    return n;
}

boolean canJump() {
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            for (int[] d : new int[][]{{2, 0}, {-2, 0}, {0, 2}, {0, -2}}) {
                if (legal(r, c, r + d[0], c + d[1])) {
                    return true;
                }
            }
        }
    }
    return false;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    int nr = Integer.parseInt(p[2]) - 1;
    int nc = Integer.parseInt(p[3]) - 1;
    require(legal(r, c, nr, nc));
    board[r][c] = board[(r + nr) / 2][(c + nc) / 2] = 0;
    board[nr][nc] = 1;
    if (pegs() == 1) {
        outcome = "One peg: you win!";
    } else {
        if (!canJump()) {
            outcome = "No jumps left: you lose.";
        }
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Columns 1-5; # peg, . empty, space outside. Pegs: " + pegs());
    for (int r = 0; r < 5; r++) {
        String s = (r + 1) + " ";
        for (int v : board[r]) {
            s += v < 0 ? "  " : v == 1 ? "# " : ". ";
        }
        IO.println(s);
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    verifyExamples();
    IO.println("Peg Solitaire");
    IO.println("Jump a peg over one adjacent peg into an empty cell. The jumped peg disappears; finish with one peg.");
    IO.println("Enter fromRow fromCol toRow toCol (1-5). A short authored two-jump cross puzzle. /r restarts; /q quits.");
    IO.println("Example: 1 2 3 2 jumps from row 1, column 2 to row 3, column 2.");
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
            IO.println("Invalid move. " + "Enter fromRow fromCol toRow toCol (1-5). A short authored two-jump cross puzzle. /r restarts; /q quits.");
        }
    }
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// verifyExamples(): Start ověřuje tvé příklady skutečnými neměnícími dotazy; herní stav se nemění.
void verifyExamples() {
    for (var e : learnerChecks()) {
        if (legal(e.row(), e.column(), e.toRow(), e.toColumn()) != e.expected()) {
            throw new IllegalStateException("Jump (" + e.row() + "," + e.column() + ")->("
                    + e.toRow() + "," + e.toColumn() + "): expected legal=" + e.expected()
                    + ", actual=" + legal(e.row(), e.column(), e.toRow(), e.toColumn()));
        }
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
/// - `/` — Dělí čísla. Dělení dvou int hodnot zahodí desetinnou část.
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
/// - `record` — Malý pojmenovaný balíček hodnot. Component accessor (přístupová metoda složky) má jméno
///   složky a závorky ().
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
