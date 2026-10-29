/// # L08.10 · Slant
///
/// Pokládej šikmé čáry podle čísel v jejich rozích a neuzavři při tom smyčku. Vrácení tahu ti dovolí
/// bezpečně zkusit nejisté lomítko.
///
/// **Tvoje mise:** Vrácení tahu nedokáže obnovit desku, protože uložené řádky se mění se současnými.
/// Oprav snapshot() včetně případu, kdy vracíš vítězný tah.
///
/// **Dnes trénuješ:** Copies and aliasing (kopie a sdílené odkazy): dvojrozměrný snapshot (snímek)
/// potřebuje samostatné řádky i vnější pole. Uložené znaky musí zůstat dřívějším stavem.
///
/// Z hlavní složky obsahující README.md:
/// - Hraní: `java L08/M10_Slant.java`
/// - Kontrola této mise: `java L08/Check.java M10_Slant.java`
///
/// Spusť command (příkaz) pro hraní uvedený výše, zadáním 1 1 / polož první lomítko a potom napiš
/// undo pro kontrolu vrácení právě této úpravy. Po opravě: ulož soubor, spusť uvedenou kontrolu a
/// zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F20 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
char[][] snapshot() {
    return cells.clone();
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F20">
/// ### References: jiné jméno, nebo jiný objekt?
///
/// Array variable (proměnná pole) uchovává reference (odkaz). Assignment (přiřazení) může dát dvěma
/// jménům přístup k jedinému array (poli); zápis přes kterékoli změní sdílený objekt. `clone()` na
/// jednorozměrném int array vytvoří další array se zkopírovanými hodnotami. Původní array i kopie se
/// potom mohou měnit nezávisle.
///
/// ```java
/// int[] wave = {4, 1};
/// int[] shared = wave;
/// int[] saved = wave.clone();
/// wave[1] = 7;
/// IO.println(shared[1]);
/// IO.println(saved[1]);
/// ```
///
/// Výstup je sedm a potom jedna: shared vidí zápis, saved uchová kopii. Po `saved[0] = 8` předpověz
/// `wave[0]` a `saved[0]`. `clone()` kopíruje jednu vrstvu: element (prvky) mřížky jsou reference na
/// řádky, takže kopie vnějšího array řádky dál sdílí. Ptej se, které objekty zůstaly společné.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `char[][]` — Řádky obsahují jednotlivé znaky lomítek, zpětných lomítek nebo prázdných políček.
/// - `snapshot()` — Vrací data desky pro vrácení jednoho tahu; nevytváří screenshot (obrázek obrazovky).
/// - `cells.clone()` — Kopíruje vnější array (pole), ale zpočátku sdílí všechna řádková pole.
///
/// ### Co už obstarává připravená část
///
/// command() před úpravou pořídí snapshot (snímek) a při undo ho obnoví i znovu otevře dokončené kolo.
/// Připravené acyclic() pomocí union-find (spojování skupin) odmítá smyčky; solved() kontroluje i počty v
/// rozích.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// ### Kopie řádků i vnější vrstvy
///
/// Vnější array (pole) mřížky ukládá row reference (odkazy na řádky). Vnější `clone()` proto kopíruje
/// odkazy na stejné řádky. Zde je jiný dvouřádkový příklad s nezávislými řádky:
///
/// ```java
/// int[][] original = {{1}, {2}};
/// int[][] copied = {original[0].clone(), original[1].clone()};
/// copied[0][0] = 9;
/// IO.println(original[0][0]);
/// ```
///
/// Výstup zůstane jedna, protože změněný řádek patří copied. Předpověz výsledek, kdyby jeho první
/// řádek byl jen `original[0]`. Tvoje deska má více řádků: podle skutečného počtu opakuj operaci
/// kopírování. Každý row clone (klon řádku) zachová jeho aktuální element (prvky). Vysvětli, která
/// vrstva by zůstala sdílená při kopii pouze vnějšího array.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Vnější clone vytvoří novou array referencí, které ale stále ukazují na staré řádky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Změna znaku pak mění obě desky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vytvoř novou vnější char[][] a naklonuj každý cells[row].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
char[][] cells;
int[][] clues;
String outcome;

/// reset(): Obnovuje počáteční kolo Slant a maže stav patřící starému kolu.
void reset() {
    cells = new char[3][3];
    previous = null;
    for (char[] row : cells) {
        Arrays.fill(row, '.');
    }
    clues = new int[][]{{0, 1, 1, 1}, {1, 2, 2, 1}, {1, 2, 2, 1}, {1, 1, 1, 0}};
    outcome = "";
}

int root(int[] parent, int n) {
    while (parent[n] != n) {
        n = parent[n];
    }
    return n;
}

/// acyclic(): Dodané union-find spojuje komponenty konců a odmítne hranu s již propojenými konci.
boolean acyclic() {
    int[] parent = new int[16];
    for (int i = 0; i < 16; i++) {
        parent[i] = i;
    }
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            if (cells[r][c] != '.') {
                int a = r * 4 + c + (cells[r][c] == '/' ? 1 : 0);
                int b = (r + 1) * 4 + c + (cells[r][c] == '/' ? 0 : 1);
                int x = root(parent, a);
                int y = root(parent, b);
                if (x == y) {
                    return false;
                }
                parent[x] = y;
            }
        }
    }
    return true;
}

