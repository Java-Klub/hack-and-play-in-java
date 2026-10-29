/// # Hotový příklad · 8.2 Nejdřív zkontroluj okraj
/// Lekce: 02-bounds.html

void main() {
    int[][] map = {{2, 0, 1}, {0, 3, 7}};
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    if (inside(map, x, y)) {
        IO.println("Value: " + map[y][x]);
    } else {
        IO.println("Outside");
    }
}

boolean inside(int[][] map, int x, int y) {
    return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
}
