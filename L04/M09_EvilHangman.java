/// # L04.09 · EvilHangman
///
/// Play hangman against a computer that has not committed to one secret word. After each guess it keeps
/// the largest group of words compatible with its next clue, so every revealed letter costs it options.
///
/// **Your mission:** The computer counts every nonempty group as one word and gives away useful choices.
/// Repair familySize so its total remembers every word counted during earlier passes.
///
/// **Today you learn:** Accumulation: initialize a running total once before a loop, then update it using
/// its previous value. A family containing three words needs three contributions, not three replacements
/// with the same number.
///
/// From the course folder containing README.md:
/// - Play: `java L04/M09_EvilHangman.java`
/// - Check this mission: `java L04/Check.java M09_EvilHangman.java`
///
/// Run the play command above and guess E to see the chosen clue, then use the check command to
/// compare a three-word family with a one-word family. After your repair: save the file, run the
/// check command above, then play again.
///
/// Need an explanation? Learn · F12 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
int familySize(List<String> family) {
    int total = 0;
    for (int i = 0; i < family.size(); i++) {
        total = 1;
    }
    return total;
}

// <editor-fold defaultstate="expanded" desc="Learn · F12">
/// An accumulator remembers a growing result across passes. Its starting value belongs before the
/// loop; putting it inside would start the result again for every item. The remembered result can be
/// text, not only a number.
///
/// These example statements belong inside a called method.
///
/// ```java
/// String receipt = "Items:";
/// for (String item : List.of("tea", "cake")) {
///     receipt += " " + item;
/// }
/// ```
///
/// Predict receipt after each pass: `Items: tea`, then `Items: tea cake`. `+=` combines the old value
/// with the new contribution and stores the result. Plain `=` replaces the stored value.
/// `List.of(...)` supplies the fixed sequence of listed values; the colon visits one item per pass.
/// Trace the current item and accumulator in two columns to discover whether earlier work survives.
///
/// Now predict receipt when the supplied list contains only `"rice"`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `int total = 0;` — Start with no counted words before the first pass.
/// - `for (int i = 0; i < family.size(); i++)` — Visit each family member once; size() is the number of
///   words in the supplied group.
/// - `total = 1;` — Assignment replaces the old total with one, so earlier passes currently leave no
///   lasting contribution.
/// - `return total;` — After all passes, return the accumulated group size for comparison with other
///   groups.
///
/// ### What the prepared part already does
///
/// command groups compatible words by the clue they would reveal and calls familySize to compare those
/// groups. The supplied selection rule resolves ties by alphabetical clue order, preserves earlier clues
/// and counts misses. display and main supply guesses, restart and quit.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// total = 1 replaces the previous count.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// The accumulator must remember earlier members.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use total += 1, leaving total = 0 before the loop.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] dictionary = {"ALLY", "BETA", "COOL", "DEAL", "ELSE", "FLEW", "GOOD", "HOPE", "IBEX", "JAZZ", "KEPT", "LEND"};
List<String> pool;
String mask;
String tried;
String outcome;
int misses;

void reset() {
    pool = new ArrayList<>(List.of(dictionary));
    mask = "____";
    tried = "";
    misses = 0;
    outcome = "";
}

String pattern(String word, char letter) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s += word.charAt(i) == letter ? letter : mask.charAt(i);
    }
    return s;
}

void command(String input) {
    require(outcome.isEmpty());
    String letter = input.toUpperCase(Locale.ROOT);
    require(letter.matches("[A-Z]") && !tried.contains(letter));
    var families = new TreeMap<String, List<String>>();
    for (String word : pool) {
        families.computeIfAbsent(pattern(word, letter.charAt(0)), (k)->new ArrayList<>()).add(word);
    }
    String best = "";
    int count = -1;
    for (var entry : families.entrySet()) {
        if (familySize(entry.getValue()) > count) {
            best = entry.getKey();
            count = familySize(entry.getValue());
        }
    }
    pool = new ArrayList<>(families.get(best));
    if (best.equals(mask)) {
        misses++;
    }
    mask = best;
    tried += letter;
    if (!mask.contains("_")) {
        outcome = "You win: " + mask;
    } else {
        if (misses == 6) {
            outcome = "You lose. A consistent word: " + pool.getFirst();
        }
    }
}

void display() {
    IO.println(mask + "  Tried: " + tried + "  Misses: " + misses + "/6");
    IO.println("Largest family stays; ties use alphabetical mask order.");
}

void main() {
    reset();
    IO.println("Evil Hangman");
    IO.println("Guess one letter. The computer keeps a consistent largest word family. /r restarts; /q quits.");
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
            IO.println("This small game uses only these words: " + String.join(", ", dictionary));
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
            IO.println("Invalid move or command. Guess one letter. The computer keeps a consistent largest word family. /r restarts; /q quits.");
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
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
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
/// - `?` — In condition ? first : second, chooses the first value when the condition is true, otherwise
///   the second.
/// - `:` — In for (int value : values), reads each element in turn. It also separates the two
///   alternatives of ? : or follows a switch label.
/// - `[]` — An array or an indexed access. Index 0 denotes the first element; length is the element
///   count.
/// - `new` — Creates a fresh value or object, such as a new array or window.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `if` — Runs its body only when the condition in parentheses is true.
/// - `else` — The alternative when the preceding if condition is false.
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `->` — In switch: a case result or action. In a lambda: describes an action to perform when called
///   later.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>

