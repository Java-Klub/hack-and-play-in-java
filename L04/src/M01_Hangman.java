/// # L04.01 · Hangman
///
/// Reveal a hidden word one letter at a time before six wrong guesses end the round. Repeated letters can
/// uncover several places at once, so each clue can change your next guess.
///
/// **Your mission:** The display loses the final character of every word. Repair the while boundary in
/// mask so each character produces exactly one letter or underscore, without stopping the index from
/// advancing.
///
/// **Today you learn:** While loops: test a condition before each pass, do one piece of work, then move
/// towards the end. For a six-letter word the valid character indices are 0 through 5; the next index
/// must stop the loop.
///
/// Use Run File and type A for the training word BANANA; compare the displayed _A_A_ with the six places
/// the word needs.
/// After your repair: Save → Test File → play again.
String mask() {
    String result = "";
    int index = 0;
    while (index < word.length()) {
        String letter = word.substring(index, index + 1);
        if (tried.contains(letter)) {
            result += letter;
        } else {
            result += "_";
        }
        index++;
    }
    return result;
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `String result = "";` — Start an empty text result once, before collecting the word’s characters.
/// - `int index = 0;` — The first character has index zero; index names the position currently being
///   processed.
/// - `while (index < word.length() - 1)` — Repeat while this comparison is true. length() gives the
///   character count; distinguish that count from the final valid index.
/// - `word.substring(index, index + 1)` — Read one character as text: include the first position and stop
///   before the second.
/// - `result += "_";` — Append one underscore to the text already collected instead of replacing the
///   whole result.
/// - `index++;` — Increase the position by one after each pass. Without progress, the same character
///   could be visited forever.
/// - `return result;` — After the loop ends, send the completed display text back to the caller.
///
/// ### What the prepared part already does
///
/// The supplied branch chooses a revealed letter or an underscore at one position. guess checks letters
/// and counts misses; main prints mask, handles the six-miss ending and accepts /r or /q. mask itself
/// never asks the player for input.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. A word of length six has indices 0 through 5.
/// 2. Test the current condition when index is five.
/// 3. Continue while index < word.length(); index++ supplies progress.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] words = {"LEVEL", "ROBOT", "GALAXY", "JAVA", "PIXEL", "PUZZLE", "PLANET", "BANANA"};
String word = "BANANA";
String tried = "";
int misses;

void reset() {
    word = words[new Random().nextInt(words.length)];
    tried = "";
    misses = 0;
}

boolean won() {
    return mask().equals(word);
}

boolean over() {
    return won() || misses >= 6;
}

boolean guess(String input) {
    String letter = input.toUpperCase(Locale.ROOT);
    if (over() || !letter.matches("[A-Z]") || tried.contains(letter)) {
        return false;
    }
    tried += letter;
    if (!word.contains(letter)) {
        misses++;
    }
    return true;
}

void main() {
    IO.println("Training word: BANANA. /r chooses a random word.");
    IO.println("HANGMAN: reveal the word before six wrong letters.");
    IO.println("Repeated letters cost nothing. Every letter accepts either case.");
    IO.println("/r chooses a new word; /q quits.");
    while (true) {
        IO.println(mask() + "    Tried: " + tried + "    Misses: " + misses + "/6");
        if (over()) {
            IO.println((won() ? "You win!" : "Out of guesses!") + " Word: " + word);
        }
        String input = IO.readln(over() ? "/r replay, /q quit: " : "Guess A-Z, /r restart, /q quit: ");
        if (input == null || input.trim().equalsIgnoreCase("/q")) {
            return;
        }
        input = input.trim();
        if (input.equalsIgnoreCase("/r")) {
            reset();
            continue;
        }
        if (!guess(input)) {
            IO.println("Enter one new letter A-Z during an unfinished round.");
        }
    }
}

/// Prepared entry points: main, reset.
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
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
/// - `-` — Subtracts numbers; in front of one number, changes its sign.
/// - `/` — Divides numbers. Dividing two int values discards the fractional part.
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
/// - `while` — Keeps repeating its body while its condition stays true.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `Random` — Generates pseudorandom choices. A fixed seed reproduces the same sequence.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

