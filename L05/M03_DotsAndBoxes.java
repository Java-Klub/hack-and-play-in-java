/// # L05.03 · DotsAndBoxes
///
/// Střídejte se v zabírání čar mezi tečkami. Kdo uzavře čtvereček, získá bod a další tah.
///
/// **Tvoje mise:** Každý čtvereček teď sleduje stejné čtyři hrany. Oprav box(int n), aby bodování
/// odpovídalo skutečně uzavřenému čtverečku.
///
/// **Dnes trénuješ:** Arrays (pole): int[] může popsat skupinu míst na desce. Převeď číslo čtverečku na
/// indexy (pozice) hran, které potřebuje volající kód.
///
/// Spusť program a střídejte se v zadávání dosud volného čísla hrany od 1 do 12, vždy
/// potvrzeného Enter.
int[] box(int n) {
    return new int[]{0, 1, 6, 7};
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
/// - `int[]` — Výsledek je array (pole) celočíselných indexů hran.
/// - `box(int n)` — n je číslo čtverečku předané volajícím kódem; počítá se od nuly.
/// - `new int[]` — Vytvoří samostatné array (pole) s hodnotami z následujících složených závorek.
/// - `return` — Vrátí skupinu hran do command(); samotné hrany tím nezabere.
///
/// ### Co už obstarává připravená část
///
/// display() kreslí očíslované hrany. command() zabere hranu, přes box() prověří nedokončené čtverečky a
/// vyřídí body i další tah; tato pravidla jsou připravená.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### I hrany jsou hodnoty
///
/// Vrácené int array (pole) uvádí čtyři edge index (pozice hran), nikoli čtyři rohy. Nakresli
/// zobrazenou desku a očísluj hrany vnitřními index (pozicemi), o jednu menšími než vypsaná čísla. U
/// vybraného čtverce zvlášť najdi horní, dolní, levou a pravou hranu. `new int[]{...}` sdruží čtyři
/// čísla do výsledku. Než napíšeš obecný expression (výraz) pro n, předpověz jiný čtverec než levý
/// horní.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Číslo čtverečku dává řádek n / 2 a sloupec n % 2.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Vodorovné hrany zabírají 0–5; svislé začínají na 6.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vrať horní, dolní, levou a pravou hranu v novém int[].
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[] edges;
int[] owners;
int[] scores;
int player;
int moves;
String outcome;

/// reset(): Obnovuje počáteční kolo Dots and Boxes a maže stav patřící starému kolu.
void reset() {
    edges = new boolean[12];
    owners = new int[4];
    scores = new int[3];
    player = 1;
    moves = 0;
    outcome = "";
}

/// command(): Zabere hranu, přidělí nově uzavřené čtverečky a po zisku ponechá tah.
void command(String input) {
    require(outcome.isEmpty());
    int edge = Integer.parseInt(input) - 1;
    require(edge >= 0 && edge < 12 && !edges[edge]);
    edges[edge] = true;
    moves++;
    int gained = 0;
    for (int b = 0; b < 4; b++) {
        if (owners[b] != 0) {
            continue;
        }
        boolean complete = true;
        for (int e : box(b)) {
            complete &= edges[e];
        }
        if (complete) {
            owners[b] = player;
            scores[player]++;
            gained++;
        }
    }
    if (moves == 12) {
        outcome = scores[1] == scores[2] ? "Draw" : "Player " + (scores[1] > scores[2] ? 1 : 2) + " wins";
    }
    if (gained == 0) {
        player = 3 - player;
    }
}

String edge(int n) {
    return edges[n] ? "##" : String.format("%02d", n + 1);
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Player " + player + "; scores " + scores[1] + ":" + scores[2]);
    for (int r = 0; r < 3; r++) {
        IO.println("+--" + edge(r * 2) + "--+--" + edge(r * 2 + 1) + "--+");
        if (r < 2) {
            IO.println(edge(6 + r * 3) + "  " + owners[r * 2] + "   " + edge(7 + r * 3) + "  " + owners[r * 2 + 1] + "   " + edge(8 + r * 3));
        }
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Dots and Boxes");
    IO.println("Take turns claiming numbered edges. Close a box to score and play again; most boxes wins.");
    IO.println("Enter an unclaimed edge number 1-12. Completing a box grants another turn. /r restarts; /q quits.");
    IO.println("Example: enter 1 to claim edge 01. ## means an edge is already taken.");
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
            IO.println("Invalid move. " + "Enter an unclaimed edge number 1-12. Completing a box grants another turn. /r restarts; /q quits.");
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
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
