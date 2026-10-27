/// # L03.07 · Hanoi
///
/// Přesuň tři disky z tyčky A na tyčku C, vždy pouze horní disk. Vymysli pořadí bez většího disku na
/// menším a zjisti, jak blízko se dostaneš sedmi tahům.
///
/// **Tvoje mise:** Věž přijímá větší disk na menším a odmítá některá neškodná položení. Oprav odmítající
/// comparison (porovnání) v move před změnou kterékoli hromádky.
///
/// **Dnes trénuješ:** Ochrana před zakázaným stavem: popiš špatnou situaci v condition (podmínce) a vrať
/// se bez provedení tahu. Prázdný cíl posuzuj odděleně od cíle s horním diskem.
///
/// Použij Run File, zadej A B a potom znovu A B pro prohlédnutí zakázaného většího disku na menším; r
/// obnoví původní věž.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Připomenutí · F08 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
boolean move(int from, int to) {
    if (won() || from < 0 || from > 2 || to < 0 || to > 2 || from == to || sizes[from] == 0) {
        return false;
    }
    int disc = pegs[from][sizes[from] - 1];
    if (sizes[to] > 0 && pegs[to][sizes[to] - 1] < disc) {
        return false;
    }
    sizes[from]--;
    pegs[from][sizes[from]] = 0;
    pegs[to][sizes[to]] = disc;
    sizes[to]++;
    moves++;
    return true;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F08">
/// Guard (ochranná podmínka) odmítne požadavek před chráněnou prací. Nejdřív přečti odmítané případy
/// a potom se ptej, co zůstává povolené. Short-circuit (zkrácené vyhodnocení) je důležité: u `A || B`
/// Java přeskočí B, pokud A už platí.
///
/// ```java
/// boolean useSlot(int slot) {
///     if (slot < 0 || slot >= 4) {
///         return false;
///     }
///     IO.println("Using slot " + slot);
///     return true;
/// }
/// ```
///
/// Předpověz výpis a vrácenou hodnotu pro `-1`, `3` a `4`. Jen trojka dojde k výpisu. Odmítnutí musí
/// nastat před jakoukoli state change (změnou stavu), která se nemá provést. Přístup na desku sám
/// může potřebovat kontrolu rozsahu, než jej lze bezpečně vyhodnotit. `return false` ukončí toto call
/// (volání metody); automaticky nevrací zpět práci již provedenou předtím.
///
/// `useSlot(3)` předá trojku do `slot`; `boolean` před názvem říká, že výsledkem je true/false. Nyní
/// předpověz slot nula.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `won()` — Zeptej se připravené kontroly výhry, zda už hra skončila.
/// - `sizes[from] == 0` — Odmítni prázdnou zdrojovou tyčku před pokusem přečíst její horní disk.
/// - `&&` — Druhá část se ověřuje pouze při platné první části, takže u prázdného cíle nedojde ke čtení
///   horního disku.
/// - `pegs[to][sizes[to] - 1]` — Připravené úložiště uchovává horní disk tyčky na místě její současné
///   velikosti minus jedna.
/// - `> disc` — Toto se nyní ptá, zda je horní disk cíle větší než přesouvaný disk; rozmysli, zda právě
///   to je zakázaná situace.
/// - `return false;` — Odmítni před pozdějšími zápisy odebírajícími nebo vkládajícími disk.
/// - `moves++;` — Započítej jeden přijatý tah po úpravě obou hromádek.
///
/// ### Co už obstarává připravená část
///
/// Pozdější část move už přesouvá jediný horní disk a upravuje velikost obou hromádek. reset obnoví
/// hádanku tří disků; main vypisuje horní disk napravo a převádí písmena tyček na index (indexy)
/// připraveného úložiště.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Čísla disků jsou velikosti: 1 je menší než 2.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Prázdný cíl musí přeskočit čtení svého horního disku.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Uvnitř ochrany neprázdného cíle odmítni targetTop < disc.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
int[][] pegs = {{3, 2, 1}, {0, 0, 0}, {0, 0, 0}};
int[] sizes = {3, 0, 0};
int moves;

void reset() {
    for (int[] peg : pegs) {
        Arrays.fill(peg, 0);
    }
    pegs[0][0] = 3;
    pegs[0][1] = 2;
    pegs[0][2] = 1;
    sizes[0] = 3;
    sizes[1] = 0;
    sizes[2] = 0;
    moves = 0;
}

boolean won() {
    return sizes[2] == 3;
}

void main() {
    IO.println("HANOI: move all three discs from A to C.");
    IO.println("Move only the top disc, never a larger disc onto a smaller one.");
    IO.println("The top disc is the rightmost number on each peg.");
    boolean showState = true;
    while (true) {
        if (showState) {
            for (int i = 0; i < 3; i++) {
                IO.println((char) ('A' + i) + ": " + Arrays.toString(Arrays.copyOf(pegs[i], sizes[i])));
            }
            IO.println("Moves: " + moves);
            if (won()) {
                IO.println("Solved! The minimum for three discs is 7 moves.");
            }
            showState = false;
        }
        String input = IO.readln(won() ? "Enter or /r: new round; /q: quit: " : "From to, e.g. A C; r restart, q quit: ");
        if (input == null || (input.trim().equalsIgnoreCase("q") || input.trim().equalsIgnoreCase("/q"))) {
            return;
        }
        input = input.trim().toUpperCase(Locale.ROOT);
        if ((input.equals("R") || input.equals("/R") || (won() && input.isBlank()))) {
            reset();
            showState = true;
            continue;
        }
        if (won()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        String[] parts = input.split("\\s+");
        if (parts.length == 2 && parts[0].length() == 1 && parts[1].length() == 1
                && move(parts[0].charAt(0) - 'A', parts[1].charAt(0) - 'A')) {
            showState = true;
        } else {
            IO.println("Illegal move. Use two different pegs A-C with a legal top disc.");
        }
    }
}

/// Připravené vstupní body: main, reset.
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
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `++` — Zvětší číselnou hodnotu o jedna.
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
/// - `return` — Ukončí právě toto volání method (metody), případně předá výsledek volající části.
/// - `if` — Provede svůj block (blok), jen když condition (podmínka) v závorkách platí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>

