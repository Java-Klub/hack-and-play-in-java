/// # L06.01 · ConnectFour
///
/// Pouštěj kameny do sloupců a spoj čtyři dřív než kamarád. Odveta má začít s čistou deskou i novým
/// kolem.
///
///
/// **Tvoje mise:** Nech odvety střídat začínajícího hráče: první kolo začne X, další O, potom znovu X.
/// Nezáleží na posledním tahu ani vítězi. Každá odveta dál vyčistí desku, vítěze a počet tahů. Podle
/// zobrazeného hráče a Moves vyšetři, které údaje patří kolu a které musí přežít několik kol.
///
/// **Dnes se učíš:** State lifetime (životnost stavu): údaj zápasu musí přežít reset kola, zatímco
/// dočasný výpočet a staré údaje kola nemají.
///
/// Začni kolo, zadej 4 a potom r. Porovnej dalšího začínajícího hráče s restartem ještě před prvním
/// kamenem. Současná hra vždy začne X.
void reset() {
    for (char[] row : board) {
        Arrays.fill(row, '.');
    }
    player = 'X';
    winner = '.';
    moves = 0;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F14">
/// ### Scope: které úložiště jméno označuje?
///
/// Field (položka objektu) patří aktuálnímu objektu a může přežít více volání. Local variable (místní
/// proměnná) patří jedinému call (volání) či bloku. Declaration (deklarace) obsahuje type (typ);
/// assignment (přiřazení) do existujícího jména nevytváří další proměnnou. Místní jméno může zastínit
/// stejně napsaný field.
///
/// ```java
/// int volume = 7;
///
/// void whisper() {
///     int volume = 2;
///     IO.println(volume);
/// }
/// ```
///
/// `whisper()` vypíše dvě, zatímco field zůstane sedm: local declaration (místní deklarace) vytvořila
/// jiné úložiště. Předpověz pozdější čtení field; `this.volume` podle potřeby výslovně označí field.
/// Která hodnota má přežít několik volání a která patří pouze dočasnému výpočtu?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Právě aktivní tah a začínající hráč odpovídají na jiné otázky. První se mění po běžném tahu; druhý
/// až při novém kole.
///
/// V jiném kvízu střídajícím týmy A/B zapomene local variable (místní proměnná) z restartu svoji
/// hodnotu po skončení volání. Field (proměnná programu) si předchozího začínajícího pamatuje, ale
/// mazání při každém restartu paměť zničí. Předpověz začátky po kolech s nula, jedním a čtyřmi tahy;
/// délky nesmějí rozhodovat. Sleduj požadavek na nové kolo a změnu běžného tahu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vypiš, co se musí vymazat a co si má pamatovat minulé kolo. Neodvozuj začátek od posledního kamene.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// reset maže kolo a volí player. drop mění player při běžné hře; tato jediná hodnota si proto
/// nepamatuje začátek.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Uchovej zvlášť začínajícího, s počáteční hodnotou dávající při prvním resetu X. Při resetu jej
/// jednou vystřídej, zkopíruj do player a dál maž desku, vítěze a moves.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Vytvoř zápas na dvě vítězství, jehož skóre přežije odvetu. Rozhodni, zda samostatný nový zápas
/// obnoví i pořadí začínajících.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
char[][] board = {
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray(),
".......".toCharArray()
};
char player = 'X';
char winner = '.';
int moves;

int count(int row, int column, int dr, int dc) {
    char mark = board[row][column];
    int total = 0;
    row += dr;
    column += dc;
    while (row >= 0 && row < 6 && column >= 0 && column < 7 && board[row][column] == mark) {
        total++;
        row += dr;
        column += dc;
    }
    return total;
}

boolean winsAt(int row, int column) {
    int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
    for (int[] direction : directions) {
        int dr = direction[0];
        int dc = direction[1];
        if (1 + count(row, column, dr, dc) + count(row, column, -dr, -dc) >= 4) {
            return true;
        }
    }
    return false;
}

/// drop(): Najde nejnižší volné pole vybraného sloupce a ověří výhru či remízu.
boolean drop(int column) {
    if (winner != '.' || column < 1 || column > 7 || board[0][column - 1] != '.') {
        return false;
    }
    column--;
    int row = 5;
    while (board[row][column] != '.') {
        row--;
    }
    board[row][column] = player;
    moves++;
    if (winsAt(row, column)) {
        winner = player;
    } else {
        if (moves == 42) {
            winner = 'D';
        } else {
            player = player == 'X' ? 'O' : 'X';
        }
    }
    return true;
}

/// display(): Vypíše současnou desku a výsledek dokončeného kola.
void display() {
    IO.println("1 2 3 4 5 6 7");
    IO.println("Moves: " + moves);
    for (char[] row : board) {
        String line = "";
        for (char mark : row) {
            line += mark + " ";
        }
        IO.println(line);
    }
    if (winner != '.') {
        IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
    }
}

void main() {
    reset();
    IO.println("CONNECT FOUR: two players; four connected pieces win.");
    IO.println("Drop a disc into a column; it falls to the lowest empty cell. Connect four in any direction.");
    IO.println("Example: 4 drops into the middle column. r or /r restarts; q or /q quits.");
    display();
    while (true) {
        String input = IO.readln(winner == '.' ? player + ": column 1-7, r restart, q quit: " : "Enter: replay, r restart, q quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        if (input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (winner != '.') {
            IO.println("That round is over. Press Enter to play again, or q to quit.");
            continue;
        }
        try {
            if (drop(Integer.parseInt(input))) {
                display();
            } else {
                IO.println("Illegal move. Choose a column with space during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a column number, r or q.");
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
/// - `'` — Jednoduché uvozovky ohraničují jeden character (znak), například značku na desce.
/// - `char` — Type (typ) pro jednu UTF-16 jednotku, často jednoduchý znak desky jako X. Pro text slouží
///   String.
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
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
