/// # L01.05 · ProgramMirror
///
/// A small program mirror shows a Java instruction as text, then shows the message that instruction would
/// print. Compare the instruction with its result and see how a program can describe another program.
///
/// **Your mission:** The reflected instruction has lost the quotation marks around HELLO. Repair
/// templateLine so its printed instruction contains them, while the next output stays HELLO.
///
/// **Today you learn:** Quoted text and escaping: code-looking text inside a String is still only data. A
/// quote that belongs inside that text needs special notation so Java does not mistake it for the end of
/// the String.
///
/// Use Run File and compare the two lines in Output: the first should describe an instruction and the
/// second should be its message.
/// After your repair: Save → Test File → play again.
///
/// Need an explanation? Recall · F02 explains the Java skill; Read this code explains these lines.
/// Hints 1 and 2 open separately; Repair outline reveals the direction.
String templateLine() {
    return "IO.println(\"HELLO\");";
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
/// - `String` — The returned value is text, even when that text looks like a Java instruction.
/// - `templateLine()` — This no-input action supplies the instruction’s written form.
/// - `return` — Give that text to main, which will print it.
/// - `"IO.println(HELLO);"` — The outer quotes enclose the entire text value; the characters inside are
///   not executed here.
/// - `IO.println(HELLO);` — Inside the String, the dot, parentheses and semicolon are printable
///   characters describing another instruction.
/// - `;` — The semicolon after the closing quote ends the real return statement; it differs from the
///   printed semicolon inside the text.
///
/// ### What the prepared part already does
///
/// main prints the returned template and then the separate HELLO message. This task mirrors one
/// instruction; it does not print its own complete source. The two actual printing actions are already
/// supplied.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="For this game">
/// Separate three layers: the Java source you edit, the text returned by templateLine, and the two
/// lines printed in Output. The first printed line must include the quote characters around HELLO;
/// the second is the separate message. Those first-line characters are data, not another instruction
/// being executed. For a neutral example, source text `"A \"B\" C"` represents the value `A "B" C`:
/// each `\"` contributes one quote without ending the outer literal. Try predicting that value before
/// changing your own literal. Keep the real return statement's final semicolon distinct from a
/// semicolon printed inside its text.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · a first clue">
/// The line in quotes is data; it is not executed as Java code.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a closer look">
/// To put a double quote inside a String literal, write backslash then quote.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repair outline · reveals the direction">
/// Use the String literal "IO.println(\"HELLO\");".
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void main() {
    IO.println(templateLine());
    IO.println("HELLO");
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
/// - `String` — Text, such as "Red Fox". Double quotes mark text literally written in the code.
/// - `"` — Double quotes enclose a text literal. The quotes themselves are not part of its text.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `IO` — Console input and output. println writes a line.
// </editor-fold>

