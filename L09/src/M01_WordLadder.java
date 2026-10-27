/// # L09.01 · WordLadder
///
/// Proměň COLD ve WARM změnou jediného písmene při každém tahu. Součástí hádanky je celá cesta: dosáhneš
/// cíle, aniž bys zapomněl, kudy jsi šel?
///
/// **Tvoje mise:** Po platném tahu cesta ztratí starší slova. Oprav změnu history tak, aby odmítnuté tahy
/// i restart zůstaly spolehlivé.
///
/// **Dnes trénuješ:** Mutable List (měnitelný seznam) pamatuje položky v pořadí. Jedno přijaté slovo má
/// prodloužit existující cestu; nahrazení celého List změní, co si hráč pamatuje.
///
/// Po Run File napiš CORD a potom CARD a posouvej se od COLD; /r obnoví začátek a /q ukončí hru. Po výhře Enter zahájí novou cestu.
/// Po opravě: Save → Test File → zahraj si znovu.
///
/// Potřebuješ výklad? Nová dovednost · F22 vysvětluje dovednost; Jak číst tento kód vysvětluje tyto
/// řádky. Nápovědy 1 a 2 se otevírají zvlášť; Možná oprava prozrazuje postup.
void command(String input) {
    require(outcome.isEmpty());
    String word = input.toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word) && adjacent(current, word));
    current = word;
    history.add(word);
    if (word.equals("WARM")) {
        outcome = "Reached WARM in " + (history.size() - 1) + " moves!";
    }
}

// <editor-fold defaultstate="expanded" desc="Nová dovednost · F22">
/// ### List (seznam): pořadí, které můžeš měnit
///
/// `List<String>` uchovává texty v pořadí. `new ArrayList<>()` vytvoří mutable list (měnitelný
/// seznam): obsah lze měnit bez nahrazení samotného seznamu.
///
/// ```java
/// List<String> snacks = new ArrayList<>();
/// snacks.add("apple");
/// snacks.add("nuts");
/// IO.println(snacks.get(0));
/// IO.println(snacks.size());
/// ```
///
/// Výstup je `apple`, potom `2`. `add(value)` připojí jeden prvek na konec; `get(index)` přečte prvek
/// bez odebrání. Indexes (indexy) začínají nulou. `isEmpty()` zjišťuje, zda seznam nemá prvky.
/// `removeLast()` odebere a vrátí poslední prvek; volej jej jen u neprázdného seznamu. Seznam přímo z
/// `List.of(...)` měnit nelze, ale `new ArrayList<>(List.of(...))` měnit lze.
///
/// Předpověz: po `snacks.removeLast()` jaká bude velikost a první prvek?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `history =` — Assignment (přiřazení) nahradí List, na který history odkazuje; neprodlouží jej.
/// - `List.of(word)` — Vytvoří List (seznam) jen s tímto slovem, bez dřívější cesty.
/// - `&&` — Před změnou current musí platit členství ve slovníku i pravidlo jednoho písmene.
/// - `history.size() - 1` — Slova zahrnují počáteční pozici, proto je tahů o jeden méně.
///
/// ### Co už obstarává připravená část
///
/// Main přečte slovo a command je ověří před změnou current. Adjacent počítá změněná písmena, display
/// vypíše stejnou historii a reset vytvoří počáteční cestu od COLD.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Pro tuto hru">
/// ### Operaci pro historii máš k dispozici tady
///
/// Kvůli tomuto API nemusíš absolvovat jinou volitelnou slovní hru. U mutable list (měnitelného
/// seznamu) `list.add(item)` připojí prvek a zachová předchozí prvky. Změní tentýž seznam. Naproti
/// tomu `list = new ArrayList<>(...)` změní, který seznam proměnná označuje. Samotné `List.of(...)`
/// je neměnitelné; připravená historie je `ArrayList`.
///
/// Trasuj zvlášť přijatý a odmítnutý command (příkaz): začni na COLD, přijmi CORD, potom zkus WARM,
/// který se liší více písmeny. Odmítnutý příkaz musí zachovat současné slovo i všechny položky
/// historie. Než určíš místo operace s historií, najdi konec validation (ověření).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Prozkoumej, co assignment (přiřazení) nového ArrayList udělá se staršími slovy.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Použij operaci, která přidá jedno slovo do existující sequence (posloupnosti).
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Projdi COLD → CORD → CARD a potom odmítni WARM bez přidání.
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

/// Skutečný program spusť přes Run File. Test File ověří pravidlo bez otevření okna.
/// Připravená část zajišťuje pouze uvedenou hlavní hru nebo pokus.
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

