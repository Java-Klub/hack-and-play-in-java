/// # 08.13 · FourColourMap
///
/// **Překresli mapu a nachytej rozhodčího.** Vyhledej data `map` a nahraď jejich řádky jediným
/// řádkem `"AABCCDEE"`. Spusť hru a zadej postupně `B 2`, `C 1`, `D 1`, `E 2`. Stejnou barvu teď
/// sdílejí jen sousedé C–D, ale původní rozhodčí hlásí tři konflikty.
///
/// **Úkol:** Uprav zjišťování sousedství, aby vycházelo ze skutečných řádků mapy A–E. Po změně
/// kresby už nemá být nutné opravovat další seznam hranic. Za sousedství považuj společnou
/// stranu, nikoli pouhý dotyk rohem.
///
/// **Výsledek:** Zkušební mapa s těmito barvami má jediný konflikt. I dlouhá společná hranice se
/// počítá jako jedna konfliktní dvojice. Zkoumání sousedů zachová hráčovy barvy a musí fungovat
/// také pro jiné obdélníkové mapy.

boolean touches(int first, int second) {
    if (first == second) {
        return false;
    }
    char a = (char) ('A' + first);
    char b = (char) ('A' + second);
    for (int row = 0; row < map.length; row++) {
        for (int column = 0; column < map[row].length(); column++) {
            char here = map[row].charAt(column);
            if (column + 1 < map[row].length()) {
                char right = map[row].charAt(column + 1);
                if ((here == a && right == b) || (here == b && right == a)) {
                    return true;
                }
            }
            if (row + 1 < map.length) {
                char below = map[row + 1].charAt(column);
                if ((here == a && below == b) || (here == b && below == a)) {
                    return true;
                }
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F17">
/// ### Traversal: vždy jedna otázka
///
/// Traversal (průchod) navštěvuje hodnoty; search (hledání) může skončit první shodou, count
/// (počítání) musí prohlédnout všechny relevantní hodnoty. Ve for-each loop (cyklu pro každý prvek)
/// dostane jméno před dvojtečkou jednu hodnotu ze sequence (posloupnosti) za ní. Průběžný počet založ
/// mimo opakované tělo, aby dřívější shody přežily.
///
/// ```java
/// int[] pulses = {2, 0, 4};
/// int strong = 0;
/// for (int pulse : pulses) {
///     if (pulse > 1) {
///         strong++;
///     }
/// }
/// IO.println(strong);
/// ```
///
/// Výsledek je dvě, protože pulzy dva a čtyři splňují condition (podmínku). Kdybys potřeboval pouze
/// vědět, zda existuje nějaký silný pulz, kdy už můžeš odpovědět? Jaký počet má mít prázdné array
/// (pole)?
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Jak číst tento kód">
/// Opakovaný dotyk je stále jediný vztah dvou regionů. Procházení buněk a počítání konfliktních dvojic
/// jsou proto dva odlišné traversal (průchody).
///
/// V jiné mapě AB / BA sdílí A a B čtyři buněčné hrany, ale tvoří jedinou sousední dvojici. V AB / CD
/// se A a D potkají jen rohem a nejsou sousedé. Předpověz vztahy v AA / BB. String (řetězec) řádku
/// nabízí charAt(column); další řádek či sloupec musí existovat před čtením. Skutečné mapy používají
/// pouze A–E.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Porovnej geografii vypsanou display s informací, kterou čte rozhodčí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// conflicts se nyní ptá touches na každou dvojici. touches stále používá pevnou tabulku hranic, takže
/// změnu mapy nezpozoruje.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Procházej každou buňku a existujícího souseda vpravo/dole. U požadované dvojice přijmi obě pořadí
/// písmen a vrať true při prvním hranovém dotyku. false vrať až po průchodu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta">
/// Navrhni mapu s dlouhou hranicí a úzkým průchodem. Dej ji spolužákovi s obarvením chybným v jediné
/// dvojici. Najde jeho rozhodčí stejný konflikt i po přesunutí průchodu?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int conflicts() {
    int total = 0;
    for (int first = 0; first < colours.length; first++) {
        for (int second = first + 1; second < colours.length; second++) {
            if (colours[first] != 0 && colours[first] == colours[second] && touches(first, second)) {
                total++;
            }
        }
    }
    return total;
}

String[] map = {"AABB", "ACCB", "DDEE"};
int[] colours;
String outcome;

/// reset(): Obnovuje počáteční kolo Four-Colour Map a maže stav patřící starému kolu.
void reset() {
    colours = new int[]{1, 0, 0, 0, 0};
    outcome = "";
}

/// solved(): Vyžaduje obarvení každé oblasti a žádný konflikt sousedů.
boolean solved() {
    for (int c : colours) {
        if (c == 0) {
            return false;
        }
    }
    return conflicts() == 0;
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toUpperCase(Locale.ROOT).split("\\s+");
    require(p.length == 2 && p[0].matches("[B-E]"));
    int region = p[0].charAt(0) - 'A';
    int colour = Integer.parseInt(p[1]);
    require(colour >= 0 && colour <= 4);
    colours[region] = colour;
    if (solved()) {
        outcome = "Map coloured correctly!";
    }
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    for (String row : map) {
        IO.println(row);
    }
    IO.println("A is fixed1. Colours1-4, 0 unfilled; corner-only touch is not adjacent.");
    for (int i = 0; i < 5; i++) {
        IO.println((char)('A' + i) + " = " + colours[i]);
    }
    IO.println("Equal-colour shared-edge conflicts: " + conflicts());
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Four-Colour Map");
    IO.println("Colour every region so regions sharing an edge differ. Region A stays colour 1; corner contact does not count.");
    IO.println("Enter region B-E and colour1-4; 0 clears. A=1 is fixed. Shared-edge neighbours must differ. /r restarts; /q quits.");
    IO.println("Example: B 2 colours region B with colour 2. B 0 clears it.");
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
            IO.println("Invalid move. " + "Enter region B-E and colour1-4; 0 clears. A=1 is fixed. Shared-edge neighbours must differ. /r restarts; /q quits.");
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
/// - `<` — Porovnává čísla: menší než. <= připouští i rovnost; > a >= porovnávají opačným směrem. V
///   List<String> lomené závorky popisují element type (typ prvku).
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
