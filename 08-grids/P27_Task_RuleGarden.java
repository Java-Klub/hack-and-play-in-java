/// # Task · RuleGarden
/// Lesson: 8.5 Read the past; write the future — 05-generations.html
///
/// Grow a whole page of patterns from one lit cell and a tiny numbered rule. Change the rule to discover
/// triangles, stripes, or apparent chaos.
///
/// **Your mission:** New cells immediately influence later cells in the same row, distorting the pattern.
/// Repair nextRow() so an entire generation reads the same old row.
///
/// The program immediately shows rule 30 in terminal output. Enter 90 at the next prompt to
/// compare, press Enter to repeat the current rule, or q to quit.

boolean[] nextRow(boolean[] current, int rule) {
    boolean[] next = current;
    for (int column = 0; column < current.length; column++) {
        int pattern = current[column] ? 2 : 0;
        if (column > 0 && current[column - 1]) {
            pattern += 4;
        }
        if (column + 1 < current.length && current[column + 1]) {
            pattern += 1;
        }
        next[column] = ((rule>>pattern)&1) != 0;
    }
    return next;
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// The prepared code encodes left/centre/right as weights 4/2/1: lit-left and lit-right with a dark
/// centre gives pattern 5. `(rule >> pattern) & 1` reads that pattern’s rule bit. Those rule
/// calculations stay supplied; your task is to keep old and next separate. Trace three adjacent columns
/// using the same unchanged current row.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Your own variant · optional">
/// Start with two lit cells instead of one and compare rules 30 and 90. Predict where the two growing
/// patterns first meet before printing them.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
List<String> grow(int rule, int width, int rows) {
    if (rule < 0 || rule > 255 || width < 3 || width > 121 || rows < 1 || rows > 60) {
        throw new IllegalArgumentException("Use rule 0..255, width 3..121 and rows 1..60.");
    }
    boolean[] cells = new boolean[width];
    cells[width / 2] = true;
    var result = new ArrayList<String>();
    for (int row = 0; row < rows; row++) {
        var line = new StringBuilder();
        for (boolean cell : cells) {
            line.append(cell ? '#' : '.');
        }
        result.add(line.toString());
        cells = nextRow(cells, rule);
    }
    return result;
}

void displayRule(int rule, int width, int rows) {
    IO.println("Rule " + rule + "; # is on, . is off; cells outside the width stay off.");
    for (String line : grow(rule, width, rows)) {
        IO.println(line);
    }
}

void main() {
    int rule = 30;
    IO.println("RULE GARDEN: watch one starting cell grow into a pattern.");
    IO.println("Each printed line is a new generation. Try 90 or 110 to compare with rule 30.");
    displayRule(rule, 61, 31);
    while (true) {
        String input = IO.readln("New rule 0..255; Enter or r: repeat; q: quit: ");
        if (input == null) {
            return;
        }
        input = input.strip();
        if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("/q")) {
            return;
        }
        try {
            int requested = input.isEmpty() || input.equalsIgnoreCase("r") || input.equalsIgnoreCase("/r")
                    ? rule : Integer.parseInt(input);
            if (requested < 0 || requested > 255) {
                throw new IllegalArgumentException("Rule: 0..255.");
            }
            rule = requested;
            displayRule(rule, 61, 31);
        } catch (IllegalArgumentException invalidInput) {
            IO.println("Enter a rule number from 0 to 255, or q to quit.");
        }
    }
}
// </editor-fold>
