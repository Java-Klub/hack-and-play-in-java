/// # L04.04 · SameGame
///
/// Odstraňuj dotýkající se bloky stejné barvy a sleduj, jak ostatní spadnou a prázdné sloupce se zavřou.
/// Větší skupiny dávají více bodů, ale lákavé odstranění může izolovat bloky potřebné později.
///
/// **Tvoje mise:** Starter (výchozí program) může oznámit, že nezbývají skupiny, i když se stejné bloky
/// dotýkají. Oprav podmínku práce v group tak, aby posbírala všechna dosažitelná shodná políčka místo
/// zastavení po prvním výsledku.
///
/// **Dnes trénuješ:** While do vyčerpání práce: čekající queue a nasbíraný result odpovídají na různé
/// otázky. Nalezení jednoho políčka neznamená, že už žádná políčka nečekají na prozkoumání.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L04/M04_SameGame.java`
/// - Kontrola této mise: `java L04/Check.java M04_SameGame.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a navzdory předčasné zprávě „No groups remain“
/// najdi dvě políčka stejné barvy, která sdílejí hranu; po opravě zadej řádek a sloupec některého
/// políčka z této skupiny. Po opravě: ulož soubor, spusť uvedenou kontrolu a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F10 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
List<int[]> group(int row, int col) {
    var result = new ArrayList<int[]>();
    if (row < 0 || row >= 5 || col < 0 || col >= 5 || board[row][col] == 0) {
        return result;
    }
    boolean[][] seen = new boolean[5][5];
    var queue = new ArrayDeque<int[]>();
    queue.add(new int[]{row, col});
    seen[row][col] = true;
    while (!queue.isEmpty() && result.isEmpty()) {
        int[] p = queue.remove();
        result.add(p);
        for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int r = p[0] + d[0];
            int c = p[1] + d[1];
            if (r >= 0 && r < 5 && c >= 0 && c < 5 && !seen[r][c] && board[r][c] == board[row][col]) {
                seen[r][c] = true;
                queue.add(new int[]{r, c});
            }
        }
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F10">
/// `while` ověřuje condition (podmínku) před každým průchodem včetně prvního. Je-li hned false
/// (nepravda), tělo proběhne nula krát. Něco musí postupovat k ukončení nebo záměrně čekat na nový
/// input (vstup).
///
/// Tyto ukázkové statement (příkazy) patří dovnitř zavolané method (metody).
///
/// ```java
/// int energy = 5;
/// while (energy > 0) {
///     IO.println(energy);
///     energy -= 2;
/// }
/// ```
///
/// Předpověz vypsaná čísla a konečnou uloženou hodnotu. výstup v terminálu (výstup) je `5`, `3`,
/// `1`; potom je energy `-1`, takže další condition neplatí. Podmínka se neověřuje uprostřed těla.
/// `energy -= 2` odečte dvojku a uloží výsledek; bez postupu se loop opakuje donekonečna. `return`
/// ukončí method (metodu); `break` ukončí loop (smyčku). Sleduj condition a měnící se hodnoty
/// odděleně, zejména pokud prohledávaná data mohou přibývat. Samotná přítomnost while nezaručuje,
/// že program někdy skončí; důvod ukončení musí vyplývat z prováděných kroků.
///
/// Nyní předpověz trace (stopu průběhu), pokud energy začíná na čtyřech.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `while (!queue.isEmpty() && result.isEmpty())` — ! znamená „ne“; queue.isEmpty() zjišťuje prázdnost
///   čekající práce, zatímco result.isEmpty() zjišťuje, zda už bylo něco nasbíráno.
/// - `int[] p = queue.remove();` — Vyjmi další čekající dvojici souřadnic z queue. Zápis dvojice je
///   připravený.
/// - `result.add(p);` — Ulož navštívené políčko do výsledku, který hra později odstraní z desky.
/// - `seen[r][c] = true;` — Označ souseda před zařazením, aby jiná cesta nemohla totéž políčko zařadit
///   znovu.
/// - `queue.add(new int[]{r, c});` — Připoj jednu nově objevenou dvojici souřadnic jako práci pro
///   pozdější průchod.
/// - `return result;` — Po skončení prohledávání vrať všechna nasbíraná políčka společně.
///
/// ### Co už obstarává připravená část
///
/// Úvodní kontrola odmítá pozice mimo desku a prázdná políčka. Připravené prohlížení sousedů kontroluje
/// čtyři směry, stejnou barvu a značky seen. command odstraňuje skupiny alespoň dvou bloků, collapse
/// zajišťuje padání a reset ověřuje možnost pokračovat; i tato kontrola volá tvoji group.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Odděl tři připravené role: queue uchovává objevená políčka čekající na návštěvu; result již
/// nasbíraná políčka; seen pamatuje už zařazená políčka, aby je dvě cesty nepřidaly dvakrát.
/// queue.remove() vyjme čekající dvojici souřadnic, zatímco result.add přidá toto políčko do
/// odpovědi. Odpověď obsahující jedno políčko neznamená, že čekající práce je prázdná. Počáteční
/// zpráva „No groups remain“ pochází z herního hledání skupin před jakýmkoli tahem; je to záměrný
/// projev chyby, nikoli problém console input (vstupu do konzole). Oprav source (zdrojový text) a
/// spusť jej znovu místo pokusu odstranit chybu pouhým restartem kola.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// result se po první zpracované buňce stane neprázdným.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// To neznamená, že queue (fronta) už skončila.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// While condition (podmínka) má být pouze !queue.isEmpty().
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] board;
int score;
String outcome;

void reset() {
    board = new int[5][5];
    var random = new Random();
    for (int[] row : board) {
        for (int c = 0; c < 5; c++) {
            row[c] = random.nextInt(1, 4);
        }
    }
    score = 0;
    outcome = "";
    assess();
}

boolean hasMove() {
    for (int r = 0; r < 5; r++) {
        for (int c = 0; c < 5; c++) {
            if (group(r, c).size() >= 2) {
                return true;
            }
        }
    }
    return false;
}

void assess() {
    boolean empty = true;
    for (int[] row : board) {
        for (int n : row) {
            empty &= n == 0;
        }
    }
    if (empty) {
        outcome = "Board clear: you win!";
    } else {
        if (!hasMove()) {
            outcome = "No groups remain. Final score: " + score;
        }
    }
}

void collapse() {
    int[][] next = new int[5][5];
    int writeColumn = 0;
    for (int c = 0; c < 5; c++) {
        int writeRow = 4;
        for (int r = 4; r >= 0; r--) {
            if (board[r][c] != 0) {
                next[writeRow--][writeColumn] = board[r][c];
            }
        }
        if (writeRow != 4) {
            writeColumn++;
        }
    }
    board = next;
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 2);
    var cells = group(Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]) - 1);
    require(cells.size() >= 2);
    for (int[] cell : cells) {
        board[cell[0]][cell[1]] = 0;
    }
    score += cells.size() * (cells.size() - 1);
    collapse();
    assess();
}

void display() {
    IO.println("Colours1/2/3, 0 empty; columns1-5. Score " + score);
    for (int r = 0; r < 5; r++) {
        IO.println((r + 1) + " " + Arrays.toString(board[r]));
    }
}

void main() {
    reset();
    IO.println("Same Game");
    IO.println("Enter a row column (1-5) in a group of at least two equal colours. Blocks fall and columns close. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Enter a row column (1-5) in a group of at least two equal colours. Blocks fall and columns close. /r restarts; /q quits.");
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
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

