/// # Task · 8.5 light spreads one step at a time
/// Lesson: 05-generations.html

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
    return now;
}

boolean isOn(boolean[][] board, int x, int y) {
    return x >= 0 && x < board[0].length && y >= 0 && y < board.length && board[y][x];
}

void show(boolean[][] board) {
    for (boolean[] row : board) {
        IO.println(Arrays.toString(row));
    }
}
