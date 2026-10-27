/// # L02.10 · MagicNumberCards
///
/// Think of a number between 0 and 63 and tell the program which of six number cards contains it. The
/// machine combines your yes/no answers to reveal your chosen number.
///
/// **Your mission:** The trick treats typed yes answers as no. Repair yesAnswer so both y and Y are
/// recognized by their text, while n stays false.
///
/// **Today you learn:** Boolean text comparison: a decision can come from text rather than arithmetic.
/// Equal-looking text can be stored separately, and letter case is another choice the comparison must
/// handle.
///
/// Use Run File, think of 5 and answer the six cards with y, n, y, n, n, n to compare the reported number
/// before and after your repair.
/// After your repair: Save → Test File → play again.
boolean yesAnswer(String input) {
    return input == "y";
}

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `boolean` — A true/false answer to whether this line means yes.
/// - `yesAnswer(String input)` — A named text decision receiving the complete answer as input.
/// - `input` — The player’s received line; it is not a quoted literal in your source.
/// - `==` — For String objects, this compares identity instead of their text contents.
/// - `"y"` — A one-character String literal; the double quotes mark text, not a variable named y.
/// - `return` — Send the decision to the prepared card-reading loop.
///
/// ### What the prepared part already does
///
/// card constructs the six displayed cards and reveal combines their prepared number values. main
/// validates y/n and asks yesAnswer which response to store. You do not need to unpack the binary-card
/// mathematics for this repair.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. Typed text need not be the same String object as a quoted literal.
/// 2. A case-insensitive comparison handles both y and Y.
/// 3. Return input.equalsIgnoreCase("y").
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<Integer> card(int bit) {
    if (bit < 0 || bit > 5) {
        throw new IllegalArgumentException("Card index: 0..5.");
    }
    var values = new ArrayList<Integer>();
    for (int value = 0; value < 64; value++) {
        if ((value & (1 << bit)) != 0) {
            values.add(value);
        }
    }
    return values;
}

int reveal(boolean[] answers) {
    if (answers.length != 6) {
        throw new IllegalArgumentException("Six answers required.");
    }
    int result = 0;
    for (int bit = 0; bit < 6; bit++) {
        if (answers[bit]) {
            result += 1 << bit;
        }
    }
    return result;
}

void main() {
    IO.println("Think of a number from 0 to 63. Is it on each card?");
    boolean[] answers = new boolean[6];
    for (int bit = 0; bit < 6; ) {
        IO.println(card(bit));
        String input = IO.readln("On this card? y/n (q to quit): ");
        if (input == null || input.equalsIgnoreCase("q")) {
            return;
        }
        if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            IO.println("Please enter y or n.");
            continue;
        }
        answers[bit] = yesAnswer(input);
        bit++;
    }
    IO.println("Your number is " + reveal(answers) + ".");
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
/// - `boolean` — A value that is either true or false.
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `var` — Java infers the local value’s type from the expression on the right. It still has one fixed
///   type.
/// - `=` — Stores the value on the right under the name on the left. It does not compare them.
/// - `==` — Compares primitive values. For text contents, use equals instead.
/// - `!=` — Checks that two values differ.
/// - `&&` — Both conditions must be true; if the first is false, Java skips the second.
/// - `||` — At least one condition must be true; if the first is true, Java skips the second.
/// - `!` — In front of a boolean expression, reverses true and false.
/// - `++` — Increases a numeric value by one.
/// - `+=` — Adds to the old value and stores the result back under the same name.
/// - `+` — Adds numbers. When a String is involved, joins text instead.
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
/// - `for` — Repeats a block over a range or a sequence of values.
/// - `null` — No object is present. IO.readln returns null when the input stream ends.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `List` — An ordered sequence that can be accessed by index; some Lists can change size.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

