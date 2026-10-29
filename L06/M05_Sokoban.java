/// # L06.05 · Sokoban
///
/// Dotlač bednu na cíl ve stísněném skladišti. Plánuj dopředu, protože bednu můžeš tlačit, ale
/// nepřitáhneš ji zpět.
///
/// **Tvoje mise:** Nehotový dotaz na podlahu odmítá každý pohyb. Sestav read-only query (neměnící
/// dotaz): před čtením buňky odmítni souřadnice mimo terrain, pak přijmi každý vnitřní znak kromě zdi
/// '#'. Cíl '.' je také podlaha; hráče ani bednu neposouvej.
///
/// **Dnes trénuješ:** Pure contracts (smlouvy čistých dotazů): dotaz slibuje odpověď pro dané vstupy bez
/// změny hry. Přesně určuj, které souřadnice představují průchozí podlahu.
///
/// Zadej A pro volnou buňku vlevo od počátečního hráče. Nehotový dotaz ji odmítne stejně jako zeď; tvá
/// odpověď musí tyto situace rozlišit před provedením pohybu.
boolean floor(int px, int py) {
    return false;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F15">
/// ### Contracts: ptej se bez změny světa
///
/// Contract (smlouva) popisuje přijímaný input (vstup) a význam výsledku. Pure calculation (čistý
/// výpočet) používá své vstupy bez změny dalšího state (stavu). Query (dotaz) může číst aktuální
/// model (model pravidel), ale má jej zachovat. Získání odpovědi a potvrzení akce jsou různé
/// odpovědnosti.
///
/// ```java
/// int contrast(int light, int dark) {
///     return Math.abs(light - dark);
/// }
/// ```
///
/// `Math.abs` dává nezápornou velikost, odstraní tedy případné záporné znaménko. Tuto method (metodu)
/// můžeš volat opakovaně, aniž by něco spotřebovala. Argument (argumenty) `(9, 4)` dávají pět: rozdíl
/// je pět. Předpověz `(4, 9)`. Které uložené hodnoty porovnáš před dotazem a po něm, abys doložil
/// nepřítomnost side effect (vedlejšího účinku)? Stejnou ochranu potřebuje odmítnutý kandidát.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// px je vodorovná souřadnice; py vybírá řádek mapy. Odmítnutí rozsahu musí přijít před vyhodnocením
/// terrain[py] či charAt(px).
///
/// | navržené px,py | údaj mapy | odpověď |
/// | --- | --- | --- |
/// | 0,2 | zeď # | false |
/// | 1,2 | volné místo | true |
/// | 5,2 | cíl . | true |
/// | 7,2 | za řádkem | false |
///
/// Znak na platné pozici dodá druhé rozhodnutí. floor odpovídá na otázku; command zvlášť pohybuje
/// hráčem a případně bednou. Dvě stejná volání floor musejí dát stejnou odpověď se zachováním všech
/// pozic. Před čtením předpověz záporné py.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Čtení jednoho map character (znaku mapy)
///
/// Řádek terrain je String. `row.charAt(index)` čte jeden char na pozici od nuly; například
/// `"map".charAt(1)` přečte prostřední character (znak). Char literal (přímo zapsaný znak) má
/// jednoduché uvozovky, na rozdíl od String literal (textové hodnoty). Zde `#` označuje zeď. Před
/// čtením řádku a znaku zachovej existující bounds (meze). Předpověz odpověď pro vnitřní zeď a
/// vysvětli, proč dvojí dotaz nesmí pohnout hráčem či bednou.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odliš neexistující buňku od existující buňky se zdí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Při špatných souřadnicích vrať false před čtením terrain; potom zkontroluj jediný znak.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Ochraň rozsahy řádku a sloupce a vrať, zda se znak liší od #. Pozice nepřiřazuj.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Navrhni sklad s jedním lákavým posunutím, které bednu uvězní, a jednou cestou k cíli. Zachovej
/// rozměry mapy a před výměnou vyřeš vlastní návrh. Pomocí floor vysvětli obě cesty, aniž by pouhé
/// zkoumání posouvalo hráče.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] terrain = {"#######", "#     #", "#    .#", "#     #", "#######"};
int x;
int y;
int bx;
int by;
String outcome;

/// reset(): Obnovuje počáteční kolo Sokoban a maže stav patřící starému kolu.
void reset() {
    x = 2;
    y = 2;
    bx = 3;
    by = 2;
    outcome = "";
}

/// command(): Před zápisem obou pozic ověří krok hráče i případný cíl tlačené bedny.
void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[wasd]"));
    int dx = input.equalsIgnoreCase("a") ? -1 : input.equalsIgnoreCase("d") ? 1 : 0;
    int dy = input.equalsIgnoreCase("w") ? -1 : input.equalsIgnoreCase("s") ? 1 : 0;
    int nx = x + dx;
    int ny = y + dy;
    require(floor(nx, ny));
    if (nx == bx && ny == by) {
        require(floor(bx + dx, by + dy));
        bx += dx;
        by += dy;
    }
    x = nx;
    y = ny;
    if (terrain[by].charAt(bx) == '.') {
        outcome = "Crate on goal: you win!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    for (int r = 0; r < 5; r++) {
        String s = "";
        for (int c = 0; c < 7; c++) {
            s += x == c && y == r ? '@' : bx == c && by == r ? (terrain[r].charAt(c) == '.' ? '*' : '$') : terrain[r].charAt(c);
        }
        IO.println(s);
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Sokoban");
    IO.println("Push the crate $ onto the goal dot. You can push, but cannot pull; restart if you trap the crate.");
    IO.println("W/A/S/D move. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
    IO.println("Example: d moves right; pressing Enter submits the command. Use /r to reset a stuck attempt.");
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
            IO.println("Invalid move. " + "W/A/S/D move. @ player, $ crate, . goal, * crate on goal. Push only. /r restarts; /q quits.");
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
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
