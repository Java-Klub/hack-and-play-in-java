/// # Task · YatzyScorecard
/// Lesson: 7.2 Visit values without picking each one — 02-traversal.html
///
/// **Restore the dice total.** Save the first roll with `bank 13`. Chance receives points, but
/// the starter’s `total` stays zero.
///
/// **Your task:** Fill the loop body in `total` below. Sum all filled categories; unfilled ones
/// contain `-1` and do not count. Alongside the overall sum, total the first six categories
/// (positions 0–5). The supplied end of the method adds a 35-point bonus when those six total at
/// least 63.
///
/// **Result:** After `bank 13`, its points also appear in `total`. Other filled categories
/// increase the total; asking for it again must not award points again.

int total() {
    int sum = 0;
    int upper = 0;
    for (int i = 0; i < 13; i++) {
    }
    return sum + (upper >= 63 ? 35 : 0);
}

// <editor-fold defaultstate="collapsed" desc="Finished game parts · explore when curious">
String[] names = {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Three-kind", "Four-kind", "Full-house", "Small-straight", "Large-straight", "Yatzy", "Chance"};
int[] dice;
int[] scores;
int rolls;
int turn;
String outcome;
Random random = new Random();

void deal() {
    dice = new int[5];
    for (int i = 0; i < 5; i++) {
        dice[i] = random.nextInt(1, 7);
    }
    rolls = 1;
}

void reset() {
    scores = new int[13];
    Arrays.fill(scores, -1);
    turn = 0;
    outcome = "";
    deal();
}

int value(int category) {
    int[] n = new int[7];
    int sum = 0;
    for (int d : dice) {
        n[d]++;
        sum += d;
    }
    if (category < 6) {
        return n[category + 1] * (category + 1);
    }
    int max = Arrays.stream(n).max().orElse(0);
    boolean two = false;
    boolean three = false;
    for (int v : n) {
        two |= v == 2;
        three |= v == 3;
    }
    int run = 0;
    int longest = 0;
    for (int i = 1; i <= 6; i++) {
        run = n[i] > 0 ? run + 1 : 0;
        longest = Math.max(longest, run);
    }
    return switch (category) {
    case 6 -> max >= 3 ? sum : 0;
    case 7 -> max >= 4 ? sum : 0;
    case 8 -> two && three ? 25 : 0;
    case 9 -> longest >= 4 ? 30 : 0;
    case 10 -> longest == 5 ? 40 : 0;
    case 11 -> max == 5 ? 50 : 0;
    default -> sum;
    };
}

void command(String input) {
    require(outcome.isEmpty());
    String[] p = input.split("\\s+");
    require(p.length >= 2);
    if (p[0].equalsIgnoreCase("bank")) {
        require(p.length == 2);
        int c = Integer.parseInt(p[1]) - 1;
        require(c >= 0 && c < 13 && scores[c] < 0);
        scores[c] = value(c);
        turn++;
        if (turn == 13) {
            outcome = "Scorecard complete. Total including upper bonus: " + total();
        } else {
            deal();
        }
    } else {
        require(p[0].equalsIgnoreCase("roll") && rolls < 3);
        var ids = new HashSet<Integer>();
        for (int i = 1; i < p.length; i++) {
            int n = Integer.parseInt(p[i]) - 1;
            require(n >= 0 && n < 5 && ids.add(n));
        }
        for (int n : ids) {
            dice[n] = random.nextInt(1, 7);
        }
        rolls++;
    }
}

void display() {
    IO.println("Dice positions:  1  2  3  4  5");
    IO.println("Dice values:    " + Arrays.toString(dice));
    IO.println("Hand " + Math.min(13, turn + 1) + "/13; rerolls left " + (3 - rolls) + "; total " + total());
    for (int i = 0; i < 13; i++) {
        IO.println((i + 1) + " " + names[i] + ": " + (scores[i] >= 0 ? scores[i] + " points saved" : "open; this hand would score " + value(i)));
    }
    IO.println(rolls < 3 ? "Example: roll 4 5 changes only dice 4 and 5; the others stay. Or bank an open category." : "No rerolls left. Bank an open category to continue.");
    IO.println("Example: bank 2 saves the Twos score in category 2. Each category can be filled once. /help explains scoring.");
}

void main() {
    reset();
    IO.println("Yatzy Scorecard");
    IO.println("Each hand starts with five dice. Reroll selected positions up to twice, then save one open scoring category. /help explains scores; /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? (rolls < 3 ? "roll 4 5, bank category, /help, /r, /q: " : "bank category, /help, /r, /q: ") : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
            continue;
        }
        if (input.strip().equalsIgnoreCase("/help")) {
            IO.println("Categories 1-6: add only dice showing that number. Example: Twos adds all the 2s.");
            IO.println("Three-kind or Four-kind: at least three or four equal dice; score the sum of all five dice.");
            IO.println("Full-house: a triple and a pair, 25 points. Small-straight: four consecutive values, 30 points.");
            IO.println("Large-straight: five consecutive values, 40 points. Yatzy: five equal dice, 50 points.");
            IO.println("Chance: sum all five dice. A category whose condition is not met scores zero.");
            IO.println("If categories 1-6 total at least 63, add a 35-point bonus. Bank fills a category even when it scores zero.");
            continue;
        }
        if (!outcome.isEmpty()) {
            IO.println("Round finished. Press Enter or /r for a new round; /q quits.");
            continue;
        }
        try {
            command(input.strip());
            showState = true;
        } catch (IllegalArgumentException invalid) {
            IO.println("Move rejected. Try roll 4 5 while rerolls remain, or bank 2 if category 2 is open. /help explains scoring.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

// </editor-fold>
