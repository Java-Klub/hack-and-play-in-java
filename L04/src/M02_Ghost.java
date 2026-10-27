/// # L04.02 · Ghost
///
/// Build a word fragment together, adding one letter each turn. Completing a dictionary word of at least
/// four letters, or leaving no possible continuation, makes the current player lose.
///
/// **Your mission:** R is a legal opening for ROBOT, ROBIN and ROCK, but the game rejects it. Repair
/// possiblePrefix so the search can reach every dictionary word and still stop when the answer is known.
///
/// **Today you learn:** While search: examine one candidate at a time and advance the index after a
/// failed match. There are two normal endings: return true when a word fits, or reach the dictionary’s
/// end and return false.
///
/// Use Run File, read the printed dictionary and type R; the starter wrongly declares a dead prefix
/// despite the listed R words.
/// After your repair: Save → Test File → play again.
boolean possiblePrefix(String candidate) {
    int index = 0;
    while (index < words.length && index < 1) {
        if (words[index].startsWith(candidate)) {
            return true;
        }
        index++;
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int index = 0;` — Begin at the first dictionary entry. This initialization happens only once for
///   the whole search.
/// - `while (index < words.length && index < 1)` — Both comparisons must be true. The first checks the
///   dictionary boundary; the extra comparison currently restricts the search further.
/// - `words[index].startsWith(candidate)` — Ask whether the word at this position begins with the whole
///   received fragment.
/// - `return true;` — A matching word is enough to finish the method immediately; no further words are
///   needed.
/// - `index++;` — Move to the next dictionary entry after a word fails the prefix test.
/// - `return false;` — This answer is reached after the loop finishes without finding any continuation.
///
/// ### What the prepared part already does
///
/// words is the supplied public dictionary, printed before play. command appends a letter, calls
/// possiblePrefix, checks completed words and switches turns. display and main supply the text interface,
/// restart and quit handling.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. index < 1 stops after a single dictionary entry.
/// 2. The bound should follow words.length, with index++ on each unsuccessful pass.
/// 3. Remove the extra index < 1 restriction; keep the length check.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] words = {"GHOST", "GHOUL", "GAME", "GAMER", "GARDEN", "ROBOT", "ROBIN", "ROCK", "STONE", "STORY", "START", "STARTER"};
String fragment;
String outcome;
int player;

void reset() {
    fragment = "";
    outcome = "";
    player = 1;
}

void command(String input) {
    require(outcome.isEmpty());
    String letter = input.toUpperCase(Locale.ROOT);
    require(letter.matches("[A-Z]"));
    fragment += letter;
    boolean possible = possiblePrefix(fragment);
    if (!possible || fragment.length() >= 4 && List.of(words).contains(fragment)) {
        outcome = "Player " + (3 - player) + " wins; player " + player + " completed a word or dead prefix.";
    } else {
        player = 3 - player;
    }
}

void display() {
    IO.println("Public dictionary: " + String.join(" ", words));
    IO.println("Player " + player + ", fragment: " + fragment);
}

void main() {
    reset();
    IO.println("Ghost");
    IO.println("Append one letter; completing a word of 4+ letters or a dead prefix loses. /r restarts; /q quits.");
    while (true) {
        display();
        if (!outcome.isEmpty()) {
            IO.println(outcome);
        }
        String input = IO.readln("> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. /r restarts; /q quits.");
            continue;
        }
        try {
            command(input.strip());
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move or command. Append one letter; completing a word of 4+ letters or a dead prefix loses. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

/// Prepared entry points: main, reset, command.
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
/// - `<` — Compares numbers: less than. <= also permits equality; > and >= compare in the other
///   direction. In List<String>, angle brackets describe the element type.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

