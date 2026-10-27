/// # L02.06 · MadLibsStoryMachine
///
/// Give the machine a name, a creature, a place and an object, then read the absurd story it builds from
/// them. Multiword answers can make the result much stranger than single words.
///
/// **Your mission:** Spaces accidentally typed around an answer leak into the story. Repair cleanWord so
/// the outside spaces disappear while a phrase such as Red Fox stays complete.
///
/// **Today you learn:** Whole-line text: reading a phrase is different from reading only its first word.
/// Work on the returned String while preserving the spaces that belong inside the phrase.
///
/// Use Run File and answer the name prompt with spaces around Red Fox, then supply the other three
/// phrases and inspect the finished story.
/// After your repair: Save → Test File → play again.
String cleanWord(String word) {
    return word;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `String cleanWord(String word)` — One String enters as word and a cleaned String should leave as the
///   result.
/// - `word` — A local name for this answer; it may contain several words and spaces.
/// - `return word;` — The current body returns the received text unchanged, including its outside spaces.
/// - `{` — The matching braces enclose the small text-cleaning rule; the prompts remain elsewhere.
///
/// ### What the prepared part already does
///
/// main asks for four complete lines. story calls cleanWord for each answer, checks usable lengths and
/// inserts the cleaned phrases into the prepared story. You are editing the phrase cleanup, not the
/// story’s input machinery.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Do not split at spaces: a name may contain several words.
/// 2. String.strip returns a cleaned copy.
/// 3. Use return word.strip();
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String story(String name, String creature, String place, String object) {
    name = cleanWord(name);
    creature = cleanWord(creature);
    place = cleanWord(place);
    object = cleanWord(object);
    for (String word : List.of(name, creature, place, object)) {
        if (word.isBlank() || word.length() > 40) {
            throw new IllegalArgumentException("Words need 1..40 characters.");
        }
    }
    return name + " hired a " + creature + " to guard " + place + ".\n"
        + "Unfortunately, the only security equipment was a " + object + ".\n"
        + "By midnight, the " + creature + " had promoted the " + object + " to manager.\n";
}

void main(String[] args) {
    String[] labels = {"A name", "A creature", "A place", "An object"};
    String[] words = new String[4];
    for (int i = 0; i < 4; i++) {
        words[i] = args.length > i ? args[i] : IO.readln(labels[i] + " (q to quit): ");
        if (words[i] == null || words[i].equalsIgnoreCase("q")) {
            return;
        }
    }
    try {
        IO.print(story(words[0], words[1], words[2], words[3]));
    } catch (IllegalArgumentException invalid) {
        IO.println(invalid.getMessage());
    }
}

/// Prepared entry points: main.
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
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

