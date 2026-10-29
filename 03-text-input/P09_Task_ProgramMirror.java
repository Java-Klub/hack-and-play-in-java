/// # Task · ProgramMirror
/// Lesson: 3.3 Let the player shape the story — 03-stories.html
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
/// not execute its contents. The quotation-mark example in 03-stories.html explains how to put a quotation mark inside text.

String templateLine() {
    return "IO.println(HELLO);";
}

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Invent a new short greeting. Change both the reflected instruction and its separately printed
/// message so they still agree. Try a greeting containing a quote and explain which backslashes
/// belong only to the Java source.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
void main() {
    IO.println(templateLine());
    IO.println("HELLO");
}

// </editor-fold>
