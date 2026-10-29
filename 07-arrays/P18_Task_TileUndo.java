/// # Úkol · 7.5 oprav paměť a návrat
/// Lekce: 05-copies.html

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
    return board;
}

void undo(int[] board, int[] saved) {
}
