/// # Vyřešený úkol · 8.5 světlo se šíří jedním krokem
/// Lekce: 05-generations.html

void main() {
    boolean[][] start = {{false, false, false}, {false, true, false}, {false, false, false}};
    int steps = Integer.parseInt(IO.readln("Steps: ").strip());
    boolean[][] board = start;
    for (int step = 0; step < steps; step++) {
        board = spread(board);
    }
    IO.println("Start");
    show(start);
    IO.println("After");
    show(board);
}

boolean[][] spread(boolean[][] now) {
    boolean[][] next = new boolean[now.length][now[0].length];
    for (int y = 0; y < now.length; y++) {
        for (int x = 0; x < now[y].length; x++) {
            next[y][x] = isOn(now, x, y)
                || isOn(now, x - 1, y) || isOn(now, x + 1, y)
                || isOn(now, x, y - 1) || isOn(now, x, y + 1);
        }
    }
    return next;
}

boolean isOn(boolean[][] board, int x, int y) {
    return x >= 0 && x < board[0].length && y >= 0 && y < board.length && board[y][x];
}

void show(boolean[][] board) {
    for (boolean[] row : board) {
        IO.println(Arrays.toString(row));
    }
}
