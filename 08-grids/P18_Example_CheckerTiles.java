/// # Hotový příklad · 8.4 Vyrob vzor podle souřadnic
/// Lekce: 04-patterns.html

void main() {
    int size = Integer.parseInt(IO.readln("Size: ").strip());
    String[][] tiles = new String[size][size];
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            if ((x + y) % 2 == 0) {
                tiles[y][x] = "#";
            } else {
                tiles[y][x] = ".";
            }
        }
    }
    show(tiles);
}

void show(String[][] tiles) {
    for (String[] row : tiles) {
        String line = "";
        for (String tile : row) {
            line += tile;
        }
        IO.println(line);
    }
}
