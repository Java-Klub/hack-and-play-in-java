/// # L01.04 · BitmapBanner
///
/// Make a large arcade sign out of small printed # characters. The handmade alphabet lets you turn a
/// short message into five rows of pixel lettering.
///
/// **Your mission · Restore an arcade sign:** The sign currently prints only blank space. Repair the
/// message from bannerMessage so
/// the default sign reads JAVA CLUB!.
///
/// **Today you learn:** Literals and output: the chosen text determines what another action prints.
/// Spaces inside quotes are real characters too, even though their output can look empty.
///
/// Run the program and look in terminal output for the five-line sign; after repairing it, try
/// reading it from farther away.
String bannerMessage() {
    return "JAVA CLUB!";
}

// <editor-fold defaultstate="collapsed" desc="Recall · F02">
/// A String literal is text written between double quotes. Those outside quotes tell Java where the
/// text begins and ends; they are not printed. Digits inside quotes are text too. An expression, by
/// contrast, can calculate a value before printing it.
///
/// These example statements belong inside a called method.
///
/// ```java
/// IO.println("Shelf 4");
/// IO.println(4 + 2);
/// IO.println("Shelf \"B\"");
/// ```
///
/// Predict the three lines. They are `Shelf 4`, `6`, and `Shelf "B"`. In the last literal, each `\"`
/// represents one quote belonging to the text. Its backslash is notation, not another printed
/// character. `IO.println` prints its argument and ends the line. A space inside a literal remains a
/// real space; changing comments does not change the printed message.
///
/// Now predict `IO.println(4 + 5);` and `IO.println("4 + 5");`.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read this code">
/// ## Read these particular lines
///
/// - `String` — This action answers with text rather than a number.
/// - `bannerMessage()` — The empty parentheses mean no input is needed to choose the default sign.
/// - `{` — The matching braces contain the answer-producing body.
/// - `return` — Pass the message back; this statement does not print the sign by itself.
/// - `"     "` — This literal contains five spaces, not an empty pair of quotes. The font renders those
///   spaces as blank cells.
/// - `;` — Finish the return statement after the text value.
///
/// ### What the prepared part already does
///
/// banner already contains the small letter patterns and builds five output rows. main asks for the
/// default message and prints the result. This font supports A B C J L U V ! and spaces.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For this game">
/// This time the result is text in the terminal, not a separate window or file. The starter prints
/// five rows of spaces and then finishes normally; blank output is the fault you are investigating.
/// Keep the output's fixed-width lettering visible by widening the panel. The supplied font
/// supports only the listed letters, `!` and spaces, so arbitrary text may be rejected. A comment
/// describing a new sign cannot change the message returned by bannerMessage.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Follow the values · predict a case">
/// Text can be present even when you cannot see a letter. `"   "` stores three spaces; `""` stores no
/// characters. The banner looks up a five-row shape for each supported letter. The question for this
/// task is which data to send, not how to draw all five rows. Predict whether adding a space changes
/// the printed width.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Make an absurd club sign using the available letters A B C J L U V, spaces and !. Try CLUB JAVA!
/// or a repeated alarm. Change the existing drawing character from # to another single character and
/// decide which sign remains readable.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// Spaces are real characters, but this font leaves them blank.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// Keep the text inside one pair of double quotes.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// The complete return statement is return "JAVA CLUB!";
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
Map<Character, String> font = Map.of(
    'A', "01110/10001/11111/10001/10001",
    'B', "11110/10001/11110/10001/11110",
    'C', "01111/10000/10000/10000/01111",
    'J', "00111/00010/00010/10010/01100",
    'L', "10000/10000/10000/10000/11111",
    'U', "10001/10001/10001/10001/01110",
    'V', "10001/10001/10001/01010/00100",
    '!', "00100/00100/00100/00000/00100",
    ' ', "00000/00000/00000/00000/00000"
);

String banner(String message, char ink) {
    if (message.isEmpty() || message.length() > 30) {
        throw new IllegalArgumentException("Message length: 1..30.");
    }
    String upper = message.toUpperCase(Locale.ROOT);
    var result = new StringBuilder();
    for (int row = 0; row < 5; row++) {
        for (char letter : upper.toCharArray()) {
            String glyph = font.get(letter);
            if (glyph == null) {
                throw new IllegalArgumentException("Supported letters: A B C J L U V ! and space.");
            }
            for (char pixel : glyph.split("/")[row].toCharArray()) {
                result.append(pixel == '1' ? ink : ' ');
            }
            result.append(' ');
        }
        result.append('\n');
    }
    return result.toString();
}

void main() {
    try {
        String message = bannerMessage();
        IO.println("Banner preview: [" + message + "]");
        IO.println("--- five picture rows below ---");
        IO.print(banner(message, '#'));
        IO.println("--- end of banner; spaces in the starter make blank rows ---");
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
/// - `'` — Single quotes enclose one character, such as a board marker.
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `char` — A type for one UTF-16 code unit, often a simple board character such as X. Text uses
///   String.
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
/// - `null` — No object is present.
/// - `try` — A block whose possible failures can be handled by the following catch.
/// - `catch` — Handles the named kind of exception instead of leaving that failure unexplained.
/// - `throw` — Reports a failure by creating or passing an exception.
/// - `Map` — Associates keys with values. A missing key needs an explicit fallback or decision.
/// - `IO` — Console input and output. println writes a line. print writes text without adding a newline.
// </editor-fold>
