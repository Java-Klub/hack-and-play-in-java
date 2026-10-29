void main() {
    Combo combo = new Combo();
    combo.hit();
    combo.hit();
    combo.miss();
    combo.hit();
    IO.println("Current: " + combo.streak);
    IO.println("Best: " + combo.best);
}

class Combo {
    int streak = 0;
    int best = 0;

    void hit() {
        streak = streak + 1;
    }

    void miss() {
    }
}
