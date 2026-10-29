/// # Task · 7.4 the first station with enough energy
/// Lesson: 04-search.html

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
