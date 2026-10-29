/// # Task · 12.1 stop the free-energy loophole
/// Lesson: 01-validation.html

boolean allowed(int energy, int cost) {
    return cost <= energy;
}

void main() {
    String reply = IO.readln("Energy and cost: ");
    if (reply == null) return;
    String[] parts = reply.strip().split("\\s+");
    int energy = Integer.parseInt(parts[0]);
    int cost = Integer.parseInt(parts[1]);
    IO.println("Allowed: " + allowed(energy, cost));
}
