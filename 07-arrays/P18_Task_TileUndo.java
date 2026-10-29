/// # Solved task · 7.5 fix the memory and undo
/// Lesson: 05-copies.html

void main() {
    int[] board = {2, 0, 1};
    int[] saved = remember(board);
    int tile = Integer.parseInt(IO.readln("New tile: ").strip());
    board[0] = tile;
    IO.println("Saved: " + Arrays.toString(saved));
    undo(board, saved);
    IO.println("Restored: " + Arrays.toString(board));
}

int[] remember(int[] board) {
    return board.clone();
}

void undo(int[] board, int[] saved) {
    for (int index = 0; index < board.length; index++) {
        board[index] = saved[index];
    }
}
