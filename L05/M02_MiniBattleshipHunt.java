/// # L05.02 · MiniBattleshipHunt
///
/// Lov dvě skryté lodě zadáváním souřadnic. Zásah ti pomůže víc, když se můžeš spolehnout na oznámené
/// délky lodí.
///
/// **Tvoje mise:** Jedna loď při přípravě přijde o poslední políčko. Oprav layout(int n), aby se celý
/// vybraný seznam souřadnic přenesl na desku.
///
/// **Dnes trénuješ:** Arrays (pole): vnořená pole uchovávají několik rozestavení i jejich dvojice
/// souřadnic. Rozsah průchodu odvoď od velikosti vybraných dat.
///
/// Spusť program, první střelu zadej jako 1 1 a potvrď Enter, potom zkoušej další dvojice řádku
/// a sloupce.
void layout(int n) {
    board = new int[6][6];
    int[][][] maps = {{{0, 0}, {0, 1}, {3, 2}, {3, 3}, {3, 4}}, {{1, 4}, {2, 4}, {5, 0}, {5, 1}, {5, 2}}, {{4, 0}, {4, 1}, {0, 5}, {1, 5}, {2, 5}}};
    for (int i = 0; i < 5; i++) {
        board[maps[n][i][0]][maps[n][i][1]] = i < 2 ? 1 : 2;
    }
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
/// - `int[][][] maps` — Tři úrovně array (pole) vybírají rozestavení, dvojici souřadnic a jednu
///   souřadnici.
/// - `maps[n][i][0]` — Čte řádkovou souřadnici pozice i v rozestavení n.
/// - `i++` — Po každém kroku loop (opakování) přejde k další dvojici souřadnic.
/// - `i < 2 ? 1 : 2` — Conditional expression (podmíněný výraz) označí počáteční dvojice jako loď 1 a
///   ostatní jako loď 2.
///
/// ### Co už obstarává připravená část
///
/// reset() vybere připravené rozestavení a smaže zásahy. command() zapíše střelu a sunk() hledá na
/// vzniklé desce dosud nezasažené části dané lodě.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Čtení nested data (vnořených dat)
///
/// `maps` obsahuje rozmístění; `maps[n]` vybere jedno. To obsahuje dvojice souřadnic, takže
/// `maps[n][i]` vybere dvojici i. Poslední `[0]` a `[1]` čtou řádek a sloupec. `maps.length` počítá
/// rozmístění, zatímco `maps[n].length` počítá dvojice ve vybraném rozmístění. Před volbou meze
/// předpověz význam každé délky. Hodnoty dvojice jsou vnitřní coordinate (souřadnice) od nuly.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// maps[n] je vybraný seznam pozic.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Každé maps[n][i] obsahuje řádek a sloupec; i není souřadnice desky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Opakuj při i < maps[n].length; ponech dodané přiřazení čísla lodi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
boolean[][] shots;
int attempts;
String outcome;
String message;

/// reset(): Obnovuje počáteční kolo Mini Battleship Hunt a maže stav patřící starému kolu.
void reset() {
    layout(new Random().nextInt(3));
    shots = new boolean[6][6];
    attempts = 0;
    outcome = message = "";
}

/// sunk(): Hledá dosud nezasažené pole požadované lodě.
boolean sunk(int ship) {
    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            if (board[r][c] == ship && !shots[r][c]) {
                return false;
            }
        }
    }
    return true;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(r >= 0 && r < 6 && c >= 0 && c < 6 && !shots[r][c]);
    shots[r][c] = true;
    attempts++;
    int ship = board[r][c];
    message = ship == 0 ? "Miss" : sunk(ship) ? "Sunk ship " + ship : "Hit";
    if (sunk(1) && sunk(2)) {
        outcome = "Both ships sunk in " + attempts + " shots!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println(message + "; shots " + attempts + "; two ships of lengths2 and3. Columns1-6.");
    for (int r = 0; r < 6; r++) {
        String s = (r + 1) + " ";
        for (int c = 0; c < 6; c++) {
            s += !shots[r][c] ? "? " : board[r][c] == 0 ? "~ " : "X ";
        }
        IO.println(s);
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Mini Battleship Hunt");
    IO.println("Sink both hidden ships: one has two cells, the other three. Only fresh shots count.");
    IO.println("Enter row column (1-6). Repeated shots do not count. Sink both ships. /r restarts; /q quits.");
    IO.println("Example: 1 1 fires at row 1, column 1. X is a hit; ~ is a miss.");
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
            IO.println("Invalid move. " + "Enter row column (1-6). Repeated shots do not count. Sink both ships. /r restarts; /q quits.");
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
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
