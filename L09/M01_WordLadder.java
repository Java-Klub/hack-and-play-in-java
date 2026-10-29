/// # L09.01 · WordLadder
///
/// Proměň COLD ve WARM změnou jediného písmene při každém tahu. Součástí hádanky je celá cesta: dosáhneš
/// cíle, aniž bys zapomněl, kudy jsi šel?
///
/// **Tvoje mise:** Vytvoř command(input): odstraň okolní mezery, převeď na uppercase (velká písmena) s
/// Locale.ROOT a přijmi jen slovo ze slovníku lišící se právě jedním písmenem. Změň current a připoj
/// slovo do history. WARM nastaví outcome s history.size()-1 tahy. Neplatný tah nebo dokončené kolo
/// odmítni před změnou current i history.
///
/// **Dnes trénuješ:** Vytvoř práci s ordered mutable collection (uspořádanou měnitelnou kolekcí) a
/// určuj přijetí i prázdný případ.
///
/// Po spuštění programu napiš CORD a potom CARD a posouvej se od COLD; /r obnoví začátek a /q
/// ukončí hru. Po výhře Enter zahájí novou cestu.
void command(String input) {
    require(outcome.isEmpty());
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F22">
/// ### List (seznam) drží pořadí měnících se věcí
///
/// Array (pole) má pevný počet míst. Mutable List (měnitelný seznam) může růst i ubývat a zachovává
/// pořadí. `List<T>` popisuje přístup k prvkům typu T; `ArrayList<T>` je měnitelná JDK implementation
/// (implementace).
///
/// ```java
/// List<String> trail = new ArrayList<>();
/// trail.add("north");
/// trail.add("east");
/// String latest = trail.removeLast();
/// IO.println(trail + " / " + latest);
/// ```
///
/// Výsledek je `[north] / east`. `add` připojí na konec; `removeLast` poslední prvek odstraní a zároveň
/// vrátí. Před odebráním z prázdného List ověř `isEmpty`. Queue (fronta) odebírá vpředu a přidává
/// vzadu, zatímco undo stack (zásobník změn) odebírá nejnovější prvek. Správný konec určuje význam
/// pořadí.
///
/// `List.of("north", "east")` vytvoří unmodifiable List (neměnitelný seznam): čtení funguje, přidávání
/// ne. `new ArrayList<>(existing)` vytvoří mutable List s dosavadními prvky. Předpověz obsah trail po
/// přidání `"south"`. Když vracíš dvě změny, která má zmizet jako první?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst tuto část
///
/// `words` je konečný slovník, při hraní jej vypíše /words. Připravené `adjacent(a,b)` ověřuje rozdíl
/// jednoho písmene. `require(false)` odmítá akci pomocí IllegalArgumentException. Počáteční slovo už je
/// v history, proto se počet navštívených slov a tahů liší o jednu. Console loop (textové opakování)
/// obstará restart i konec.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Celý navržený word (slovo) ověř před změnou cesty.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Použij připravený slovník a adjacent; přijatá slova history prodlužují, nenahrazují.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Po obou guard (vstupních podmínkách) ulož normalizované slovo, připoj je do history a pro WARM
/// použij počet tahů, nikoli navštívených slov.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Vlastní varianta · dobrovolně">
/// Vytvoř nový čtyřpísmenný slovník, začátek i cíl s alespoň jednou známou cestou. Povolená slova
/// vypiš, aby šla hádanka objevovat offline.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String[] words = {"COLD", "CORD", "CARD", "WARD", "WARM", "WORD", "WORM", "GOLD", "SOLD", "TOLD", "HOLD"};
String current;
String outcome;
List<String> history;

/// Připravený restart: vytvoří počáteční model (model pravidel); neotevírá okno.
void reset() {
    current = "COLD";
    history = new ArrayList<>(List.of(current));
    outcome = "";
}

boolean adjacent(String a, String b) {
    if (a.length() != b.length()) {
        return false;
    }
    int n = 0;
    for (int i = 0; i < a.length(); i++) {
        if (a.charAt(i) != b.charAt(i)) {
            n++;
        }
    }
    return n == 1;
}

void display() {
    IO.println("Dictionary: " + String.join(" ", words));
    IO.println(String.join(" -> ", history) + "; target WARM");
}

void main() {
    reset();
    IO.println("Word Ladder: transform COLD to WARM using the listed words. /r restarts; /q quits.");
    display();
    while (true) {
        boolean finished = !outcome.isEmpty();
        String input = IO.readln(finished ? "Enter or /r: new round; /q: quit > " : "> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) return;
        if (input.strip().equalsIgnoreCase("/r") || finished && input.isBlank()) {
            reset();
            display();
            continue;
        }
        if (finished) {
            IO.println("This round is finished. Enter or /r starts a fresh trail; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. Change one letter to a listed word; the trail has not changed.");
        }
    }
}

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
/// - `List` — Uspořádaná sequence (posloupnost) přístupná přes index (pořadové číslo); některé List mohou
///   měnit velikost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
