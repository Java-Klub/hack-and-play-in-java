/// # Úkol · 4.1 oprav nespravedlivou bránu
/// Lekce: 01-questions.html

void main() {
    int energy = Integer.parseInt(IO.readln("Energy: ").strip());
    String pass = IO.readln("Pass: ");
    boolean enough = energy > 3;
    boolean recognised = pass == "MOON";
    IO.println("Enough energy: " + enough);
    IO.println("Recognised pass: " + recognised);
}
