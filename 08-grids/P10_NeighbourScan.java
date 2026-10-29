void main() {
    int[][] board = {{0, 1, 0}, {1, 1, 1}, {0, 1, 0}};
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    IO.println("Neighbours: " + neighbours(board, x, y));
}

int neighbours(int[][] board, int x, int y) {
    int count = 0;
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx != 0 || dy != 0) {
                int nx = x + dx;
                int ny = y + dy;
                if (inside(board, nx, ny) && board[ny][nx] == 1) {
                    count++;
                }
            }
        }
    }
    return count;
}

boolean inside(int[][] board, int x, int y) {
    return x >= 0 && x < board[0].length && y >= 0 && y < board.length;
}
