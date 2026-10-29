/// # Hotový příklad · 14.2 Skládej objekty podle jejich chování
/// Lekce: 02-interfaces.html

interface Greeting {
    String message(String name);
}

record Welcome(String prefix) implements Greeting {
    public String message(String name) {
        return prefix + ", " + name + "!";
    }
}

String greet(Greeting greeting, String name) {
    return greeting.message(name);
}

void main() {
    Greeting greeting = new Welcome("Hello");
    IO.println(greet(greeting, "Nova"));
    IO.println(greet(new Welcome("Welcome"), "Mika"));
}
