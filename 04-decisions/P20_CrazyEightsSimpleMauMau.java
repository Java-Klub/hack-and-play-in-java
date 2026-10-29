/// # 04.20 · CrazyEightsSimpleMauMau
///
/// **Zbav se karet dřív než bot.** Rozhodčí ve výchozí verzi odmítá každou kartu. Při hraní `p 1`
/// vybírá první kartu ve vypsané ruce; toto pořadí není číselný kód karty.
///
/// **Úkol:** Doplň `legal` níže. Osmička je povolená vždy; ostatní karty potřebují stejnou
/// hodnotu jako vrchní karta nebo stejnou aktivní barvu. Neplatný číselný kód odmítni. Tato
/// metoda dostává interní kód karty 0–51, nikoli její pořadí v ruce.
///
/// **Výsledek:** Platnou kartu půjde odehrát a bot odpoví. Při osmičce přidej barvu 0–3,
/// například `p 1 2`; příkaz `d` lízne kartu jen tehdy, když nemáš platný tah.

boolean legal(int card) {
    if (card < 0 || card >= 52) return false;
    int value = rank(card);
    return switch (value) {
        case 8 -> true;
        default -> value == rank(top) || card / 13 == suit;
    };
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F09">
/// ### Switch expression (výraz s výběrem) vybírá hodnotu
///
/// Expression (výraz) má hodnotu. Switch expression přečte selector (vybíranou hodnotu) a zvolí
/// výsledek za odpovídající šipkou. `return` jej předá volajícímu. Nejdřív přečti tento malý výběr
/// zvuku:
///
/// ```java
/// String sound(int taps) {
///     return switch (taps) {
///         case 0 -> "silence";
///         case 1, 2 -> "tap";
///         default -> "drum";
///     };
/// }
/// ```
///
/// Trace (průchod) pro `sound(2)`: selector je 2, společný case (větev) zahrnuje 2, výsledkem tedy je
/// `"tap"`. Další case se neprovádí. Pro 7 neodpovídá žádný uvedený case, takže `default` dá `"drum"`.
/// Semicolon (středník) uzavírá celý switch expression. Výsledky musí odpovídat return type (typu
/// výsledku) uvedenému u method (metody).
///
/// Switch může stejně vybrat boolean, číslo nebo record (datový záznam). Sám automaticky nemění field
/// (sdílenou proměnnou): vrátit navržený směr a uložit směr jsou dvě různé akce. Předpověz `sound(0)` a
/// `sound(-1)` a najdi příslušný case.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst tuto část
///
/// Připravená method (metoda) `rank(card)` vrací 2–14; `card / 13` dává suit (barvu) 0–3. `top` a
/// `suit` jsou aktuální state (stav) stolu. Zde jen rozhoduj platnost: kartu neodebírej ani neměň suit.
/// Console command (textový příkaz) to po přijetí udělá sám.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Odliš neplatný index (pozici) od běžného platného tahu.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Jako switch selector (vybíranou hodnotu) použij rank; default (záložní větev) má stále dvě možné
/// příčiny přijetí.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Nejdřív odmítni vstup mimo 0–51; rank 8 vrací true, default porovnává rank NEBO suit.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Vymysli jiný wild rank (hodnotu divoké karty) a před rozdáním jej řekni soupeři. Zahraj dvě kola:
/// vyplatí se tuto kartu schovávat?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
List<List<Integer>> hands;
ArrayDeque<Integer> stock;
int top;
int suit;
String outcome;
String message;

/// reset(): Obnovuje počáteční kolo Crazy Eights / Simple Mau-Mau a maže stav patřící starému kolu.
void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 52; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    stock = new ArrayDeque<>(cards);
    hands = List.of(new ArrayList<>(), new ArrayList<>());
    for (int n = 0; n < 7; n++) {
        for (int who = 0; who < 2; who++) {
            hands.get(who).add(stock.remove());
        }
    }
    do {
        top = stock.remove();
        if (rank(top) == 8) {
            stock.add(top);
        }
    }
    while (rank(top) == 8);
    suit = top / 13;
    outcome = message = "";
}

int rank(int card) {
    return card % 13 + 1;
}

int firstLegal(int who) {
    for (int i = 0; i < hands.get(who).size(); i++) {
        if (legal(hands.get(who).get(i))) {
            return i;
        }
    }
    return -1;
}

