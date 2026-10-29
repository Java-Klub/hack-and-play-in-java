/// # Solved task · Merge2048
/// Lesson: 14.1 Choose examples that catch mistakes — 01-checks.html
///
/// Original assignment (this version is complete):
///
/// **Catch a fake merging machine.** The game already merges tiles correctly. Your result will be
/// a set of examples, not a changed game rule.
///
/// **Your task:** Extend `learnerChecks` below. Each example contains a four-number input row,
/// its expected row after moving left, and the earned points. Zero means a gap. A newly merged
/// tile does not merge again in the same move; points are the sum of the newly created tiles.
///
/// **Result:** The examples must expose ignored merging, gaps preventing a merge, merging a new
/// tile again, lost points, and merging only the first of two independent pairs. They must still
/// agree with the real game.

record MergeExample(int[] input, int[] expected, int score) {
}

MergeExample[] learnerChecks() {
    return new MergeExample[]{
    new MergeExample(new int[]{0, 0, 0, 0}, new int[]{0, 0, 0, 0}, 0),
    new MergeExample(new int[]{2, 0, 2, 0}, new int[]{4, 0, 0, 0}, 4),
    new MergeExample(new int[]{2, 2, 4, 0}, new int[]{4, 4, 0, 0}, 4),
    new MergeExample(new int[]{4, 4, 4, 4}, new int[]{8, 8, 0, 0}, 16)};
}

// <editor-fold defaultstate="collapsed" desc="For this game">
/// ### Check the whole observable result
///
/// A MergeExample contains an input row, an expected row and awarded points. The supplied checker
/// compares array contents. For orientation, sliding `[0,8,0,2]` left compresses it to `[8,2,0,0]`
/// without a merge or points. A newly merged tile cannot merge again in the same move. Draw a
/// different row to test that rule, then choose another that separates moving tiles from awarding
/// points. Predict both parts before constructing the record.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
int[][] board;
int score;
String outcome;
Random random = new Random();

void reset() {
    board = new int[4][4];
    score = 0;
    outcome = "";
    spawn();
    spawn();
}

void spawn() {
    var free = new ArrayList<int[]>();
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (board[r][c] == 0) {
                free.add(new int[]{r, c});
            }
        }
    }
    if (free.isEmpty()) {
        return;
    }
    int[] p = free.get(random.nextInt(free.size()));
    board[p[0]][p[1]] = random.nextInt(10) == 0 ? 4 : 2;
}

int[] merge(int[] row) {
    int[] values = Arrays.stream(row).filter((n) -> n != 0).toArray();
    int[] result = new int[4];
    int out = 0;
    for (int i = 0; i < values.length; i++) {
        int n = values[i];
        if (i + 1 < values.length && n == values[i + 1]) {
            n *= 2;
            score += n;
            i++;
        }
        result[out++] = n;
    }
    return result;
}

boolean canMove() {
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (board[r][c] == 0 || r < 3 && board[r][c] == board[r + 1][c] || c < 3 && board[r][c] == board[r][c + 1]) {
                return true;
            }
        }
    }
    return false;
}

void command(String input) {
    require(outcome.isEmpty());
    require(input.matches("(?i)[wasd]"));
    char d = input.toLowerCase(Locale.ROOT).charAt(0);
    boolean changed = false;
    for (int line = 0; line < 4; line++) {
        int[] values = new int[4];
        for (int i = 0; i < 4; i++) {
            int k = d == 'd' || d == 's' ? 3 - i : i;
            values[i] = d == 'a' || d == 'd' ? board[line][k] : board[k][line];
        }
        int[] result = merge(values);
        changed |= !Arrays.equals(values, result);
        for (int i = 0; i < 4; i++) {
            int k = d == 'd' || d == 's' ? 3 - i : i;
            if (d == 'a' || d == 'd') {
                board[line][k] = result[i];
            } else {
                board[k][line] = result[i];
            }
        }
    }
    if (changed) {
        spawn();
    }
    for (int[] row : board) {
        for (int n : row) {
            if (n >= 2048) {
                outcome = "2048: you win!";
            }
        }
    }
    if (outcome.isEmpty() && !canMove()) {
        outcome = "No legal move: game over.";
    }
}

void display() {
    for (int[] row : board) {
        IO.println(Arrays.toString(row));
    }
    IO.println("Score: " + score);
}

void main() {
    reset();
    verifyExamples();
    IO.println("2048");
    IO.println("Slide all tiles with W/A/S/D. Equal neighbours merge once per move; aim for 2048 before the board locks.");
    IO.println("W/A/S/D slides up/left/down/right. Equal values merge once. /r restarts; /q quits.");
    IO.println("Example: a slides left. A slide that changes nothing does not add another tile.");
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
            IO.println("Invalid move. " + "W/A/S/D slides up/left/down/right. Equal values merge once. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

void verifyExamples() {
    int saved = score;
    try {
        for (var e : learnerChecks()) {
            score = 0;
            int[] actual = merge(e.input().clone());
            if (!Arrays.equals(actual, e.expected()) || score != e.score()) {
                throw new IllegalStateException("Row " + Arrays.toString(e.input())
                    + ": expected " + Arrays.toString(e.expected()) + " with score " + e.score()
                    + ", actual " + Arrays.toString(actual) + " with score " + score);
            }
        }
    } finally {
        score = saved;
    }
}
// </editor-fold>
