/// # 08.04 · LoopBuilder
///
/// **Spoj čáry se správnými rohy.** Rozhodčí zatím neví, které rohy každá čára spojuje. Kresbu
/// čar lze měnit, ale uzavřený okruh nedokáže správně posoudit.
///
/// **Úkol:** Doplň `vertices` níže: pro číslo hrany vrať čísla jejích dvou koncových rohů.
/// Šestnáct rohů tvoří mřížku 4×4 a číslují se po řádcích zleva doprava od 0 do 15. Hrany 0–11
/// jsou vodorovné, po třech v každém řádku. Hrany 12–23 jsou svislé, po čtyřech mezi sousedními
/// řádky. Číslo mimo 0–23 odmítni.
///
/// **Výsledek:** Hrana 0 má konce `[0, 1]`, hrana 12 má `[0, 4]`; Check ověří všech 24 hran. Při
/// hraní se čísla hran zadávají od 1 do 24, tedy o jednu větší než jejich čísla v kódu.

int[] vertices(int edge) {
    if (edge < 0 || edge >= 24) throw new IllegalArgumentException("Edge: 0..23.");
    if (edge < 12) {
        int row = edge / 3;
        int col = edge % 3;
        return new int[]{row * 4 + col, row * 4 + col + 1};
    }
    int vertical = edge - 12;
    int row = vertical / 4;
    int col = vertical % 4;
    return new int[]{row * 4 + col, (row + 1) * 4 + col};
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F18">
/// ### Coordinates (souřadnice) popisují sousedy
///
/// Grid (mřížka) ukládá nejdřív row (řádek): `board[y][x]`. Rostoucí x vede doprava, rostoucí y na
/// obrazovce dolů. Neighbour (soused) vznikne přičtením malého offset (posunu).
///
/// ```text
///           (0,-1)
/// (-1,0)    HERE    (1,0)
///           (0,1)
/// ```
///
/// ```java
/// int[][] board = {{2, 5, 8}, {1, 4, 7}};
/// int x = 1, y = 0;
/// int nx = x + 0, ny = y + 1;
/// if (ny >= 0 && ny < board.length && nx >= 0 && nx < board[ny].length) {
///     IO.println(board[ny][nx]);
/// }
/// ```
///
/// Přesun vede z 5 na 4, vypíše se tedy 4. Bounds (hranice) kontrolujeme před indexováním: array (pole)
/// za okrajem žádné cell (políčko) nemá. Diagonal (úhlopříčný posun) mění obě coordinates. Corridor
/// (chodba) opakuje stejný offset; neighbourhood (okolí) zkouší několik offset. Jde o různé postupy se
/// stejným modelem.
///
/// Předpověz výsledek offset `(-1,0)` ze stejného začátku. Který offset opustí tento board (hrací
/// plochu)? Než spojíš směry do loop (opakování), zakresli začátek a cíl na papír.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst tuto část
///
/// Ve flat grid (zploštělé mřížce) má vertex (vrchol) na (row,col) index row*4+col. `/` najde row
/// (řádek), `%` remainder (zbytek), tedy column (sloupec). Horizontal edge (vodorovná hrana) 0 spojuje
/// 0→1; vertical edge (svislá hrana) 12 spojuje 0→4. Připravená kontrola okruhu se ptá tvé method
/// (metody) na spojení; endpoints (koncové body) z obrázku neodhadne.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Nejdřív na papíře očísluj šestnáct vertex (vrcholů).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Odděl prvních dvanáct edge (hran) od dalších dvanácti; jejich row width (šířka řádku) je jiná.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Převeď edge na row/column (řádek/sloupec), začátek zplošti šířkou 4 a druhý konec posuň o 1
/// vodorovně nebo o 4 svisle.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Nakresli vlastní uzavřený okruh, odvoď čísla v okolních cell (políčkách) a vyzvi spoluhráče.
/// Potřebuješ hratelnou hádanku, nikoli solver (řešič) ani záruku jediného řešení.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[] edges;
int[] clues = {2, 1, 2, 1, 0, 1, 2, 1, 2};
String outcome;

/// reset(): Obnovuje počáteční kolo Loop Builder a maže stav patřící starému kolu.
void reset() {
    edges = new boolean[24];
    outcome = "";
}

/// oneLoop(): Dodané kontroly stupně dva a souvislosti rozlišují jednu smyčku od větví nebo
/// oddělených smyček.
boolean oneLoop() {
    int[] degree = new int[16];
    var graph = new ArrayList<List<Integer>>();
    for (int i = 0; i < 16; i++) {
        graph.add(new ArrayList<>());
    }
    for (int i = 0; i < 24; i++) {
        if (edges[i]) {
            int[] v = vertices(i);
            degree[v[0]]++;
            degree[v[1]]++;
            graph.get(v[0]).add(v[1]);
            graph.get(v[1]).add(v[0]);
        }
    }
    int start = -1;
    int total = 0;
    for (int i = 0; i < 16; i++) {
        if (degree[i] != 0 && degree[i] != 2) {
            return false;
        }
        if (degree[i] == 2) {
            start = i;
            total++;
        }
    }
    if (start < 0) {
        return false;
    }
    var seen = new HashSet<Integer>();
    var queue = new ArrayDeque<Integer>();
    queue.add(start);
    seen.add(start);
    while (!queue.isEmpty()) {
        for (int v : graph.get(queue.remove())) {
            if (seen.add(v)) {
                queue.add(v);
            }
        }
    }
    return seen.size() == total;
}

boolean solved() {
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            int n = 0;
            for (int e : new int[]{r * 3 + c, (r + 1) * 3 + c, 12 + r * 4 + c, 13 + r * 4 + c}) {
                if (edges[e]) {
                    n++;
                }
            }
            if (n != clues[r * 3 + c]) {
                return false;
            }
        }
    }
    return oneLoop();
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    int edge = Integer.parseInt(input) - 1;
    require(edge >= 0 && edge < 24);
    edges[edge] = !edges[edge];
    if (solved()) {
        outcome = "One complete loop satisfies every clue!";
    }
}

String label(int edge) {
    return edges[edge] ? "##" : String.format("%02d", edge + 1);
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    for (int r = 0; r < 4; r++) {
        String line = "+";
        for (int c = 0; c < 3; c++) {
            line += "--" + label(r * 3 + c) + "--+";
        }
        IO.println(line);
        if (r < 3) {
            line = label(12 + r * 4);
            for (int c = 0; c < 3; c++) {
                line += "  " + clues[r * 3 + c] + "   " + label(13 + r * 4 + c);
            }
            IO.println(line);
        }
    }
    IO.println("## marks chosen edge; cell digits count surrounding edges.");
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Loop Builder");
    IO.println("Choose edges to make exactly one closed loop. Each cell's number counts the chosen edges around it.");
    IO.println("Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
    IO.println("Example: enter 1 to toggle edge 01. ## means chosen; enter the same edge again to remove it.");
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
            IO.println("Invalid move. " + "Enter edge1-24 to toggle it. One closed loop must match all numbered cells. /r restarts; /q quits.");
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+=` — Přičte k původní hodnotě a výsledek uloží zpět pod stejné jméno.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
/// - `-` — Odčítá čísla; před jedním číslem mění jeho znaménko.
/// - `*` — Násobí čísla. V import jako java.awt.* zpřístupňuje jména type (typů) z daného package
///   (balíčku).
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
