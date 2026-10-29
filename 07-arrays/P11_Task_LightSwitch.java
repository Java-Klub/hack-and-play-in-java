/// # Task · 7.3 a light switch
/// Lesson: 03-updates.html

void main() {
    boolean[] lights = {false, false, false, false};
    int slot = Integer.parseInt(IO.readln("Slot: ").strip());
    if (slot >= 0 && slot < lights.length) {
        boolean selected = lights[slot];
        selected = !selected;
    } else {
        IO.println("Invalid slot");
    }
    IO.println(Arrays.toString(lights));
}
