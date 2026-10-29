/// # Solved task · BitmapBanner
/// Lesson: 3.3 Let the player shape the story — 03-stories.html
///
/// Original assignment (this version is complete):
///
/// **Light up the club sign.** Running the program leaves five empty rows between the separator
/// lines. It already knows how to draw letters, but its message contains only spaces.
///
/// **Your task:** In `bannerMessage` below, replace the spaces inside the quotation marks with
/// `JAVA CLUB!`, including the space between the words and the exclamation mark. Keep the other
/// characters on that line.
///
/// **Result:** The text output will show a large JAVA CLUB! sign made from `#` characters. This
/// program does not open a separate window.

String bannerMessage() {
    return "JAVA CLUB!";
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// This time the result is text in the terminal, not a separate window or file. The starter prints
/// five rows of spaces and then finishes normally; blank output is the fault you are investigating.
/// Keep the output's fixed-width lettering visible by widening the panel. The supplied font
/// supports only the listed letters, `!` and spaces, so arbitrary text may be rejected. A comment
/// describing a new sign cannot change the message returned by bannerMessage.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own version · optional">
/// Make an absurd club sign using the available letters A B C J L U V, spaces and !. Try CLUB JAVA!
/// or a repeated alarm. Change the existing drawing character from # to another single character and
/// decide which sign remains readable.
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

// </editor-fold>
