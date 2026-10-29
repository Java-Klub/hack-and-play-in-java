void main() {
    int size = Integer.parseInt(IO.readln("Size: ").strip());
    show(frame(size));
}

String[][] frame(int size) {
    String[][] tiles = new String[size][size];
    for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
            tiles[y][x] = ".";
        }
    }
    return tiles;
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
