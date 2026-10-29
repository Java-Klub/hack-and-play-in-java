/// # Worked example · 9.4 Data can know their operations
/// Lesson: 04-data-methods.html

void main() {
    Battery original = new Battery(8);
    IO.println("Ready: " + original.canSpend(3));
    Battery after = original.spend(3);
    IO.println("Original: " + original.charge());
    IO.println("After: " + after.charge());
}

record Battery(int charge) {
    boolean canSpend(int cost) {
        return cost >= 0 && charge >= cost;
    }

    Battery spend(int cost) {
        return new Battery(charge - cost);
    }
}
