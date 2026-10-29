/// # Vyřešený úkol · ShutTheBox
/// Lekce: 5.3 Vrať výsledek volajícímu — 03-results.html
///
/// Původní zadání (tato verze je již vyřešená):
///
/// Hoď dvěma kostkami a zavři otevřená číslovaná políčka se součtem odpovídajícím hodu. Snaž se ponechat
/// užitečný výběr pro další hody a zavřít všechna políčka, než nezbude platná možnost.
///
/// **Tvoje mise · Napiš a zapoj pravidlo kostek:** Napiš diceTotal tak, aby vrátila součet obou kostek.
/// Potom oprav její zapojení v
/// roll: výsledek diceTotal(firstDie, secondDie) přiřaď do target. Musí fungovat samotný výpočet i
/// skutečný cíl hodu.
///
/// Spusť program, porovnej dvě vypsané hodnoty Dice s cílem rolled a potom napiš různá otevřená
/// čísla, která mají tento součet vytvořit.

int diceTotal(int first, int second) {
    return first + second;
}

void roll() {
    firstDie = random.nextInt(1, 7);
    secondDie = random.nextInt(1, 7);
    target = diceTotal(firstDie, secondDie);
    if (!possible()) {
        outcome = "No legal closure. Final open sum: " + sum();
    }
}

// <editor-fold defaultstate="collapsed" desc="Tvoje vlastní verze · dobrovolně">
/// Vytvoř domácí variantu s bonusem za dvě stejné kostky. Pravidlo dej do diceTotal a ponech jeho
/// volání v roll. Předpověz nejmenší a největší cíl varianty; ověř, zda lze každý cíl ještě splnit
/// zbývajícími políčky.
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Hotové části hry · prozkoumej podle zájmu">
boolean[] open;
int target;
String outcome;
Random random = new Random();

void reset() {
    open = new boolean[10];
    Arrays.fill(open, true);
    open[0] = false;
    outcome = "";
    roll();
}

int sum() {
    int n = 0;
    for (int i = 1; i <= 9; i++) {
        if (open[i]) {
            n += i;
        }
    }
    return n;
}

boolean possible() {
    for (int bits = 1; bits < 512; bits++) {
        int total = 0;
        boolean legal = true;
        for (int i = 1; i <= 9; i++) {
            if ((bits & (1 << (i - 1))) != 0) {
                total += i;
                legal &= open[i];
            }
        }
        if (legal && total == target) {
            return true;
        }
    }
    return false;
}

void command(String input) {
    require(outcome.isEmpty());
    var chosen = new HashSet<Integer>();
    int total = 0;
    for (String p : input.split("\\s+")) {
        int n = Integer.parseInt(p);
        require(n >= 1 && n <= 9 && open[n] && chosen.add(n));
        total += n;
    }
    require(total == target);
    for (int n : chosen) {
        open[n] = false;
    }
    if (sum() == 0) {
        outcome = "Box shut: you win!";
    } else {
        roll();
    }
}

void display() {
    String s = "Open: ";
    for (int i = 1; i <= 9; i++) {
        if (open[i]) {
            s += i + " ";
        }
    }
    IO.println("Dice: " + firstDie + " + " + secondDie);
    IO.println(s + "; rolled " + target);
}

void main() {
    reset();
    IO.println("Shut the Box");
    IO.println("Enter distinct open numbers whose sum matches two dice. No legal subset ends the round. /r restarts; /q quits.");
    boolean showState = true;
    while (true) {
        if (showState) {
            display();
            if (!outcome.isEmpty()) IO.println(outcome);
            showState = false;
        }
        String input = IO.readln(outcome.isEmpty() ? "> " : "Enter or /r: new round; /q: quit: ");
        if (input == null || input.strip().equalsIgnoreCase("/q")) {
            return;
        }
        if (input.strip().equalsIgnoreCase("/r") || (!outcome.isEmpty() && input.isBlank())) {
            reset();
            showState = true;
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
            IO.println("Invalid move or command. Enter distinct open numbers whose sum matches two dice. No legal subset ends the round. /r restarts; /q quits.");
        }
    }
}

void require(boolean valid) {
    if (!valid) {
        throw new IllegalArgumentException("Invalid move or command.");
    }
}

int firstDie;
int secondDie;

// </editor-fold>
