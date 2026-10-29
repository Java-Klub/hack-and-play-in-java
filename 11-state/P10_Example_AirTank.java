/// # Worked example · 11.3 Rejection must preserve state
/// Lesson: 03-invariants.html

void main() {
    Tank tank = new Tank();
    IO.println("Use 3: " + tank.use(3));
    IO.println("Air: " + tank.air());
    IO.println("Use 4: " + tank.use(4));
    IO.println("Air: " + tank.air());
}

class Tank {
    private int air = 5;

    int air() {
        return air;
    }

    boolean use(int amount) {
        if (amount <= 0 || amount > air) {
            return false;
        }
        air = air - amount;
        return true;
    }
}
