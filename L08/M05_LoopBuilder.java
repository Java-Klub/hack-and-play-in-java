/// # L08.05 · LoopBuilder
///
/// Přepínej čáry a vytvoř jedinou uzavřenou smyčku kolem očíslovaných políček. Čísla ji omezují, ale její
/// rohy se také musí skutečně spojovat.
///
/// **Tvoje mise:** Svislé čáry hlásí špatný koncový bod, takže se viditelná smyčka považuje za
/// rozpojenou. Oprav svislé mapování uvnitř vertices(int edge).
///
/// **Dnes trénuješ:** Grid neighbours (sousedé v mřížce): zobrazené hrany spojují vrcholy v samostatné
/// síti. Převeď sousedství řádků a sloupců na ploché indexy podle šířky sítě vrcholů.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L08/M05_LoopBuilder.java`
/// - Kontrola této mise: `java L08/Check.java M05_LoopBuilder.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše a zadáním 1 přepni první očíslovanou hranu, potom
/// dalšími hranami sestav jedinou uzavřenou smyčku. Po opravě: ulož soubor, spusť uvedenou kontrolu
/// a zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F18 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
int[] vertices(int edge) {
    if (edge < 12) {
        int r = edge / 3;
        int c = edge % 3;
        return new int[]{r * 4 + c, r * 4 + c + 1};
    }
    int n = edge - 12;
    int r = n / 4;
    int c = n % 4;
    return new int[]{r * 4 + c, r * 4 + c + 1};
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F18">
/// ### Grids: nejdřív řádek, potom sloupec
///
/// V array of rows (poli řádků) první index (pozice) vybírá řádek a druhý pozici v něm. Řádky mohou
/// mít různé délky. Neighbour (soused) je posun od buňky, nikoli další absolutní pozice. Před
/// kontrolou existence kandidáta urči povolené směry.
///
/// ```java
/// int[][] mosaic = {{3, 6, 9}, {2, 4, 8}};
/// int row = 0;
/// int column = 1;
/// IO.println(mosaic[row][column + 1]);
/// ```
///
/// Výstup je devět: řádek nula a sloupec dva vyberou tento element (prvek). Pohyb dolů mění row;
/// doprava mění column. Který z těchto pohybů z pravé horní cell (buňky) zůstane uvnitř? U úhlopříčky
/// se mění obě souřadnice; bounds (meze) musíš ověřit před přístupem ke kandidátovi.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `if (edge < 12)` — Odděluje čísla vodorovných hran od svislých.
/// - `edge - 12` — Převede číslo svislé hrany na místní číslování od nuly.
/// - `r * 4 + c` — Převede řádek a sloupec vrcholu na jeden index (pozici) v síti široké čtyři vrcholy.
/// - `new int[]` — Vrací oba indexy koncových bodů společně jako malé array (pole).
///
/// ### Co už obstarává připravená část
///
/// display() čísluje vodorovné a svislé hrany. oneLoop() z dvojic vrcholů vrácených vertices() kontroluje
/// počet spojení i souvislost; čísla uvnitř políček se kontrolují zvlášť.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Buňky a jejich rohy mají různé grid (mřížky)
///
/// Řada dvou sousedních buněk má napříč tři corner vertex (rohové vrcholy). Cell grid (mřížka buněk)
/// proto potřebuje o jeden řádek a sloupec vrcholů více. V jiném vertex grid (mřížce vrcholů) šířky
/// tři leží index (pozice) jedna nad čtyřkou: pohyb dolů zachová sloupec a přeskočí tři pozice.
/// Vodorovná hrana mění sloupec, svislá řádek. Před předpovědí konců očísluj vrcholy odděleně od
/// čísel uvnitř buněk.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Řádky polí mají šířku 3, ale řádky vrcholů šířku 4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Pohyb o řádek vrcholů dolů přidá k lineárnímu indexu 4.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Pro svislou hranu vrať r*4+c a (r+1)*4+c.
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
