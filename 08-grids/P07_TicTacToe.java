/// # 08.07 · TicTacToe
///
/// Zahraj si s kamarádem piškvorky výběrem očíslovaných prázdných políček. Snaž se vytvořit řadu tří a
/// současně blokovat soupeřovu řadu.
///
/// **Tvoje mise · Sestav strážce tahů:** Všechna pole jsou zamčená. Doplň rozhodčího mayPlay: odmítni
/// ukončenou hru, čísla
/// mimo 1..9 a obsazená pole. Platné prázdné pole vrátí true. Připravené účinky play zachovej;
/// odmítnutý tah nesmí změnit desku ani hráče.
///
/// **Dnes trénuješ:** Guard clause (ochranná podmínka): pokud je akce zakázaná, vrať se včas před změnou
/// stavu. Ověř poslední platné políčko, opakované políčko a číslo těsně mimo rozsah.
///
/// Spusť program, vyber políčko 9 a potom jej zkus znovu; porovnej přijaté prázdné políčko s
/// odmítnutým obsazeným.
boolean mayPlay(int cell) {
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Provedení přijatého tahu · připravené">
boolean play(int cell) {
    if (!mayPlay(cell)) {
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
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F08">
/// Guard (ochranná podmínka) odmítne požadavek před chráněnou prací. Nejdřív přečti odmítané případy
/// a potom se ptej, co zůstává povolené. Short-circuit (zkrácené vyhodnocení) je důležité: u `A || B`
/// Java přeskočí B, pokud A už platí.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Předpověz výpis a vrácenou hodnotu pro `-1`, `3` a `4`. Jen trojka dojde k výpisu. Odmítnutí musí
/// nastat před jakoukoli state change (změnou stavu), která se nemá provést. Přístup na desku sám
/// může potřebovat kontrolu rozsahu, než jej lze bezpečně vyhodnotit. `return false` ukončí toto call
/// (volání metody); automaticky nevrací zpět práci již provedenou předtím.
///
/// `useSlot(3)` předá trojku do `slot`; `boolean` před názvem říká, že výsledkem je true/false. Nyní
/// předpověz slot nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Povolení před účinky
///
/// mayPlay vrací boolean a neumísťuje značku. winner je '.' během hry; board používá '.' pro prázdné
/// pole. Hráčova pole 1..9 se přes cell - 1 mění na array index (index pole) 0..8. play už používá
/// tvé rozhodnutí, potom vloží značku, započítá tah, ověří řadu a přepne hráče. Dnes píšeš povolovací
/// pravidlo, nikoli pozdější účinky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Průchod hodnotami · zkus předpovědět">
/// Guard (ochranu) rozděl do kroků. Nejprve odmítni ukončenou hru nebo číslo mimo 1..9; teprve potom
/// smí cell - 1 indexovat array (pole). Dál odmítni obsazené pole. True je povolení, nikoli provedený
/// tah: play vykoná připravené účinky až po true z mayPlay. Dostane se cell 10 ve správné verzi k
/// čtení desky?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Nahraď X a O vlastními dvěma jednoznakovými značkami hráčů. Ponech '.' pro prázdné pole a 'D' pro
/// remízu. Slaď počátečního hráče, reset a přepínání hráčů a odehraj celé kolo s kamarádem. Znovu
/// zkus obsazené pole a vítěznou řadu: vlastní značky nesmějí obejít tvého rozhodčího.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odděl existenci pole od jeho obsazenosti.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Pro cell mimo 1..9 vrať false ještě před indexováním.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Nejdřív odmítni winner != '.' nebo neplatný rozsah; potom board[cell - 1] != '.'; jinak vrať true.
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
    boolean showState = true;
    while (true) {
        if (showState) {
            for (int row = 0; row < 3; row++) {
                IO.println(cell(row * 3) + " | " + cell(row * 3 + 1) + " | " + cell(row * 3 + 2));
            }
            if (winner != '.') {
                IO.println(winner == 'D' ? "Draw!" : winner + " wins!");
            }
            showState = false;
        }
        String input = IO.readln(winner == '.' ? player + ": cell 1-9, r restart, q quit: " : "Enter or /r: new round; /q: quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim();
        if ((input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r") || (winner != '.' && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (winner != '.') {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            if (play(Integer.parseInt(input))) {
                showState = true;
            } else {
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
