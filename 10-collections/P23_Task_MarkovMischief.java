/// # Task · MarkovMischief
/// Lesson: 10.3 Each name has its own account — 03-maps.html
///
/// Feed a tiny word machine a phrase and let it remix new nonsense from words it has seen together.
/// Repeated examples should influence which continuation wins the next draw.
///
/// **Your mission:** Training forgets earlier followers whenever a word appears again. Restore the
/// collected possibilities so the remix can escape a repetitive two-word loop.

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

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Apply the model here
///
/// Each Map value is itself a mutable List of followers. For `sun cat sun dog`, sun must retain both
/// cat and dog. Repeating cat is meaningful: the prepared generator chooses an entry, so duplicate
/// followers affect probability. `containsKey(word)` asks whether that word already owns a List; create
/// a new List only for a first encounter.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Write a tiny corpus with several different followers after the same word. Repeat one follower on
/// purpose and listen to the resulting rhythm of the generated nonsense.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
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
