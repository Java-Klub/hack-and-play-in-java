/// # Hotový příklad · 8.5 Minulost čti, budoucnost zapisuj
/// Lekce: 05-generations.html

void main() {
    boolean[][] old = {{true, false, false, false}, {false, true, false, false}};
    boolean[][] next = shiftRight(old);
    IO.println("Old");
    show(old);
    IO.println("Next");
    show(next);
}

boolean[][] shiftRight(boolean[][] old) {
    boolean[][] next = new boolean[old.length][old[0].length];
    for (int y = 0; y < old.length; y++) {
        for (int x = 1; x < old[y].length; x++) {
            next[y][x] = old[y][x - 1];
        }
    }
    return next;
}

void show(boolean[][] board) {
    for (boolean[] row : board) {
        IO.println(Arrays.toString(row));
    }
}
