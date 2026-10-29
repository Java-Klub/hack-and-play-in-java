/// # L05.05 · Reversi
///
/// Sevři soupeřovy kameny mezi své a otoč je. Hráč smí vynechat tah jen tehdy, když opravdu nemá kam
/// hrát.
///
/// **Tvoje mise:** Už v úvodní pozici lze chybně vynechat tah, protože většina řádků se neprohledává.
/// Oprav hasMove(int who), aby posoudilo celou desku.
///
/// **Dnes trénuješ:** Traversal and search (průchod a hledání): procházej kandidátní pozice, dokud
/// nenajdeš legální tah. Výsledek false (ne) patří až úplnému neúspěšnému hledání.
///
/// Spusť program, jako úvodní tah napiš 3 2 a potvrď Enter, potom nech dalšího hráče zvolit
/// povolený řádek a sloupec.
boolean hasMove(int who) {
    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            if (!captures(r, c, who).isEmpty()) {
                return true;
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F17">
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
/// - `for (int r = 0; r < 1; r++)` — Současný vnější loop (opakování) vybírá prohledávané řádky; jeho mez
///   je důležitá.
/// - `for (int c = 0; c < 6; c++)` — Vnitřní loop (opakování) prochází sloupce aktuálního řádku.
/// - `!captures(r, c, who).isEmpty()` — Výsledek captures() není prázdný, takže zde hráč může legálně
///   položit kámen.
/// - `return false` — Říká, že žádný kandidát neuspěl; jeho umístění za oběma průchody je podstatné.
///
/// ### Co už obstarává připravená část
///
/// captures() už prochází všech osm směrů a vrací kameny, které by tah otočil. command() přes hasMove()
/// posuzuje vynechání; otáčení kamenů ani střídání hráčů nepřepisuj.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vnější loop teď prochází jediný řádek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Úspěšné hledání hned vrací true; neúspěšné musí dokončit všechny řádky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Projdi všechny řádky i sloupce a false vrať až za oběma loops.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board = new int[6][6];
int player;
int passes;
String outcome;

/// reset(): Obnovuje počáteční kolo Reversi a maže stav patřící starému kolu.
void reset() {
    board = new int[6][6];
    board[2][2] = board[3][3] = 2;
    board[2][3] = board[3][2] = 1;
    player = 1;
    passes = 0;
    outcome = "";
}

/// captures(): Projde osm směrů a vrátí soupeřovy kameny uzavřené vlastním kamenem.
List<int[]> captures(int row, int col, int who) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 6 || col < 0 || col >= 6 || board[row][col] != 0) {
        return result;
    }
    for (int dr = -1; dr <= 1; dr++) {
        for (int dc = -1; dc <= 1; dc++) {
            if (dr == 0 && dc == 0) {
                continue;
            }
            var line = new ArrayList<int[]>();
            int r = row + dr;
            int c = col + dc;
            while (r >= 0 && r < 6 && c >= 0 && c < 6 && board[r][c] == 3 - who) {
                line.add(new int[]{r, c});
                r += dr;
                c += dc;
            }
            if (r >= 0 && r < 6 && c >= 0 && c < 6 && board[r][c] == who) {
                result.addAll(line);
            }
        }
    }
    return result;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("pass")) {
        require(!hasMove(player));
        passes++;
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 2);
        int r = Integer.parseInt(p[0]) - 1;
        int c = Integer.parseInt(p[1]) - 1;
        var flips = captures(r, c, player);
        require(!flips.isEmpty());
        board[r][c] = player;
        for (int[] cell : flips) {
            board[cell[0]][cell[1]] = player;
        }
        passes = 0;
    }
    player = 3 - player;
    if (passes == 2) {
        int[] counts = new int[3];
        for (int[] row : board) {
            for (int cell : row) {
                counts[cell]++;
            }
        }
        outcome = counts[1] == counts[2] ? "Draw" : "Player " + (counts[1] > counts[2] ? 1 : 2) + " wins";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Columns 1-6; player " + player + ". 1=X, 2=O, 0=empty");
    for (int r = 0; r < 6; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Reversi");
    IO.println("Place a disc to trap an enemy line between two of yours; trapped discs flip. Most discs wins after both players pass.");
    IO.println("Enter row column (1-6), or pass when blocked. /r restarts; /q quits.");
    IO.println("Example: 2 3 is an opening move for Player 1. Enter pass only when you have no legal move.");
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
            IO.println("Invalid move. " + "Enter row column (1-6), or pass when blocked. /r restarts; /q quits.");
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
