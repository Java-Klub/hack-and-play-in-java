/// # Vyřešený úkol · MarkovMischief
/// Lekce: 10.3 Každé jméno má svůj účet — 03-maps.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Nakrm malý slovní stroj větou a nech jej míchat nové nesmysly ze slov, která viděl vedle sebe.
/// Opakované příklady mají ovlivnit další vylosované pokračování.
///
/// **Tvoje mise:** Training (učení) zapomene dřívější následníky při každém opakování slova. Obnov
/// shromážděné možnosti, aby se remix dostal z opakované dvouslovné smyčky.
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
        if (!links.containsKey(words[i])) {
            links.put(words[i], new ArrayList<>());
        }
        if (i + 1 < words.length) {
            links.get(words[i]).add(words[i + 1]);
        }
    }
    return links;
}

// <editor-fold defaultstate="collapsed" desc="Pro tuto hru">
/// ### Použij model právě zde
///
/// Každá Map value (hodnota mapy) je sama mutable List (měnitelný seznam) následníků. Pro `sun cat sun
/// dog` musí sun uchovat cat i dog. Opakované cat má význam: připravený generator (vytvářecí postup)
/// vybírá položku, proto duplicity mění pravděpodobnost. `containsKey(word)` zjišťuje, zda slovo už
/// vlastní List; novou vytvoř jen při prvním setkání.
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
