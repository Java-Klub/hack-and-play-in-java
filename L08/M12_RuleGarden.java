/// # L08.12 · RuleGarden
///
/// Vypěstuj stránku vzorů z jediného svítícího políčka a malého číslovaného pravidla. Změnou pravidla
/// objevuj trojúhelníky, pruhy nebo zdánlivý chaos.
///
/// **Tvoje mise:** Nová políčka okamžitě ovlivňují další políčka ve stejném řádku a křiví vzor. Oprav
/// nextRow(), aby celá generace četla tentýž starý řádek.
///
/// **Dnes trénuješ:** Simultaneous updates (současné aktualizace): odděl čtený snapshot (snímek) od
/// zapisované generace. Výsledek nemá záviset na průchodu sloupců zleva či zprava.
///
/// Program hned ukáže rule (pravidlo) 30 v terminálu. V další výzvě napiš 90 pro porovnání, stiskni
/// Enter pro opakování současného pravidla, nebo q pro konec.
boolean[] nextRow(boolean[] current, int rule) {
    boolean[] next = new boolean[current.length];
    for (int column = 0; column < current.length; column++) {
        int pattern = current[column] ? 2 : 0;
        if (column > 0 && current[column - 1]) {
            pattern += 4;
        }
        if (column + 1 < current.length && current[column + 1]) {
            pattern += 1;
        }
        next[column] = ((rule>>pattern)&1) != 0;
    }
    return next;
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F26">
/// ### Generation (generace) sdílí stejnou minulost
///
/// Simultaneous update (současná aktualizace) znamená, že každá nová cell (buňka) čte stejný minulý
/// svět. Při přepisování starých cell za pochodu by další column (sloupec) viděl budoucnost, kterou
/// soused neviděl.
///
/// Zkus malé pravidlo: cell se rozsvítí, jestliže předtím svítila ona nebo její levý neighbour
/// (soused). Za levým okrajem je zhasnuto. Ze starého `[true,false,false]` musí vzniknout
/// `[true,true,false]`.
///
/// ```java
/// boolean[] old = {true, false, false};
/// boolean[] next = new boolean[old.length];
/// for (int x = 0; x < old.length; x++) {
///     next[x] = old[x] || (x > 0 && old[x - 1]);
/// }
/// ```
///
/// Proč ne tři true? Třetí cell čte old[1], tedy false, nikoli právě zapsané next[1]. Old zůstává
/// nedotčené, takže obrácené pořadí průchodu dá stejný výsledek. Hotové next zveřejni až po výpočtu
/// celé generation.
///
/// Nakresli OLD nad NEXT a šipky od čtených cell k zapisované cell. Předpověz druhou generation.
/// Two-dimensional grid (dvourozměrná mřížka) potřebuje stejný princip včetně samostatných
/// zapisovatelných row (řádků).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `boolean[] next = current` — Obě jména nyní odkazují na stejný měnitelný řádek, takže zápis změní
///   pozdější čtení.
/// - `current.length` — Počet políček vstupního řádku včetně malých testovacích řádků.
/// - `column - 1` — Levý soused se čte jen tehdy, když jeho index (pozice) leží uvnitř řádku.
/// - `((rule>>pattern)&1) != 0` — Připravený bit expression (bitový výraz) čte výstup ano/ne pro tento
///   třípolíčkový vzor.
///
/// ### Co už obstarává připravená část
///
/// Připravený bit decoder (dekodér bitů) převádí tři sousední hodnoty na volbu v tabulce pravidla. grow()
/// vypíše dokončenou generaci a pak použije nextRow() pro další; vnější políčka zůstávají zhasnutá.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// Připravený kód zapisuje left/centre/right (levou/střední/pravou cell) vahami 4/2/1: svítící kraje a
/// zhasnutý střed dávají pattern (vzor) 5. `(rule >> pattern) & 1` čte příslušný rule bit (bit
/// pravidla). Výpočet pravidla zůstává připravený; ty odděluješ old a next. Projdi tři sousední column
/// (sloupce) se stejným nezměněným current.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// next=current vytváří dvě jména jedné array; dřívější zápis změní vstup pozdější buňky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Clone by jednorozměrné boolean hodnoty oddělil, ale nová výstupní array nejjasněji rozliší čtení a
/// zápis.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Vytvoř new boolean[current.length]; všechny pattern vstupy čti z current a next vrať až za loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Začni dvěma svítícími cell (buňkami) místo jedné a porovnej rules (pravidla) 30 a 90. Před výpisem
/// předpověz, kde se oba rostoucí vzory poprvé setkají.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<String> grow(int rule, int width, int rows) {
    if (rule < 0 || rule > 255 || width < 3 || width > 121 || rows < 1 || rows > 60) {
        throw new IllegalArgumentException("Use rule 0..255, width 3..121 and rows 1..60.");
    }
    boolean[] cells = new boolean[width];
    cells[width / 2] = true;
    var result = new ArrayList<String>();
    for (int row = 0; row < rows; row++) {
        var line = new StringBuilder();
        for (boolean cell : cells) {
            line.append(cell ? '#' : '.');
        }
        result.add(line.toString());
        cells = nextRow(cells, rule);
    }
    return result;
}

/// displayRule(): Vypíše konečný vzor vybraného pravidla.
void displayRule(int rule, int width, int rows) {
    IO.println("Rule " + rule + "; # is on, . is off; cells outside the width stay off.");
    for (String line : grow(rule, width, rows)) {
        IO.println(line);
    }
}

/// Program hned vypíše rule (pravidlo) 30. # znamená zapnuto a . vypnuto; řádek je jedna generace.
/// V dalším prompt (výzvě) zkus jiné pravidlo, Enter zopakuje současné a q skončí. Žádný soubor
/// neotvíráš.
void main() {
    int rule = 30;
    IO.println("RULE GARDEN: watch one starting cell grow into a pattern.");
    IO.println("Each printed line is a new generation. Try 90 or 110 to compare with rule 30.");
    displayRule(rule, 61, 31);
    while (true) {
        String input = IO.readln("New rule 0..255; Enter or r: repeat; q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        try {
            int requested = input.isEmpty() || input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r")
                    ? rule : Integer.parseInt(input);
            if (requested < 0 || requested > 255) {
                throw new IllegalArgumentException("Rule: 0..255.");
            }
            rule = requested;
            displayRule(rule, 61, 31);
        } catch (IllegalArgumentException invalidInput) {
            IO.println("Enter a rule number from 0 to 255, or q to quit.");
        }
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
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
