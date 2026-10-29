/// # Úkol · 8.3 senzor vidí pouze čtyři směry
/// Lekce: 03-neighbours.html

void main() {
    int[][] board = {{1, 1, 0}, {0, 1, 1}, {1, 0, 0}};
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    IO.println("Neighbours: " + cardinal(board, x, y));
}

int cardinal(int[][] board, int x, int y) {
    return 0;
}

boolean isOn(int[][] board, int x, int y) {
    if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
        return false;
    }
    return board[y][x] == 1;
}
