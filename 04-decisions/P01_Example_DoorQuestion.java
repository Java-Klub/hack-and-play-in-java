/// # Hotový příklad · 4.1 Polož otázku kódu
/// Lekce: 01-questions.html

void main() {
    int energy = Integer.parseInt(IO.readln("Energy: ").strip());
    String pass = IO.readln("Pass: ");
    boolean enough = energy >= 3;
    boolean recognised = pass.equals("MOON");
    IO.println("Enough energy: " + enough);
    IO.println("Recognised pass: " + recognised);
}
