/// # 04.04 · ClubDoor
///
/// **Find the workshop pass.** Running the program prints Badge: VISITOR and Door locked. The
/// door refuses the visitor badge.
///
/// **Your task:** Read the small program to find the text the door accepts. Replace VISITOR on
/// your badge with that text. Keep the acceptance rule itself: other badges must still be
/// refused.
///
/// **Result:** Running the program will print your changed badge and Door opened. The pass is in
/// the source; you do not need to guess it or type it while the program runs.

// <editor-fold defaultstate="expanded" desc="Learn · F03">
/// Read a name backwards to its stored value, then forwards to its uses. main starts the story; a
/// method call asks a named question. The supplied equals compares text contents and ? : chooses one
/// of two messages; you will study those decisions in the next level. Today your work is choosing
/// data, not rewriting the question. A String holds quoted text. For example `String colour =
/// "blue";` stores blue under colour; the name itself is not the text. Predict what the badge line
/// will show before changing anything.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 1 · search area">
/// The displayed badge and the accepted pass are different pieces of data.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hint 2 · a concrete route">
/// Follow the argument of mayEnter, then read the text it compares against.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Possible change · reveals the direction">
/// Keep workshopPass as ORBIT and set cardCode to the same text.
// </editor-fold>

// <editor-fold defaultstate="expanded" desc="Small program · explore">
void main() {
    IO.println("CLUB DOOR");
    IO.println("Badge: " + cardCode);
    IO.println(mayEnter(cardCode) ? "Door opened. Welcome to the workshop!" : "Door locked. Find the workshop pass in this source.");
}

String cardCode = "VISITOR";

boolean mayEnter(String code) {
    return workshopPass.equals(code);
}

final String workshopPass = "ORBIT";
// </editor-fold>
