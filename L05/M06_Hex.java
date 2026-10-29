/// # L05.06 · Hex
///
/// Postav souvislou cestu své barvy mezi protilehlými okraji. Šestiúhelníky propojí i cikcak, které by na
/// čtvercové síti nefungovalo.
///
/// **Tvoje mise:** Některá skutečná spojení šestiúhelníků nevyhrávají. Oprav sousední směry uvnitř
/// connected(int who) a zachovej připravené prohledávání.
///
/// **Dnes trénuješ:** Traversal and search (průchod a hledání): hledání cesty závisí na povolených
/// sousedních pozicích. Kandidátní graph (síť spojení) je zde šestiúhelníková deska.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L05/M06_Hex.java`
/// - Kontrola této mise: `java L05/Check.java M06_Hex.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a zaber levý horní šestiúhelník zadáním 1 1, potom
/// se s kamarádem střídejte ve volbě řádku a sloupce. Po opravě: ulož soubor, spusť uvedenou
/// kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F17 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean connected(int who) {
    boolean[][] seen = new boolean[5][5];
    var queue = new ArrayDeque<int[]>();
    for (int i = 0; i < 5; i++) {
        int r = who == 1 ? 0 : i;
        int c = who == 1 ? i : 0;
        if (board[r][c] == who) {
            seen[r][c] = true;
            queue.add(new int[]{r, c});
        }
    }
    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    while (!queue.isEmpty()) {
        int[] cell = queue.remove();
        if (who == 1 && cell[0] == 4 || who == 2 && cell[1] == 4) {
            return true;
        }
        for (int[] d : directions) {
            int r = cell[0] + d[0];
            int c = cell[1] + d[1];
            if (r >= 0 && r < 5 && c >= 0 && c < 5 && !seen[r][c] && board[r][c] == who) {
                seen[r][c] = true;
                queue.add(new int[]{r, c});
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F17">
/// ### Traversal: vždy jedna otázka
///
/// Traversal (průchod) navštěvuje hodnoty; search (hledání) může skončit první shodou, count
/// (počítání) musí prohlédnout všechny relevantní hodnoty. Ve for-each loop (cyklu pro každý prvek)
/// dostane jméno před dvojtečkou jednu hodnotu ze sequence (posloupnosti) za ní. Průběžný počet založ
/// mimo opakované tělo, aby dřívější shody přežily.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// Výsledek je dvě, protože pulzy dva a čtyři splňují condition (podmínku). Kdybys potřeboval pouze
/// vědět, zda existuje nějaký silný pulz, kdy už můžeš odpovědět? Jaký počet má mít prázdné array
/// (pole)?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int[][] directions` — Každé malé array (pole) obsahuje posun řádku a sloupce k sousednímu
///   šestiúhelníku.
/// - `queue.remove()` — Vezme z queue (fronty) další objevené políčko k prozkoumání sousedů.
/// - `!seen[r][c]` — Povolí zařazení políčka do hledání, jen pokud ještě nebylo navštíveno.
/// - `cell[0] + d[0]` — Přičte posun řádku ve zvoleném směru k aktuálnímu řádku.
///
/// ### Co už obstarává připravená část
///
/// Queue (fronta) uchovává objevená políčka a seen brání jejich opakovaným návštěvám. command() zabere
/// políčko, zavolá connected() a při dosažení protějšího okraje oznámí výhru.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Hex adjacency (sousedství hexů) a připravená queue (fronta)
///
/// Vypsané řádky se sklánějí doprava. Buňka má šest dotýkajících se sousedů: nakresli dva posunuté
/// řádky a sleduj společné strany místo úhlopříček čtvercové mřížky. Dvojice směru obsahuje posun
/// řádku a potom sloupce. Připravená ArrayDeque je work queue (fronta práce): `add` naplánuje buňku,
/// `remove` vezme další a `seen` brání opakování. Měníš jen definici sousedství. Předpověz spojení
/// přecházející mezi posunutými řádky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vytištěné řádky jsou odsazené; nejde o čtvercová pole.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Kromě čtyř přímých posunů se dotýkají (+1,-1) a (-1,+1).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Přidej oba směry; seen[][] a logiku queue ponech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
int player;
String outcome;

/// reset(): Obnovuje počáteční kolo Hex a maže stav patřící starému kolu.
void reset() {
    board = new int[5][5];
    player = 1;
    outcome = "";
}

/// command(): Zabere prázdný hex, ověří spojení a při nedokončené hře vystřídá hráče.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(r >= 0 && r < 5 && c >= 0 && c < 5 && board[r][c] == 0);
    board[r][c] = player;
    if (connected(player)) {
        outcome = "Player " + player + " wins";
    } else {
        player = 3 - player;
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Player " + player + "; 1 joins top/bottom, 2 joins left/right. Columns 1-5.");
    for (int r = 0; r < 5; r++) {
        IO.println(" ".repeat(r) + (r + 1) + " " + Arrays.toString(board[r]));
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Hex");
    IO.println("Connect your two opposite borders: Player 1 joins top to bottom; Player 2 joins left to right.");
    IO.println("Enter row column (1-5) on the staggered board. /r restarts; /q quits.");
    IO.println("Example: 1 1 claims the upper-left cell. Players alternate and occupied cells cannot be claimed again.");
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
            IO.println("Invalid move. " + "Enter row column (1-5) on the staggered board. /r restarts; /q quits.");
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
