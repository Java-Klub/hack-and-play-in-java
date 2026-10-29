/// # L04.02 · Ghost
///
/// Build a word fragment together, adding one letter each turn. Completing a dictionary word of at least
/// four letters, or leaving no possible continuation, makes the current player lose.
///
/// **Your mission:** R should begin ROBOT, ROBIN or ROCK, but the unfinished search rejects every
/// prefix. Build a while search over words: return true at the first startsWith match, otherwise
/// advance and return false only after the dictionary ends.
///
/// **Today you learn:** While search: examine one candidate at a time and advance the index after a
/// failed match. There are two normal endings: return true when a word fits, or reach the dictionary’s
/// end and return false.
///
/// Read the printed dictionary and type R; the starter wrongly declares a dead
/// prefix despite the listed R words.
boolean possiblePrefix(String candidate) {
    int index = 0;
    while (index < words.length) {
        if (words[index].startsWith(candidate)) {
            return true;
        }
        index++;
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="Recall · F10">
/// A `while` checks its condition before every pass, including the first. If the condition is false
/// immediately, its body runs zero times. Something must make progress towards stopping, or
/// deliberately wait for new input.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int energy = 5;
/// while (energy > 0) {
///     IO.println(energy);
///     energy -= 2;
/// }
/// ```
///
/// Predict the printed numbers and the final stored value. The output is `5`, `3`, `1`; afterwards
/// energy is `-1`, so the next condition fails. The condition is not checked halfway through the
/// body. `energy -= 2` subtracts two and stores the result; without it, the loop repeats forever.
/// `return` ends the method; `break` ends the loop. Trace the condition and changing values
/// separately, especially when the data being searched can grow.
///
/// Now predict the trace if energy starts at four.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Read this code">
/// A search has two exits: a successful early return and an unsuccessful end. index begins at zero;
/// words.length counts candidates.
///
/// | dictionary | candidate | visits | result |
/// | --- | --- | --- | --- |
/// | CAT, ROBOT | RO | CAT then ROBOT | true |
/// | CAT, ROBOT | XY | both words | false |
/// | empty | RO | none | false |
///
/// `words[index].startsWith(candidate)` asks one word, not the whole dictionary. An unsuccessful pass
/// must increase index. A successful return leaves immediately; it need not scan later words. Predict
/// which entries are visited for candidate C. The prepared command validates the player's letter and
/// applies the returned answer.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Trace RO against two words with the match second.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Put the single-word check inside a while guarded by the current index.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return true on a match; advance after a miss; keep the final false outside the loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Make it yours">
/// Replace the dictionary with five words of at least four letters. Give two words a long shared prefix
/// and make one tempting prefix a dead end. Exchange dictionaries: can the other player explain a safe
/// opening and your trap before playing?
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
    IO.println("The dictionary is deliberately small. /words lists its words without using a turn.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/words")) {
            IO.println("This small game uses only these words: " + String.join(", ", words));
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
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
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
