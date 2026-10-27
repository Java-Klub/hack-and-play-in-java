/// # L05.04 · Quarto
///
/// Soupeř ti vybírá figurku, kterou musíš položit. Vyhraje čtveřice se společnou vlastností, takže i
/// nenápadná diagonála může být past.
///
/// **Tvoje mise:** Řada vyhraje, ale vítězný sloupec či diagonála zůstane bez povšimnutí. Rozšiř won(),
/// aby prohlédlo všechny druhy čtyřpolíčkových linií.
///
/// **Dnes trénuješ:** Arrays (pole): jednorozměrné pole může představovat čtvercovou desku. Sleduj převod
/// řádku a sloupce na index (pozici) a použij stejný dotaz line().
///
/// Spusť Run File, vyber číslo nepoužité figurky od 0 do 15 a nech druhého hráče zadat její cílové
/// políčko od 1 do 16.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F16 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean won() {
    for (int row = 0; row < 4; row++) {
        if (line(row * 4, row * 4 + 1, row * 4 + 2, row * 4 + 3)) {
            return true;
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F16">
/// ### Arrays: záleží na pozici
///
/// Array (pole) uchovává pevný počet hodnot jednoho element type (typu prvku). Hranaté závorky
/// vybírají pozici: první index (pozice) je nula a `length` je počet. Čtení jednoho element (prvku)
/// nemění ostatní. Použitý index musí existovat; prázdné array nemá první element.
///
/// ```java
/// String[] tools = {"pen", "tape", "chalk"};
/// int chosen = 1;
/// IO.println(tools[chosen]);
/// IO.println(tools.length);
/// ```
///
/// Tyto statement (příkazy) patří dovnitř method (metody). Index jedna vybere `tape`; počet je tři.
/// Co se změní při `chosen` rovném dvěma? Který index už bude první neplatný?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `for (int row = 0; row < 4; row++)` — Loop (opakování) navštíví čísla řádků 0, 1, 2 a 3.
/// - `row * 4` — Najde první pozici řádku v jednorozměrném array (poli) o šestnácti prvcích.
/// - `line(` — Volá připravený dotaz se čtyřmi indexy (pozicemi) na desce.
/// - `return true` — Jakmile najde vítěznou linii, ukončí hledání s výsledkem true (ano).
///
/// ### Co už obstarává připravená část
///
/// line() pomocí bit operations (bitových operací) už pozná společnou vlastnost čtyř obsazených míst.
/// command() řeší výběr a položení figurky, potom se ptá won() na konec kola.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Plochá deska má skrytou šířku řádku
///
/// Flat array (ploché pole) může uchovávat řádky za sebou. V jiném obrázku se třemi sloupci začíná
/// řádek jedna za prvními třemi hodnotami; jeho sloupec dva leží na index (pozici) pět. Pohyb o řádek
/// dolů zachová sloupec a přeskočí šířku řádku. Nakresli index tohoto čtverce. `line(a,b,c,d)` už
/// ověří navrženou skupinu: vybíráš pozice, nerozebíráš vlastnosti figurek. Předpověz sloupec i obě
/// úhlopříčky před rozšířením search (hledání).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nakresli indexy array jako desku 4 × 4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Sloupec má indexy c, c+4, c+8, c+12.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Diagonály jsou 0,5,10,15 a 3,6,9,12.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[] board;
boolean[] used;
int player;
int selected;
int moves;
String outcome;

/// reset(): Obnovuje počáteční kolo Quarto a maže stav patřící starému kolu.
void reset() {
    board = new int[16];
    Arrays.fill(board, -1);
    used = new boolean[16];
    player = 1;
    selected = -1;
    moves = 0;
    outcome = "";
}

/// line(): Dodané bitové masky ověřují společnou dvojhodnotovou vlastnost čtyř dílků.
boolean line(int a, int b, int c, int d) {
    if (board[a] < 0 || board[b] < 0 || board[c] < 0 || board[d] < 0) {
        return false;
    }
    int both = board[a] & board[b] & board[c] & board[d];
    int either = board[a] | board[b] | board[c] | board[d];
    return both != 0 || ((~either) & 15) != 0;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    int n = Integer.parseInt(input);
    if (selected < 0) {
        require(n >= 0 && n < 16 && !used[n]);
        selected = n;
        used[n] = true;
        player = 3 - player;
    } else {
        require(n >= 1 && n <= 16 && board[n - 1] < 0);
        board[n - 1] = selected;
        selected = -1;
        moves++;
        if (won()) {
            outcome = "Player " + player + " wins";
        } else {
            if (moves == 16) {
                outcome = "Draw";
            }
        }
    }
}

String piece(int n) {
    return String.format("%4s", Integer.toBinaryString(n)).replace(' ', '0');
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Player " + player + (selected < 0 ? ": choose a piece for your opponent" : ": place piece " + selected + " (" + piece(selected) + ")"));
    IO.println("Attributes: bits = tall, dark, square, hollow (1=yes, 0=no).");
    for (int r = 0; r < 4; r++) {
        IO.println(Arrays.toString(Arrays.copyOfRange(board, r * 4, r * 4 + 4)));
    }
    String available = "Unused pieces: ";
    for (int i = 0; i < 16; i++) {
        if (!used[i]) {
            available += i + "=" + piece(i) + " ";
        }
    }
    IO.println(available);
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Quarto");
    IO.println("Give a piece to your opponent, then place the piece they gave you. Four in a line sharing any attribute wins.");
    IO.println("Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
    IO.println("Example: choose piece 0; the next player can enter 1 to place it in the first cell. Read the current phase above.");
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
            IO.println("Invalid move. " + "Choose unused piece 0-15, then its receiver places in cell 1-16 (row order). -1 is empty. /r restarts; /q quits.");
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

