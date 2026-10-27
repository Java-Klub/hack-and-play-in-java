/// # L03.04 · TicTacToe
///
/// Zahraj si s kamarádem piškvorky výběrem očíslovaných prázdných políček. Snaž se vytvořit řadu tří a
/// současně blokovat soupeřovu řadu.
///
/// **Tvoje mise:** Prázdné pravé dolní políčko je chybně odmítnuto. Oprav úvodní guard (ochranu) v play
/// tak, aby přijímalo všechna platná políčka a neplatný tah nezměnil desku ani hráče.
///
/// **Dnes trénuješ:** Guard clause (ochranná podmínka): pokud je akce zakázaná, vrať se včas před změnou
/// stavu. Ověř poslední platné políčko, opakované políčko a číslo těsně mimo rozsah.
///
/// Použij Run File, vyber políčko 9 a potom jej zkus znovu; porovnej přijaté prázdné políčko s odmítnutým
/// obsazeným.
/// Po opravě: Save → Test File → zahraj si znovu.
boolean play(int cell) {
    if (winner != '.' || cell < 1 || cell > 9 || board[cell - 1] != '.') {
        return false;
    }
    board[cell - 1] = player;
    moves++;
    if (hasLine(player)) {
        winner = player;
    } else if (moves == 9) {
        winner = 'D';
    } else {
        player = player == 'X' ? 'O' : 'X';
    }
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `||` — Odmítni, pokud platí kterákoli spojená špatná situace; vyhodnocení skončí při nalezení
///   platného důvodu odmítnutí.
/// - `cell >= 9` — Současná horní kontrola odmítá samotnou devítku i větší čísla.
/// - `board[cell - 1]` — Přečti jedno místo připravené desky: lidská čísla políček začínají jedničkou,
///   zatímco místa se počítají od nuly.
/// - `'.'` — Jednoduché uvozovky obsahují jediný znak; tečka je připravená značka prázdného políčka.
/// - `return false;` — Dokonči call (volání) jako odmítnuté před chráněným zápisem do desky.
/// - `board[cell - 1] = player;` — Chráněná akce zapíše značku současného hráče do přijatého políčka.
/// - `moves++;` — Zvětši počet přijatých tahů o jednu; odmítnutý pokus k tomuto řádku nesmí dojít.
///
/// ### Co už obstarává připravená část
///
/// Pozdější tělo rozpoznává řady, remízu a dalšího hráče. hasLine kontroluje připravenou desku; main
/// vypisuje očíslovaná políčka a čte tahy. Opravuješ vstupní bránu před těmito připravenými účinky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda po krocích">
/// ## Když se zasekneš
///
/// 1. Poslední platný input (vstup) je devět, nikoli osm.
/// 2. Range guard (ochrana rozsahu) musí proběhnout před board[cell - 1].
/// 3. Odmítej cell > 9 místo cell >= 9.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
char[] board = ".........".toCharArray();
char player = 'X';
char winner = '.';
int moves;

void reset() {
    Arrays.fill(board, '.');
    player = 'X';
    winner = '.';
    moves = 0;
}

boolean hasLine(char mark) {
    for (int i = 0; i < 3; i++) {
        if (board[i * 3] == mark && board[i * 3 + 1] == mark && board[i * 3 + 2] == mark) {
            return true;
        }
        if (board[i] == mark && board[i + 3] == mark && board[i + 6] == mark) {
            return true;
        }
    }
    return board[0] == mark && board[4] == mark && board[8] == mark
        || board[2] == mark && board[4] == mark && board[6] == mark;
}

String cell(int index) {
    return board[index] == '.' ? Integer.toString(index + 1) : Character.toString(board[index]);
}

void main() {
    reset();
    IO.println("TIC-TAC-TOE: two players, three in a row wins.");
    while (true) {
        for (int row = 0; row < 3; row++) {
            IO.println(cell(row * 3) + " | " + cell(row * 3 + 1) + " | " + cell(row * 3 + 2));
        }
        if (winner != '.') {
            IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
        }
        String input = IO.readln(winner == '.' ? player + ": cell 1-9, r restart, q quit: " : "r replay, q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("q")) {
            return;
        }
        input = input.trim();
        if (input.equalsIgnoreCase("r")) {
            reset();
            continue;
        }
        try {
            if (!play(Integer.parseInt(input))) {
                IO.println("Illegal move. Choose an empty cell during an unfinished round.");
            }
        } catch (NumberFormatException exception) {
            IO.println("Enter a cell number, r or q.");
        }
    }
}

/// Připravené vstupní body: main, reset.
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
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

