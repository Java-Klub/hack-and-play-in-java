/// # Solved task · 8.3 the sensor sees only four directions
/// Lesson: 03-neighbours.html

void main() {
    int[][] board = {{1, 1, 0}, {0, 1, 1}, {1, 0, 0}};
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    IO.println("Neighbours: " + cardinal(board, x, y));
}

int cardinal(int[][] board, int x, int y) {
    int count = 0;
    if (isOn(board, x - 1, y)) {
        count++;
    }
    if (isOn(board, x + 1, y)) {
        count++;
    }
    if (isOn(board, x, y - 1)) {
        count++;
    }
    if (isOn(board, x, y + 1)) {
        count++;
    }
    return count;
}

boolean isOn(int[][] board, int x, int y) {
    if (x < 0 || x >= board[0].length || y < 0 || y >= board.length) {
        return false;
    }
    return board[y][x] == 1;
}
