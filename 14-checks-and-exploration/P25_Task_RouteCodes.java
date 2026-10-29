/// # Task · 14.5 explore every permitted route code
/// Lesson: 05-recursion.html

void build(String prefix, int remaining, List<String> result) {
    if (remaining == 0) {
        result.add(prefix);
        return;
    }
    build(prefix + "L", remaining - 1, result);
}

List<String> codes(int length) {
    if (length < 0 || length > 8) {
        throw new IllegalArgumentException("Use 0..8.");
    }
    var result = new ArrayList<String>();
    build("", length, result);
    return result;
}

void main() {
    String reply = IO.readln("Length: ");
    if (reply == null) return;
    try {
        int length = Integer.parseInt(reply.strip());
        var result = codes(length);
        for (String code : result) IO.println("[" + code + "]");
        IO.println("Count: " + result.size());
    } catch (NumberFormatException failure) {
        IO.println("Use a whole number.");
    } catch (IllegalArgumentException failure) {
        IO.println(failure.getMessage());
    }
}
