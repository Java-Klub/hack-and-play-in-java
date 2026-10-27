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
String templateLine() {
    return "IO.println(\"HELLO\");";
}

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

// <editor-fold defaultstate="collapsed" desc="Step-by-step hints">
/// ## If you get stuck
///
/// 1. The line in quotes is data; it is not executed as Java code.
/// 2. To put a double quote inside a String literal, write backslash then quote.
/// 3. Use the String literal "IO.println(\"HELLO\");".
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
/// - `\` — Inside quoted text, starts an escape such as \n for a new line or \" for a quote.
/// - `return` — Ends this method call, optionally handing a result back to its caller.
/// - `IO` — Console input and output. println writes a line; readln waits for one line of text.
// </editor-fold>

