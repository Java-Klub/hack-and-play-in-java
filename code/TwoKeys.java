void main() {
    String badge = IO.readln("Badge: ").strip();
    int energy = Integer.parseInt(IO.readln("Energy: ").strip());
    boolean trusted = badge.equals("SUN") || badge.equals("MOON");
    boolean enough = energy >= 3;
    boolean locked = false;
    boolean enter = trusted && enough && !locked;
    IO.println("Access: " + enter);
}