/// play(): Odebere platnou kartu, změní vrchní kartu a zvolenou barvu a rozpozná prázdnou ruku.
void play(int who, int index, int chosenSuit) {
    require(index >= 0 && index < hands.get(who).size());
    int card = hands.get(who).get(index);
    require(legal(card) && chosenSuit >= 0 && chosenSuit < 4);
    hands.get(who).remove(index);
    top = card;
    suit = rank(card) == 8 ? chosenSuit : card / 13;
    if (hands.get(who).isEmpty()) {
        outcome = who == 0 ? "You win" : "Bot wins";
    }
}

/// draw(): Dodané kreslení čte tento model; volání paint neposouvá pravidla.
void draw(int who) {
    require(firstLegal(who) < 0);
    if (!stock.isEmpty()) {
        hands.get(who).add(stock.remove());
    }
}

/// command(): Ověřuje jeden skutečný příkaz hráče před jeho provedením v této hře.
void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.toLowerCase(Locale.ROOT).split("\\s+");
    if (input.equalsIgnoreCase("d")) {
        draw(0);
    } else {
        require((p.length == 2 || p.length == 3) && p[0].equals("p"));
        int index = Integer.parseInt(p[1]) - 1;
        require(index >= 0 && index < hands.get(0).size());
        int card = hands.get(0).get(index);
        require(rank(card) != 8 || p.length == 3);
        play(0, index, p.length == 3 ? Integer.parseInt(p[2]) : card / 13);
    }
    if (!outcome.isEmpty()) {
        return;
    }
    if (stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
        return;
    }
    int index = firstLegal(1);
    if (index < 0) {
        draw(1);
    } else {
        play(1, index, hands.get(1).get(index) / 13);
    }
    if (outcome.isEmpty() && stock.isEmpty() && firstLegal(0) < 0 && firstLegal(1) < 0) {
        outcome = "Draw: both blocked";
    }
}

String card(int n) {
    return rank(n) + "/" + n / 13;
}

/// display(): Zobrazuje aktuální desku a volby zadávání v konzoli.
void display() {
    IO.println("Suits 0=clubs 1=diamonds 2=hearts 3=spades. Ace=1, faces11-13.");
    IO.println("Top " + card(top) + "; active suit " + suit + "; bot cards " + hands.get(1).size() + "; stock " + stock.size());
    for (int i = 0; i < hands.get(0).size(); i++) {
        IO.println((i + 1) + ": " + card(hands.get(0).get(i)));
    }
}

/// main(): Spouští skutečnou herní smyčku: zobrazit, načíst, ověřit, opakovat nebo ukončit.
/// Připravený console flow (konzolový průběh): napiš příkaz a stiskni Enter. Platný příkaz obnoví desku.
/// Odmítnutý vstup neopakuje staré události; po konci Enter či /r začne znovu a /q ukončí hru.
void main() {
    reset();
    IO.println("Crazy Eights / Simple Mau-Mau");
    IO.println("Empty your hand against the bot. Match the top card's rank or active suit; an eight can change the suit.");
    IO.println("p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
    IO.println("Example: p 1 plays your first card if legal; p 1 2 plays an eight and chooses hearts. d draws only when blocked.");
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
            IO.println("Invalid move. " + "p index plays a card; an eight needs p index suit(0-3). d draws one only when blocked, then passes. /r restarts; /q quits.");
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
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `!=` — Ověří, že se dvě hodnoty liší.
/// - `&&` — Obě condition (podmínky) musejí platit; je-li první false, Java druhou přeskočí.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `!` — Před boolean expression (logickým výrazem) obrátí true na false a naopak.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `%` — Remainder (zbytek po dělení); hodí se pro opakující se pozice nebo tahy.
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
/// - `else` — Alternativní větev pro případ, že předchozí if condition (podmínka) neplatí.
/// - `for` — Opakuje block (blok) pro zvolený rozsah nebo posloupnost hodnot.
/// - `while` — Opakuje svůj block (blok), dokud condition (podmínka) zůstává true.
/// - `switch` — Vybere case (větev) z několika možností. Může také vrátit výsledek.
/// - `case` — Jedna větev switch; šipka ukazuje, co větev provede nebo vrátí.
/// - `->` — Ve switch: výsledek nebo akce větve. V lambda (anonymní funkci): popis akce, která se provede
///   až při pozdějším volání.
/// - `null` — Žádný object (objekt) tu není. IO.readln vrací null, když vstup skončí.
/// - `try` — Block (blok), jehož případné selhání může zpracovat následující catch.
/// - `catch` — Zpracuje uvedený druh exception (výjimky), aby selhání nezůstalo bez vysvětlení.
/// - `throw` — Oznámí selhání vytvořením nebo předáním exception (výjimky).
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
