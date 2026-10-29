/// # 03.09 · ProgramMirror
///
/// **Print code as text.** This is a harder detour about quotation marks. The program prints an
/// example of Java code followed by a greeting.
///
/// **Your task:** Edit the text returned by `templateLine` below so the first printed line
/// includes quotation marks around HELLO. The output must look exactly like this:
///
/// ```text
/// IO.println("HELLO");
/// HELLO
/// ```
///
/// The second line already works; keep it. The first line is only printed text: this program does
/// not execute its contents. The section below your code explains how to put a quotation mark
/// inside text.

String templateLine() {
    return "IO.println(\"HELLO\");";
}

// <editor-fold defaultstate="expanded" desc="A quotation mark inside text · F02">
/// An ordinary `"` ends the text, so a quotation mark belonging inside needs to be written as
/// `\"`. Java turns those two written characters into one quotation mark in the text.
///
/// ```java
/// return "The \"Owl\" club";
/// ```
///
/// This example returns `The "Owl" club`. The outer quotation marks and backslashes are not part
/// of the result. Use the same technique to put two quotation marks around HELLO in your text.
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

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Invent a new short greeting. Change both the reflected instruction and its separately printed
/// message so they still agree. Try a greeting containing a quote and explain which backslashes
/// belong only to the Java source.
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
