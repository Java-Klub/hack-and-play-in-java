/// # Solved task · 8.2 unlock the radar only inside the map
/// Lesson: 02-bounds.html

void main() {
    int[][] map = {{0, 4, 0, 6}, {2, 0, 5, 9}};
    int x = Integer.parseInt(IO.readln("X: ").strip());
    int y = Integer.parseInt(IO.readln("Y: ").strip());
    if (inside(map, x, y)) {
        IO.println("Signal: " + map[y][x]);
    } else {
        IO.println("Outside");
    }
}

boolean inside(int[][] map, int x, int y) {
    return x >= 0 && x < map[0].length && y >= 0 && y < map.length;
}
