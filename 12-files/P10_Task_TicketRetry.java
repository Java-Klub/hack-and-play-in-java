/// # Úkol · 12.3 dokonči jednu rezervaci, nikoli několik
/// Lekce: 03-retry.html

int readTickets(String text) {
    int tickets = Integer.parseInt(text.strip());
    if (tickets < 1 || tickets > 6) {
        throw new IllegalArgumentException("Use 1..6 tickets.");
    }
    return tickets;
}

void main() {
    String reply = IO.readln("Tickets: ");
    if (reply == null || reply.strip().equalsIgnoreCase("q")) {
        IO.println("Stopped.");
        return;
    }
    try {
        int tickets = readTickets(reply);
        IO.println("Booked " + tickets + " tickets.");
    } catch (NumberFormatException failure) {
        IO.println("Use a whole number.");
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    }
}
