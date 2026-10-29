/// # Solved task · ClubDoor
/// Lesson: 4.1 Ask a question in code — 01-questions.html
///
/// Original assignment (this version is complete):
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
