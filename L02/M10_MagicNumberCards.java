/// # L02.10 · MagicNumberCards
///
/// Think of a number between 0 and 63 and tell the program which of six number cards contains it. The
/// machine combines your yes/no answers to reveal your chosen number.
///
/// **Your mission · Repair a mind-reading trick:** The trick treats typed yes answers as no. Repair
/// yesAnswer so both y and Y are
/// recognized by their text, while n stays false.
///
/// **Today you learn:** Boolean text comparison: a decision can come from text rather than arithmetic.
/// Equal-looking text can be stored separately, and letter case is another choice the comparison must
/// handle.
///
/// Run the program, think of 5 and answer the six cards with y, n, y, n, n, n to compare the
/// reported number before and after your repair.
boolean yesAnswer(String input) {
    return input == "y";
}

// <editor-fold defaultstate="collapsed" desc="Recall · F06">
/// A boolean expression produces `true` or `false`. `&&` requires both conditions, `||` needs at
/// least one, and `!` reverses a boolean result. Comparisons such as `>=` include equality; `>` does
/// not.
///
/// These example statements belong inside a called method.
///
/// ```java
/// int age = 14;
/// boolean hasTicket = false;
/// boolean enter = age >= 12 && hasTicket;
/// boolean recognised = "open".equals("OPEN");
/// ```
///
/// Predict both results: each is `false`. The age is sufficient, but the ticket condition is not.
/// `equals` compares String contents and is case-sensitive; `equalsIgnoreCase` deliberately ignores
/// letter case. For objects, `==` asks whether two references identify the same object, not whether
/// their text matches. A content comparison and a range comparison answer different questions. Check
/// values exactly at a boundary and just beyond it before trusting a condition.
///
/// Now predict enter for age twelve with a ticket.
// </editor-fold>

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

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// For this task the decision is about letter content and case. y and Y mean the same answer, while n
/// must remain different. equalsIgnoreCase compares those contents without changing the input String.
/// Predict the answer for a newly typed uppercase Y; it must work regardless of where the text is
/// stored.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Perform the trick for a friend who chooses the number privately. After it works, deliberately lie
/// on exactly one card and compare the error with that card’s first number. Can your friend explain
/// the changed result from the card rather than guessing magic?
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Typed text need not be the same String object as a quoted literal.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// A case-insensitive comparison handles both y and Y.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Return input.equalsIgnoreCase("y").
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
    IO.println("Think of one number from 0 to 63. Keep that same number for all six cards.");
    IO.println("For each card, type y if it contains your number, otherwise n. Enter sends an answer; /r starts over; /q quits.");
    boolean[] answers = new boolean[6];
    int bit = 0;
    boolean showCard = true;
    while (true) {
        if (showCard) {
            IO.println("Card " + (bit + 1) + "/6:");
            IO.println(card(bit));
            showCard = false;
        }
        String input = IO.readln(bit < 6 ? "Is your number on this card? y/n, /r, /q: " : "Enter or /r: new number; /q: quit: ");
        if (input == null) return;
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) return;
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r") || (bit == 6 && input.isEmpty())) {
            Arrays.fill(answers, false); bit = 0; showCard = true;
            IO.println("Choose a new number from 0 to 63 and keep it for all six cards.");
            continue;
        }
        if (bit == 6) {
            IO.println("The number was revealed. Press Enter or /r for another number; /q quits.");
            continue;
        }
        if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            IO.println("Please type y for present or n for absent, then press Enter. The card above is still current.");
            continue;
        }
        answers[bit] = yesAnswer(input);
        bit++;
        if (bit == 6) IO.println("Your number is " + reveal(answers) + ".");
        else showCard = true;
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
/// - `IO` — Console input and output. println writes a line. readln waits for a whole line of input.
// </editor-fold>