boolean solved() {
    int[][] counts = new int[4][4];
    for (int r = 0; r < 3; r++) {
        for (int c = 0; c < 3; c++) {
            if (cells[r][c] == '.') {
                return false;
            }
            if (cells[r][c] == '/') {
                counts[r][c + 1]++;
                counts[r + 1][c]++;
            } else {
                counts[r][c]++;
                counts[r + 1][c + 1]++;
            }
        }
    }
    return Arrays.deepEquals(counts, clues) && acyclic();
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    if (input.equalsIgnoreCase("undo")) {
        require(previous != null);
        cells = previous;
        previous = null;
        outcome = "";
        return;
    }
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length == 3 && (p[2].equals("/") || p[2].equals("\\") || p[2].equals(".")));
    int r = Integer.parseInt(p[0]) - 1;
    int c = Integer.parseInt(p[1]) - 1;
    require(r >= 0 && r < 3 && c >= 0 && c < 3);
    previous = snapshot();
    cells[r][c] = p[2].charAt(0);
    if (solved()) {
        outcome = "Counts satisfied with no closed loops!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("undo restores the previous diagonal change.");
    IO.println("Vertex counts (4x4):");
    for (int[] row : clues) {
        IO.println(Arrays.toString(row));
    }
    IO.println("Cell diagonals (3x3), columns1-3:");
    for (int r = 0; r < 3; r++) {
        IO.println((r + 1) + " " + Arrays.toString(cells[r]));
    }
    IO.println(acyclic() ? "No closed loop" : "Closed loop: revise a diagonal");
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Slant");
    IO.println("Fill the cells with diagonals matching every corner count, without making a closed loop.");
    IO.println("Enter row col / or row col backslash; . clears a cell. Match vertex counts with no loops. /r restarts; /q quits.");
    IO.println("Example: 1 1 / changes the upper-left cell. undo reverses one change, even after completing the puzzle.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter: new round; undo: previous change; /r: restart; /q: quit: ");
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
        if (!outcome.isEmpty() && !input.equalsIgnoreCase("undo")) {
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
            IO.println("Invalid move. " + "Enter row col / or row col backslash; . clears a cell. Match vertex counts with no loops. /r restarts; /q quits.");
        }
    }
}

/// require(): Odmítá neplatný příkaz výjimkou zachycenou dodanou vstupní smyčkou.
void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

char[][] previous;
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
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
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
