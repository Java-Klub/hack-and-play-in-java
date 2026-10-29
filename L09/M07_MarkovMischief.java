/// # L09.07 · MarkovMischief
///
/// Nakrm malý slovní stroj větou a nech jej míchat nové nesmysly ze slov, která viděl vedle sebe.
/// Opakované příklady mají ovlivnit další vylosované pokračování.
///
/// **Tvoje mise:** Training (učení) zapomene dřívější následníky při každém opakování slova. Obnov
/// shromážděné možnosti, aby se remix dostal z opakované dvouslovné smyčky.
///
/// **Dnes trénuješ:** Tato Map (mapa) ukládá jako hodnotu List (seznam): jedno slovo může mít více
/// následníků včetně opakovaných. Kolekci pro slovo založ záměrně a při dalších výskytech ji zachovej.
///
/// Po spuštění programu stiskni Enter pro remix ukázkového corpus (učebního textu) moon cat
/// moon dog moon cat, nebo napiš vlastní krátkou větu.
Map<String, List<String>> train(String text) {
    if (text.isBlank() || text.length() > 100000) {
        throw new IllegalArgumentException("Corpus size: 1..100000 characters.");
    }
    String[] words = text.strip().split("\\s+");
    var links = new LinkedHashMap<String, List<String>>();
    for (int i = 0; i < words.length; i++) {
        if (true) {
            links.put(words[i], new ArrayList<>());
        }
        if (i + 1 < words.length) {
            links.get(words[i]).add(words[i + 1]);
        }
    }
    return links;
}

// <editor-fold defaultstate="collapsed" desc="Připomenutí · F24">
/// ### Map (mapa): hodnota podle klíče
///
/// `Map<String, Integer>` přiřazuje textovým keys (klíčům) celočíselné values (hodnoty). Jeden klíč
/// má nejvýše jednu uloženou hodnotu.
///
/// ```java
/// Map<String, Integer> badges = new HashMap<>();
/// badges.put("moon", 4);
/// IO.println(badges.getOrDefault("moon", 0));
/// IO.println(badges.getOrDefault("sun", 0));
/// ```
///
/// Výstup je `4`, potom `0`. `getOrDefault(key, fallback)` přečte existující hodnotu; při chybějícím
/// klíči vrátí fallback (náhradní hodnotu). Toto čtení nic nevkládá. `put(key, value)` hodnotu uloží
/// a případnou předchozí hodnotu téhož klíče přepíše; čísla automaticky nesčítá. `containsKey(key)`
/// použij, když potřebuješ výslovně rozlišit chybějící klíč. V těchto příkladech neukládáme `null`.
///
/// Předpověz: po `badges.put("moon", 9)` co vrátí stejná dvě čtení?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Jak číst tento kód">
/// ## Jak číst právě tyto řádky
///
/// - `Map<String, List<String>>` — Každý String key (textový klíč) vede k uspořádané kolekci možných
///   dalších slov.
/// - `if (true)` — Tato branch (větev) běží pokaždé; zvaž důsledek pro existující List (seznam)
///   následníků.
/// - `links.put(words[i], new ArrayList<>())` — Nahradí případnou uloženou kolekci pro toto slovo novou
///   prázdnou kolekcí.
/// - `links.get(words[i]).add(words[i + 1])` — Najde kolekci tohoto slova a přidá následující slovo.
///
/// ### Co už obstarává připravená část
///
/// main nabídne malý corpus (učební text) a předá zvolený text do generate. train dodá Map (mapu
/// následovníků slov); vygenerovaný remix se vypíše vedle původního corpus.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Map (mapa) může odkazovat na mutable list (měnitelný seznam)
///
/// `Map<String, List<String>>` přiřazuje každému slovu seznam možných následujících slov.
/// `containsKey(key)` zjišťuje existenci klíče. `get(key)` přečte uložený seznam; při chybějícím
/// klíči vrátí null a volání add na null selže. Čtení existujícího seznamu jej nekopíruje. V jiném
/// příkladu: pokud klíč `gate` ukládá `[hall]`, připojení `yard` k seznamu získanému přes get změní i
/// uložený seznam na `[hall, yard]`.
///
/// V train musí opakovaná slova zachovat dřívější následovníky. Předpověz záznamy po `moon cat moon
/// dog`. Vysvětli, kdy je třeba vytvořit seznam a kdy zachovat existující. Opakovaní následovníci
/// jsou dovolení: jejich opakované výskyty ovlivňují pozdější náhodný výběr.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 1 · první vodítko">
/// Stejné slovo se může v textu opakovat.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Nápověda 2 · bližší pohled">
/// Nahrazení jeho List (seznamu) zahodí starší následníky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Možná oprava · prozrazuje postup">
/// Počáteční put podmiň containsKey; další add zachovej, včetně stejných následníků.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
String corpus = "The moon hired a tiny dragon. The tiny dragon collected umbrellas. "
    + "The moon collected rumours. Every umbrella wanted a holiday. Every dragon wanted the moon.";

String generate(String text, long seed, int count) {
    if (count < 1 || count > 200) {
        throw new IllegalArgumentException("Words: 1..200.");
    }
    var links = train(text);
    String first = links.keySet().iterator().next();
    String current = first;
    var random = new Random(seed);
    var output = new StringJoiner(" ");
    for (int i = 0; i < count; i++) {
        output.add(current);
        var next = links.get(current);
        current = next.isEmpty() ? first : next.get(random.nextInt(next.size()));
    }
    return output.toString();
}

void main() {
    String input = IO.readln("Corpus (blank moon cat moon dog moon cat, q quits): ");
    if (input == null || input.strip().equalsIgnoreCase("q")) return;
    corpus = input.isBlank() ? "moon cat moon dog moon cat" : input;
    try {
        IO.println("Corpus: " + corpus);
        IO.println("Generated remix: " + generate(corpus, 42, 50));
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
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
/// - `String` — Text, například "Red Fox". Dvojité uvozovky označují text přímo napsaný v kódu.
/// - `"` — Dvojité uvozovky ohraničují text literal (text přímo zapsaný v kódu). Samotné uvozovky do
///   textu nepatří.
/// - `\` — Uvnitř textu začíná escape (zvláštní zápis), například \n pro nový řádek nebo \" pro uvozovku.
/// - `long` — Whole-number type (typ pro celá čísla) s větším rozsahem než int. Koncové L označuje long
///   literal (přímo zapsanou hodnotu).
/// - `var` — Java odvodí type (typ) místní hodnoty z expression (výrazu) vpravo. Type zůstává pevný.
/// - `=` — Uloží hodnotu vpravo pod jméno vlevo. Není to comparison (porovnání).
/// - `==` — Porovnává primitive value (primitivní hodnoty). Pro obsah textu použij equals.
/// - `||` — Musí platit aspoň jedna condition (podmínka); je-li první true, Java druhou přeskočí.
/// - `++` — Zvětší číselnou hodnotu o jedna.
/// - `+` — Sčítá čísla. Pokud je součástí String, spojuje text.
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
/// - `Map` — Přiřazuje key (klíčům) hodnoty. Pro chybějící key je třeba rozhodnout výchozí chování.
/// - `Random` — Generuje pseudorandom choice (pseudonáhodné volby). Pevný seed (počáteční hodnota)
///   zopakuje stejnou posloupnost.
/// - `IO` — Console input/output (vstup a výstup konzole). println vypíše řádek. readln čeká na celý
///   řádek vstupu.
// </editor-fold>
