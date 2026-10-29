/// # L09.07 · MarkovMischief
///
/// Feed a tiny word machine a phrase and let it remix new nonsense from words it has seen together.
/// Repeated examples should influence which continuation wins the next draw.
///
/// **Your mission:** Training forgets earlier followers whenever a word appears again. Restore the
/// collected possibilities so the remix can escape a repetitive two-word loop.
///
/// **Today you learn:** This Map stores a List as each value: one word can have several followers,
/// including repeated ones. Create a word’s collection deliberately, then preserve it across later
/// occurrences.
///
/// From the course folder containing README.md:
/// - Play: `java L09/M07_MarkovMischief.java`
/// - Check this mission: `java L09/Check.java M07_MarkovMischief.java`
///
/// After the play command, press Enter to remix the sample corpus moon cat moon dog moon cat, or
/// type your own short phrase. After your repair: save the file, run the check command above, then
/// play again.
///
/// Need an explanation? Recall · F24 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
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

// <editor-fold defaultstate="collapsed" desc="Recall · F24">
/// ### Map: look up a value by its key
///
/// A `Map<String, Integer>` associates text keys with integer values. Each key has at most one stored
/// value.
///
/// ```java
/// Map<String, Integer> badges = new HashMap<>();
/// badges.put("moon", 4);
/// IO.println(badges.getOrDefault("moon", 0));
/// IO.println(badges.getOrDefault("sun", 0));
/// ```
///
/// The output is `4`, then `0`. `getOrDefault(key, fallback)` reads an existing value, or returns the
/// fallback when the key is absent. This lookup does not insert anything. `put(key, value)` stores a
/// value and replaces any previous value for that key; it does not add numbers automatically. Use
/// `containsKey(key)` when you need to distinguish absence explicitly. These examples do not store
/// null values.
///
/// Predict: after `badges.put("moon", 9)`, what will the same two lookups return?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `Map<String, List<String>>` — Each string key maps to an ordered collection of possible next words.
/// - `if (true)` — This branch runs every time; consider what that does to an existing follower list.
/// - `links.put(words[i], new ArrayList<>())` — Replaces any collection already stored for this word with
///   a fresh empty one.
/// - `links.get(words[i]).add(words[i + 1])` — Finds this word’s collection and appends the following
///   word.
///
/// ### What the prepared part already does
///
/// main offers one small corpus and passes the chosen text to generate. train supplies its
/// word-successor map; the generated remix is printed beside the original corpus.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### A map can point to a mutable list
///
/// `Map<String, List<String>>` maps each word to a list of possible next words. `containsKey(key)`
/// asks whether a key exists. `get(key)` reads its stored list, or returns null when absent; calling
/// add on null fails. Reading an existing list does not copy it. For a separate example, if key
/// `gate` stores `[hall]`, appending `yard` to the list obtained by get makes the stored list `[hall,
/// yard]` too.
///
/// In train, repeated words must retain earlier successors. Predict the entries after `moon cat moon
/// dog`. Explain when a list needs creating and when an existing list must survive. Repeated
/// successors are allowed: their repeated occurrences affect later random choices.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The same word can occur several times in the corpus.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Replacing its List discards all earlier followers.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Guard the initial put with containsKey; keep every later add, including duplicate followers.
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

/// Use the play command above to start the program. The check command checks the rule without
/// opening a window. The supplied part supports only the main game or experiment described above.
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

// <editor-fold defaultstate="collapsed" desc="Small Java map · optional">
/// ## An unfamiliar piece of Java?
///
/// This small map is optional. Look up just the symbol or word you need; no need to learn it all first.
///
/// - `///` — Words for the reader. Java skips these comment lines when running.
/// - `;` — Ends one statement, like a full stop. A semicolon inside a for header separates its three
///   parts.
/// - `()` — After a name: call an action, or list its parameters in its declaration. Empty parentheses
///   mean no supplied arguments.
/// - `{}` — Braces group a block of code. The opening and closing brace belong together.
/// - `.` — Accesses something belonging to the value or type on its left, such as IO.println.
/// - `void` — This method performs an action and returns no result value.
/// - `main` — Java starts the program here. Other method declarations do not run just because they appear
///   earlier in the file.
/// - `int` — A type for whole numbers within a fixed range.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `long` — A type for whole numbers with a larger range than int. A trailing L marks a long literal.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `++` — Increases a numeric value by one.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `Map` — Associates keys with values. A missing key needs an explicit fallback or decision.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
