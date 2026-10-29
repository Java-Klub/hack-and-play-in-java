/// # L01.10 · ClubDoor
///
/// **Your mission · Find the hidden pass:** Open the workshop door. The working program checks a
/// badge, but your visitor badge is refused. Read the small source, find the accepted pass and put it
/// on your card. Keep the door’s ordinary check: it must still reject other badges. Run the file to
/// see Badge and Door opened/locked.

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

String cardCode = "ORBIT";

boolean mayEnter(String code) {
    return workshopPass.equals(code);
}

final String workshopPass = "ORBIT";
// </editor-fold>
