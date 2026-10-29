/// # Úkol · 7.4 první dostačující stanice
/// Lekce: 04-search.html

void main() {
    int[] energy = {0, 4, 4, 9};
    int need = Integer.parseInt(IO.readln("Need: ").strip());
    int station = firstFit(energy, need);
    if (station == -1) {
        IO.println("Unavailable");
    } else {
        IO.println("Station: " + station);
    }
}

int firstFit(int[] energy, int need) {
    return -1;
}
