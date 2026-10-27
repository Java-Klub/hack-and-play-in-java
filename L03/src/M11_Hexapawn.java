/// # L03.11 · Hexapawn
///
/// Veď tři pěšce na malé desce a snaž se dojít na protější stranu nebo soupeři znemožnit tah. Pěšci
/// postupují dopředu a berou šikmo, takže i několik políček umožní pasti.
///
/// **Tvoje mise:** Oba hráči dostali stejný cílový řádek. Oprav goalRow tak, aby výsledek závisel na tom,
/// který hráč se ptá.
///
/// **Dnes trénuješ:** Method (metoda), parameter (parametr) a výsledek: jediný pojmenovaný výpočet může
/// odpovídat na různá volání. goalRow(1) a goalRow(2) předají stejnému tělu různé hodnoty who; vrácený
/// řádek dostane pravidlo tahu.
///
/// Použij Run File a jako čtyři samostatné tahy zadej 3 1 2 1; 1 2 2 1; 3 3 2 3; 2 1 3 1, čímž hráč 2
/// dojde dolů.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F13 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int goalRow(int who) {
    if (who == 1) {
        return 0;
    } else {
        return 2;
    }
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F13">
/// Method (metoda) zabalí operaci pod jediné jméno. Parameter (parametry) jsou místní jména informací
/// přijatých při každém call (volání); argument (argumenty) jsou skutečně předané hodnoty. `return`
/// předá jediný výsledek zpět volající části.
///
/// ```java
/// int remaining(int capacity, int used) {
///     return capacity - used;
/// }
///
/// void main() {
///     IO.println(remaining(12, 5));
///     IO.println(remaining(9, 4));
/// }
/// ```
///
/// Předpověz obě vypsané hodnoty: sedmičku a pětku. Čárka odděluje parameter či argument a jejich
/// pořadí rozhoduje, kterou hodnotu jméno dostane. Jména popisují role; informace si sama
/// nezískávají. Tato method počítá bez vypisování a změny připraveného state (stavu programu). Při
/// opravě výpočtu vyzkoušej druhou dvojici argument, aby se za jediný případ nemohla schovat náhodná
/// pevná odpověď. Method se provede až při skutečném call, nikoli samotným zapsáním definice.
///
/// Nyní předpověz remaining(10, 6).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `int goalRow(int who)` — První int popisuje vrácené celé číslo; int who popisuje přijaté číslo
///   hráče.
/// - `who` — Parameter (parametr): místní jméno hodnoty dodané tímto call (voláním), nikoli pevný výběr
///   hráče.
/// - `return 0;` — Nehotové tělo vrací stejný řádek pro každý input (vstup).
/// - `{` — Tělo uvnitř odpovídajících složených závorek se znovu použije při každém call (volání) metody.
///
/// ### Co už obstarává připravená část
///
/// legal a canMove už řeší pohyb pěšců. command volá goalRow se současným hráčem a ověřuje dosažení
/// vráceného řádku; display a main dodávají desku a input (vstup) jednotlivých tahů.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Porovnej dvě volání: goalRow(1) a goalRow(2).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Return value (vrácená hodnota) musí záviset na parametru volání, ne na pevném globálním hráči.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Při who == 1 vrať 0; jinak vrať 2.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
int player;
String outcome;

void reset() {
    board = new int[][]{{2, 2, 2}, {0, 0, 0}, {1, 1, 1}};
    player = 1;
    outcome = "";
}

boolean legal(int r, int c, int nr, int nc, int who) {
    if (r < 0 || r > 2 || c < 0 || c > 2 || nr < 0 || nr > 2 || nc < 0 || nc > 2) {
        return false;
    }
    int direction = who == 1 ? -1 : 1;
    return board[r][c] == who && nr == r + direction && (nc == c && board[nr][nc] == 0 || Math.abs(nc - c) == 1 && board[nr][nc] == 3 - who);
}

boolean canMove(int who) {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            for (int nc = 0; nc < 3; nc++) {
                if (legal(r, c, r + (who == 1 ? -1 : 1), nc, who)) {
                    return true;
                }
            }
        }
    }
    return false;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 4);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    int nr = Integer.parseInt(p[2]) - 1;
    int nc = Integer.parseInt(p[3]) - 1;
    require(legal(r, c, nr, nc, player));
    board[nr][nc] = player;
    board[r][c] = 0;
    if (nr == goalRow(player) || !canMove(3 - player)) {
        outcome = "Player " + player + " wins";
    } else {
        player = 3 - player;
    }
}

void display() {
    IO.println("Player " + player + "; columns 1-3, 1 moves up, 2 moves down");
    for (int r = 0; r < 3; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Hexapawn");
    IO.println("Enter fromRow fromColumn toRow toColumn (1-3). /r restarts; /q quits.");
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
            IO.println("Invalid move or command. Enter fromRow fromColumn toRow toColumn (1-3). /r restarts; /q quits.");
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek; readln čeká na jeden
///   řádek textu.
// </editor-fold>

