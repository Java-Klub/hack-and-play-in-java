/// # Hotový příklad · 8.1 Z řad vznikne mapa
/// Lekce: 01-grids.html

void main() {
    int[][] arena = {
        {1, 0, 2},
        {0, 3, 5}
    };
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    IO.println("Value: " + arena[y][x]);
    IO.println("Rows: " + arena.length);
    IO.println("Columns: " + arena[0].length);
}
