/// # Úkol · 8.1 cedulka pro vybrané místo
/// Lekce: 01-grids.html

void main() {
    String[][] map = {
        {"cave", "pond", "gate"},
        {"tree", "tower", "camp"}
    };
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    IO.println("Tile: " + map[0][0]);
    IO.println("Rows: " + map.length);
    IO.println("Columns: " + map.length);
}
