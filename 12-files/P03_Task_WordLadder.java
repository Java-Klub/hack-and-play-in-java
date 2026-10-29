/// # Task · WordLadder
/// Lesson: 12.1 Decide which values belong — 01-validation.html
///
/// The one-word ladder works. Become a route planner: send several carefully chosen stops at once, and
/// make the machine refuse a bad plan without stranding you halfway.
///
/// **Your mission:** Accept a space-separated route as well as one word. Every stop must be a listed
/// word one letter from the previous stop; strip outer whitespace and ignore letter case. Validate the
/// whole route before changing play. A bad later stop leaves the original word and history untouched.
/// Reaching WARM is allowed only as the final stop and counts individual moves, not submissions.
///
/// Use the printed dictionary to plan a route. The starter accepts CORD, then CARD separately. Your new
/// planner must also accept cord card in one submission. Try WARD COLD from CARD: the entire bad plan
/// must be refused. /r restarts; /q quits.

// <editor-fold defaultstate="expanded" desc="Program · explore the rules">
String[] words = {"COLD", "CORD", "CARD", "WARD", "WARM", "WORD", "WORM", "GOLD", "SOLD", "TOLD", "HOLD"};
String current;
String outcome;
List<String> history;

void reset() {
    current = "COLD";
    history = new ArrayList<>(List.of(current));
    outcome = "";
}

void command(String input) {
    require(outcome.isEmpty());
    String word = input.strip().toUpperCase(Locale.ROOT);
    require(List.of(words).contains(word) && adjacent(current, word));
    current = word;
    history.add(word);
    if (word.equals("WARM")) outcome = "Reached WARM in " + (history.size() - 1) + " moves!";
}

boolean adjacent(String a, String b) {
    if (a.length() != b.length()) {
        return false;
    }
    int n = 0;
    for (int i = 0; i < a.length(); i++) {
        if (a.charAt(i) != b.charAt(i)) {
            n++;
        }
    }
    return n == 1;
}

void display() {
    IO.println("Dictionary: " + String.join(" ", words));
    IO.println(String.join(" -> ", history) + "; target WARM");
}

void main() {
    reset();
    IO.println("Word Ladder: transform COLD to WARM using the listed words. /r restarts; /q quits.");
    display();
    while (true) {
        boolean finished = !outcome.isEmpty();
        String input = IO.readln(finished ? "Enter or /r: new round; /q: quit > " : "> ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) return;
        if (input.strip().equalsIgnoreCase("/r") || finished && input.isBlank()) {
            reset();
            display();
            continue;
        }
        if (finished) {
            IO.println("This round is finished. Enter or /r starts a fresh trail; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid route. Use listed words one letter apart; the trail has not changed.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Design a new small dictionary and write two plans: a shortest known route and a longer scenic route.
/// Let a partner predict which submitted plan is rejected.
// </editor-fold>
