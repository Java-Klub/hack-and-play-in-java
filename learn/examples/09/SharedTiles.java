void main() {
    Tile[] tiles = {new Tile("Blue"), new Tile("White")};
    Tile[] view = tiles;
    Tile saved = tiles[0];
    view[0] = new Tile("Gold");
    IO.println("Board: " + tiles[0].color());
    IO.println("Saved: " + saved.color());
    IO.println("Same array: " + (tiles == view));
    IO.println("Equal tile: " + saved.equals(new Tile("Blue")));
}

record Tile(String color) {
}
