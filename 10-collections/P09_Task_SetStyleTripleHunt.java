/// # Task · SetStyleTripleHunt
/// Lesson: 10.1 A bag need not know its size ahead of time — 01-lists.html
///
/// **Find the overlooked triple.** Submitting a correct triple already works. A different
/// decision is faulty: whether any valid triple remains on the table. The `more` command and the
/// end-of-game decision use that answer.
///
/// **Your task:** Fix the search in `hasSet` below. Examine every combination of three distinct
/// cards, including combinations using the last card. The supplied `triple` already checks a
/// chosen triple.
///
/// **Result:** `more` adds cards only when no valid triple remains. Check also tries a table
/// whose only valid triple needs its last card; submitting an ordinary triple alone does not
/// demonstrate this repair.

boolean hasSet() {
    for (int a = 0; a < table.size(); a++) {
        for (int b = a + 1; b < table.size(); b++) {
            for (int c = b + 1; c < table.size() - 1; c++) {
                if (triple(table.get(a), table.get(b), table.get(c))) {
                    return true;
                }
            }
        }
    }
    return false;
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### The table's reading API
///
/// `table` is a supplied List: `size()` gives its current number of cards, and `get(index)` reads one
/// card without removing it. Its positions start at zero like an array. The three loop indices
/// deliberately increase: a precedes b and b precedes c, so one card is never selected twice. Predict
/// how the last table position could enter a triple; keep the supplied `triple` query responsible for
/// judging properties.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
ArrayDeque<Integer> deck;
List<Integer> table;
int score;
String outcome;

void reset() {
    var cards = new ArrayList<Integer>();
    for (int i = 0; i < 81; i++) {
        cards.add(i);
    }
    Collections.shuffle(cards);
    deck = new ArrayDeque<>(cards);
    table = new ArrayList<>();
    score = 0;
    outcome = "";
    deal(12);
}

void deal(int count) {
    while (count-- > 0 && !deck.isEmpty()) {
        table.add(deck.remove());
    }
}

boolean triple(int a, int b, int c) {
    for (int i = 0; i < 4; i++) {
        if ((a % 3 + b % 3 + c % 3) % 3 != 0) {
            return false;
        }
        a /= 3;
        b /= 3;
        c /= 3;
    }
    return true;
}

void command(String input) {
    require(outcome.isEmpty());
    if (input.equalsIgnoreCase("more")) {
        require(!hasSet() && !deck.isEmpty());
        deal(3);
    } else {
        String[] p = input.split("\\s+");
        require(p.length == 3);
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            ids[i] = Integer.parseInt(p[i]) - 1;
            require(ids[i] >= 0 && ids[i] < table.size());
        }
        Arrays.sort(ids);
        require(ids[0] != ids[1] && ids[1] != ids[2] && triple(table.get(ids[0]), table.get(ids[1]), table.get(ids[2])));
        for (int i = 2; i >= 0; i--) {
            table.remove(ids[i]);
        }
        score++;
        deal(Math.max(0, 12 - table.size()));
    }
    if (deck.isEmpty() && !hasSet()) {
        outcome = "Finished: " + score + " triples found.";
    }
}

String code(int card) {
    String s = "";
    for (int i = 0; i < 4; i++) {
        s = (card % 3) + s;
        card /= 3;
    }
    return s;
}

void display() {
    IO.println("Four attributes, values 0/1/2: each attribute must be all equal or all different.");
    for (int i = 0; i < table.size(); i++) {
        IO.println((i + 1) + ": " + code(table.get(i)));
    }
    IO.println("Sets " + score + "; deck " + deck.size() + "; " + (hasSet() ? "A set exists" : "No set: use more if deck remains"));
}

void main() {
    reset();
    IO.println("Set-style Triple Hunt");
    IO.println("Find three cards whose four attributes are each all equal or all different.");
    IO.println("Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
    IO.println("Example format: 1 2 3 selects three displayed card numbers. Use more only when no triple exists.");
    display();
    while (true) {
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("/q") || input.equalsIgnoreCase("q")) {
            return;
        }
        if (input.equalsIgnoreCase("/r") || input.equalsIgnoreCase("r")
                || (!outcome.isEmpty() && input.isEmpty())) {
            reset();
            display();
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("That round is over. Press Enter to play again, or /q to quit.");
            continue;
        }
        try {
            command(input);
            display();
            if (!outcome.isEmpty()) {
                IO.println(outcome);
            }
        } catch (IllegalArgumentException invalid) {
            IO.println("Invalid move. " + "Enter three card numbers, or more when no set exists. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}
// </editor-fold>
